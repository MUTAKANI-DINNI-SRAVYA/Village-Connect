package com.villageconnect.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ProductDto {
    private Long productId;
    
    @NotNull
    private Long shopId;
    
    private String shopName;
    
    @NotBlank
    private String productName;
    
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;
    
    @NotNull
    @Min(0)
    private Integer quantity;
    
    private String category;
    
    private String description;
    
    private String imageUrl;
    
    private String stockStatus; // Derived IN_STOCK / OUT_OF_STOCK

    public ProductDto() {}

    public ProductDto(Long productId, Long shopId, String shopName, String productName, BigDecimal price, Integer quantity, String category, String description, String imageUrl, String stockStatus) {
        this.productId = productId;
        this.shopId = shopId;
        this.shopName = shopName;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.description = description;
        this.imageUrl = imageUrl;
        this.stockStatus = stockStatus;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }
}
