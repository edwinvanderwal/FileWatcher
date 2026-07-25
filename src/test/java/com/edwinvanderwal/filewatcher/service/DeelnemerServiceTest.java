package com.edwinvanderwal.filewatcher.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.edwinvanderwal.filewatcher.model.Deelnemer;
import com.edwinvanderwal.filewatcher.repository.DeelnemerRepo;

@ExtendWith(MockitoExtension.class)
class DeelnemerServiceTest {

    @Mock
    private DeelnemerRepo deelnemerRepo;

    @InjectMocks
    private DeelnemerService deelnemerService;

    @Test
    void testSaveDeelnemer() {
        Deelnemer deelnemer = new Deelnemer();
        deelnemer.setReferentie(12343L);
        deelnemerService.save(deelnemer);
        verify(deelnemerRepo, times(1)).save(deelnemer);
    }

    @Test
    void testSaveDeelnemerWithNullReferentie() {
        Deelnemer deelnemer = new Deelnemer();
        deelnemer.setReferentie(null);
        deelnemerService.save(deelnemer);
        verify(deelnemerRepo, times(0)).save(deelnemer);
    }

    @Test
    void testSaveDeelnemerNull() {
        Deelnemer deelnemer = null;
        deelnemerService.save(deelnemer);
        verify(deelnemerRepo, times(0)).save(deelnemer);
    }


    @Test
    void testGetDeelnemerByChipCode() {
        Deelnemer deelnemer = new Deelnemer();
        deelnemer.setChipcode("12343e11");
        deelnemerService.getDeelnemerByChipCode("12343e11");
        verify(deelnemerRepo, times(1)).findByChipcodeIgnoreCase("12343e11");
    }
    
    @Test
    void testGetDeelnemerByStartnummer() {
        Deelnemer deelnemer = new Deelnemer();
        deelnemer.setStartnummer("123");
        deelnemerService.getDeelnemerByStartnummer("123");
        verify(deelnemerRepo, times(1)).findByStartnummerIgnoreCase("123");
    }


}
