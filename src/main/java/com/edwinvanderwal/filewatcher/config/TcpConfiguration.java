package com.edwinvanderwal.filewatcher.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLfSerializer;
import org.springframework.integration.ip.tcp.serializer.ByteArraySingleTerminatorSerializer;

@Configuration
public class TcpConfiguration {

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
                new TcpNetClientConnectionFactory("localhost", 10000);
        factory.setDeserializer(new ByteArrayLfSerializer());
        return factory;
    }

}
