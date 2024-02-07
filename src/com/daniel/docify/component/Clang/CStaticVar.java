package com.daniel.docify.component.Clang;

import com.daniel.docify.component.StaticVar;
import com.daniel.docify.fileProcessor.ExtendedFileInputStream;
import com.daniel.docify.fileProcessor.ExtendedFileOutputStream;
import com.daniel.docify.fileProcessor.TagDataPair;

import java.io.IOException;

public class CStaticVar extends StaticVar {
    public CStaticVar() {
    }

    @Override
    public void serialize(ExtendedFileOutputStream out) throws IOException {
        out.writeStringFieldMappingToStream(COMPONENT_NAME_TAG, getName());
        out.writeStringFieldMappingToStream(COMPONENT_FILE_NAME_TAG, getFileName());
        out.writeIntFieldMappingToStream(COMPONENT_LINE_NUM_TAG, getLineNumber());
        out.writeStringFieldMappingToStream(COMPONENT_STATIC_TAG, "");
        out.writeStringFieldMappingToStream(COMPONENT_STATIC_TAG, getValue());
    }

    public static CStaticVar deserialize(ExtendedFileInputStream in) throws IOException {
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
        if (pair.tag() == COMPONENT_STATIC_TAG){
            CStaticVar staticVar = new CStaticVar();
            staticVar.setName(name);
            staticVar.setFileName(fileName);
            staticVar.setLineNumber(lineNum);
            // read extern value
            pair = in.readStringFieldMappingFromStream();
            staticVar.setValue(pair.SData());
            return staticVar;
        }
        return null;
    }
}
