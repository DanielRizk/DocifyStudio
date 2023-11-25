package com.daniel.docify.fileProcessor;

import com.daniel.docify.model.FileInfoModel;
import com.daniel.docify.parser.clang.ClangParser;

import java.io.*;

public class FileProcessor {
    public static FileNode buildFileTree(File directory, String ProjectType) throws IOException {
        String fullPath = directory.getAbsolutePath();
        FileNode node = new FileNode(directory.getName(), false, fullPath);

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    FileNode childNode = buildFileTree(file, ProjectType);
                    node.addChild(childNode);
                } else {
                    FileNode childNode = new FileNode(file.getName(), true, file.getAbsolutePath());
                    if (file.getName().endsWith(ProjectType)){
                        BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
                        childNode.setFileInfo(ClangParser.parseFile(reader));
                    }
                    node.addChild(childNode);
                }
            }
        }

        return node;
    }

    public static void printFileTree(FileNode node, int depth) {

        System.out.println("  ".repeat(Math.max(0, depth)) + (node.isFile() ? "- " : "+ ") + node.getName() + " (" + node.getFullPath() + ")");

        for (FileNode child : node.getChildren()) {
            printFileTree(child, depth + 1);
        }
    }

    public static void printFileTree(FileNode node, String fileType) {
        if (node.isFile() && node.getFullPath().endsWith(fileType)) {
            System.out.println(node.getFullPath());
        }

        for (FileNode child : node.getChildren()) {
            printFileTree(child, fileType);
        }
    }

    public static void processNode(FileNode node) throws IOException {
        if (node.isFile() && node.getFullPath().endsWith(".h")) {
            BufferedReader reader = new BufferedReader(new FileReader(node.getFullPath()));
            FileInfoModel fileInfo = ClangParser.parseFile(reader);
            node.setFileInfo(fileInfo);
        }

        for (FileNode child : node.getChildren()) {
            processNode(child);
        }
    }

    public static void getNodesFileInfo(FileNode node) {
        if (node.isFile() && node.getFileInfo() != null) {
            if (node.getFileInfo().getFunctionModel() != null) {
                for (int i = 0; i < node.getFileInfo().getFunctionModel().size(); i++) {
                    System.out.println(node.getName());
                    System.out.println("name: " + node.getFileInfo().getFunctionModel().get(i).getName());
                    System.out.println("Doc: " + node.getFileInfo().getFunctionModel().get(i).getDocumentation().getFunctionBrief());
                    System.out.println("line: " + node.getFileInfo().getFunctionModel().get(i).getLineNumber() + "\n");
                }
            }
        }
        for (FileNode child : node.getChildren()) {
            getNodesFileInfo(child);
        }
    }
}
