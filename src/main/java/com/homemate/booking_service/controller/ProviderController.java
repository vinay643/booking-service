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

    // ================= ADD PROVIDER (ADMIN) =================
    @PostMapping
    public ProviderEntity addProvider(
            @RequestHeader("X-ROLE") String role,
            @RequestBody ProviderEntity provider) {

        roleValidator.adminOnly(role);
        provider.setAvailable(true);
        return providerRepo.save(provider);
    }

    // ================= VIEW ALL PROVIDERS (ADMIN) =================
    @GetMapping
    public List<ProviderEntity> getProviders(
            @RequestHeader("X-ROLE") String role) {

        roleValidator.adminOnly(role);
        System.out.println("PROVIDERS COUNT = " + providerRepo.findAll().size());
        return providerRepo.findAll();
    }

    // ================= UPDATE PROVIDER (ADMIN) =================
    @PutMapping("/{id}")
    public ProviderEntity updateProvider(
            @RequestHeader("X-ROLE") String role,
            @PathVariable Integer id,
            @RequestBody ProviderEntity updatedProvider) {

        roleValidator.adminOnly(role);

        ProviderEntity provider = providerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        provider.setName(updatedProvider.getName());
        provider.setSkill(updatedProvider.getSkill());

        return providerRepo.save(provider);
    }

    // ================= ENABLE / DISABLE PROVIDER (ADMIN) =================
    @PutMapping("/{id}/availability")
    public ProviderEntity updateAvailability(
            @RequestHeader("X-ROLE") String role,
            @PathVariable Integer id,
            @RequestParam boolean available) {

        roleValidator.adminOnly(role);

        ProviderEntity provider = providerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        provider.setAvailable(available);
        return providerRepo.save(provider);
    }

    // ================= DELETE PROVIDER (ADMIN) =================
    @DeleteMapping("/{id}")
    public String deleteProvider(
            @RequestHeader("X-ROLE") String role,
            @PathVariable Integer id) {

        roleValidator.adminOnly(role);

        providerRepo.deleteById(id);
        return "PROVIDER_DELETED";
    }
}
