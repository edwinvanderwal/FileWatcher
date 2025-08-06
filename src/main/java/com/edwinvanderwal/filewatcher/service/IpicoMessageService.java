package com.edwinvanderwal.filewatcher.service;

import java.util.List;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import com.edwinvanderwal.filewatcher.Deelnemer;

import lombok.extern.slf4j.Slf4j;

/**
 * Example chipcode                                  aa0005800385e07f00012408141913055def
 *         fixed prefix (per Ipico machine?)         aa00 
 *         chipcode                                      05800385e07f
 *         identification of mat?                                    0001          
 *         date                                                          240814
 *         time                                                                1913055
 *         checksum? (sum al bits from 2 to 34 ?)                                     def   
 * 
 *    See https://stackoverflow.com/questions/56602415/how-to-dynamically-create-multiple-tcpoutboundgateways-using-spring-integration
 * 
 */ 

@MessageEndpoint
@Slf4j
public class IpicoMessageService {

    private DeelnemerService deelnemerService;

    private LedBoardService ledBoardService;

    public IpicoMessageService(DeelnemerService deelnemerService, LedBoardService ledBoardService) {
        this.deelnemerService = deelnemerService;
        this.ledBoardService = ledBoardService; 
    }

    @ServiceActivator(inputChannel = "server-channel")
    public void consume(byte[] bytes) {
        String chipRead = new String(bytes);
        //logger.info(chipRead);
        // FIXME startbutton pressed the string is shorter.
        if (!chipRead.isEmpty() && chipRead.length() > 16) {
            String chipCode = chipRead.substring(4,16);
            List<Deelnemer> deelnemers = deelnemerService.getDeelnemerByChipCode(chipCode);
            //log.info("Deelnemers {} gevonden bij {}", deelnemers.size(), chipCode);
            if (!deelnemers.isEmpty()) {
                ledBoardService.handleMessage(deelnemers.get(0).toString());
                System.out.println(deelnemers.get(0));
            } else {
                ledBoardService.handleMessage(chipCode);
                System.out.println(chipCode);
            }
        } else {
            ledBoardService.handleMessage("Start button pressed");
        }
        
    }

}