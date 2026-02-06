package com.edwinvanderwal.filewatcher.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.edwinvanderwal.filewatcher.file.JsonFileProcessor;
import com.edwinvanderwal.filewatcher.model.Deelnemer;
import com.edwinvanderwal.filewatcher.repository.DeelnemerRepo;

@Component
public class DeelnemerService {

    private static Logger logger = LoggerFactory.getLogger(DeelnemerService.class);

    private DeelnemerRepo deelnemerRepo;

    public DeelnemerService(DeelnemerRepo deelnemerRepo) {
        this.deelnemerRepo = deelnemerRepo;
    }

    @Transactional
    @CachePut(cacheNames="deelnemers", key="#deelnemer.id")
    @CacheEvict(cacheNames="customersSearch", allEntries=true)
    public Deelnemer save(Deelnemer deelnemer) {
        if (deelnemer == null || deelnemer.getReferentie() == null) {
            logger.error("Deelnemer mag niet null zijn");
            return null;
        }
        return deelnemerRepo.save(deelnemer);
    }

    public List<Deelnemer> getDeelnemerByChipCode(String chipcode) {
        return deelnemerRepo.findByChipcodeIgnoreCase(chipcode);
    }

}
