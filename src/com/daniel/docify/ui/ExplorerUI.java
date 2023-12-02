package com.daniel.docify.ui;

import com.daniel.docify.fileProcessor.FileNodeModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

public class ExplorerUI {
    private static JList<String> listView;
    private static DefaultListModel<String> listModel;
    ExplorerUI(@NotNull MainWindow mainWindow, DocDisplayModelUI mainDisplay) {
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

        JTextArea mainDisplayTextArea = mainDisplay.getTextArea();

        listView.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()){
                    String selectedTitle = listView.getSelectedValue();
                    try {
                        scrollToAndHighlight(mainDisplayTextArea, selectedTitle);
                    } catch (BadLocationException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

    }

    private void scrollToAndHighlight(JTextArea mainDisplayTextArea, String title) throws BadLocationException {
        Color highlightColor = new Color(255, 255, 160);
        Highlighter highlighter = mainDisplayTextArea.getHighlighter();
        Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(highlightColor);
        //DefaultHighlighter highlighter = (DefaultHighlighter) mainDisplayTextArea.getHighlighter();
        highlighter.removeAllHighlights();

        String text = mainDisplayTextArea.getText();
        int index = text.indexOf(title);

        int start = mainDisplayTextArea.viewToModel2D(new Point(0, 0));
        int end = mainDisplayTextArea.viewToModel2D(new Point(mainDisplayTextArea.getWidth(), mainDisplayTextArea.getHeight()));

        if (index != -1) {
            try {
                // Select the text in the JTextArea
                mainDisplayTextArea.setSelectionStart(index);
                mainDisplayTextArea.setSelectionEnd(index + title.length());
            } catch (IllegalArgumentException ex) {
                // Ignore the exception if the selection bounds are invalid
            }
            Rectangle viewRect = toRectangle(mainDisplayTextArea.modelToView2D(index));
            centerRectangleInView(mainDisplayTextArea, viewRect);
        }
        while (index >= 0){
            try {
                highlighter.addHighlight(index, index + title.length(), painter);
            }catch (BadLocationException e){
                System.err.println(Arrays.toString(e.getStackTrace()));
            }
            index = text.indexOf(title, index + title.length());
        }
    }
    public static Rectangle toRectangle(Rectangle2D b) {
        if (b == null) {
            return null;
        }/*from w ww . j a  va 2s . c o  m*/
        if (b instanceof Rectangle) {
            return (Rectangle) b;
        } else {
            return new Rectangle((int) b.getX(), (int) b.getY(),
                    (int) b.getWidth(), (int) b.getHeight());
        }
    }
    private void centerRectangleInView(JComponent component, Rectangle rect) {
        Rectangle visibleRect = component.getVisibleRect();
        int targetY = rect.y - (visibleRect.height - rect.height) / 2;

        if (targetY < 0 || rect.height > visibleRect.height) {
            targetY = 0;
        } else if (targetY + visibleRect.height > component.getHeight()) {
            targetY = (component.getHeight() - visibleRect.height)/2;
        }

        component.scrollRectToVisible(new Rectangle(rect.x, targetY, rect.width, rect.height));
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
