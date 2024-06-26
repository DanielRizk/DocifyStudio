package com.daniel.docify.ui.components;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;

public class HelpMenu{
    public HelpMenu() {
        Stage help = new Stage();

        try {
            File iconFile = new File("resources/assets/icons/help.png");
            String iconUrl = iconFile.toURI().toURL().toExternalForm();
            Image icon = new Image(iconUrl);
            help.getIcons().add(icon);
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }

        WebView webView = new WebView();

        // HTML content with styled sections
        String htmlContent = "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>Help</title>" +
                "<style>" +
                "  body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f0f0f0; }" +
                "  .container { width: 100%; margin: 0; padding: 20px; background-color: #fff; box-sizing: border-box; }" +
                "  .section { margin-bottom: 20px; }" +
                "  .section-header { background-color: #cccccc; padding: 10px; font-size: 18px; font-weight: bold; }" +
                "  .section-content { background-color: #ffffff; padding: 10px; font-family: monospace; white-space: pre-wrap; border: none; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                "<div class=\"section\">" +
                "<div class=\"section-header\">Introduction</div>" +
                "<div class=\"section-content\">" +
                "<b>DocifyStudio</b> is a software used to generate documentation for programming projects based on the comments found in the source code itself. <br><br>" +
                "The documentation generated by the software is presented in a interactive graphical way, allowing the user to search and revise all the components of the source code.<br><br>" +
                "<b>Usage:</b><br>Start by creating a new project and choose the root dir of your project, and then the software will generate a documentation for every file of you project if it has a valid documentation syntax.<br><br>" +
                "User can remove file or directories from the project files by right-click on the file and then <b>remove form project</b> option, note that remove from project option only removes the file/dir from the documentation and not from the hard drive.<br><br>" +
                "If the user wishes to exclude certain files/dirs before opening the project, DocifyStudio allows this by creating a file called <b>doci.ignore</b> in the root directory of the project and in this file add the relative path of the file/dir you want " +
                "to exclude, each in a separate line i.g. <br><br>" +
                "<b>/relative/path/to/directory</b><br>" +
                "<b>/relative/path/to/file.cpp</b><br><br>" +
                "After generating the documentation, user can save the project as doci file which allows the user to open it again directly with all configuration, or as pdf file to combine the whole project in one file.<br><br>" +
                "When a project is first created, DocifyStudio keeps a watch service on the project directory so if any modification happened to a file/dir content, file/dir re-location, file/dir addition or file/dir deletion, will be reflected directly " +
                "to the created documentation allowing better experience for the user. Note that when project is saved/opened the watch service is terminated." +
                "</div>" +
                "</div>" +
                "<div class=\"section\">" +
                "<div class=\"section-header\">Comments syntax</div>" +
                "<div class=\"section-content\">" +
                "In order to have seamless experience please follow the comment syntax convention in your code.<br><br>" +
                "To document an <b>Enum</b>, make sure to add this comment before the enum itself:<br><br>" +
                "<b>/**<br>" +
                "* This enum &lt add your documentation and description here! &gt<br>" +
                "*/</b><br><br>" +
                "To document a <b>Struct</b>, make sure to add this comment before the struct itself:<br><br>" +
                "<b>/**<br>" +
                "* This struct &lt add your documentation and description here! &gt<br>" +
                "*/</b><br><br>" +
                "To document a <b>Method</b>(in java/python) or a <b>Function</b>(in C/C++), make sure to add this comment before the method itself:<br><br>" +
                "<b>/**<br>" +
                "* This function &lt add your documentation and description here! &gt<br>" +
                "*/</b><br><br>" +
                "<b>/**<br>" +
                "* This method &lt add your documentation and description here! &gt<br>" +
                "*/</b><br><br>" +
                "Other components such Macros, Static variables and externs are detected automatically without comment syntax, Note that these components do not support documentation." +
                "</div>" +
                "</div>" +
                "<div class=\"section\">" +
                "<div class=\"section-header\"><b>DocifyStudio v1.0</b></div>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        webView.getEngine().loadContent(htmlContent);

        StackPane root = new StackPane(webView);
        Scene scene = new Scene(root);

        help.setTitle("Help");
        help.setResizable(false);
        help.setWidth(900);
        help.setHeight(600);
        help.setScene(scene);
        help.show();
    }
}
