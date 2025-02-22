package com.edwinvanderwal.filewatcher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLfSerializer;

@Configuration
public class TcpConfiguration {

    @Value("${tcp.server.port}")
    private int port;

    @Value("${tcp.server.host}")
    private String host;

    @Value("${ledboard.port}")
    private int ledboardport;

    @Value("${ledboard.host}")
    private String ledboardhost;


    @Bean
    public TcpReceivingChannelAdapter demoTcpReceivingChannelAdapter() {
        TcpReceivingChannelAdapter adapter = new TcpReceivingChannelAdapter();
        adapter.setConnectionFactory(prepareTcpNetClientConnectionFactory());
        adapter.setClientMode(true);
        adapter.setOutputChannelName("server-channel");
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "toLedBoard")
    public TcpSendingMessageHandler tcpOut(AbstractClientConnectionFactory connectionFactory) throws Exception {
        TcpSendingMessageHandler sender = new TcpSendingMessageHandler();
        sender.setConnectionFactory(connectionFactory);
        sender.setClientMode(true);
        return sender;
    }

    @Bean
    public AbstractClientConnectionFactory serverCF() {
      return new TcpNetClientConnectionFactory(ledboardhost, ledboardport);
    }
    

    private TcpNetClientConnectionFactory prepareTcpNetClientConnectionFactory(){
        TcpNetClientConnectionFactory factory =
                new TcpNetClientConnectionFactory(host, port);
        factory.setDeserializer(new ByteArrayLfSerializer());
        return factory;
    }

}
