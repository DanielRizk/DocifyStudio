package com.daniel.docify.parser2.clang;

import com.daniel.docify.model2.DocumentationModel;
import com.daniel.docify.model2.FileInfoModel;
import com.daniel.docify.model2.FunctionModel;
import com.daniel.docify.model2.StructModel;
import com.daniel.docify.parser2.ParserUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief   This class provides all the necessary functionalities to parse C project files
 */
public class ClangParser extends ParserUtils{

    /** Constant search keyword */
    private static final String BRIEF = "@brief";
    private static final String PARAM = "@param";
    private static final String RETURN = "@return";
    private static final String NOTE = "@note";

    /** Keeps track of the current line number in the document */
    private static int currentLineNumber = 0;

    /**
     * @brief   This method parses passed file according to the pre-existing comments
     */
    @NotNull
    public static FileInfoModel parseFile(BufferedReader reader, String fileName) throws IOException {

        List<StructModel> structModels = new ArrayList<>();
        List<FunctionModel> functionModels = new ArrayList<>();

        boolean isInCommentBlock = false;
        String functionName = null;
        StringBuilder commentBlock = new StringBuilder();
        DocumentationModel documentation = null;

        String line;
        while ((line = reader.readLine()) != null) {
            currentLineNumber++;

            if (line.contains("/**F**")) {
                isInCommentBlock = true;
                commentBlock = new StringBuilder();
                commentBlock.append(line).append("\n");
            } else if (isInCommentBlock) {
                commentBlock.append(line).append("\n");

                if (line.contains("*/")) {
                    isInCommentBlock = false;
                    documentation = processCommentBlock(commentBlock.toString());
                    commentBlock = new StringBuilder();
                }
            } else if (extractFunctionName(line) != null) {
                functionName = extractFunctionName(line);
            }
            if (functionName != null){
                functionModels.add(new FunctionModel(
                        functionName,
                        documentation,
                        currentLineNumber
                ));
                functionName = null;
                documentation = null;
            }
        }
        return new FileInfoModel(fileName, functionModels, structModels);
    }

    /**
     * @brief   This method extracts the documentation parameters from
     *          a document block -String- and return DocumentationModel
     *
     * @implSpec
     * @param commentBlock
     * @return
     * @param
     * @apiNote
     * @implNote
     *
     */
    private static DocumentationModel processCommentBlock(String commentBlock){
        DocumentationModel documentation = new DocumentationModel();
        String[] lines = commentBlock.split("\n");
        String brief = null;
        List<String> params = new ArrayList<>();
        String returnVal = null;
        String note = null;
        boolean isStillBrief = false;

        for (String commentLine : lines){
            if (isStillBrief && !commentLine.contains("*/")){
                if (!stripPrefixInCommentBlock(commentLine,null).trim().isEmpty())  {
                    brief = brief.concat("\n").concat(stripPrefixInCommentBlock(commentLine, null));
                }
            }
            if (commentLine.contains(BRIEF)){
                isStillBrief = true;
                brief = stripPrefixInCommentBlock(commentLine, BRIEF);
            }
            if (commentLine.contains(PARAM)){
                isStillBrief = false;
                params.add(stripPrefixInCommentBlock(commentLine, PARAM));
            }
            if (commentLine.contains(RETURN)){
                isStillBrief = false;
                returnVal = stripPrefixInCommentBlock(commentLine, RETURN);
            }
            if (commentLine.contains(NOTE)){
                isStillBrief = false;
                note = stripPrefixInCommentBlock(commentLine, NOTE);
            }
        }

        if (brief != null || !params.isEmpty() || returnVal != null || note != null) {
            documentation.setBrief(brief);
            documentation.setParams(params);
            documentation.setReturn(returnVal);
            documentation.setNote(note);
        } else {
            return null; // Discard the comment block if it doesn't contain relevant information
        }

        return documentation;
    }
}
