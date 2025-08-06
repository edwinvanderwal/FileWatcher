package com.edwinvanderwal.filewatcher.config;

import java.io.IOException;
import java.net.Socket;

import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.lang.NonNull;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TcpNetClientRetryConnectionFactory extends TcpNetClientConnectionFactory {

	private final TcpProperties tcpProperties;

	public TcpNetClientRetryConnectionFactory(TcpProperties tcpProperties) {
		super(tcpProperties.getServerHost(), tcpProperties.getServerPort());
		this.tcpProperties = tcpProperties;
	}

	public Socket getSocket(){
		try {
			return createSocket(tcpProperties.getServerHost(), tcpProperties.getServerPort());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected @NonNull Socket createSocket(@NonNull String host, int port) throws IOException {
		Socket socket = null;

		try {
			socket = super.createSocket(host, port);
		} catch (Exception e) {
			log.warn("retrying connection");

			try {
				Thread.sleep(tcpProperties.getConnectionRetryInterval());
			} catch (InterruptedException ex) {
				log.error("thread killed");
			}

			socket = this.createSocket(host, port);
		}

		log.debug("connection established");
		return socket;
	}

}