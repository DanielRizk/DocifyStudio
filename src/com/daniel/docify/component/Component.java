package com.daniel.docify.component;

public abstract class Component {

    private String fileName;
    private String name;
    private int lineNumber;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
