package com.daniel.docify.model;

import com.daniel.docify.fileProcessor.*;
import com.daniel.docify.ui.Controller;
import javafx.application.Platform;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * This class represents a node in a directory structure hierarchy,
 * also provides useful method to create a node-tree structure
 */
public class FileNodeModel extends FileSerializer implements DociSerializable, Serializable {
    private String name;
    private String projectType;
    private boolean isFile;
    private String fullPath;
    private final List<FileNodeModel> children;
    private FileInfoModel fileInfo;

    public FileNodeModel(String name,String projectType ,boolean isFile, String fullPath) {
        this.name = name;
        this.projectType = projectType;
        this.isFile = isFile;
        this.fullPath = fullPath;
        this.children = new ArrayList<>();
    }

    public void updateNode(Path path, boolean isFile, WatchEvent.Kind<?> kind, DirectoryProcessor directoryProcessor) throws IOException {
        // Check if the event is for file modification
        if (kind == ENTRY_MODIFY || kind == ENTRY_DELETE || kind == ENTRY_CREATE) {
            if (getFullPath().equals(path.toString())){
                Platform.runLater(() -> {
                    Controller controller = directoryProcessor.getController();
                    try {
                        FileNodeModel newNode = directoryProcessor.RebuildDirTree(path.toFile(), this.getProjectType());
                        directoryProcessor.processDirTree(newNode, this.getProjectType());
                        controller.menuActions.getFileFormatModel().setRootNode(newNode);
                        controller.explorer.updateTreeView(controller.menuActions.getFileFormatModel().getRootNode());
                        controller.mainWindow.refreshWebViewDisplay();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            for (int i = 0; i < children.size(); i++) {
                FileNodeModel child = children.get(i);
                if (child.getFullPath().equals(path.toString())) {
                    try {
                        // Rebuild the node for the modified file/directory
                        FileNodeModel updatedNode = isFile ?
                                new FileNodeModel(path.getFileName().toString(), this.getProjectType(), true, path.toString()) :
                                directoryProcessor.RebuildDirTree(path.toFile(), this.getProjectType());

                        if (updatedNode != null) {
                            directoryProcessor.processDirTree(updatedNode, this.getProjectType());
                            children.set(i, updatedNode); // Replace the old node with the updated one
                        }

                        // UI update logic
                        Platform.runLater(() -> {
                            Controller controller = directoryProcessor.getController();
                            try {
                                controller.explorer.updateTreeView(controller.menuActions.getFileFormatModel().getRootNode());
                                controller.mainWindow.refreshWebViewDisplay();
                            } catch (MalformedURLException e) {
                                throw new RuntimeException(e);
                            }
                        });

                        break; // Exit the loop as the relevant node has been found and updated
                    } catch (IOException e) {
                        throw new IOException("could not build project! - " + this.getClass().getName()); // Or handle the exception as appropriate
                    }
                }else {
                    if (!child.isFile()){
                        Path childPath = Path.of(child.getFullPath());
                        updateNode(childPath, child.isFile, kind, directoryProcessor);
                    }
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public List<FileNodeModel> getChildren() {
        return children;
    }

    public void addChild(FileNodeModel child) {
        children.add(child);
    }

    public void removeChild(String childFullPath) {
        // First, try to remove the child directly if it's a direct child of this node
        boolean removed = children.removeIf(child -> child.getFullPath().equals(childFullPath));

        // If not removed, it means the target node is a subchild, so we need to look deeper
        if (!removed) {
            for (FileNodeModel child : children) {
                child.removeChild(childFullPath);
            }
        }
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

    @Override
    public void serialize(ExtendedFileOutputStream out) throws IOException {
        out.writeStringFieldMappingToStream(FILE_NODE_NAME_TAG, getName());
        out.writeStringFieldMappingToStream(FILE_NODE_TYPE_TAG, getProjectType());
        out.writeBoolFieldMappingToStream(FILE_NODE_IS_FILE_TAG, isFile());
        out.writeStringFieldMappingToStream(FILE_NODE_PATH_TAG, getFullPath());
        out.writeStringFieldMappingToStream(FILE_INFO_MODEL_TAG, "");
        // serialize fileInfo here

        out.writeIntFieldMappingToStream(FILE_NODE_CHILD_COUNT_TAG, getChildren().size());
        for (FileNodeModel child : getChildren()){
            child.serialize(out);
        }
    }

    public static FileNodeModel deserialize(ExtendedFileInputStream in) throws IOException {
        FileNodeModel fileNode = new FileNodeModel(null, null, false, null);
        TagDataPair pair;

        // read node name
        pair = in.readStringFieldMappingFromStream();
        if (pair.tag() == FILE_NODE_NAME_TAG){
            fileNode.setName(pair.SData());
        }

        // read node type
        pair = in.readStringFieldMappingFromStream();
        if (pair.tag() == FILE_NODE_TYPE_TAG){
            fileNode.setProjectType(pair.SData());
        }

        // read node is file
        pair = in.readBoolFieldMappingFromStream();
        if (pair.tag() == FILE_NODE_IS_FILE_TAG){
            fileNode.setFile(pair.BData());
        }

        // read node path
        pair = in.readStringFieldMappingFromStream();
        if (pair.tag() == FILE_NODE_PATH_TAG){
            fileNode.setFullPath(pair.SData());
        }

        // read file info
        pair = in.readStringFieldMappingFromStream();
        if (pair.tag() == FILE_INFO_MODEL_TAG){
            fileNode.setFileInfo(/*fileInfo deserialize*/ null);
        }

        int childCount = 0;
        // read child count
        pair = in.readIntFieldMappingFromStream();
        if (pair.tag() == FILE_NODE_CHILD_COUNT_TAG){
            childCount = pair.IData();
        }

        for (int i = 0; i < childCount; i++){
            fileNode.addChild(FileNodeModel.deserialize(in));
        }

        return fileNode;
    }
}
