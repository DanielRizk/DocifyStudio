package com.daniel.docify.model;

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
}
