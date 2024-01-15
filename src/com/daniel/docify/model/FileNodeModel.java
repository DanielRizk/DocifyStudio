package com.daniel.docify.model;

import com.daniel.docify.fileProcessor.FileSerializer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a node in a directory structure hierarchy,
 * also provides useful method to create a node-tree structure
 */
public class FileNodeModel extends FileSerializer implements Serializable {
    private final String name;
    private final boolean isFile;
    private final String fullPath;
    private final List<FileNodeModel> children;
    private FileInfoModel fileInfo;

    public FileNodeModel(String name, boolean isFile, String fullPath) {
        this.name = name;
        this.isFile = isFile;
        this.fullPath = fullPath;
        this.children = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public boolean isFile() {
        return isFile;
    }

    public String getFullPath() {
        return fullPath;
    }

    public List<FileNodeModel> getChildren() {
        return children;
    }

    public void addChild(FileNodeModel child) {
        children.add(child);
    }

    public void setFileInfo (FileInfoModel fileInfo){
        this.fileInfo = fileInfo;
    }

    public FileInfoModel getFileInfo () {
        return this.fileInfo;
    }

    @Override
    public String toString() {
        return name;
    }
}
