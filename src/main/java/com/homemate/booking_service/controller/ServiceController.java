package com.homemate.booking_service.controller;

import com.homemate.booking_service.entity.ServiceEntity;
import com.homemate.booking_service.repository.ServiceRepository;
import com.homemate.booking_service.util.RoleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/services")
public class ServiceController {

    @Autowired
    private ServiceRepository serviceRepo;

    @Autowired
    private RoleValidator roleValidator;

    // ================= ADD SERVICE (ADMIN) =================
    @PostMapping
    public ServiceEntity addService(
            @RequestHeader("X-ROLE") String role,
            @RequestBody ServiceEntity service) {

        roleValidator.adminOnly(role);
        return serviceRepo.save(service);
    }

    // ================= VIEW ALL SERVICES (PUBLIC / ADMIN) =================
    @GetMapping
    public List<ServiceEntity> getServices() {
        return serviceRepo.findAll();
    }

    // ================= UPDATE SERVICE (ADMIN) =================
    @PutMapping("/{id}")
    public ServiceEntity updateService(
            @RequestHeader("X-ROLE") String role,
            @PathVariable Integer id,
            @RequestBody ServiceEntity updatedService) {

        roleValidator.adminOnly(role);

        ServiceEntity service = serviceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        service.setName(updatedService.getName());
        service.setPrice(updatedService.getPrice());

        return serviceRepo.save(service);
    }

    // ================= DELETE SERVICE (ADMIN) =================
    @DeleteMapping("/{id}")
    public String deleteService(
            @RequestHeader("X-ROLE") String role,
            @PathVariable Integer id) {

        roleValidator.adminOnly(role);

        serviceRepo.deleteById(id);
        return "SERVICE_DELETED";
    }
}
