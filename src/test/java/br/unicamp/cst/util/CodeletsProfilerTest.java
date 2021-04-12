package br.unicamp.cst.util;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.util.CodeletsProfiler.FileFormat;

public class CodeletsProfilerTest {
	
	@BeforeClass
    public static void beforeAllTestMethods() {
    }

	@AfterClass
    public static void afterAllTestMethods() {
    }
    
    @Test
    public void testCodeletsProfiler() throws InterruptedException {
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
         m7.setI(0.55, 0.23);
         m6.setI(0.33, 0.22);
         m6.setI(0.12, 0.13);
         m6.setI(m7);
         Codelet c = new TestCodelet("Codelet 1");
         c.addInput(m1);
         c.addInput(m2);
         c.addOutput(m3);
         c.addOutput(m4);
         c.addBroadcast(m5);
         c.addBroadcast(mo);
         c.setCodeletProfiler("profile/", "codeletProfilerTestC.txt", "Mind 1", 10, null, FileFormat.CSV);
         m.insertCodelet(c);
         Codelet c2 = new TestCodelet("Codelet 2");
         c2.addInput(m4);
         c2.addInput(m5);
         c2.addOutput(m6);
         c2.addOutput(m3);
         c2.addBroadcast(m5);
         c2.setCodeletProfiler("profile/", "codeletProfilerTestC2.txt", "Mind 1", 10, null, FileFormat.JSON);
         m.insertCodelet(c2);
         m.start();
         Thread.sleep(2000);
         m.shutDown();
    	
    }
    
    @Test
    public void testCodeletsProfiler2() throws InterruptedException {
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
         m7.setI(0.55, 0.23);
         m6.setI(0.33, 0.22);
         m6.setI(0.12, 0.13);
         m6.setI(m7);
         Codelet c = new TestCodelet("Codelet 1");
         c.addInput(m1);
         c.addInput(m2);
         c.addOutput(m3);
         c.addOutput(m4);
         c.addBroadcast(m5);
         c.addBroadcast(mo);
         c.setCodeletProfiler("profile/", "CjSON.txt", "Mind 1", null,  (long) 1000, FileFormat.JSON);
         m.insertCodelet(c);
         Codelet c2 = new TestCodelet("Codelet 2");
         c2.addInput(m4);
         c2.addInput(m5);
         c2.addOutput(m6);
         c2.addOutput(m3);
         c2.addBroadcast(m5);
         c2.setCodeletProfiler("profile/", "C2JSON.txt", "Mind 1", null,  (long) 1000, FileFormat.JSON);
         m.insertCodelet(c2);
         m.start();
         Thread.sleep(2000);
         m.shutDown();
    	
    }
    
    @Test
    public void testCodeletsProfiler3() throws InterruptedException {
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
         m7.setI(0.55, 0.23);
         m6.setI(0.33, 0.22);
         m6.setI(0.12, 0.13);
         m6.setI(m7);
         Codelet c = new TestCodelet("Codelet 1");
         c.addInput(m1);
         c.addInput(m2);
         c.addOutput(m3);
         c.addOutput(m4);
         c.addBroadcast(m5);
         c.addBroadcast(mo);
         m.insertCodelet(c);
         Codelet c2 = new TestCodelet("Codelet 2");
         c2.addInput(m4);
         c2.addInput(m5);
         c2.addOutput(m6);
         c2.addOutput(m3);
         c2.addBroadcast(m5);
         c2.setCodeletProfiler("profile/", "JustC2Logging.txt", "Mind 1", null,  (long) 1000, FileFormat.CSV);
         m.insertCodelet(c2);
         m.start();
         Thread.sleep(2000);
         m.shutDown();
    	
    }
    
    @Test
    public void testCodeletsProfiler4() throws InterruptedException {
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
         m7.setI(0.55, 0.23);
         m6.setI(0.33, 0.22);
         m6.setI(0.12, 0.13);
         m6.setI(m7);
         Codelet c = new TestCodelet("Codelet 1");
         c.addInput(m1);
         c.addInput(m2);
         c.addOutput(m3);
         c.addOutput(m4);
         c.addBroadcast(m5);
         c.addBroadcast(mo);
         c.setCodeletProfiler("profile/", "C.txt", "Mind 1", 5,  null, FileFormat.JSON);
         m.insertCodelet(c);
         Codelet c2 = new TestCodelet("Codelet 2");
         c2.addInput(m4);
         c2.addInput(m5);
         c2.addOutput(m6);
         c2.addOutput(m3);
         c2.addBroadcast(m5);
         c2.setCodeletProfiler( "profile/", "C2.txt", "Mind 1", 5,  (long) 1000, FileFormat.CSV);
         m.insertCodelet(c2);
         m.start();
         Thread.sleep(2000);
         m.shutDown();
    	
    }
    

}
