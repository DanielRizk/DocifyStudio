package com.daniel.docify.model;

import java.util.List;

public class FileInfoModel {
    List <FunctionModel> functionModel;
    List<StructModel> structModel;

    public FileInfoModel(List<FunctionModel> functionModel, List<StructModel> structModel){
        this.functionModel = functionModel;
        this.structModel = structModel;
    }

    public List<FunctionModel> getFunctionModel(){
        return functionModel;
    }
}
