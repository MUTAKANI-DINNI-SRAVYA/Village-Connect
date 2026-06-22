package com.villageconnect.service;

import com.villageconnect.dto.ProductRequestDto;
import com.villageconnect.entity.ProductRequest;
import com.villageconnect.entity.Shop;
import com.villageconnect.entity.User;
import com.villageconnect.repository.ProductRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductRequestService {

    @Autowired
    private ProductRequestRepository requestRepository;

    public ProductRequest createRequest(ProductRequestDto dto, User user, Shop shop) {
        ProductRequest request = new ProductRequest(
                user,
                shop,
                dto.getProductName(),
                dto.getQuantityRequested(),
                "PENDING"
        );
        return requestRepository.save(request);
    }

    public ProductRequest updateStatus(Long requestId, String status) {
        ProductRequest request = findById(requestId);
        request.setStatus(status.toUpperCase());
        return requestRepository.save(request);
    }

    public ProductRequest findById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product request not found with ID: " + id));
    }

    public List<ProductRequest> getAllRequests() {
        return requestRepository.findAll();
    }

    public List<ProductRequest> getRequestsByShop(Shop shop) {
        return requestRepository.findByShop(shop);
    }

    public List<ProductRequest> getRequestsByUser(User user) {
        return requestRepository.findByUser(user);
    }

    public void deleteRequest(Long id) {
        if (!requestRepository.existsById(id)) {
            throw new RuntimeException("Product request not found with ID: " + id);
        }
        requestRepository.deleteById(id);
    }
}
