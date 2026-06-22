package com.villageconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_id", referencedColumnName = "shop_id", nullable = false)
    private Shop shop;

    @NotBlank
    @Size(max = 100)
    @Column(name = "product_name")
    private String productName;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;

    @NotNull
    @Min(0)
    private Integer quantity;

    @Size(max = 50)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 255)
    @Column(name = "image_url")
    private String imageUrl;

    public Product() {
    }

    public Product(Shop shop, String productName, BigDecimal price, Integer quantity, String category, String description, String imageUrl) {
        this.shop = shop;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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

    // Dynamic stock status helper for frontend integration
    @Transient
    public String getStockStatus() {
        return (this.quantity != null && this.quantity > 0) ? "IN_STOCK" : "OUT_OF_STOCK";
    }
}
