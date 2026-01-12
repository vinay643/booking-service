package com.homemate.booking_service.controller;

import com.homemate.booking_service.entity.BookingEntity;
import com.homemate.booking_service.entity.ProviderEntity;
import com.homemate.booking_service.entity.UserEntity;
import com.homemate.booking_service.repository.BookingRepository;
import com.homemate.booking_service.repository.ProviderRepository;
import com.homemate.booking_service.repository.UserRepository;
import com.homemate.booking_service.util.RoleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private ProviderRepository providerRepo;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RoleValidator roleValidator;


    // ================= CREATE BOOKING =================
    @PostMapping
    public BookingEntity createBooking(
            @RequestHeader("X-ROLE") String role,
            @RequestBody BookingEntity booking) {

        roleValidator.userOnly(role);
        // ✅ 1️⃣ USER ENTITY SE DATA UTHAO
        UserEntity user = userRepository.findByName(booking.getUserName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // ✅ 2️⃣ EMAIL & PHONE SET KARO
        booking.setEmail(user.getEmail());
        booking.setPhone(user.getPhone());

        // ---------------- 1️⃣ BOOKING REQUESTED ----------------
        booking.setStatus("REQUESTED");

        // 🔔 EMAIL (REQUESTED)
        String requestedEmail =
                "Dear " + booking.getUserName() + ",\n\n" +
                        "We have received your service request successfully.\n\n" +
                        "Service: " + booking.getServiceName() + "\n" +
                        "Status: Booking Requested\n\n" +
                        "Our team will assign a service professional shortly.\n\n" +
                        "Thank you for choosing HomeMate.\n\n" +
                        "Best regards,\n" +
                        "HomeMate Support Team "+booking.getEmail();


        restTemplate.postForObject(
                "http://EMAIL-SERVICE/send-email",
               requestedEmail,
                String.class
        );

//        // 🔔 SMS (REQUESTED)
//        String requestedSms =
//                "Hi " + booking.getUserName() + ", we have received your " +
//                        booking.getServiceName() +
//                        " service request. Our team will assign a professional shortly. - HomeMate";
//
//        restTemplate.postForObject(
//                "http://SMS-SERVICE/send-sms?phone=" + booking.getPhone(),
//                requestedSms,
//                String.class
//        );

        // ---------------- 2️⃣ PAYMENT ----------------
        String paymentResponse =
                restTemplate.getForObject(
                        "http://PAYMENT-SERVICE/pay",
                        String.class);

        if (!"PAYMENT_SUCCESS".equals(paymentResponse)) {
            booking.setStatus("PAYMENT_FAILED");
            return booking;
        }

        // ---------------- 3️⃣ ASSIGN PROVIDER ----------------
        ProviderEntity provider = providerRepo
                .findFirstBySkillAndAvailableTrue(booking.getServiceName())
                .orElseThrow(() ->
                        new RuntimeException("No provider available"));

        provider.setAvailable(false);
        providerRepo.save(provider);

        booking.setProviderName(provider.getName());
        booking.setStatus("ASSIGNED");

        BookingEntity savedBooking = bookingRepo.save(booking);

        // 🔔 EMAIL (ASSIGNED)
        String assignedEmail =
                "Dear " + booking.getUserName() + ",\n\n" +
                        "Your service request has been successfully assigned.\n\n" +
                        "Service: " + booking.getServiceName() + "\n" +
                        "Assigned Professional: " + provider.getName() + "\n\n" +
                        "Our service professional will contact you shortly.\n\n" +
                        "Thank you for choosing HomeMate.\n\n" +
                        "Best regards,\n" +
                        "HomeMate Support Team";

        restTemplate.postForObject(
                "http://EMAIL-SERVICE/send-email",
                assignedEmail,
                String.class
        );

        // 🔔 SMS (ASSIGNED)
//        String assignedSms =
//                "Hi " + booking.getUserName() + ", your " +
//                        booking.getServiceName() +
//                        " service has been assigned to " +
//                        provider.getName() + ". - HomeMate";
//
//        restTemplate.postForObject(
//                "http://SMS-SERVICE/send-sms?phone=" + booking.getPhone(),
//                assignedSms,
//                String.class
//        );

        return savedBooking;
    }

    // ================= COMPLETE BOOKING =================
    @PutMapping("/{id}/complete")
    public BookingEntity completeBooking(
            @RequestHeader("X-ROLE") String role,
            @PathVariable Integer id) {

        roleValidator.adminOnly(role);

        BookingEntity booking = bookingRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found"));

        booking.setStatus("COMPLETED");
        BookingEntity savedBooking = bookingRepo.save(booking);

        // 🔔 EMAIL (COMPLETED)
        String completedEmail =
                "Dear " + booking.getUserName() + ",\n\n" +
                        "We are pleased to inform you that your service request has been successfully completed.\n\n" +
                        "Service: " + booking.getServiceName() + "\n" +
                        "Status: Completed\n\n" +
                        "Thank you for choosing HomeMate.\n\n" +
                        "Best regards,\n" +
                        "HomeMate Support Team";

        restTemplate.postForObject(
                "http://EMAIL-SERVICE/send-email",
                completedEmail,
                String.class
        );

        // 🔔 SMS (COMPLETED)
//        String completedSms =
//                "Hi " + booking.getUserName() + ", your " +
//                        booking.getServiceName() +
//                        " service has been completed successfully. Thank you for choosing HomeMate.";
//
//        restTemplate.postForObject(
//                "http://SMS-SERVICE/send-sms?phone=" + booking.getPhone(),
//                completedSms,
//                String.class
//        );

        return savedBooking;
    }

    // ================= VIEW ALL BOOKINGS (ADMIN) =================
    @GetMapping
    public List<BookingEntity> getAllBookings(
            @RequestHeader("X-ROLE") String role) {

        roleValidator.adminOnly(role);
        return bookingRepo.findAll();
    }
    // ================= USER: VIEW OWN BOOKINGS (READ-ONLY) =================
    @GetMapping("/my")
    public List<BookingEntity> getMyBookings(
            @RequestHeader("X-ROLE") String role,
            @RequestHeader("X-USER") String userName) {

        roleValidator.userOnly(role);

        // ONLY fetch — no save/update/delete here
        return bookingRepo.findByUserName(userName);
    }

}
