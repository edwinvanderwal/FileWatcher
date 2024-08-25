package com.edwinvanderwal.filewatcher.service;

import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

class DeelnemerServiceTest {
    
    @Test
    void testChecksum() {
        byte[] byteArray = {(byte)0x1b,(byte)0x40,(byte)0x53,(byte)0x5a,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x4d,(byte)0x49,
            (byte)0x43,(byte)0x52,(byte)0x4f,(byte)0x47,(byte)0x41,(byte)0x54,(byte)0x45,(byte)0x03};
        byte bit7 = (byte) 0x7F;
        byte sum = 0;
        System.out.println("hexadecimaal " + Hex.encodeHexString( byteArray ) );
        for (int i = 0; i < byteArray.length; i++) {
        
            sum += byteArray[i];
        }
        System.out.println("sum " + sum );
        sum = (byte) (sum & bit7);

        System.out.println(System.getProperty("file.encoding"));

        assertEquals((byte)0x58, sum);
    }

    @Test
    void testChecksumm() {
        byte[] byteArray = {(byte)0x1b,(byte)0x52,(byte)0x03,(byte)0x10,(byte)0x1b,(byte)0x40,(byte)0x53,(byte)0x00,(byte)0x00,(byte)0x00,
            (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x6d,(byte)0x03};
        byte bit7 = (byte) 0x7F;
        byte sum = 0;
        System.out.println("hexadecimaal " + Hex.encodeHexString( byteArray ) );
        for (int i = 0; i < byteArray.length; i++) {
        
            sum += byteArray[i];
        }
        System.out.println("sum " + sum );
        sum = (byte) (sum & bit7);
        System.out.println("sum " + sum );
        System.out.println(System.getProperty("file.encoding"));

        assertEquals((byte)0x1e, sum);
    }

    @Test
    void testChecksumEdwin() {
        System.setProperty("file.encoding", "Cp1252");
        byte[] byteArray = {(byte)0x1b,(byte)0x52,(byte)0x03,(byte)0x10,(byte)0x1b,(byte)0x40,(byte)0x53,(byte)0x00,(byte)0x00,(byte)0x00,
            (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x65,(byte)0x64,(byte)0x77,(byte)0x69,(byte)0x6e,(byte)0x03};
        byte bit7 = (byte) 0x7F;
        byte sum = 0;
        System.out.println("hexadecimaal " + Hex.encodeHexString( byteArray ) );
        for (int i = 0; i < byteArray.length; i++) {
        
            sum += byteArray[i];
        }
        System.out.println("sum " + sum );
        sum = (byte) (sum & bit7);
        System.out.println("sum " + sum );
        System.out.println(System.getProperty("file.encoding"));

        assertEquals((byte)0x48, sum);
    }
}
