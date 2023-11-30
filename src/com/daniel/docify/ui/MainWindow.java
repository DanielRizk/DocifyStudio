package com.daniel.docify.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class MainWindow extends JFrame implements ActionListener {
    public MainWindow() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e){
            e.printStackTrace();
        }
        this.setTitle("Docify Studio");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setDefaultLookAndFeelDecorated(true);
        this.setSize(1500,1000);
        this.setLayout(new BorderLayout());

        new MenuBarUI(this);
        new StatusBarUI(this);
        new TreeModelUI(this);
        new DocDisplayModelUI(this);
        new ExplorerUI(this);


        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Retrieve the width after the frame is resized
                int width = getWidth();
                System.out.println("Frame Width: " + width);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
