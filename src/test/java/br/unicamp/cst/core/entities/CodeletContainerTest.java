package br.unicamp.cst.core.entities;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.plaf.synth.SynthOptionPaneUI;

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
			activation = counter;
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
		// no codelet runs
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
		
		CodeletContainer codeletContainer = new CodeletContainer(codeletContainerArray, false);
		
		mind.insertCodelet(codeletOne);
		mind.insertCodelet(codeletTwo);
		mind.insertCodelet(codeletThree);
		mind.start();
		Thread.sleep(2000);
		mind.shutDown();
		
		assertEquals(0, codeletContainer.getOutputs().size());
		assertEquals(new ArrayList<Memory>(), codeletContainer.getOutputs());
		assertEquals(new ArrayList<Memory>(), codeletContainer.getOutputs());
		assertEquals(new ArrayList<Memory>(), codeletContainer.getOutputs());
		assertEquals(0, codeletContainer.getEvaluation(), 0);
		
	}
	
	@Test
	public void noMemoryChangeButCodeletAddedIsStartedTest() throws InterruptedException {
		// no codelet runs
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
		
		CodeletContainer codeletContainer = new CodeletContainer(codeletContainerArray, true);
		
		mind.insertCodelet(codeletOne);
		mind.insertCodelet(codeletTwo);
		mind.insertCodelet(codeletThree);
		mind.start();
		Thread.sleep(2000);
		mind.shutDown();
		
		assertEquals(0, codeletContainer.getOutputs().size());
		assertEquals(new ArrayList<Memory>(), codeletContainer.getOutputs());
		assertEquals(new ArrayList<Memory>(), codeletContainer.getOutputs());
		assertEquals(new ArrayList<Memory>(), codeletContainer.getOutputs());
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
		
		CodeletContainer codeletContainer = new CodeletContainer(codeletContainerArray, false);
		
		mind.insertCodelet(codeletOne);
		mind.insertCodelet(codeletTwo);
		mind.insertCodelet(codeletThree);
		codeletContainer.setI(10);
		mind.start();
		Thread.sleep(2000);
		mind.shutDown();
		
		for (Codelet codelet : codeletContainer.getAll()) {
			for (Memory mem : codelet.getInputs()) {
				assertEquals(10, mem.getI());
			}
		}
		
		for (Codelet codelet : codeletContainer.getAll()) {
			for (Memory mem : codelet.getBroadcast()) {
				assertEquals(0.32, mem.getI());
			}
		}
		
		assertEquals(3, codeletContainer.getOutputs().size());
		List<Memory> expectedOutputs = new ArrayList<Memory>();
		expectedOutputs.add(memoryOutput1);
		expectedOutputs.add(memoryOutput2);
		expectedOutputs.add(memoryOutput3);
		assertArrayEquals(expectedOutputs.toArray(), codeletContainer.getOutputs().toArray());
		assertEquals(0.22, codeletContainer.getOutputs().get(1).getI());
		assertEquals("MEMORY_OUTPUT_3", codeletContainer.getOutputs().get(2).getName());
		assertEquals(0, codeletContainer.getEvaluation(), 0);
		
	}
	
	@Test
	public void runningCodeletChangingInputCodeletStartedWhenAddedTest() throws InterruptedException {
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
		
		CodeletContainer codeletContainer = new CodeletContainer(codeletContainerArray, true);
		
		codeletContainer.setI(10);
		Thread.sleep(2000);
		
		for (Codelet codelet : codeletContainer.getAll()) {
			for (Memory mem : codelet.getInputs()) {
				assertEquals(10, mem.getI());
			}
		}
		
		for (Codelet codelet : codeletContainer.getAll()) {
			for (Memory mem : codelet.getBroadcast()) {
				assertEquals(0.32, mem.getI());
			}
		}
		
		CodeletToTestOne codeletToTestOne = (CodeletToTestOne) codeletContainer.getCodelet("Codelet 1");
		assertEquals(7, codeletToTestOne.getCounter());
		assertEquals(3, codeletContainer.getOutputs().size());
		List<Memory> expectedOutputs = new ArrayList<Memory>();
		expectedOutputs.add(memoryOutput1);
		expectedOutputs.add(memoryOutput2);
		expectedOutputs.add(memoryOutput3);
		assertArrayEquals(expectedOutputs.toArray(), codeletContainer.getOutputs().toArray());
		assertEquals(0.22, codeletContainer.getOutputs().get(1).getI());
		assertEquals("MEMORY_OUTPUT_3", codeletContainer.getOutputs().get(2).getName());
		assertEquals(0, codeletContainer.getEvaluation(), 0);
		
	}
	
	@Test
	public void addCodeletsToCodeletContainerTest() throws InterruptedException {
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
		
		CodeletContainer codeletContainer = new CodeletContainer();
		codeletContainer.addCodelet(codeletOne, false);
		codeletContainer.addCodelet(codeletTwo, false);
		codeletContainer.addCodelet(codeletThree, false);
		
		
		assertEquals(3, codeletContainer.getOutputs().size());
		List<Memory> expectedOutputs = new ArrayList<Memory>();
		expectedOutputs.add(memoryOutput1);
		expectedOutputs.add(memoryOutput2);
		expectedOutputs.add(memoryOutput3);
		assertArrayEquals(expectedOutputs.toArray(), codeletContainer.getOutputs().toArray());
		assertEquals("MEMORY_OUTPUT_1", codeletContainer.getOutputs().get(0).getName());
		assertEquals("MEMORY_OUTPUT_2", codeletContainer.getOutputs().get(1).getName());
		assertEquals("MEMORY_OUTPUT_3", codeletContainer.getOutputs().get(2).getName());
		assertEquals(3, codeletContainer.getCodelet(codeletOne.getName()).outputs.size());
		assertEquals(3, codeletContainer.getCodelet(codeletTwo.getName()).outputs.size());
		assertEquals(3, codeletContainer.getCodelet(codeletThree.getName()).outputs.size());
		
		assertEquals(2, codeletContainer.getInputs().size());
		List<Memory> expectedInputs = new ArrayList<Memory>();
		expectedInputs.add(memoryInput1);
		expectedInputs.add(memoryInput4);
		assertArrayEquals(expectedInputs.toArray(), codeletContainer.getInputs().toArray());
		assertEquals("MEMORY_INPUT_1", codeletContainer.getInputs().get(0).getName());
		assertEquals("MEMORY_INPUT_4", codeletContainer.getInputs().get(1).getName());
		assertEquals(2, codeletContainer.getCodelet(codeletOne.getName()).inputs.size());
		assertEquals(2, codeletContainer.getCodelet(codeletThree.getName()).inputs.size());
		
		assertEquals(2, codeletContainer.getBroadcast().size());
		List<Memory> expectedBroadcast = new ArrayList<Memory>();
		expectedBroadcast.add(memoryInput2);
		expectedBroadcast.add(memoryInput3);
		assertArrayEquals(expectedBroadcast.toArray(), codeletContainer.getBroadcast().toArray());
		assertEquals("MEMORY_INPUT_2", codeletContainer.getBroadcast().get(0).getName());
		assertEquals("MEMORY_INPUT_3", codeletContainer.getBroadcast().get(1).getName());
		assertEquals(2, codeletContainer.getCodelet(codeletOne.getName()).broadcast.size());
		assertEquals(2, codeletContainer.getCodelet(codeletTwo.getName()).broadcast.size());
	}
	
	@Test
	public void addCodeletsToCodeletContainerWhichHasInputsAndOuputsTest() throws InterruptedException {
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
		
		CodeletContainer codeletContainer = new CodeletContainer();
		
		ArrayList<Memory> newInputs = new ArrayList<Memory>();
		newInputs.add(memoryInput1);
		newInputs.add(memoryInput4);
		codeletContainer.setInputs(newInputs);
		
		ArrayList<Memory> newOutputs = new ArrayList<Memory>();
		newOutputs.add(memoryOutput1);
		newOutputs.add(memoryOutput2);
		newOutputs.add(memoryOutput3);
		codeletContainer.setOutputs(newOutputs);	
		
		codeletContainer.addCodelet(codeletOne, false);
		codeletContainer.addCodelet(codeletTwo, false);
		codeletContainer.addCodelet(codeletThree, false);
		
		
		assertEquals(3, codeletContainer.getOutputs().size());
		List<Memory> expectedOutputs = new ArrayList<Memory>();
		expectedOutputs.add(memoryOutput1);
		expectedOutputs.add(memoryOutput2);
		expectedOutputs.add(memoryOutput3);
		assertArrayEquals(expectedOutputs.toArray(), codeletContainer.getOutputs().toArray());
		assertEquals("MEMORY_OUTPUT_1", codeletContainer.getOutputs().get(0).getName());
		assertEquals("MEMORY_OUTPUT_2", codeletContainer.getOutputs().get(1).getName());
		assertEquals("MEMORY_OUTPUT_3", codeletContainer.getOutputs().get(2).getName());
		assertEquals(3, codeletContainer.getCodelet(codeletOne.getName()).outputs.size());
		assertEquals(3, codeletContainer.getCodelet(codeletTwo.getName()).outputs.size());
		assertEquals(3, codeletContainer.getCodelet(codeletThree.getName()).outputs.size());
		
		assertEquals(2, codeletContainer.getInputs().size());
		List<Memory> expectedInputs = new ArrayList<Memory>();
		expectedInputs.add(memoryInput1);
		expectedInputs.add(memoryInput4);
		assertArrayEquals(expectedInputs.toArray(), codeletContainer.getInputs().toArray());
		assertEquals("MEMORY_INPUT_1", codeletContainer.getInputs().get(0).getName());
		assertEquals("MEMORY_INPUT_4", codeletContainer.getInputs().get(1).getName());
		assertEquals(2, codeletContainer.getCodelet(codeletOne.getName()).inputs.size());
		assertEquals(2, codeletContainer.getCodelet(codeletThree.getName()).inputs.size());
		
	}
	
	@Test
	public void removeCodeletsFromCodeletContainerTest() throws InterruptedException {
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
		
		CodeletContainer codeletContainer = new CodeletContainer(codeletContainerArray, false);
		
		
		assertEquals(3, codeletContainer.getOutputs().size());
		List<Memory> expectedOutputs = new ArrayList<Memory>();
		expectedOutputs.add(memoryOutput1);
		expectedOutputs.add(memoryOutput2);
		expectedOutputs.add(memoryOutput3);
		assertArrayEquals(expectedOutputs.toArray(), codeletContainer.getOutputs().toArray());
		assertEquals("MEMORY_OUTPUT_1", codeletContainer.getOutputs().get(0).getName());
		assertEquals("MEMORY_OUTPUT_2", codeletContainer.getOutputs().get(1).getName());
		assertEquals("MEMORY_OUTPUT_3", codeletContainer.getOutputs().get(2).getName());
		assertEquals(3, codeletContainer.getCodelet(codeletOne.getName()).outputs.size());
		assertEquals(3, codeletContainer.getCodelet(codeletTwo.getName()).outputs.size());
		assertEquals(3, codeletContainer.getCodelet(codeletThree.getName()).outputs.size());
		
		assertEquals(2, codeletContainer.getInputs().size());
		List<Memory> expectedInputs = new ArrayList<Memory>();
		expectedInputs.add(memoryInput1);
		expectedInputs.add(memoryInput4);
		assertArrayEquals(expectedInputs.toArray(), codeletContainer.getInputs().toArray());
		assertEquals("MEMORY_INPUT_1", codeletContainer.getInputs().get(0).getName());
		assertEquals("MEMORY_INPUT_4", codeletContainer.getInputs().get(1).getName());
		assertEquals(2, codeletContainer.getCodelet(codeletOne.getName()).inputs.size());
		assertEquals(2, codeletContainer.getCodelet(codeletThree.getName()).inputs.size());
		
		assertEquals(2, codeletContainer.getBroadcast().size());
		List<Memory> expectedBroadcast = new ArrayList<Memory>();
		expectedBroadcast.add(memoryInput2);
		expectedBroadcast.add(memoryInput3);
		assertArrayEquals(expectedBroadcast.toArray(), codeletContainer.getBroadcast().toArray());
		assertEquals("MEMORY_INPUT_2", codeletContainer.getBroadcast().get(0).getName());
		assertEquals("MEMORY_INPUT_3", codeletContainer.getBroadcast().get(1).getName());
		assertEquals(2, codeletContainer.getCodelet(codeletOne.getName()).broadcast.size());
		assertEquals(2, codeletContainer.getCodelet(codeletTwo.getName()).broadcast.size());
		
		codeletContainer.removeCodelet(codeletOne);
		
		assertEquals(2, codeletContainer.getOutputs().size());
		expectedOutputs = new ArrayList<Memory>();
		expectedOutputs.add(memoryOutput2);
		expectedOutputs.add(memoryOutput3);
		assertArrayEquals(expectedOutputs.toArray(), codeletContainer.getOutputs().toArray());
		assertEquals(2, codeletContainer.getCodelet(codeletTwo.getName()).outputs.size());
		assertEquals(2, codeletContainer.getCodelet(codeletThree.getName()).outputs.size());
		
		assertEquals(1, codeletContainer.getInputs().size());
		expectedInputs = new ArrayList<Memory>();
		expectedInputs.add(memoryInput4);
		assertArrayEquals(expectedInputs.toArray(), codeletContainer.getInputs().toArray());
		assertEquals("MEMORY_INPUT_4", codeletContainer.getInputs().get(0).getName());
		assertEquals(1, codeletContainer.getCodelet(codeletThree.getName()).inputs.size());
		
		assertEquals(1, codeletContainer.getBroadcast().size());
		expectedBroadcast = new ArrayList<Memory>();
		expectedBroadcast.add(memoryInput3);
		assertArrayEquals(expectedBroadcast.toArray(), codeletContainer.getBroadcast().toArray());
		assertEquals("MEMORY_INPUT_3", codeletContainer.getBroadcast().get(0).getName());
		assertEquals(1, codeletContainer.getCodelet(codeletTwo.getName()).broadcast.size());
		
		codeletContainer.removeCodelet(codeletTwo);
		
		assertEquals(1, codeletContainer.getOutputs().size());
		expectedOutputs = new ArrayList<Memory>();
		expectedOutputs.add(memoryOutput3);
		assertArrayEquals(expectedOutputs.toArray(), codeletContainer.getOutputs().toArray());
		assertEquals(1, codeletContainer.getCodelet(codeletThree.getName()).outputs.size());
		
		assertEquals(1, codeletContainer.getInputs().size());
		expectedInputs = new ArrayList<Memory>();
		expectedInputs.add(memoryInput4);
		assertArrayEquals(expectedInputs.toArray(), codeletContainer.getInputs().toArray());
		assertEquals("MEMORY_INPUT_4", codeletContainer.getInputs().get(0).getName());
		assertEquals(1, codeletContainer.getCodelet(codeletThree.getName()).inputs.size());
		
		assertEquals(0, codeletContainer.getBroadcast().size());
		assertEquals(0, codeletContainer.getCodelet(codeletThree.getName()).broadcast.size());
		
		
	}
	
	@Test
	public void getEvaluationTest() throws InterruptedException {
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
		
		CodeletContainer codeletContainer = new CodeletContainer(codeletContainerArray, false);
		Double testValue = 100.0;
		
		mind.insertCodelet(codeletOne);
		mind.insertCodelet(codeletTwo);
		mind.insertCodelet(codeletThree);
		memory1.setEvaluation(testValue);
		codeletContainer.setI(10);
		mind.start();
		Thread.sleep(2000);
		mind.shutDown();
		
	
		
		assertEquals(testValue, codeletContainer.getEvaluation());
		
		
	}
	
	@Test
	public void getActivationTest() throws InterruptedException {
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
		
		CodeletContainer codeletContainer = new CodeletContainer(codeletContainerArray, false);
		double testValue = 6.0;
		
		mind.insertCodelet(codeletOne);
		mind.insertCodelet(codeletTwo);
		mind.insertCodelet(codeletThree);
		memory1.setEvaluation(testValue);
		codeletContainer.setI(10);
		mind.start();
		Thread.sleep(2000);
		mind.shutDown();
		
	
		
		assertEquals(testValue, codeletContainer.getActivation(), 0);
		
	}
	
	@Test
	public void setInputsTest() throws InterruptedException {
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
		
		CodeletContainer codeletContainer = new CodeletContainer(codeletContainerArray, false);
		
		mind.insertCodelet(codeletOne);
		mind.insertCodelet(codeletTwo);
		mind.insertCodelet(codeletThree);

		ArrayList<Memory> newInputs = new ArrayList<Memory>();
		newInputs.add(memory1);
		codeletContainer.setInputs(newInputs);
		
	
		
		assertEquals(newInputs, codeletContainer.getInputs());
		
	}
	
	@Test
	public void setBroadcastTest() throws InterruptedException {
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
		
		CodeletContainer codeletContainer = new CodeletContainer(codeletContainerArray, false);
		
		mind.insertCodelet(codeletOne);
		mind.insertCodelet(codeletTwo);
		mind.insertCodelet(codeletThree);

		ArrayList<Memory> newBroadcast = new ArrayList<Memory>();
		newBroadcast.add(memory1);
		codeletContainer.setBroadcast(newBroadcast);
		
	
		
		assertEquals(newBroadcast, codeletContainer.getBroadcast());
		
	}
	
	@Test
	public void setNameTest() throws InterruptedException {
		CodeletContainer codeletContainer = new CodeletContainer();
		codeletContainer.setName("Container");
		assertEquals("Container", codeletContainer.getName());
	}
	
	@Test
	public void setTypeTest() throws InterruptedException {
		CodeletContainer codeletContainer = new CodeletContainer();
		codeletContainer.setType("Container");
		assertEquals("Container", codeletContainer.getName());
	}
	
	@Test
	public void setEvaluationTest() throws InterruptedException {
		Codelet codeletOne = new CodeletToTestOne("Codelet 1");
		Mind mind = new Mind();
		MemoryObject memory1 = mind.createMemoryObject("MEMORY1", 0.12);
		codeletOne.addInput(memory1);
		ArrayList<Codelet> codeletContainerArray = new ArrayList<Codelet>();
		codeletContainerArray.add(codeletOne);
		CodeletContainer codeletContainer = new CodeletContainer(codeletContainerArray, false);
		codeletContainer.setEvaluation(5.0);;
		assertEquals(5.0, codeletContainer.getCodelet("Codelet 1").getInputs().get(0).getEvaluation(),0);
	}
	
	@Test
	public void addMemoryObserverTest() throws InterruptedException {
		Codelet codeletOne = new CodeletToTestOne("Codelet 1");
		Codelet codeletTwo = new CodeletToTestTwo("Codelet 2");
		Codelet codeletThree = new CodeletToTestThree("Codelet 3");
		
		Mind mind = new Mind();
		MemoryObject memory1 = mind.createMemoryObject("MEMORY1", 0.12);
		MemoryObject memory2 = mind.createMemoryObject("MEMORY2", 0.32);
		MemoryObject memory3 = mind.createMemoryObject("MEMORY3", 0.32);
		MemoryObject memory4 = mind.createMemoryObject("MEMORY4", 0.32);
		
		codeletOne.setIsMemoryObserver(true);
		codeletOne.addInput(memory1);
		codeletOne.addBroadcast(memory2);
		
		codeletTwo.addBroadcast(memory3);
		
		codeletThree.addInput(memory4);
		
		ArrayList<Codelet> codeletContainerArray = new ArrayList<Codelet>();
		codeletContainerArray.add(codeletOne);
		codeletContainerArray.add(codeletTwo);
		codeletContainerArray.add(codeletThree);
		
		CodeletContainer codeletContainer = new CodeletContainer(codeletContainerArray, false);
		codeletContainer.addMemoryObserver(codeletOne);
		
		mind.insertCodelet(codeletOne);
		mind.insertCodelet(codeletTwo);
		mind.insertCodelet(codeletThree);
		codeletContainer.setI(10);
		mind.start();
		Thread.sleep(2000);
		mind.shutDown();
		
	
		CodeletToTestOne codeletToTestOne = (CodeletToTestOne) codeletContainer.getCodelet("Codelet 1");
		assertEquals(7, codeletToTestOne.getCounter());
	}
	
	@Test
	public void getTimestampTest() throws InterruptedException {
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
		
		CodeletContainer codeletContainer = new CodeletContainer(codeletContainerArray, false);
		
		mind.insertCodelet(codeletOne);
		mind.insertCodelet(codeletTwo);
		mind.insertCodelet(codeletThree);
		codeletContainer.setI(10);
		mind.start();
		Thread.sleep(2000);
		mind.shutDown();
		
		assertEquals(true, codeletContainer.getTimestamp().doubleValue() > 1);
		
	}


}
