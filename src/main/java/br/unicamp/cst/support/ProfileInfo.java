/***********************************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * K. Raizer, A. L. O. Paraense, E. M. Froes, R. R. Gudwin - initial API and implementation
 ***********************************************************************************************/
package br.unicamp.cst.support;

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
