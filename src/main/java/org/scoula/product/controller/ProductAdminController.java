package org.scoula.product.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.scoula.product.scheduler.DepositProductScheduler;
import org.scoula.product.scheduler.PensionProductScheduler;
import org.scoula.product.scheduler.SavingProductScheduler;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "상품fetch API")
@RestController
@RequestMapping("/api/admin")
public class ProductAdminController {

    @Autowired
    private DepositProductScheduler depositProductScheduler;
    @Autowired
    private PensionProductScheduler pensionProductScheduler;
    @Autowired
    private SavingProductScheduler savingProductScheduler;

    @ApiOperation(value = "예금 상품 수동 수집 트리거", notes = "예금 상품을 수동으로 수집하는 API입니다.")
    @GetMapping("/deposit-product/fetch")
    public ApiResponse<?> depositTriggerFetch() {
        depositProductScheduler.fetchDepositProductsManually();
        return ApiResponse.success(ResponseCode.DEPOSIT_PRODUCT_FETCH_SUCCESS);
    }

    @ApiOperation(value = "연금 상품 수동 수집 트리거", notes = "연금 상품을 수동으로 수집하는 API입니다.")
    @GetMapping("/pension-product/fetch")
    public ApiResponse<?> pensionTriggerFetch() {
        pensionProductScheduler.fetchPensiontProductsManually();
        return ApiResponse.success(ResponseCode.PENSION_PRODUCT_FETCH_SUCCESS);
    }

    @ApiOperation(value = "적금 상품 수동 수집 트리거", notes = "적금 상품을 수동으로 수집하는 API입니다.")
    @GetMapping("/saving-product/fetch")
    public ApiResponse<?> savingTriggerFetch() {
        savingProductScheduler.fetchSavingProducttProductsManually();
        return ApiResponse.success(ResponseCode.SAVING_PRODUCT_FETCH_SUCCESS);
    }
}
