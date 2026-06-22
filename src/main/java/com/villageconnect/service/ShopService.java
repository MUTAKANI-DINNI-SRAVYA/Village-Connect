package com.villageconnect.service;

import com.villageconnect.dto.ShopDto;
import com.villageconnect.entity.Shop;
import com.villageconnect.entity.User;
import com.villageconnect.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopService {

    @Autowired
    private ShopRepository shopRepository;

    public Shop createShop(ShopDto shopDto, User owner) {
        Shop shop = new Shop(
                shopDto.getShopName(),
                shopDto.getOwnerName(),
                shopDto.getPhone(),
                shopDto.getVillage(),
                shopDto.getAddress(),
                shopDto.getCategory(),
                shopDto.getDescription(),
                shopDto.getLatitude(),
                shopDto.getLongitude(),
                owner
        );
        return shopRepository.save(shop);
    }

    public Shop updateShop(Long shopId, ShopDto shopDto) {
        Shop shop = findById(shopId);
        shop.setShopName(shopDto.getShopName());
        shop.setOwnerName(shopDto.getOwnerName());
        shop.setPhone(shopDto.getPhone());
        shop.setVillage(shopDto.getVillage());
        shop.setAddress(shopDto.getAddress());
        shop.setCategory(shopDto.getCategory());
        shop.setDescription(shopDto.getDescription());
        shop.setLatitude(shopDto.getLatitude());
        shop.setLongitude(shopDto.getLongitude());
        return shopRepository.save(shop);
    }

    public Shop findById(Long id) {
        return shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found with ID: " + id));
    }

    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    public List<Shop> getShopsByOwner(User owner) {
        return shopRepository.findByOwner(owner);
    }

    public List<Shop> filterByCategory(String category) {
        return shopRepository.findByCategoryIgnoreCase(category);
    }

    public List<Shop> filterByVillage(String village) {
        return shopRepository.findByVillageContainingIgnoreCase(village);
    }

    public List<Shop> searchShops(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllShops();
        }
        return shopRepository.searchShops(query);
    }

    public void deleteShop(Long id) {
        if (!shopRepository.existsById(id)) {
            throw new RuntimeException("Shop not found with ID: " + id);
        }
        shopRepository.deleteById(id);
    }
}
