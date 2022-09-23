/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thegameoflife.thegameoflife;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 *
 * @author lalex
 */
public class MainWindow extends JFrame {
    private static final int WINDOW_WORLD_LENGTH=900;
    private static final int WINDOW_WORLD_HEIGHT=700;
    private static final int TOOLBAR_SIZE = 30;
    private static final int SCROLLBAR_SIZE=15;
    private static final int UNKNOWN_SPACE=60;//Without this space the horizontal scrollbar doesn't fit to the window
    private static final int BUTTON_LENGTH=35;
    private static final int LABEL_LENGTH=140;
    private static final int COMBOBOX_LENGTH = 65;
    private static final int CELLS_X=250;
    private static final int CELLS_Y=250;
    private static final int CELLS_PIXELS = 10;
    private static final String[] WORLD_TYPES={"Toroidal","Finite"};
    //Components
    private JScrollPane worldScroller;
    private WorldPanel worldPanel;
    private JMenuBar menuBar;
    private JMenu file,editMenu;
    private JMenuItem openFile,saveFile;
    private JMenuItem editWolrdColors,editWroldType;
    private JToolBar toolbar;
    private JButton pauseAndPlay,edit,random,erase;
    private JComboBox zoomOptions, speedOptions;
    private JLabel zoomIcon,speedIcon,generationLabel,aliveLabel,deathLabel;
    //Others
    private Lock runGOL;
    private double randomProbability;
    
    
    public MainWindow(){
        this.setSize(WINDOW_WORLD_LENGTH,WINDOW_WORLD_HEIGHT);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Conway's game of life");
        this.setResizable(false);
        this.setLayout(null);
        this.setIconImage(new ImageIcon("resources/abstractLogo.png").getImage());
        //Configure all components of the main window for the simulation
        initComponents();
        setActions();
        this.setVisible(true);
        
        randomProbability = 10; //10% of alive cells
        //TESTING
        //////////////////////////////
        //Object used to pause/play GOL simulation
        runGOL = new Lock();
        runGOL.setValue(false);
        //Create a random world at the beginning
        worldPanel.setInfoLables(generationLabel, aliveLabel, deathLabel);
        worldPanel.generateRandomWorldPanel(10);
        worldPanel.setLockGOL(runGOL);    
        new Thread(worldPanel).start();
        /////////////////////////////
    }
    private void initComponents(){
        
        //Menu Bar
        openFile = new JMenuItem();
        saveFile = new JMenuItem();
        file =  new JMenu();
        editWolrdColors = new JMenuItem();
        editWroldType = new JMenuItem();
        editMenu =  new JMenu();
        menuBar = new JMenuBar();
        file.setText("File");
        openFile.setText("Open");
        saveFile.setText("Save");
        file.add(openFile);
        file.add(saveFile);
        editMenu.setText("Edit");
        editWolrdColors.setText("World color");
        editWroldType.setText("World type");
        editMenu.add(editWolrdColors);
        editMenu.add(editWroldType);
        menuBar.add(file);
        menuBar.add(editMenu);
        this.setJMenuBar(menuBar);
        //Simulation
        worldPanel = new WorldPanel(CELLS_PIXELS, CELLS_X, CELLS_Y);
        //Scrollbars
        worldScroller = new JScrollPane();
        worldScroller.setViewportView(worldPanel);
        worldScroller.setBounds(0,TOOLBAR_SIZE,WINDOW_WORLD_LENGTH-SCROLLBAR_SIZE,WINDOW_WORLD_HEIGHT-TOOLBAR_SIZE-UNKNOWN_SPACE);
        worldScroller.getVerticalScrollBar().setPreferredSize(new Dimension(SCROLLBAR_SIZE, 0));
        worldScroller.getHorizontalScrollBar().setPreferredSize(new Dimension(0, SCROLLBAR_SIZE));
        this.add(worldScroller);
        //Pause and play button
        pauseAndPlay = new JButton();
        pauseAndPlay.setBounds(0, 0, BUTTON_LENGTH,TOOLBAR_SIZE );
        pauseAndPlay.setBorderPainted(false);
        pauseAndPlay.setFocusPainted(false);
        pauseAndPlay.setBackground(Color.white);
        pauseAndPlay.setIcon(new ImageIcon("resources/playGreen.png"));
        pauseAndPlay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pauseAndPlay.setToolTipText("Play");
        pauseAndPlay.setEnabled(true);
        //Edit button
        edit = new JButton();
        edit.setBounds(BUTTON_LENGTH*2, 0, BUTTON_LENGTH,TOOLBAR_SIZE );
        edit.setBorderPainted(false);
        edit.setFocusPainted(false);
        edit.setBackground(Color.white);
        edit.setIcon(new ImageIcon("resources/editYellow.png"));
        edit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        edit.setToolTipText("Edit");
        edit.setEnabled(true);
        //Random button
        random = new JButton();
        random.setBounds(BUTTON_LENGTH*3, 0, BUTTON_LENGTH,TOOLBAR_SIZE );
        random.setBorderPainted(false);
        random.setFocusPainted(false);
        random.setBackground(Color.white);
        random.setIcon(new ImageIcon("resources/randomPinkBlue.png"));
        random.setCursor(new Cursor(Cursor.HAND_CURSOR));
        random.setToolTipText("Random");
        random.setEnabled(true);
        //Erase button
        erase = new JButton();
        erase.setBounds(BUTTON_LENGTH*4, 0, BUTTON_LENGTH,TOOLBAR_SIZE );
        erase.setBorderPainted(false);
        erase.setFocusPainted(false);
        erase.setBackground(Color.white);
        erase.setIcon(new ImageIcon("resources/eraseAqua.png"));
        erase.setCursor(new Cursor(Cursor.HAND_CURSOR));
        erase.setToolTipText("Erase");
        erase.setEnabled(true);
        //Zoom options
        zoomIcon = new JLabel();
        zoomIcon.setBounds(BUTTON_LENGTH*6, 0, BUTTON_LENGTH,TOOLBAR_SIZE );
        zoomIcon.setBackground(Color.white);
        zoomIcon.setIcon(new ImageIcon("resources/zoomYellow.png"));
        zoomIcon.setEnabled(true);
        zoomOptions = new JComboBox<String>();
        zoomOptions.setBounds(BUTTON_LENGTH*7, 0, COMBOBOX_LENGTH, TOOLBAR_SIZE);
        zoomOptions.setBackground(Color.white);
        zoomOptions.setCursor(new Cursor(Cursor.HAND_CURSOR));
        zoomOptions.setToolTipText("Zoom");
        zoomOptions.setEnabled(true);
        zoomOptions.addItem("25%");
        zoomOptions.addItem("50%");
        zoomOptions.addItem("100%");
        zoomOptions.addItem("150%");
        zoomOptions.addItem("200%");
        zoomOptions.setSelectedIndex(2);
        //Speed options
        speedIcon = new JLabel();
        speedIcon.setBounds(BUTTON_LENGTH*7+COMBOBOX_LENGTH+5, 0, BUTTON_LENGTH,TOOLBAR_SIZE );
        speedIcon.setBackground(Color.white);
        speedIcon.setIcon(new ImageIcon("resources/speedColors.png"));
        speedIcon.setEnabled(true);
        speedOptions = new JComboBox<String>();
        speedOptions.setBounds(BUTTON_LENGTH*8+COMBOBOX_LENGTH, 0, COMBOBOX_LENGTH, TOOLBAR_SIZE);
        speedOptions.setBackground(Color.white);
        speedOptions.setCursor(new Cursor(Cursor.HAND_CURSOR));
        speedOptions.setToolTipText("Speed");
        speedOptions.setEnabled(true);
        speedOptions.addItem("2s");
        speedOptions.addItem("1s");
        speedOptions.addItem("0.5s");
        speedOptions.addItem("0.1s");
        speedOptions.addItem("0.05s");
        speedOptions.addItem("MAX");
        speedOptions.setSelectedIndex(2);
        //Generation label
        generationLabel = new JLabel("Gen: 10");
        generationLabel.setBounds(WINDOW_WORLD_LENGTH-LABEL_LENGTH*3-15, 0, LABEL_LENGTH,TOOLBAR_SIZE );
        generationLabel.setIcon(new ImageIcon("resources/timePurplePink.png"));
        generationLabel.setToolTipText("Generation");
        generationLabel.setEnabled(true);
        //Alive cells label
        aliveLabel = new JLabel("Alive: 500");
        aliveLabel.setBounds(WINDOW_WORLD_LENGTH-LABEL_LENGTH*2-15, 0, LABEL_LENGTH,TOOLBAR_SIZE );
        aliveLabel.setIcon(new ImageIcon("resources/aliveRed.png"));
        aliveLabel.setToolTipText("Alive cells");
        aliveLabel.setEnabled(true);
        //Death cells label
        deathLabel = new JLabel("Death: 1000");
        deathLabel.setBounds(WINDOW_WORLD_LENGTH-LABEL_LENGTH-15, 0, LABEL_LENGTH,TOOLBAR_SIZE );
        deathLabel.setIcon(new ImageIcon("resources/deathGreen.png"));
        deathLabel.setToolTipText("Death cells");
        deathLabel.setEnabled(true);
        //Toolbar
        toolbar = new JToolBar();
        toolbar.setLayout(null);
        toolbar.setBounds(0, 0, 900,30);
        toolbar.setBackground(Color.white);
        toolbar.setBorderPainted(false);
        toolbar.setFloatable(false);
        toolbar.add(pauseAndPlay);
        toolbar.add(edit);
        toolbar.add(random);
        toolbar.add(erase);
        toolbar.add(zoomIcon);
        toolbar.add(zoomOptions);
        toolbar.add(speedIcon);
        toolbar.add(speedOptions);
        toolbar.add(generationLabel);
        toolbar.add(aliveLabel);
        toolbar.add(deathLabel);
        this.add(toolbar);
        /*
        JPanel jp = new JPanel();
        jp.setPreferredSize(new Dimension(100,700));
        jp.setBackground(Color.red);
        jp.setBounds(700, 0, 100, 640);
        jp.setVisible(true);
        this.add(jp);
        */
    }
    
