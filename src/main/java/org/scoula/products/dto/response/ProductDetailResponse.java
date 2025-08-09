package org.scoula.products.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.products.dto.response.deposit.DepositProductDTO;
import org.scoula.products.dto.response.pension.PensionProductDTO;
import org.scoula.products.dto.response.saving.SavingProductDTO;

/**
 * 금융 상품 상세 정보 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {

    private Long productId;

    // 상품 유형 (deposit: 예금, saving: 적금, pension: 연금저축)
    private String productType;

    // 상품 상세 정보 (객체 참조)
    private Object productDetail;

    // 금융회사 코드
    private String finCoNo;

    // 금융회사명
    private String korCoNm;

    // 금융상품 코드
    private String finPrdtCd;

    // 금융 상품명
    private String finPrdtNm;

    // 가입 방법
    private String joinWay;

    // 우대 조건
    private String spclCnd;

    // 우대 조건 내용 파싱한 태그
    private String preferentialTags;

    // 가입 대상
    private String joinMember;

    // 최고 금리
    private Double maxIntrRate;

    /**
     * 예금, 적금 상품 정보로 응답 생성
     */
    public ProductDetailResponse(DepositProductDTO deposit, String productType) {
        this.productType = productType;
        this.productDetail = deposit;
        this.productId = deposit.getProductId();
        this.finCoNo = deposit.getFinCoNo();
        this.korCoNm = deposit.getKorCoNm();
        this.finPrdtCd = deposit.getFinPrdtCd();
        this.finPrdtNm = deposit.getFinPrdtNm();
        this.joinWay = deposit.getJoinWay();
        this.spclCnd = deposit.getSpclCnd();
        this.preferentialTags = deposit.getPreferentialTags();
        this.joinMember = deposit.getJoinMember();

        // 최고 금리 계산 (옵션 중 가장 높은 금리)
        this.maxIntrRate = deposit.getOptions().stream()
                .mapToDouble(option ->
                        option.getIntrRate2() != null && option.getIntrRate2() > 0
                                ? option.getIntrRate2()
                                : option.getIntrRate())
                .max()
                .orElse(0.0);
    }

    /**
     * 적금 상품 정보로 응답 생성
     */
    public ProductDetailResponse(SavingProductDTO saving) {
        this.productType = "saving";
        this.productDetail = saving;
        this.finCoNo = saving.getFinCoNo();
        this.korCoNm = saving.getKorCoNm();
        this.finPrdtCd = saving.getFinPrdtCd();
        this.finPrdtNm = saving.getFinPrdtNm();
        this.joinWay = saving.getJoinWay();
        this.spclCnd = saving.getSpclCnd();
        this.joinMember = saving.getJoinMember();

        // 최고 금리 계산 (옵션 중 가장 높은 금리)
        this.maxIntrRate = saving.getOptions().stream()
                .mapToDouble(option ->
                        option.getIntrRate2() != null && option.getIntrRate2() > 0
                                ? option.getIntrRate2()
                                : option.getIntrRate())
                .max()
                .orElse(0.0);
    }

    /**
     * 연금 상품 정보로 응답 생성
     */
    public ProductDetailResponse(PensionProductDTO pension, String productType) {
        this.productType = "pension";
        this.productDetail = pension;
        this.finCoNo = pension.getFinCoNo();
        this.korCoNm = pension.getKorCoNm();
        this.finPrdtCd = pension.getFinPrdtCd();
        this.finPrdtNm = pension.getFinPrdtNm();
        this.joinWay = pension.getJoinWay();
        this.joinMember = ""; // 연금 상품은 이 필드가 없을 수 있음
        this.spclCnd = ""; // 연금 상품은 이 필드가 없을 수 있음

        // 연금 수령액 기준 계산
        if (pension.getOptions() != null && !pension.getOptions().isEmpty()) {
            // 연금 수령액 기준으로 가장 높은 값 사용
            this.maxIntrRate = pension.getOptions().stream()
                    .mapToDouble(option -> option.getPnsnRecpAmt() / 1000000.0) // 백만 원당 비율 예시
                    .max()
                    .orElse(0.0);
        } else {
            this.maxIntrRate = 0.0;
        }
    }

    /**
     * 상품 상세 정보에서 금리 옵션 개수 조회
     */
    public int getOptionCount() {
        if (productDetail instanceof DepositProductDTO) {
            return ((DepositProductDTO) productDetail).getOptions().size();
        } else if (productDetail instanceof SavingProductDTO) {
            return ((SavingProductDTO) productDetail).getOptions().size();
        } else if (productDetail instanceof PensionProductDTO) {
            return ((PensionProductDTO) productDetail).getOptions().size();
        }
        return 0;
    }

    /**
     * 상품의 판매 상태 확인 (공시 종료일 기준)
     */
    public boolean isAvailable() {
        if (productDetail instanceof DepositProductDTO) {
            return ((DepositProductDTO) productDetail).isAvailable();
        } else if (productDetail instanceof SavingProductDTO) {
            return ((SavingProductDTO) productDetail).isAvailable();
        } else if (productDetail instanceof PensionProductDTO) {
            return ((PensionProductDTO) productDetail).isAvailable();
        }
        return false;
    }
}