package com.daniel.docify.model;

import com.daniel.docify.fileProcessor.FileSerializer;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

public class FileFormatModel extends FileSerializer implements Serializable {

    public static final String FILE_FORMAT_VERSION = "1.0";

    /* Metadata objects */
    private String authorName;
    private String creationDate;
    private String fileFormatVersion;
    private String softwareVersion;
    private String savedLocation;

    private FileNodeModel rootNode;
    public FileFormatModel(FileNodeModel rootNode){
        this.rootNode = rootNode;
    }

    public String getSavedLocation() {
        return savedLocation;
    }

    @Metadata
    public void setSavedLocation(String savedLocation) {
        this.savedLocation = savedLocation;
    }

    public FileNodeModel getRootNode() {
        return rootNode;
    }

    @Metadata
    public void setRootNode(FileNodeModel rootNode) {
        this.rootNode = rootNode;
    }

    public String getAuthorName() {
        return authorName;
    }

    @Metadata
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCreationDate() {
        return creationDate;
    }

    @Metadata
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getFileFormatVersion() {
        return fileFormatVersion;
    }

    @Metadata
    public void setFileFormatVersion(String fileFormatVersion) {
        this.fileFormatVersion = fileFormatVersion;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    @Metadata
    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public void clear() {
        Class<?> clazz = this.getClass();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Metadata.class)) {
                try {
                    method.invoke(this, new Object[]{null});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
