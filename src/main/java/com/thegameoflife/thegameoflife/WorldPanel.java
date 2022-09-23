/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thegameoflife.thegameoflife;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;

/**
 *
 * @author lalex
 */
public class WorldPanel extends JLabel{
    private static final int REFRESH_TIME = 200;
    private int pixelsX,pixelsY,zoomPixelsX,zoomPixelsY;
    private int cellPixels,cellsX,cellsY;
    private BufferedImage worldImage;
    private Graphics worldDraw;
    private GOLWorld world;
    
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
    }
    public void initBlankWorld(){
        worldDraw.setColor(Color.BLACK);
        worldDraw.fillRect(0, 0, pixelsX-1, pixelsY-1);
    }
    public void setWorldPanelSize(int x, int y){
        zoomPixelsX = x;
        zoomPixelsY = y;
        this.setPreferredSize(new Dimension(zoomPixelsX,zoomPixelsY));
    }
    public void eraseWorldPanel(){
        worldDraw.setColor(Color.BLACK);
        worldDraw.fillRect(0, 0, pixelsX-1, pixelsY-1);
        world.eraseWorld();
    }
    private void paintWorldPanel(){
        for(int y=0;y<cellsY;y++){
            for(int x=0;x<cellsX;x++){
                if(world.cellChangedState(x, y)){
                    if(world.isCellAlive(x, y)){
                        worldDraw.setColor(Color.WHITE);
                    }else{
                        worldDraw.setColor(Color.BLACK);
                    }
                    worldDraw.fillRect(x*cellPixels,y*cellPixels, cellPixels,cellPixels);
                }
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
            worldDraw.setColor(Color.WHITE);
            worldDraw.fillRect(x*cellPixels,y*cellPixels, cellPixels,cellPixels);
        }
        repaint();
    }
    public void setWorldPanelCellAsDeath(int x, int y){
        if(world.isCellAlive(x, y)){
            world.setCellAsDeath(x, y);
            worldDraw.setColor(Color.BLACK);
            worldDraw.fillRect(x*cellPixels,y*cellPixels, cellPixels,cellPixels);
        }
        repaint();
    }
    public void setWorldPanelAsToroidal(){
        world.setToroidal();
    }
    public boolean isWorldPanelToroidal(){
        return world.isToroidal();
    }
    public void setWorldPanelRule(String bornRule, String surviveRule){
        world.setRule(bornRule, surviveRule);
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
    @Override
    public void paintComponent(Graphics g){
        g.drawImage(worldImage, 0, 0,zoomPixelsX,zoomPixelsY,this);
        g.dispose();
    }
}
