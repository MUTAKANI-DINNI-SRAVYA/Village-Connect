package com.villageconnect.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ProductRequestDto {
    private Long requestId;
    
    private Long userId;
    private String userName;
    private String userPhone;
    
    @NotNull
    private Long shopId;
    private String shopName;
    
    @NotBlank
    private String productName;
    
    @NotNull
    @Min(1)
    private Integer quantityRequested;
    
    private String status; // PENDING, FULFILLED, CANCELLED
    private LocalDateTime requestDate;

    public ProductRequestDto() {}

    public ProductRequestDto(Long requestId, Long userId, String userName, String userPhone, Long shopId, String shopName, String productName, Integer quantityRequested, String status, LocalDateTime requestDate) {
        this.requestId = requestId;
        this.userId = userId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.shopId = shopId;
        this.shopName = shopName;
        this.productName = productName;
        this.quantityRequested = quantityRequested;
        this.status = status;
        this.requestDate = requestDate;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
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

    public Integer getQuantityRequested() {
        return quantityRequested;
    }

    public void setQuantityRequested(Integer quantityRequested) {
        this.quantityRequested = quantityRequested;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }
}
