package com.daniel.docify.model;

import java.io.Serializable;
import java.util.Date;

public class FileFormatModel implements Serializable {

    static final String FILE_FORMAT_VERSION = "1.0";

    /* Metadata objects */
    private String authorName;
    private Date creationDate;
    private String fileFormatVersion = FILE_FORMAT_VERSION;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
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
