package com.daniel.docify.parser.clang;

import com.daniel.docify.component.Clang.*;
import com.daniel.docify.fileProcessor.FileSerializer;
import com.daniel.docify.model.FileNodeModel;
import com.daniel.docify.model.fileInfo.CFileInfo;
import com.daniel.docify.parser.IParser;
import com.daniel.docify.parser.ParserUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @brief   This class provides all the necessary functionalities to parse C project files
 */
public class ClangParser extends ParserUtils implements IParser<CFileInfo> {

    private static final Logger LOGGER = Logger.getLogger(FileSerializer.class.getName());

    /* Keeps track of the current line number in the document */
    private static int currentLineNumber = 0;

    @Override
    public CFileInfo safeParse(FileNodeModel node, String fileType, BufferedReader reader) throws IOException {
        currentLineNumber = 0;

        String fileContent = null;

        try {
            fileContent = readFromFile(String.valueOf(node.getFullPath()));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error opening file: "+node.getName(), e);
        }

        List<CExtern> externs = new ArrayList<>();
        List<CMacro> macros = new ArrayList<>();
        List<CStaticVar> staticVars = new ArrayList<>();
        List<CEnum> enums = new ArrayList<>();
        List<CStruct> structs = new ArrayList<>();
        List<CFunction> functions = new ArrayList<>();

        StringBuilder chunk = new StringBuilder();

        boolean macroScope = false;
        boolean commentScope = false;
        boolean enumScope = false;
        boolean structScope = false;
        boolean functionScope = false;

        boolean enumFoundWithNoComment = false;
        boolean structFoundWithNoComment = false;

        String commentBuffer = null;
        String line;

        try {

            while ((line = nextLine(reader)) != null) {

                if (line.contains("/**") || commentScope) {
                    if (commentScope) {
                        chunk.append(line);
                        commentScope = false;
                    }
                    if (line.contains("*/")) {
                        chunk.append(line);
                    } else {
                        while (!line.contains("*/")) {
                            line = nextLine(reader);
                            chunk.append(line);
                        }
                    }
                    switch (identifyCommentBlock(chunk.toString())) {
                        case 1:
                            enumScope = true;
                            break;
                        case 2:
                            structScope = true;
                            break;
                        case 3:
                            functionScope = true;
                            break;
                        default:
                            chunk = new StringBuilder();
                            break;
                    }
                    commentBuffer = extractFromComment(chunk.toString());
                    chunk = new StringBuilder();
                } else if (line.matches("\\s*//.*")) {
                    chunk = new StringBuilder();
                } else if (line.contains("static ") && line.contains(";") && ensureNotCommentLine(line)) {
                    CStaticVar staticVar = extractStaticVar(line);
                    staticVar.setFileName(node.getName());
                    staticVar.setLineNumber(currentLineNumber);
                    staticVars.add(staticVar);
                } else if ((line.contains("typedef enum") || line.contains("enum ")) && ensureNotCommentLine(line)) {
                    enumScope = true;
                    enumFoundWithNoComment = true;
                } else if ((line.contains("typedef struct") || line.contains("struct ")) && ensureNotCommentLine(line)) {
                    structScope = true;
                    structFoundWithNoComment = true;
                } else if (line.contains("#define ") && ensureNotCommentLine(line)) {
                    macroScope = true;
                } else if (line.contains("extern ") && ensureNotCommentLine(line)) {
                    CExtern extern = extractExtern(line);
                    extern.setFileName(node.getName());
                    externs.add(extern);
                }

                if (macroScope) {
                    chunk.append(line);
                    Pattern pattern = Pattern.compile("\\\\(?!n)");
                    Matcher matcher = pattern.matcher(line);
                    if (line.contains("\\")) {
                        do {
                            line = nextLine(reader);
                            matcher = pattern.matcher(line);
                            chunk.append(line);
                        } while (matcher.find());
                    }
                    CMacro macro = extractMacro(chunk.toString());
                    if (macro != null) {
                        macro.setFileName(node.getName());
                        macros.add(macro);
                    }
                    chunk = new StringBuilder();
                    macroScope = false;
                }

                if (enumScope) {
                    if (enumFoundWithNoComment) {
                        enumFoundWithNoComment = false;
                        chunk.append(line);
                    }
                    if (!(chunk.toString().contains("enum") && chunk.toString().contains(";"))) {
                        do {
                            line = nextLine(reader);

                            if(line.trim().contains("//") || (line.trim().contains("/*") && line.trim().contains("*/"))) {
                                if (line.trim().startsWith("//") || line.trim().startsWith("/*")){
                                    line = "";
                                }else {
                                    if (line.trim().contains("//")) {
                                        line = line.substring(0, line.indexOf("//"));
                                    }else if (line.trim().contains("/*")){
                                        line = line.substring(0, line.indexOf("/*"));
                                    }
                                }
                            }

                            chunk.append(line);
                            if (line.contains("/**")) {
                                commentScope = true;
                                break;
                            }
                        } while (!(line.contains("}") && line.contains(";")));
                        if (!commentScope) {
                            CEnum cEnum = extractEnum(chunk.toString(), commentBuffer);
                            cEnum.setFileName(node.getName());
                            commentBuffer = null;
                            enums.add(cEnum);
                        }
                    }
                    chunk = new StringBuilder();
                    enumScope = false;
                }

                if (structScope) {
                    if (structFoundWithNoComment) {
                        structFoundWithNoComment = false;
                        chunk.append(line);
                    }
                    if (!(chunk.toString().contains("struct") && chunk.toString().contains(";"))) {
                        do {
                            line = nextLine(reader);

                            if(line.trim().contains("//") || (line.trim().contains("/*") && line.trim().contains("*/"))) {
                                if (line.trim().startsWith("//") || line.trim().startsWith("/*")){
                                    line = "";
                                }else {
                                    if (line.trim().contains("//")) {
                                        line = line.substring(0, line.indexOf("//"));
                                    }else if (line.trim().contains("/*")){
                                        line = line.substring(0, line.indexOf("/*"));
                                    }
                                }
                            }

                            chunk.append(line);

                            if (line.contains("/**")) {
                                commentScope = true;
                                break;
                            }
                        } while (!(line.contains("}") && line.contains(";")));
                        if (!commentScope) {
                            CStruct struct = extractStruct(chunk.toString(), commentBuffer);
                            struct.setFileName(node.getName());
                            commentBuffer = null;
                            structs.add(struct);
                        }
                    }
                    chunk = new StringBuilder();
                    structScope = false;
                }

                if (functionScope) {
                    do {
                        line = nextLine(reader);
                        chunk.append(line);
                        if (line.contains("/**")) {
                            commentScope = true;
                            break;
                        }
                    } while (!(line.contains(";") || line.contains("{")));
                    if (!commentScope) {
                        CFunction function = extractFunction(chunk.toString(), commentBuffer);
                        function.setFileName(node.getName());
                        commentBuffer = null;
                        functions.add(function);
                    }
                    chunk = new StringBuilder();
                    functionScope = false;
                }
            }
        } catch (NullPointerException e){
            LOGGER.log(Level.WARNING, "Null pointer exception in file: " + node.getFullPath() + " in line: " + currentLineNumber);
        }
        return new CFileInfo(node.getName(), externs, macros, staticVars, enums, structs, functions, fileContent, fileType);
    }

