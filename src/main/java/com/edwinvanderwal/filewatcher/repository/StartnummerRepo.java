package com.edwinvanderwal.filewatcher.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edwinvanderwal.filewatcher.model.Startnummer;

@Repository
public interface StartnummerRepo extends JpaRepository<Startnummer, String>{

    List<Startnummer> findByChipcodeIgnoreCase(String chipcode);
}
