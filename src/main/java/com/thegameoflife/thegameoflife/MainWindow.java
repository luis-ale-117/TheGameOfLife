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
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author lalex
 */
public class MainWindow extends JFrame {
    private static final int WINDOW_WORLD_LENGTH=1100;
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
    private JMenuItem loadTxt,saveAsTxt,saveImg;
    private JMenuItem editWolrdColors,editWroldType,randomness;
    private JToolBar toolbar;
    private JButton pauseAndPlay,edit,random,erase,ruleButton;
    private JComboBox zoomOptions, speedOptions;
    private JLabel zoomIcon,speedIcon,generationLabel,aliveLabel,deathLabel;
    //Others
    private boolean runGOL, generateRandomFlag, eraseFlag, editFlag, cellEditedFlag;
    private double randomAliveCellProbability;
    private int nextStateSpeed;
    private double[] zoomValues = {0.25,0.5,1,1.5,2};
    private int[] speedValues = {2000,1000,500,100,50,10,0};//Miliseconds
    
    
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
        
        randomAliveCellProbability = 10; //10% of alive cells at the beginning
        nextStateSpeed = 100;
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
                    Thread.sleep(nextStateSpeed);
                } catch (InterruptedException ex) {
                    JOptionPane.showMessageDialog(this,
                    "Error when sleeping after refreshing world\nError: "+ex.getMessage(),
                    "Refresh error",
                    JOptionPane.ERROR_MESSAGE);
                }
            }else if(editFlag){
                //Just repaint the world and delegate changes to mouse listeners
                worldPanel.repaint();
                try {
                    Thread.sleep(REFRESH_TIME);
                } catch (InterruptedException ex) {
                    JOptionPane.showMessageDialog(this,
                    "Error when sleeping after refreshing world\nError: "+ex.getMessage(),
                    "Refresh error",
                    JOptionPane.ERROR_MESSAGE);
                }
            }else if(generateRandomFlag){
                worldPanel.generateRandomWorldPanel(randomAliveCellProbability);
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
                    JOptionPane.showMessageDialog(this,
                    "Error when sleeping after refreshing world\nError: "+ex.getMessage(),
                    "Refresh error",
                    JOptionPane.ERROR_MESSAGE);
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
        loadTxt = new JMenuItem();
        saveAsTxt = new JMenuItem();
        saveImg = new JMenuItem();
        file =  new JMenu();
        editWolrdColors = new JMenuItem();
        editWroldType = new JMenuItem();
        randomness = new JMenuItem();
        editMenu =  new JMenu();
        menuBar = new JMenuBar();
        file.setText("File");
        loadTxt.setText("Load from .txt");
        saveAsTxt.setText("Save as .txt");
        saveImg.setText("Save as PNG");
        file.add(loadTxt);
        file.add(saveAsTxt);
        file.add(saveImg);
        editMenu.setText("Edit");
        editWolrdColors.setText("World color");
        editWroldType.setText("World type");
        randomness.setText("Randomness");
        editMenu.add(editWolrdColors);
        editMenu.add(editWroldType);
        editMenu.add(randomness);
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
        edit.setBounds(BUTTON_LENGTH*2-10, 0, BUTTON_LENGTH,TOOLBAR_SIZE );
        edit.setBorderPainted(false);
        edit.setFocusPainted(false);
        edit.setBackground(Color.white);
        edit.setIcon(new ImageIcon("resources/editYellow.png"));
        edit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        edit.setToolTipText("Edit");
        edit.setEnabled(true);
        //Random button
        random = new JButton();
        random.setBounds(BUTTON_LENGTH*3-10, 0, BUTTON_LENGTH,TOOLBAR_SIZE );
        random.setBorderPainted(false);
        random.setFocusPainted(false);
        random.setBackground(Color.white);
        random.setIcon(new ImageIcon("resources/randomPinkBlue.png"));
        random.setCursor(new Cursor(Cursor.HAND_CURSOR));
        random.setToolTipText("Random");
        random.setEnabled(true);
        //Erase button
        erase = new JButton();
        erase.setBounds(BUTTON_LENGTH*4-10, 0, BUTTON_LENGTH,TOOLBAR_SIZE );
        erase.setBorderPainted(false);
        erase.setFocusPainted(false);
        erase.setBackground(Color.white);
        erase.setIcon(new ImageIcon("resources/eraseAqua.png"));
        erase.setCursor(new Cursor(Cursor.HAND_CURSOR));
        erase.setToolTipText("Erase");
        erase.setEnabled(true);
        //Zoom options
        zoomIcon = new JLabel();
        zoomIcon.setBounds(BUTTON_LENGTH*6-10, 0, BUTTON_LENGTH,TOOLBAR_SIZE );
        zoomIcon.setBackground(Color.white);
        zoomIcon.setIcon(new ImageIcon("resources/zoomBlue.png"));
        zoomIcon.setEnabled(true);
        zoomOptions = new JComboBox<String>();
        zoomOptions.setBounds(BUTTON_LENGTH*7-10, 0, COMBOBOX_LENGTH, TOOLBAR_SIZE);
        zoomOptions.setBackground(Color.white);
        zoomOptions.setCursor(new Cursor(Cursor.HAND_CURSOR));
        zoomOptions.setToolTipText("Zoom");
        zoomOptions.setEnabled(true);
        for(double zoom: zoomValues) zoomOptions.addItem((int)(zoom*100)+"%");
        zoomOptions.setSelectedItem("100%");
        //Speed options
        speedIcon = new JLabel();
        speedIcon.setBounds(BUTTON_LENGTH*7+COMBOBOX_LENGTH-5, 0, BUTTON_LENGTH,TOOLBAR_SIZE );
        speedIcon.setBackground(Color.white);
        speedIcon.setIcon(new ImageIcon("resources/speedColors.png"));
        speedIcon.setEnabled(true);
        speedOptions = new JComboBox<String>();
        speedOptions.setBounds(BUTTON_LENGTH*8+COMBOBOX_LENGTH-10, 0, COMBOBOX_LENGTH, TOOLBAR_SIZE);
        speedOptions.setBackground(Color.white);
        speedOptions.setCursor(new Cursor(Cursor.HAND_CURSOR));
        speedOptions.setToolTipText("Speed");
        speedOptions.setEnabled(true);
        for(double speed: speedValues) if(speed!=0) speedOptions.addItem((double)speed/1000+"s"); else speedOptions.addItem("MAX");
        speedOptions.setSelectedItem("0.1s");
        //Rule label
        ruleButton = new JButton(worldPanel.getWorldPanelRule());
        ruleButton.setBounds(BUTTON_LENGTH*8+COMBOBOX_LENGTH*2, 0, BUTTON_LENGTH*6,TOOLBAR_SIZE );
        ruleButton.setBackground(Color.white);
        ruleButton.setBorderPainted(false);
        ruleButton.setFocusPainted(false);
        ruleButton.setIcon(new ImageIcon("resources/ruleColors.png"));
        ruleButton.setHorizontalAlignment(SwingConstants.LEFT);
        ruleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ruleButton.setToolTipText("Applied rule");
        ruleButton.setEnabled(true);
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
        toolbar.setBounds(0, 0, WINDOW_WORLD_LENGTH,TOOLBAR_SIZE);
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
        toolbar.add(ruleButton);
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
        worldPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e){
                if(editFlag){
                    int cellPosX = (int)(e.getX()/(CELLS_PIXELS*zoomValues[zoomOptions.getSelectedIndex()]));
                    int cellPosY = (int)(e.getY()/(CELLS_PIXELS*zoomValues[zoomOptions.getSelectedIndex()]));
                    if((e.getModifiersEx()&MOUSE_BUTTON_DOWN)==RIGHT_BUTTON_DOWN) worldPanel.setWorldPanelCellAsAlive(cellPosX,cellPosY);
                    else if((e.getModifiersEx()&MOUSE_BUTTON_DOWN)==LEFT_BUTTON_DOWN) worldPanel.setWorldPanelCellAsDeath(cellPosX,cellPosY);
                    setInfoInLabels();
                }
            }
        });
        worldPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                if(editFlag){
                    int cellPosX = (int)(e.getX()/(CELLS_PIXELS*zoomValues[zoomOptions.getSelectedIndex()]));
                    int cellPosY = (int)(e.getY()/(CELLS_PIXELS*zoomValues[zoomOptions.getSelectedIndex()]));
                    if(e.getButton() == RIGHT_CLICK) worldPanel.setWorldPanelCellAsAlive(cellPosX,cellPosY);
                    else if(e.getButton() == LEFT_CLICK)  worldPanel.setWorldPanelCellAsDeath(cellPosX,cellPosY);
                    setInfoInLabels();
                }
            }
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
        zoomOptions.addActionListener((ActionEvent e) -> {
            worldPanel.setWorldPanelSize((int)(CELLS_PIXELS*CELLS_X*zoomValues[zoomOptions.getSelectedIndex()]),(int)(CELLS_PIXELS*CELLS_Y*zoomValues[zoomOptions.getSelectedIndex()]));
            worldScroller.setViewportView(worldPanel);
        });
        speedOptions.addActionListener((ActionEvent e) -> {
            nextStateSpeed = speedValues[speedOptions.getSelectedIndex()];
        });
        ruleButton.addActionListener((ActionEvent e) -> {
            runGOL = false;
            pauseAndPlay.setIcon(new ImageIcon("resources/playGreen.png"));
            pauseAndPlay.setToolTipText("Play");
            JTextField bornRule, surviveRule;
            bornRule = new JTextField(worldPanel.getWorldPanelBornRule());
            surviveRule = new JTextField(worldPanel.getWorldPanelSurviveRule());
            Object[] ruleItems= new Object[5];
            ruleItems[0] = "Set born and survive rules";
            ruleItems[1] = "Born";
            ruleItems[2] = bornRule;
            ruleItems[3] = "Survive";
            ruleItems[4] = surviveRule;
            int okOption = JOptionPane.showOptionDialog(this, ruleItems, "Rules", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon("resources/ruleRed.png"), null, null);
            if(okOption == JOptionPane.OK_OPTION){
                worldPanel.setWorldPanelRule(bornRule.getText(), surviveRule.getText());
                ruleButton.setText(worldPanel.getWorldPanelRule());
            }
        });
        editWolrdColors.addActionListener((ActionEvent e) -> {
            JColorChooser aliveColorChooser = new JColorChooser(Color.WHITE);//Black by default
            int aliveColorOption = JOptionPane.showOptionDialog(this, aliveColorChooser, "Choose alive cell color", JOptionPane.CLOSED_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (aliveColorOption == JOptionPane.CLOSED_OPTION){
                return;
            }
            worldPanel.setAliveColor(aliveColorChooser.getColor());
            JColorChooser deathColorChooser = new JColorChooser(Color.WHITE);//White by default
            int deathColorOption = JOptionPane.showOptionDialog(null, deathColorChooser, "Choose death cell color", JOptionPane.CLOSED_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (deathColorOption == JOptionPane.CLOSED_OPTION){
                return;
            }
            worldPanel.setDeathColor(deathColorChooser.getColor());
        });
        randomness.addActionListener((ActionEvent e) -> {
            SpinnerNumberModel spModel = new SpinnerNumberModel(10, 0, 100, 1);
            JSpinner randomSpinner = new JSpinner(spModel);
            Object[] ob= new Object[5];
            ob[0] = "Alive cells probability (%)";
            ob[1] = randomSpinner;
            int randomnessOption = JOptionPane.showOptionDialog(null, ob, "Randomness in world", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (randomnessOption == JOptionPane.OK_OPTION){
                this.randomAliveCellProbability = (int)randomSpinner.getValue();
            }
        });
        saveAsTxt.addActionListener((ActionEvent e) -> {
            runGOL = false;
            pauseAndPlay.setIcon(new ImageIcon("resources/playGreen.png"));
            pauseAndPlay.setToolTipText("Play");
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Configuration as .txt","txt");
            fileChooser.setFileFilter(filter);
            int aproveOption = fileChooser.showSaveDialog(this);
            if(aproveOption == JFileChooser.APPROVE_OPTION){
                try {
                    File fileConfiguration = fileChooser.getSelectedFile();
                    if(!fileConfiguration.getCanonicalPath().endsWith(".txt"))
                        fileConfiguration = new File(fileChooser.getSelectedFile().getCanonicalPath()+".txt");
                    BufferedWriter fileConfigurationWriter = new BufferedWriter(new FileWriter(fileConfiguration));
                    fileConfigurationWriter.write("Rule: "+worldPanel.getWorldPanelRule()+"\n");
                    fileConfigurationWriter.write("World length: "+worldPanel.getCellsX()+"\n");
                    fileConfigurationWriter.write("World heigth: "+worldPanel.getCellsY()+"\n");
                    fileConfigurationWriter.write("World type: "+(worldPanel.isWorldPanelToroidal()?"Toroidal":"Finite")+"\n");
                    fileConfigurationWriter.write("Generation: "+worldPanel.getWorldPanelGeneration()+"\n");
                    fileConfigurationWriter.write("Alive cells: "+worldPanel.getWorldPanelAliveCells()+"\n");
                    fileConfigurationWriter.write("Death cells: "+worldPanel.getWorldPanelDeathCells()+"\n");
                    fileConfigurationWriter.write("Cell pixels: "+worldPanel.getCellPixels()+"\n");
                    fileConfigurationWriter.write("Alive cells color: "+worldPanel.getAliveCellsColor().getRed()+","+worldPanel.getAliveCellsColor().getGreen()+","+worldPanel.getAliveCellsColor().getBlue()+"\n");
                    fileConfigurationWriter.write("Death cells color: "+worldPanel.getDeathCellsColor().getRed()+","+worldPanel.getDeathCellsColor().getGreen()+","+worldPanel.getDeathCellsColor().getBlue()+"\n");
                    fileConfigurationWriter.write("World:\n");
                    for(int y=0;y<worldPanel.getCellsY();y++){
                        for(int x=0;x<worldPanel.getCellsX();x++){
                            fileConfigurationWriter.write('0'+worldPanel.getWorldPanelCellState(x, y));
                        }
                        fileConfigurationWriter.write("\n");
                    }
                    fileConfigurationWriter.close();
                    JOptionPane.showMessageDialog(this,
                        "Configuration saved succesfully as\n"+fileConfiguration.getCanonicalPath(),
                        "Configuration saved",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                    "Error when creating configuration file\nError: "+ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        saveImg.addActionListener((ActionEvent e) -> {
            runGOL = false;
            pauseAndPlay.setIcon(new ImageIcon("resources/playGreen.png"));
            pauseAndPlay.setToolTipText("Play");
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG image","png");
            fileChooser.setFileFilter(filter);
            int aproveOption = fileChooser.showSaveDialog(this);
            if(aproveOption == JFileChooser.APPROVE_OPTION){
                try {
                    File fileImage = fileChooser.getSelectedFile();
                    if(!fileImage.getCanonicalPath().endsWith(".png"))
                        fileImage = new File(fileChooser.getSelectedFile().getCanonicalPath()+".png");
                    ImageIO.write(worldPanel.getWorldImage(), "png", fileImage);
                    JOptionPane.showMessageDialog(this,
                        "Image saved succesfully as\n"+fileImage.getCanonicalPath(),
                        "Image saved",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                    "Error when creating image\nError: "+ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
/**
 * TODO: Change cell pixels
 * TODO: Change cells number
 * TODO: Change world type
 * TODO: Save as txt file
 * TODO: Load from txt file
 * TODO: Graphics (Save as CSV files)
 */