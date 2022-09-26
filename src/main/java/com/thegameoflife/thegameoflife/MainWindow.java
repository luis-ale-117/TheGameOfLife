/**
 * TheGameOfLife: Java GUI to run game of life like cellular automatas
 * @author Luis Alejandro Mendoza Franco
 * Github: luis-ale-117
 */
package com.thegameoflife.thegameoflife;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
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
 * JFrame where all Game of life like cellular automatas are shown
 * Controls and options are in this window
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
    //Components
    private JScrollPane worldScroller;
    private WorldPanel worldPanel;
    private JMenuBar menuBar;
    private JMenu file,editMenu;
    private JMenuItem loadTxt,saveAsTxt,saveImg;
    private JMenuItem editWorldColors,editWorldType,editWorldDimensions,randomness;
    private JToolBar toolbar;
    private JButton pauseAndPlay,edit,random,erase,ruleButton;
    private JComboBox zoomOptions, speedOptions;
    private JLabel zoomIcon,speedIcon,generationLabel,aliveLabel,deathLabel;
    //Others
    private boolean runGOL, generateRandomFlag, eraseFlag, editFlag, stopRefreshFlag;
    private double randomAliveCellProbability;
    private int nextStateSpeed, cellsPixels, cellsX,cellsY;
    private final double[] zoomValues = {0.25,0.5,1,1.5,2};
    private final int[] speedValues = {2000,1000,500,100,50,10,0};//Miliseconds
    
    
    public MainWindow(){
        super();
        setWindowSettings();
        randomAliveCellProbability = 10; //10% of alive cells at the beginning
        nextStateSpeed = 100;
        cellsPixels = CELLS_PIXELS;//Default values of the world
        cellsX = CELLS_X;
        cellsY = CELLS_Y;
        //Create a random world at the beginning
        worldPanel.generateRandomWorldPanel(10);//10% of alive cells
        setInfoInLabels();
    }
    private void setWindowSettings(){
        setSize(WINDOW_WORLD_LENGTH,WINDOW_WORLD_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Conway's game of life");
        setResizable(false);
        setLayout(null);
        setIconImage(new ImageIcon(getImage("images/abstractLogo.png")).getImage());
        //Configure all components of the main window for the simulation
        initComponents();
        setActions();
        setVisible(true);
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
            }else if(stopRefreshFlag){
                try {
                    Thread.sleep(REFRESH_TIME);
                } catch (InterruptedException ex) {
                    JOptionPane.showMessageDialog(this,
                    "Error when sleeping after refreshing world\nError: "+ex.getMessage(),
                    "Refresh error",
                    JOptionPane.ERROR_MESSAGE);
                }
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
        editWorldColors = new JMenuItem();
        editWorldType = new JMenuItem();
        randomness = new JMenuItem();
        editWorldDimensions = new JMenuItem();
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
        editWorldColors.setText("World color");
        editWorldType.setText("World type");
        editWorldDimensions.setText("World dimensions");
        randomness.setText("Randomness");
        editMenu.add(editWorldColors);
        editMenu.add(editWorldType);
        editMenu.add(editWorldDimensions);
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
        pauseAndPlay.setIcon(new ImageIcon(getImage("images/playGreen.png")));
        pauseAndPlay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pauseAndPlay.setToolTipText("Play");
        pauseAndPlay.setEnabled(true);
        //Edit button
        edit = new JButton();
        edit.setBounds(BUTTON_LENGTH*2-10, 0, BUTTON_LENGTH,TOOLBAR_SIZE );
        edit.setBorderPainted(false);
        edit.setFocusPainted(false);
        edit.setBackground(Color.white);
        edit.setIcon(new ImageIcon(getImage("images/editYellow.png")));
        edit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        edit.setToolTipText("Edit");
        edit.setEnabled(true);
        //Random button
        random = new JButton();
        random.setBounds(BUTTON_LENGTH*3-10, 0, BUTTON_LENGTH,TOOLBAR_SIZE );
        random.setBorderPainted(false);
        random.setFocusPainted(false);
        random.setBackground(Color.white);
        random.setIcon(new ImageIcon(getImage("images/randomPinkBlue.png")));
        random.setCursor(new Cursor(Cursor.HAND_CURSOR));
        random.setToolTipText("Random");
        random.setEnabled(true);
        //Erase button
        erase = new JButton();
        erase.setBounds(BUTTON_LENGTH*4-10, 0, BUTTON_LENGTH,TOOLBAR_SIZE );
        erase.setBorderPainted(false);
        erase.setFocusPainted(false);
        erase.setBackground(Color.white);
        erase.setIcon(new ImageIcon(getImage("images/eraseAqua.png")));
        erase.setCursor(new Cursor(Cursor.HAND_CURSOR));
        erase.setToolTipText("Erase");
        erase.setEnabled(true);
        //Zoom options
        zoomIcon = new JLabel();
        zoomIcon.setBounds(BUTTON_LENGTH*6-10, 0, BUTTON_LENGTH,TOOLBAR_SIZE );
        zoomIcon.setBackground(Color.white);
        zoomIcon.setIcon(new ImageIcon(getImage("images/zoomBlue.png")));
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
        speedIcon.setIcon(new ImageIcon(getImage("images/speedColors.png")));
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
        ruleButton.setIcon(new ImageIcon(getImage("images/ruleColors.png")));
        ruleButton.setHorizontalAlignment(SwingConstants.LEFT);
        ruleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ruleButton.setToolTipText("Applied rule");
        ruleButton.setEnabled(true);
        //Generation label
        generationLabel = new JLabel("Gen: 0");
        generationLabel.setBounds(WINDOW_WORLD_LENGTH-LABEL_LENGTH*3-15, 0, LABEL_LENGTH,TOOLBAR_SIZE );
        generationLabel.setIcon(new ImageIcon(getImage("images/timePurplePink.png")));
        generationLabel.setToolTipText("Generation");
        generationLabel.setEnabled(true);
        //Alive cells label
        aliveLabel = new JLabel("Alive: ");
        aliveLabel.setBounds(WINDOW_WORLD_LENGTH-LABEL_LENGTH*2-15, 0, LABEL_LENGTH,TOOLBAR_SIZE );
        aliveLabel.setIcon(new ImageIcon(getImage("images/aliveRed.png")));
        aliveLabel.setToolTipText("Alive cells");
        aliveLabel.setEnabled(true);
        //Death cells label
        deathLabel = new JLabel("Death: ");
        deathLabel.setBounds(WINDOW_WORLD_LENGTH-LABEL_LENGTH-15, 0, LABEL_LENGTH,TOOLBAR_SIZE );
        deathLabel.setIcon(new ImageIcon(getImage("images/deathGreen.png")));
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
            pauseAndPlay.setIcon(runGOL ? new ImageIcon(getImage("images/pauseRed.png")) : new ImageIcon(getImage("images/playGreen.png")));
            pauseAndPlay.setToolTipText(runGOL ? "Pause" : "Play");
        });
        edit.addActionListener((ActionEvent e) -> {
            runGOL = false;
            pauseAndPlay.setIcon(new ImageIcon(getImage("images/playGreen.png")));
            pauseAndPlay.setToolTipText("Play");
            if(pauseAndPlay.isEnabled()){
                pauseAndPlay.setEnabled(false);
                random.setEnabled(false);
                erase.setEnabled(false);
                edit.setIcon(new ImageIcon(getImage("images/uneditYellow.png")));
                edit.setToolTipText("Stop edition");
            }else{
                pauseAndPlay.setEnabled(true);
                random.setEnabled(true);
                erase.setEnabled(true);
                edit.setIcon(new ImageIcon(getImage("images/editYellow.png")));
                edit.setToolTipText("Edit");
            }
            editFlag = !editFlag;
        });
        worldPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e){
                if(editFlag){
                    int cellPosX = (int)(e.getX()/(cellsPixels*zoomValues[zoomOptions.getSelectedIndex()]));
                    int cellPosY = (int)(e.getY()/(cellsPixels*zoomValues[zoomOptions.getSelectedIndex()]));
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
                    int cellPosX = (int)(e.getX()/(cellsPixels*zoomValues[zoomOptions.getSelectedIndex()]));
                    int cellPosY = (int)(e.getY()/(cellsPixels*zoomValues[zoomOptions.getSelectedIndex()]));
                    if(e.getButton() == RIGHT_CLICK) worldPanel.setWorldPanelCellAsAlive(cellPosX,cellPosY);
                    else if(e.getButton() == LEFT_CLICK)  worldPanel.setWorldPanelCellAsDeath(cellPosX,cellPosY);
                    setInfoInLabels();
                }
            }
        });
        random.addActionListener((ActionEvent e) -> {
            runGOL = false;
            pauseAndPlay.setIcon(new ImageIcon(getImage("images/playGreen.png")));
            pauseAndPlay.setToolTipText("Play");
            generateRandomFlag = true;
        });
        erase.addActionListener((ActionEvent e) -> {
            runGOL = false;
            pauseAndPlay.setIcon(new ImageIcon(getImage("images/playGreen.png")));
            pauseAndPlay.setToolTipText("Play");
            eraseFlag = true;
        });
        zoomOptions.addActionListener((ActionEvent e) -> {
            worldPanel.setWorldPanelZoomPixels((int)(cellsPixels*cellsX*zoomValues[zoomOptions.getSelectedIndex()]),(int)(cellsPixels*cellsY*zoomValues[zoomOptions.getSelectedIndex()]));
            worldScroller.setViewportView(worldPanel);
        });
        speedOptions.addActionListener((ActionEvent e) -> {
            nextStateSpeed = speedValues[speedOptions.getSelectedIndex()];
        });
        ruleButton.addActionListener((ActionEvent e) -> {
            runGOL = false;
            pauseAndPlay.setIcon(new ImageIcon(getImage("images/playGreen.png")));
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
            int okOption = JOptionPane.showOptionDialog(this, ruleItems, "Rules", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(getImage("images/ruleRed.png")), null, null);
            if(okOption == JOptionPane.OK_OPTION){
                worldPanel.setWorldPanelRule(bornRule.getText(), surviveRule.getText());
                ruleButton.setText(worldPanel.getWorldPanelRule());
            }
        });
        editWorldColors.addActionListener((ActionEvent e) -> {
            runGOL = false;
            stopRefreshFlag = true;
            pauseAndPlay.setIcon(new ImageIcon(getImage("images/playGreen.png")));
            pauseAndPlay.setToolTipText("Play");
            JColorChooser aliveColorChooser = new JColorChooser(Color.WHITE);//Black by default
            int aliveColorOption = JOptionPane.showOptionDialog(this, aliveColorChooser, "Choose alive cell color", JOptionPane.CLOSED_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (aliveColorOption == JOptionPane.CLOSED_OPTION){
                return;
            }
            worldPanel.setWorldPanelAliveColor(aliveColorChooser.getColor());
            JColorChooser deathColorChooser = new JColorChooser(Color.WHITE);//White by default
            int deathColorOption = JOptionPane.showOptionDialog(null, deathColorChooser, "Choose death cell color", JOptionPane.CLOSED_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (deathColorOption == JOptionPane.CLOSED_OPTION){
                return;
            }
            worldPanel.setWorldPanelDeathColor(deathColorChooser.getColor());
            worldPanel.paintAllWorldPanel();
            stopRefreshFlag = false;
        });
        editWorldDimensions.addActionListener((ActionEvent e) -> {
            runGOL = false;
            stopRefreshFlag = true;
            pauseAndPlay.setIcon(new ImageIcon(getImage("images/playGreen.png")));
            pauseAndPlay.setToolTipText("Play");
            SpinnerNumberModel spModelX = new SpinnerNumberModel(cellsX, 3, 10000, 1);
            SpinnerNumberModel spModelY = new SpinnerNumberModel(cellsY, 3, 10000, 1);
            SpinnerNumberModel spModelPixels = new SpinnerNumberModel(cellsPixels, 1, 50, 1);
            JSpinner cellsXSpinner = new JSpinner(spModelX);
            JSpinner cellsYSpinner = new JSpinner(spModelY);
            JSpinner cellPixelsSpinner = new JSpinner(spModelPixels);
            
            Object[] dimensionItems= new Object[8];
            dimensionItems[0] = "Set world dimensions";
            dimensionItems[1] = "World length (cells)";
            dimensionItems[2] = cellsXSpinner;
            dimensionItems[3] = "World height (cells)";
            dimensionItems[4] = cellsYSpinner;
            dimensionItems[5] = "Pixels per cell";
            dimensionItems[6] = cellPixelsSpinner;
            dimensionItems[7] = "WARNING: Changing length or height\nwill create a new blank world with\nthe specified dimensions";
            int okOption = JOptionPane.showOptionDialog(this, dimensionItems, "World dimensions", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(getImage("images/settings.png")), null, null);
            if(okOption == JOptionPane.OK_OPTION){
                //If user changed any dimension value
                if(cellsPixels!=(int)cellPixelsSpinner.getValue() || cellsX!=(int)cellsXSpinner.getValue() || cellsY!=(int)cellsYSpinner.getValue()){
                    //Just update pixels size
                    if(cellsX==(int)cellsXSpinner.getValue() && cellsY==(int)cellsYSpinner.getValue()){
                        cellsPixels = (int)cellPixelsSpinner.getValue();
                        worldPanel.setWorldPanelCellPixels(cellsPixels);
                    }else{
                        cellsPixels = (int)cellPixelsSpinner.getValue();
                        cellsX = (int)cellsXSpinner.getValue();
                        cellsY = (int)cellsYSpinner.getValue();
                        worldPanel.setWorldPanelSettings(cellsPixels, cellsX, cellsY);
                        setInfoInLabels();
                    }
                    zoomOptions.setSelectedItem("100%");
                    worldScroller.setViewportView(worldPanel);
                    worldPanel.paintAllWorldPanel();
                }
            }
            stopRefreshFlag = false;
        });
        editWorldType.addActionListener((ActionEvent e) -> {
            runGOL = false;
            stopRefreshFlag = true;
            pauseAndPlay.setIcon(new ImageIcon(getImage("images/playGreen.png")));
            pauseAndPlay.setToolTipText("Play");
            JComboBox typeComboBox = new JComboBox<String>();
            typeComboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
            typeComboBox.addItem("Toroidal");
            typeComboBox.addItem("Finite");
            typeComboBox.setSelectedItem(worldPanel.isWorldPanelToroidal()?"Toroidal":"Finite");
            Object[] typeItems= new Object[2];
            typeItems[0] = "Set world type";
            typeItems[1] = typeComboBox;
            int okOption = JOptionPane.showOptionDialog(this, typeItems, "World type", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if(okOption == JOptionPane.OK_OPTION){
                if(typeComboBox.getSelectedItem().equals("Toroidal")){
                    worldPanel.setWorldPanelToroidal(true);
                }else if(typeComboBox.getSelectedItem().equals("Finite")){
                    JComboBox borderStateComboBox = new JComboBox<String>();
                    borderStateComboBox.addItem("Alive");
                    borderStateComboBox.addItem("Death");
                    borderStateComboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    borderStateComboBox.setSelectedItem(worldPanel.isWorldPanelNonToroidalStateAlive()?"Alive":"Death");
                    Object[] borderStateItems= new Object[2];
                    borderStateItems[0] = "Set state of cells out of the border";
                    borderStateItems[1] = borderStateComboBox;
                    okOption = JOptionPane.showOptionDialog(this, borderStateItems, "Border cells state", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                    if(okOption == JOptionPane.OK_OPTION){
                        worldPanel.setWorldPanelToroidal(false);
                        if(borderStateComboBox.getSelectedItem().equals("Alive")){
                            worldPanel.setWorldPanelNonToroidalStateAsAlive();
                        }else if(borderStateComboBox.getSelectedItem().equals("Death")){
                            worldPanel.setWorldPanelNonToroidalStateAsDeath();
                        }
                    }
                }
            }
            stopRefreshFlag = false;
        });
        randomness.addActionListener((ActionEvent e) -> {
            SpinnerNumberModel spModel = new SpinnerNumberModel(10, 0, 100, 1);
            JSpinner randomSpinner = new JSpinner(spModel);
            Object[] ob= new Object[2];
            ob[0] = "Alive cells probability (%)";
            ob[1] = randomSpinner;
            int randomnessOption = JOptionPane.showOptionDialog(null, ob, "Randomness in world", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (randomnessOption == JOptionPane.OK_OPTION){
                this.randomAliveCellProbability = (int)randomSpinner.getValue();
            }
        });
        saveAsTxt.addActionListener((ActionEvent e) -> {
            runGOL = false;
            pauseAndPlay.setIcon(new ImageIcon(getImage("images/playGreen.png")));
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
                    fileConfigurationWriter.write("World height: "+worldPanel.getCellsY()+"\n");
                    fileConfigurationWriter.write("World type: "+(worldPanel.isWorldPanelToroidal()?"Toroidal":worldPanel.isWorldPanelNonToroidalStateAlive()?"Finite_alive":"Finite_death")+"\n");
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
        loadTxt.addActionListener((ActionEvent e) -> {
            runGOL = false;
            stopRefreshFlag = true;
            pauseAndPlay.setIcon(new ImageIcon(getImage("images/playGreen.png")));
            pauseAndPlay.setToolTipText("Play");
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Configuration as .txt","txt");
            fileChooser.setFileFilter(filter);
            int aproveOption = fileChooser.showOpenDialog(this);
            if(aproveOption == JFileChooser.APPROVE_OPTION){
                try {
                    File fileConfiguration = fileChooser.getSelectedFile();
                    if(!fileConfiguration.getCanonicalPath().endsWith(".txt"))  return;
                    String line;
                    String[] lineSections;
                    BufferedReader fileConfigurationReader = new BufferedReader(new FileReader(fileConfiguration));
                    //Get world rule
                    String bornRule,surviveRule;
                    line = fileConfigurationReader.readLine();
                    lineSections = line.split(":");
                    if(lineSections.length==2 && lineSections[0].equals("Rule")){
                        lineSections = lineSections[1].split("/");
                        if(lineSections.length != 2) throw new IOException("Error in Rule format");
                        bornRule = lineSections[0].strip().substring(1);
                        surviveRule = lineSections[1].strip().substring(1);
                    }else throw new IOException("Error in Rule format");
                    //Get world length and height
                    int worldLength, worldHeight;
                    line = fileConfigurationReader.readLine();
                    lineSections = line.split(":");
                    if(lineSections.length==2 && lineSections[0].equals("World length")){
                        worldLength = Integer.parseInt(lineSections[1].strip());
                    }else throw new IOException("Error in World length format");
                    line = fileConfigurationReader.readLine();
                    lineSections = line.split(":");
                    if(lineSections.length==2 && lineSections[0].equals("World height")){
                        worldHeight = Integer.parseInt(lineSections[1].strip());
                    }else throw new IOException("Error in World height format");
                    //Get world type
                    String worldType;
                    line = fileConfigurationReader.readLine();
                    lineSections = line.split(":");
                    if(lineSections.length==2 && lineSections[0].equals("World type")){
                        worldType = lineSections[1].strip();
                    }else throw new IOException("Error in World type format");
                    //Get generation
                    int generation;
                    line = fileConfigurationReader.readLine();
                    lineSections = line.split(":");
                    if(lineSections.length==2 && lineSections[0].equals("Generation")){
                        generation = Integer.parseInt(lineSections[1].strip());
                    }else throw new IOException("Error in Generation format");
                    //Get alive cells
                    int aliveCells;
                    line = fileConfigurationReader.readLine();
                    lineSections = line.split(":");
                    if(lineSections.length==2 && lineSections[0].equals("Alive cells")){
                        aliveCells = Integer.parseInt(lineSections[1].strip());
                    }else throw new IOException("Error in Alive cells format");
                    //Get death cells
                    int deathCells;
                    line = fileConfigurationReader.readLine();
                    lineSections = line.split(":");
                    if(lineSections.length==2 && lineSections[0].equals("Death cells")){
                        deathCells = Integer.parseInt(lineSections[1].strip());
                    }else throw new IOException("Error in Death cells format");
                    //Get cell pixels
                    int cellPixels;
                    line = fileConfigurationReader.readLine();
                    lineSections = line.split(":");
                    if(lineSections.length==2 && lineSections[0].equals("Cell pixels")){
                        cellPixels = Integer.parseInt(lineSections[1].strip());
                    }else throw new IOException("Error in Cell pixels format");
                    //Get alive cells color
                    int aliveColorRed,aliveColorGreen,aliveColorBlue;
                    line = fileConfigurationReader.readLine();
                    lineSections = line.split(":");
                    if(lineSections.length==2 && lineSections[0].equals("Alive cells color")){
                        lineSections = lineSections[1].strip().split(",");
                        if(lineSections.length != 3) throw new IOException("Error in configuration format");
                        aliveColorRed = Integer.parseInt(lineSections[0].strip());
                        aliveColorGreen = Integer.parseInt(lineSections[1].strip());
                        aliveColorBlue = Integer.parseInt(lineSections[2].strip());
                    }else throw new IOException("Error in Alive cells color format");
                    //Get death cells color
                    int deathColorRed,deathColorGreen,deathColorBlue;
                    line = fileConfigurationReader.readLine();
                    lineSections = line.split(":");
                    if(lineSections.length==2 && lineSections[0].equals("Death cells color")){
                        lineSections = lineSections[1].strip().split(",");
                        if(lineSections.length != 3) throw new IOException("Error in Death cells color format");
                        deathColorRed = Integer.parseInt(lineSections[0].strip());
                        deathColorGreen = Integer.parseInt(lineSections[1].strip());
                        deathColorBlue = Integer.parseInt(lineSections[2].strip());
                    }else throw new IOException("Error in Death cells color format");
                    //Set world settings
                    worldPanel.setWorldPanelSettings(cellPixels, worldLength, worldHeight);
                    if(worldType.equals("Toroidal")) worldPanel.setWorldPanelToroidal(true);
                    else if(worldType.equals("Finite_alive")){
                        worldPanel.setWorldPanelToroidal(false);
                        worldPanel.setWorldPanelNonToroidalStateAsAlive();
                    }
                    else if(worldType.equals("Finite_death")){
                        worldPanel.setWorldPanelToroidal(false);
                        worldPanel.setWorldPanelNonToroidalStateAsDeath();
                    }
                    else throw new IOException("Error in World type format");
                    worldPanel.setWorldPanelRule(bornRule, surviveRule);
                    worldPanel.setWorldPanelGeneration(generation);
                    worldPanel.setWorldPanelAliveColor(new Color(aliveColorRed,aliveColorGreen,aliveColorBlue));
                    worldPanel.setWorldPanelDeathColor(new Color(deathColorRed,deathColorGreen,deathColorBlue));
                    //Get world cells
                    line = fileConfigurationReader.readLine();
                    if(!line.equals("World:")) throw new IOException("Error in World format");
                    char[] cellsRow;
                    for(int y=0;y<worldHeight;y++){
                        line = fileConfigurationReader.readLine();
                        if(line.length()!=worldLength) throw new IOException("Error in World format");
                        cellsRow = line.toCharArray();
                        for(int x=0;x<worldLength;x++){
                            if(cellsRow[x] == '1') worldPanel.setWorldPanelCellAsAliveNoPaint(x, y);
                            else worldPanel.setWorldPanelCellAsDeathNoPaint(x, y);
                        }
                    }
                    fileConfigurationReader.close();
                    if(worldPanel.getWorldPanelAliveCells()!=aliveCells || worldPanel.getWorldPanelDeathCells()!=deathCells)
                        throw new IOException("Error. Incompatibility between alive cells,\ndeath cells and world in file");
                    setInfoInLabels();
                    zoomOptions.setSelectedItem("100%");
                    worldScroller.setViewportView(worldPanel);
                    worldPanel.paintAllWorldPanel();
                    ruleButton.setText(worldPanel.getWorldPanelRule());
                    JOptionPane.showMessageDialog(this,
                        "Configuration file loaded succesfully\nFile: "+fileConfiguration.getName(),
                        "Configuration loaded",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                    "Error when creating configuration file\nError: "+ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                }
            }
            stopRefreshFlag = false;
        });
        saveImg.addActionListener((ActionEvent e) -> {
            runGOL = false;
            pauseAndPlay.setIcon(new ImageIcon(getImage("images/playGreen.png")));
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
    private static URL getImage(final String pathAndFileName) {
        return Thread.currentThread().getContextClassLoader().getResource(pathAndFileName);
    }
}