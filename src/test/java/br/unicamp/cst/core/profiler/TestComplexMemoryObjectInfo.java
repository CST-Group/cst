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
package br.unicamp.cst.core.profiler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public short testshort;
    public boolean testboolean;
    public String testString="Test";
    public Date testdate=new Date();
    public int[] testintarray = new int[10];
    public long[] testlongarray = new long[10];
    public float[] testfloatarray = new float[10];
    public double[] testdoublearray = new double[10];
    public short[] testshortarray = new short[10];
    public byte[] testbytearray = new byte[10];
    public boolean[] testbooleanarray = new boolean[10];
    public TestComplexMemoryObjectInfo complextest;
    public TestComplexMemoryObjectInfo[] complextestarray = new TestComplexMemoryObjectInfo[3];
    public List<TestComplexMemoryObjectInfo> complextestlist = new ArrayList<>();
    public List<Double> complextestlist2 = new ArrayList<>();
    
    public TestComplexMemoryObjectInfo() {
        complextestlist2.add(3.14);
        complextestlist2.add(0.12);
    } 
    
    
    public String toString() {
        return(testString);
    }
    
    public int equals(TestComplexMemoryObjectInfo other) {
        int ret = 0;
        if (testlong != other.testlong ||
            testint != other.testint ||
            testfloat != other.testfloat ||
            testdouble != other.testdouble ||
            testbyte != other.testbyte ||
            testshort != other.testshort ||
            testboolean != other.testboolean)  ret = 1;
        if (!testString.equals(other.testString)) ret = 2;
        if (!testdate.equals(other.testdate)) ret = 3;
        if (testintarray.length != other.testintarray.length) 
            ret = 4;
        else {            
            for (int i=0;i<testintarray.length;i++) 
                if (testintarray[i] != other.testintarray[i]) ret = 5;
        }
        if (testlongarray.length != other.testlongarray.length) 
            ret = 6;
        else {            
            for (int i=0;i<testlongarray.length;i++) 
                if (testlongarray[i] != other.testlongarray[i]) ret = 7;
        }
        if (testfloatarray.length != other.testfloatarray.length) 
            ret = 8;
        else {            
            for (int i=0;i<testfloatarray.length;i++) 
                if (testfloatarray[i] != other.testfloatarray[i]) ret = 9;
        }
        if (testdoublearray.length != other.testdoublearray.length) 
            ret = 10;
        else {            
            for (int i=0;i<testdoublearray.length;i++) 
                if (testdoublearray[i] != other.testdoublearray[i]) ret = 11;
        }
        if (complextest == null && other.complextest != null) ret = 12;
        if (complextest != null && other.complextest == null) ret = 13;
        if (complextest != null && other.complextest != null)
            if (complextest.equals(other.complextest) != 0) ret = 14;
        if (complextestarray.length != other.complextestarray.length) ret = 15;
        else {            
            for (int i=0;i<complextestarray.length;i++) {
                if (complextestarray[i] == null && other.complextestarray[i] != null) ret = 16;
                if (complextestarray[i] != null && other.complextestarray[i] == null) ret = 17;
                if (complextestarray[i] != null && other.complextestarray[i] != null)
                    if (complextestarray[i].equals(other.complextestarray[i]) != 0) ret = 18;
            }    
        }
        if (complextestlist == null && other.complextestlist != null) ret = 19;
        if (complextestlist != null && other.complextestlist == null) ret = 20;
        if (complextestlist != null && complextestlist != null) {
            if (complextestlist.size() != other.complextestlist.size()) ret = 21;
            for (int i=0;i<complextestlist.size();i++) {
                if (complextestlist.get(i).equals(other.complextestlist.get(i)) != 0) ret = 22;
            }
        }
        if (complextestlist2 == null && other.complextestlist2 != null) ret = 19;
        if (complextestlist2 != null && other.complextestlist2 == null) ret = 20;
        if (complextestlist2 != null && complextestlist2 != null) {
            if (complextestlist2.size() != other.complextestlist2.size()) ret = 21;
            for (int i=0;i<complextestlist2.size();i++) {
                if ((double)complextestlist2.get(i) != (double)other.complextestlist2.get(i)) ret = 22;
            }
        }
        return(ret);
    }
}
