package com.villageconnect.controller;

import com.villageconnect.dto.ChatRequest;
import com.villageconnect.dto.ChatResponse;
import com.villageconnect.entity.Product;
import com.villageconnect.entity.User;
import com.villageconnect.service.AiService;
import com.villageconnect.service.ProductService;
import com.villageconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AiController {

    @Autowired
    private AiService aiService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    // AI Chatbot endpoint
    @PostMapping("/chat")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest chatRequest) {
        String reply = aiService.chatDiscover(chatRequest.getMessage());
        return ResponseEntity.ok(new ChatResponse(reply));
    }

    // AI Product Recommendations endpoint
    @GetMapping("/recommendations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getRecommendations(Principal principal) {
        try {
            User user = userService.findByEmail(principal.getName());
            List<Product> availableProducts = productService.getAllProducts();
            String htmlRecommendations = aiService.getProductRecommendations(user, availableProducts);

            Map<String, String> response = new HashMap<>();
            response.put("recommendations", htmlRecommendations);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
