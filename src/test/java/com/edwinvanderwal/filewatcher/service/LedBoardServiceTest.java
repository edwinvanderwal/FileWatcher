package com.edwinvanderwal.filewatcher.service;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.edwinvanderwal.filewatcher.config.TcpNetClientRetryConnectionFactory;
import com.edwinvanderwal.filewatcher.config.TcpProperties;

@ExtendWith(MockitoExtension.class)
class LedBoardServiceTest {

    @Mock
    TcpProperties tcpProperties;

    @Mock
    TcpNetClientRetryConnectionFactory tcpNetClientRetryConnectionFactory;

    private LedBoardService ledBoardService;
    private ByteArrayOutputStream outputStream;

    private Socket socket;

   @BeforeEach
    void setUp() throws IOException {
        lenient().when(tcpProperties.isSimulation()).thenReturn(false);
        lenient().when(tcpProperties.getServerHost()).thenReturn("localhost");
        lenient().when(tcpProperties.getServerPort()).thenReturn(12345);
        lenient().when(tcpProperties.getConnectionRetryInterval()).thenReturn(10);
        lenient().when(tcpProperties.getWelcomeMessage()).thenReturn("Welcome to the ledboard!");

        outputStream = new ByteArrayOutputStream();
        socket = mock(Socket.class);
        when(socket.getOutputStream()).thenReturn(outputStream);
        when(tcpNetClientRetryConnectionFactory.getSocket()).thenReturn(socket);

        ledBoardService = new LedBoardService(tcpProperties, tcpNetClientRetryConnectionFactory);
    }

    @Test
    void testHandleMessage() throws IOException {
        ledBoardService.handleMessage("Test message");
        assertTrue(outputStream.size() > 0, "Expected the service to write bytes to the socket output stream");
    }

    @Test
    void testSendLionitasLogo() {

    }
}
