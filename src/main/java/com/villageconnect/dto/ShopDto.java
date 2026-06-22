package com.villageconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ShopDto {
    private Long shopId;
    
    @NotBlank
    private String shopName;
    
    @NotBlank
    private String ownerName;
    
    @NotBlank
    private String phone;
    
    @NotBlank
    private String village;
    
    @NotBlank
    private String address;
    
    @NotBlank
    private String category;
    
    private String description;
    
    @NotNull
    private Double latitude;
    
    @NotNull
    private Double longitude;
    
    private Long ownerId;
    private String imageUrl;

    public ShopDto() {}

    public ShopDto(Long shopId, String shopName, String ownerName, String phone, String village, String address, String category, String description, Double latitude, Double longitude, Long ownerId, String imageUrl) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.ownerName = ownerName;
        this.phone = phone;
        this.village = village;
        this.address = address;
        this.category = category;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ownerId = ownerId;
        this.imageUrl = imageUrl;
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

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
