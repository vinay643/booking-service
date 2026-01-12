package com.homemate.booking_service.repository;

import com.homemate.booking_service.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository
        extends JpaRepository<BookingEntity, Integer> {
    List<BookingEntity> findByUserName(String userName);
}
