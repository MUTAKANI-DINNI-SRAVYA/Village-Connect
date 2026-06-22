package com.villageconnect.service;

import com.villageconnect.dto.ServiceDto;
import com.villageconnect.entity.Service;
import com.villageconnect.entity.Shop;
import com.villageconnect.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    public Service createService(ServiceDto serviceDto, Shop shop) {
        Service service = new Service(
                shop,
                serviceDto.getServiceName(),
                serviceDto.getDescription()
        );
        return serviceRepository.save(service);
    }

    public Service updateService(Long serviceId, ServiceDto serviceDto) {
        Service service = findById(serviceId);
        service.setServiceName(serviceDto.getServiceName());
        service.setDescription(serviceDto.getDescription());
        return serviceRepository.save(service);
    }

    public Service findById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + id));
    }

    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public List<Service> getServicesByShop(Shop shop) {
        return serviceRepository.findByShop(shop);
    }

    public void deleteService(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new RuntimeException("Service not found with ID: " + id);
        }
        serviceRepository.deleteById(id);
    }
}
