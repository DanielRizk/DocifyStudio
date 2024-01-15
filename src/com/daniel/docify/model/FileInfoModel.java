package com.daniel.docify.model;


import java.io.Serializable;
import java.util.List;

/**
 * This class represents the file in any project
 * it provides useful information about the file
 */
public abstract class FileInfoModel implements Serializable{

    private final String fileName;
    private String htmlContent = null;

    protected FileInfoModel(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
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


