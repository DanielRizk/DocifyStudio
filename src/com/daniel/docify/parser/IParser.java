package com.daniel.docify.parser;

import com.daniel.docify.model.FileNodeModel;
import com.daniel.docify.model.fileInfo.CFileInfo;

import java.io.BufferedReader;
import java.io.IOException;

public interface IParser <T>{
    T safeParse(FileNodeModel node, String filetype, BufferedReader reader) throws IOException;

    T parseFile(FileNodeModel node, String filetype);

    String nextLine(BufferedReader reader) throws IOException;
}