    public void setActions(){
        pauseAndPlay.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                runGOL.switchValue();
                pauseAndPlay.setIcon(runGOL.getValue() ? new ImageIcon("resources/pauseRed.png") : new ImageIcon("resources/playGreen.png"));
                pauseAndPlay.setToolTipText(runGOL.getValue() ? "Pause" : "Play");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    System.err.println("ERROR: Sleep failed...");
                }
            }
        });
        edit.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                runGOL.setValue(false);
                pauseAndPlay.setIcon(new ImageIcon("resources/playGreen.png"));
                pauseAndPlay.setToolTipText("Play");
                if(pauseAndPlay.isEnabled()){
                    pauseAndPlay.setEnabled(false);
                    random.setEnabled(false);
                    erase.setEnabled(false);
                    edit.setIcon(new ImageIcon("resources/uneditYellow.png"));
                    edit.setToolTipText("Stop edition");
                }else{
                    pauseAndPlay.setEnabled(true);
                    random.setEnabled(true);
                    erase.setEnabled(true);
                    edit.setIcon(new ImageIcon("resources/editYellow.png"));
                    edit.setToolTipText("Edit");
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    System.err.println("ERROR: Sleep failed...");
                }
                
            }
        });
        random.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                runGOL.setValue(false);
                pauseAndPlay.setIcon(new ImageIcon("resources/playGreen.png"));
                pauseAndPlay.setToolTipText("Play");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    System.err.println("ERROR: Sleep failed...");
                }
                worldPanel.generateRandomWorldPanel(randomProbability);
            }
        });
        erase.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                runGOL.setValue(false);
                pauseAndPlay.setIcon(new ImageIcon("resources/playGreen.png"));
                pauseAndPlay.setToolTipText("Play");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    System.err.println("ERROR: Sleep failed...");
                }
                worldPanel.eraseWorldPanel();
            }
        });
    }
}
