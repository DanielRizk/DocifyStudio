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


    void serialize(ExtendedFileOutputStream out) throws IOException;

    static <T extends DociSerializable> T deserialize(ExtendedFileInputStream in, Class<T> clazz) {
        // Placeholder for a generic deserialization method;
        throw new UnsupportedOperationException("Deserialization not implemented");
    }
}
