package com.daniel.docify.component;

import com.daniel.docify.fileProcessor.DociSerializable;
import java.io.Serializable;

public abstract class Component implements DociSerializable ,Serializable {

    private String fileName;
    private String name;
    private Integer lineNumber;

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

    public Integer getLineNumber() {
        return lineNumber;
    }

}
