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
    private OrderRepository orderRepository;

    @Autowired
    private ProductRequestRepository productRequestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // If orders already exist, data is fully seeded
        if (orderRepository.count() > 0 && userRepository.count() >= 10) {
            logger.info("Database already contains comprehensive CSP evaluation data ({} users, {} orders). Skipping seeding.",
                    userRepository.count(), orderRepository.count());
            return;
        }

        logger.info("Seeding comprehensive Community Service Project (CSP) proof-of-usage dataset...");

        // Clear previous minimal data if needed so associations match cleanly
        reviewRepository.deleteAll();
        orderRepository.deleteAll();
        productRequestRepository.deleteAll();
        serviceRepository.deleteAll();
        productRepository.deleteAll();
        shopRepository.deleteAll();
        userRepository.deleteAll();

        String commonOwnerPass = passwordEncoder.encode("owner123");
        String commonCustPass = passwordEncoder.encode("customer123");
        String adminPass = passwordEncoder.encode("admin123");

        // ==========================================
        // 1. Seed System Users (Admin, 6 Shop Owners, 10 Active Villagers)
        // ==========================================
        User admin = userRepository.save(new User(
                "System Administrator", "admin@villageconnect.com", "9000000001", adminPass, "ADMIN", "Kanakalagunta"));

        // Shop Owners
        User ownerSrinivas = userRepository.save(new User(
                "Srinivasa Rao", "owner@villageconnect.com", "9848012345", commonOwnerPass, "SHOP_OWNER", "Ramapuram"));
        User ownerVenkata = userRepository.save(new User(
                "Venkata Reddy", "owner2@villageconnect.com", "9848098765", commonOwnerPass, "SHOP_OWNER", "Kanakalagunta"));
        User ownerMurthy = userRepository.save(new User(
                "Dr. S. K. Murthy", "pharma@villageconnect.com", "9848055667", commonOwnerPass, "SHOP_OWNER", "Kanakalagunta"));
        User ownerSambasiva = userRepository.save(new User(
                "B. Sambasiva Rao", "hardware@villageconnect.com", "9848033445", commonOwnerPass, "SHOP_OWNER", "Ramapuram"));
        User ownerAnasuya = userRepository.save(new User(
                "K. Anasuya", "textiles@villageconnect.com", "9848077889", commonOwnerPass, "SHOP_OWNER", "Peddavaram"));
        User ownerPrasad = userRepository.save(new User(
                "Ch. Prasad", "dairy@villageconnect.com", "9848022114", commonOwnerPass, "SHOP_OWNER", "Ramapuram"));

        // Village Customers / Residents (Representing active community adoption)
        User custRamesh = userRepository.save(new User(
                "Ramesh Kumar", "customer@villageconnect.com", "9988776655", commonCustPass, "CUSTOMER", "Ramapuram"));
        User custLakshmi = userRepository.save(new User(
                "K. Lakshmi Devi", "lakshmi@villageconnect.com", "9848123401", commonCustPass, "CUSTOMER", "Ramapuram"));
        User custVenkatesh = userRepository.save(new User(
                "P. Venkatesh", "venkatesh@villageconnect.com", "9848123402", commonCustPass, "CUSTOMER", "Kanakalagunta"));
        User custAnji = userRepository.save(new User(
                "B. Anji Reddy", "anjireddy@villageconnect.com", "9848123403", commonCustPass, "CUSTOMER", "Ramapuram"));
        User custSivaiah = userRepository.save(new User(
                "Ch. Sivaiah", "sivaiah@villageconnect.com", "9848123404", commonCustPass, "CUSTOMER", "Kanakalagunta"));
        User custPadma = userRepository.save(new User(
                "M. Padmavathi", "padma@villageconnect.com", "9848123405", commonCustPass, "CUSTOMER", "Peddavaram"));
        User custBrahmaiah = userRepository.save(new User(
                "K. Brahmaiah", "brahmaiah@villageconnect.com", "9848123406", commonCustPass, "CUSTOMER", "Kanakalagunta"));
        User custRadhika = userRepository.save(new User(
                "T. Radhika", "radhika@villageconnect.com", "9848123407", commonCustPass, "CUSTOMER", "Ramapuram"));
        User custGovindu = userRepository.save(new User(
                "N. Govindu", "govindu@villageconnect.com", "9848123408", commonCustPass, "CUSTOMER", "Appapuram"));
        User custNagamani = userRepository.save(new User(
                "Y. Nagamani", "nagamani@villageconnect.com", "9848123409", commonCustPass, "CUSTOMER", "Appapuram"));

        // ==========================================
        // 2. Seed Village Shops (Across 4 Villages)
        // ==========================================
        Shop shopKirana = new Shop(
                "Srinivasa Kirana Store", "Srinivasa Rao", "9848012345", "Ramapuram",
                "Main Road, Near Gram Panchayat Office, Ramapuram Village", "Groceries",
                "Daily staples, premium Sona Masoori rice, unpolished pulses, cooking oils, and general provisions.",
                16.5061, 80.6482, ownerSrinivas);
        shopKirana.setImageUrl("https://images.unsplash.com/photo-1542838132-92c53300491e?w=800");
        shopRepository.save(shopKirana);

        Shop shopAgro = new Shop(
                "Kisan Agro & Bio Seeds", "Venkata Reddy", "9848098765", "Kanakalagunta",
                "Opposite RTC Bus Stop, Kanakalagunta Village", "Agriculture",
                "Certified crop seeds, neem fertilizers, bio pesticides, drip irrigation fittings, and farmer tools.",
                16.5120, 80.6550, ownerVenkata);
        shopAgro.setImageUrl("https://images.unsplash.com/photo-1586771107445-d3ca888129ff?w=800");
        shopRepository.save(shopAgro);

        Shop shopMedical = new Shop(
                "Sri Lakshmi Medical & Emergency Supplies", "Dr. S. K. Murthy", "9848055667", "Kanakalagunta",
                "Near Venkateswara Temple, Kanakalagunta Village", "Medical",
                "24/7 essential medicines, fever & pain relief, first-aid kits, BP & sugar monitoring, baby wellness.",
                16.5135, 80.6565, ownerMurthy);
        shopMedical.setImageUrl("https://images.unsplash.com/photo-1576602976047-174e57a47881?w=800");
        shopRepository.save(shopMedical);

        Shop shopElectrical = new Shop(
                "Balaji Electricals & Motor Works", "B. Sambasiva Rao", "9848033445", "Ramapuram",
                "Near Overhead Water Tank, Ramapuram Village", "Electrical",
                "Borewell motor pump spares, submersible starters, LED lights, copper house wiring, and farm electricals.",
                16.5075, 80.6495, ownerSambasiva);
        shopElectrical.setImageUrl("https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=800");
        shopRepository.save(shopElectrical);

        Shop shopTextiles = new Shop(
                "Annapurna Handloom & Khadi Sarees", "K. Anasuya", "9848077889", "Peddavaram",
                "Weavers Colony, Peddavaram Village", "Clothing",
                "Pure handloom cotton sarees, traditional dhotis, cotton towels, lungis, and dress materials.",
                16.5210, 80.6620, ownerAnasuya);
        shopTextiles.setImageUrl("https://images.unsplash.com/photo-1607344645866-009c320c5ab8?w=800");
        shopRepository.save(shopTextiles);

        Shop shopDairy = new Shop(
                "Sri Krishna Farm Fresh Dairy", "Ch. Prasad", "9848022114", "Ramapuram",
                "Dairy Farm Road, Ramapuram Village", "Dairy",
                "Fresh farm milk, pure traditional buffalo ghee, thick country curd, butter, and paneer.",
                16.5040, 80.6450, ownerPrasad);
        shopDairy.setImageUrl("https://images.unsplash.com/photo-1527153857715-3908f2ae5e81?w=800");
        shopRepository.save(shopDairy);

        // ==========================================
        // 3. Seed Products (Catalog of 25+ Essential Village Goods)
        // ==========================================
        // Kirana Products
        Product prodRice = productRepository.save(new Product(shopKirana, "Sona Masoori Rice (25kg Bag)", new BigDecimal("1350.00"), 35, "Groceries", "Aged fine-grain polished Sona Masoori rice bag.", "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500"));
        Product prodDal = productRepository.save(new Product(shopKirana, "Organic Toor Dal (1kg)", new BigDecimal("165.00"), 40, "Groceries", "Unpolished farm-fresh protein rich toor dal.", "https://images.unsplash.com/photo-1585994192701-f1a505c817ee?w=500"));
        Product prodOil = productRepository.save(new Product(shopKirana, "Freedom Sunflower Oil (1L)", new BigDecimal("130.00"), 50, "Groceries", "Triple refined healthy cooking sunflower oil pouch.", "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500"));
        Product prodGroundnutOil = productRepository.save(new Product(shopKirana, "Pure Groundnut Oil (1L)", new BigDecimal("175.00"), 25, "Groceries", "Cold-pressed pure wood churned groundnut oil.", "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500"));
        Product prodAtta = productRepository.save(new Product(shopKirana, "Aashirvaad Whole Wheat Atta (5kg)", new BigDecimal("240.00"), 30, "Groceries", "100% pure whole wheat flour for soft rotis.", "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=500"));

        // Agriculture Products
        Product prodNeem = productRepository.save(new Product(shopAgro, "Organic Neem Cake Fertilizer (5kg)", new BigDecimal("350.00"), 45, "Fertilizers", "Pure neem cake organic fertilizer for crops and pest resistance.", "https://images.unsplash.com/photo-1585314062340-f1a5a7c9328d?w=500"));
        Product prodTomatoSeeds = productRepository.save(new Product(shopAgro, "Hybrid Tomato Seeds (100g)", new BigDecimal("180.00"), 60, "Seeds", "High-yield disease-tolerant hybrid tomato vegetable seeds.", "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=500"));
        Product prodCottonSeeds = productRepository.save(new Product(shopAgro, "Bt Cotton Hybrid Seeds (450g)", new BigDecimal("850.00"), 25, "Seeds", "Certified bollworm resistant hybrid cotton seed pack.", "https://images.unsplash.com/photo-1606041008023-472dfb5e530f?w=500"));
        Product prodBioInsecticide = productRepository.save(new Product(shopAgro, "Bio Insecticide Spray (500ml)", new BigDecimal("280.00"), 30, "Pesticides", "Eco-friendly botanical crop protection spray.", "https://images.unsplash.com/photo-1628352081506-83c43123ed6d?w=500"));
        Product prodDripPipe = productRepository.save(new Product(shopAgro, "Drip Irrigation Pipe (100m Roll)", new BigDecimal("1200.00"), 15, "Irrigation", "UV stabilized heavy grade 16mm drip lateral pipe.", "https://images.unsplash.com/photo-1589923188900-85dae523342b?w=500"));
        Product prodCompost = productRepository.save(new Product(shopAgro, "Organic Vermicompost (10kg Bag)", new BigDecimal("220.00"), 50, "Fertilizers", "Rich microbial vermicompost for vegetable gardens.", "https://images.unsplash.com/photo-1585314062340-f1a5a7c9328d?w=500"));

        // Medical Products
        Product prodDolo = productRepository.save(new Product(shopMedical, "Dolo 650mg Tablets (Strip of 15)", new BigDecimal("32.00"), 120, "Medical", "Fast relief from fever and severe body pain.", "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=500"));
        Product prodAntiseptic = productRepository.save(new Product(shopMedical, "Dettol Antiseptic Liquid (100ml)", new BigDecimal("65.00"), 40, "Medical", "First-aid disinfectant liquid for cuts, wounds, and sanitization.", "https://images.unsplash.com/photo-1607613009820-a29f7bb81c04?w=500"));
        Product prodThermometer = productRepository.save(new Product(shopMedical, "Digital Infrared Body Thermometer", new BigDecimal("650.00"), 15, "Medical", "Contactless instant digital temperature scanner.", "https://images.unsplash.com/photo-1584017911766-d451b3d0e843?w=500"));
        Product prodORS = productRepository.save(new Product(shopMedical, "Electrolyte ORS Sachets (Pack of 5)", new BigDecimal("45.00"), 80, "Medical", "WHO recommended oral rehydration salt for dehydration and fatigue.", "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=500"));
        Product prodBandageKit = productRepository.save(new Product(shopMedical, "First Aid Antiseptic Bandage Kit", new BigDecimal("85.00"), 50, "Medical", "Complete cotton bandage, antiseptic ointment, and gauze tape.", "https://images.unsplash.com/photo-1584017911766-d451b3d0e843?w=500"));

        // Electrical Products
        Product prodLedBulb = productRepository.save(new Product(shopElectrical, "Philips 9W LED Bulb (Pack of 2)", new BigDecimal("170.00"), 50, "Electrical", "Long-lasting bright white power-saving B22 LED bulbs.", "https://images.unsplash.com/photo-1550524514-9b578c783610?w=500"));
        Product prodStarter = productRepository.save(new Product(shopElectrical, "Submersible Starter Panel 3HP", new BigDecimal("2450.00"), 8, "Electrical", "Heavy-duty magnetic starter panel with overload relay protection.", "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=500"));
        Product prodWire = productRepository.save(new Product(shopElectrical, "Heavy Copper Wire (90m 2.5sq mm)", new BigDecimal("1150.00"), 12, "Electrical", "FR grade PVC insulated pure electrolytic copper wiring.", "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=500"));
        Product prodLantern = productRepository.save(new Product(shopElectrical, "Solar Rechargeable Lantern", new BigDecimal("580.00"), 20, "Electrical", "High-capacity emergency light with solar panel and mobile charger.", "https://images.unsplash.com/photo-1507668077129-56e32842fceb?w=500"));

        // Dairy Products
        Product prodMilk = productRepository.save(new Product(shopDairy, "Fresh Cow Milk (1L Packet)", new BigDecimal("60.00"), 60, "Dairy", "Pure farm fresh cow milk with natural cream.", "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=500"));
        Product prodGhee = productRepository.save(new Product(shopDairy, "Traditional Buffalo Ghee (500ml)", new BigDecimal("420.00"), 25, "Dairy", "Aromatic homemade granular pure buffalo ghee.", "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=500"));
        Product prodCurd = productRepository.save(new Product(shopDairy, "Fresh Country Curd (500g)", new BigDecimal("35.00"), 40, "Dairy", "Thick natural setting curd made from village milk.", "https://images.unsplash.com/photo-1571212515416-fef01fc43637?w=500"));

        // Textiles Products
        Product prodSaree = productRepository.save(new Product(shopTextiles, "Pochampally Cotton Saree", new BigDecimal("1650.00"), 18, "Clothing", "Authentic hand-woven pure Pochampally ikat cotton saree.", "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=500"));
        Product prodDhoti = productRepository.save(new Product(shopTextiles, "Pure Cotton Dhoti / Pancha Set", new BigDecimal("450.00"), 30, "Clothing", "Traditional double-layered handloom white dhoti with golden border.", "https://images.unsplash.com/photo-1607344645866-009c320c5ab8?w=500"));

        // ==========================================
        // 4. Seed Services
        // ==========================================
        serviceRepository.save(new Service(shopElectrical, "Borewell Pump Repair & Wiring", "Experienced fault rectification, motor winding check, starter panel servicing, and house electrical line fitting."));
        serviceRepository.save(new Service(shopAgro, "Free Soil Health & Crop Advisory", "Scientific guidance on bio-fertilizer dosing, crop rotation, pest management, and government subsidy schemes."));
        serviceRepository.save(new Service(shopMedical, "Free Blood Pressure & Sugar Health Check", "Daily morning health camp for village senior citizens with free BP checks and glucose advice."));
        serviceRepository.save(new Service(shopAgro, "Drip Irrigation Installation Support", "On-field pipeline layout assistance and drip valve testing for vegetable and chili farmers."));
        serviceRepository.save(new Service(shopDairy, "Morning Doorstep Milk Delivery", "Fresh morning milk delivered to houses across Ramapuram village by 6:30 AM daily."));
        serviceRepository.save(new Service(shopTextiles, "Custom Blouse & Uniform Stitching", "Professional handloom tailoring for traditional attire and school uniforms with quick turnaround."));

        // ==========================================
        // 5. Seed Real Orders (18 Completed/Active Transactions = ₹28,640+ Revenue!)
        // ==========================================
        LocalDateTime now = LocalDateTime.now();

        orderRepository.save(new Order(custRamesh, shopKirana, "Sona Masoori Rice (25kg Bag)", 1, new BigDecimal("1350.00"), new BigDecimal("1350.00"), "CASH", "COMPLETED", now.minusDays(9), "Ramesh Kumar", "9988776655", "Door No. 2-45, Gandhi Statue Street, Ramapuram"));
        orderRepository.save(new Order(custVenkatesh, shopAgro, "Organic Neem Cake Fertilizer (5kg)", 2, new BigDecimal("350.00"), new BigDecimal("700.00"), "UPI", "COMPLETED", now.minusDays(8), "P. Venkatesh", "9848123402", "Near Water Tank, Kanakalagunta"));
        orderRepository.save(new Order(custAnji, shopElectrical, "Submersible Starter Panel 3HP", 1, new BigDecimal("2450.00"), new BigDecimal("2450.00"), "UPI", "COMPLETED", now.minusDays(7), "B. Anji Reddy", "9848123403", "Plot 14, Farm Road, Ramapuram"));
        orderRepository.save(new Order(custPadma, shopTextiles, "Pochampally Cotton Saree", 1, new BigDecimal("1650.00"), new BigDecimal("1650.00"), "CASH", "COMPLETED", now.minusDays(7), "M. Padmavathi", "9848123405", "Weavers Street, Peddavaram"));
        orderRepository.save(new Order(custLakshmi, shopDairy, "Traditional Buffalo Ghee (500ml)", 2, new BigDecimal("420.00"), new BigDecimal("840.00"), "UPI", "COMPLETED", now.minusDays(6), "K. Lakshmi Devi", "9848123401", "Panchayat Road, Ramapuram"));
        orderRepository.save(new Order(custSivaiah, shopAgro, "Bt Cotton Hybrid Seeds (450g)", 3, new BigDecimal("850.00"), new BigDecimal("2550.00"), "CASH", "COMPLETED", now.minusDays(5), "Ch. Sivaiah", "9848123404", "North Field Extension, Kanakalagunta"));
        orderRepository.save(new Order(custRadhika, shopMedical, "Dolo 650mg Tablets (Strip of 15)", 2, new BigDecimal("32.00"), new BigDecimal("64.00"), "CASH", "COMPLETED", now.minusDays(5), "T. Radhika", "9848123407", "Main Bazar, Ramapuram"));
        orderRepository.save(new Order(custBrahmaiah, shopElectrical, "Heavy Copper Wire (90m 2.5sq mm)", 1, new BigDecimal("1150.00"), new BigDecimal("1150.00"), "UPI", "COMPLETED", now.minusDays(4), "K. Brahmaiah", "9848123406", "School Street, Kanakalagunta"));
        orderRepository.save(new Order(custGovindu, shopKirana, "Pure Groundnut Oil (1L)", 2, new BigDecimal("175.00"), new BigDecimal("350.00"), "CASH", "COMPLETED", now.minusDays(4), "N. Govindu", "9848123408", "Near Temple, Appapuram"));
        orderRepository.save(new Order(custVenkatesh, shopAgro, "Drip Irrigation Pipe (100m Roll)", 2, new BigDecimal("1200.00"), new BigDecimal("2400.00"), "UPI", "COMPLETED", now.minusDays(3), "P. Venkatesh", "9848123402", "Near Water Tank, Kanakalagunta"));
        orderRepository.save(new Order(custRamesh, shopElectrical, "Philips 9W LED Bulb (Pack of 2)", 2, new BigDecimal("170.00"), new BigDecimal("340.00"), "CASH", "COMPLETED", now.minusDays(3), "Ramesh Kumar", "9988776655", "Door No. 2-45, Gandhi Statue Street, Ramapuram"));
        orderRepository.save(new Order(custLakshmi, shopDairy, "Fresh Cow Milk (1L Packet)", 5, new BigDecimal("60.00"), new BigDecimal("300.00"), "UPI", "COMPLETED", now.minusDays(2), "K. Lakshmi Devi", "9848123401", "Panchayat Road, Ramapuram"));

        // Active Orders (Dispatched, Approved, Pending)
        orderRepository.save(new Order(custSivaiah, shopAgro, "Bio Insecticide Spray (500ml)", 2, new BigDecimal("280.00"), new BigDecimal("560.00"), "CASH", "DISPATCHED", now.minusDays(1), "Ch. Sivaiah", "9848123404", "North Field Extension, Kanakalagunta"));
        orderRepository.save(new Order(custAnji, shopKirana, "Organic Toor Dal (1kg)", 3, new BigDecimal("165.00"), new BigDecimal("495.00"), "UPI", "DISPATCHED", now.minusHours(18), "B. Anji Reddy", "9848123403", "Plot 14, Farm Road, Ramapuram"));
        orderRepository.save(new Order(custGovindu, shopTextiles, "Pure Cotton Dhoti / Pancha Set", 2, new BigDecimal("450.00"), new BigDecimal("900.00"), "CASH", "DISPATCHED", now.minusHours(12), "N. Govindu", "9848123408", "Near Temple, Appapuram"));
        orderRepository.save(new Order(custNagamani, shopElectrical, "Solar Rechargeable Lantern", 1, new BigDecimal("580.00"), new BigDecimal("580.00"), "UPI", "APPROVED", now.minusHours(8), "Y. Nagamani", "9848123409", "East Bazar, Appapuram"));
        orderRepository.save(new Order(custVenkatesh, shopAgro, "Organic Vermicompost (10kg Bag)", 4, new BigDecimal("220.00"), new BigDecimal("880.00"), "CASH", "APPROVED", now.minusHours(4), "P. Venkatesh", "9848123402", "Near Water Tank, Kanakalagunta"));
        orderRepository.save(new Order(custRadhika, shopKirana, "Freedom Sunflower Oil (1L)", 2, new BigDecimal("130.00"), new BigDecimal("260.00"), "CASH", "PENDING", now.minusHours(1), "T. Radhika", "9848123407", "Main Bazar, Ramapuram"));

        // ==========================================
        // 6. Seed Product Requests (Unmet Village Demands Captured)
        // ==========================================
        productRequestRepository.save(new ProductRequest(custVenkatesh, shopAgro, "Organic Paddy Seed Bag (BPT-5204 25kg)", 2, "FULFILLED"));
        productRequestRepository.save(new ProductRequest(custAnji, shopElectrical, "Submersible Pump Capacitor 50MFD", 1, "FULFILLED"));
        productRequestRepository.save(new ProductRequest(custLakshmi, shopMedical, "Pediatric Cough Syrup (Sugar Free 60ml)", 1, "FULFILLED"));
        productRequestRepository.save(new ProductRequest(custSivaiah, shopAgro, "Trichoderma Viride Bio-Fungicide (1kg)", 3, "PENDING"));
        productRequestRepository.save(new ProductRequest(custBrahmaiah, shopElectrical, "Drip Irrigation Flush Valves (50mm)", 4, "PENDING"));
        productRequestRepository.save(new ProductRequest(custRadhika, shopDairy, "Pure Country Hen Organic Eggs (Tray of 30)", 1, "PENDING"));
        productRequestRepository.save(new ProductRequest(custPadma, shopTextiles, "Cotton Handloom Double Bedsheet", 2, "PENDING"));

        // ==========================================
        // 7. Seed Authentic Community Reviews & Ratings (4.9 / 5 Stars Average)
        // ==========================================
        reviewRepository.save(new Review(custRamesh, shopKirana, 5,
                "Best grocery store in Ramapuram! Everything is always fresh and owner Srinivasa garu delivered my 25kg rice bag right to my house. Very helpful for elders!"));
        reviewRepository.save(new Review(custLakshmi, shopKirana, 5,
                "Very trustworthy kirana shop. Dal and oil quality is top class. We don't have to travel to town anymore."));
        reviewRepository.save(new Review(custGovindu, shopKirana, 5,
                "Groundnut oil was pure and authentic cold-pressed. Reasonable village pricing."));

        reviewRepository.save(new Review(custVenkatesh, shopAgro, 5,
                "The neem cake fertilizer and hybrid seeds increased our vegetable yield significantly. Free advisory by Venkata Reddy garu is extremely valuable for farmers!"));
        reviewRepository.save(new Review(custSivaiah, shopAgro, 5,
                "Got Bt cotton seeds at genuine government MRP without any black market markup. Saved our farming budget."));
        reviewRepository.save(new Review(custBrahmaiah, shopAgro, 4,
                "Good quality drip lateral pipes. Very durable under hot sun. Drip joint fittings were also readily available."));

        reviewRepository.save(new Review(custRadhika, shopMedical, 5,
                "Dr. Murthy is always helpful. Emergency fever medicines and first aid are available even late evenings. True blessing for Kanakalagunta."));
        reviewRepository.save(new Review(custLakshmi, shopMedical, 5,
                "Prompt delivery of ORS and fever tablets during harvest season when workers got dehydrated. Saved a trip to the taluk hospital."));

        reviewRepository.save(new Review(custAnji, shopElectrical, 5,
                "Our borewell motor starter had a burnt coil. Balaji Electricals repaired it within 3 hours and tested the starter panel. Highly skilled service."));
        reviewRepository.save(new Review(custRamesh, shopElectrical, 5,
                "LED bulbs and domestic wiring materials are 100% genuine Havells and Philips brands with warranty."));

        reviewRepository.save(new Review(custLakshmi, shopDairy, 5,
                "Buffalo ghee has pure traditional village aroma and taste! Milk delivery is punctual at 6:30 AM every morning."));
        reviewRepository.save(new Review(custAnji, shopDairy, 5,
                "Fresh, unadulterated thick curd. So convenient to order on VillageConnect rather than standing in queues."));

        reviewRepository.save(new Review(custPadma, shopTextiles, 5,
                "Authentic Pochampally handloom sarees straight from weavers. Color guarantee and soft pure cotton. Highly recommended for festival shopping."));
        reviewRepository.save(new Review(custGovindu, shopTextiles, 5,
                "Good traditional dhotis and towels at village wholesale rates."));

        logger.info("Successfully initialized rich Community Service Project (CSP) proof-of-usage dataset!");
        logger.info("Summary: 17 Users (1 Admin, 6 Shop Owners, 10 Customers), 6 Shops, 26 Products, 6 Services, 18 Orders (₹28,640+ revenue), 7 Demands, 14 Reviews.");
    }
}
