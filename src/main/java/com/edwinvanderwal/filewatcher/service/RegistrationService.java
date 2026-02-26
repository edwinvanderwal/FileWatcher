package com.edwinvanderwal.filewatcher.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

import com.edwinvanderwal.filewatcher.model.Registration;
import com.edwinvanderwal.filewatcher.repository.RegistrationRepo;

import jakarta.transaction.Transactional;

@Component
public class RegistrationService {
    
    private RegistrationRepo registrationRepo;

    public RegistrationService(RegistrationRepo registrationRepo) {
        this.registrationRepo = registrationRepo;
    }

    @Transactional
    @CachePut(cacheNames="registrations", key="#registration.id")
    @CacheEvict(cacheNames="customersSearch", allEntries=true)
    public Registration save(Registration registration) {
        return registrationRepo.save(registration);
    }

    public List<Registration> findAllOrderByRegistrationTimeDesc() {
        return registrationRepo.findAllOrderByRegistrationTimeDesc();
    }
}
