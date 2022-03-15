package br.unicamp.cst.core.entities;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;


public class CodeletContainerTest {
	class CodeletToTestOne extends Codelet {

		private int counter = 0;

		public int getCounter() {
			return counter;
		}

		public CodeletToTestOne(String name) {
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
			if (this.outputs != null && !this.outputs.isEmpty()) {
				this.outputs.get(0).setI("CODELET 1 OUTPUT");
			}
		}

	}
	
	class CodeletToTestTwo extends Codelet {

		private int counter = 0;

		public int getCounter() {
			return counter;
		}

		public CodeletToTestTwo(String name) {
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
			counter = counter + 2;
		}

	}
	
	class CodeletToTestThree extends Codelet {

		private int counter = 0;

		public int getCounter() {
			return counter;
		}

		public CodeletToTestThree(String name) {
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
			counter = counter + 3;
			if (this.outputs != null && !this.outputs.isEmpty()) {
				this.outputs.get(0).setI("CODELET 3 OUTPUT");
			}
		}

	}
	
	
	@Test
	public void noMemoryChangeTest() throws InterruptedException {
		// no codelet run
		Codelet codeletOne = new CodeletToTestOne("Codelet 1");
		Codelet codeletTwo = new CodeletToTestTwo("Codelet 2");
		Codelet codeletThree = new CodeletToTestThree("Codelet 3");
		
		Mind mind = new Mind();
		MemoryObject memory1 = mind.createMemoryObject("MEMORY1", 0.12);
		MemoryObject memory2 = mind.createMemoryObject("MEMORY2", 0.32);
		MemoryObject memory3 = mind.createMemoryObject("MEMORY3", 0.32);
		MemoryObject memory4 = mind.createMemoryObject("MEMORY4", 0.32);
		
		codeletOne.addInput(memory1);
		codeletOne.addBroadcast(memory2);
		
		codeletTwo.addBroadcast(memory3);
		
		codeletThree.addInput(memory4);
		
		ArrayList<Codelet> codeletContainerArray = new ArrayList<Codelet>();
		codeletContainerArray.add(codeletOne);
		codeletContainerArray.add(codeletTwo);
		codeletContainerArray.add(codeletThree);
		
		CodeletContainer codeletContainer = new CodeletContainer(codeletContainerArray);
		
		mind.insertCodelet(codeletOne);
		mind.insertCodelet(codeletTwo);
		mind.insertCodelet(codeletThree);
		mind.start();
		Thread.sleep(2000);
		mind.shutDown();
		
		assertEquals(new ArrayList<Codelet>(), codeletContainer.getOutputs());
		assertEquals(0, codeletContainer.getEvaluation(), 0);
		
	}
	
	@Test
	public void runningCodeletChangingInputTest() throws InterruptedException {
		// changes the codelet container input
		Codelet codeletOne = new CodeletToTestOne("Codelet 1");
		Codelet codeletTwo = new CodeletToTestTwo("Codelet 2");
		Codelet codeletThree = new CodeletToTestThree("Codelet 3");
		
		Mind mind = new Mind();
		MemoryObject memoryInput1 = mind.createMemoryObject("MEMORY_INPUT_1", 0.12);
		MemoryObject memoryInput2 = mind.createMemoryObject("MEMORY_INPUT_2", 0.32);
		MemoryObject memoryInput3 = mind.createMemoryObject("MEMORY_INPUT_3", 0.32);
		MemoryObject memoryInput4 = mind.createMemoryObject("MEMORY_INPUT_4", 0.32);
		MemoryObject memoryOutput1 = mind.createMemoryObject("MEMORY_OUTPUT_1", 0.22);
		MemoryObject memoryOutput2 = mind.createMemoryObject("MEMORY_OUTPUT_2", 0.22);
		MemoryObject memoryOutput3 = mind.createMemoryObject("MEMORY_OUTPUT_3", 0.22);
		
		codeletOne.addInput(memoryInput1);
		codeletOne.addBroadcast(memoryInput2);
		codeletOne.addOutput(memoryOutput1);
		
		codeletTwo.addBroadcast(memoryInput3);
		codeletTwo.addOutput(memoryOutput2);
		
		codeletThree.addInput(memoryInput4);
		codeletThree.addOutput(memoryOutput3);
		
		ArrayList<Codelet> codeletContainerArray = new ArrayList<Codelet>();
		codeletContainerArray.add(codeletOne);
		codeletContainerArray.add(codeletTwo);
		codeletContainerArray.add(codeletThree);
		
		CodeletContainer codeletContainer = new CodeletContainer(codeletContainerArray);
		
		mind.insertCodelet(codeletOne);
		mind.insertCodelet(codeletTwo);
		mind.insertCodelet(codeletThree);
		codeletContainer.setI(10);
		mind.start();
		Thread.sleep(2000);
		mind.shutDown();
		
		for (Codelet codelet : codeletContainer.getAll()) {
			for (Memory mem : codelet.getInputs()) {
				System.out.println("---------------- mem.getI() " + mem.getI());
				assertEquals(10, mem.getI());
			}
		}
		
		for (Codelet codelet : codeletContainer.getAll()) {
			for (Memory mem : codelet.getBroadcast()) {
				System.out.println(">>>>>>>>>>>>>. mem.getI() " + mem.getI());
				assertEquals(10, mem.getI());
			}
		}
		
		assertEquals(3, codeletContainer.getOutputs().size());
		assertEquals("CODELET 1 OUTPUT", codeletContainer.getOutputs().get(0).getI());
		assertEquals(0.22, codeletContainer.getOutputs().get(1).getI());
		assertEquals("CODELET 3 OUTPUT", codeletContainer.getOutputs().get(2).getI());
		assertEquals(0, codeletContainer.getEvaluation(), 0);
		
	}


}
