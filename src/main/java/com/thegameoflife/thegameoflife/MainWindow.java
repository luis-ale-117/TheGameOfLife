/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thegameoflife.thegameoflife;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 *
 * @author lalex
 */
public class MainWindow extends JFrame {
    private static final int WINDOW_WORLD_LENGTH=700;
    private static final int WINDOW_TOOLS_LENGTH = WINDOW_WORLD_LENGTH/7*2;//200px
    private static final int SCROLLBAR_SIZE=15;
    //Without this space the horizontal scrollbar doesn't fit to the window
    private static final int UNKNOWN_SPACE=60;
    
    private static final int CELLS_X=250;
    private static final int CELLS_Y=250;
    private static final int CELLS_PIXELS = 10;
    
    private static final String[] WORLD_TYPES={"Toroidal","Finite"};
    
    private JScrollPane worldScroller;
    private WorldPanel worldPanel;
    private JMenuBar menuBar;
    private JMenu file;
    private JMenuItem openFile,saveFile;
    
    private JButton pauseAndPlay;
    
    /*BANDERAS*/
    private boolean switchCell,updateGraphs;
    private Lock runGOL;
    private int generation,calculus;
    
    
    public MainWindow(){
        this.setSize(WINDOW_WORLD_LENGTH+WINDOW_TOOLS_LENGTH,WINDOW_WORLD_LENGTH);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Conway's game of life");
        this.setResizable(false);
        this.setLayout(null);
        //Configure all components of the main window for the simulation
        initComponents();
        this.setVisible(true);
        
        //TESTING
        //////////////////////////////
        //CreateWorld
        runGOL.value = false;
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
        menuBar = new JMenuBar();
        file.setText("File");
        openFile.setText("Open");
        saveFile.setText("Save");
        file.add(openFile);
        file.add(saveFile);
        menuBar.add(file);
        this.setJMenuBar(menuBar);
        //Simulation panel
        worldPanel = new WorldPanel(CELLS_PIXELS, CELLS_X, CELLS_Y);
        //Scrollbars
        worldScroller = new JScrollPane();
        worldScroller.setViewportView(worldPanel);
        worldScroller.setBounds(0,0,WINDOW_WORLD_LENGTH,WINDOW_WORLD_LENGTH-UNKNOWN_SPACE);
        worldScroller.getVerticalScrollBar().setPreferredSize(new Dimension(SCROLLBAR_SIZE, 0));
        worldScroller.getHorizontalScrollBar().setPreferredSize(new Dimension(0, SCROLLBAR_SIZE));
        this.add(worldScroller);
        
        //Synchronized object
        runGOL = new Lock();
        //Buttons
        pauseAndPlay = new JButton("Play");
        pauseAndPlay.setBounds(WINDOW_WORLD_LENGTH+5, 93, 80, 30);
        pauseAndPlay.setEnabled(true);
        this.add(pauseAndPlay);
        
        setActions();
        
        switchCell = false;
        updateGraphs = false;
        
        generation = 0;
        calculus=0;
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
                //synchronized(runGOL){//True
                    runGOL.value = !runGOL.value;
                    pauseAndPlay.setText(runGOL.value ? "Pause" : "Play");
                //}
            }
        });
    }
}
