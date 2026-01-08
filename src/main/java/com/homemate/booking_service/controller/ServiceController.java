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

    // ADMIN: Add service
    @PostMapping
    public ServiceEntity addService(
            @RequestHeader("X-ROLE") String role,
            @RequestBody ServiceEntity service) {

        roleValidator.adminOnly(role);
        return serviceRepo.save(service);
    }

    // PUBLIC: View services
    @GetMapping
    public List<ServiceEntity> getServices() {
        return serviceRepo.findAll();
    }
}
