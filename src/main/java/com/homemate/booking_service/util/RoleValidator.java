package com.homemate.booking_service.util;

import org.springframework.stereotype.Component;

@Component
public class RoleValidator {

    public void adminOnly(String role) {
        if (!"ADMIN".equals(role)) {
            throw new RuntimeException("ADMIN access required");
        }
    }

    public void userOnly(String role) {
        if (!"USER".equals(role)) {
            throw new RuntimeException("USER access required");
        }
    }
}
