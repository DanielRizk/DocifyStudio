package com.daniel.docify.model2;

/**
 * @brief   This abstract class provides the outline of all the components
 */
public abstract class ComponentModel {
    public abstract String getName();

    public abstract DocumentationModel getDocumentation();

    public abstract Integer getLineNumber();
}

