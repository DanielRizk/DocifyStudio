package com.daniel.docify.model;

import com.daniel.docify.fileProcessor.FileSerializer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileInfoModel implements Serializable{
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

    public List<String> getItemNames(){
        List<String> itemNames = new ArrayList<>();
        if (structModel != null){
            for (StructModel struct : structModel){
                itemNames.add(struct.getName());
            }
        }
        if (functionModel != null){
            for (FunctionModel func : functionModel){
                itemNames.add(func.getName());
            }
        }
        return itemNames;
    }
}
