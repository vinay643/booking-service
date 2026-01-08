package com.homemate.booking_service.repository;

import com.homemate.booking_service.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository
        extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByPhone(String phone);
}

