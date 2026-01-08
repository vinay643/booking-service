package com.homemate.booking_service.controller;

import com.homemate.booking_service.entity.ProviderEntity;
import com.homemate.booking_service.repository.ProviderRepository;
import com.homemate.booking_service.util.RoleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/providers")
public class ProviderController {

    @Autowired
    private ProviderRepository providerRepo;

    @Autowired
    private RoleValidator roleValidator;

    // ADMIN: Add provider
    @PostMapping
    public ProviderEntity addProvider(
            @RequestHeader("X-ROLE") String role,
            @RequestBody ProviderEntity provider) {

        roleValidator.adminOnly(role);
        provider.setAvailable(true);
        return providerRepo.save(provider);
    }

    // ADMIN: View providers
    @GetMapping
    public List<ProviderEntity> getProviders(
            @RequestHeader("X-ROLE") String role) {

        roleValidator.adminOnly(role);
        return providerRepo.findAll();
    }
}

