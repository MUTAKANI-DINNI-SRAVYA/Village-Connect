package com.villageconnect.service;

import com.villageconnect.dto.ReviewDto;
import com.villageconnect.entity.Review;
import com.villageconnect.entity.Shop;
import com.villageconnect.entity.User;
import com.villageconnect.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Review createReview(ReviewDto reviewDto, User user, Shop shop) {
        Review review = new Review(
                user,
                shop,
                reviewDto.getRating(),
                reviewDto.getComment()
        );
        return reviewRepository.save(review);
    }

    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + id));
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> getReviewsByShop(Shop shop) {
        return reviewRepository.findByShop(shop);
    }

    public List<Review> getReviewsByUser(User user) {
        return reviewRepository.findByUser(user);
    }

    public double getAverageRatingForShop(Shop shop) {
        List<Review> reviews = getReviewsByShop(shop);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (Review review : reviews) {
            sum += review.getRating();
        }
        return sum / reviews.size();
    }

    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("Review not found with ID: " + id);
        }
        reviewRepository.deleteById(id);
    }
}
