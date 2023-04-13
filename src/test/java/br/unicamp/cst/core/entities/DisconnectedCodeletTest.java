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
package br.unicamp.cst.core.entities;

import org.junit.jupiter.api.Test;


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
