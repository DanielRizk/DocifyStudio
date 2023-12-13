package com.daniel.docify.model.fileInfo;

import com.daniel.docify.component.Clang.CEnum;
import com.daniel.docify.component.Clang.CFunction;
import com.daniel.docify.component.Clang.CStaticVar;
import com.daniel.docify.component.Clang.CStruct;
import com.daniel.docify.model.FileInfoModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CFileInfo extends FileInfoModel implements Serializable {
    private final String fileName;
    private final List<CStaticVar> staticVars;
    private final List<CStruct> structs;
    private final List<CEnum> enums;
    private final List<CFunction> functions;

    public CFileInfo(String fileName,
                     List<CStaticVar> staticVars,
                     List<CStruct> structs,
                     List<CEnum> enums,
                     List<CFunction> functions
    ) {
        this.fileName = fileName;
        this.staticVars = staticVars;
        this.structs = structs;
        this.enums = enums;
        this.functions = functions;
    }

    public String getFileName() {
        return fileName;
    }

    public List<CStaticVar> getStaticVars() {
        return staticVars;
    }

    public List<CStruct> getStructs() {
        return structs;
    }

    public List<CEnum> getEnums() {
        return enums;
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
