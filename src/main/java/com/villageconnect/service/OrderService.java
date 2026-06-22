package com.villageconnect.service;

import com.villageconnect.dto.OrderDto;
import com.villageconnect.entity.Order;
import com.villageconnect.entity.Product;
import com.villageconnect.entity.Shop;
import com.villageconnect.entity.User;
import com.villageconnect.repository.OrderRepository;
import com.villageconnect.repository.ProductRepository;
import com.villageconnect.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Order placeOrder(OrderDto dto, User user) {
        Shop shop = shopRepository.findById(dto.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop not found with ID: " + dto.getShopId()));

        Product product = productRepository.findByShopAndProductName(shop, dto.getProductName())
                .orElseThrow(() -> new RuntimeException("Product '" + dto.getProductName() + "' not found in shop: " + shop.getShopName()));

        if (product.getQuantity() < dto.getQuantityOrdered()) {
            throw new RuntimeException("Insufficient stock for product '" + dto.getProductName() + "'. Available: " + product.getQuantity());
        }

        // Deduct inventory
        product.setQuantity(product.getQuantity() - dto.getQuantityOrdered());
        productRepository.save(product);

        BigDecimal total = product.getPrice().multiply(new BigDecimal(dto.getQuantityOrdered()));

        Order order = new Order(
                user,
                shop,
                dto.getProductName(),
                dto.getQuantityOrdered(),
                product.getPrice(),
                total,
                dto.getPaymentMethod(),
                "PENDING",
                LocalDateTime.now(),
                dto.getCustomerName() != null ? dto.getCustomerName() : user.getName(),
                dto.getCustomerPhone() != null ? dto.getCustomerPhone() : user.getPhone(),
                dto.getDeliveryAddress()
        );

        return orderRepository.save(order);
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    public List<Order> getOrdersByShop(Shop shop) {
        return orderRepository.findByShopOrderByOrderDateDesc(shop);
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = findById(orderId);
        String oldStatus = order.getStatus();
        String newStatus = status.toUpperCase();

        if (oldStatus.equals(newStatus)) {
            return order;
        }

        // If order gets cancelled, restore the inventory
        if (newStatus.equals("CANCELLED") && !oldStatus.equals("CANCELLED")) {
            try {
                Product product = productRepository.findByShopAndProductName(order.getShop(), order.getProductName())
                        .orElse(null);
                if (product != null) {
                    product.setQuantity(product.getQuantity() + order.getQuantityOrdered());
                    productRepository.save(product);
                }
            } catch (Exception e) {
                // Log and continue
                System.err.println("Failed to restore inventory on cancel: " + e.getMessage());
            }
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
}
