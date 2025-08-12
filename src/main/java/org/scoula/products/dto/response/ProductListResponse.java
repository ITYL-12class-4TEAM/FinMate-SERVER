package org.scoula.products.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "금융상품 목록 조회 응답 DTO")
public class ProductListResponse {

    @ApiModelProperty(value = "상품 유형 (호환성 유지용)", example = "deposit")
    private String productType;

    @ApiModelProperty(value = "조회된 상품의 카테고리 ID", example = "1")
    private Long categoryId;

    @ApiModelProperty(value = "조회된 상품의 하위 카테고리 ID", example = "101")
    private Long subcategoryId;

    @ApiModelProperty(value = "예금/적금 상품 목록")
    private List<ProductSummary> products;

    @ApiModelProperty(value = "연금 상품 목록")
    private List<PensionProductSummary> pensionProducts;

    @ApiModelProperty(value = "전체 검색 결과 수", example = "150", required = true)
    private int totalCount;

    @ApiModelProperty(value = "현재 페이지 번호", example = "1", required = true)
    private int currentPage;

    @ApiModelProperty(value = "페이지 당 항목 수", example = "10", required = true)
    private int pageSize;

    @ApiModelProperty(value = "전체 페이지 수", example = "15", required = true)
    private int totalPages;

    @ApiModelProperty(value = "정렬 기준", example = "intrRate")
    private String sortBy;

    @ApiModelProperty(value = "정렬 방향", example = "desc")
    private String sortDirection;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "예금/적금 상품 요약 정보")
    public static class ProductSummary {
        @ApiModelProperty(value = "상품 고유 ID", example = "1", required = true)
        private Long productId;

        @ApiModelProperty(value = "금융회사 코드", example = "0010001")
        private String finCoNo;

        @ApiModelProperty(value = "금융상품 코드", example = "WR0001B", required = true)
        private String finPrdtCd;

        @ApiModelProperty(value = "금융회사명", example = "우리은행")
        private String korCoNm;

        @ApiModelProperty(value = "금융상품명", example = "우리 SUPER 정기예금")
        private String finPrdtNm;

        @ApiModelProperty(value = "기본 이자율 (저축 금리)", example = "3.5")
        private Double intrRate;

        @ApiModelProperty(value = "최고 우대금리", example = "3.7")
        private Double intrRate2;

        @ApiModelProperty(value = "저축 기간 (개월)", example = "12")
        private Integer saveTrm;

        @ApiModelProperty(value = "가입 방법", example = "인터넷,스마트폰")
        private String joinWay;

        @ApiModelProperty(value = "이자율 유형 (S: 단리, M: 복리)", example = "S")
        private String intrRateType;

        @ApiModelProperty(value = "최소 예치 금액", example = "100000")
        private Long minDepositAmount;

        @ApiModelProperty(value = "최대 예치 금액", example = "50000000")
        private Long maxDepositAmount;

        @ApiModelProperty(value = "적립식 유형 (R: 정기적금, F: 자유적금)", example = "R")
        private String rsrvType; // 적립식 유형 (R: 정기적금, F: 자유적금)
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "연금 상품 요약 정보")
    public static class PensionProductSummary {
        @ApiModelProperty(value = "금융상품 코드", example = "10-025371-04", required = true)
        private String finPrdtCd;

        @ApiModelProperty(value = "금융회사명", example = "삼성생명보험")
        private String korCoNm;

        @ApiModelProperty(value = "금융상품명", example = "삼성 연금저축보험 골드연금")
        private String finPrdtNm;

        @ApiModelProperty(value = "공시이율 (%)", example = "2.5")
        private Double dclsRate;

        @ApiModelProperty(value = "최저보증이율 (%)", example = "1.0")
        private Double guarRate;

        @ApiModelProperty(value = "연금 종류 코드", example = "F")
        private String pnsnKind;

        @ApiModelProperty(value = "연금 종류명", example = "연금저축")
        private String pnsnKindNm;

        @ApiModelProperty(value = "상품 유형 코드", example = "I")
        private String prdtType;

        @ApiModelProperty(value = "상품 유형명", example = "보험")
        private String prdtTypeNm;
    }
}