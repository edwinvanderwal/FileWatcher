package com.edwinvanderwal.filewatcher;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeelnemerRepo extends JpaRepository<Deelnemer, Long> {

    List<Deelnemer> findByChipcodeIgnoreCase(String chipcode);

}
