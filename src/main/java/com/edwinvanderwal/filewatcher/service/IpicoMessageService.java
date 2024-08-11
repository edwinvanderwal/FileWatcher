package com.edwinvanderwal.filewatcher.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import com.edwinvanderwal.filewatcher.Deelnemer;

@MessageEndpoint
public class IpicoMessageService {
     private Logger logger = LoggerFactory.getLogger(IpicoMessageService.class);

    private DeelnemerService deelnemerService;

    public IpicoMessageService(DeelnemerService deelnemerService) {
        this.deelnemerService = deelnemerService;
    }

    @ServiceActivator(inputChannel = "server-channel")
    public void consume(byte[] bytes) {
        String chipRead = new String(bytes);
        logger.info(chipRead);
        if (!chipRead.isEmpty()) {
            String chipCode = chipRead.substring(4,16);
            List<Deelnemer> deelnemers = deelnemerService.getDeelnemerByChipCode(chipCode);
            logger.info(chipCode);
            if (!deelnemers.isEmpty()) {
                logger.info(String.format("%s  %s", deelnemers.get(0).getStartnummer(), deelnemers.get(0).getNaam()));
            }
        }
        
    }

}