package com.daniel.docify.parser.clang;

import com.daniel.docify.component.Clang.*;
import com.daniel.docify.model.FileNodeModel;
import com.daniel.docify.model.fileInfo.CFileInfo;
import com.daniel.docify.parser2.ParserUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief   This class provides all the necessary functionalities to parse C project files
 */
public class ClangParser extends ParserUtils{


    /** Keeps track of the current line number in the document */
    private static int currentLineNumber = 0;

    /**
     * @brief   This method parses passed file according to the pre-existing comments
     */
    @NotNull
    public static CFileInfo parseFile(FileNodeModel node) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(node.getFullPath()));

        String fileContent = null;

        try {
            fileContent = readFromFile(node.getFullPath());
            //System.out.println(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<CMacro> macros = new ArrayList<>();
        List<CStaticVar> staticVars = new ArrayList<>();
        List<CEnum> enums = new ArrayList<>();
        List<CStruct> structs = new ArrayList<>();
        List<CFunction> functions = new ArrayList<>();

        StringBuilder chunk = new StringBuilder();

        boolean enumScope = false;
        boolean structScope = false;
        boolean functionScope = false;

        String commentBuffer = null;


        String line;
        while ((line = nextLine(reader)) != null) {

            if (line.contains("/**")){
                while(!line.contains("*/")){
                    line = nextLine(reader);
                    chunk.append(line);
                }
                //System.out.println(chunk);
                switch (identifyCommentBlock(chunk.toString())){
                    case 1: enumScope = true;break;
                    case 2: structScope = true;break;
                    case 3: functionScope = true;break;
                    default: System.out.println("undefined");
                }
                commentBuffer = extractFromComment(chunk.toString());
                //System.out.println(commentBuffer);
                chunk = new StringBuilder();
            }

            if (functionScope){
                do{
                    line = nextLine(reader);
                    chunk.append(line);
                }while(!line.contains(";"));
                CFunction function = createFunction(chunk.toString());
                function.setFileName(node.getName());
                function.setDocumentation(commentBuffer);
                functions.add(function);
                functionScope = false;
            }



        }
        for (CFunction fun : functions){
            System.out.println("file name: "+fun.getFileName());
            System.out.println("function name: "+fun.getName());
            System.out.println("doc: "+fun.getDocumentation());
            System.out.println("params: "+fun.getParams());
            System.out.println("return: "+fun.getReturnType());
            System.out.println("line: "+fun.getLineNumber());
            System.out.println("\n");
        }
        return new CFileInfo(node.getName(), macros, staticVars, enums, structs, functions, fileContent);
    }

    private static CFunction createFunction(String chunk) {
        CFunction function = new CFunction();

        String[] parts = chunk.split("\\s+");

        // Extract the return type
        StringBuilder returnTypeBuilder = new StringBuilder();
        int index = 0;
        while (index < parts.length && !parts[index].contains("(")) {
            returnTypeBuilder.append(parts[index]).append(" ");
            index++;
        }
        function.setReturnType(returnTypeBuilder.toString().trim());

        // Extract the function name
        function.setName(parts[index].replace("(", ""));

        // Extract parameters
        function.setParams(extractParameters(parts, index + 1));

        function.setLineNumber(currentLineNumber);

        return function;
    }
    private static List<String> extractParameters(String[] parts, int startIndex) {
        List<String> parameters = new ArrayList<>();

        // Concatenate parts until the closing parenthesis is found
        StringBuilder parameterBuilder = new StringBuilder();
        for (int i = startIndex; i < parts.length; i++) {
            parameterBuilder.append(parts[i]).append(" ");
            if (parts[i].contains(")")) {
                parameters.add(parameterBuilder.toString().trim().replace(")", ""));
                break;
            } else if (parts[i].contains(",")) {
                parameters.add(parameterBuilder.toString().trim().replace(",", ""));
                parameterBuilder = new StringBuilder();
            }
        }

        return parameters;
    }

    private static String nextLine(BufferedReader reader) throws IOException {
        currentLineNumber++;
        return reader.readLine();
    }

    private static String extractFromComment(String chunk) {
        int startTagIndex;
        String startTag;
        if (chunk.contains("@brief")){
            startTag = "@brief";
        } else {
            startTag = "*";
        }
        startTagIndex = chunk.indexOf(startTag);

        if (startTagIndex != -1) {
            int startBriefContent = startTagIndex + startTag.length();
            chunk = chunk.trim().substring(startBriefContent);
            int endBriefContent;
            if (chunk.contains("@")){
                endBriefContent = chunk.indexOf("@");
            }else {
                endBriefContent = chunk.indexOf("*/");
            }
            chunk = chunk.substring(0,endBriefContent);
            chunk = chunk.replaceAll("\\*", "\n").lines().map(
                    String::trim).reduce("", (s1, s2) -> s1 + s2 + "\n").trim();
            return chunk;
        }
        return null;
    }

    public static Integer identifyCommentBlock(String chunk){

        if (chunk.contains("This Enum")){
            return 1;
        } else if (chunk.contains("This Struct")){
            return 2;
        }else if (chunk.contains("This function")){
            return 3;
        }
        return 0;
    }

    private static String readFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readString(path);
    }

//    private CFunction extractFunction(String chunck){
//        CFunction function;
//        return function;
//    }

}
