package com.daniel.docify.model.fileInfo;

import com.daniel.docify.component.Clang.*;
import com.daniel.docify.model.FileInfoModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CFileInfo extends FileInfoModel implements Serializable {
    private final String fileName;
    private final List<CMacro> macros;
    private final List<CStaticVar> staticVars;
    private final List<CEnum> enums;
    private final List<CStruct> structs;
    private final List<CFunction> functions;
    private final String fileContent;

    public CFileInfo(String fileName,
                     List<CMacro> macros,
                     List<CStaticVar> staticVars,
                     List<CEnum> enums,
                     List<CStruct> structs,
                     List<CFunction> functions,
                     String fileContent
    ) {
        this.fileName = fileName;
        this.macros = macros;
        this.staticVars = staticVars;
        this.enums = enums;
        this.structs = structs;
        this.functions = functions;
        this.fileContent = fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    public List<CMacro> getMacros() {
        return macros;
    }

    public List<CStaticVar> getStaticVars() {
        return staticVars;
    }

    public List<CEnum> getEnums() {
        return enums;
    }

    public List<CStruct> getStructs() {
        return structs;
    }

    public String getFileContent() {
        return fileContent;
    }

    public List<CFunction> getFunctions() {
        return functions;
    }

    @Override
    public List<String> getItemNames() {
        List<String> itemNames = new ArrayList<>();
        if (staticVars != null){
            for (CStaticVar staticVar : staticVars){
                itemNames.add(staticVar.getName());
            }
        }
        if (structs != null){
            for (CStruct struct : structs){
                itemNames.add(struct.getName());
            }
        }
        if (enums != null){
            for (CEnum cEnum  : enums){
                itemNames.add(cEnum.getName());
            }
        }
        if (functions != null){
            for (CFunction function  : functions){
                itemNames.add(function.getName());
            }
        }
        return itemNames;
    }
}
