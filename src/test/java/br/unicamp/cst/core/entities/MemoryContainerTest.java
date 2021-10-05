/**
 * 
 */
package br.unicamp.cst.core.entities;

import org.junit.Test;

import java.util.function.BinaryOperator;
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

/**
 * @author andre
 *
 */
public class MemoryContainerTest {

	
	@Test
	public void testMemoryContainerContent() {
		
		Memory memoryContainer = new MemoryContainer("TYPE");
		
		((MemoryContainer) memoryContainer).setI(71L, 0.1D, "TYPE");
		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE");
			
		assertEquals(75L, memoryContainer.getI());
	}
	
	@Test
	public void testMemoryContainerSize() {
		
		Memory memoryContainer = new MemoryContainer("TYPE");
		
		((MemoryContainer) memoryContainer).setI(71L, 0.1D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE3");
			
		assertEquals(2, ((MemoryContainer) memoryContainer).getAllMemories().size());
	}

	@Test
	public void setTypeTest(){
		MemoryContainer memoryContainer = new MemoryContainer();
		memoryContainer.setType("TYPE");

		assertEquals("TYPE", memoryContainer.getName());
	}

	@Test
	public void getITest() {

		Memory memoryContainer = new MemoryContainer("TYPE");

		((MemoryContainer) memoryContainer).setI(71L, 0.1D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(70L, 0.3D, "TYPE3");

		assertEquals(70L, ((MemoryContainer) memoryContainer).getI());
		assertEquals(75L, ((MemoryContainer) memoryContainer).getI(0));
		assertNull(((MemoryContainer) memoryContainer).getI(2));
		assertEquals(70L, ((MemoryContainer) memoryContainer).getI("TYPE3"));
	}

	@Test
	public void getIPredicateTest() {

		Memory memoryContainer = new MemoryContainer("TYPE");

		((MemoryContainer) memoryContainer).setI(71L, 0.1D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(70L, 0.3D, "TYPE3");
		((MemoryContainer) memoryContainer).setI(70L, 0.25D);

		Predicate<Memory> pred = new Predicate<Memory>() {
			@Override
			public boolean test(Memory memory) {
				return memory.getName().equals("TYPE2");
			}
		};

		assertEquals(75L, ((MemoryContainer) memoryContainer).getI(pred));
	}

	@Test
	public void getIAccumulatorTest() {

		Memory memoryContainer = new MemoryContainer("TYPE");

		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(70L, 0.3D, "TYPE3");
		((MemoryContainer) memoryContainer).setI(80L);

		BinaryOperator<Memory> binaryOperator = (mem1,mem2) -> mem1.getEvaluation() <= mem2.getEvaluation() ? mem1 : mem2;

		assertEquals(80L, ((MemoryContainer) memoryContainer).getI(binaryOperator));
	}

	@Test
	public void setISpecificTest() {

		Memory memoryContainer = new MemoryContainer("TYPE");

		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(70L, 0.3D, "TYPE3");
		((MemoryContainer) memoryContainer).setI(80L);

		((MemoryContainer) memoryContainer).setI(60L, 1);
		((MemoryContainer) memoryContainer).setI(90L, 0.5D, 2);

		assertEquals(60L, ((MemoryContainer) memoryContainer).getI(1));
		assertEquals(90L, ((MemoryContainer) memoryContainer).getI());
		assertEquals(0.5D, ((MemoryContainer) memoryContainer).getEvaluation(), 0);
	}

	@Test
	public void setEvaluationTest() {

		Memory memoryContainer = new MemoryContainer("TYPE");

		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(70L, 0.3D, "TYPE3");

		((MemoryContainer) memoryContainer).setI(90L, 0.5D, 2);


		assertEquals(70L, ((MemoryContainer) memoryContainer).getI());
		((MemoryContainer) memoryContainer).setEvaluation(0.5D, 0);
		assertEquals(75L, ((MemoryContainer) memoryContainer).getI());
	}

	@Test
	public void setEvaluationErrorTest() {
		Memory memoryContainer = new MemoryContainer("TYPE");

		Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
			memoryContainer.setEvaluation(2.0);
		});
		String expectedMessage = "This method is not available for MemoryContainer. Use setEvaluation(Double eval, int index) instead";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	public void addTest() {

		Memory memoryContainer = new MemoryContainer("TYPE");

		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(70L, 0.3D, "TYPE3");
		((MemoryContainer) memoryContainer).add(new MemoryObject());


		assertEquals(3, ((MemoryContainer) memoryContainer).getAllMemories().size());
	}

	@Test
	public void getInternalTest() {

		Memory memoryContainer = new MemoryContainer("TYPE");

		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(70L, 0.3D, "TYPE3");

		assertEquals(75L, ((MemoryContainer) memoryContainer).getInternalMemory("TYPE2").getI());
		assertNull(((MemoryContainer) memoryContainer).getInternalMemory("TYPE4"));
	}

	@Test
	public void getTimestampTest() {

		Memory memoryContainer = new MemoryContainer("TYPE");

		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(70L, 0.3D, "TYPE3");

		assertEquals(((MemoryContainer) memoryContainer).getInternalMemory("TYPE3").getTimestamp(),
				((MemoryContainer) memoryContainer).getTimestamp());
	}

}
