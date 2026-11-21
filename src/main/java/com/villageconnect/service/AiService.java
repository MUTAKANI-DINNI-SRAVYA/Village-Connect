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

    private boolean isApiKeyValid() {
        return apiKey != null && !apiKey.trim().isEmpty()
                && !apiKey.trim().equals("YOUR_GEMINI_API_KEY_HERE")
                && !apiKey.trim().equals("test_api_key_mock");
    }

    // 1. Core Gemini API invocation helper
    private String invokeGemini(String prompt) {
        if (!isApiKeyValid()) {
            logger.warn("Gemini API key is not configured. Skipping external API call.");
            return null;
        }

        try {
            String url = apiUrl + "?key=" + apiKey.trim();

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
            return null;
        }
        return null;
    }

    // 2. Multilingual Search translation / expansion
    public String translateOrExpandQuery(String query, boolean isTelugu) {
        if (!isApiKeyValid()) {
            return query;
        }

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

        String result = invokeGemini(prompt);
        if (result == null || result.isBlank()) {
            return query;
        }
        logger.info("Gemini translation/expansion for '{}': {}", query, result.trim());
        return result.trim();
    }

    // Check if a string contains Telugu characters
    public boolean containsTelugu(String query) {
        if (query == null) return false;
        // Telugu unicode block is U+0C00 to U+0C7F
        return query.matches(".*[\\u0C00-\\u0C7F]+.*");
    }

    // 3. AI Chatbot for Product/Shop Discovery
    public String chatDiscover(String userMessage) {
        if (isApiKeyValid()) {
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

            String geminiReply = invokeGemini(systemPrompt);
            if (geminiReply != null && !geminiReply.isBlank()) {
                return geminiReply;
            }
        }

        // Offline fallback discovery when API key is missing or call fails
        return offlineChatDiscover(userMessage);
    }

    // Smart catalog-based offline discovery fallback
    private String offlineChatDiscover(String userMessage) {
        String lower = (userMessage != null) ? userMessage.toLowerCase() : "";
        boolean isTelugu = containsTelugu(userMessage);

        List<Shop> shops = shopRepository.findAll();
        List<Product> products = productRepository.findAll();

        List<Shop> matchedShops = new ArrayList<>();
        List<Product> matchedProducts = new ArrayList<>();

        for (Shop s : shops) {
            if (lower.contains(s.getShopName().toLowerCase()) ||
                lower.contains(s.getCategory().toLowerCase()) ||
                lower.contains(s.getVillage().toLowerCase())) {
                matchedShops.add(s);
            }
        }

        for (Product p : products) {
            if (lower.contains(p.getProductName().toLowerCase()) ||
                lower.contains(p.getCategory().toLowerCase())) {
                matchedProducts.add(p);
            }
        }

        StringBuilder response = new StringBuilder();

        if (isTelugu) {
            response.append("నమస్కారం! VillageConnect సహాయకుడికి స్వాగతం.\n\n");
            if (!matchedShops.isEmpty() || !matchedProducts.isEmpty()) {
                response.append("మీ ప్రశ్నకు సరిపోయే వివరాలు:\n\n");
            } else {
                response.append("మా గ్రామీణ నెట్‌వర్క్‌లో అందుబాటులో ఉన్న దుకాణాలు:\n\n");
            }
        } else {
            response.append("Hello! Welcome to VillageConnect Assistant.\n\n");
            if (!matchedShops.isEmpty() || !matchedProducts.isEmpty()) {
                response.append("Here is what I found in our village network:\n\n");
            } else {
                response.append("Here are some available shops and services in our villages:\n\n");
            }
        }

        if (!matchedProducts.isEmpty()) {
            response.append("📦 **Products:**\n");
            for (Product p : matchedProducts) {
                response.append(String.format("• %s - Rs. %s (Available at %s, %s)\n",
                        p.getProductName(), p.getPrice(), p.getShop().getShopName(), p.getShop().getVillage()));
            }
            response.append("\n");
        }

        List<Shop> displayShops = !matchedShops.isEmpty() ? matchedShops : shops.subList(0, Math.min(4, shops.size()));
        response.append("🏪 **Shops:**\n");
        for (Shop s : displayShops) {
            response.append(String.format("• %s (%s) - %s | Phone: %s\n",
                    s.getShopName(), s.getCategory(), s.getVillage(), s.getPhone()));
        }

        if (!isApiKeyValid()) {
            response.append("\n💡 *Tip: To enable conversational Google Gemini AI responses, add your free API Key from https://aistudio.google.com in application.properties or set the GEMINI_API_KEY environment variable.*");
        }

        return response.toString();
    }

    // 4. AI Product Recommendations
    public String getProductRecommendations(User user, List<Product> availableProducts) {
        if (availableProducts == null || availableProducts.isEmpty()) {
            return "No products are currently available in the database for recommendation.";
        }

        if (isApiKeyValid()) {
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

            String geminiReply = invokeGemini(prompt);
            if (geminiReply != null && !geminiReply.isBlank()) {
                return geminiReply;
            }
        }

        // Offline fallback recommendation
        StringBuilder sb = new StringBuilder("<ul>");
        int count = 0;
        for (Product p : availableProducts) {
            if (count >= 3) break;
            sb.append("<li><strong>").append(p.getProductName()).append("</strong> (Rs. ")
              .append(p.getPrice()).append(") from <em>")
              .append(p.getShop().getShopName()).append("</em> - Recommended local essential product.</li>");
            count++;
        }
        sb.append("</ul>");
        return sb.toString();
    }
}
