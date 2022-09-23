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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
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
    private static final int REFRESH_TIME = 200;
    private static final int RIGHT_CLICK = MouseEvent.BUTTON1;
    private static final int LEFT_CLICK = MouseEvent.BUTTON3;
    private static final int RIGHT_BUTTON_DOWN = MouseEvent.BUTTON1_DOWN_MASK;
    private static final int LEFT_BUTTON_DOWN = MouseEvent.BUTTON3_DOWN_MASK;
    private static final int MOUSE_BUTTON_DOWN = RIGHT_BUTTON_DOWN | LEFT_BUTTON_DOWN;
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
    private boolean runGOL, generateRandomFlag, eraseFlag, editFlag, cellEditedFlag;
    private double randomProbability;
    private int nextStateTime;
    
    
    public MainWindow(){
        setSize(WINDOW_WORLD_LENGTH,WINDOW_WORLD_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Conway's game of life");
        setResizable(false);
        setLayout(null);
        setIconImage(new ImageIcon("resources/abstractLogo.png").getImage());
        //Configure all components of the main window for the simulation
        initComponents();
        setActions();
        setVisible(true);
        
        randomProbability = 10; //10% of alive cells at the beginning
        nextStateTime = 100;
        //Create a random world at the beginning
        worldPanel.generateRandomWorldPanel(10);//10% of alive cells
        setInfoInLabels();
    }
    public void startGOL(){
        while(true){
            if(runGOL){
                worldPanel.nextWorldPanelState();
                setInfoInLabels();
                try {
                    Thread.sleep(nextStateTime);
                } catch (InterruptedException ex) {
                    System.err.println("ERROR: Sleep failed...");
                }
            }else if(editFlag){
                //Just repaint the world and delegate changes to mouse listeners
                worldPanel.repaint();
                try {
                    Thread.sleep(REFRESH_TIME);
                } catch (InterruptedException ex) {
                    System.err.println("ERROR: Sleep failed...");
                }
            }else if(generateRandomFlag){
                worldPanel.generateRandomWorldPanel(randomProbability);
                setInfoInLabels();
                generateRandomFlag = false;
            }else if(eraseFlag){
                worldPanel.eraseWorldPanel();
                setInfoInLabels();
                eraseFlag = false;
            }
            else{
                worldPanel.repaint();
                try {
                    Thread.sleep(REFRESH_TIME);
                } catch (InterruptedException ex) {
                    System.err.println("ERROR: Sleep failed...");
                }
            }
        }
    }
    private void setInfoInLabels(){
        generationLabel.setText("Gen: "+worldPanel.getWorldPanelGeneration());
        aliveLabel.setText("Alive: "+worldPanel.getWorldPanelAliveCells());
        deathLabel.setText("Death: "+worldPanel.getWorldPanelDeathCells());
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
        worldPanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
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
        generationLabel = new JLabel("Gen: 0");
        generationLabel.setBounds(WINDOW_WORLD_LENGTH-LABEL_LENGTH*3-15, 0, LABEL_LENGTH,TOOLBAR_SIZE );
        generationLabel.setIcon(new ImageIcon("resources/timePurplePink.png"));
        generationLabel.setToolTipText("Generation");
        generationLabel.setEnabled(true);
        //Alive cells label
        aliveLabel = new JLabel("Alive: ");
        aliveLabel.setBounds(WINDOW_WORLD_LENGTH-LABEL_LENGTH*2-15, 0, LABEL_LENGTH,TOOLBAR_SIZE );
        aliveLabel.setIcon(new ImageIcon("resources/aliveRed.png"));
        aliveLabel.setToolTipText("Alive cells");
        aliveLabel.setEnabled(true);
        //Death cells label
        deathLabel = new JLabel("Death: ");
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
    }
    
    public void setActions(){
        pauseAndPlay.addActionListener((ActionEvent e) -> {
            runGOL = !runGOL;
            pauseAndPlay.setIcon(runGOL ? new ImageIcon("resources/pauseRed.png") : new ImageIcon("resources/playGreen.png"));
            pauseAndPlay.setToolTipText(runGOL ? "Pause" : "Play");
        });
        edit.addActionListener((ActionEvent e) -> {
            runGOL = false;
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
            editFlag = !editFlag;
        });
        random.addActionListener((ActionEvent e) -> {
            runGOL = false;
            pauseAndPlay.setIcon(new ImageIcon("resources/playGreen.png"));
            pauseAndPlay.setToolTipText("Play");
            generateRandomFlag = true;
        });
        erase.addActionListener((ActionEvent e) -> {
            runGOL = false;
            pauseAndPlay.setIcon(new ImageIcon("resources/playGreen.png"));
            pauseAndPlay.setToolTipText("Play");
            eraseFlag = true;
        });
        worldPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e){
                if(editFlag){
                    if((e.getModifiersEx()&MOUSE_BUTTON_DOWN)==RIGHT_BUTTON_DOWN) worldPanel.setWorldPanelCellAsAlive(e.getX()/CELLS_PIXELS,e.getY()/CELLS_PIXELS);
                    else if((e.getModifiersEx()&MOUSE_BUTTON_DOWN)==LEFT_BUTTON_DOWN) worldPanel.setWorldPanelCellAsDeath(e.getX()/CELLS_PIXELS,e.getY()/CELLS_PIXELS);
                    setInfoInLabels();
                }
            }
        });
        worldPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                if(editFlag){
                    if(e.getButton() == RIGHT_CLICK) worldPanel.setWorldPanelCellAsAlive(e.getX()/CELLS_PIXELS,e.getY()/CELLS_PIXELS);
                    else if(e.getButton() == LEFT_CLICK)  worldPanel.setWorldPanelCellAsDeath(e.getX()/CELLS_PIXELS,e.getY()/CELLS_PIXELS);
                    setInfoInLabels();
                }
            }
        });
    }
}