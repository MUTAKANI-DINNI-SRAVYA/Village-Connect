package com.villageconnect.config;

import com.villageconnect.entity.*;
import com.villageconnect.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            logger.info("Database already contains data. Skipping default data seeding.");
            return;
        }

        logger.info("Database is empty. Seeding initial VillageConnect demo data...");

        // 1. Seed Users
        User admin = new User(
                "System Administrator",
                "admin@villageconnect.com",
                "9000000001",
                passwordEncoder.encode("admin123"),
                "ADMIN",
                "Kanakalagunta"
        );
        userRepository.save(admin);

        User owner1 = new User(
                "Srinivasa Rao",
                "owner@villageconnect.com",
                "9848012345",
                passwordEncoder.encode("owner123"),
                "SHOP_OWNER",
                "Ramapuram"
        );
        userRepository.save(owner1);

        User owner2 = new User(
                "Venkata Reddy",
                "owner2@villageconnect.com",
                "9848098765",
                passwordEncoder.encode("owner123"),
                "SHOP_OWNER",
                "Kanakalagunta"
        );
        userRepository.save(owner2);

        User customer = new User(
                "Ramesh Kumar",
                "customer@villageconnect.com",
                "9988776655",
                passwordEncoder.encode("customer123"),
                "CUSTOMER",
                "Ramapuram"
        );
        userRepository.save(customer);

        // 2. Seed Shops
        Shop shop1 = new Shop(
                "Srinivasa Kirana Store",
                "Srinivasa Rao",
                "9848012345",
                "Ramapuram",
                "Main Road, Near Panchayat Office, Ramapuram Village",
                "Groceries",
                "Fresh daily staples, premium rice, pulses, cooking oils, and dairy products.",
                16.5061,
                80.6482,
                owner1
        );
        shop1.setImageUrl("https://images.unsplash.com/photo-1542838132-92c53300491e?w=800");
        shopRepository.save(shop1);

        Shop shop2 = new Shop(
                "Kisan Agro & Seeds",
                "Venkata Reddy",
                "9848098765",
                "Kanakalagunta",
                "Near Bus Stand, Kanakalagunta Village",
                "Agriculture",
                "High quality crop seeds, bio fertilizers, organic pesticides, and farming tools.",
                16.5120,
                80.6550,
                owner2
        );
        shop2.setImageUrl("https://images.unsplash.com/photo-1586771107445-d3ca888129ff?w=800");
        shopRepository.save(shop2);

        Shop shop3 = new Shop(
                "Sri Lakshmi Medical & General Store",
                "Venkata Reddy",
                "9848098766",
                "Kanakalagunta",
                "Temple Street, Kanakalagunta Village",
                "Medical",
                "Essential medicines, first-aid kits, wellness products, and emergency supplies.",
                16.5135,
                80.6565,
                owner2
        );
        shop3.setImageUrl("https://images.unsplash.com/photo-1576602976047-174e57a47881?w=800");
        shopRepository.save(shop3);

        Shop shop4 = new Shop(
                "Balaji Electricals & Hardware",
                "Srinivasa Rao",
                "9848012346",
                "Ramapuram",
                "Opposite Water Tank, Ramapuram Village",
                "Electrical",
                "Wiring cables, LED lights, switches, water motor pumps, and repair accessories.",
                16.5075,
                80.6495,
                owner1
        );
        shop4.setImageUrl("https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=800");
        shopRepository.save(shop4);

        // 3. Seed Products
        // Shop 1 - Groceries
        productRepository.save(new Product(
                shop1,
                "Sona Masoori Rice (25kg)",
                new BigDecimal("1350.00"),
                20,
                "Groceries",
                "Fine grain, aged polished Sona Masoori rice bag.",
                "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500"
        ));
        productRepository.save(new Product(
                shop1,
                "Organic Toor Dal (1kg)",
                new BigDecimal("160.00"),
                35,
                "Groceries",
                "Unpolished farm-fresh protein rich toor dal.",
                "https://images.unsplash.com/photo-1585994192701-f1a505c817ee?w=500"
        ));
        productRepository.save(new Product(
                shop1,
                "Fresh Cow Milk (1L Packet)",
                new BigDecimal("60.00"),
                50,
                "Dairy",
                "Freshly sourced cow milk from local village farmers.",
                "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=500"
        ));

        // Shop 2 - Agriculture
        productRepository.save(new Product(
                shop2,
                "Organic Neem Fertilizer (5kg)",
                new BigDecimal("350.00"),
                30,
                "Fertilizers",
                "Pure neem cake organic fertilizer for crops, vegetables, and pest resistance.",
                "https://images.unsplash.com/photo-1585314062340-f1a5a7c9328d?w=500"
        ));
        productRepository.save(new Product(
                shop2,
                "Hybrid Tomato Seeds (100g)",
                new BigDecimal("180.00"),
                45,
                "Seeds",
                "High-yield disease-tolerant hybrid tomato vegetable seeds.",
                "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=500"
        ));
        productRepository.save(new Product(
                shop2,
                "Bio Insecticide Spray (500ml)",
                new BigDecimal("280.00"),
                15,
                "Pesticides",
                "Eco-friendly crop protection spray against whiteflies and caterpillars.",
                "https://images.unsplash.com/photo-1628352081506-83c43123ed6d?w=500"
        ));

        // Shop 3 - Medical
        productRepository.save(new Product(
                shop3,
                "Paracetamol Tablets 650mg (Strip of 15)",
                new BigDecimal("35.00"),
                100,
                "Medical",
                "Fast relief from fever and body aches.",
                "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=500"
        ));
        productRepository.save(new Product(
                shop3,
                "Antiseptic Liquid (100ml)",
                new BigDecimal("65.00"),
                25,
                "Medical",
                "First aid disinfectant liquid for cuts, wounds, and insect bites.",
                "https://images.unsplash.com/photo-1607613009820-a29f7bb81c04?w=500"
        ));

        // Shop 4 - Electrical
        productRepository.save(new Product(
                shop4,
                "LED Bulb 9W (Warm White)",
                new BigDecimal("90.00"),
                60,
                "Electrical",
                "Energy saving 9W B22 LED bulb with 1-year warranty.",
                "https://images.unsplash.com/photo-1550524514-9b578c783610?w=500"
        ));
        productRepository.save(new Product(
                shop4,
                "Modular Fan Regulator",
                new BigDecimal("140.00"),
                20,
                "Electrical",
                "Smooth step-type hum-free fan regulator.",
                "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=500"
        ));

        // 4. Seed Services
        serviceRepository.save(new Service(
                shop4,
                "House Wiring & Electrical Repair",
                "Expert domestic wiring, meter connections, ceiling fan installations, and fault repairs."
        ));
        serviceRepository.save(new Service(
                shop2,
                "Soil Health & Crop Advisory",
                "Free guidance on organic soil fertility, seasonal crop rotation, and government fertilizer schemes."
        ));

        // 5. Seed Reviews
        reviewRepository.save(new Review(
                customer,
                shop1,
                5,
                "Best grocery shop in Ramapuram! Everything is always fresh and owner Srinivasa garu is very courteous."
        ));
        reviewRepository.save(new Review(
                customer,
                shop2,
                5,
                "The neem fertilizer worked wonders for my chili crops. Highly recommended for farmers."
        ));

        logger.info("Default seed data initialized successfully! Admin, 2 Shop Owners, Customer, 4 Shops, and Products created.");
    }
}
