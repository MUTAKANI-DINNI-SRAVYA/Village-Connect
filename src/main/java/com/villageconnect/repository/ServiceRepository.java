package com.villageconnect.repository;

import com.villageconnect.entity.Service;
import com.villageconnect.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByShop(Shop shop);
}
