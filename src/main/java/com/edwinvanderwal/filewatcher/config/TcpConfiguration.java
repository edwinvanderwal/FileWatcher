package com.edwinvanderwal.filewatcher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.ip.tcp.TcpOutboundGateway;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLfSerializer;
import org.springframework.integration.ip.tcp.serializer.ByteArrayStxEtxSerializer;

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

   @Bean
	public AbstractClientConnectionFactory clientFactory() {
		AbstractClientConnectionFactory factory = new TcpNetClientRetryConnectionFactory(tcpProperties);
		//factory.setSerializer(new ByteArrayStxEtxSerializer());
		//factory.setDeserializer(new ByteArrayStxEtxSerializer());
		factory.setLeaveOpen(true);

		return factory;
	}

    @Bean
	@ServiceActivator(inputChannel = MESSAGE_CHANNEL)
	public TcpOutboundGateway outboundGateway(AbstractClientConnectionFactory clientFactory) {
		TcpOutboundGateway outboundGateway = new TcpOutboundGateway();
		outboundGateway.setConnectionFactory(clientFactory);
		outboundGateway.setLoggingEnabled(true);
		outboundGateway.setRequiresReply(false);

		return outboundGateway;
	}

    @MessagingGateway
	public interface TcpClientGateway {

		@Gateway(requestChannel = MESSAGE_CHANNEL, replyTimeout = 1)
		void send(byte[] payload);
	}

    

}
