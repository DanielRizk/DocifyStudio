package com.daniel.docify.ui;

import com.daniel.docify.core.ActionManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MenuBarUI extends JFrame implements ActionListener {

    // declare menu bar components
    private final JMenuItem openMenuItem;
    private final JMenuItem closeMenuItem;
    private final JMenuItem cProjectItem;
    private final JMenuItem javaProjectItem;
    private final JMenuItem pythonProjectItem;
    private final JMenuItem savePDFItem;
    private final JMenuItem saveDociItem;

    // MenuBarUI constructor
    MenuBarUI(@NotNull MainWindow mainWindow){

        // create the menu bar itself
        JMenuBar menuBar = new JMenuBar();

        // create file and help menus
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");

        // create file menu items
        JMenu newSubMenu = createMenu("New", "icons/png/new.png", 20, 20);
        JMenu saveSubMenu = createMenu("Save As", "icons/png/save.png", 20, 20);
        openMenuItem = createMenuItem("Open", "icons/png/open.png", 20, 20);
        closeMenuItem = createMenuItem("Close", "icons/png/close.png", 20, 20);

        // create new sub-menu items
        cProjectItem = createMenuItem("C Project", "icons/png/cprog.png", 20, 20);
        javaProjectItem = createMenuItem("Java Project", "icons/png/javaprog.png", 20, 20);
        pythonProjectItem = createMenuItem("Python Project", "icons/png/pyprog.png", 20, 20);

        // create save as sub-menu items
        saveDociItem = createMenuItem("Docify", "icons/png/docify.png", 20, 20);
        savePDFItem = createMenuItem("PDF", "icons/png/pdf.png", 20, 20);

        // add items to new sub-menu
        newSubMenu.add(cProjectItem);
        newSubMenu.add(javaProjectItem);
        newSubMenu.add(pythonProjectItem);

        // add items to save as sub-menu
        saveSubMenu.add(saveDociItem);
        saveSubMenu.add(savePDFItem);

        // add items to file menu
        fileMenu.add(newSubMenu);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveSubMenu);
        fileMenu.add(closeMenuItem);

        // add file and help menu to the menu bar
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        // add action listener to menu items
        openMenuItem.addActionListener(this);
        closeMenuItem.addActionListener(this);
        cProjectItem.addActionListener(this);
        javaProjectItem.addActionListener(this);
        pythonProjectItem.addActionListener(this);
        saveDociItem.addActionListener(this);
        savePDFItem.addActionListener(this);

        // add the menu bat to the main window
        mainWindow.setJMenuBar(menuBar);

    }

    // create JMenuItem with icon if available
    private static JMenuItem createMenuItem(String text, String iconName, int width, int height) {
        JMenuItem menuItem = new JMenuItem(text);
        if (iconName != null) {
            ImageIcon icon = new ImageIcon(iconName);
            Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            menuItem.setIcon(scaledIcon);
        }
        return menuItem;
    }

    // create JMenu with icon if available
    private static JMenu createMenu(String text, String iconName, int width, int height) {
        JMenu menu = new JMenu(text);
        if (iconName != null) {
            ImageIcon icon = new ImageIcon(iconName);
            Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            menu.setIcon(scaledIcon);
        }
        return menu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openMenuItem){
            System.out.println("this is open");
            try {
                ActionManager.openDociFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (e.getSource() == closeMenuItem){
            System.out.println("this is exit");
            try {
                ActionManager.closeFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (e.getSource() == cProjectItem){
            System.out.println("this is C");
            try {
                ActionManager.startCLang();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (e.getSource() == javaProjectItem){
            System.out.println("this is java");
        }
        if (e.getSource() == pythonProjectItem){
            System.out.println("this is py");
        }
        if (e.getSource() == saveDociItem){
            System.out.println("this is save doci");
            try {
                ActionManager.saveDocify();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (e.getSource() == savePDFItem){
            System.out.println("this is save pdf");
        }

    }
}







