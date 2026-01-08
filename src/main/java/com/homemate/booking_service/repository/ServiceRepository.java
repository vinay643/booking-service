package com.homemate.booking_service.repository;

import com.homemate.booking_service.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository
        extends JpaRepository<ServiceEntity, Integer> {
}

