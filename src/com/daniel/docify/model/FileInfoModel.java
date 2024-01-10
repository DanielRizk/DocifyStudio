package com.daniel.docify.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the file in any project
 * it provides useful information about the file
 */
public abstract class FileInfoModel implements Serializable{


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


