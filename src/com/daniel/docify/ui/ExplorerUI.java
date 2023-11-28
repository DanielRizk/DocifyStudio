package com.daniel.docify.ui;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ExplorerUI {
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

        mainWindow.add(scrollPane, BorderLayout.EAST);
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
