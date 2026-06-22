package com.villageconnect.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ReviewDto {
    private Long reviewId;
    
    private Long userId;
    private String userName;
    
    @NotNull
    private Long shopId;
    private String shopName;
    
    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;
    
    private String comment;

    public ReviewDto() {}

    public ReviewDto(Long reviewId, Long userId, String userName, Long shopId, String shopName, Integer rating, String comment) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.userName = userName;
        this.shopId = shopId;
        this.shopName = shopName;
        this.rating = rating;
        this.comment = comment;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
