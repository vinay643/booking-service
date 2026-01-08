package com.homemate.booking_service.controller;

import com.homemate.booking_service.entity.UserEntity;
import com.homemate.booking_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    // USER SIGNUP
    @PostMapping("/signup")
    public UserEntity signup(@RequestBody UserEntity user) {
        user.setRole("USER");
        return userRepo.save(user);
    }

    // ADMIN SIGNUP (protected by secret key)
    @PostMapping("/admin/signup")
    public UserEntity adminSignup(
            @RequestHeader("X-ADMIN-KEY") String adminKey,
            @RequestBody UserEntity user) {

        if (!"ADMIN123".equals(adminKey)) {
            throw new RuntimeException("Invalid admin key");
        }

        user.setRole("ADMIN");
        return userRepo.save(user);
    }

    // LOGIN
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> request) {

        String phone = request.get("phone");
        String password = request.get("password");

        UserEntity user = userRepo.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        Map<String, String> response = new HashMap<>();
        response.put("name", user.getName());
        response.put("role", user.getRole());

        return response;
    }
}
