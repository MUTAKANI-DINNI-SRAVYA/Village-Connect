package com.villageconnect.controller;

import com.villageconnect.dto.ShopDto;
import com.villageconnect.entity.Shop;
import com.villageconnect.entity.User;
import com.villageconnect.service.ShopService;
import com.villageconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shops")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private UserService userService;

    // Convert Entity to DTO helper
    private ShopDto convertToDto(Shop shop) {
        return new ShopDto(
                shop.getShopId(),
                shop.getShopName(),
                shop.getOwnerName(),
                shop.getPhone(),
                shop.getVillage(),
                shop.getAddress(),
                shop.getCategory(),
                shop.getDescription(),
                shop.getLatitude(),
                shop.getLongitude(),
                shop.getOwner() != null ? shop.getOwner().getUserId() : null,
                shop.getImageUrl()
        );
    }

    // Get all shops (optionally search/filter)
    @GetMapping
    public ResponseEntity<List<ShopDto>> getShops(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String village) {
        
        List<Shop> shops;
        if (query != null && !query.trim().isEmpty()) {
            shops = shopService.searchShops(query);
        } else if (category != null && !category.trim().isEmpty()) {
            shops = shopService.filterByCategory(category);
        } else if (village != null && !village.trim().isEmpty()) {
            shops = shopService.filterByVillage(village);
        } else {
            shops = shopService.getAllShops();
        }

        List<ShopDto> shopDtos = shops.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(shopDtos);
    }

    // Get a specific shop
    @GetMapping("/{id}")
    public ResponseEntity<ShopDto> getShopById(@PathVariable Long id) {
        Shop shop = shopService.findById(id);
        return ResponseEntity.ok(convertToDto(shop));
    }

    // Get shops of current logged in owner
    @GetMapping("/owner")
    @PreAuthorize("hasRole('SHOP_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<List<ShopDto>> getOwnerShops(Principal principal) {
        User owner = userService.findByEmail(principal.getName());
        List<Shop> shops = shopService.getShopsByOwner(owner);
        List<ShopDto> shopDtos = shops.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(shopDtos);
    }

    // Create a new shop
    @PostMapping
    @PreAuthorize("hasRole('SHOP_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<?> createShop(@Valid @RequestBody ShopDto shopDto, Principal principal) {
        try {
            User owner = userService.findByEmail(principal.getName());
            Shop shop = shopService.createShop(shopDto, owner);
            return ResponseEntity.ok(convertToDto(shop));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Update a shop
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SHOP_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateShop(@PathVariable Long id, @Valid @RequestBody ShopDto shopDto, Principal principal) {
        try {
            Shop shop = shopService.findById(id);
            User currentUser = userService.findByEmail(principal.getName());
            
            // Check authorization: must be the shop owner or admin
            if (!currentUser.getRole().equals("ADMIN") && !shop.getOwner().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body("You are not authorized to edit this shop");
            }

            Shop updatedShop = shopService.updateShop(id, shopDto);
            return ResponseEntity.ok(convertToDto(updatedShop));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Delete a shop
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SHOP_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteShop(@PathVariable Long id, Principal principal) {
        try {
            Shop shop = shopService.findById(id);
            User currentUser = userService.findByEmail(principal.getName());
            
            // Check authorization
            if (!currentUser.getRole().equals("ADMIN") && !shop.getOwner().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body("You are not authorized to delete this shop");
            }

            shopService.deleteShop(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Shop deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
