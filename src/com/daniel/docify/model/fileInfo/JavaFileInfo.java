package com.daniel.docify.model.fileInfo;

import com.daniel.docify.model.FileInfoModel;

import java.lang.reflect.Field;
import java.util.List;

public class JavaFileInfo extends FileInfoModel {
    public JavaFileInfo(String fileName, String fileContent,String fileType) {
        super(fileName, fileType ,fileContent);
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
