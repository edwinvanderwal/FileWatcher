package com.edwinvanderwal.filewatcher.service;

import java.util.LinkedList;
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
    // Add a cache for the last 20 read items
    private final List<String> lastReadCache = new LinkedList<>();
    private static final int CACHE_SIZE = 20;

    public IpicoMessageService(DeelnemerService deelnemerService, LedBoardService ledBoardService) {
        this.deelnemerService = deelnemerService;
        this.ledBoardService = ledBoardService; 
    }

    @ServiceActivator(inputChannel = "server-channel")
    public void consume(byte[] bytes) {
        String chipRead = new String(bytes);   
        
        if (!chipRead.isEmpty() && chipRead.length() > 16) {
            String chipCode = chipRead.substring(4,16);

            if (!getLastReadCache().contains(chipCode)) {
                List<Deelnemer> deelnemers = deelnemerService.getDeelnemerByChipCode(chipCode);
                //log.info("Deelnemers {} gevonden bij {}", deelnemers.size(), chipCode);
                if (!deelnemers.isEmpty()) {
                    ledBoardService.handleMessage(deelnemers.get(0).toString());
                    System.out.println(deelnemers.get(0));
                } else {
                    ledBoardService.handleMessage(chipCode);
                    System.out.println(chipCode);
                }
                // Add to cache
                addToCache(chipCode);
            }
        } else {
            ledBoardService.handleMessage("Start button pressed");
        }

        
        
    }

     // Helper method to maintain a fixed-size cache
    private synchronized void addToCache(String item) {
        if (lastReadCache.size() >= CACHE_SIZE) {
            lastReadCache.remove(0);
        }
        lastReadCache.add(item);
    }

    // Optional: method to get the cache contents
    public synchronized List<String> getLastReadCache() {
        return new LinkedList<>(lastReadCache);
    }

}