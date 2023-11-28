package com.daniel.docify.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class MainWindow extends JFrame implements ActionListener {
    public MainWindow(){
        this.setTitle("Docify Studio");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1500,1000);
        this.setLayout(new BorderLayout());

        new MenuBarUI(this);
        new TreeModelUI(this);

        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
