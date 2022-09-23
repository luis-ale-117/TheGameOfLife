/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thegameoflife.thegameoflife;

/**
 *
 * @author lalex
 * Lock class is used to share states between objects so they can determine
 * its actions
 */
public class Lock {
    private boolean value;
    Lock(){
        value = false;
    }
    public boolean getValue(){
        return value;
    }
    public void setValue(boolean v){
        value = v;
    }
    public void switchValue(){
        value = !value;
    }
}
