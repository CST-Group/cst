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
public class DisconnectedCodeletTest {
	
	@Test
	public void testDisconnectedCodelet() {
		
		Codelet disconnectedCodelet = new Codelet() {
				
			@Override
			public void accessMemoryObjects() {
			}
			
			@Override
			public void proc() {
								
			}
			
			@Override
			public void calculateActivation() {
								
			}
		};
		disconnectedCodelet.setName("Disconnected Codelet");
		disconnectedCodelet.start();
		
		try {
			disconnectedCodelet.getInput("TYPE", 0);
		}catch(Exception e) {
			assertEquals(e.getMessage(), "This Codelet could not find a memory object it needs: Disconnected Codelet");
		}		
	}

}
