/**
 * ****************************************************************************
 * Copyright (c) 2018  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * R. G. Polizeli and R. R. Gudwin
 * ****************************************************************************
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
