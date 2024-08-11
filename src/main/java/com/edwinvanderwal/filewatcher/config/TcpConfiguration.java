package com.edwinvanderwal.filewatcher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLfSerializer;

@Configuration
public class TcpConfiguration {

    @Value("${tcp.server.port}")
    private int port;

    @Value("${tcp.server.host}")
    private String host;


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
