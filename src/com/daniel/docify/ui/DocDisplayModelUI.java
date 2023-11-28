package com.daniel.docify.ui;

import com.daniel.docify.model.FileInfoModel;
import com.daniel.docify.model.FunctionModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.RoundRectangle2D;

public class DocDisplayModelUI extends JFrame {
    static Container mainPane;
    DocDisplayModelUI(@NotNull MainWindow mainWindow){

        mainPane = getContentPane();
        mainPane.setLayout(new FlowLayout());
        mainPane.setBackground(Color.WHITE);


//        JTextPane textPane = new JTextPane();
//        textPane.setCaret(new HiddenCaret());


        JScrollPane scrollPane = new JScrollPane(mainPane);
        scrollPane.addMouseWheelListener(new CustomMouseWheelListener());
        mainWindow.add(scrollPane, BorderLayout.CENTER);

    }
    public static void updateDisplayModelUI(FileInfoModel fileInfo){
        mainPane.removeAll();

        if (fileInfo != null) {

            if (fileInfo.getFunctionModel() != null) {
                JTextArea text = new JTextArea();
                Font customFont = new Font("Calibri", Font.BOLD, 24); // Replace "Arial" with your desired font
                text.setFont(customFont);
                text.setForeground(Color.DARK_GRAY); // Replace Color.BLUE with your desired color


                for (FunctionModel function : fileInfo.getFunctionModel()) {
                    if (function.getName() != null)text.append("Function Name: " + function.getName() + "\n");
                    if (function.getDocumentation().getFunctionBrief() != null)text.append("Function Brief: " + function.getDocumentation().getFunctionBrief()+ "\n");
                    for(String params : function.getDocumentation().getFunctionParams()) if (params != null) text.append("Function Param: " + params+ "\n");
                    if (function.getDocumentation().getReturn() != null) text.append("Function Return: " + function.getDocumentation().getReturn()+ "\n");
                    if (function.getDocumentation().getNotes() != null) text.append("Note: " + function.getDocumentation().getNotes() + "\n");
                    if (function.getLineNumber() != null) text.append("Declared on line: " + function.getLineNumber() + "\n\n");
                }
                mainPane.add(text);
            }
        }
        // Repaint the mainPane
        mainPane.revalidate();
        mainPane.repaint();
    }
    static class HiddenCaret extends DefaultCaret {
        @Override
        public void paint(Graphics g) {
            // Do not paint the caret
        }
    }
    static class CustomMouseWheelListener implements MouseWheelListener {
        private static final int SCROLL_SPEED = 10; // Adjust this value to control the scroll speed

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            JScrollBar verticalScrollBar = ((JScrollPane) e.getComponent()).getVerticalScrollBar();

            // Determine the direction of the scroll (positive for up, negative for down)
            int direction = (e.getWheelRotation() < 0) ? -1 : 1;

            // Adjust the scroll amount based on the scroll speed
            int scrollAmount = SCROLL_SPEED * e.getScrollAmount();

            // Calculate the new value for the vertical scrollbar
            int newValue = verticalScrollBar.getValue() + direction * scrollAmount;

            // Ensure the new value is within the valid range
            newValue = Math.max(verticalScrollBar.getMinimum(), Math.min(newValue, verticalScrollBar.getMaximum()));

            // Set the new value to scroll
            verticalScrollBar.setValue(newValue);
        }
    }
    static class RoundedPanel extends JPanel
    {
        private Color backgroundColor;
        private int cornerRadius = 15;

        public RoundedPanel(LayoutManager layout, int radius) {
            super(layout);
            cornerRadius = radius;
        }

        public RoundedPanel(LayoutManager layout, int radius, Color bgColor) {
            super(layout);
            cornerRadius = radius;
            backgroundColor = bgColor;
        }

        public RoundedPanel(int radius) {
            super();
            cornerRadius = radius;
        }

        public RoundedPanel(int radius, Color bgColor) {
            super();
            cornerRadius = radius;
            backgroundColor = bgColor;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getWidth();
            int height = getHeight();
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            //Draws the rounded panel with borders.
            if (backgroundColor != null) {
                graphics.setColor(backgroundColor);
            } else {
                graphics.setColor(getBackground());
            }
            graphics.fillRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height); //paint background
            graphics.setColor(getForeground());
            graphics.drawRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height); //paint border
        }
    }
}

