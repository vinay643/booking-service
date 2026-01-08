package com.homemate.booking_service.repository;

import com.homemate.booking_service.entity.ProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderRepository
        extends JpaRepository<ProviderEntity, Integer> {

    Optional<ProviderEntity>
    findFirstBySkillAndAvailableTrue(String skill);
}
