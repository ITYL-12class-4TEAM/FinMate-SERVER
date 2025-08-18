package org.scoula.product.scheduler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.scoula.product.EtcNoteParsedResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SavingProductScheduler {

    private static final String[] GROUP_CODES = {"020000", "030300"}; // 은행, 저축은행

    @Value("${finlife.api.url.savingProduct}")
    private String API_URL;

    @Value("${finlife.api.key}")
    private String AUTH_KEY;

    @Value("${spring.datasource.url:${jdbc.url}}")
    private String DB_URL;

    @Value("${spring.datasource.username:${jdbc.username}}")
    private String DB_USER;

    @Value("${spring.datasource.password:${jdbc.password}}")
    private String DB_PASS;

    @Scheduled(cron = "0 0 4 * * 1")
    public void fetchSavingProductProductsScheduled() {
        System.out.println("⏰ [스케줄러] 적금상품 데이터 수집 시작 - " + java.time.LocalDateTime.now());
        executeDataFetch();
    }

    public void fetchSavingProducttProductsManually() {
        System.out.println("🔧 [수동실행] 적금상품 데이터 수집 시작 - " + java.time.LocalDateTime.now());
        executeDataFetch();
    }

    public void executeDataFetch() {
        System.out.println("🚀 SavingProductScheduler 데이터 수집 시작");
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            System.out.println("✅ 데이터베이스 연결 성공");

            // 외래키 사용하기 위해 auto commit false
            conn.setAutoCommit(false);

            for (String code : GROUP_CODES) {
                System.out.println("\n🔍 그룹코드 " + code + " 처리 시작...");

                // 첫 번째 페이지로 전체 페이지 수 확인
                int totalPages = getTotalPages(client, mapper, code);
                System.out.println("📄 총 페이지 수: " + totalPages);

                // 모든 페이지 처리
                for (int pageNo = 1; pageNo <= totalPages; pageNo++) {
                    System.out.println("📖 페이지 " + pageNo + "/" + totalPages + " 처리 중...");

                    String url = API_URL + "?auth=" + AUTH_KEY + "&topFinGrpNo=" + code + "&pageNo=" + pageNo;
                    System.out.println("   🌐 API 호출: " + url.substring(0, url.indexOf("auth=")) + "auth=***");

                    String body = client.send(HttpRequest.newBuilder(URI.create(url)).build(),
                            HttpResponse.BodyHandlers.ofString()).body();

                    JsonNode result = mapper.readTree(body).path("result");
                    JsonNode baseList = result.path("baseList");
                    JsonNode optionList = result.path("optionList");

                    System.out.println("   📊 상품 " + baseList.size() + "개, 옵션 " + optionList.size() + "개 발견");

                    // baseList 처리
                    for (JsonNode base : baseList) {
                        processBaseProduct(conn, base);
                    }

                    // optionList 처리
                    for (JsonNode option : optionList) {
                        processProductOption(conn, option);
                    }
                }

                conn.commit();
                System.out.println("✅ 완료: 그룹코드=" + code + " (총 " + totalPages + "페이지)\n");
            }

            System.out.println("🎉 전체 적금상품 데이터 수집 완료!");

        } catch (Exception e) {
            System.out.println("❌ 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 전체 페이지 수 조회
    private int getTotalPages(HttpClient client, ObjectMapper mapper, String groupCode) throws Exception {
        String url = API_URL + "?auth=" + AUTH_KEY + "&topFinGrpNo=" + groupCode + "&pageNo=1";
        System.out.println("   🔍 페이지 수 조회 API 호출");

        String body = client.send(HttpRequest.newBuilder(URI.create(url)).build(),
                HttpResponse.BodyHandlers.ofString()).body();

        JsonNode result = mapper.readTree(body).path("result");
        int maxPage = result.path("max_page_no").asInt(1);
        System.out.println("   📄 최대 페이지 수: " + maxPage);
        return maxPage;
    }

    // 기본 상품 정보 처리
    private static void processBaseProduct(Connection conn, JsonNode base) throws SQLException {
        String productName = base.path("fin_prdt_nm").asText();
        String companyName = base.path("kor_co_nm").asText();
        System.out.println("     🏦 적금상품 처리: " + productName + " (" + companyName + ")");

        // 1. financial_product 저장
        String insertFin = "INSERT INTO financial_product (fin_co_no, fin_prdt_cd, product_name, kor_co_nm, dcls_month,join_way,join_deny, join_member, risk_level, external_link, category_id, subcategory_id) " +
                "VALUES (?, ?, ?, ?, ?, ?,?,?,'LOW', '', 1, 102) " +  // 예적금(1), 적금(102)
                "ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), kor_co_nm=VALUES(kor_co_nm), dcls_month=VALUES(dcls_month)";

        try (PreparedStatement ps = conn.prepareStatement(insertFin, Statement.RETURN_GENERATED_KEYS)) {
            // 필수 필드 설정
            ps.setString(1, base.path("fin_co_no").asText());
            ps.setString(2, base.path("fin_prdt_cd").asText());
            ps.setString(3, productName);
            ps.setString(4, companyName);
            ps.setString(5, base.path("dcls_month").asText());
            ps.setString(6, base.path("join_way").asText()); // 가입방법
            ps.setString(7, base.path("join_deny").asText()); // 가입제한
            ps.setString(8, base.path("join_member").asText()); // 가입대상

            int affected = ps.executeUpdate();
            System.out.println("       💾 financial_product 저장 완료 - 영향받은 행 수: " + affected);

            Long productId = getProductId(conn, ps, base);
            System.out.println("       🔑 Product ID: " + productId);

            // 2. deposit_product 저장
            insertDepositProduct(conn, base, productId);
        }
    }

    // product_id 조회 또는 생성된 키 반환
    private static Long getProductId(Connection conn, PreparedStatement ps, JsonNode base) throws SQLException {
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                // 이미 존재하는 경우 조회
                String sel = "SELECT product_id FROM financial_product WHERE fin_co_no=? AND fin_prdt_cd=?";
                try (PreparedStatement ps2 = conn.prepareStatement(sel)) {
                    ps2.setString(1, base.path("fin_co_no").asText());
                    ps2.setString(2, base.path("fin_prdt_cd").asText());
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        rs2.next();
                        return rs2.getLong(1);
                    }
                }
            }
        }
    }

    private static void insertDepositProduct(Connection conn, JsonNode base, Long productId) throws SQLException {
        String insDp = "INSERT INTO deposit_product (product_id, min_deposit, preferential_conditions, inquiry_url, etc_note, max_limit, dcls_strt_day, dcls_end_day, fin_co_subm_day, contract_period, interest_payment_type, is_digital_only, one_account_per_person, account_limit_note, rotation_cycle, preferential_tags,mtrt_int) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?) " +
                "ON DUPLICATE KEY UPDATE " +
                "preferential_conditions=VALUES(preferential_conditions), etc_note=VALUES(etc_note), " +
                "contract_period=VALUES(contract_period), interest_payment_type=VALUES(interest_payment_type), " +
                "is_digital_only=VALUES(is_digital_only), one_account_per_person=VALUES(one_account_per_person), " +
                "account_limit_note=VALUES(account_limit_note), rotation_cycle=VALUES(rotation_cycle), " +
                "preferential_tags=VALUES(preferential_tags)";

        try (PreparedStatement psDp = conn.prepareStatement(insDp)) {
            // product_id 설정
            psDp.setLong(1, productId);

            // etcNote 파싱
            String etcNoteRaw = base.path("etc_note").asText(null);
            System.out.println("       📝 ETC Note 파싱 중...");
            EtcNoteParsedResult parsed = parseEtcNote(etcNoteRaw);

            if (parsed.minDeposit != null) {
                System.out.println("       💰 최소적립금: " + String.format("%,d원", parsed.minDeposit));
            }
            if (parsed.maxLimit != null) {
                System.out.println("       📊 최대한도: " + String.format("%,d원", parsed.maxLimit));
            }
            if (parsed.isDigitalOnly) {
                System.out.println("       📱 디지털 전용 상품");
            }

            // 필수 필드 설정
            psDp.setInt(2, parsed.minDeposit != null ? parsed.minDeposit : 0);
            psDp.setString(3, base.path("spcl_cnd").asText(null)); // 우대조건
            psDp.setNull(4, Types.VARCHAR); // inquiry_url
            psDp.setString(5, etcNoteRaw);

            // max_limit
            if (parsed.maxLimit != null) {
                psDp.setLong(6, parsed.maxLimit);
            } else {
                // fallback: base JSON 값 사용
                String maxLimitStr = base.path("max_limit").asText();
                if (!maxLimitStr.equals("null") && !maxLimitStr.isEmpty()) {
                    try {
                        long maxLimit = Long.parseLong(maxLimitStr);
                        psDp.setLong(6, maxLimit);
                        System.out.println("       📈 fallback 최대한도: " + String.format("%,d원", maxLimit));
                    } catch (NumberFormatException e) {
                        psDp.setNull(6, Types.BIGINT);
                    }
                } else {
                    psDp.setNull(6, Types.BIGINT);
                }
            }

            // 날짜들
            setDateField(psDp, 7, base.path("dcls_strt_day").asText());
            setDateField(psDp, 8, base.path("dcls_end_day").asText());
            setTimestampField(psDp, 9, base.path("fin_co_subm_day").asText());
            // 계약기간
            psDp.setString(10, parsed.contractPeriod);
            // 이자 지급 방식
            psDp.setString(11, parsed.interestPaymentType);
            // 디지털 전용 여부
            psDp.setBoolean(12, parsed.isDigitalOnly);
            // 1인 1계좌 여부
            if (parsed.oneAccountPerPerson != null) {
                psDp.setBoolean(13, parsed.oneAccountPerPerson);
            } else {
                psDp.setNull(13, Types.BOOLEAN);
            }
            // 계좌수 제한 노트
            psDp.setString(14, parsed.accountLimitNote);
            // 회전주기
            psDp.setString(15, parsed.rotationCycle);
            // 우대 태그
            String preferentialTags = extractPreferentialTags(base.path("spcl_cnd").asText(null));
            psDp.setString(16, preferentialTags);
            psDp.setString(17, base.path("mtrt_int").asText(null));

            int affected = psDp.executeUpdate();
            System.out.println("       💾 deposit_product 저장 완료 - 영향받은 행 수: " + affected);
        }
    }

    // 상품 옵션 처리
    private static void processProductOption(Connection conn, JsonNode option) throws SQLException {
        // product_id 조회
        String selPid = "SELECT product_id FROM financial_product WHERE fin_co_no=? AND fin_prdt_cd=?";
        long productId;

        try (PreparedStatement ps3 = conn.prepareStatement(selPid)) {
            ps3.setString(1, option.path("fin_co_no").asText());
            ps3.setString(2, option.path("fin_prdt_cd").asText());
            try (ResultSet rs3 = ps3.executeQuery()) {
                if (rs3.next()) {
                    productId = rs3.getLong(1);
                    System.out.println("       ⚙️  옵션 처리 - Product ID: " + productId + ", 기간: " + option.path("save_trm").asInt() + "개월");
                } else {
                    System.out.println("       ⚠️  상품을 찾을 수 없어 옵션 건너뜀");
                    return; // product를 찾을 수 없으면 스킵
                }
            }
        }

        // deposit_option 삽입
        String insOpt = "INSERT IGNORE INTO deposit_option (save_trm, intr_rate_type, intr_rate_type_nm, intr_rate, intr_rate2, rsrv_type, rsrv_type_nm, product_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement psOpt = conn.prepareStatement(insOpt)) {

            // 1. 예치 기간 (단위: 개월)
            int saveTrm = option.path("save_trm").asInt();
            psOpt.setInt(1, saveTrm);

            // 2. 이자율 타입 (코드)
            psOpt.setString(2, option.path("intr_rate_type").asText());

            // 3. 이자율 타입 이름 (예: 단리, 복리)
            psOpt.setString(3, option.path("intr_rate_type_nm").asText());

            // 4. 기본 금리 설정
            if (option.path("intr_rate").isNull()) {
                psOpt.setNull(4, Types.DECIMAL);
            } else {
                double intrRate = option.path("intr_rate").asDouble();
                psOpt.setBigDecimal(4, BigDecimal.valueOf(intrRate));
                System.out.println("         💹 기본금리: " + intrRate + "%");
            }

            // 5. 우대 금리 설정
            if (option.path("intr_rate2").isNull()) {
                psOpt.setNull(5, Types.DECIMAL);
            } else {
                double intrRate2 = option.path("intr_rate2").asDouble();
                psOpt.setBigDecimal(5, BigDecimal.valueOf(intrRate2));
                System.out.println("         🔥 우대금리: " + intrRate2 + "%");
            }

            // 6. 적립 방식 코드 (예: 정액적립식, 자유적립식)
            psOpt.setString(6, option.path("rsrv_type").asText(null));

            // 7. 적립 방식 이름
            psOpt.setString(7, option.path("rsrv_type_nm").asText(null));

            // 8. 해당 옵션이 속한 상품의 product_id (외래키)
            psOpt.setLong(8, productId);

            int affected = psOpt.executeUpdate();
            System.out.println("         💾 옵션 저장 완료 - 영향받은 행 수: " + affected);
        }
    }

    // 날짜 필드 설정 헬퍼 메서드
    private static void setDateField(PreparedStatement ps, int paramIndex, String dateStr) throws SQLException {
        if (dateStr != null && !dateStr.isEmpty() && !dateStr.equals("null") && dateStr.length() == 8) {
            try {
                String formatted = dateStr.substring(0, 4) + "-" +
                        dateStr.substring(4, 6) + "-" +
                        dateStr.substring(6, 8);
                ps.setDate(paramIndex, Date.valueOf(formatted));
            } catch (Exception e) {
                ps.setNull(paramIndex, Types.DATE);
            }
        } else {
            ps.setNull(paramIndex, Types.DATE);
        }
    }

    // 타임스탬프 필드 설정 헬퍼 메서드
    private static void setTimestampField(PreparedStatement ps, int paramIndex, String timestampStr) throws SQLException {
        if (timestampStr != null && !timestampStr.isEmpty() && timestampStr.length() >= 12) {
            try {
                String formatted = timestampStr.substring(0, 4) + "-" +
                        timestampStr.substring(4, 6) + "-" +
                        timestampStr.substring(6, 8) + " " +
                        timestampStr.substring(8, 10) + ":" +
                        timestampStr.substring(10, 12) + ":00";
                ps.setTimestamp(paramIndex, Timestamp.valueOf(formatted));
            } catch (Exception e) {
                ps.setNull(paramIndex, Types.TIMESTAMP);
            }
        } else {
            ps.setNull(paramIndex, Types.TIMESTAMP);
        }
    }

    private static EtcNoteParsedResult parseEtcNote(String etcNoteRaw) {
        EtcNoteParsedResult result = new EtcNoteParsedResult();

        if (etcNoteRaw == null || etcNoteRaw.isBlank()) return result;

        // 텍스트 정규화
        String normalized = etcNoteRaw.replaceAll("[\\n\\r]", " ")
                .replaceAll("\\s+", " ")
                .toLowerCase();

        // 디지털 전용 여부 체크
        result.isDigitalOnly =
                // 명시적 키워드
                normalized.contains("디지털전용") ||
                        normalized.contains("디지털채널전용") ||
                        normalized.contains("비대면전용") ||

                        // 인터넷/모바일 뱅킹 전용
                        normalized.contains("인터넷뱅킹") && normalized.contains("전용") ||
                        normalized.contains("모바일뱅킹") && normalized.contains("전용") ||
                        normalized.contains("스마트폰뱅킹") && normalized.contains("전용") ||

                        // 가입방법이 디지털 채널인 경우
                        Pattern.compile("가입방법\\s*[:：]?\\s*(스마트폰|모바일|인터넷|온라인|비대면)")
                                .matcher(normalized).find() ||

                        // 일반적인 패턴 (양방향 체크)
                        Pattern.compile("(인터넷|모바일|스마트폰|비대면|온라인).{0,20}(전용|가입)")
                                .matcher(normalized).find() ||
                        Pattern.compile("(가입|신청).{0,20}(인터넷|모바일|스마트폰|비대면|온라인)")
                                .matcher(normalized).find() ||

                        // "전용상품" 패턴
                        Pattern.compile("(인터넷|모바일|디지털).{0,30}전용상품")
                                .matcher(normalized).find();

        // === 금액 파싱 개선 ===

        // 1. 범위 표현 패턴 (X 이상 Y 이하/이내)
        Pattern rangePattern = Pattern.compile(
                "(?:월\\s*)?(?:가입금액|가입한도|적립금액|납입|불입)?[:\\s]*" +
                        "(\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?)\\s*(천원|만원|십만원|백만원|천만원|억원)?\\s*" +
                        "(이상|초과)(?:[^\\d가-힣]{0,20})" +
                        "(\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?)\\s*(천원|만원|십만원|백만원|천만원|억원)?\\s*" +
                        "(이하|이내|까지)"
        );

        // 2. 최소 금액 패턴
        Pattern minPattern = Pattern.compile(
                "(?:월\\s*)?(?:가입금액|가입한도|적립금액|납입|불입)?[:\\s]*" +
                        "(?:최저|최소|초회불입금|초입금)?[^\\d]{0,10}" +
                        "(\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?)\\s*(천원|만원|십만원|백만원|천만원|억원)?\\s*" +
                        "(이상|초과)"
        );

        // 3. 최대 금액 패턴
        Pattern maxPattern = Pattern.compile(
                "(?:월\\s*)?(?:가입한도|한도|납입한도|적립한도)?[:\\s]*" +
                        "(?:최대|최고)?[^\\d]{0,10}" +
                        "(\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?)\\s*(천원|만원|십만원|백만원|천만원|억원)?\\s*" +
                        "(이하|이내|까지)"
        );

        // 4. "월 XX만원" 형태 패턴
        Pattern monthlyPattern = Pattern.compile(
                "월\\s*(\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?)\\s*(천원|만원|십만원|백만원|천만원)\\s*(이하|이내|까지)"
        );

        // 파싱 우선순위: 범위 > 월별한도 > 개별 최소/최대

        // 1순위: 범위 표현
        Matcher rangeMatcher = rangePattern.matcher(normalized);
        if (rangeMatcher.find()) {
            result.minDeposit = Math.toIntExact(convertToNumber(rangeMatcher.group(1), rangeMatcher.group(2)));
            result.maxLimit = convertToNumber(rangeMatcher.group(4), rangeMatcher.group(5));
            return result; // 범위를 찾으면 다른 패턴 무시
        }

        // 2순위: 월별 한도 (가장 명확한 최대 금액)
        Matcher monthlyMatcher = monthlyPattern.matcher(normalized);
        if (monthlyMatcher.find()) {
            result.maxLimit = convertToNumber(monthlyMatcher.group(1), monthlyMatcher.group(2));
        }

        // 3순위: 개별 최소 금액
        if (result.minDeposit == null) {
            Matcher minMatcher = minPattern.matcher(normalized);
            if (minMatcher.find()) {
                result.minDeposit = Math.toIntExact(convertToNumber(minMatcher.group(1), minMatcher.group(2)));
            }
        }

        // 4순위: 개별 최대 금액 (월별 한도가 없을 때만)
        if (result.maxLimit == null) {
            Matcher maxMatcher = maxPattern.matcher(normalized);
            if (maxMatcher.find()) {
                result.maxLimit = convertToNumber(maxMatcher.group(1), maxMatcher.group(2));
            }
        }

        // 특수 케이스 처리
        handleSpecialCases(normalized, result);

        // 나머지 필드 파싱...
        parseOtherFields(normalized, result);

        return result;
    }

    // 특수 케이스 처리 메서드
    private static void handleSpecialCases(String normalized, EtcNoteParsedResult result) {
        // "제한없음" 처리
        if (normalized.contains("제한없음") || normalized.contains("제한 없음")) {
            result.maxLimit = null; // 또는 Long.MAX_VALUE
        }

        // "1계좌당 가입한도" 형태
        Pattern accountLimitPattern = Pattern.compile(
                "1계좌당\\s+(?:가입한도|한도)[:\\s]*" +
                        "월?\\s*(\\d{1,3}(?:,\\d{3})*)\\s*(만원|백만원|천만원)"
        );
        Matcher accountMatcher = accountLimitPattern.matcher(normalized);
        if (accountMatcher.find() && result.maxLimit == null) {
            result.maxLimit = convertToNumber(accountMatcher.group(1), accountMatcher.group(2));
        }

        // "1인당 월별 최고 XX 이내" 형태
        Pattern personalLimitPattern = Pattern.compile(
                "1인당\\s+월별\\s+최고\\s*(\\d{1,3}(?:,\\d{3})*)\\s*(만원|백만원|천만원)\\s*이내"
        );
        Matcher personalMatcher = personalLimitPattern.matcher(normalized);
        if (personalMatcher.find() && result.maxLimit == null) {
            result.maxLimit = convertToNumber(personalMatcher.group(1), personalMatcher.group(2));
        }
    }

    // 기타 필드 파싱 메서드
    private static void parseOtherFields(String normalized, EtcNoteParsedResult result) {
        // 이자지급방식
        Matcher intMatcher = Pattern.compile("(만기일시지급식|월이자지급식|매월지급식)").matcher(normalized);
        if (intMatcher.find()) {
            result.interestPaymentType = intMatcher.group(1).trim();
        }

        // 가입기간
        Matcher periodMatcher = Pattern.compile("(계약기간|가입기간)[^\\d]{0,10}([0-9~년월일개월가-힣 ,]+)").matcher(normalized);
        if (periodMatcher.find()) {
            result.contractPeriod = periodMatcher.group(2).trim();
        }

        // 1인 1계좌 여부
        if (normalized.contains("1인 1계좌") || normalized.contains("1인1계좌")) {
            result.oneAccountPerPerson = true;
        } else if (normalized.contains("1인 다계좌")) {
            result.oneAccountPerPerson = false;
        }

        // 계좌수 제한
        Matcher limitMatcher = Pattern.compile("(1인\\s*(?:최대|당)?\\s*\\d+계좌|1인\\s*다계좌\\s*가능|공동명의\\s*불가)").matcher(normalized);
        if (limitMatcher.find()) {
            result.accountLimitNote = limitMatcher.group(0).trim();
        }

        // 회전주기
        Matcher rotMatcher = Pattern.compile("(회전주기[^\\n,\\.]{0,40})").matcher(normalized);
        if (rotMatcher.find()) {
            result.rotationCycle = rotMatcher.group(1).trim();
        }
    }

    // 숫자 변환 헬퍼 메서드
    private static long convertToNumber(String numberStr, String unit) {
        if (numberStr == null || numberStr.isEmpty()) return 0;

        long base = Long.parseLong(numberStr.replaceAll(",", ""));

        if (unit == null || unit.isEmpty()) return base;

        switch (unit.toLowerCase()) {
            case "천원":   return base * 1_000L;
            case "만원":   return base * 10_000L;
            case "십만원": return base * 100_000L;
            case "백만원": return base * 1_000_000L;
            case "천만원": return base * 10_000_000L;
            case "억원":   return base * 100_000_000L;
            default:       return base;
        }
    }

    private static String extractPreferentialTags(String text) {
        if (text == null || text.isBlank()) return null;

        text = text.toLowerCase().replaceAll("\\s+", "");

        String[] keywords = {
                "신규", "재예치", "급여", "연금", "카드",
                "청약", "모바일", "자동이체", "마케팅동의", "첫거래"
        };

        Set<String> tags = new LinkedHashSet<>();
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                tags.add(keyword);
            }
        }
        return tags.isEmpty() ? null : String.join(",", tags);
    }
}