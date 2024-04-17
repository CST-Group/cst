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
package br.unicamp.cst.core.profiler;


import java.util.ArrayList;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

class CodeletToTest extends Codelet {

	private volatile int counter = 0;

	public int getCounter() {
		return counter;
	}

	public CodeletToTest(String name) {
		setName(name);
	}

	@Override
	public void accessMemoryObjects() {

	}

	@Override
	public void calculateActivation() {

	}

	@Override
	public void proc() {
		counter++;
	}

}

public class TestMemoryObserver {

	@Test
	public void noMemoryChangeTest() throws InterruptedException {
		// Codelet runs being a Memory Observer, in this case memory info does not
		// change, so codelet wont run
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
		for (int i = 0; i < 3; i++)
			mComplex.complextestarray[i] = new TestComplexMemoryObjectInfo();
		MemoryObject mo = new MemoryObject();
		mo.setType("TestObject");
		mo.setI(mComplex);
		m7.setI(0.55, 0.23);
		m6.setI(0.33, 0.22);
		m6.setI(0.12, 0.13);
		m6.setI(m7);
		CodeletToTest c = new CodeletToTest("Codelet 1");
		c.setIsMemoryObserver(true);
		c.addInput(m1);
		c.addInput(m2);
		c.addOutput(m3);
		c.addOutput(m4);
		c.addBroadcast(m5);
		c.addBroadcast(mo);
		mo.addMemoryObserver(c);
		m.insertCodelet(c);
		CodeletToTest c2 = new CodeletToTest("Codelet 2");
		c2.setIsMemoryObserver(true);
		c2.addInput(m4);
		c2.addInput(m5);
		c2.addOutput(m6);
		c2.addOutput(m3);
		c2.addBroadcast(m5);
		mo.addMemoryObserver(c);
		m.insertCodelet(c2);
		m.start();
		Thread.sleep(2000);
		m.shutDown();

		assertEquals(0, c.getCounter());
		assertEquals(0, c2.getCounter());
	}

	@Test
	public void usualRunTest() throws InterruptedException {
		// Codelet runs being a Memory Observer, and memories inputs are changed
		Mind m = new Mind();
		MemoryObject m1 = m.createMemoryObject("M1", 0.12);
		MemoryObject m2 = m.createMemoryObject("M2", 0.32);
		MemoryObject m3 = m.createMemoryObject("M3", 0.44);
		MemoryObject m4 = m.createMemoryObject("M4", 0.52);
		MemoryObject m5 = m.createMemoryObject("M5", 0.12);
		MemoryContainer m6 = m.createMemoryContainer("C1");
		MemoryContainer m7 = m.createMemoryContainer("C2");
		MemoryContainer m8 = m.createMemoryContainer("C3");
		m7.add(m4);
		m8.add(m7);
		TestComplexMemoryObjectInfo mComplex = new TestComplexMemoryObjectInfo();
		mComplex.complextest = new TestComplexMemoryObjectInfo();
		for (int i = 0; i < 3; i++)
			mComplex.complextestarray[i] = new TestComplexMemoryObjectInfo();
		MemoryObject mo = new MemoryObject();
		mo.setType("TestObject");
		mo.setI(mComplex);
		m7.setI(0.55, 0.23);
		m6.setI(0.33, 0.22);
		m6.setI(0.12, 0.13);
		m6.setI(m7);
		CodeletToTest c = new CodeletToTest("Codelet 1");
		c.setIsMemoryObserver(true);
		c.addInput(m1);
		c.addInput(m2);
		c.addOutput(m3);
		c.addOutput(m4);
		c.addBroadcast(m5);
		c.addBroadcast(mo);
		mo.addMemoryObserver(c);
		m.insertCodelet(c);
		CodeletToTest c2 = new CodeletToTest("Codelet 2");
		c2.setIsMemoryObserver(true);
		c2.addInput(m4);
		c2.addInput(m7);
		c2.addInput(m5);
		c2.addOutput(m6);
		c2.addOutput(m3);
		c2.addBroadcast(m5);
		mo.addMemoryObserver(c2);
		m8.addMemoryObserver(c2);
		m.insertCodelet(c2);
		m.start();
		mo.setI(10);
		m8.setI(100, 0);
		Thread.sleep(2000);
		m.shutDown();

		assertEquals(1, c.getCounter());
		assertEquals(2, c2.getCounter());
	}
	
