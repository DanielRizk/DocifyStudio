package com.daniel.docify.fileProcessor;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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

    public void writeStringFieldMappingToStream(Integer tag, String message) throws IOException {
        // Convert the tag to bytes - assuming 4 bytes for an Integer
        ByteBuffer tagBuffer = ByteBuffer.allocate(4);
        tagBuffer.putInt(tag);
        write(tagBuffer.array());


        byte[] messageBytes;
        ByteBuffer lengthBuffer;
        // Write the message length
        messageBytes = Objects.requireNonNullElse(message, "").getBytes(StandardCharsets.UTF_8);
        lengthBuffer = ByteBuffer.allocate(4);
        lengthBuffer.putInt(messageBytes.length);
        write(lengthBuffer.array());

        // Write the message bytes
        write(messageBytes);

        // Write a newline character
        write(System.lineSeparator().getBytes());
    }

    public void writeIntFieldMappingToStream(Integer tag, Integer message) throws IOException {
        // Convert the tag to bytes - assuming 4 bytes for an Integer
        ByteBuffer tagBuffer = ByteBuffer.allocate(4);
        tagBuffer.putInt(tag);
        write(tagBuffer.array());

        ByteBuffer msgBuffer = ByteBuffer.allocate(4);
        msgBuffer.putInt(message);
        write(msgBuffer.array());

        // Write a newline character
        write(System.lineSeparator().getBytes());
    }

    public void writeBoolFieldMappingToStream(Integer tag, boolean message) throws IOException {
        // Convert the tag to bytes - assuming 4 bytes for an Integer
        ByteBuffer tagBuffer = ByteBuffer.allocate(4);
        tagBuffer.putInt(tag);
        write(tagBuffer.array());

        // Write the message bytes
        String boolToString;
        if (message){
            boolToString = "true";
        }else {
            boolToString = "false";
        }
        byte[] messageBytes = boolToString.getBytes();
        write(messageBytes);

        // Write a newline character
        write(System.lineSeparator().getBytes());
    }
}
