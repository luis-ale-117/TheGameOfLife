/**
 * TheGameOfLife: Java GUI to run game of life like cellular automatas
 * @author Luis Alejandro Mendoza Franco
 * Github: luis-ale-117
 */
package com.thegameoflife.thegameoflife;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;

/**
 * JLabel where the Game of life like cellular automatas are displayed
 */
public class WorldPanel extends JLabel{
    private int pixelsX,pixelsY,zoomPixelsX,zoomPixelsY;
    private int cellPixels,cellsX,cellsY;
    private BufferedImage worldImage;
    private Graphics worldDraw;
    private GOLWorld world;
    private Color aliveColor,deathColor;
    
    WorldPanel(int pixels, int cellsInX, int cellsInY){//Puede ser rectangular
        super();
        cellPixels = pixels;
        cellsX = cellsInX;
        cellsY = cellsInY;
        pixelsX = cellPixels * cellsX;
        pixelsY = cellPixels * cellsY;
        zoomPixelsX = pixelsX;
        zoomPixelsY = pixelsY;
        this.setPreferredSize(new Dimension(pixelsX,pixelsY));
        world = new GOLWorld(cellsX,cellsY);
        worldImage = new BufferedImage(pixelsX,pixelsY,BufferedImage.TYPE_INT_RGB);
        worldDraw = worldImage.createGraphics();
        aliveColor = Color.WHITE;
        deathColor = Color.BLACK;
    }
    public void setWorldPanelSettings(int pixels, int cellsInX, int cellsInY){
        cellPixels = pixels;
        cellsX = cellsInX;
        cellsY = cellsInY;
        pixelsX = cellPixels * cellsX;
        pixelsY = cellPixels * cellsY;
        zoomPixelsX = pixelsX;
        zoomPixelsY = pixelsY;
        this.setPreferredSize(new Dimension(pixelsX,pixelsY));
        world = new GOLWorld(cellsX,cellsY);
        worldImage = new BufferedImage(pixelsX,pixelsY,BufferedImage.TYPE_INT_RGB);
        worldDraw = worldImage.createGraphics();
    }
    public void setWorldPanelCellPixels(int pixels){
        cellPixels = pixels;
        pixelsX = cellPixels * cellsX;
        pixelsY = cellPixels * cellsY;
        zoomPixelsX = pixelsX;
        zoomPixelsY = pixelsY;
        this.setPreferredSize(new Dimension(pixelsX,pixelsY));
        worldImage = new BufferedImage(pixelsX,pixelsY,BufferedImage.TYPE_INT_RGB);
        worldDraw = worldImage.createGraphics();
    }
    public void initBlankWorld(){
        worldDraw.setColor(deathColor);
        worldDraw.fillRect(0, 0, pixelsX-1, pixelsY-1);
    }
    public void setWorldPanelZoomPixels(int x, int y){
        zoomPixelsX = x;
        zoomPixelsY = y;
        this.setPreferredSize(new Dimension(zoomPixelsX,zoomPixelsY));
    }
    public void eraseWorldPanel(){
        worldDraw.setColor(deathColor);
        worldDraw.fillRect(0, 0, pixelsX-1, pixelsY-1);
        world.eraseWorld();
    }
    private void paintWorldPanel(){
        for(int y=0;y<cellsY;y++){
            for(int x=0;x<cellsX;x++){
                if(world.cellChangedState(x, y)){
                    if(world.isCellAlive(x, y)){
                        worldDraw.setColor(aliveColor);
                    }else{
                        worldDraw.setColor(deathColor);
                    }
                    worldDraw.fillRect(x*cellPixels,y*cellPixels, cellPixels,cellPixels);
                }
            }
        }
        repaint();
    }
    public void paintAllWorldPanel(){
        for(int y=0;y<cellsY;y++){
            for(int x=0;x<cellsX;x++){
                if(world.isCellAlive(x, y)){
                    worldDraw.setColor(aliveColor);
                }else{
                    worldDraw.setColor(deathColor);
                }
                worldDraw.fillRect(x*cellPixels,y*cellPixels, cellPixels,cellPixels);
            }
        }
        repaint();
    }
    public void nextWorldPanelState(){
        world.nextWorldState();
        paintWorldPanel();
    }
    public void generateRandomWorldPanel(double aliveProbability){
        world.generateRandomWorld(aliveProbability);
        initBlankWorld();
        paintWorldPanel();
    }
    public void setWorldPanelCellAsAlive(int x, int y){
        if(world.isCellDeath(x, y)){
            world.setCellAsAlive(x, y);
            worldDraw.setColor(aliveColor);
            worldDraw.fillRect(x*cellPixels,y*cellPixels, cellPixels,cellPixels);
        }
        repaint();
    }
    public void setWorldPanelCellAsDeath(int x, int y){
        if(world.isCellAlive(x, y)){
            world.setCellAsDeath(x, y);
            worldDraw.setColor(deathColor);
            worldDraw.fillRect(x*cellPixels,y*cellPixels, cellPixels,cellPixels);
        }
        repaint();
    }
    public void setWorldPanelCellAsAliveNoPaint(int x, int y){
            world.setCellAsAlive(x, y);
    }
    public void setWorldPanelCellAsDeathNoPaint(int x, int y){
            world.setCellAsDeath(x, y);
    }
    public void setWorldPanelToroidal(boolean toroidal){
        world.setToroidal(toroidal);
    }
    public void setWorldPanelNonToroidalStateAsAlive(){
        world.setNonToroidalBorderStateAsAlive();
    }
    public void setWorldPanelNonToroidalStateAsDeath(){
        world.setNonToroidalBorderStateAsDeath();
    }
    public boolean isWorldPanelToroidal(){
        return world.isToroidal();
    }
    public boolean isWorldPanelNonToroidalStateAlive(){
        return world.isNonToroidalBorderStateAlive();
    }
    public boolean isWorldPanelNonToroidalStateDeath(){
        return world.isNonToroidalBorderStateDeath();
    }
    public void setWorldPanelRule(String bornRule, String surviveRule){
        world.setRule(bornRule, surviveRule);
    }
    public void setWorldPanelGeneration(int generation){
        world.setGeneration(generation);
    }
    public void setWorldPanelAliveCells(int aliveCells){
        world.setAliveCells(aliveCells);
    }
    public void setWorldPanelDeathCells(int deathCells){
        world.setAliveCells(deathCells);
    }
    public byte getWorldPanelCellState(int x, int y){
        return world.getCellState(x, y);
    }
    public String getWorldPanelRule(){
        return world.getRuleAsString();
    }
    public int getWorldPanelGeneration(){
        return world.getGeneration();
    }
    public int getWorldPanelAliveCells(){
        return world.getAliveCells();
    }
    public int getWorldPanelDeathCells(){
        return world.getDeathCells();
    }
    public int getPixelsX() {
        return pixelsX;
    }
    public int getPixelsY() {
        return pixelsY;
    }
    public String getWorldPanelBornRule(){
        return world.getBornRule();
    }
    public String getWorldPanelSurviveRule(){
        return world.getSurviveRule();
    }
    public void setWorldPanelAliveColor(Color aliveColor) {
        this.aliveColor = aliveColor;
    }
    public void setWorldPanelDeathColor(Color deathColor) {
        this.deathColor = deathColor;
    }
    public BufferedImage getWorldImage() {
        return worldImage;
    }
    public int getCellPixels() {
        return cellPixels;
    }
    public int getCellsX() {
        return cellsX;
    }
    public int getCellsY() {
        return cellsY;
    }
    public Color getAliveCellsColor() {
        return aliveColor;
    }
    public Color getDeathCellsColor() {
        return deathColor;
    }
    @Override
    public void paintComponent(Graphics g){
        g.drawImage(worldImage, 0, 0,zoomPixelsX,zoomPixelsY,this);
        g.dispose();
    }
}
