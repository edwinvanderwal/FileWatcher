package com.edwinvanderwal.filewatcher.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.PrintWriter;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLfSerializer;

@Configuration
public class LedBoardConfig {

    @Value("${ledboard.port}")
    private int port;

    @Value("${ledboard.host}")
    private String host;

     private Socket clientSocket;
    private PrintWriter out;


    

}
