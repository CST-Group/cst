/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.behavior.asbac;

/**
 *
 * @author rgpolizeli
 */
public class MyLock {
    public Object lock;
    public boolean isLocked;
    
    public MyLock(){
        this.isLocked = Boolean.FALSE;
        lock = new Object();
    }
    
    public void lock(){
        this.isLocked = Boolean.TRUE;
    }
    
    public void unlock(){
        this.isLocked = Boolean.FALSE;
    }
}
