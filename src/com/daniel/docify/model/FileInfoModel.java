package com.daniel.docify.model;

import java.util.ArrayList;
import java.util.List;

public class FileInfoModel {
    String fileName;
    List <FunctionModel> functionModel;
    List<StructModel> structModel;

    public FileInfoModel(String fileName, List<FunctionModel> functionModel, List<StructModel> structModel){
        this.fileName = fileName;
        this.functionModel = functionModel;
        this.structModel = structModel;
    }

    public String getFileName(){
        return fileName;
    }

    public List<FunctionModel> getFunctionModel(){
        return functionModel;
    }

    public List<StructModel> getStructModel(){
        return structModel;
    }

    public List<String> getFunctionsNames(){
        if (functionModel != null){
            List<String> funcNames = new ArrayList<>();
            for (FunctionModel func : functionModel){
                funcNames.add(func.getName());
            }
            return funcNames;
        }
        return null;
    }
}
