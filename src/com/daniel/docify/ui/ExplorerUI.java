package com.daniel.docify.ui;

import com.daniel.docify.fileProcessor.FileNodeModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class ExplorerUI {
    private static JList<String> listView;
    private static DefaultListModel<String> listModel;
    ExplorerUI(@NotNull MainWindow mainWindow) {
        // Create an empty root node for the initial state
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setCaret(new HiddenCaret());

        // Set up a scroll pane for the tree
        JScrollPane scrollPane = new JScrollPane(textArea);
        updateScrollPaneWidth(scrollPane, mainWindow.getWidth()); // Set initial width

        // Add a component listener to track MainWindow resize events
        mainWindow.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateScrollPaneWidth(scrollPane, mainWindow.getWidth());
            }
        });
        listModel = new DefaultListModel<>();
        listView = new JList<>(listModel);
        scrollPane.setViewportView(listView);
        mainWindow.add(scrollPane, BorderLayout.EAST);
    }

    public static void updateExplorer(List<String> funcNames){
        listModel.clear();
        for (String funcName : funcNames){
            listModel.addElement(funcName);
        }
    }

    private void updateScrollPaneWidth(JScrollPane scrollPane, int mainWindowWidth) {
        // Set the preferred size of the scroll pane based on the MainWindow width
        scrollPane.setPreferredSize(new Dimension(mainWindowWidth / 6, 200));
        scrollPane.revalidate();
    }
    static class HiddenCaret extends DefaultCaret {
        @Override
        public void paint(Graphics g) {
            // Do not paint the caret
        }
    }
}
