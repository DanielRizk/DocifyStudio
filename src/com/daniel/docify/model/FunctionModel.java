package com.daniel.docify.model;

public class FunctionModel extends ComponentModel {

    private final String name;
    private final DocumentationModel documentation;
    private final String fileName;
    private final int lineNumber;

    public FunctionModel(String name, DocumentationModel documentation, String fileName, int lineNumber) {
        this.name = name;
        this.documentation = documentation;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

   @Override
   public String getName() {
       return name;
    }

    @Override
    public DocumentationModel getDocumentation() {
        return documentation;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }
}

