package com.edwinvanderwal.filewatcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edwinvanderwal.filewatcher.model.Deelnemer;

@Repository
public interface DeelnemerRepo extends JpaRepository<Deelnemer, Long> {

    List<Deelnemer> findByChipcodeIgnoreCase(String chipcode);

    List<Deelnemer> findByStartnummerIgnoreCase(String startnummer);

}
