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
package br.unicamp.cst.consciousness;


import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author andre
 *
 */
public class SpotlightBroadcastControllerTest {
	
    @BeforeAll
    public static void beforeAllTestMethods() {
		

    }

    @AfterAll
    public static void afterAllTestMethods() {
		
    }
    
    @Test
    public void testSpotlightBroadcastController() throws InterruptedException {
    	
		Mind mind = new Mind();
		
		HighActivationCodelet highActivationCodelet = new HighActivationCodelet("HighActivationCodelet");
		MemoryObject memoryHighActivation = mind.createMemoryObject(highActivationCodelet.getName());
		highActivationCodelet.addOutput(memoryHighActivation);	    
	    mind.insertCodelet(highActivationCodelet);
	    
	    MediumActivationCodelet mediumActivationCodelet = new MediumActivationCodelet("MediumActivationCodelet");
	    MemoryObject memoryMediumActivation = mind.createMemoryObject(mediumActivationCodelet.getName());
	    mediumActivationCodelet.addOutput(memoryMediumActivation);
	    mind.insertCodelet(mediumActivationCodelet);
	    
	    LowActivationCodelet lowActivationCodelet = new LowActivationCodelet("LowActivationCodelet");
	    MemoryObject memoryLowActivation = mind.createMemoryObject(lowActivationCodelet.getName());
	    lowActivationCodelet.addOutput(memoryLowActivation);
	    mind.insertCodelet(lowActivationCodelet);
	    
	    SpotlightBroadcastController spotlightBroadcastController = new SpotlightBroadcastController(mind.getCodeRack());
	    mind.insertCodelet(spotlightBroadcastController);
	    
	    mind.start();
	    
	    String messageExpected = "Winner message";
	    
	    memoryHighActivation.setI(messageExpected);
	    
	    Thread.sleep(2000);
	    
	    Memory actualLowActivationMemory = lowActivationCodelet.getBroadcast(highActivationCodelet.getName());
	    
	    String actualLowActivationMessage = (String) actualLowActivationMemory.getI();
	    
	    assertEquals(messageExpected, actualLowActivationMessage);
	    
	    Memory actualMediumActivationMemory = mediumActivationCodelet.getBroadcast(highActivationCodelet.getName());
	    
	    String actualMediumActivationMessage = (String) actualMediumActivationMemory.getI();
	    
	    assertEquals(messageExpected,actualMediumActivationMessage);
	    
	    Thread.sleep(1000);
	    
	    mind.shutDown();
    	
    }
}
