package org.scoula.product.scheduler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.Properties;

@Component
public class ProductCompanyScheduler {
    private static final String[] GROUP_CODES = {"020000", "030200", "030300", "050000", "060000"};

    private String API_URL;
    private String AUTH_KEY;
    private String DB_URL;
    private String DB_USER;
    private String DB_PASS;

    @PostConstruct
    public void init() {
        try {
            Properties props = new Properties();
            String baseDir = System.getProperty("config.location", "");
            if (!baseDir.isEmpty() && !baseDir.endsWith("/") && !baseDir.endsWith("\\")) {
                baseDir = baseDir + "/";
            }
            String oauthPath = baseDir + "application-oauth.properties";
            String dbPath = baseDir + "application-local.properties";
            try (FileInputStream oauthInput = new FileInputStream(oauthPath)) {
                props.load(oauthInput);
            }
            try (FileInputStream dbInput = new FileInputStream(dbPath)) {
                props.load(dbInput);
            }
            API_URL = props.getProperty("finlife.api.url.productCompany");
            AUTH_KEY = props.getProperty("finlife.api.key");
            DB_URL = props.getProperty("jdbc.url");
            DB_USER = props.getProperty("jdbc.username");
            DB_PASS = props.getProperty("jdbc.password");
        } catch (Exception e) {
            throw new RuntimeException("프로퍼티 로딩 실패", e);
        }
    }

    @Scheduled(cron = "0 0 4 * * 1")
    public void fetchProductCompanyScheduled() {
        executeDataFetch();
    }

    public void fetchProductCompanyManually() {
        System.out.println("🔧 [Spring Legacy] 수동 상품회사 데이터 수집 시작...");
        executeDataFetch();
    }

    public void executeDataFetch() {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
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

                    System.out.println("  - 회사 " + baseList.size() + "개");

                    // baseList 처리 - 회사 정보만 처리
                    for (JsonNode base : baseList) {
                        processProductCompany(conn, base);
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

    // 회사 정보 처리 - product_company 테이블에 저장
    private static void processProductCompany(Connection conn, JsonNode base) throws SQLException {
        String insertCompany = "INSERT INTO product_company (dcls_month, fin_co_no, kor_co_nm, dcls_chrg_man, homp_url, cal_tel) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "dcls_month=VALUES(dcls_month), " +
                "kor_co_nm=VALUES(kor_co_nm), " +
                "dcls_chrg_man=VALUES(dcls_chrg_man), " +
                "homp_url=VALUES(homp_url), " +
                "cal_tel=VALUES(cal_tel)";

        try (PreparedStatement ps = conn.prepareStatement(insertCompany)) {
            String dclsMonth = base.path("dcls_month").asText();
            String finCoNo = base.path("fin_co_no").asText();
            String korCoNm = base.path("kor_co_nm").asText();
            String dclsChrgMan = base.path("dcls_chrg_man").asText();
            String hompUrl = base.path("homp_url").asText();
            String calTel = base.path("cal_tel").asText();


            // 길이 체크 및 잘라내기
            if (dclsChrgMan.length() > 100) {
                System.out.println("    ⚠️ dcls_chrg_man 길이 초과: " + dclsChrgMan.length() + " -> 100자로 잘라냄");
                dclsChrgMan = dclsChrgMan.substring(0, 100);
            }
            if (hompUrl.length() > 255) {
                System.out.println("    ⚠️ homp_url 길이 초과: " + hompUrl.length() + " -> 255자로 잘라냄");
                hompUrl = hompUrl.substring(0, 255);
            }
            if (calTel.length() > 20) {
                System.out.println("    ⚠️ cal_tel 길이 초과: " + calTel.length() + " -> 20자로 잘라냄");
                calTel = calTel.substring(0, 20);
            }


            ps.setString(1, dclsMonth);
            ps.setString(2, finCoNo);
            ps.setString(3, korCoNm);
            ps.setString(4, dclsChrgMan);
            ps.setString(5, hompUrl);
            ps.setString(6, calTel);

            int affected = ps.executeUpdate();
            System.out.println("    영향받은 행 수: " + affected);



        } catch (SQLException e) {
            System.out.println("  ❌ SQL 오류 발생:");
            System.out.println("    오류 코드: " + e.getErrorCode());
            System.out.println("    SQL 상태: " + e.getSQLState());
            System.out.println("    오류 메시지: " + e.getMessage());
            throw e;
        }
    }
}