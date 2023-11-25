package com.daniel.docify.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public interface FunctionParser<T> {
    public T parseFile(BufferedReader reader) throws IOException;
}
