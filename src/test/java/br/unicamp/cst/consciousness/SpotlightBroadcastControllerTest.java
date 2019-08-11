/**
 * 
 */
package br.unicamp.cst.consciousness;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;

/**
 * @author andre
 *
 */
public class SpotlightBroadcastControllerTest {
	
	@BeforeClass
    public static void beforeAllTestMethods() {
		

    }

	@AfterClass
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
