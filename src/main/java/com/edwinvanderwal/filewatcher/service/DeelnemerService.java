package com.edwinvanderwal.filewatcher.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.edwinvanderwal.filewatcher.model.Deelnemer;
import com.edwinvanderwal.filewatcher.repository.DeelnemerRepo;

@Component
public class DeelnemerService {

    private DeelnemerRepo deelnemerRepo;

    public DeelnemerService(DeelnemerRepo deelnemerRepo) {
        this.deelnemerRepo = deelnemerRepo;
    }

    @Transactional
    @CachePut(cacheNames="deelnemers", key="#deelnemer.id")
    @CacheEvict(cacheNames="customersSearch", allEntries=true)
    public Deelnemer save(Deelnemer deelnemer) {
        return deelnemerRepo.save(deelnemer);
    }

    public List<Deelnemer> getDeelnemerByChipCode(String chipcode) {
        return deelnemerRepo.findByChipcodeIgnoreCase(chipcode);
    }

}
