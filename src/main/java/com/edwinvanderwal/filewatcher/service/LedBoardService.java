package com.edwinvanderwal.filewatcher.service;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import com.edwinvanderwal.filewatcher.config.TcpNetClientRetryConnectionFactory;
import com.edwinvanderwal.filewatcher.config.TcpProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * Ledboard: 128x32 pixels
 */

@Component
@Slf4j
public class LedBoardService {


    private Socket clientSocket;
    private DataOutputStream out;
    private TcpNetClientRetryConnectionFactory factory;

    private String row0 = "";
    private String row1 = "";
    private String row2 = "";

     public LedBoardService(final TcpProperties tcpProperties) {
        if (!tcpProperties.isSimulation()){
            factory = new TcpNetClientRetryConnectionFactory(tcpProperties);
            connectToSocket();
            handleMessage(tcpProperties.getWelcomeMessage());
        } else {
            System.out.println(tcpProperties.getWelcomeMessage());
        }
    }

    private void connectToSocket() {
        try {
            clientSocket = factory.getSocket();
            out = new DataOutputStream(clientSocket.getOutputStream());
            log.info("Connected to the socket successfully.");
        } catch (IOException e) {
            log.error("Failed to connect to the socket: {}", e.getMessage());
            retryConnection();
        }
    }

    private void retryConnection() {
        int retryCount = 0;
        int maxRetries = 5;
        int retryDelay = 2000; // 2 seconds

        while (retryCount < maxRetries) {
            try {
                log.info("Attempting to reconnect... (Attempt {}/{})", retryCount + 1, maxRetries);
                clientSocket = factory.getSocket();
                out = new DataOutputStream(clientSocket.getOutputStream());
                log.info("Reconnected successfully.");
                handleMessage("We hebben weer verbinding met de ledboard.");
                return;
            } catch (IOException e) {
                retryCount++;
                log.error("Reconnect attempt failed: {}", e.getMessage());
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("Retry interrupted: {}", ie.getMessage());
                    break;
                }
            }
        }

        log.error("Failed to reconnect after {} attempts.", maxRetries);
    }

    public byte[] handleMessage(String msg) {
        try {
            if (out != null) {
                // set rows down
                row2 = row1;
                row1 = row0;
                row0 = msg;

                // reset board
                byte[] c = createByteArray(true, 0, "");
                out.write(c);

                c = createByteArray(false, 0, row0);
                out.write(c);
                c = createByteArray(false, 1, row1);
                out.write(c);
                c = createByteArray(false, 2, row2);
                out.write(c);
            }
        } catch (SocketException e) {
            log.error("SocketException occurred: {}", e.getMessage());
            retryConnection();
        } catch (IOException e) {
            log.error("IOException occurred: {}", e.getMessage());
        }
        return null;
    }

    private byte[] createByteArray(boolean reset, int rijnummer, String messageString) throws IOException {
        byte[] stuurbytes = getStuurbytes(reset, rijnummer);
        byte[] message;
        if (reset) {
            message = "                   ".getBytes();    
        } else {
            message = messageString.getBytes();    
        }
        byte[] endbyte = {(byte)0x03};
        byte[] messagewithEndBytes = mergeBytes(message, endbyte);

        byte[] merged = mergeBytes(stuurbytes, messagewithEndBytes);

        byte[] checkSumByte = getCheckSumByte(merged);
        byte[] c = mergeBytes(merged, checkSumByte);
        return c;
    }


    private byte[] getStuurbytes(boolean reset, int rijnummer) {
        

        byte startFrame = (byte)0x1b;
        byte graphicalBoardIdentifier = (byte)0x40;
        // Fixed text
        byte commandByte = (byte)0x53;
        byte startCoordX1 = (byte)0x00;
        byte startCoordX2 = (byte)0x00;
        byte startCoordY1 = getStartCoordY1(rijnummer);
        byte startCoordY2 = (byte)0x00;
        if (reset) {
            // then coordinates 0,0
            startCoordX1 = (byte)0x00;
            startCoordX2 = (byte)0x00;
            startCoordY1 = (byte)0x00;
            startCoordY2 = (byte)0x00;
        }

        byte binaryOperation = (byte)0x00;
        byte font = getFontSize(reset);

        byte[] stuurbytes = {startFrame,graphicalBoardIdentifier,commandByte,startCoordX1,startCoordX2,startCoordY1,
            startCoordY2,binaryOperation,font};

        return stuurbytes;
    }


    private byte getStartCoordY1(int rijnummer) {
     if (rijnummer == 0) {
        return (byte)0x00;
     } else if (rijnummer == 1) {
        return (byte)0x0A;
     } else if (rijnummer == 2) {
        return (byte)0x14;
     }
     return (byte)0x00;
    }


    private byte getFontSize(boolean reset) {
        if (reset) {
          return (byte)0x33;  
        }
        // small (byte)0x31
        // medium (byte)0x32
        // large (byte)0x33
        return (byte)0x31;
    }


    private byte[] mergeBytes(byte[] byteArray, byte[] checkSumByte) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write( byteArray );
        outputStream.write( checkSumByte );

        byte[] c = outputStream.toByteArray( );
        return c;
    }


    private byte[] getCheckSumByte(byte[] byteArray) {
        byte bit7 = (byte) 0x7F;
        byte sum = 0;
        for (int i = 0; i < byteArray.length; i++) {          
            sum += byteArray[i];
        }
        //System.out.println("sum " + sum );
        sum = (byte) (sum & bit7);
        //System.out.println("sum " + sum );
        byte[] checkSumBute = {sum};
        return checkSumBute;
    }


    /**
     * 7-bit checksum executed for the whole frame:
0x1B+0x40+0x53+0x5A+0x30+0x02+0x4D+0x49+0
x43+0x52+0x4F+0x47+0x41+0x54+0x45+0x03 = 
0x3D8
0x3D8 AND 0x7F = 0x58 
     
     */
    public static int calculateChecksum(String test) {
        byte[] bytes = test.getBytes();
        byte bit7 = (byte) 0x7F;
        byte sum = 0;
        System.out.println("hexadecimaal " + Hex.encodeHexString( bytes ) );
        for (int i = 0; i < bytes.length; i++) {   
            sum += bytes[i];
        }
        System.out.println("sum " + sum );
        sum = (byte) (sum & bit7);
        System.out.println("sum " + sum );
        return sum ;
    }

}
