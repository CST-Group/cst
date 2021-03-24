package br.unicamp.cst.util;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;

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
         m.insertCodelet(c);
         Codelet c2 = new TestCodelet("Codelet 2");
         c2.addInput(m4);
         c2.addInput(m5);
         c2.addOutput(m6);
         c2.addOutput(m3);
         c2.addBroadcast(m5);
         m.insertCodelet(c2);
         CodeletsProfiler codeletProfiler = new CodeletsProfiler(m, "D:\\Projeto Ericsson-Unicamp\\codelet_profiler_test\\", "codeletProfilerTest.txt","Mind 1", 100);
         codeletProfiler.start();
         Thread.sleep(1000);
         System.out.println("******** isRunning() "+ codeletProfiler.isRunning());
    	
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
         m.insertCodelet(c);
         Codelet c2 = new TestCodelet("Codelet 2");
         c2.addInput(m4);
         c2.addInput(m5);
         c2.addOutput(m6);
         c2.addOutput(m3);
         c2.addBroadcast(m5);
         m.insertCodelet(c2);
         CodeletsProfiler codeletProfiler = new CodeletsProfiler(m, "D:\\Projeto Ericsson-Unicamp\\codelet_profiler_test\\", "codeletProfilerTest.txt","Mind 1", (long) 1000);
         codeletProfiler.start();
         Thread.sleep(5000);
         System.out.println("******** isRunning() "+ codeletProfiler.isRunning());
    	
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
         m.insertCodelet(c2);
         CodeletsProfiler codeletProfiler = new CodeletsProfiler(m, "D:\\Projeto Ericsson-Unicamp\\codelet_profiler_test\\", "codeletProfilerTest.txt","Mind 1",500, (long) 500);
         codeletProfiler.start();
         Thread.sleep(1000);
         System.out.println("******** isRunning() "+ codeletProfiler.isRunning());
    	
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
         m.insertCodelet(c);
         Codelet c2 = new TestCodelet("Codelet 2");
         c2.addInput(m4);
         c2.addInput(m5);
         c2.addOutput(m6);
         c2.addOutput(m3);
         c2.addBroadcast(m5);
         m.insertCodelet(c2);
         CodeletsProfiler codeletProfiler = new CodeletsProfiler(m, "D:\\Projeto Ericsson-Unicamp\\codelet_profiler_test\\", "codeletProfilerTest.txt","Mind 1",50000, (long) 500);
         codeletProfiler.start();
         Thread.sleep(1000);
         System.out.println("******** isRunning() "+ codeletProfiler.isRunning());
    	
    }
    
    @Test
    public void testCodeletsProfilerWithInterrupt() throws InterruptedException {
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
         m.insertCodelet(c2);
         CodeletsProfiler codeletProfiler = new CodeletsProfiler(m, "D:\\Projeto Ericsson-Unicamp\\codelet_profiler_test\\", "codeletProfilerTestWithInterrupt.txt","Mind 1", 100);
         CodeletsProfiler codeletProfiler2 = new CodeletsProfiler(m, "D:\\Projeto Ericsson-Unicamp\\codelet_profiler_test\\", "codeletProfilerTestWithInterrupt.txt","Mind 2", 50);
         codeletProfiler.start();
         codeletProfiler2.start();
         Thread.sleep(10);
         System.out.println("******** codeletProfiler isRunning() "+ codeletProfiler.isRunning());
         System.out.println("******** codeletProfiler2 isRunning() "+ codeletProfiler2.isRunning());
         codeletProfiler2.interrupt();
         codeletProfiler.interrupt();
    	
    }

}
