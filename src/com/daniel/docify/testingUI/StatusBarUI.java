package com.daniel.docify.testingUI;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class StatusBarUI {
    StatusBarUI(@NotNull MainWindow mainWindow){
        // Create an empty root node for the initial state
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setCaret(new ExplorerUI.HiddenCaret());


        // Set up a scroll pane for the tree
        JPanel jPanel = new JPanel();
        jPanel.add(textArea);
        jPanel.setPreferredSize(new Dimension(mainWindow.getPreferredSize().width, 20));
        mainWindow.add(jPanel,BorderLayout.SOUTH);
    }
}
