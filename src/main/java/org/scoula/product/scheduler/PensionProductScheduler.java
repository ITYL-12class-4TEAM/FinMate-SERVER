package org.scoula.product.scheduler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;

@Component
public class PensionProductScheduler {
    private static final String[] GROUP_CODES = {"050000", "060000"}; // 보험, 금융투자

    @Value("${finlife.api.url.pension}")
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
    public void fetchPensionProductsScheduled() {
        executeDataFetch();
    }

    public void fetchPensiontProductsManually() {
        System.out.println("🔧 [Spring Legacy] 수동 연금상품 데이터 수집 시작...");
        executeDataFetch();
    }

    public void executeDataFetch() {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            // 외래키 사용하기 위해 auto commit false
            conn.setAutoCommit(false);

            for (String code : GROUP_CODES) {
                System.out.println("🔍 그룹코드 " + code + " 처리 시작...");

                // 첫 번째 페이지로 전체 페이지 수 확인
                int totalPages = getTotalPages(client, mapper, code);
                System.out.println("📄 총 페이지 수: " + totalPages);

                // 모든 페이지 처리
                for (int pageNo = 1; pageNo <= totalPages; pageNo++) {
                    System.out.println("📖 페이지 " + pageNo + "/" + totalPages + " 처리 중...");

                    String url = API_URL + "?auth=" + AUTH_KEY + "&topFinGrpNo=" + code + "&pageNo=" + pageNo;
                    String body = client.send(HttpRequest.newBuilder(URI.create(url)).build(),
                            HttpResponse.BodyHandlers.ofString()).body();

                    JsonNode result = mapper.readTree(body).path("result");
                    JsonNode baseList = result.path("baseList");
                    JsonNode optionList = result.path("optionList");

                    System.out.println("  - 상품 " + baseList.size() + "개, 옵션 " + optionList.size() + "개");

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
        } catch (Exception e) {
            System.out.println("❌ 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 전체 페이지 수 조회
    private int getTotalPages(HttpClient client, ObjectMapper mapper, String groupCode) throws Exception {
        String url = API_URL + "?auth=" + AUTH_KEY + "&topFinGrpNo=" + groupCode + "&pageNo=1";
        String body = client.send(HttpRequest.newBuilder(URI.create(url)).build(),
                HttpResponse.BodyHandlers.ofString()).body();

        JsonNode result = mapper.readTree(body).path("result");
        return result.path("max_page_no").asInt(1); // 기본값 1
    }

    // 기본 상품 정보 처리
    private static void processBaseProduct(Connection conn, JsonNode base) throws SQLException {
        // 1. financial_product 저장
        String insertFin = "INSERT INTO financial_product (fin_co_no, fin_prdt_cd, product_name, kor_co_nm, dcls_month, risk_level, external_link, category_id, subcategory_id) " +
                "VALUES (?, ?, ?, ?, ?, 'MEDIUM', '', 5, 501) " +  // 연금(5), 연금저축(501)
                "ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), kor_co_nm=VALUES(kor_co_nm), dcls_month=VALUES(dcls_month)";

        try (PreparedStatement ps = conn.prepareStatement(insertFin, Statement.RETURN_GENERATED_KEYS)) {
            // 필수 필드 설정
            ps.setString(1, base.path("fin_co_no").asText());
            ps.setString(2, base.path("fin_prdt_cd").asText());
            ps.setString(3, base.path("fin_prdt_nm").asText());
            ps.setString(4, base.path("kor_co_nm").asText());
            ps.setString(5, base.path("dcls_month").asText());
            ps.executeUpdate();

            Long productId = getProductId(conn, ps, base);

            // 2. pension_product 저장
            insertPensionProduct(conn, base, productId);
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

    // pension_product 테이블에 삽입
    private static void insertPensionProduct(Connection conn, JsonNode base, Long productId) throws SQLException {
        // SQL에 category 컬럼 추가
        String insPension = "INSERT INTO pension_product (product_id, pnsn_kind, pnsn_kind_nm, sale_strt_day, mntn_cnt, prdt_type, prdt_type_nm, dcls_rate, guar_rate, btrm_prft_rate_1, btrm_prft_rate_2, btrm_prft_rate_3, etc, sale_co, dcls_strt_day, dcls_end_day, fin_co_subm_day, category, avg_prft_rate) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE pnsn_kind=VALUES(pnsn_kind), pnsn_kind_nm=VALUES(pnsn_kind_nm), etc=VALUES(etc), category=VALUES(category)";

        try (PreparedStatement psPension = conn.prepareStatement(insPension)) {
            // product_id 설정
            psPension.setLong(1, productId);

            // 연금 종류
            psPension.setString(2, base.path("pnsn_kind").asText(null));
            psPension.setString(3, base.path("pnsn_kind_nm").asText(null));

            // 판매 시작일
            setDateField(psPension, 4, base.path("sale_strt_day").asText());

            // 유지건수
            String mntnCntStr = base.path("mntn_cnt").asText();
            if (!mntnCntStr.equals("null") && !mntnCntStr.isEmpty()) {
                try {
                    psPension.setLong(5, Long.parseLong(mntnCntStr));
                } catch (NumberFormatException e) {
                    psPension.setNull(5, Types.BIGINT);
                }
            } else {
                psPension.setNull(5, Types.BIGINT);
            }

            // 상품유형
            String prdtType = base.path("prdt_type").asText(null);
            psPension.setString(6, prdtType);
            psPension.setString(7, base.path("prdt_type_nm").asText(null));

            // 공시이율
            setDecimalField(psPension, 8, base.path("dcls_rate").asText());

            // 최저보증이율
            setDecimalField(psPension, 9, base.path("guar_rate").asText());

            // 전년도 수익률
            setDecimalField(psPension, 10, base.path("btrm_prft_rate_1").asText());

            // 전전년도 수익률
            setDecimalField(psPension, 11, base.path("btrm_prft_rate_2").asText());

            // 전전전년도 수익률
            setDecimalField(psPension, 12, base.path("btrm_prft_rate_3").asText());

            // 기타사항
            psPension.setString(13, base.path("etc").asText(null));

            // 판매사
            psPension.setString(14, base.path("sale_co").asText(null));

            // 공시 시작일
            setDateField(psPension, 15, base.path("dcls_strt_day").asText());

            // 공시 종료일
            setDateField(psPension, 16, base.path("dcls_end_day").asText());

            // 금융회사 제출일
            setTimestampField(psPension, 17, base.path("fin_co_subm_day").asText());

            // 카테고리 설정 (새로 추가)
            String prdtTypeNm = base.path("prdt_type_nm").asText(null);
            String category = getProductCategory(prdtTypeNm);
            psPension.setString(18, category);
            JsonNode avgRateNode = base.path("avg_prft_rate");
            if (avgRateNode.isInt() || avgRateNode.isDouble()) {
                psPension.setInt(19, avgRateNode.asInt());
            } else {
                psPension.setNull(19, Types.INTEGER);
            }
            psPension.executeUpdate();
        }
    }

    // 상품 옵션 처리
    private static void processProductOption(Connection conn, JsonNode option) throws SQLException {
        // 연금가입나이 체크 (30 이하만 처리)
        String pnsnEntrAge = option.path("pnsn_entr_age").asText(null);
        if (pnsnEntrAge != null && !pnsnEntrAge.isEmpty()) {
            try {
                int age = Integer.parseInt(pnsnEntrAge);
                if (age > 30) {
                    return; // 30 초과면 처리하지 않고 리턴
                }
            } catch (NumberFormatException e) {
                // 숫자가 아니면 건너뛰기
                return;
            }
        }
        // product_id 조회
        String selPid = "SELECT product_id FROM financial_product WHERE fin_co_no=? AND fin_prdt_cd=?";
        long productId;

        try (PreparedStatement ps3 = conn.prepareStatement(selPid)) {
            ps3.setString(1, option.path("fin_co_no").asText()); // 금융회사 코드
            ps3.setString(2, option.path("fin_prdt_cd").asText()); // 상품 코드
            try (ResultSet rs3 = ps3.executeQuery()) {
                if (rs3.next()) {
                    productId = rs3.getLong(1);
                } else {
                    return; // 해당 상품이 없으면 옵션 저장 건너뜀
                }
            }
        }

        // pension_option 삽입
        String sql = """
        INSERT INTO pension_option
        (product_id, pnsn_recp_trm, pnsn_recp_trm_nm, pnsn_entr_age, pnsn_entr_age_nm,
         mon_paym_atm, mon_paym_atm_nm, paym_prd, paym_prd_nm,
         pnsn_strt_age, pnsn_strt_age_nm, pnsn_recp_amt)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
          pnsn_recp_amt = VALUES(pnsn_recp_amt),
          pnsn_recp_trm_nm = VALUES(pnsn_recp_trm_nm),
          pnsn_entr_age_nm = VALUES(pnsn_entr_age_nm),
          mon_paym_atm_nm = VALUES(mon_paym_atm_nm),
          paym_prd_nm = VALUES(paym_prd_nm),
          pnsn_strt_age_nm = VALUES(pnsn_strt_age_nm)
    """;

        try (PreparedStatement psIns = conn.prepareStatement(sql)) {
            psIns.setLong(1, productId);

            // 수령 기간 코드/이름
            psIns.setString(2, option.path("pnsn_recp_trm").asText(null));
            psIns.setString(3, option.path("pnsn_recp_trm_nm").asText(null));

            // 연금 가입 나이 코드/이름
            psIns.setString(4, option.path("pnsn_entr_age").asText(null));
            psIns.setString(5, option.path("pnsn_entr_age_nm").asText(null));

            // 월 납입 금액 코드/이름
            psIns.setString(6, option.path("mon_paym_atm").asText(null));
            psIns.setString(7, option.path("mon_paym_atm_nm").asText(null));

            // 납입 기간 코드/이름
            psIns.setString(8, option.path("paym_prd").asText(null));
            psIns.setString(9, option.path("paym_prd_nm").asText(null));

            // 연금 수령 시작 나이 코드/이름
            psIns.setString(10, option.path("pnsn_strt_age").asText(null));
            psIns.setString(11, option.path("pnsn_strt_age_nm").asText(null));

            // 수령 예상 금액
            if (option.hasNonNull("pnsn_recp_amt")) {
                psIns.setBigDecimal(12, new BigDecimal(option.get("pnsn_recp_amt").asText()));
            } else {
                psIns.setNull(12, Types.DECIMAL);
            }

            psIns.executeUpdate();
        }
    }

    // Decimal 필드 설정 메서드
    private static void setDecimalField(PreparedStatement ps, int paramIndex, String decimalStr) throws SQLException {
        if (decimalStr != null && !decimalStr.isEmpty() && !decimalStr.equals("null")) {
            try {
                ps.setBigDecimal(paramIndex, new BigDecimal(decimalStr));
            } catch (NumberFormatException e) {
                ps.setNull(paramIndex, Types.DECIMAL);
            }
        } else {
            ps.setNull(paramIndex, Types.DECIMAL);
        }
    }

    // 날짜 필드 설정 메서드
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

    // 타임스탬프 필드 설정 메서드
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

    // 분류 메서드
    private static String getProductCategory(String prdtType) {
        if (prdtType == null || prdtType.isEmpty()) {
            return "기타";
        }

        switch (prdtType) {
            case "금리연동형":
            case "단기금융(MMF)":
            case "혼합채권형":
                return "저위험";
            case "혼합주식형":
            case "혼합자산":
                return "중고위험";
            case "재간접형":
            case "재간접파생형":
            case "특별자산":
            case "파생형":
            case "주식형" :
                return "고위험";
            default:
                return "기타";
        }
    }
}