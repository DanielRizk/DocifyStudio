package com.daniel.docify.parser.clang;

import com.daniel.docify.component.Clang.*;
import com.daniel.docify.model.fileInfo.CFileInfo;
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


    /** Keeps track of the current line number in the document */
    private static int currentLineNumber = 0;

    /**
     * @brief   This method parses passed file according to the pre-existing comments
     */
    @NotNull
    public static CFileInfo parseFile(BufferedReader reader, String fileName) throws IOException {
        List<CMacro> macros = new ArrayList<>();
        List<CStaticVar> staticVars = new ArrayList<>();
        List<CEnum> enums = new ArrayList<>();
        List<CStruct> structs = new ArrayList<>();
        List<CFunction> functions = new ArrayList<>();

        StringBuilder chunck = new StringBuilder();


        String line;
        while ((line = reader.readLine()) != null) {
            currentLineNumber++;

        }
        return new CFileInfo(fileName, macros, staticVars, enums, structs, functions);
    }

//    private CFunction extractFunction(String chunck){
//        CFunction function;
//        return function;
//    }

}
