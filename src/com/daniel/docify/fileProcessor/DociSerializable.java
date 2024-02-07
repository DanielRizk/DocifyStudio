package com.daniel.docify.fileProcessor;

import java.io.IOException;

public interface DociSerializable {

    Integer FILE_FORMAT_MODEL_TAG          = 0x10;
    Integer FILE_VERSION_TAG               = 0x11;
    Integer SOFTWARE_VERSION_TAG           = 0x12;
    Integer AUTHOR_NAME_TAG                = 0x13;
    Integer CREATION_DATE_TAG              = 0x14;
    Integer SAVED_LOCATION_TAG             = 0x15;
    Integer FILE_NODE_MODEL_TAG            = 0x20;
    Integer FILE_NODE_NAME_TAG             = 0x21;
    Integer FILE_NODE_TYPE_TAG             = 0x22;
    Integer FILE_NODE_IS_FILE_TAG          = 0x23;
    Integer FILE_NODE_PATH_TAG             = 0x24;
    Integer FILE_NODE_CHILD_COUNT_TAG      = 0x25;
    Integer FILE_INFO_MODEL_TAG            = 0x30;
    Integer FILE_INFO_MODEL_INVALID_TAG    = 0x31;
    Integer FILE_INFO_NAME_TAG             = 0x32;
    Integer FILE_INFO_P_TYPE_TAG           = 0x33;
    Integer FILE_INFO_CONTENT_TAG          = 0x34;
    Integer FILE_INFO_TYPE_TAG             = 0x35;
    Integer FILE_INFO_COMP_COUNT_TAG       = 0x36;
    Integer FILE_INFO_COMP_LIST_SIZE_TAG   = 0x37;
    Integer FILE_INFO_EXTERN_LIST_TAG      = 0x38;
    Integer FILE_INFO_MACRO_LIST_TAG       = 0x39;
    Integer FILE_INFO_STATIC_LIST_TAG      = 0x3A;
    Integer FILE_INFO_ENUM_LIST_TAG        = 0x3B;
    Integer FILE_INFO_STRUCT_LIST_TAG      = 0x3C;
    Integer FILE_INFO_METHOD_LIST_TAG      = 0x3D;
    Integer COMPONENT_NAME_TAG             = 0x40;
    Integer COMPONENT_FILE_NAME_TAG        = 0x41;
    Integer COMPONENT_LINE_NUM_TAG         = 0x42;
    Integer COMPONENT_EXTERN_TAG           = 0x43;
    Integer COMPONENT_MACRO_TAG            = 0x44;
    Integer COMPONENT_STATIC_TAG           = 0x45;
    Integer COMPONENT_ENUM_TAG             = 0x46;
    Integer COMPONENT_STRUCT_TAG           = 0x47;
    Integer COMPONENT_METHOD_TAG           = 0x48;

    static int getTagNameForField(String name){
        return switch (name) {
            case "externs" -> FILE_INFO_EXTERN_LIST_TAG;
            case "macros" -> FILE_INFO_MACRO_LIST_TAG;
            case "staticVars" -> FILE_INFO_STATIC_LIST_TAG;
            case "enums" -> FILE_INFO_ENUM_LIST_TAG;
            case "structs" -> FILE_INFO_STRUCT_LIST_TAG;
            case "functions" -> FILE_INFO_METHOD_LIST_TAG;
            default -> 0xFF;
        };
    }

    void serialize(ExtendedFileOutputStream out) throws IOException;

    static <T extends DociSerializable> T deserialize(ExtendedFileInputStream in, Class<T> clazz) {
        // Placeholder for a generic deserialization method;
        throw new UnsupportedOperationException("Deserialization not implemented");
    }
}
