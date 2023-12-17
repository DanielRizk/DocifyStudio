package com.daniel.docify.parser2;

/**
 * @brief   This class supports the parser module with some helpful methods
 */
public class ParserUtils {

    /**
     * @brief   This method identifies function declaration and return the function as a string
     */
    public static String extractFunctionName(String line) {
        if (line == null){
            return null;
        }
        if (line.contains("(") && line.contains(")")) {
            return line.substring(0, line.indexOf(")")+1).trim();
        }
        return null;
    }

    /**
     * @brief   this method strips any specified prefix of a string
     *          in a comment block, if prefix is null, strips just
     *          the "*" and return the string
     */
    public static String stripPrefixInCommentBlock(String line, String prefix) {
        if (line == null){
            return null;
        }
        line = line.trim();
        if (line.startsWith("*")){
            if (prefix != null){
                line = line.substring(1).trim().substring(prefix.length()).trim();
            }else {
                line = line.substring(1).trim();
            }
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
}

