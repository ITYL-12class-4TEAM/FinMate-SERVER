package org.scoula.products.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.scoula.products.dto.response.deposit.DepositOptionDTO;
import org.scoula.products.dto.response.deposit.DepositProductDTO;
import org.scoula.products.dto.response.pension.PensionOptionDTO;
import org.scoula.products.dto.response.pension.PensionProductDTO;

import java.util.List;
import java.util.Map;
@Mapper
// FinancialProductMapper.java
public interface FinancialProductMapper {
    @MapKey("product_id")
    List<Map<String, Object>> findProducts(
            @Param("productType") String productType,
            @Param("keyword") String keyword,
            @Param("minIntrRate") Double minIntrRate,
            @Param("saveTrm") Integer saveTrm,
            @Param("intrRateType") String intrRateType,
            @Param("joinWay") String joinWay);

    List<String> findProductNamesByKeyword(@Param("keyword") String keyword);
}



