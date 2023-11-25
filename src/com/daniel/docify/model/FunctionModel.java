package com.daniel.docify.model;

public class FunctionModel extends ComponentModel {

    private final String name;
    private final DocumentationModel documentation;
    private final int lineNumber;

    public FunctionModel(String name, DocumentationModel documentation, int lineNumber) {
        this.name = name;
        this.documentation = documentation;
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
    public int getLineNumber() {
        return lineNumber;
    }
}

