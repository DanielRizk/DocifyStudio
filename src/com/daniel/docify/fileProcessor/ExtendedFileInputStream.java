package com.daniel.docify.fileProcessor;


import java.io.*;
import java.nio.charset.StandardCharsets;

import static com.daniel.docify.fileProcessor.DociSerializable.ENCRYPTION_KEY;
/**
 * if you ever want to remove the decryption, remove all readDecrypted call
 * i.g. tagBuffer = readDecrypted(tagBuffer); -> delete the whole line
 */
public class ExtendedFileInputStream extends FileInputStream {
    private final BufferedInputStream bufferedIn;

    public ExtendedFileInputStream(String filePath) throws FileNotFoundException {
        super(filePath);
        this.bufferedIn = new BufferedInputStream(this);
    }

    public ExtendedFileInputStream(File file) throws FileNotFoundException {
        super(file);
        this.bufferedIn = new BufferedInputStream(this);
    }

    public Integer readValidationKey() throws IOException {
        byte[] tagBuffer = new byte[4];
        long read = bufferedIn.read(tagBuffer);
        if (read < 4) { // End of stream or incomplete tag
            return null;
        }

        return ((tagBuffer[0] & 0xFF) << 24) | ((tagBuffer[1] & 0xFF) << 16)
                | ((tagBuffer[2] & 0xFF) << 8) | (tagBuffer[3] & 0xFF);
    }

    private byte[] readDecrypted(byte[] byteArray) {
        byte[] result = new byte[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            // Cast result to byte, truncates towards zero
            result[i] = (byte)(byteArray[i] - ENCRYPTION_KEY);
        }
        return result;
    }

    public TagDataPair readStringFieldMappingFromStream() throws IOException {
        byte[] tagBuffer = new byte[4];
        long read = bufferedIn.read(tagBuffer);
        if (read < 4) { // End of stream or incomplete tag
            return null;
        }

        tagBuffer = readDecrypted(tagBuffer);

        int tag = ((tagBuffer[0] & 0xFF) << 24) | ((tagBuffer[1] & 0xFF) << 16)
                | ((tagBuffer[2] & 0xFF) << 8) | (tagBuffer[3] & 0xFF);

        // Read the message length
        byte[] lengthBuffer = new byte[4];
        read = bufferedIn.read(lengthBuffer);
        if (read < 4) {
            return null; // Incomplete length or end of stream
        }

        lengthBuffer = readDecrypted(lengthBuffer);

        int length = ((lengthBuffer[0] & 0xFF) << 24) | ((lengthBuffer[1] & 0xFF) << 16)
                | ((lengthBuffer[2] & 0xFF) << 8) | (lengthBuffer[3] & 0xFF);

        byte[] messageBytes = new byte[length];
        int totalRead = 0;
        while (totalRead < length) {
            int currentRead = bufferedIn.read(messageBytes, totalRead, length - totalRead);
            if (currentRead == -1) { // End of stream
                break;
            }
            totalRead += currentRead;
        }

        if (totalRead < length) {
            return null; // Incomplete message data or end of stream
        }

        messageBytes = readDecrypted(messageBytes);

        // Optionally read and discard the line separator if it's consistently included after each message
        bufferedIn.read(new byte[System.lineSeparator().getBytes().length]);

        String data = new String(messageBytes, StandardCharsets.UTF_8);
        if (data.isEmpty()) data = null;

        return new TagDataPair(tag, data);
    }

    public TagDataPair readIntFieldMappingFromStream() throws IOException {
        byte[] tagBuffer = new byte[4];
        int readTag = bufferedIn.read(tagBuffer);
        if (readTag < 4) { // End of stream or incomplete tag
            return null;
        }

        tagBuffer = readDecrypted(tagBuffer);

        int tag = ((tagBuffer[0] & 0xFF) << 24) | ((tagBuffer[1] & 0xFF) << 16)
                | ((tagBuffer[2] & 0xFF) << 8) | (tagBuffer[3] & 0xFF);

        byte[] dataBuffer = new byte[4];
        int readInt = bufferedIn.read(dataBuffer);
        if (readInt < 4) { // End of stream or incomplete tag
            return null;
        }

        dataBuffer = readDecrypted(dataBuffer);

        int data = ((dataBuffer[0] & 0xFF) << 24) | ((dataBuffer[1] & 0xFF) << 16)
                | ((dataBuffer[2] & 0xFF) << 8) | (dataBuffer[3] & 0xFF);

        // Skip the next two bytes -> newline character
        long skipped = bufferedIn.skip(2);
        if (skipped < 2) { // Failed to skip the required bytes

            return null; // Example handling
        }

        return new TagDataPair(tag, data);
    }

    public TagDataPair readBoolFieldMappingFromStream() throws IOException {
        byte[] tagBuffer = new byte[4];
        int read = bufferedIn.read(tagBuffer);
        if (read < 4) { // End of stream or incomplete tag
            return null;
        }

        tagBuffer = readDecrypted(tagBuffer);

        int tag = ((tagBuffer[0] & 0xFF) << 24) | ((tagBuffer[1] & 0xFF) << 16)
                | ((tagBuffer[2] & 0xFF) << 8) | (tagBuffer[3] & 0xFF);

        StringBuilder dataBuilder = new StringBuilder();
        int ch;
        while ((ch = bufferedIn.read()) != -1) {
            // Check for line end (considering both \n and \r\n)
            ch = ch- ENCRYPTION_KEY;
            if (ch == '\n') {
                break;
            }
            // Do not append carriage return to data
            if (ch != '\r') {
                dataBuilder.append((char) ch);
            }
        }

        boolean data = dataBuilder.toString().equals("true");

        return new TagDataPair(tag, data);
    }

    @Override
    public void close() throws IOException {
        bufferedIn.close(); // Ensure the buffered stream is closed
    }
}

