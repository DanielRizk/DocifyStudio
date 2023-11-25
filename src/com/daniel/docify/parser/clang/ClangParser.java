package com.daniel.docify.parser.clang;

import com.daniel.docify.model.DocumentationModel;
import com.daniel.docify.model.FileInfoModel;
import com.daniel.docify.model.FunctionModel;
import com.daniel.docify.model.StructModel;
import com.daniel.docify.parser.ParserUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClangParser extends ParserUtils{

    /** Constant search keyword */
    private static final String INCLUDES   = "Includes";
    private static final String MACROS     = "Macros";
    private static final String STRUCTURES = "Structures";
    private static final String FUNCTION   = "Function Prototypes";


    private static final String BRIEF = "@brief";
    private static final String PARAM = "@param";
    private static final String RETURN = "@return";
    private static final String NOTE = "@note";

    private static String line;
    private static int currentLineNumber = 1;

    /** This function parses passed file according to the pre-existing comments */
    @NotNull
    public static FileInfoModel parseFile(BufferedReader reader) throws IOException {

        List<FunctionModel> functionModels = null;

        while ((line = reader.readLine()) != null) {

            if (line.trim().startsWith("/*===")) {
                line = reader.readLine();
                currentLineNumber++;

                line = stripBlockCommentSyntax(line);
                switch (line) {
                    case INCLUDES:
                        break;
                    case MACROS:
                        break;
                    case STRUCTURES:
                        break;
                    case FUNCTION:
                        functionModels = functionReader(reader);
                        break;
                }
            }
            currentLineNumber++;
        }
        List<StructModel> temp2 = null;
        currentLineNumber = 1;
        return new FileInfoModel(functionModels, temp2);
    }
    private static List<FunctionModel> functionReader(BufferedReader reader) throws IOException {

        boolean isCommentBlock = false;
        int lineNumBuff = 0;
        String functionName = null;
        List<String> functionParams = new ArrayList<>();
        DocumentationModel doc = new DocumentationModel();
        List<FunctionModel> functionModels = new ArrayList<>();

        while((line = reader.readLine()) != null){
            currentLineNumber++;
            if (line.trim().startsWith("/*===")){ // when it returns it won't catch the /*=== for the rest
                reader.reset();
                currentLineNumber = lineNumBuff;
                break;
            }
            if (line.trim().startsWith("/**")){
                isCommentBlock = true;
            }
            if (extractFunctionName(line) != null){
                functionName = extractFunctionName(line);
            }
            while (isCommentBlock){

                line = reader.readLine();
                currentLineNumber++;

                if (line.trim().startsWith("*") && line.contains(BRIEF)){
                    line = stripPrefix(line, BRIEF);
                    doc.setFunctionBrief(line);
                }

                if (line.trim().startsWith("*") && line.contains(PARAM)){
                    line = stripPrefix(line, PARAM);
                    functionParams.add(line);
                }

                if (line.trim().startsWith("*") && line.contains(NOTE)){
                    line = stripPrefix(line, NOTE);
                    doc.setNote(line);
                }

                if (line.trim().startsWith("*") && line.contains(RETURN)){
                    line = stripPrefix(line, RETURN);
                    doc.setReturn(line);
                }

                if (line.contains("*/")){
                    isCommentBlock = false;
                    doc.setFunctionParams(functionParams);
                }
            }

            if (functionName != null) {
                functionModels.add(new FunctionModel(
                        functionName,
                        doc,
                        currentLineNumber
                ));
                functionName = null;
                doc = new DocumentationModel();
                reader.mark(250);
                lineNumBuff = currentLineNumber;
            }
        }
        return functionModels;
    }
}
