package org.scoula.chatgpt.dto.portfolio;

public class ProductInfoRequest {
    private String productName;
    private String companyName;
    private String category;        // "예금", "적금", "보험", "연금", "주식", "기타"
    private String subcategory;     // subcategoryMapping에 따른 세부 분류

    // 기본 생성자
    public ProductInfoRequest() {}

    // 전체 매개변수 생성자
    public ProductInfoRequest(String productName, String companyName, String category, String subcategory) {
        this.productName = productName;
        this.companyName = companyName;
        this.category = category;
        this.subcategory = subcategory;
    }

    // getter/setter
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSubcategory() { return subcategory; }
    public void setSubcategory(String subcategory) { this.subcategory = subcategory; }
}