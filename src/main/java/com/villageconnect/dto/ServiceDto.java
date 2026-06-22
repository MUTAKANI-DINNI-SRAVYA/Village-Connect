package com.villageconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ServiceDto {
    private Long serviceId;
    
    @NotNull
    private Long shopId;
    
    private String shopName;
    
    @NotBlank
    private String serviceName;
    
    private String description;

    public ServiceDto() {}

    public ServiceDto(Long serviceId, Long shopId, String shopName, String serviceName, String description) {
        this.serviceId = serviceId;
        this.shopId = shopId;
        this.shopName = shopName;
        this.serviceName = serviceName;
        this.description = description;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
