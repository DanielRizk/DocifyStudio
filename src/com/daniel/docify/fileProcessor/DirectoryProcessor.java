package com.daniel.docify.fileProcessor;

import com.daniel.docify.model.FileInfoModel;
import com.daniel.docify.parser.clang.ClangParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DirectoryProcessor {
    public static FileNodeModel buildFileTree(File directory, String ProjectType) throws IOException {
        String fullPath = directory.getAbsolutePath();
        FileNodeModel node = new FileNodeModel(directory.getName(), false, fullPath);

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    FileNodeModel childNode = buildFileTree(file, ProjectType);
                    node.addChild(childNode);
                } else {
                    FileNodeModel childNode = new FileNodeModel(file.getName(), true, file.getAbsolutePath());
                    if (file.getName().endsWith(ProjectType)){
                        BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
                        childNode.setFileInfo(ClangParser.parseFile(reader, file.getName()));
                        node.addChild(childNode);
                    }
                }
            }
        }
        return node;
    }

    public static void printFileTree(FileNodeModel node, int depth) {

        System.out.println("  ".repeat(Math.max(0, depth)) + (node.isFile() ? "- " : "+ ") + node.getName() + " (" + node.getFullPath() + ")");

        for (FileNodeModel child : node.getChildren()) {
            printFileTree(child, depth + 1);
        }
    }

    public static void printFileTree(FileNodeModel node, String fileType) {
        if (node.isFile() && node.getFullPath().endsWith(fileType)) {
            System.out.println(node.getFullPath());
        }

        for (FileNodeModel child : node.getChildren()) {
            printFileTree(child, fileType);
        }
    }

    public static void processNode(FileNodeModel node) throws IOException {
        if (node.isFile() && node.getFullPath().endsWith(".h")) {
            BufferedReader reader = new BufferedReader(new FileReader(node.getFullPath()));
            FileInfoModel fileInfo = ClangParser.parseFile(reader, null);
            node.setFileInfo(fileInfo);
        }

        for (FileNodeModel child : node.getChildren()) {
            processNode(child);
        }
    }

    public static List<FileNodeModel> getNodesFileInfo(FileNodeModel node) {
        List<FileNodeModel> fileNodes = new ArrayList<>();
        if (node.isFile() && node.getFileInfo() != null) {
            fileNodes.add(node);
        }
        for (FileNodeModel child : node.getChildren()) {
            getNodesFileInfo(child);
        }
        return fileNodes;
    }
}