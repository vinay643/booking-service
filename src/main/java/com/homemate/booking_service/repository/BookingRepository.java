package com.homemate.booking_service.repository;

import com.homemate.booking_service.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository
        extends JpaRepository<BookingEntity, Integer> {
}
