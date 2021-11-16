package br.unicamp.cst.util;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;

class CodeletToTest extends Codelet {

	private int counter = 0;

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
		mo.addMemoryObservers(c);
		m.insertCodelet(c);
		CodeletToTest c2 = new CodeletToTest("Codelet 2");
		c2.setIsMemoryObserver(true);
		c2.addInput(m4);
		c2.addInput(m5);
		c2.addOutput(m6);
		c2.addOutput(m3);
		c2.addBroadcast(m5);
		mo.addMemoryObservers(c);
		m.insertCodelet(c2);
		m.start();
		Thread.sleep(2000);
		m.shutDown();

		assertEquals(1, c.getCounter());
		assertEquals(1, c2.getCounter());
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
		mo.addMemoryObservers(c);
		m.insertCodelet(c);
		CodeletToTest c2 = new CodeletToTest("Codelet 2");
		c2.setIsMemoryObserver(true);
		c2.addInput(m4);
		c2.addInput(m7);
		c2.addInput(m5);
		c2.addOutput(m6);
		c2.addOutput(m3);
		c2.addBroadcast(m5);
		mo.addMemoryObservers(c2);
		m8.addMemoryObservers(c2);
		m.insertCodelet(c2);
		m.start();
		mo.setI(10);
		m8.setI(100, 0);
		Thread.sleep(2000);
		m.shutDown();

		assertEquals(2, c.getCounter());
		assertEquals(3, c2.getCounter());
	}
	
	@Test
	public void test3() throws InterruptedException {
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
		mo.addMemoryObservers(c);
		m2.addMemoryObservers(c);
		m4.addMemoryObservers(c);
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

		assertEquals(5, c.getCounter());
		assertEquals(2, c2.getCounter());
	}
	
	@Test
	public void test4() throws InterruptedException {
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
		mo.addMemoryObservers(c);
		mo.addMemoryObservers(c);
		m2.addMemoryObservers(c);
		m2.addMemoryObservers(c);
		m4.addMemoryObservers(c);
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

		assertEquals(241, c.getCounter());
		assertEquals(61, c2.getCounter());
		assertTrue(c2.isProfiling());
	}
}
