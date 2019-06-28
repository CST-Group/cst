/*******************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     R. R. Gudwin 
 ******************************************************************************/

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
    
    public void printDifference(String pre) {
        System.out.println(pre+" Time elapsed: "+(after-before)+" ms");
    }
    
}
