package com.edwinvanderwal.filewatcher.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.edwinvanderwal.filewatcher.model.Startnummer;
import com.edwinvanderwal.filewatcher.repository.StartnummerRepo;

@Component
public class TagmapService {
    
    private StartnummerRepo startnummerRepo;

    public TagmapService(StartnummerRepo startnummerRepo) {
        this.startnummerRepo = startnummerRepo;
    }

    @Transactional
    @CachePut(cacheNames="deelnemers", key="#deelnemer.id")
    @CacheEvict(cacheNames="customersSearch", allEntries=true)
    public Startnummer save(Startnummer startnummer) {
        return startnummerRepo.save(startnummer);
    }

    public List<Startnummer> getStartnummerByChipCode(String chipcode) {
        return startnummerRepo.findByChipcodeIgnoreCase(chipcode);
    }



}
