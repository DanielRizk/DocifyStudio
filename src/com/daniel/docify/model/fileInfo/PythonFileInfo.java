package com.daniel.docify.model.fileInfo;

import com.daniel.docify.model.FileInfoModel;

import java.lang.reflect.Field;
import java.util.List;

public class PythonFileInfo extends FileInfoModel {
    public PythonFileInfo(String fileName,
                          String fileContent,
                          String fileType) {
        super(fileName, fileType, fileContent);
    }

    @Override
    public List<ItemNameAndProperty> getItemNames() {
        return null;
    }

    @Override
    public Field[] getComponents() {
        return this.getClass().getDeclaredFields();
    }
}
