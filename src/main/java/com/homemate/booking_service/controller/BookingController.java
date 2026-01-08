package com.homemate.booking_service.controller;

import com.homemate.booking_service.entity.BookingEntity;
import com.homemate.booking_service.entity.ProviderEntity;
import com.homemate.booking_service.repository.BookingRepository;
import com.homemate.booking_service.repository.ProviderRepository;
import com.homemate.booking_service.util.RoleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private ProviderRepository providerRepo;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RoleValidator roleValidator;

    // USER: Create booking
    @PostMapping
    public BookingEntity createBooking(
            @RequestHeader("X-ROLE") String role,
            @RequestBody BookingEntity booking) {

        roleValidator.userOnly(role);

        booking.setStatus("REQUESTED");

        // 1️⃣ Payment
        String paymentResponse =
                restTemplate.getForObject(
                        "http://PAYMENT-SERVICE/pay",
                        String.class);

        if (!"PAYMENT_SUCCESS".equals(paymentResponse)) {
            booking.setStatus("PAYMENT_FAILED");
            return booking;
        }

        // 2️⃣ Find provider
        ProviderEntity provider =
                providerRepo
                        .findFirstBySkillAndAvailableTrue(
                                booking.getServiceName())
                        .orElseThrow(() ->
                                new RuntimeException("No provider available"));

        provider.setAvailable(false);
        providerRepo.save(provider);

        // 3️⃣ Assign provider
        booking.setProviderName(provider.getName());
        booking.setStatus("ASSIGNED");

        BookingEntity savedBooking =
                bookingRepo.save(booking);

        // 4️⃣ Send email


        restTemplate.postForObject(
                "http://EMAIL-SERVICE/send-email",
                "Dear " + booking.getUserName() + ",\n\n" +
                        "Your service request has been successfully assigned.\n\n" +
                        "Service: " + booking.getServiceName() + "\n" +
                        "Assigned Professional: " + provider.getName() + "\n\n" +
                        "Our service professional will contact you shortly.\n\n" +
                        "Thank you for choosing HomeMate.\n\n" +
                        "Best regards,\n" +
                        "HomeMate Support Team"
                ,
                String.class);

        return savedBooking;
    }

    // ADMIN: View all bookings
    @GetMapping
    public List<BookingEntity> getAllBookings(
            @RequestHeader("X-ROLE") String role) {

        roleValidator.adminOnly(role);
        return bookingRepo.findAll();
    }
}

