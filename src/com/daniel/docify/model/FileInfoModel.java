package com.daniel.docify.model;


import java.io.Serializable;
import java.util.List;

/**
 * This class represents the file in any project
 * it provides useful information about the file
 */
public abstract class FileInfoModel implements Serializable{

    private final String fileName;
    private final String fileType;
    private final String fileContent;
    private String htmlContent = null;

    protected FileInfoModel(String fileName, String fileType, String fileContent) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileContent = fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public String getFileContent() {
        return fileContent;
    }

    public String getHtmlContent(){
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent){
        this.htmlContent = htmlContent;
    }


    /**
     * @brief   This method returns all the function and struct names
     *          in a single file
     */
    public abstract List<ItemNameAndProperty> getItemNames();

    public enum ObjectType{
        EXTREN,
        MACRO,
        STATIC,
        STRUCT,
        ENUM,
        FUNCTION
    }

    public record ItemNameAndProperty(String name, ObjectType type) {
        @Override
        public String toString() {
            return name;
        }

        public ObjectType getType(){
            return type;
        }
    }
}


