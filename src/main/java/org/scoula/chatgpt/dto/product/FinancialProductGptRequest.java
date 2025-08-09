package org.scoula.chatgpt.dto.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "GPT 요약을 위한 금융 상품 요청 DTO")
public class FinancialProductGptRequest {

    @ApiModelProperty(value = "상품 ID", example = "1")
    private Long productId;

    @ApiModelProperty(value = "상품 유형 (예: deposit, saving)", example = "deposit")
    private String productType;

    @ApiModelProperty(value = "금융회사 코드", example = "0010001")
    private String finCoNo;

    @ApiModelProperty(value = "금융회사 이름", example = "우리은행")
    private String korCoNm;

    @ApiModelProperty(value = "금융상품 코드", example = "WR0001B")
    private String finPrdtCd;

    @ApiModelProperty(value = "금융상품 이름", example = "WON플러스예금")
    private String finPrdtNm;

    @ApiModelProperty(value = "가입 방법", example = "인터넷,스마트폰,전화(텔레뱅킹)")
    private String joinWay;

    @ApiModelProperty(value = "특이사항", example = "해당사항 없음")
    private String spclCnd;

    @ApiModelProperty(value = "우대 조건 태그", example = "[\"청년 우대\"]", required = false)
    private List<String> preferentialTags;

    @ApiModelProperty(value = "가입 대상", example = "실명의 개인")
    private String joinMember;

    @ApiModelProperty(value = "최고 금리", example = "2.5")
    private Double maxIntrRate;

    @ApiModelProperty(value = "상품 가입 가능 여부", example = "true")
    private Boolean available;

    @ApiModelProperty(value = "상품 상세 정보")
    private ProductDetail productDetail;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ApiModel(description = "금융 상품의 상세 정보")
    public static class ProductDetail {

        @ApiModelProperty(value = "상품 가입 가능 여부", example = "true")
        private Boolean available;

        @ApiModelProperty(value = "상품 공시 시작일", example = "2025-07-21")
        private String dclsStrtDay;

        @ApiModelProperty(value = "상품 공시 종료일", example = "2026-07-21")
        private String dclsEndDay;

        @ApiModelProperty(value = "상품 ID", example = "1")
        private Long productId;

        @ApiModelProperty(value = "금융회사 코드", example = "0010001")
        private String finCoNo;

        @ApiModelProperty(value = "금융회사명", example = "우리은행")
        private String korCoNm;

        @ApiModelProperty(value = "금융상품 코드", example = "WR0001B")
        private String finPrdtCd;

        @ApiModelProperty(value = "금융상품명", example = "WON플러스예금")
        private String finPrdtNm;

        @ApiModelProperty(value = "가입 방법", example = "인터넷,스마트폰,전화(텔레뱅킹)")
        private String joinWay;

        @ApiModelProperty(value = "만기 후 이자율 안내", example = "만기 후 1.5% 적용")
        private String mtrtInt;

        @ApiModelProperty(value = "기타 유의사항", example = "- 가입기간: 1~36개월, 중도해지 시 이자율 차감")
        private String etcNote;

        @ApiModelProperty(value = "특이사항", example = "해당사항 없음")
        private String spclCnd;

        @ApiModelProperty(value = "우대조건 태그", example = "[\"청년 우대\"]")
        private List<String> preferentialTags;

        @ApiModelProperty(value = "가입 대상", example = "실명의 개인")
        private String joinMember;

        @ApiModelProperty(value = "가입 불가 대상", example = "없음", required = false)
        private String joinDeny;

        @ApiModelProperty(value = "최소 가입 금액", example = "10000")
        private Integer minDepositAmount;

        @ApiModelProperty(value = "최대 가입 금액", example = "100000000")
        private Integer maxDepositAmount;

        @ApiModelProperty(value = "금리 옵션 리스트")
        private List<ProductOption> options;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ApiModel(description = "금리 옵션")
    public static class ProductOption {

        @ApiModelProperty(value = "단리 여부", example = "true")
        private Boolean singleRate;

        @ApiModelProperty(value = "복리 여부", example = "false")
        private Boolean compoundRate;

        @ApiModelProperty(value = "상품 ID", example = "1")
        private Long productId;

        @ApiModelProperty(value = "금융상품 코드", example = "WR0001B")
        private String finPrdtCd;

        @ApiModelProperty(value = "저축 기간(개월)", example = "12")
        private Integer saveTrm;

        @ApiModelProperty(value = "금리 유형 코드", example = "S")
        private String intrRateType;

        @ApiModelProperty(value = "금리 유형명", example = "단리")
        private String intrRateTypeNm;

        @ApiModelProperty(value = "기본 금리", example = "2.5")
        private Double intrRate;

        @ApiModelProperty(value = "최고 우대 금리", example = "2.5")
        private Double intrRate2;

        @ApiModelProperty(value = "적립 유형 코드", example = "null", required = false)
        private String rsrvType;

        @ApiModelProperty(value = "적립 유형명", example = "null", required = false)
        private String rsrvTypeNm;
    }
}
