package com.villageconnect.service;

import com.villageconnect.dto.ProductDto;
import com.villageconnect.entity.Product;
import com.villageconnect.entity.Shop;
import com.villageconnect.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(ProductDto productDto, Shop shop) {
        Product product = new Product(
                shop,
                productDto.getProductName(),
                productDto.getPrice(),
                productDto.getQuantity(),
                productDto.getCategory(),
                productDto.getDescription(),
                productDto.getImageUrl()
        );
        return productRepository.save(product);
    }

    public Product updateProduct(Long productId, ProductDto productDto) {
        Product product = findById(productId);
        product.setProductName(productDto.getProductName());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        product.setCategory(productDto.getCategory());
        product.setDescription(productDto.getDescription());
        product.setImageUrl(productDto.getImageUrl());
        return productRepository.save(product);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByShop(Shop shop) {
        return productRepository.findByShop(shop);
    }

    public List<Product> filterByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category);
    }

    public List<Product> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.searchProducts(query);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
    }
}
