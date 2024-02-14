package com.daniel.docify.model;


import com.daniel.docify.component.Clang.*;
import com.daniel.docify.component.Component;
import com.daniel.docify.fileProcessor.DociSerializable;
import com.daniel.docify.fileProcessor.ExtendedFileInputStream;
import com.daniel.docify.fileProcessor.ExtendedFileOutputStream;
import com.daniel.docify.fileProcessor.TagDataPair;
import com.daniel.docify.model.fileInfo.CFileInfo;
import com.daniel.docify.model.fileInfo.JavaFileInfo;
import com.daniel.docify.model.fileInfo.PythonFileInfo;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the file in any project
 * it provides useful information about the file
 */
public abstract class FileInfoModel implements DociSerializable ,Serializable{

    private final String fileName;
    private final String fileType;
    private final String fileContent;
    private String htmlContent = null;

    protected FileInfoModel(String fileName, String fileType, String fileContent) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileContent = fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public String getFileContent() {
        return fileContent;
    }

    public String getHtmlContent(){
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent){
        this.htmlContent = htmlContent;
    }


    /**
     * @brief   This method returns all the function and struct names
     *          in a single file
     */
    public abstract List<ItemNameAndProperty> getItemNames();

    public abstract Field[] getComponents();

    public enum ObjectType{
        EXTREN,
        MACRO,
        STATIC,
        STRUCT,
        ENUM,
        FUNCTION
    }

    public record ItemNameAndProperty(String name, ObjectType type) {
        @Override
        public String toString() {
            return name;
        }

        public ObjectType getType(){
            return type;
        }
    }

