package com.daniel.docify.fileProcessor;


import java.io.*;

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

    public TagDataPair readStringFieldMappingFromStream() throws IOException {
        byte[] tagBuffer = new byte[4];
        int read = bufferedIn.read(tagBuffer);
        if (read < 4) { // End of stream or incomplete tag
            return null;
        }

        int tag = ((tagBuffer[0] & 0xFF) << 24) | ((tagBuffer[1] & 0xFF) << 16)
                | ((tagBuffer[2] & 0xFF) << 8) | (tagBuffer[3] & 0xFF);

        StringBuilder dataBuilder = new StringBuilder();
        int ch;
        while ((ch = bufferedIn.read()) != -1) {
            // Check for line end (considering both \n and \r\n)
            if (ch == '\n') {
                break;
            }
            // Do not append carriage return to data
            if (ch != '\r') {
                dataBuilder.append((char) ch);
            }
        }

        return new TagDataPair(tag, dataBuilder.toString());
    }

    public TagDataPair readIntFieldMappingFromStream() throws IOException {
        byte[] tagBuffer = new byte[4];
        int readTag = bufferedIn.read(tagBuffer);
        if (readTag < 4) { // End of stream or incomplete tag
            return null;
        }

        int tag = ((tagBuffer[0] & 0xFF) << 24) | ((tagBuffer[1] & 0xFF) << 16)
                | ((tagBuffer[2] & 0xFF) << 8) | (tagBuffer[3] & 0xFF);

        byte[] dataBuffer = new byte[4];
        int readInt = bufferedIn.read(dataBuffer);
        if (readInt < 4) { // End of stream or incomplete tag
            return null;
        }

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

        int tag = ((tagBuffer[0] & 0xFF) << 24) | ((tagBuffer[1] & 0xFF) << 16)
                | ((tagBuffer[2] & 0xFF) << 8) | (tagBuffer[3] & 0xFF);

        StringBuilder dataBuilder = new StringBuilder();
        int ch;
        while ((ch = bufferedIn.read()) != -1) {
            // Check for line end (considering both \n and \r\n)
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

