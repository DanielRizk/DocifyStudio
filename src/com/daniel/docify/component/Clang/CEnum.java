package com.daniel.docify.component.Clang;

import com.daniel.docify.component.Enumeration;
import com.daniel.docify.fileProcessor.ExtendedFileInputStream;
import com.daniel.docify.fileProcessor.ExtendedFileOutputStream;
import com.daniel.docify.fileProcessor.TagDataPair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CEnum extends Enumeration {
    public CEnum() {
    }

    @Override
    public void serialize(ExtendedFileOutputStream out) throws IOException {
        out.writeStringFieldMappingToStream(COMPONENT_NAME_TAG, getName());
        out.writeStringFieldMappingToStream(COMPONENT_FILE_NAME_TAG, getFileName());
        out.writeIntFieldMappingToStream(COMPONENT_LINE_NUM_TAG, getLineNumber());
        out.writeStringFieldMappingToStream(COMPONENT_ENUM_TAG, "");
        out.writeStringFieldMappingToStream(COMPONENT_ENUM_TAG,getEnumType());
        out.writeStringFieldMappingToStream(COMPONENT_ENUM_TAG,getDocumentation());
        out.writeIntFieldMappingToStream(COMPONENT_ENUM_TAG, getMembers().size());
        for (String member : getMembers()){
            out.writeStringFieldMappingToStream(COMPONENT_ENUM_TAG, member);
        }
    }

    public static CEnum deserialize(ExtendedFileInputStream in) throws IOException {
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
        if (pair.tag() == COMPONENT_ENUM_TAG) {
            CEnum enumeration = new CEnum();
            enumeration.setName(name);
            enumeration.setFileName(fileName);
            enumeration.setLineNumber(lineNum);
            // read enum type value
            pair = in.readStringFieldMappingFromStream();
            enumeration.setEnumType(pair.SData());
            // read enum doc
            pair = in.readStringFieldMappingFromStream();
            enumeration.setDocumentation(pair.SData());
            // read enum member size
            pair = in.readIntFieldMappingFromStream();
            int size = pair.IData();
            List<String> members = new ArrayList<>();
            for (int i = 0; i < size; i++){
                // read enum member
                pair = in.readStringFieldMappingFromStream();
                members.add(pair.SData());
            }
            enumeration.setMembers(members);
            return enumeration;
        }
        return null;
    }
}
