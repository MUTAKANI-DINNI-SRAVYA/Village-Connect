package com.villageconnect.service;

import com.villageconnect.entity.Product;
import com.villageconnect.entity.Shop;
import com.villageconnect.entity.User;
import com.villageconnect.repository.ProductRepository;
import com.villageconnect.repository.ShopRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AiService {
    private static final Logger logger = LoggerFactory.getLogger(AiService.class);

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductRepository productRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    // 1. Core Gemini API invocation helper
    private String invokeGemini(String prompt) {
        try {
            String url = apiUrl + "?key=" + apiKey;

            // Prepare payload
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", Collections.singletonList(part));

            Map<String, Object> payload = new HashMap<>();
            payload.put("contents", Collections.singletonList(content));

            // Call API
            Map<String, Object> response = restTemplate.postForObject(url, payload, Map.class);
            if (response != null && response.containsKey("candidates")) {
                List<?> candidates = (List<?>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
                    Map<?, ?> contentMap = (Map<?, ?>) candidate.get("content");
                    List<?> parts = (List<?>) contentMap.get("parts");
                    if (!parts.isEmpty()) {
                        Map<?, ?> partMap = (Map<?, ?>) parts.get(0);
                        return (String) partMap.get("text");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Gemini API call failed: {}", e.getMessage());
            return "Sorry, I am unable to connect to my AI core at the moment. Please verify your internet connection or API Key. (Error: " + e.getMessage() + ")";
        }
        return "I'm sorry, I couldn't process that query.";
    }

    // 2. Multilingual Search translation / expansion
    public String translateOrExpandQuery(String query, boolean isTelugu) {
        String prompt;
        if (isTelugu) {
            prompt = "The user entered a search query in Telugu: '" + query + "'. "
                   + "Translate this query into standard English search keywords. "
                   + "Return ONLY the translated English search terms (separated by spaces or commas). Do not write any other explanation or text.";
        } else {
            prompt = "The user searched for '" + query + "' but we found no exact database match. "
                   + "Provide a list of 3-4 standard synonyms, related categories, or product terms. "
                   + "For example, if they search for 'cough remedy', output: 'cough syrup, cold medicine, medical'. "
                   + "Return ONLY the search keywords (separated by commas). Do not write any other explanation.";
        }
        
        String result = invokeGemini(prompt).trim();
        logger.info("Gemini translation/expansion for '{}': {}", query, result);
        return result;
    }

    // Check if a string contains Telugu characters
    public boolean containsTelugu(String query) {
        if (query == null) return false;
        // Telugu unicode block is U+0C00 to U+0C7F
        return query.matches(".*[\\u0C00-\\u0C7F]+.*");
    }

    // 3. AI Chatbot for Product/Shop Discovery
    public String chatDiscover(String userMessage) {
        // Load database catalog
        List<Shop> shops = shopRepository.findAll();
        List<Product> products = productRepository.findAll();

        StringBuilder catalogBuilder = new StringBuilder();
        catalogBuilder.append("AVAILABLE SHOPS:\n");
        for (Shop shop : shops) {
            catalogBuilder.append(String.format("- ID: %d, Name: %s, Category: %s, Village: %s, Phone: %s\n",
                    shop.getShopId(), shop.getShopName(), shop.getCategory(), shop.getVillage(), shop.getPhone()));
        }

        catalogBuilder.append("\nAVAILABLE PRODUCTS:\n");
        for (Product prod : products) {
            catalogBuilder.append(String.format("- Name: %s, Category: %s, Price: Rs.%s, Quantity: %d, Shop: %s (Village: %s)\n",
                    prod.getProductName(), prod.getCategory(), prod.getPrice().toString(), prod.getQuantity(),
                    prod.getShop().getShopName(), prod.getShop().getVillage()));
        }

        String systemPrompt = "You are the AI Assistant for 'VillageConnect', a multilingual rural business discovery platform. "
                + "Your job is to help village residents find shops, products, and services close to them.\n"
                + "Here is the current real-time database state:\n"
                + catalogBuilder.toString() + "\n"
                + "CRITICAL INSTRUCTIONS:\n"
                + "1. Respond in the SAME language the user asks their question in. If they ask in Telugu, answer in Telugu. If in English, answer in English.\n"
                + "2. Be polite, friendly, and helpful to rural residents.\n"
                + "3. Only recommend shops and products that are actually listed in the database state above.\n"
                + "4. If a product is out of stock (quantity is 0), mention it but suggest they use the 'Product Request' feature on the shop's details page.\n"
                + "5. Recommend shops located in the user's requested village first, if specified.\n\n"
                + "User Query: " + userMessage;

        return invokeGemini(systemPrompt);
    }

    // 4. AI Product Recommendations
    public String getProductRecommendations(User user, List<Product> availableProducts) {
        if (availableProducts.isEmpty()) {
            return "No products are currently available in the database for recommendation.";
        }

        StringBuilder prodList = new StringBuilder();
        for (Product prod : availableProducts) {
            prodList.append(String.format("- ID: %d, Name: %s, Category: %s, Price: Rs.%s, Shop: %s\n",
                    prod.getProductId(), prod.getProductName(), prod.getCategory(), prod.getPrice(), prod.getShop().getShopName()));
        }

        String prompt = "You are a rural business recommendation engine. "
                + "Recommend 3 products from the list below for a user named " + user.getName() + " (who is a " + user.getRole().toLowerCase() + "). "
                + "Provide a brief, compelling 1-sentence reason for each recommendation that appeals to a rural resident.\n\n"
                + "Available Products:\n"
                + prodList.toString() + "\n"
                + "Format your response as a clean HTML bullet list (<ul>) with <li> tags. Do not write markdown tags or other surrounding texts. Start directly with <ul>.";

        return invokeGemini(prompt);
    }
}