	@Test
	public void multipleMemoryChangesTest() throws InterruptedException {
		// Codelet runs being a Memory Observer, and memories inputs are changed
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
		for (int i = 0; i < 3; i++)
			mComplex.complextestarray[i] = new TestComplexMemoryObjectInfo();
		MemoryObject mo = new MemoryObject();
		mo.setType("TestObject");
		mo.setI(mComplex);
		m7.setI(0.55, 0.23);
		m6.setI(0.33, 0.22);
		m6.setI(0.12, 0.13);
		m6.setI(m7);
		CodeletToTest c = new CodeletToTest("Codelet 1");
		c.setIsMemoryObserver(true);
		c.addInput(m1);
		c.addInput(m2);
		c.addOutput(m3);
		c.addOutput(m4);
		c.addBroadcast(m5);
		c.addBroadcast(mo);
		mo.addMemoryObserver(c);
		m2.addMemoryObserver(c);
		m4.addMemoryObserver(c);
		m.insertCodelet(c);
		CodeletToTest c2 = new CodeletToTest("Codelet 2");
		c2.setIsMemoryObserver(true);
		c2.addInput(m4);
		c2.addInput(m5);
		c2.addOutput(m6);
		c2.addOutput(m3);
		c2.addBroadcast(m5);
		m.insertCodelet(c2);
		m.start();
		mo.setI(10);
		mo.setI(4);
		m2.setI(1);
		m4.setI(2);
		Thread.sleep(2000);
		m.shutDown();

		assertEquals(4, c.getCounter());
		assertEquals(1, c2.getCounter());
	}
	
	@Test
	public void addSameMemoryTest() throws InterruptedException {
		// Codelet runs being a Memory Observer, and memories inputs are changed, add same memory more than once
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
		for (int i = 0; i < 3; i++)
			mComplex.complextestarray[i] = new TestComplexMemoryObjectInfo();
		MemoryObject mo = new MemoryObject();
		mo.setType("TestObject");
		mo.setI(mComplex);
		m7.setI(0.55, 0.23);
		m6.setI(0.33, 0.22);
		m6.setI(0.12, 0.13);
		m6.setI(m7);
		CodeletToTest c = new CodeletToTest("Codelet 1");
		c.setIsMemoryObserver(true);
		mo.addMemoryObserver(c);
		mo.addMemoryObserver(c);
		m2.addMemoryObserver(c);
		m2.addMemoryObserver(c);
		m4.addMemoryObserver(c);
		m.insertCodelet(c);
		CodeletToTest c2 = new CodeletToTest("Codelet 2");
		c2.setIsMemoryObserver(true);
		c2.setProfiling(true);
		ArrayList<Memory> memories = new ArrayList<Memory>();
		memories.add(m4);
		memories.add(m4);
		memories.add(m5);
		c2.addInputs(memories);
		c2.addOutput(m6);
		c2.addOutput(m3);
		ArrayList<Memory> broadcasts = new ArrayList<Memory>();
		broadcasts.add(m5);
		broadcasts.add(m5);
		c2.addBroadcasts(broadcasts);
		m.insertCodelet(c2);
		m.start();
		for (int i = 0; i < 60; i++) {
			mo.setI(i);
			mo.setI(i);
			m2.setI(i);
			m4.setI(i);
		}
		Thread.sleep(2000);
		m.shutDown();

		assertEquals(240, c.getCounter());
		assertEquals(60, c2.getCounter());
		assertTrue(c2.isProfiling());
	}
	
	@Test
	public void usualRunWithMemoryContainerTest() throws InterruptedException {
		// Using a Memory Container to use setI and notify codelets
			
		Mind m = new Mind();
		MemoryObject m1 = m.createMemoryObject("M1", 0.12);
		MemoryObject m2 = m.createMemoryObject("M2", 0.32);
		MemoryObject m3 = m.createMemoryObject("M3", 0.44);
		MemoryObject m4 = m.createMemoryObject("M4", 0.52);
		MemoryObject m5 = m.createMemoryObject("M5", 0.12);
		MemoryContainer m7 = m.createMemoryContainer("C2");
		MemoryContainer m8 = m.createMemoryContainer("C3");
		m7.add(m4);
		m8.add(m7);
		TestComplexMemoryObjectInfo mComplex = new TestComplexMemoryObjectInfo();
		mComplex.complextest = new TestComplexMemoryObjectInfo();
		for (int i = 0; i < 3; i++)
			mComplex.complextestarray[i] = new TestComplexMemoryObjectInfo();
		MemoryObject mo = new MemoryObject();
		mo.setType("TestObject");
		mo.setI(mComplex);
		CodeletToTest c = new CodeletToTest("Codelet 1");
		c.setIsMemoryObserver(true);
		c.addInput(m1);
		c.addInput(m2);
		c.addOutput(m3);
		c.addOutput(m4);
		c.addBroadcast(m5);
		c.addBroadcast(mo);
		mo.addMemoryObserver(c);
		m.insertCodelet(c);
		CodeletToTest c2 = new CodeletToTest("Codelet 2");
		c2.setIsMemoryObserver(true);
		c2.addInput(m4);
		c2.addInput(m7);
		c2.addInput(m5);
		c2.addOutput(m3);
		c2.addBroadcast(m5);
		mo.addMemoryObserver(c2);
		m8.addMemoryObserver(c2);
		m.insertCodelet(c2);
		m.start();
		//setI in Memory Container and verify if Codelet was notified
		m8.setI(100, 0);
		m7.setI(0.55, 0.23);
		Thread.sleep(2000);
		m.shutDown();

		assertEquals(0, c.getCounter());
		assertEquals(1, c2.getCounter());
	}
        
