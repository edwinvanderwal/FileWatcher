package com.edwinvanderwal.filewatcher.service;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LedBoardService {

    @Value("${ledboard.port}")
    private int port;

    @Value("${ledboard.host}")
    private String host;

    private Socket clientSocket;
    private DataOutputStream out;

    public LedBoardService() {
        try {
            log.debug("Connecting to {} and port", host, port);
            clientSocket = new Socket("10.19.1.60", 21967);
            out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    @ServiceActivator(outputChannel = "toLedBoard")
    public byte[] handleMessage(String msg) {
        try {
            byte[] byteArray = {(byte)0x1b,(byte)0x40,(byte)0x53,(byte)0x00,(byte)0x00,(byte)0x00,
                (byte)0x00,(byte)0x00,(byte)0x31};
            byte[] message = msg.getBytes();    
            byte[] endbyte = {(byte)0x03};
            byte[] messagewithEndBytes = mergeBytes(message, endbyte);

            byte[] merged = mergeBytes(byteArray, messagewithEndBytes);

            byte[] checkSumByte = getCheckSumByte(merged);
            byte[] c = mergeBytes(merged, checkSumByte);
        
            out.write(c);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    // second p;osition was char(32), but also be backspace???
    // FIXME convert to Constants
    protected char[] createStartArray() {
        char[] chars = {(char) 27, (char) 82, (char) 3, (char)16, (char)27, (char) 64, (char) 83, (char) 0, (char) 0,(char) 0,(char) 0,(char) 0,(char) 0};
        return chars;
    }

    // FIXME convert to Constants
    protected char[] createEndArray() {
        char[] chars = {(char) 3};
        return chars;
    }

    /**
     * 7-bit checksum executed for the whole frame:
0x1B+0x40+0x53+0x5A+0x30+0x02+0x4D+0x49+0
x43+0x52+0x4F+0x47+0x41+0x54+0x45+0x03 = 
0x3D8
0x3D8 AND 0x7F = 0x58 
     
     */
    private  int calculateChecksum(String test) {
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
        System.out.println("sum " + sum );
        sum = (byte) (sum & bit7);
        System.out.println("sum " + sum );
        byte[] checkSumBute = {sum};
        return checkSumBute;
    }

}
