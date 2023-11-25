package com.daniel.docify;

import com.daniel.docify.model.FileInfoModel;
import com.daniel.docify.model.FunctionModel;
import com.daniel.docify.parser.clang.ClangParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws IOException {
        // Press Alt+Enter with your caret at the highlighted text to see how
        final String path = "D:\\Projects\\Technical\\Programming\\DocifyStudio\\ctpValProcess.h";
        BufferedReader buff = new BufferedReader(new FileReader(path));
        FileInfoModel fileInfo = ClangParser.parseFile(buff);
        for(int i = 0; i < fileInfo.getFunctionModel().size(); i++) {
            System.out.println("name: " + fileInfo.getFunctionModel().get(i).getName());
            System.out.println("Doc: " + fileInfo.getFunctionModel().get(i).getDocumentation().getFunctionBrief());
            System.out.println("file: " + fileInfo.getFunctionModel().get(i).getFileName());
            System.out.println("line: " + fileInfo.getFunctionModel().get(i).getLineNumber());

        }
    }
}
