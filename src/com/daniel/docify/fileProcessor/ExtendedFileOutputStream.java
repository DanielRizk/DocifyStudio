package com.daniel.docify.fileProcessor;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.daniel.docify.fileProcessor.DociSerializable.ENCRYPTION_KEY;
/**
 * if you ever want to remove the encryption, replace all writeEncrypted call with write
 */
public class ExtendedFileOutputStream extends FileOutputStream {
    private final String filePath;

    public ExtendedFileOutputStream(String filePath) throws IOException {
        super(filePath);
        this.filePath = filePath;
    }

    public ExtendedFileOutputStream(File file) throws IOException {
        super(file);
        this.filePath = file.getAbsolutePath();
    }

    public String getFilePath() {
        return filePath;
    }

    public void writeValidationKey(Integer key) throws IOException {
        ByteBuffer tagBuffer = ByteBuffer.allocate(4);
        tagBuffer.putInt(key);
        write(tagBuffer.array());
    }

    // Method to multiply each byte in the array by a constant
    private void writeEncrypted(byte[] byteArray) throws IOException {
        byte[] result = new byte[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            // Cast result to byte to handle overflow, might need a different approach based on your needs
            result[i] = (byte)(byteArray[i] + ENCRYPTION_KEY);
        }
        write(result);
    }

    public void writeStringFieldMappingToStream(Integer tag, String message) throws IOException {
        // Convert the tag to bytes - assuming 4 bytes for an Integer
        ByteBuffer tagBuffer = ByteBuffer.allocate(4);
        tagBuffer.putInt(tag);
        writeEncrypted(tagBuffer.array());


        byte[] messageBytes;
        ByteBuffer lengthBuffer;
        // Write the message length
        messageBytes = Objects.requireNonNullElse(message, "").getBytes(StandardCharsets.UTF_8);
        lengthBuffer = ByteBuffer.allocate(4);
        lengthBuffer.putInt(messageBytes.length);
        writeEncrypted(lengthBuffer.array());

        // Write the message bytes
        writeEncrypted(messageBytes);

        // Write a newline character
        writeEncrypted(System.lineSeparator().getBytes());
    }

    public void writeIntFieldMappingToStream(Integer tag, Integer message) throws IOException {
        // Convert the tag to bytes - assuming 4 bytes for an Integer
        ByteBuffer tagBuffer = ByteBuffer.allocate(4);
        tagBuffer.putInt(tag);
        writeEncrypted(tagBuffer.array());

        ByteBuffer msgBuffer = ByteBuffer.allocate(4);
        msgBuffer.putInt(message);
        writeEncrypted(msgBuffer.array());

        // Write a newline character
        writeEncrypted(System.lineSeparator().getBytes());
    }

    public void writeBoolFieldMappingToStream(Integer tag, boolean message) throws IOException {
        // Convert the tag to bytes - assuming 4 bytes for an Integer
        ByteBuffer tagBuffer = ByteBuffer.allocate(4);
        tagBuffer.putInt(tag);
        writeEncrypted(tagBuffer.array());

        // Write the message bytes
        String boolToString;
        if (message){
            boolToString = "true";
        }else {
            boolToString = "false";
        }
        byte[] messageBytes = boolToString.getBytes();
        writeEncrypted(messageBytes);

        // Write a newline character
        writeEncrypted(System.lineSeparator().getBytes());
    }
}
