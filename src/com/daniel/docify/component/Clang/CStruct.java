package com.daniel.docify.component.Clang;

import com.daniel.docify.component.Struct;
import com.daniel.docify.fileProcessor.ExtendedFileInputStream;
import com.daniel.docify.fileProcessor.ExtendedFileOutputStream;
import com.daniel.docify.fileProcessor.TagDataPair;
import com.daniel.docify.model.FileInfoModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CStruct extends Struct {
    public CStruct() {
    }

    @Override
    public void serialize(ExtendedFileOutputStream out) throws IOException {
        out.writeStringFieldMappingToStream(COMPONENT_NAME_TAG, getName());
        out.writeStringFieldMappingToStream(COMPONENT_FILE_NAME_TAG, getFileName());
        out.writeIntFieldMappingToStream(COMPONENT_LINE_NUM_TAG, getLineNumber());
        out.writeStringFieldMappingToStream(COMPONENT_STRUCT_TAG, "");
        out.writeStringFieldMappingToStream(COMPONENT_STRUCT_TAG,getStructType());
        out.writeStringFieldMappingToStream(COMPONENT_STRUCT_TAG,getDocumentation());
        out.writeIntFieldMappingToStream(COMPONENT_STRUCT_TAG, getMembers().size());
        for (String member : getMembers()){
            out.writeStringFieldMappingToStream(COMPONENT_STRUCT_TAG, member);
        }
    }

    public static CStruct deserialize(ExtendedFileInputStream in) throws IOException {
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
        if (pair.tag() == COMPONENT_STRUCT_TAG) {
            CStruct struct = new CStruct();
            struct.setName(name);
            struct.setFileName(fileName);
            struct.setLineNumber(lineNum);
            // read struct type value
            pair = in.readStringFieldMappingFromStream();
            struct.setStructType(pair.SData());
            // read struct doc
            pair = in.readStringFieldMappingFromStream();
            struct.setDocumentation(pair.SData());
            // read struct member size
            pair = in.readIntFieldMappingFromStream();
            int size = pair.IData();
            List<String> members = new ArrayList<>();
            for (int i = 0; i < size; i++){
                // read struct member
                pair = in.readStringFieldMappingFromStream();
                members.add(pair.SData());
            }
            struct.setMembers(members);
            return struct;
        }
        return null;
    }
}
