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
    
    String message = "";
	
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
                try {   
                    System.out.println("Starting the codelet ...");
                    disconnectedCodelet.start();
                    disconnectedCodelet.getInput("TYPE", 0);
                    disconnectedCodelet.stop();
		}catch(Exception e) {
                     message = e.getMessage();
                     System.out.println("Testing DisconnectedCodelet:"+e.getMessage());
			//assertEquals(e.getMessage(), "This Codelet could not find a memory object it needs: Disconnected Codelet");
		}
                //assertEquals(message, "This Codelet could not find a memory object it needs: Disconnected Codelet");
                disconnectedCodelet.stop();
                System.out.println("Codelet stopped !");
	}

}
