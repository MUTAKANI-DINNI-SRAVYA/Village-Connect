package com.villageconnect.repository;

import com.villageconnect.entity.Shop;
import com.villageconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    
    List<Shop> findByOwner(User owner);
    
    List<Shop> findByCategoryIgnoreCase(String category);
    
    List<Shop> findByVillageContainingIgnoreCase(String village);

    @Query("SELECT s FROM Shop s WHERE " +
           "LOWER(s.shopName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.village) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Shop> searchShops(@Param("query") String query);
}
