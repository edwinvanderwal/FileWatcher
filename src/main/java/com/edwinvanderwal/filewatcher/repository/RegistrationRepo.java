package com.edwinvanderwal.filewatcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.edwinvanderwal.filewatcher.model.Registration;

@Repository
public interface RegistrationRepo extends JpaRepository<Registration, Long> {

    @Query("select a from Registration a order by a.registrationTime desc")
    List<Registration> findAllOrderByRegistrationTimeDesc();

}