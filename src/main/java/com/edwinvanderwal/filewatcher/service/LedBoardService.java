package com.edwinvanderwal.filewatcher.service;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.edwinvanderwal.filewatcher.Constants;
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

    @Autowired
    public LedBoardService(final TcpProperties tcpProperties) {
         this(tcpProperties, null);
    }

    LedBoardService(final TcpProperties tcpProperties, final TcpNetClientRetryConnectionFactory factory) {
        if (!tcpProperties.isSimulation()){
            this.factory = factory != null ? factory : new TcpNetClientRetryConnectionFactory(tcpProperties);
            connectToSocket();
            handleMessage(tcpProperties.getWelcomeMessage());
            sendLionitasLogo();
            //sendRunpointLogo();
            //sendFrieslandCampinaLogo();
            //sendFysio058Logo();
        } else {
            log.info(tcpProperties.getWelcomeMessage());
        }
    }


    private void connectToSocket() {
        try {
            clientSocket = factory.getSocket();
            out = new DataOutputStream(clientSocket.getOutputStream());
            log.info("Connected to the socket successfully with IP {} and my Own IP address is {}.", clientSocket.getInetAddress(), clientSocket.getLocalAddress());
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

    private void resetBoard() {
        try {
            if (out != null) {
                for (int i = 0; i < 3; i++) {
                    byte[] c = createByteArray(true, i, "");
                    out.write(c);
                }
            }
        } catch (SocketException e) {
            log.error("SocketException occurred: {}", e.getMessage());
            retryConnection();
        } catch (IOException e) {
            log.error("IOException occurred: {}", e.getMessage());
        }
    }

    public void handleMessage(String msg) {
        try {
            if (out != null) {
                // set rows down
                row2 = row1;
                row1 = row0;
                row0 = msg;

                resetBoard();

                byte[] c = createByteArray(false, 0, row0);
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
        // Fixed text, command S
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

       return outputStream.toByteArray( );
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

    // Send the bundled LIONITAS logo bitmap to the ledboard
    public void sendLionitasLogo() {
        sendLogo(Constants.LIONITAS_LOGO_BITMAP);
    }

    // Send the bundled LIONITAS logo bitmap to the ledboard
    public void sendRunpointLogo() {
        sendLogo(Constants.RUNPOINT_LOGO_BITMAP);
    }
    
    private void sendFrieslandCampinaLogo() {
        sendLogo(Constants.FRIESLANDCAMPINA_LOGO_BITMAP_128x32);
    }

    private void sendFysio058Logo() {
        sendLogo(Constants.FYSIO058_LOGO_BITMAP_128x32);
    }

    private void sendLogo(byte[] bitmap) {
        try {
            resetBoard();
            if (out != null) {
                byte[] frame = createImageFrame(bitmap, 128, 32);
                out.write(frame);
                out.flush();
                log.info("Sent logo bitmap ({} bytes)", frame.length);
            } else {
                log.warn("Output stream is null; cannot send bitmap.");
            }
        } catch (SocketException e) {
            log.error("SocketException while sending bitmap: {}", e.getMessage());
            retryConnection();
        } catch (IOException e) {
            log.error("IOException while sending bitmap: {}", e.getMessage());
        }
    }

    /**
     * The ledboard expects the bitmap data to be sent in a specific order. This method converts the bitmap from a linear array to the required format.
     * Ledboard expects all bits of the first column, then all bits of the second column, and so on. The bitmap is 128x32 pixels, which means it has 128 columns and 32 rows. Each byte in the bitmap represents 8 vertical pixels (1 bit per pixel). The method rearranges the bytes accordingly.
     * @param bitmap
     * @return
     */
    private byte[] convertBitmapToByteArray(byte[] bitmap) {
        byte[] byteArray = new byte[bitmap.length];
        for (int i = 0; i < 128; i++) {   
            byteArray[i*4] = bitmap[i];
            byteArray[i*4+1] = bitmap[i+128];
            byteArray[i*4+2] = bitmap[i+256];
            byteArray[i*4+3] = bitmap[i+384];
        }
        return byteArray;
    }

    private byte[] createImageFrame(byte[] bitmap, int width, int height) throws IOException {
        byte startFrame = (byte)0x1b;
        byte graphicalBoardIdentifier = (byte)0x40;
        // Fixed text
        byte commandByte = (byte) 0x49; // 'I' = insert image
        byte startCoordX1 = (byte)0x00;
        byte startCoordX2 = (byte)0x00;
        byte startCoordY1 = (byte)0x00;
        byte startCoordY2 = (byte)0x00;
        byte binaryOperation = (byte)0x00;
        byte font = getFontSize(true);

        byte xLow = (byte)0x80; // start with with and height of 1,1 after that uncomment and test with 128,32
        //byte xLow = (byte)0x01;
        byte xHigh = (byte)0x00;
        //byte yLow = (byte)0x08;
       byte yLow = (byte)0x20; // start with with and height of 1,1 after that uncomment and test with 128,32
        byte yHigh = (byte)0x00;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(startFrame);
        outputStream.write(graphicalBoardIdentifier);
        outputStream.write(commandByte);
        outputStream.write(startCoordX1);
        outputStream.write(startCoordX2);       
        outputStream.write(startCoordY1);
        outputStream.write(startCoordY2);
        outputStream.write(binaryOperation);
        outputStream.write(font);
        outputStream.write(xLow);
        outputStream.write(xHigh);
        outputStream.write(yLow);
        outputStream.write(yHigh);
        outputStream.write(convertBitmapToByteArray(bitmap));
        // end-of-frame byte
        outputStream.write((byte) 0x03);

        byte[] withoutChecksum = outputStream.toByteArray();
        byte[] checksum = getCheckSumByte(withoutChecksum);

        // append checksum
        return mergeBytes(withoutChecksum, checksum);
    }

    // TODO: add method to insert images to the leadboard
    /* This command is used to display Bitmap images on the graphical display board. Each data bit placed at ‘1’
corresponds to a lit pixel of the image. The image is scanned vertically, sending one column at a time,
aligned to the byte. No compression is provided
Command code 
‘I’ 
Data area
Item 
Length (bytes) 
Notes 
X Dimension 
2 
Horizontal image dimension, in pixels 
Y Dimension 
2 
Vertical image dimension, in pixels 
Image data 
? 
Each pixel column is sent starting from the top. The
Least Significant bit is the highest pixel. The last byte
of the column is padded with zeroes, if the vertical
image size is different from n*8.  

Resolution of the board is 128x32
Command Insert images 
I 
Dec. 73 – Hex 49h 
*/



}
