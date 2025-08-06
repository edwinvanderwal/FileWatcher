package com.edwinvanderwal.filewatcher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLfSerializer;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableIntegration
public class TcpConfiguration {

    @Value("${tcp.server.port}")
    private int port;

    @Value("${tcp.server.host}")
    private String host;

    @Value("${ledboard.port}")
    private int ledboardport;

    @Value("${ledboard.host}")
    private String ledboardhost;

    private final TcpProperties tcpProperties;

    private static final String MESSAGE_CHANNEL = "message-channel";


    @Bean
    public TcpReceivingChannelAdapter demoTcpReceivingChannelAdapter() {
        TcpReceivingChannelAdapter adapter = new TcpReceivingChannelAdapter();
        adapter.setConnectionFactory(prepareTcpNetClientConnectionFactory());
        adapter.setClientMode(true);
        adapter.setOutputChannelName("server-channel");
        return adapter;
    }

    private TcpNetClientConnectionFactory prepareTcpNetClientConnectionFactory(){
        TcpNetClientConnectionFactory factory =
                new TcpNetClientConnectionFactory(host, port);
            factory.setDeserializer(new ByteArrayLfSerializer());
        return factory;
    }

    

}
