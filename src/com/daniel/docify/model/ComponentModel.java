package com.daniel.docify.model;

public abstract class ComponentModel {
    public abstract String getName();

    public abstract DocumentationModel getDocumentation();

    public abstract Integer getLineNumber();
}

