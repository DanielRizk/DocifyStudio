package com.daniel.docify.model;

import com.daniel.docify.fileProcessor.FileSerializer;

import java.io.Serializable;
import java.util.Date;

public class FileFormatModel extends FileSerializer implements Serializable {

    public static final String FILE_FORMAT_VERSION = "1.0";

    /* Metadata objects */
    private String authorName;
    private String creationDate;
    private String fileFormatVersion;
    private String softwareVersion;

    private FileNodeModel rootNode;

    public FileFormatModel(FileNodeModel rootNode){
        this.rootNode = rootNode;
    }

    public FileNodeModel getRootNode() {
        return rootNode;
    }

    public void setRootNode(FileNodeModel rootNode) {
        this.rootNode = rootNode;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getFileFormatVersion() {
        return fileFormatVersion;
    }

    public void setFileFormatVersion(String fileFormatVersion) {
        this.fileFormatVersion = fileFormatVersion;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }
}
