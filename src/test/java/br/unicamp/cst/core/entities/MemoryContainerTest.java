/**
 * 
 */
package br.unicamp.cst.core.entities;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
	
}
