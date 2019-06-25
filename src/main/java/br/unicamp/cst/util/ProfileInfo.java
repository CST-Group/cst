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
public class ProfileInfo {
    long executionTime;
    long callingTime;
    long lastCallingTime;

    public ProfileInfo(long executionTime, long callingTime, long lastCallingTime) {
        this.executionTime = executionTime;
        this.callingTime = callingTime;
        this.lastCallingTime = lastCallingTime;
    }    
}