        @Test
	public void changeOfRegimeTestMemoryObject() {
                Mind m = new Mind();
		MemoryObject input = m.createMemoryObject("INPUT_NUMBER", 0.12);
		MemoryObject output = m.createMemoryObject("OUTPUT_NUMBER", 0.32);
                Codelet c = new Codelet() {
                    MemoryObject input_number;
                    MemoryObject output_number;
                    public int counter = 0;
                    @Override
                    public void accessMemoryObjects() {
                        input_number = (MemoryObject) this.getInput("INPUT_NUMBER");
                        output_number = (MemoryObject) this.getOutput("OUTPUT_NUMBER");
                    }
                    @Override
                    public void calculateActivation() {
                        try {
                            double a = counter;
                            setActivation(a/100);
                        } catch (CodeletActivationBoundsException ex) {
                            Logger.getLogger(TestMemoryObserver.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    @Override
                    public void proc() {
                        System.out.println("Processing");
                        int n = (int) input_number.getI();
                        output_number.setI(n+1);
                        counter++;
                    }
                };
		c.addInput(input);
		c.addOutput(output);
		m.insertCodelet(c);
		c.setPublishSubscribe(true);
                m.start();
                try { Thread.sleep(500); } catch(InterruptedException e) {}
		//setI in Memory Container and verify if Codelet was notified
                long ts = output.getTimestamp();
                input.setI(0);
                long startwait = System.currentTimeMillis();
                while(ts == output.getTimestamp()) {
                    if (System.currentTimeMillis() - startwait > 2000) {
                        System.out.println("Restarting timer due to inactivity");
                        c.start();
                    }
                    if (System.currentTimeMillis() - startwait > 5000) fail("Some problem have occurred 3 !");
                }
                int nout = (int) output.getI();
		System.out.println("Result: "+output.getI());
                assertEquals(nout,1);
		c.setPublishSubscribe(false);
                ts = output.getTimestamp();
                startwait = System.currentTimeMillis();
                while(ts == output.getTimestamp()) {
                    if (System.currentTimeMillis() - startwait > 2000) {
                        System.out.println("Restarting timer due to inactivity");
                        m.start();
                    }
                    if (System.currentTimeMillis() - startwait > 5000) fail("Some problem have occurred 4 !");
                }
                System.out.println("Result: "+output.getI()+" "+c.getActivation());
                m.shutDown();
		//assertEquals(0, c.getCounter());
        }
        
        @Test
	public void changeOfRegimeTestMemoryContainer() {
                Mind m = new Mind();
		MemoryContainer input = m.createMemoryContainer("INPUT_NUMBER");
		MemoryObject output = m.createMemoryObject("OUTPUT_NUMBER", 0.32);
                Codelet c = new Codelet() {
                    volatile MemoryContainer input_number;
                    volatile MemoryObject output_number;
                    public volatile int counter = 0;
                    @Override
                    public void accessMemoryObjects() {
                        input_number = (MemoryContainer) this.getInput("INPUT_NUMBER");
                        output_number = (MemoryObject) this.getOutput("OUTPUT_NUMBER");
                    }
                    @Override
                    public void calculateActivation() {
                        try {
                            double a = counter;
                            setActivation(a/100);
                        } catch (CodeletActivationBoundsException ex) {
                            Logger.getLogger(TestMemoryObserver.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.out.println("Calculating activation: "+getActivation());
                    }
                    @Override
                    public void proc() {
                        System.out.println("Processing");
                        int n = (int) input_number.getI();
                        output_number.setI(n+1);
                        counter++;
                        try {
                            double a = counter;
                            setActivation(a/100);
                        } catch (CodeletActivationBoundsException ex) {
                            Logger.getLogger(TestMemoryObserver.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                };
		c.addInput(input);
		c.addOutput(output);
		m.insertCodelet(c);
                input.setI(0);
		c.setPublishSubscribe(true);
                m.start();
		//setI in Memory Container and verify if Codelet was notified
                long ts = output.getTimestamp();
                input.setI(0,0);
                //while(ts == output.getTimestamp()) System.out.print(".");
                long startwait = System.currentTimeMillis();
                long amountwait=0;
                while(ts == output.getTimestamp()) {
                    try{Thread.sleep(100);}catch(Exception e){};
                    amountwait = System.currentTimeMillis() - startwait;
                    if (amountwait > 2000) {
                        System.out.println("I am waiting too long ... something wrong happened");
                    }
                    if (amountwait > 5000) fail("Some problem have occurred 1 !");
                }
                System.out.println("The test took "+amountwait+" miliseconds");
                int nout = (int) output.getI();
		System.out.println("Result: "+nout+" "+c.getActivation());
                assertEquals(nout,1);
		c.setPublishSubscribe(false);
                
                ts = output.getTimestamp();
                startwait = System.currentTimeMillis();
                while(ts == output.getTimestamp()) {
                    try{Thread.sleep(400);}catch(Exception e){};
                    if (System.currentTimeMillis() - startwait > 2000) {
                        System.out.println("Restarting timer due to inactivity");
                        m.start();
                    }
                    if (System.currentTimeMillis() - startwait > 10000) fail("Some problem have occurred 2 !");
                }
                System.out.println("Result: "+output.getI()+" "+c.getActivation());
                
                m.shutDown();
		//assertEquals(0, c.getCounter());
        }
}
