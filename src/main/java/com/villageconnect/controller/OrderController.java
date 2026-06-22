package com.villageconnect.controller;

import com.villageconnect.dto.OrderDto;
import com.villageconnect.entity.Order;
import com.villageconnect.entity.Shop;
import com.villageconnect.entity.User;
import com.villageconnect.service.OrderService;
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
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private UserService userService;

    private OrderDto convertToDto(Order order) {
        return new OrderDto(
                order.getOrderId(),
                order.getUser().getUserId(),
                order.getUser().getName(),
                order.getUser().getPhone(),
                order.getShop().getShopId(),
                order.getShop().getShopName(),
                order.getProductName(),
                order.getQuantityOrdered(),
                order.getPrice(),
                order.getTotalAmount(),
                order.getPaymentMethod(),
                order.getStatus(),
                order.getOrderDate(),
                order.getCustomerName(),
                order.getCustomerPhone(),
                order.getDeliveryAddress()
        );
    }

    // Place a new order
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> placeOrder(@Valid @RequestBody OrderDto dto, Principal principal) {
        try {
            User user = userService.findByEmail(principal.getName());
            Order order = orderService.placeOrder(dto, user);
            return ResponseEntity.ok(convertToDto(order));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // View orders placed by current user
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderDto>> getUserOrders(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<Order> orders = orderService.getOrdersByUser(user);
        List<OrderDto> dtos = orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // View orders submitted to a specific shop (for shop owner)
    @GetMapping("/shop/{shopId}")
    @PreAuthorize("hasRole('SHOP_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<?> getShopOrders(@PathVariable Long shopId, Principal principal) {
        try {
            Shop shop = shopService.findById(shopId);
            User currentUser = userService.findByEmail(principal.getName());

            // Validate that the user is the owner of the shop or an Admin
            if (!currentUser.getRole().equals("ADMIN") && !shop.getOwner().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body("You are not authorized to view orders for this shop");
            }

            List<Order> orders = orderService.getOrdersByShop(shop);
            List<OrderDto> dtos = orders.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Update status of an order
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('SHOP_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status,
            Principal principal) {
        try {
            Order order = orderService.findById(orderId);
            User currentUser = userService.findByEmail(principal.getName());

            // Validate ownership of shop
            if (!currentUser.getRole().equals("ADMIN") && !order.getShop().getOwner().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body("You are not authorized to update orders for this shop");
            }

            Order updated = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(convertToDto(updated));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
