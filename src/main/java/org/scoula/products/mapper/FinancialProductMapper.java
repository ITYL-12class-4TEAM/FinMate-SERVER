package org.scoula.products.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.products.dto.response.ProductDTO;

import java.util.List;
import java.util.Map;

@Mapper
public interface FinancialProductMapper {

    /**
     * 필터 조건에 맞는 금융 상품 목록 조회
     */
    List<ProductDTO> findProducts(
            @Param("keyword") String keyword,
            @Param("categoryName") String categoryName,
            @Param("categoryId") Long categoryId,
            @Param("subcategoryId") Long subcategoryId,
            @Param("searchText") String searchText,
            @Param("minIntrRate") Double minIntrRate,
            @Param("saveTrm") Integer saveTrm,
            @Param("intrRateType") String intrRateType,
            @Param("joinWays") String joinWays,  // 파라미터명 변경: joinWay -> joinWays
            @Param("depositAmount") Long depositAmount,
            @Param("sortBy") String sortBy,
            @Param("sortDirection") String sortDirection,
            @Param("pageSize") Integer pageSize,
            @Param("offset") Integer offset,
            @Param("banksStr") String banksStr,
            @Param("rsrvType") String rsrvType);  // 적립식 유형 추가


    /**
     * 필터 조건에 맞는 금융 상품 총 개수 조회
     */
    int countProducts(
            @Param("categoryName") String categoryName,
            @Param("CategoryId") Long categoryId,
            @Param("subCategoryId") Long subCategoryId,
            @Param("searchText") String searchText,
            @Param("minIntrRate") Double minIntrRate,
            @Param("saveTrm") Integer saveTrm,
            @Param("intrRateType") String intrRateType,
            @Param("joinWays") String joinWays,  // 파라미터명 변경: joinWay -> joinWays
            @Param("amount") Integer amount,
            @Param("banksStr") String banksStr,
            @Param("rsrvType") String rsrvType);

    /**
     * 검색어에 맞는 상품명 자동완성 목록 조회
     */
    List<String> findProductNamesByKeyword(@Param("keyword") String keyword);

    /**
     * 상품 ID로 상세 정보 조회
     */
    @MapKey("product_id")
    Map<String, Object> findProductDetail(@Param("productId") String productId);

    /**
     * 고유 은행명 목록 조회
     */
    List<String> getDistinctBanks(@Param("categoryId") Long categoryId);

    /**
     * 카테고리 ID로 서브카테고리 목록 조회
     */
    @MapKey("subcategory_id")
    List<Map<String, Object>> getSubcategoriesByCategoryId(@Param("categoryId") Long categoryId);

}