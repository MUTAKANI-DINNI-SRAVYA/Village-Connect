package com.villageconnect.repository;

import com.villageconnect.entity.ProductRequest;
import com.villageconnect.entity.Shop;
import com.villageconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRequestRepository extends JpaRepository<ProductRequest, Long> {
    List<ProductRequest> findByShop(Shop shop);
    List<ProductRequest> findByUser(User user);
}