    @Override
    public void serialize(ExtendedFileOutputStream out) throws IOException {
        out.writeStringFieldMappingToStream(FILE_INFO_NAME_TAG,getFileName());
        out.writeStringFieldMappingToStream(FILE_INFO_P_TYPE_TAG,getFileType());
        out.writeStringFieldMappingToStream(FILE_INFO_CONTENT_TAG,getFileContent());

        if (this instanceof CFileInfo fileInfo) {
            Field[] fields = fileInfo.getComponents();
            out.writeStringFieldMappingToStream(FILE_INFO_TYPE_TAG, "CFileInfo");
            out.writeIntFieldMappingToStream(FILE_INFO_COMP_COUNT_TAG, fields.length);

            for (Field field : fields) {
                field.setAccessible(true);

                if (List.class.isAssignableFrom(field.getType())) {
                    List<?> list = null;
                    try {
                        list = (List<?>) field.get(fileInfo);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    int tag = DociSerializable.getTagNameForField(field.getName());

                    // Serialize list size for this component
                    out.writeStringFieldMappingToStream(tag, "");
                    out.writeIntFieldMappingToStream(FILE_INFO_COMP_LIST_SIZE_TAG, list.size());

                    // Serialize each element in the list
                    for (Object element : list) {
                        if (element instanceof DociSerializable) {
                            ((DociSerializable) element).serialize(out);
                        }
                    }
                }
            }
        } else if (this instanceof JavaFileInfo) {
            out.writeStringFieldMappingToStream(FILE_INFO_TYPE_TAG, "JavaFileInfo");
        } else if (this instanceof PythonFileInfo) {
            out.writeStringFieldMappingToStream(FILE_INFO_TYPE_TAG, "PythonFileInfo");
        } else {
            throw new InvalidClassException("Unknown FileInfoModel sub-class");
        }


    }

    public static FileInfoModel deserialize(ExtendedFileInputStream in) throws IOException {
        TagDataPair pair;

        String fileName = null;
        String pType = null;
        String fileContent = null;

        List<CExtern> externs = new ArrayList<>();
        List<CMacro> macros = new ArrayList<>();
        List<CStaticVar> staticVars = new ArrayList<>();
        List<CEnum> enums = new ArrayList<>();
        List<CStruct> structs = new ArrayList<>();
        List<CFunction> functions = new ArrayList<>();

        // read fileInfo tag
        pair = in.readStringFieldMappingFromStream();
        if (pair.tag() == FILE_INFO_NAME_TAG){
            fileName = pair.SData();
        }

        // read fileInfo project type
        pair = in.readStringFieldMappingFromStream();
        if (pair.tag() == FILE_INFO_P_TYPE_TAG){
            pType = pair.SData();
        }

        // read fileInfo content
        pair = in.readStringFieldMappingFromStream();
        if (pair.tag() == FILE_INFO_CONTENT_TAG){
            fileContent = pair.SData();
        }

        // read fileInfo concrete class type
        pair = in.readStringFieldMappingFromStream();
        if (pair.tag() == FILE_INFO_TYPE_TAG){
            switch (pair.SData()){
                case "CFileInfo": {

                    int compCount = 0;

                    // read fileInfo number of list of components
                    pair = in.readIntFieldMappingFromStream();
                    if (pair.tag() == FILE_INFO_COMP_COUNT_TAG){
                        compCount = pair.IData();
                    }

                    for (int i = 0; i < compCount; i++){
                        // read component list's element name
                        pair = in.readStringFieldMappingFromStream();
                        if (pair.tag() == FILE_INFO_EXTERN_LIST_TAG){
                            // read component list's element size
                            pair = in.readIntFieldMappingFromStream();
                            if (pair.tag() == FILE_INFO_COMP_LIST_SIZE_TAG){
                                for (int j = 0; j < pair.IData(); j++){
                                    externs.add(CExtern.deserialize(in));
                                }
                            }
                        }else if (pair.tag() == FILE_INFO_MACRO_LIST_TAG){
                            // read component list's element size
                            pair = in.readIntFieldMappingFromStream();
                            if (pair.tag() == FILE_INFO_COMP_LIST_SIZE_TAG){
                                for (int j = 0; j < pair.IData(); j++){
                                    macros.add(CMacro.deserialize(in));
                                }
                            }
                        }else if (pair.tag() == FILE_INFO_STATIC_LIST_TAG){
                            // read component list's element size
                            pair = in.readIntFieldMappingFromStream();
                            if (pair.tag() == FILE_INFO_COMP_LIST_SIZE_TAG){
                                for (int j = 0; j < pair.IData(); j++){
                                    staticVars.add(CStaticVar.deserialize(in));
                                }
                            }
                        }else if (pair.tag() == FILE_INFO_ENUM_LIST_TAG){
                            // read component list's element size
                            pair = in.readIntFieldMappingFromStream();
                            if (pair.tag() == FILE_INFO_COMP_LIST_SIZE_TAG){
                                for (int j = 0; j < pair.IData(); j++){
                                    enums.add(CEnum.deserialize(in));
                                }
                            }
                        }else if (pair.tag() == FILE_INFO_STRUCT_LIST_TAG){
                            // read component list's element size
                            pair = in.readIntFieldMappingFromStream();
                            if (pair.tag() == FILE_INFO_COMP_LIST_SIZE_TAG){
                                for (int j = 0; j < pair.IData(); j++){
                                    structs.add(CStruct.deserialize(in));
                                }
                            }
                        }else if (pair.tag() == FILE_INFO_METHOD_LIST_TAG){
                            // read component list's element size
                            pair = in.readIntFieldMappingFromStream();
                            if (pair.tag() == FILE_INFO_COMP_LIST_SIZE_TAG){
                                for (int j = 0; j < pair.IData(); j++){
                                    functions.add(CFunction.deserialize(in));
                                }
                            }
                        }
                    }
                    return new CFileInfo(fileName, externs, macros,
                            staticVars, enums, structs,
                            functions, fileContent, pType);
                }
                case "JavaFileInfo": return null;
                case "PythonFileInfo": return null;
                default: return null;
            }
        }else {
            return null;
        }
    }
}


