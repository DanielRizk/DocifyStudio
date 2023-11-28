package com.daniel.docify.fileProcessor;

import com.daniel.docify.model.FileInfoModel;

import java.util.ArrayList;
import java.util.List;

public class FileNodeModel {
    private final String name;
    private final boolean isFile;
    private final String fullPath; // New attribute for full path
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
        return name; // or any other representation you want to display
    }
}
