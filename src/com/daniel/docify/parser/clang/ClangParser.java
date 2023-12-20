package com.daniel.docify.parser.clang;

import com.daniel.docify.component.Clang.*;
import com.daniel.docify.model.FileNodeModel;
import com.daniel.docify.model.fileInfo.CFileInfo;
import com.daniel.docify.parser.ParserUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<CMacro> macros = new ArrayList<>();
        List<CStaticVar> staticVars = new ArrayList<>();
        List<CEnum> enums = new ArrayList<>();
        List<CStruct> structs = new ArrayList<>();
        List<CFunction> functions = new ArrayList<>();

        StringBuilder chunk = new StringBuilder();

        boolean commentScope = false;
        boolean enumScope = false;
        boolean structScope = false;
        boolean functionScope = false;

        boolean enumFoundWithNoComment = false;
        boolean structFoundWithNoComment = false;

        String commentBuffer = null;


        String line;
        while ((line = nextLine(reader)) != null) {

            if (line.contains("/**") || commentScope){
                if (commentScope){
                    chunk.append(line);
                    commentScope = false;
                }
                /* fix one line documentation detection */
                if (line.contains("*/")){
                    chunk.append(line);
                }else {
                    while (!line.contains("*/")) {
                        line = nextLine(reader);
                        chunk.append(line);
                    }
                }
                switch (identifyCommentBlock(chunk.toString())){
                    case 1:     enumScope = true;break;
                    case 2:     structScope = true;break;
                    case 3:     functionScope = true;break;
                    default:    System.out.println("undefined");
                                chunk = new StringBuilder();
                                break;
                }
                commentBuffer = extractFromComment(chunk.toString());
                chunk = new StringBuilder();
            } else if (line.contains("typedef enum") || line.contains("enum")) {
                enumScope = true;
                enumFoundWithNoComment = true;
            }else if (line.contains("typedef struct") || line.contains("struct")) {
                structScope = true;
                structFoundWithNoComment = true;
            }else if (line.contains("static") && line.contains(";")) {
                CStaticVar staticVar = extractStaticVar(line);
                staticVar.setFileName(node.getName());
                staticVar.setLineNumber(currentLineNumber);
                staticVars.add(staticVar);
            }

            if (enumScope){
                if (enumFoundWithNoComment){
                    enumFoundWithNoComment = false;
                    chunk.append(line);
                }
                do{
                    line = nextLine(reader);
                    chunk.append(line);
                    if (line.contains("/**")){
                        commentScope = true;
                        break;
                    }
                }while(!(line.contains("}") && line.contains(";")));
                if (!commentScope) {
                    CEnum cEnum = extractEnum(chunk.toString());
                    cEnum.setFileName(node.getName());
                    cEnum.setDocumentation(commentBuffer);
                    commentBuffer = null;
                    enums.add(cEnum);
                }
                chunk = new StringBuilder();
                enumScope = false;
            }

            if (structScope){
                if (structFoundWithNoComment){
                    structFoundWithNoComment = false;
                    chunk.append(line);
                }
                do{
                    line = nextLine(reader);
                    chunk.append(line);
                    if (line.contains("/**")){
                        commentScope = true;
                        break;
                    }
                }while(!(line.contains("}") && line.contains(";")));
                if (!commentScope) {
                    CStruct struct = extractStruct(chunk.toString());
                    struct.setFileName(node.getName());
                    struct.setDocumentation(commentBuffer);
                    commentBuffer = null;
                    structs.add(struct);
                }
                chunk = new StringBuilder();
                structScope = false;
            }

            if (functionScope){
                do{
                    line = nextLine(reader);
                    chunk.append(line);
                    if (line.contains("/**")){
                        commentScope = true;
                        break;
                    }
                }while(!line.contains(";"));
                if (!commentScope) {
                    CFunction function = extractFunction(chunk.toString());
                    function.setFileName(node.getName());
                    function.setDocumentation(commentBuffer);
                    commentBuffer = null;
                    functions.add(function);
                }
                chunk = new StringBuilder();
                functionScope = false;
            }



        }
        System.out.println("------------------------------------------------");
        for (CStaticVar st : staticVars){
            System.out.println("file name: "+st.getFileName());
            System.out.println("static var name: "+st.getName());
            System.out.println("value: "+st.getValue());
            System.out.println("line: "+st.getLineNumber());
            System.out.println("\n");
        }
        System.out.println("------------------------------------------------");
        for (CEnum en : enums){
            System.out.println("file name: "+en.getFileName());
            System.out.println("enum name: "+en.getName());
            System.out.println("doc: "+en.getDocumentation());
            System.out.println("members: "+en.getMembers());
            System.out.println("return: "+en.getEnumType());
            System.out.println("line: "+en.getLineNumber());
            System.out.println("\n");
        }
        System.out.println("------------------------------------------------");
        for (CStruct st : structs){
            System.out.println("file name: "+st.getFileName());
            System.out.println("struct name: "+st.getName());
            System.out.println("doc: "+st.getDocumentation());
            System.out.println("members: "+st.getMembers());
            System.out.println("return: "+st.getStructType());
            System.out.println("line: "+st.getLineNumber());
            System.out.println("\n");
        }
        System.out.println("------------------------------------------------");
        for (CFunction fun : functions){
            System.out.println("file name: "+fun.getFileName());
            System.out.println("function name: "+fun.getName());
            System.out.println("doc: "+fun.getDocumentation());
            System.out.println("params: "+fun.getParams());
            System.out.println("return: "+fun.getReturnType());
            System.out.println("line: "+fun.getLineNumber());
            System.out.println("\n");
        }
        System.out.println("------------------------------------------------");
        return new CFileInfo(node.getName(), macros, staticVars, enums, structs, functions, fileContent);
    }

    private static CStaticVar extractStaticVar(String line) {
        CStaticVar staticVar = new CStaticVar();

        int start, startVal, end;
        String tag;

        line = line.trim();

        if (line.contains("=")){
            tag = "=";
            startVal = line.indexOf(tag);
        }
        else{
            tag = ";";
            startVal = line.indexOf(tag);;
        }

        /* extract static variable name */
        start = 0;
        end = line.indexOf(tag);
        String staticName = line.substring(start,end).trim();
        staticVar.setName(staticName);

        /* extract static variable value */
        end = line.indexOf(";");
        String value = line.substring(startVal,end);
        if (!value.isEmpty()) value = value.replace("=", "").trim();
        else value = "Uninitialized";
        staticVar.setValue(value);

        return staticVar;
    }

    private static CEnum extractEnum(String chunk) {
        CEnum cEnum = new CEnum();

        int start, end;

        /* extract struct name */
        start = chunk.indexOf("}");
        end = chunk.lastIndexOf(";");
        String enumName = chunk.substring(start + 1, end).trim();
        cEnum.setName(enumName);

        /* extract struct type */
        end = chunk.indexOf("{");
        String enumType = chunk.substring(0, end);
        cEnum.setEnumType(enumType);

        /* extract struct members */
        start = chunk.indexOf("{");
        end = chunk.indexOf("}");
        String rawMembers = chunk.substring(start + 1,  end).trim();
        String[] membersArray = rawMembers.split(",");
        List<String> members = Arrays.stream(membersArray)
                .map(String::trim)
                .collect(Collectors.toList());
        cEnum.setMembers(members);

        /* set line number */
        cEnum.setLineNumber(currentLineNumber);

        return cEnum;
    }

    private static CStruct extractStruct(String chunk) {
        CStruct struct = new CStruct();

        int start, end;

        /* extract struct name */
        start = chunk.indexOf("}");
        end = chunk.lastIndexOf(";");
        String structName = chunk.substring(start + 1, end).trim();
        struct.setName(structName);

        /* extract struct type */
        end = chunk.indexOf("{");
        String structType = chunk.substring(0, end);
        struct.setStructType(structType);

        /* extract struct members */
        start = chunk.indexOf("{");
        end = chunk.indexOf("}");
        String rawMembers = chunk.substring(start + 1,  end).trim();
        String[] membersArray = rawMembers.split(";");
        List<String> members = Arrays.stream(membersArray)
                .map(String::trim)
                .collect(Collectors.toList());
        struct.setMembers(members);

        /* set line number */
        struct.setLineNumber(currentLineNumber);

        return struct;
    }

    private static CFunction extractFunction(String chunk) {
        CFunction function = new CFunction();

        int start, end;

        /* extract params */
        start = chunk.indexOf("(");
        end = chunk.indexOf(")");
        String rawParams = chunk.substring(start + 1, end);
        String[] paramsArray = rawParams.split(",");
        List<String> params = Arrays.stream(paramsArray)
                .map(String::trim)
                .collect(Collectors.toList());
        function.setParams(params);

        /* extract function name */
        start = 0;
        end = chunk.indexOf("(");
        chunk = chunk.substring(start, end);
        start = chunk.lastIndexOf(" ");
        String functionName = chunk.substring(start).trim();
        function.setName(functionName);

        /* extract return type */
        start = 0;
        end = chunk.lastIndexOf(" ");
        String returnType = chunk.substring(start,end).trim();
        function.setReturnType(returnType);

        /* set line number */
        function.setLineNumber(currentLineNumber);

        return function;
    }

    private static String extractFromComment(String chunk) {
        int startTagIndex;
        String startTag;

        chunk = chunk.trim();

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

    private static Integer identifyCommentBlock(String chunk){

        if (chunk.contains("This enum")){
            return 1;
        } else if (chunk.contains("This struct")){
            return 2;
        }else if (chunk.contains("This function")){
            return 3;
        }
        return 0;
    }

    private static String nextLine(BufferedReader reader) throws IOException {
        currentLineNumber++;
        return reader.readLine();
    }

    private static String readFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readString(path);
    }
}
