package com.daniel.docify.model;

import java.io.Serializable;

/**
 * @brief   This class represents functions in a processed file
 *          and provides useful information about the function
 */
public class FunctionModel extends ComponentModel implements Serializable {

    private final String name;
    private final DocumentationModel documentation;
    private final Integer lineNumber;

    public FunctionModel(String name, DocumentationModel documentation, Integer lineNumber) {
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

