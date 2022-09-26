/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thegameoflife.thegameoflife;

/**
 *
 * @author lalex
 */
public class GOLWorld {
    private static final byte DEATH=0;
    private static final byte ALIVE=1;
    private static final byte[] STATES = {DEATH,ALIVE};
    
    private int deathCells, aliveCells, generation;
    private boolean toroidalWorld;
    private final int gridSizeX, gridSizeY;
    private byte[][] currentWorld,previousWorld;
    private String bornRule, surviveRule;
    private int[] bornRuleValues, surviveRuleValues;
    private byte nonToroidalBorderState;
    
    GOLWorld(int x, int y){
        toroidalWorld = true;
        currentWorld = new byte[y][x];//Intialize with death cells
        previousWorld = new byte[y][x];
        gridSizeX = x;
        gridSizeY = y;
        deathCells = x*y;
        aliveCells=0;
        generation = 0;
        //Conway's game of life rules
        setRule("3","23");
        nonToroidalBorderState = DEATH;
    }
    public void setToroidal(boolean toroidal){
        toroidalWorld = toroidal;
    }
    public boolean isToroidal(){
        return toroidalWorld;
    }
    public void setRule(String born, String survive){
        bornRule = born;
        surviveRule = survive;
        bornRuleValues = new int[bornRule.length()];
        surviveRuleValues = new int[surviveRule.length()];
        for(int i=0; i<bornRule.length();i++){
            bornRuleValues[i] = Character.getNumericValue(bornRule.charAt(i));
        }
        for(int i=0; i<surviveRule.length();i++){
            surviveRuleValues[i] = Character.getNumericValue(surviveRule.charAt(i));
        }
    }
    public String getRuleAsString(){
        return "B"+bornRule +" / S"+ surviveRule;
    }
    public String getBornRule() {
        return bornRule;
    }
    public String getSurviveRule() {
        return surviveRule;
    }
    public void setNonToroidalBorderStateAsAlive(){
        nonToroidalBorderState = ALIVE;
    }
    public void setNonToroidalBorderStateAsDeath(){
        nonToroidalBorderState = DEATH;
    }
    public boolean isNonToroidalBorderStateAlive(){
        return nonToroidalBorderState == ALIVE;
    }
    public boolean isNonToroidalBorderStateDeath(){
        return nonToroidalBorderState == DEATH;
    }
    private void nextCellState(int x1, int y1){
        int aliveNeighboors, y0, y2, x0, x2;
        if(toroidalWorld){
            y0 = y1-1<0?gridSizeY-1 : y1-1;
            y2 = y1+1>=gridSizeY? 0 : y1+1;
            x0 = x1-1<0?gridSizeX-1 : x1-1;
            x2 = x1+1>=gridSizeX? 0 : x1+1;
            aliveNeighboors = currentWorld[y0][x0] + currentWorld[y0][x1] + currentWorld[y0][x2] +
                              currentWorld[y1][x0] +                        currentWorld[y1][x2] + 
                              currentWorld[y2][x0] + currentWorld[y2][x1] + currentWorld[y2][x2];
        }else{
            y0 = y1-1;
            y2 = y1+1;
            x0 = x1-1;
            x2 = x1+1;
            aliveNeighboors = (y0<0 || x0<0?                   nonToroidalBorderState : currentWorld[y0][x0]) +
                              (y0<0?                           nonToroidalBorderState : currentWorld[y0][x1]) +
                              (y0<0 || x2>=gridSizeX?          nonToroidalBorderState : currentWorld[y0][x2]) +
                              (x0<0?                           nonToroidalBorderState : currentWorld[y1][x0]) +
                              (x2>=gridSizeX?                  nonToroidalBorderState : currentWorld[y1][x2]) +
                              (y2>=gridSizeY || x0<0?          nonToroidalBorderState : currentWorld[y2][x0]) +
                              (y2>=gridSizeY ?                 nonToroidalBorderState : currentWorld[y2][x1]) +
                              (y2>=gridSizeY || x2>=gridSizeX? nonToroidalBorderState : currentWorld[y2][x2]);
        }
        
        //Apply born rule
        if(currentWorld[y1][x1] == DEATH){
            byte state = DEATH;
            for(int rule: bornRuleValues){
                if(aliveNeighboors == rule) state = ALIVE;
            }
            previousWorld[y1][x1] = state;
        }
        //Apply survive rule
        else {
            byte state = DEATH;
            for(int rule: surviveRuleValues){
                if(aliveNeighboors == rule) state = ALIVE ;
            }
            previousWorld[y1][x1] = state;
        }
    }
    public void nextWorldState(){
        aliveCells = 0;
        deathCells = (gridSizeX)*(gridSizeY);
        for(int y=0;y<gridSizeY;y++){
            for(int x=0;x<gridSizeX;x++){
                nextCellState(x,y);
                aliveCells += previousWorld[y][x];
            }
        }
        deathCells -= aliveCells;
        generation++;
        byte[][] w = currentWorld;
        currentWorld = previousWorld;
        previousWorld = w;
    }
    public boolean isCellAlive(int x, int y){
        return currentWorld[y][x] == ALIVE;
    }
    public boolean isCellDeath(int x, int y){
        return currentWorld[y][x] == DEATH;
    }
    public void setCellAsAlive(int x, int y){
        if(currentWorld[y][x] == DEATH){
            aliveCells++;
            deathCells--;
        }
        currentWorld[y][x] = ALIVE;
    }
    public void setCellAsDeath(int x, int y){
        if(currentWorld[y][x] == ALIVE){
            aliveCells--;
            deathCells++;
        }
        currentWorld[y][x] = DEATH;
    }
    public void setDeathCells(int deathCells) {
        this.deathCells = deathCells;
    }

    public void setAliveCells(int aliveCells) {
        this.aliveCells = aliveCells;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }
    public byte getCellState(int x, int y){
        return currentWorld[y][x];
    }
    public int getAliveCells(){
        return aliveCells;
    }
    public int getDeathCells(){
        return deathCells;
    }
    public int getGeneration(){
        return generation;
    }
    public boolean cellChangedState(int x,int y){
        return currentWorld[y][x] != previousWorld[y][x];
    }
    public void eraseWorld(){
        generation = 0;
        aliveCells = 0;
        deathCells = (gridSizeX)*(gridSizeY);
        for(int y=0;y<gridSizeY;y++){
            for(int x=0;x<gridSizeX;x++){
                currentWorld[y][x] = DEATH;
            }
        }
    }
    public void generateRandomWorld(double aliveProbability){
        double percentage = aliveProbability*0.01;
        generation = 0;
        aliveCells = 0;
        deathCells = (gridSizeX)*(gridSizeY);
        for(int y=0;y<gridSizeY;y++){
            for(int x=0;x<gridSizeX;x++){
                currentWorld[y][x] = DEATH;
                if(Math.random()<= percentage){
                    currentWorld[y][x]=ALIVE;
                    aliveCells++;
                }else{
                    currentWorld[y][x]=DEATH;
                }
            }
        }
        deathCells -= aliveCells;
    }
    public int getGridSizeX() {
        return gridSizeX;
    }
    public int getGridSizeY() {
        return gridSizeY;
    }
    
}
