package org.scoula.chatgpt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.scoula.chatgpt.dto.portfolio.ProductInfoResponse;
import org.scoula.chatgpt.dto.portfolio.RateOption;
import org.scoula.chatgpt.dto.portfolio.TermOption;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProductInfoParser {

    public ProductInfoResponse parseResponse(String gptResponse, String category) {
        ProductInfoResponse response = new ProductInfoResponse();

        try {
            ObjectMapper mapper = new ObjectMapper();

            // 이스케이프된 JSON 처리
            String cleanJson = gptResponse;
            if (gptResponse != null && gptResponse.startsWith("\"") && gptResponse.endsWith("\"")) {
                cleanJson = mapper.readValue(gptResponse, String.class);
            }

            // 숫자 내 언더스코어 제거
            cleanJson = cleanJson.replaceAll("(\\d)_(\\d)", "$1$2");

            JsonNode jsonNode = mapper.readTree(cleanJson);

            // 공통 필드
            response.setSuccess(true);
            response.setCategory(jsonNode.path("category").asText());
            response.setSubcategory(jsonNode.path("subcategory").asText());
            response.setConfidence(jsonNode.path("confidence").asDouble(0.5));
            response.setRawResponse(gptResponse);

            // 카테고리별 파싱
            switch (category) {
                case "deposit":
                    parseDepositResponse(jsonNode, response);
                    break;
                case "savings":
                    parseSavingsResponse(jsonNode, response);
                    break;
                case "insurance":
                    parseInsuranceResponse(jsonNode, response);
                    break;
                case "pension":
                    parsePensionResponse(jsonNode, response);
                    break;
                case "stock":
                    parseStockResponse(jsonNode, response);
                    break;
                default:
                    parseDefaultResponse(jsonNode, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setCategory(category);
            response.setMemo("응답 파싱에 실패했습니다: " + e.getMessage());
            response.setConfidence(0.0);
        }

        return response;
    }

    private Double extractPercentPoint(String text) {
        if (text == null) return null;
        Pattern p = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*%p?");
        Matcher m = p.matcher(text);
        Double val = null;
        while (m.find()) { // 여러 개면 마지막(보통 우대금리)을 채택
            val = Double.parseDouble(m.group(1));
        }
        return val;
    }

    // ===== 예금 =====
    private void parseDepositResponse(JsonNode jsonNode, ProductInfoResponse response) {
        response.setBaseRate(jsonNode.path("baseRate").asDouble(0.0));
        response.setMaxRate(jsonNode.path("maxRate").asDouble(0.0));
        response.setMinAmount(jsonNode.path("minAmount").asLong(100000L));
        response.setMaxAmount(jsonNode.path("maxAmount").asLong(100000000L));
        response.setAmountType("TOTAL");
        response.setRiskLevel("LOW");

        List<TermOption> termOptions = new ArrayList<>();
        JsonNode termOptionsNode = jsonNode.path("termOptions");
        if (termOptionsNode.isArray()) {
            for (JsonNode termNode : termOptionsNode) {
                int months = termNode.asInt();
                double termRate = response.getBaseRate() + (months >= 24 ? 0.3 : months >= 12 ? 0.1 : 0.0);
                String desc = months == 12 ? "1년" : months == 24 ? "2년" : months == 36 ? "3년" : months + "개월";
                termOptions.add(new TermOption(months, termRate, desc));
            }
            response.setRecommendedTerm(12);
        }
        response.setTermOptions(termOptions);

        List<RateOption> rateOptions = new ArrayList<>();
        rateOptions.add(new RateOption("기본금리", response.getBaseRate(), "기본 적용"));

        JsonNode features = jsonNode.path("features");
        if (features.isArray()) {
            for (JsonNode featureNode : features) {
                String featureText = featureNode.asText();
                Double pct = extractPercentPoint(featureText);
                if (featureText.contains("청년") && pct != null) {
                    rateOptions.add(new RateOption("청년우대", pct, featureText));
                } else if (featureText.contains("급여") && pct != null) {
                    rateOptions.add(new RateOption("급여이체우대", pct, featureText));
                } else if (featureText.contains("대출") && pct != null) {
                    rateOptions.add(new RateOption("첫대출우대", pct, featureText));
                }
            }
        }
        response.setRateOptions(rateOptions);

        StringBuilder memo = new StringBuilder();
        memo.append("예금 상품 정보\n");
        memo.append("예치한도: ").append(response.getMinAmount()).append("~").append(response.getMaxAmount()).append("원\n");
        memo.append("기본금리: ").append(response.getBaseRate()).append("%\n");
        memo.append("최고금리: ").append(response.getMaxRate()).append("%\n");
        response.setMemo(memo.toString());
    }

    // ===== 적금 =====
    private void parseSavingsResponse(JsonNode jsonNode, ProductInfoResponse response) {
        response.setBaseRate(jsonNode.path("baseRate").asDouble(0.0));
        response.setMaxRate(jsonNode.path("maxRate").asDouble(0.0));
        response.setMinAmount(jsonNode.path("minMonthlyAmount").asLong(10000L));
        response.setMaxAmount(jsonNode.path("maxMonthlyAmount").asLong(500000L));
        response.setAmountType("MONTHLY");
        response.setRiskLevel("LOW");

        List<TermOption> termOptions = new ArrayList<>();
        JsonNode termOptionsNode = jsonNode.path("termOptions");
        if (termOptionsNode.isArray()) {
            for (JsonNode termNode : termOptionsNode) {
                int months = termNode.asInt();
                double termRate = response.getBaseRate() + (months > 12 ? 0.2 : 0.0);
                String desc = months == 12 ? "1년" : months == 24 ? "2년" : months == 36 ? "3년" : months + "개월";
                termOptions.add(new TermOption(months, termRate, desc));
            }
            response.setRecommendedTerm(24);
        }
        response.setTermOptions(termOptions);

        List<RateOption> rateOptions = new ArrayList<>();
        rateOptions.add(new RateOption("기본금리", response.getBaseRate(), "기본 적용"));

        JsonNode features = jsonNode.path("features");
        if (features.isArray()) {
            for (JsonNode feature : features) {
                String featureText = feature.asText();
                if (featureText.contains("청년") && featureText.contains("%")) {
                    String rateStr = featureText.replaceAll("[^0-9.]", "");
                    try {
                        double rate = Double.parseDouble(rateStr);
                        rateOptions.add(new RateOption("청년우대", rate, featureText));
                    } catch (NumberFormatException e) {
                        rateOptions.add(new RateOption("청년우대", 1.0, featureText));
                    }
                } else if (featureText.contains("급여") && featureText.contains("%")) {
                    String rateStr = featureText.replaceAll("[^0-9.]", "");
                    try {
                        double rate = Double.parseDouble(rateStr);
                        rateOptions.add(new RateOption("급여이체우대", rate, featureText));
                    } catch (NumberFormatException e) {
                        rateOptions.add(new RateOption("급여이체우대", 0.5, featureText));
                    }
                }
            }
        }
        response.setRateOptions(rateOptions);

        StringBuilder memo = new StringBuilder();
        memo.append("적금 상품 정보\n");
        memo.append("월납입한도: ").append(response.getMinAmount()).append("~").append(response.getMaxAmount()).append("원\n");
        memo.append("기본금리: ").append(response.getBaseRate()).append("%\n");
        memo.append("최고금리: ").append(response.getMaxRate()).append("%\n");
        response.setMemo(memo.toString());
    }

    // ===== 보험 =====
    private void parseInsuranceResponse(JsonNode jsonNode, ProductInfoResponse response) {
        JsonNode premium = jsonNode.path("monthlyPremium");
        response.setMinAmount(premium.path("min").asLong(50000L));
        response.setMaxAmount(premium.path("max").asLong(300000L));
        response.setAmountType("PREMIUM");
        response.setRiskLevel("MEDIUM");

        response.setBaseRate(0.0);
        response.setMaxRate(0.0);

        List<TermOption> termOptions = new ArrayList<>();
        JsonNode termOptionsNode = jsonNode.path("termOptions");
        if (termOptionsNode.isArray()) {
            for (JsonNode termNode : termOptionsNode) {
                if (termNode.isNumber()) {
                    int years = termNode.asInt();
                    int months = years * 12;
                    String desc = years + "년";
                    termOptions.add(new TermOption(months, 0.0, desc));
                } else if ("종신".equals(termNode.asText())) {
                    termOptions.add(new TermOption(null, 0.0, "종신"));
                }
            }
            response.setRecommendedTerm(240);
        }
        response.setTermOptions(termOptions);
        response.setRateOptions(new ArrayList<>());

        StringBuilder memo = new StringBuilder();
        memo.append("보험 상품 정보\n");
        memo.append("월보험료: ").append(response.getMinAmount()).append("~").append(response.getMaxAmount()).append("원\n\n");

        JsonNode coverage = jsonNode.path("coverage");
        if (!coverage.isMissingNode()) {
            memo.append("[보장 내용]\n");
            memo.append("사망보험금: ").append(coverage.path("deathBenefit").asText()).append("원\n");
            JsonNode additionalBenefits = coverage.path("additionalBenefits");
            if (additionalBenefits.isArray() && additionalBenefits.size() > 0) {
                memo.append("추가보장: ");
                for (int i = 0; i < additionalBenefits.size(); i++) {
                    if (i > 0) memo.append(", ");
                    memo.append(additionalBenefits.get(i).asText());
                }
                memo.append("\n");
            }
            memo.append("\n");
        }

        JsonNode ageLimit = jsonNode.path("ageLimit");
        if (!ageLimit.isMissingNode()) {
            memo.append("[가입 조건]\n");
            memo.append("가입연령: ").append(ageLimit.path("minAge").asText())
                    .append("~").append(ageLimit.path("maxAge").asText()).append("세\n\n");
        }

        JsonNode features = jsonNode.path("features");
        if (features.isArray() && features.size() > 0) {
            memo.append("[상품 특징]\n");
            for (JsonNode feature : features) {
                memo.append("- ").append(feature.asText()).append("\n");
            }
            memo.append("\n");
        }

        JsonNode taxBenefits = jsonNode.path("taxBenefits");
        if (taxBenefits.isArray() && taxBenefits.size() > 0) {
            memo.append("[세제 혜택]\n");
            for (JsonNode benefit : taxBenefits) {
                memo.append("- ").append(benefit.asText()).append("\n");
            }
            memo.append("\n");
        }

        String note = jsonNode.path("note").asText("");
        if (!note.isEmpty()) {
            memo.append("[주의사항]\n").append(note);
        }

        response.setMemo(memo.toString());
    }

    // ===== 연금 =====
    private void parsePensionResponse(JsonNode jsonNode, ProductInfoResponse response) {
        JsonNode contribution = jsonNode.path("monthlyContribution");
        response.setMinAmount(contribution.path("min").asLong(50000L));
        response.setMaxAmount(contribution.path("max").asLong(1800000L));
        response.setAmountType("MONTHLY");
        response.setRiskLevel("MEDIUM");

        response.setBaseRate(jsonNode.path("expectedReturn").asDouble(0.0));
        response.setMaxRate(jsonNode.path("expectedReturn").asDouble(0.0));

        List<TermOption> termOptions = new ArrayList<>();
        int[] commonPeriods = {120, 180, 240, 300};
        for (int months : commonPeriods) {
            int years = months / 12;
            double expectedReturn = response.getBaseRate();
            termOptions.add(new TermOption(months, expectedReturn, years + "년 납입"));
        }
        response.setRecommendedTerm(180);
        response.setTermOptions(termOptions);

        response.setRateOptions(new ArrayList<>());

        StringBuilder memo = new StringBuilder();
        memo.append("연금 상품 정보\n");
        memo.append("월납입한도: ").append(response.getMinAmount()).append("~").append(response.getMaxAmount()).append("원\n");
        memo.append("예상수익률: ").append(response.getBaseRate()).append("%\n\n");

        JsonNode taxBenefits = jsonNode.path("taxBenefits");
        if (!taxBenefits.isMissingNode()) {
            memo.append("[세제 혜택]\n");
            memo.append("세액공제: ").append(taxBenefits.path("deduction").asText()).append("\n");
            memo.append("공제율: ").append(taxBenefits.path("deductionRate").asText()).append("\n");
            memo.append("연금수령세: ").append(taxBenefits.path("pensionTax").asText()).append("\n\n");
        }

        JsonNode ageLimit = jsonNode.path("ageLimit");
        if (!ageLimit.isMissingNode()) {
            memo.append("[가입 조건]\n");
            memo.append("가입연령: ").append(ageLimit.path("minJoinAge").asText())
                    .append("~").append(ageLimit.path("maxJoinAge").asText()).append("세\n");
            memo.append("연금수령: 만 ").append(ageLimit.path("receiveAge").asText()).append("세부터\n\n");
        }

        JsonNode investmentOptions = jsonNode.path("investmentOptions");
        if (investmentOptions.isArray() && investmentOptions.size() > 0) {
            memo.append("[투자 옵션]\n");
            for (JsonNode option : investmentOptions) {
                memo.append("- ").append(option.asText()).append("\n");
            }
            memo.append("\n");
        }

        JsonNode fees = jsonNode.path("fees");
        if (!fees.isMissingNode()) {
            memo.append("[수수료]\n");
            memo.append("운용수수료: ").append(fees.path("managementFee").asText()).append("\n");
            memo.append("판매수수료: ").append(fees.path("salesFee").asText()).append("\n");
        }

        response.setMemo(memo.toString());
    }

    // ===== 주식 =====
    private void parseStockResponse(JsonNode jsonNode, ProductInfoResponse response) {
        JsonNode stockInfo = jsonNode.path("stockInfo");
        JsonNode fundamentals = jsonNode.path("fundamentals");

        Long currentPrice = stockInfo.path("currentPrice").asLong(0L);
        response.setMinAmount(currentPrice);
        response.setMaxAmount(null);
        response.setAmountType("TOTAL");
        response.setRiskLevel("HIGH");

        response.setBaseRate(fundamentals.path("dividendYield").asDouble(0.0));
        response.setMaxRate(null);

        response.setTermOptions(new ArrayList<>());
        response.setRateOptions(new ArrayList<>());
        response.setRecommendedTerm(null);

        StringBuilder memo = new StringBuilder();
        memo.append("주식 정보\n");
        memo.append("현재가: ").append(stockInfo.path("currentPrice").asText());
        String currency = stockInfo.path("currency").asText("KRW");
        if ("USD".equals(currency)) memo.append(" USD (달러)");
        else memo.append("원");
        memo.append("\n");

        memo.append("시장: ").append(jsonNode.path("marketType").asText()).append("\n");
        memo.append("업종: ").append(stockInfo.path("sector").asText()).append("\n");

        if (!fundamentals.isMissingNode()) {
            memo.append("PER: ").append(fundamentals.path("per").asText()).append(" | ");
            memo.append("배당률: ").append(fundamentals.path("dividendYield").asText()).append("%\n");
        }

        JsonNode analystRating = jsonNode.path("analystRating");
        if (!analystRating.isMissingNode()) {
            memo.append("투자의견: ").append(analystRating.path("recommendation").asText());
            String targetPrice = analystRating.path("targetPrice").asText();
            if (!targetPrice.isEmpty()) {
                memo.append(" (목표가: ").append(targetPrice);
                memo.append("USD".equals(currency) ? " USD" : "원").append(")");
            }
            memo.append("\n");
        }
        memo.append("\n※ 주식 투자는 원금 손실 위험이 있습니다.");
        response.setMemo(memo.toString());
    }

    // ===== 기본(기타) =====
    private void parseDefaultResponse(JsonNode jsonNode, ProductInfoResponse response) {
        response.setBaseRate(jsonNode.path("expectedReturn").asDouble(0.0));
        response.setMaxRate(jsonNode.path("expectedReturn").asDouble(0.0));
        response.setMinAmount(jsonNode.path("minimumInvestment").asLong(100000L));
        response.setMaxAmount(jsonNode.path("minimumInvestment").asLong(100000000L));
        response.setAmountType("TOTAL");

        String riskLevel = jsonNode.path("riskLevel").asText("MEDIUM");
        response.setRiskLevel(riskLevel);

        List<TermOption> termOptions = new ArrayList<>();
        int[] periods = {12, 24, 36, 60};
        for (int months : periods) {
            int years = months / 12;
            double returnRate = response.getBaseRate() + (years >= 3 ? 0.5 : 0.0);
            termOptions.add(new TermOption(months, returnRate, years + "년"));
        }
        response.setRecommendedTerm(24);
        response.setTermOptions(termOptions);

        List<RateOption> feeOptions = new ArrayList<>();
        JsonNode fees = jsonNode.path("fees");
        if (!fees.isMissingNode()) {
            feeOptions.add(new RateOption("운용수수료", 0.0, fees.path("managementFee").asText()));
            feeOptions.add(new RateOption("가입수수료", 0.0, fees.path("entryFee").asText()));
            feeOptions.add(new RateOption("환매수수료", 0.0, fees.path("exitFee").asText()));
        }
        response.setRateOptions(feeOptions);

        String note = jsonNode.path("note").asText("");
        if (note.isEmpty()) {
            note = "기타 투자 상품입니다. 상세 조건을 확인해주세요.";
        }
        response.setMemo(note);
    }
}
