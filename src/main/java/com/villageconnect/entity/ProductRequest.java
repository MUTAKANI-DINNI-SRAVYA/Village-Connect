package com.villageconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_requests")
public class ProductRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_id", referencedColumnName = "shop_id", nullable = false)
    private Shop shop;

    @NotBlank
    @Size(max = 100)
    @Column(name = "product_name")
    private String productName;

    @NotNull
    @Min(1)
    @Column(name = "quantity_requested")
    private Integer quantityRequested;

    @NotBlank
    @Size(max = 20)
    private String status; // PENDING, FULFILLED, CANCELLED

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    public ProductRequest() {
    }

    public ProductRequest(User user, Shop shop, String productName, Integer quantityRequested, String status) {
        this.user = user;
        this.shop = shop;
        this.productName = productName;
        this.quantityRequested = quantityRequested;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        requestDate = LocalDateTime.now();
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
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
