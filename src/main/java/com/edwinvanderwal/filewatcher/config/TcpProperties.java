package com.edwinvanderwal.filewatcher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class TcpProperties {

    @Value("${ledboard.host}")
	private String serverHost;

	@Value("${ledboard.port}")
	private int serverPort;

	@Value("${connection.retry.interval:10}")
	private int connectionRetryInterval;

	@Value("${ledboard.welcomeMessage:Welkom hardlopers!}")
    private String welcomeMessage;

	@Value("${ledboard.simulation:false}")
	private boolean simulation;
}
