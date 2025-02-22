package com.edwinvanderwal.filewatcher.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Value;
import lombok.Getter;

@Configuration
public class LedBoardConfig {

    @Value("${ledboard.port}")
    @Getter
    private int port;

    @Value("${ledboard.host}")
    @Getter
    private String host;



    

}
