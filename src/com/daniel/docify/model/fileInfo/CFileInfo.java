package com.daniel.docify.model.fileInfo;

import com.daniel.docify.component.Clang.*;
import com.daniel.docify.model.FileInfoModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CFileInfo extends FileInfoModel implements Serializable {
    private final List<CExtern> externs;
    private final List<CMacro> macros;
    private final List<CStaticVar> staticVars;
    private final List<CEnum> enums;
    private final List<CStruct> structs;
    private final List<CFunction> functions;

    public CFileInfo(String fileName,
                     List<CExtern> externs,
                     List<CMacro> macros,
                     List<CStaticVar> staticVars,
                     List<CEnum> enums,
                     List<CStruct> structs,
                     List<CFunction> functions,
                     String fileContent,
                     String fileType
    ) {
        super(fileName, fileType, fileContent);
        this.externs = externs;
        this.macros = macros;
        this.staticVars = staticVars;
        this.enums = enums;
        this.structs = structs;
        this.functions = functions;
    }
    public List<CExtern> getExterns() {
        return externs;
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

    public List<CFunction> getFunctions() {
        return functions;
    }

    @Override
    public List<ItemNameAndProperty> getItemNames() {
        List<ItemNameAndProperty> itemNames = new ArrayList<>();
        if (externs != null){
            for (CExtern extern : externs){
                itemNames.add(new ItemNameAndProperty(extern.getName(), ObjectType.EXTREN));
            }
        }
        if (macros != null){
            for (CMacro macro : macros){
                itemNames.add(new ItemNameAndProperty(macro.getName(), ObjectType.MACRO));
            }
        }
        if (staticVars != null){
            for (CStaticVar staticVar : staticVars){
                itemNames.add(new ItemNameAndProperty(staticVar.getName(), ObjectType.STATIC));
            }
        }
        if (enums != null){
            for (CEnum cEnum  : enums){
                itemNames.add(new ItemNameAndProperty(cEnum.getName(), ObjectType.ENUM));
            }
        }
        if (structs != null){
            for (CStruct struct : structs){
                itemNames.add(new ItemNameAndProperty(struct.getName(), ObjectType.STRUCT));
            }
        }
        if (functions != null){
            for (CFunction function  : functions){
                itemNames.add(new ItemNameAndProperty(function.getName(), ObjectType.FUNCTION));
            }
        }
        return itemNames;
    }


}
