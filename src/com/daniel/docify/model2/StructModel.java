package com.daniel.docify.model2;

import java.io.Serializable;

/**
 * @brief   This class represents Structs in a processed file
 *          and provides useful information about the Struct
 */
public class StructModel extends ComponentModel implements Serializable {

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
