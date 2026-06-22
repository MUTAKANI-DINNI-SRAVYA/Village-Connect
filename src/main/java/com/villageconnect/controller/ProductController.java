package com.villageconnect.controller;

import com.villageconnect.dto.ProductDto;
import com.villageconnect.entity.Product;
import com.villageconnect.entity.Shop;
import com.villageconnect.entity.User;
import com.villageconnect.service.AiService;
import com.villageconnect.service.ProductService;
import com.villageconnect.service.ShopService;
import com.villageconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private UserService userService;

    @Autowired
    private AiService aiService;

    // Convert Entity to DTO helper
    private ProductDto convertToDto(Product product) {
        return new ProductDto(
                product.getProductId(),
                product.getShop().getShopId(),
                product.getShop().getShopName(),
                product.getProductName(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory(),
                product.getDescription(),
                product.getImageUrl(),
                product.getStockStatus()
        );
    }

    // List and Search products with AI query-routing
    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category) {
        
        List<Product> products = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            String cleanQuery = query.trim();
            
            // Check if query is in Telugu
            if (aiService.containsTelugu(cleanQuery)) {
                // Translate Telugu query using Gemini
                String translatedKeywords = aiService.translateOrExpandQuery(cleanQuery, true);
                products = productService.searchProducts(translatedKeywords);
            } else {
                // English query: Search database directly first
                products = productService.searchProducts(cleanQuery);
                
                // Fallback: If SQL search yields no results, query Gemini for semantic keywords expansion
                if (products.isEmpty()) {
                    String expandedKeywords = aiService.translateOrExpandQuery(cleanQuery, false);
                    products = productService.searchProducts(expandedKeywords);
                }
            }
        } else if (category != null && !category.trim().isEmpty()) {
            products = productService.filterByCategory(category);
        } else {
            products = productService.getAllProducts();
        }

        List<ProductDto> dtos = products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(convertToDto(product));
    }

    // Get products of a specific shop
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<ProductDto>> getProductsByShop(@PathVariable Long shopId) {
        Shop shop = shopService.findById(shopId);
        List<Product> products = productService.getProductsByShop(shop);
        List<ProductDto> dtos = products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Add product to shop
    @PostMapping
    @PreAuthorize("hasRole('SHOP_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDto productDto, Principal principal) {
        try {
            Shop shop = shopService.findById(productDto.getShopId());
            User currentUser = userService.findByEmail(principal.getName());

            // Validate that the user is the owner of the shop or an Admin
            if (!currentUser.getRole().equals("ADMIN") && !shop.getOwner().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body("You are not authorized to add products to this shop");
            }

            Product product = productService.createProduct(productDto, shop);
            return ResponseEntity.ok(convertToDto(product));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Update product
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SHOP_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto productDto, Principal principal) {
        try {
            Product product = productService.findById(id);
            User currentUser = userService.findByEmail(principal.getName());

            // Validate ownership
            if (!currentUser.getRole().equals("ADMIN") && !product.getShop().getOwner().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body("You are not authorized to update this product");
            }

            Product updatedProduct = productService.updateProduct(id, productDto);
            return ResponseEntity.ok(convertToDto(updatedProduct));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Delete product
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SHOP_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, Principal principal) {
        try {
            Product product = productService.findById(id);
            User currentUser = userService.findByEmail(principal.getName());

            // Validate ownership
            if (!currentUser.getRole().equals("ADMIN") && !product.getShop().getOwner().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body("You are not authorized to delete this product");
            }

            productService.deleteProduct(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Product deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