    private boolean ensureNotCommentLine(String line){
        String lineBuff = line;
        lineBuff = lineBuff.trim();
        return !lineBuff.startsWith("*") && !lineBuff.startsWith("//");
    }

    /**
     * @brief   This method parses passed file according to the pre-existing comments
     */
    @NotNull
    @Override
    public CFileInfo parseFile(FileNodeModel node, String fileType){
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(node.getFullPath()));
            return safeParse(node, fileType, reader);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error processing file: " + node.getName(), e);
            return new CFileInfo(null, null, null,
                    null,null,null,null,null, null);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error closing BufferedReader", e);
                }
            }
        }
    }

    private CExtern extractExtern(String line) {
        CExtern extern = new CExtern();

        line = line.trim();

        /* extract extern name*/
        extern.setName(line);
        extern.setLineNumber(currentLineNumber);

        return extern;
    }

    private CMacro extractMacro(String chunk) {
        CMacro macro = new CMacro();

        int start;

        chunk = chunk.trim();

        /* extract macro name */
        start = chunk.indexOf("#define ") + "#define ".length();
        chunk = chunk.substring(start).trim();

        String name;

        if (!chunk.contains(" ")){
            name = chunk;
        } else {
            name = chunk.substring(0, chunk.indexOf(" "));
        }
        if (name.isEmpty() || name.contains("_H")){
            return null;
        }
        macro.setName(name);

        /* extract macro value */
        chunk = chunk.substring(name.length()).trim();
        String value = chunk;
        value = value.replaceAll("\\\\(?!n)", "\n");

        String[] lines = value.split("\\n");
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].trim();
        }

        value = String.join("\n", lines);
        if (value.isEmpty()){
            value = "*Empty macro*";
        }
        macro.setValue(value);

        macro.setLineNumber(currentLineNumber);

        return macro;
    }

    private CStaticVar extractStaticVar(String line) {
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

    private CEnum extractEnum(String chunk, String commentBuffer) {
        CEnum cEnum = new CEnum();

        int start, end;

        /* extract struct name */
        start = chunk.indexOf("}");
        end = chunk.lastIndexOf(";");
        String enumName = chunk.substring(start + 1, end).trim();
        if (enumName.isEmpty()){
            start = chunk.indexOf("enum") + "enum".length();
            end = chunk.indexOf("{");
            enumName = chunk.substring(start, end).trim();
            if (enumName.isEmpty()){
                enumName = "Enum has no identifier!";
            }
        }
        cEnum.setName(enumName);

        /* extract struct type */
        end = chunk.indexOf("{");
        String enumType = chunk.substring(0, end);
        cEnum.setEnumType(enumType);

        /* extract struct members */
        if (commentBuffer == null) {
            commentBuffer = ""; // Or handle this case as appropriate for your context.
        }

        start = chunk.indexOf("{");
        end = chunk.indexOf("}");
        String rawMembers = chunk.substring(start + 1, end).trim();
        String[] membersArray = rawMembers.split(";");

        // Convert commentBuffer into a List for easier manipulation.
        List<String> commentLines = new ArrayList<>(Arrays.asList(commentBuffer.split("\n")));

        List<String> members = Arrays.stream(membersArray)
                .map(String::trim)
                .map(member -> {
                    // Extract the member name; assuming format is "type name".
                    // Adjust this logic as needed if your member declarations are more complex.
                    String[] parts = member.split("\\s+");
                    if (parts.length < 2) return member; // Skip if the member format is unexpected.
                    String memberName = parts[1];
                    Iterator<String> iterator = commentLines.iterator();
                    while (iterator.hasNext()) {
                        String line = iterator.next();
                        if (line.startsWith(memberName + ":")) {
                            // Extract the comment and remove the line from commentLines.
                            String comment = line.substring(line.indexOf(':') + 1).trim();
                            iterator.remove(); // Remove the used comment line.
                            return member + " -> " + comment;
                        }
                    }
                    // Return the member declaration unchanged if no comment is found.
                    return member;
                })
                .collect(Collectors.toList());

        cEnum.setMembers(members);

        // Reassemble commentBuffer without the used lines, if needed.
        commentBuffer = String.join("\n", commentLines);

        /* set line number */
        cEnum.setLineNumber(currentLineNumber);

        /* set documentation */
        if (!commentBuffer.isEmpty()) {
            cEnum.setDocumentation(commentBuffer);
        }

        return cEnum;
    }

    private CStruct extractStruct(String chunk, String commentBuffer) {
        CStruct struct = new CStruct();

        int start, end;

        /* extract struct name */
        start = chunk.indexOf("}");
        end = chunk.lastIndexOf(";");
        String structName = chunk.substring(start + 1, end).trim();
        if (structName.isEmpty()){
            start = chunk.indexOf("struct") + "struct".length();
            end = chunk.indexOf("{");
            structName = chunk.substring(start, end).trim();
        }
        struct.setName(structName);

        /* extract struct type */
        end = chunk.indexOf("{");
        String structType = chunk.substring(0, end).trim();
        if (structType.contains(structName)){
            structType = structType.replace(structName, "");
        }
        struct.setStructType(structType);

        /* extract struct members */
        if (commentBuffer == null) {
            commentBuffer = ""; // Or handle this case as appropriate for your context.
        }

        start = chunk.indexOf("{");
        end = chunk.indexOf("}");
        String rawMembers = chunk.substring(start + 1, end).trim();
        String[] membersArray = rawMembers.split(";");

        // Convert commentBuffer into a List for easier manipulation.
        List<String> commentLines = new ArrayList<>(Arrays.asList(commentBuffer.split("\n")));

        List<String> members = Arrays.stream(membersArray)
                .map(String::trim)
                .map(member -> {
                    // Extract the member name; assuming format is "type name".
                    // Adjust this logic as needed if your member declarations are more complex.
                    String[] parts = member.split("\\s+");
                    if (parts.length < 2) return member; // Skip if the member format is unexpected.
                    String memberName = parts[1];
                    Iterator<String> iterator = commentLines.iterator();
                    while (iterator.hasNext()) {
                        String line = iterator.next();
                        if (line.startsWith(memberName + ":")) {
                            // Extract the comment and remove the line from commentLines.
                            String comment = line.substring(line.indexOf(':') + 1).trim();
                            iterator.remove(); // Remove the used comment line.
                            return member + " -> " + comment;
                        }
                    }
                    // Return the member declaration unchanged if no comment is found.
                    return member;
                })
                .collect(Collectors.toList());

        struct.setMembers(members);

        // Reassemble commentBuffer without the used lines, if needed.
        commentBuffer = String.join("\n", commentLines);

        /* set line number */
        struct.setLineNumber(currentLineNumber);

        /* set documentation */
        if (!commentBuffer.isEmpty()) {
            struct.setDocumentation(commentBuffer);
        }

        return struct;
    }

    private CFunction extractFunction(String chunk, String commentBuffer) {
        CFunction function = new CFunction();

        int start, end;

        /* extract params */
        if (commentBuffer == null) {
            commentBuffer = ""; // Or handle this case as appropriate for your context.
        }

        start = chunk.indexOf("(");
        end = chunk.indexOf(")");
        String rawMembers = chunk.substring(start + 1, end).trim();
        String[] membersArray = rawMembers.split(",");

        // Convert commentBuffer into a List for easier manipulation.
        List<String> commentLines = new ArrayList<>(Arrays.asList(commentBuffer.split("\n")));

        List<String> params = Arrays.stream(membersArray)
                .map(String::trim)
                .map(member -> {
                    // Extract the member name; assuming format is "type name".
                    // Adjust this logic as needed if your member declarations are more complex.
                    String[] parts = member.split("\\s+");
                    if (parts.length < 2) return member; // Skip if the member format is unexpected.
                    String memberName = parts[1];
                    Iterator<String> iterator = commentLines.iterator();
                    while (iterator.hasNext()) {
                        String line = iterator.next();
                        if (line.startsWith(memberName + ":")) {
                            // Extract the comment and remove the line from commentLines.
                            String comment = line.substring(line.indexOf(':') + 1).trim();
                            iterator.remove(); // Remove the used comment line.
                            return member + " -> " + comment;
                        }
                    }
                    // Return the member declaration unchanged if no comment is found.
                    return member;
                })
                .collect(Collectors.toList());

        function.setParams(params);

        // Reassemble commentBuffer without the used lines, if needed.
        commentBuffer = String.join("\n", commentLines);

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

        /* set documentation */
        if (!commentBuffer.isEmpty()) {
            function.setDocumentation(commentBuffer);
        }

        return function;
    }

    private String extractFromComment(String chunk) {
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
            chunk = chunk.replaceAll("\\. ", ".\n");
            return chunk;
        }
        return null;
    }

    private Integer identifyCommentBlock(String chunk){
        String buff = chunk;
        buff = buff.toLowerCase();
        if (buff.contains("this enum")){
            return 1;
        } else if (buff.contains("this struct")){
            return 2;
        }else if (buff.contains("this function")){
            return 3;
        }
        return 0;
    }

    @Override
    public String nextLine(BufferedReader reader) throws IOException {
        currentLineNumber++;
        return reader.readLine();
    }
}
