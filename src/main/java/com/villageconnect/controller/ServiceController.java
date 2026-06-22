package com.villageconnect.controller;

import com.villageconnect.dto.ServiceDto;
import com.villageconnect.entity.Service;
import com.villageconnect.entity.Shop;
import com.villageconnect.entity.User;
import com.villageconnect.service.ServiceService;
import com.villageconnect.service.ShopService;
import com.villageconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private UserService userService;

    private ServiceDto convertToDto(Service service) {
        return new ServiceDto(
                service.getServiceId(),
                service.getShop().getShopId(),
                service.getShop().getShopName(),
                service.getServiceName(),
                service.getDescription()
        );
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<ServiceDto>> getServicesByShop(@PathVariable Long shopId) {
        Shop shop = shopService.findById(shopId);
        List<Service> services = serviceService.getServicesByShop(shop);
        List<ServiceDto> dtos = services.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @PreAuthorize("hasRole('SHOP_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<?> createService(@Valid @RequestBody ServiceDto serviceDto, Principal principal) {
        try {
            Shop shop = shopService.findById(serviceDto.getShopId());
            User currentUser = userService.findByEmail(principal.getName());

            // Auth verification
            if (!currentUser.getRole().equals("ADMIN") && !shop.getOwner().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body("You are not authorized to add services to this shop");
            }

            Service service = serviceService.createService(serviceDto, shop);
            return ResponseEntity.ok(convertToDto(service));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SHOP_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateService(@PathVariable Long id, @Valid @RequestBody ServiceDto serviceDto, Principal principal) {
        try {
            Service service = serviceService.findById(id);
            User currentUser = userService.findByEmail(principal.getName());

            // Auth verification
            if (!currentUser.getRole().equals("ADMIN") && !service.getShop().getOwner().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body("You are not authorized to update this service");
            }

            Service updatedService = serviceService.updateService(id, serviceDto);
            return ResponseEntity.ok(convertToDto(updatedService));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SHOP_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteService(@PathVariable Long id, Principal principal) {
        try {
            Service service = serviceService.findById(id);
            User currentUser = userService.findByEmail(principal.getName());

            // Auth verification
            if (!currentUser.getRole().equals("ADMIN") && !service.getShop().getOwner().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body("You are not authorized to delete this service");
            }

            serviceService.deleteService(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Service deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
