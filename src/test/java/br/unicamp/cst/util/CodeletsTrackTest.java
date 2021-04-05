/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.util;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author rgudwin
 */
public class CodeletsTrackTest {
	
	@BeforeClass
    public static void beforeAllTestMethods() {
    }

	@AfterClass
    public static void afterAllTestMethods() {
    }
    
    
    @Test
    public void testCodeletTracking() throws InterruptedException {
    	 Mind m = new Mind();
         MemoryObject m1 = m.createMemoryObject("M1", 0.12);
         MemoryObject m2 = m.createMemoryObject("M2", 0.32);
         MemoryObject m3 = m.createMemoryObject("M3", 0.44);
         MemoryObject m4 = m.createMemoryObject("M4", 0.52);
         MemoryObject m5 = m.createMemoryObject("M5", 0.12);
         MemoryContainer m6 = m.createMemoryContainer("C1");
         MemoryContainer m7 = m.createMemoryContainer("C2");
         TestComplexMemoryObjectInfo mComplex = new TestComplexMemoryObjectInfo();
         mComplex.complextest = new TestComplexMemoryObjectInfo();
         for (int i=0;i<3;i++)
        	 mComplex.complextestarray[i] = new TestComplexMemoryObjectInfo();
         MemoryObject mo = new MemoryObject();
         mo.setType("TestObject");
         mo.setI(mComplex);
         m7.setI(0.55, 0.23,"first");
         m6.setI(0.33, 0.22,"first");
         m6.setI(0.12, 0.13,"second");
         m6.setI(m7,0.15,"third");
         Codelet c = new TestCodelet("Codelet1");
         c.addInput(m1);
         c.addInput(m2);
         c.addOutput(m3);
         c.addOutput(m4);
         c.addBroadcast(m5);
         c.addBroadcast(mo);
         m.insertCodelet(c);
         Codelet c2 = new TestCodelet("Codelet2");
         c2.addInput(m4);
         c2.addInput(m5);
         c2.addOutput(m6);
         c2.addOutput(m3);
         c2.addBroadcast(m5);
         m.insertCodelet(c2);
         c.setTimeStep(100);
         c2.setTimeStep(100);
         c.setTracking(true); // This sets codelet c to be tracked 
         c2.setTracking(true);
         c.setProfiling(true); // This sets codelet c to be profiled 
         c2.setProfiling(true);
         m.start();
         System.out.println("******** Starting 10s running ");
         Thread.sleep(10000);
         m.shutDown();
         System.out.println("******** Test finished: check ./tests/ directory for files with the tracking and profiles ");
    	
    }

}

