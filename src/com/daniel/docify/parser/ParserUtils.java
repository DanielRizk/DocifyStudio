package com.daniel.docify.parser;

public class ParserUtils {

    public static String extractFunctionName(String line) {
        if (line == null){
            return null;
        }
        if (line.contains("(") && line.contains(")")) {
            return line.substring(0, line.indexOf(")")+1).trim();
        }
        return null;
    }

    public static String stripPrefix(String line, String prefix) {
        if (line == null){
            return null;
        }
        line = line.trim();
        if (line.startsWith("*")){
            line = line.substring(1).trim().substring(prefix.length()).trim();
            if(line.contains("[")) {
                while (line.contains("]")) {
                    line = line.substring(1);
                }
                line = line.trim();
            }
            return line;
        }
        return null;
    }

    public static String stripBlockCommentSyntax(String line) {
        if (line == null){
            return null;
        }
        line = line.trim();
        if (line.startsWith("*")){
            line = line.substring(1).trim();
            return line;
        }
        return null;
    }
}

