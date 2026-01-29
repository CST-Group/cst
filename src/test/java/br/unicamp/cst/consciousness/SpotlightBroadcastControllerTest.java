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


import br.unicamp.cst.consciousness.SpotlightBroadcastController.Policy;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    public void testSpotlightBroadcastControllerPolicyMAX() throws InterruptedException {
    	
        Mind mind = new Mind();

        MockCodelet highActivationCodelet = new MockCodelet("HighActivationCodelet", 0.95d);
        MemoryObject memoryHighActivation = mind.createMemoryObject(highActivationCodelet.getName());
        highActivationCodelet.addOutput(memoryHighActivation);	    
        mind.insertCodelet(highActivationCodelet);

        MockCodelet mediumActivationCodelet = new MockCodelet("MediumActivationCodelet",0.5d);
        MemoryObject memoryMediumActivation = mind.createMemoryObject(mediumActivationCodelet.getName());
        mediumActivationCodelet.addOutput(memoryMediumActivation);
        mind.insertCodelet(mediumActivationCodelet);

        MockCodelet lowActivationCodelet = new MockCodelet("LowActivationCodelet", 0.1d);
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
    
    @Test
    public void testSpotlightBroadcastControllerPolicyMIN() throws InterruptedException {
    	
        Mind mind = new Mind();

        MockCodelet highActivationCodelet = new MockCodelet("HighActivationCodelet", 0.95d);
        MemoryObject memoryHighActivation = mind.createMemoryObject(highActivationCodelet.getName());
        highActivationCodelet.addOutput(memoryHighActivation);	    
        mind.insertCodelet(highActivationCodelet);

        MockCodelet mediumActivationCodelet = new MockCodelet("MediumActivationCodelet",0.5d);
        MemoryObject memoryMediumActivation = mind.createMemoryObject(mediumActivationCodelet.getName());
        mediumActivationCodelet.addOutput(memoryMediumActivation);
        mind.insertCodelet(mediumActivationCodelet);

        MockCodelet lowActivationCodelet = new MockCodelet("LowActivationCodelet", 0.1d);
        MemoryObject memoryLowActivation = mind.createMemoryObject(lowActivationCodelet.getName());
        lowActivationCodelet.addOutput(memoryLowActivation);
        mind.insertCodelet(lowActivationCodelet);

        SpotlightBroadcastController spotlightBroadcastController = new SpotlightBroadcastController(mind.getCodeRack(), Policy.MIN);
        mind.insertCodelet(spotlightBroadcastController);

        mind.start();

        String messageExpected = "Winner message";
        memoryLowActivation.setI(messageExpected);

        Thread.sleep(2000);

        Memory actualHighActivationMemory = highActivationCodelet.getBroadcast(lowActivationCodelet.getName());
        String actualHighActivationMessage = (String) actualHighActivationMemory.getI();
        assertEquals(messageExpected, actualHighActivationMessage);

        Memory actualMediumActivationMemory = mediumActivationCodelet.getBroadcast(lowActivationCodelet.getName());
        String actualMediumActivationMessage = (String) actualMediumActivationMemory.getI();
        assertEquals(messageExpected,actualMediumActivationMessage);

        Thread.sleep(1000);

        mind.shutDown();
    	
    }
    
    @Test
    public void testSpotlightBroadcastControllerPolicyALL_MAX() throws InterruptedException {
        Mind mind = new Mind();

        String messageM1 = "M1";
        String messageM2 = "M2";
        //Two codelets with high activation, one low
        MockCodelet highActivationCodelet = new MockCodelet("C1", 0.9); // High
        MemoryObject memoryHighActivation = mind.createMemoryObject(highActivationCodelet.getName());
        highActivationCodelet.addOutput(memoryHighActivation);
        
        MockCodelet highActivationCodelet2 = new MockCodelet("C2", 0.9); // High
        MemoryObject memoryHighActivation2 = mind.createMemoryObject(highActivationCodelet2.getName());
        highActivationCodelet2.addOutput(memoryHighActivation2);
        
        MockCodelet lowActivationCodelet = new MockCodelet("C3", 0.1); // Low
        MemoryObject memoryLowActivation = mind.createMemoryObject(lowActivationCodelet.getName());
        lowActivationCodelet.addOutput(memoryLowActivation);
        

        mind.insertCodelet(highActivationCodelet);
        mind.insertCodelet(highActivationCodelet2);
        mind.insertCodelet(lowActivationCodelet);

        // ALL_MAX with Threshold 0.7
        SpotlightBroadcastController sbc = new SpotlightBroadcastController(mind.getCodeRack(), Policy.ALL_MAX);
        sbc.setThresholdActivation(0.7); 
        mind.insertCodelet(sbc);
        
        mind.start();
        memoryHighActivation.setI(messageM1);
        memoryHighActivation2.setI(messageM2);
        Thread.sleep(1000);

        // lowActivationCodelet should receive broadcasts from BOTH high activation codelets
        Memory broadcastFromHighActivationCodelet = lowActivationCodelet.getBroadcast(highActivationCodelet.getName());
        Memory broadcastFromHighActivationCodelet2 = lowActivationCodelet.getBroadcast(highActivationCodelet2.getName());
        Memory broadcastFromLowActivationCodelet = lowActivationCodelet.getBroadcast(lowActivationCodelet.getName()); // Should be null or handled by implementation

        String actualbroadcastFromHighActivationCodeletMessage = (String) broadcastFromHighActivationCodelet.getI();
        String actualbroadcastFromHighActivationCodelet2Message = (String) broadcastFromHighActivationCodelet2.getI();
        assertEquals(actualbroadcastFromHighActivationCodeletMessage, messageM1);
        assertEquals(actualbroadcastFromHighActivationCodelet2Message, messageM2);
        assertTrue(broadcastFromLowActivationCodelet == null, "Should not receive broadcast from lowActivationCodelet");
        
        // Ensure lowActivationCodelet did not broadcast
        // Note: checking 'getBroadcast' on another codelet looking for lowActivationCodelet
        Memory lowActivationCodeletLeak = highActivationCodelet.getBroadcast(lowActivationCodelet.getName());
        assertTrue(lowActivationCodeletLeak == null, "lowActivationCodelet should not have been broadcasted");

        mind.shutDown();
    }
    
    @Test
    public void testSpotlightBroadcastControllerPolicyALL_MIN() throws InterruptedException {
        Mind mind = new Mind();

        String messageM1 = "M1";
        String messageM2 = "M2";
        
        //Two codelets with high activation, one low
        MockCodelet lowActivationCodelet = new MockCodelet("C1", 0.1); // High
        MemoryObject memoryLowActivation = mind.createMemoryObject(lowActivationCodelet.getName());
        lowActivationCodelet.addOutput(memoryLowActivation);
        
        MockCodelet lowActivationCodelet2 = new MockCodelet("C2", 0.3); // High
        MemoryObject memoryLowActivation2 = mind.createMemoryObject(lowActivationCodelet2.getName());
        lowActivationCodelet2.addOutput(memoryLowActivation2);
        
        MockCodelet highActivationCodelet = new MockCodelet("C3", 0.9); // Low
        MemoryObject memoryHighActivation = mind.createMemoryObject(highActivationCodelet.getName());
        highActivationCodelet.addOutput(memoryHighActivation);
        

        mind.insertCodelet(lowActivationCodelet);
        mind.insertCodelet(lowActivationCodelet2);
        mind.insertCodelet(highActivationCodelet);

        //ALL_MIN with Threshold 0.7
        SpotlightBroadcastController sbc = new SpotlightBroadcastController(mind.getCodeRack(), Policy.ALL_MIN);
        sbc.setThresholdActivation(0.7); 
        mind.insertCodelet(sbc);
        
        mind.start();
        memoryLowActivation.setI(messageM1);
        memoryLowActivation2.setI(messageM2);
        Thread.sleep(1000);

        //highActivationCodelet should receive broadcasts from BOTH highActivationCodelets
        Memory broadcastFromLowActivationCodelet = highActivationCodelet.getBroadcast(lowActivationCodelet.getName());
        Memory broadcastFromLowActivationCodelet2 = highActivationCodelet.getBroadcast(lowActivationCodelet2.getName());
        Memory broadcastFromHighActivationCodelet = highActivationCodelet.getBroadcast(highActivationCodelet.getName()); // Should be null or handled by implementation

        String actualbroadcastFromLowActivationCodeletMessage = (String) broadcastFromLowActivationCodelet.getI();
        String actualbroadcastFromLowActivationCodelet2Message = (String) broadcastFromLowActivationCodelet2.getI();
        assertEquals(actualbroadcastFromLowActivationCodeletMessage, messageM1);
        assertEquals(actualbroadcastFromLowActivationCodelet2Message, messageM2);
        assertTrue(broadcastFromHighActivationCodelet == null, "Should receive broadcast from highActivationCodelet");
        
        // Ensure highActivationCodelet did not broadcast
        // Note: checking 'getBroadcast' on another codelet looking for highActivationCodelet
        Memory highActivationCodeletLeak = lowActivationCodelet.getBroadcast(highActivationCodelet.getName());
        assertTrue(highActivationCodeletLeak == null, "highActivationCodelet should not have been broadcasted");

        mind.shutDown();
    }
    
    @Test
    public void testSpotlightBroadcastControllerPolicyITERATE() {
        Mind mind = new Mind();
        
        // 3 Codelets same activation
        MockCodelet c1 = new MockCodelet("C1", 0.5);
        MemoryObject memoryC1 = mind.createMemoryObject(c1.getName());
        c1.addOutput(memoryC1);
        MockCodelet c2 = new MockCodelet("C2", 0.5);
        MemoryObject memoryC2 = mind.createMemoryObject(c2.getName());
        c2.addOutput(memoryC2);
        MockCodelet c3 = new MockCodelet("C3", 0.5);
        MemoryObject memoryC3 = mind.createMemoryObject(c3.getName());
        c3.addOutput(memoryC3);
        
        mind.insertCodelet(c1);
        mind.insertCodelet(c2);
        mind.insertCodelet(c3);

        SpotlightBroadcastController sbc = new SpotlightBroadcastController(mind.getCodeRack(), Policy.ITERATE);
        mind.insertCodelet(sbc);

        // c1 chosen
        sbc.proc();
        assertTrue(c2.getBroadcast("C1") != null, "Cycle 1: C1 must be chosen");
        assertTrue(c3.getBroadcast("C2") == null, "Cycle 1: C2 must not be chosen");
        assertTrue(c2.getBroadcast("C3") == null, "Cycle 1: C3 must not be chosen");

        // c2 chosen0
        sbc.proc();
        assertTrue(c3.getBroadcast("C1") == null, "Cycle 2: C1 must not be chosen");
        assertTrue(c3.getBroadcast("C2") != null, "Cycle 2: C2 must be chosen");
        assertTrue(c2.getBroadcast("C3") == null, "Cycle 2: C3 must not be chosen");

        // c3 chosen
        sbc.proc();
        assertTrue(c3.getBroadcast("C1") == null, "Cycle 2: C1 must not be chosen");
        assertTrue(c2.getBroadcast("C2") == null, "Cycle 2: C2 must not be chosen");
        assertTrue(c1.getBroadcast("C3") != null, "Cycle 3: C3 must be chosen");
        
        // c2 chosen (loop completed)
        sbc.proc();
        assertTrue(c3.getBroadcast("C1") != null, "Cycle 1: C1 must be chosen");
        assertTrue(c2.getBroadcast("C2") == null, "Cycle 1: C2 must not be chosen");
        assertTrue(c1.getBroadcast("C3") == null, "Cycle 1: C3 must not be chosen");
    }
    
    @Test
    public void testSpotlightBroadcastControllerPolicyRANDOM_FLAT() throws InterruptedException {
        Mind mind = new Mind();
        MockCodelet c1 = new MockCodelet("C1", 0.5);
        MemoryObject memoryC1 = mind.createMemoryObject(c1.getName());
        c1.addOutput(memoryC1);
        
        MockCodelet c2 = new MockCodelet("C2", 0.5);
        MemoryObject memoryC2 = mind.createMemoryObject(c2.getName());
        c2.addOutput(memoryC2);
        
        mind.insertCodelet(c1);
        mind.insertCodelet(c2);

        SpotlightBroadcastController sbc = new SpotlightBroadcastController(mind.getCodeRack(), Policy.RANDOM_FLAT);
        mind.insertCodelet(sbc);
        
        String messageM1 = "M1";
        String messageM2 = "M2";
        mind.start();
        memoryC1.setI(messageM1);
        memoryC2.setI(messageM2);
        Thread.sleep(1000);

        boolean c1Received = (c1.getBroadcast("C2") != null);
        boolean c2Received = (c2.getBroadcast("C1") != null);

        assertTrue(c1Received || c2Received, "At lest one was broadcasted");
        if(c1Received){
            String actualBroadcastedMessage = (String) c1.getBroadcast("C2").getI();
            assertEquals(messageM2, actualBroadcastedMessage);
        } else{
            String actualBroadcastedMessage = (String) c2.getBroadcast("C1").getI();
            assertEquals(messageM1, actualBroadcastedMessage);
        }
    }
    
    @Test
    public void testSpotlightBroadcastControllerPolicyRANDOM_PROPORTIONAL() {
        Mind mind = new Mind();
        
        // C1 tem muita chance (0.99), C2 tem quase nenhuma (0.01)
        MockCodelet big = new MockCodelet("Big", 0.99);
        MemoryObject memoryC1 = mind.createMemoryObject(big.getName());
        big.addOutput(memoryC1);
        
        MockCodelet small = new MockCodelet("Small", 0.01);
        MemoryObject memoryC2 = mind.createMemoryObject(small.getName());
        small.addOutput(memoryC2);
        
        mind.insertCodelet(big);
        mind.insertCodelet(small);

        SpotlightBroadcastController sbc = new SpotlightBroadcastController(mind.getCodeRack(), Policy.RANDOM_PROPORTIONAL);
        mind.insertCodelet(sbc);

        int bigWins = 0;
        int smallWins = 0;

        // Run 1k turns
        for (int i = 0; i < 1000; i++) {
            sbc.proc();

            if (small.getBroadcast("Big") != null)
                bigWins++;
            else if (big.getBroadcast("Small") != null)
                smallWins++;
        }

        assertTrue(bigWins > smallWins, "Biggest activation wins more");
        assertTrue(bigWins > 800, "In 1k turns the Big wins much more");
    }
    
    @Test
    public void testSpotlightBroadcastControllerGettersAndSettersPolicyActivation() {
        Mind mind = new Mind();
        SpotlightBroadcastController sbc = new SpotlightBroadcastController(mind.getCodeRack());

        // Test Default
        assertEquals(Policy.MAX, sbc.getPolicy());
        assertEquals(0.9d, sbc.getThresholdActivation());

        // Test Setters
        sbc.setPolicy(Policy.MIN);
        sbc.setThresholdActivation(0.5d);

        // Test Getters
        assertEquals(Policy.MIN, sbc.getPolicy());
        assertEquals(0.5d, sbc.getThresholdActivation());
    }
    
    class MockCodelet extends Codelet {
        public MockCodelet(String name, double activation) {
            this.setName(name);
            try {
                this.setActivation(activation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void accessMemoryObjects() { }

        @Override
        public void calculateActivation() {
        }

        @Override
        public void proc() { }
    }
}
