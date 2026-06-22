package com.villageconnect.controller;

import com.villageconnect.dto.ProductRequestDto;
import com.villageconnect.entity.ProductRequest;
import com.villageconnect.entity.Shop;
import com.villageconnect.entity.User;
import com.villageconnect.service.ProductRequestService;
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
@RequestMapping("/api/requests")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductRequestController {

    @Autowired
    private ProductRequestService requestService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private UserService userService;

    private ProductRequestDto convertToDto(ProductRequest req) {
        return new ProductRequestDto(
                req.getRequestId(),
                req.getUser().getUserId(),
                req.getUser().getName(),
                req.getUser().getPhone(),
                req.getShop().getShopId(),
                req.getShop().getShopName(),
                req.getProductName(),
                req.getQuantityRequested(),
                req.getStatus(),
                req.getRequestDate()
        );
    }

    // Submit a request for a product
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> submitRequest(@Valid @RequestBody ProductRequestDto dto, Principal principal) {
        try {
            User user = userService.findByEmail(principal.getName());
            Shop shop = shopService.findById(dto.getShopId());
            ProductRequest request = requestService.createRequest(dto, user, shop);
            return ResponseEntity.ok(convertToDto(request));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // View requests submitted by current user
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductRequestDto>> getUserRequests(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<ProductRequest> requests = requestService.getRequestsByUser(user);
        List<ProductRequestDto> dtos = requests.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // View requests submitted to a specific shop (for shop owner)
    @GetMapping("/shop/{shopId}")
    @PreAuthorize("hasRole('SHOP_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<?> getShopRequests(@PathVariable Long shopId, Principal principal) {
        try {
            Shop shop = shopService.findById(shopId);
            User currentUser = userService.findByEmail(principal.getName());

            // Validate that the user is the owner of the shop or an Admin
            if (!currentUser.getRole().equals("ADMIN") && !shop.getOwner().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body("You are not authorized to view requests for this shop");
            }

            List<ProductRequest> requests = requestService.getRequestsByShop(shop);
            List<ProductRequestDto> dtos = requests.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Update status of a product request
    @PutMapping("/{requestId}/status")
    @PreAuthorize("hasRole('SHOP_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateRequestStatus(
            @PathVariable Long requestId,
            @RequestParam String status,
            Principal principal) {
        try {
            ProductRequest request = requestService.findById(requestId);
            User currentUser = userService.findByEmail(principal.getName());

            // Validate ownership of shop
            if (!currentUser.getRole().equals("ADMIN") && !request.getShop().getOwner().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body("You are not authorized to update requests for this shop");
            }

            ProductRequest updated = requestService.updateStatus(requestId, status);
            return ResponseEntity.ok(convertToDto(updated));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
