package com.daniel.docify.component.Clang;

import com.daniel.docify.component.Method;
import com.daniel.docify.fileProcessor.ExtendedFileInputStream;
import com.daniel.docify.fileProcessor.ExtendedFileOutputStream;
import com.daniel.docify.fileProcessor.TagDataPair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CFunction extends Method {
    public CFunction(){
    }

    @Override
    public void serialize(ExtendedFileOutputStream out) throws IOException {
        out.writeStringFieldMappingToStream(COMPONENT_NAME_TAG, getName());
        out.writeStringFieldMappingToStream(COMPONENT_FILE_NAME_TAG, getFileName());
        out.writeIntFieldMappingToStream(COMPONENT_LINE_NUM_TAG, getLineNumber());
        out.writeStringFieldMappingToStream(COMPONENT_METHOD_TAG, "");
        out.writeStringFieldMappingToStream(COMPONENT_STRUCT_TAG,getReturnType());
        out.writeStringFieldMappingToStream(COMPONENT_STRUCT_TAG,getDocumentation());
        out.writeIntFieldMappingToStream(COMPONENT_STRUCT_TAG, getParams().size());
        for (String params : getParams()){
            out.writeStringFieldMappingToStream(COMPONENT_STRUCT_TAG, params);
        }

    }

    public static CFunction deserialize(ExtendedFileInputStream in) throws IOException {
        String name = null;
        String fileName = null;
        int lineNum = 0;

        TagDataPair pair;

        // read component name
        pair = in.readStringFieldMappingFromStream();
        if (pair.tag() == COMPONENT_NAME_TAG){
            name = pair.SData();
        }

        // read component file name
        pair = in.readStringFieldMappingFromStream();
        if (pair.tag() == COMPONENT_FILE_NAME_TAG){
            fileName = pair.SData();
        }

        // read component line number
        pair = in.readIntFieldMappingFromStream();
        if (pair.tag() == COMPONENT_LINE_NUM_TAG){
            lineNum = pair.IData();
        }

        // read component type
        pair = in.readStringFieldMappingFromStream();
        if (pair.tag() == COMPONENT_METHOD_TAG) {
            CFunction function = new CFunction();
            function.setName(name);
            function.setFileName(fileName);
            function.setLineNumber(lineNum);
            // read method type value
            pair = in.readStringFieldMappingFromStream();
            function.setReturnType(pair.SData());
            // read method doc
            pair = in.readStringFieldMappingFromStream();
            function.setDocumentation(pair.SData());
            // read method params size
            pair = in.readIntFieldMappingFromStream();
            int size = pair.IData();
            List<String> params = new ArrayList<>();
            for (int i = 0; i < size; i++){
                // read method member
                pair = in.readStringFieldMappingFromStream();
                params.add(pair.SData());
            }
            function.setParams(params);
            return function;
        }
        return null;
    }
}
