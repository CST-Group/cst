/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.util;

/**
 *
 * @author rgudwin
 */
public class Profiler {
    long before;
    long after;
    
    public void begin() {
        before = System.currentTimeMillis();
    }
    
    public void end() {
        after = System.currentTimeMillis();
    }
    
    public void printDifference() {
        System.out.println("Time elapsed: "+(after-before)+" ms");
    }
    
}
