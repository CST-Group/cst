/***********************************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * K. Raizer, A. L. O. Paraense, E. M. Froes, R. R. Gudwin - initial API and implementation
 * **********************************************************************************************/
package br.unicamp.cst.util;

import java.util.Date;

/**
 *
 * @author rgudwin
 */
public class TestComplexMemoryObjectInfo {
    public long testlong;
    public int testint;
    public float testfloat;
    public double testdouble;
    public byte testbyte;
    public String testString="Test";
    public Date testdate=new Date();
    public int[] testintarray = new int[10];
    public long[] testlongarray = new long[10];
    public float[] testfloatarray = new float[10];
    public double[] testdoublearray = new double[10];
    public TestComplexMemoryObjectInfo complextest;
    public TestComplexMemoryObjectInfo[] complextestarray = new TestComplexMemoryObjectInfo[3];
    
    public String toString() {
        return(testString);
    }
}
