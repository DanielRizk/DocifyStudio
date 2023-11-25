package com.daniel.docify;

import com.daniel.docify.fileProcessor.FileNode;

import java.io.*;

import static com.daniel.docify.fileProcessor.FileProcessor.*;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    private final static String CProject = ".h";
    private final static String PythonProject = ".py";
    private final static String JavaProject = ".java";
    public static void main(String[] args) throws IOException {
        // Press Alt+Enter with your caret at the highlighted text to see how
        String rootPath = "D:\\Projects\\Technical\\Programming\\DocifyStudio\\test";
        File rootDir = new File(rootPath);

        if (rootDir.exists() && rootDir.isDirectory()) {
            FileNode root = buildFileTree(rootDir, CProject);
            //processNode(root);
            getNodesFileInfo(root);
        } else {
            System.out.println("Root directory does not exist or is not a directory.");
        }
    }
}
