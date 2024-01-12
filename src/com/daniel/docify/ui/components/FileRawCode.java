package com.daniel.docify.ui.components;

import com.daniel.docify.ui.Controller;
import com.daniel.docify.ui.utils.ControllerUtils;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileRawCode extends ControllerUtils {

    private final CodeArea codeView = new CodeArea();

    public CodeArea getCodeView(){
        return codeView;
    }


    public VirtualizedScrollPane<CodeArea> codeAreaScrollPane = new VirtualizedScrollPane<>(codeView);


    private static final String[] KEYWORDS = new String[] {
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const", "int",
            "uint8_t","uint16_t","uint32_t","uin64_t","bool",
            "float","struct","typedef", "static", "while",
            "transient", "try", "void", "volatile",
            "do", "double", "for", "if", "else",
            "size_t", "switch", "case", "default"
    };

    private static final String[] PREPROCESSORS = new String[] {
            "#define", "#include", "#ifdef", "#ifndef", "#else", "#if defined"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PREPROCESSOR_PATTERN = "(" + String.join("|", PREPROCESSORS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(\\)";
    private static final String BRACE_PATTERN = "\\{\\}";
    private static final String BRACKET_PATTERN = "\\[\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PREPROCESSOR>" + PREPROCESSOR_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
    public FileRawCode(Controller controller) {
        super(controller);
    }

    public StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        while (matcher.find()) {
            String styleClass = getStyleClass(matcher);
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    @NotNull
    private static String getStyleClass(Matcher matcher) {
        String styleClass =
                matcher.group("KEYWORD") != null ? "keyword" :
                        matcher.group("PREPROCESSOR") != null ? "preprocessor" :
                                matcher.group("PAREN") != null ? "paren" :
                                        matcher.group("BRACE") != null ? "brace" :
                                                matcher.group("BRACKET") != null ? "bracket" :
                                                        matcher.group("SEMICOLON") != null ? "semicolon" :
                                                                matcher.group("STRING") != null ? "string" :
                                                                        matcher.group("COMMENT") != null ? "comment" :
                                                                                null; /* never happens */
        assert styleClass != null;
        return styleClass;
    }

    public void initializeCodeArea(){
        codeView.setEditable(false);
        codeView.setParagraphGraphicFactory(LineNumberFactory.get(codeView));
        try {
            loadCodeAreaStylesheet();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        codeView.textProperty().addListener((obs, oldText, newText) -> {
            codeView.setStyleSpans(0, computeHighlighting(newText));
        });
    }

    private void loadCodeAreaStylesheet() throws IOException {
        File file = new File("src/com/daniel/docify/ui/styling/syntax.css");
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + "src/com/daniel/docify/ui/syntax.css");
        }
        String stylesheet = file.toURI().toURL().toExternalForm();
        codeView.getStylesheets().add(stylesheet);
    }
}
