package com.villageconnect.controller;

import com.villageconnect.dto.ReviewDto;
import com.villageconnect.entity.Review;
import com.villageconnect.entity.Shop;
import com.villageconnect.entity.User;
import com.villageconnect.service.ReviewService;
import com.villageconnect.service.ShopService;
import com.villageconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private UserService userService;

    private ReviewDto convertToDto(Review review) {
        return new ReviewDto(
                review.getReviewId(),
                review.getUser().getUserId(),
                review.getUser().getName(),
                review.getShop().getShopId(),
                review.getShop().getShopName(),
                review.getRating(),
                review.getComment()
        );
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByShop(@PathVariable Long shopId) {
        Shop shop = shopService.findById(shopId);
        List<Review> reviews = reviewService.getReviewsByShop(shop);
        List<ReviewDto> dtos = reviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/shop/{shopId}/average")
    public ResponseEntity<?> getAverageRating(@PathVariable Long shopId) {
        Shop shop = shopService.findById(shopId);
        double avg = reviewService.getAverageRatingForShop(shop);
        Map<String, Object> response = new HashMap<>();
        response.put("shopId", shopId);
        response.put("averageRating", avg);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReviewDto>> getReviewsByUser(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<Review> reviews = reviewService.getReviewsByUser(user);
        List<ReviewDto> dtos = reviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewDto reviewDto, Principal principal) {
        try {
            User user = userService.findByEmail(principal.getName());
            Shop shop = shopService.findById(reviewDto.getShopId());
            Review review = reviewService.createReview(reviewDto, user, shop);
            return ResponseEntity.ok(convertToDto(review));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteReview(@PathVariable Long id, Principal principal) {
        try {
            Review review = reviewService.findById(id);
            User currentUser = userService.findByEmail(principal.getName());

            // Check if reviewer is deleting, or admin
            if (!currentUser.getRole().equals("ADMIN") && !review.getUser().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body("You are not authorized to delete this review");
            }

            reviewService.deleteReview(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Review deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
