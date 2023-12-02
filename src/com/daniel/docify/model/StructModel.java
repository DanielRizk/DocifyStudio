package com.daniel.docify.model;

public class StructModel extends ComponentModel{

    private final String name;
    private final DocumentationModel documentation;
    private final Integer lineNumber;

    public StructModel(String name, DocumentationModel documentation, Integer lineNumber) {
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
    public Integer getLineNumber() {
        return lineNumber;
    }
}
