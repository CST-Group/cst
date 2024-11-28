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

import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.cst.core.exceptions.CodeletThresholdBoundsException;
import java.util.Arrays;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


/**
 * @author wander
 *
 */
public class CodeletTest {
    // This class contains tests covering some core Codelet methods
    
    // This method is used to generate a new Codelet
    private Codelet generateCodelet() {
        Codelet testCodelet = new Codelet() {

        @Override
        public void accessMemoryObjects() {}
        @Override
        public void proc() {
            //ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
            //System.setOut(new PrintStream(outputStreamCaptor));
            System.out.println("running the proc() method!");
        }
        @Override
        public void calculateActivation() {}
    };
     return(testCodelet);   
    }    


    @Test
    public void testExceptionOnRun() {
        Codelet testCodelet = generateCodelet();
        System.out.println("testando");
    }


    @Test
    public void getIsLoopTest(){
        Codelet testCodelet = generateCodelet();
        // Any instantiated Codelet, if not changed, should be looping
        assertTrue(testCodelet.isLoop());
    }

    @Test
    public void upperActivationBoundException(){
        Codelet testCodelet = generateCodelet();
        Exception exception = assertThrows(CodeletActivationBoundsException.class, () -> {
            testCodelet.setActivation(2.0);
        });
        String expectedMessage = "Codelet activation set to value > 1.0";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals(1.0, testCodelet.getActivation(), 0);
    }

    @Test
    public void lowerActivationBoundException(){
        Codelet testCodelet = generateCodelet();
        Exception exception = assertThrows(CodeletActivationBoundsException.class, () -> {
            testCodelet.setActivation(-0.8);
        });
        String expectedMessage = "Codelet activation set to value < 0.0";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals(0.0, testCodelet.getActivation(), 0);
    }

    @Test
    public void setInputsTest(){
        Codelet testCodelet = generateCodelet();
        List<Memory> dummyInputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        testCodelet.setInputs(dummyInputs);
        assertEquals(2, testCodelet.getInputs().size());
    }

    @Test
    public void getInputTest(){
        Codelet testCodelet = generateCodelet();
        List<Memory> dummyInputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        dummyInputs.get(0).setName("testName1");
        testCodelet.setInputs(dummyInputs);
        assertEquals(dummyInputs.get(0), testCodelet.getInput("testName1"));
    }

    @Test
    public void getInputNullTest(){
        Codelet testCodelet = generateCodelet();
        List<Memory> dummyInputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        testCodelet.setInputs(dummyInputs);

        assertNull(testCodelet.getInput("testName2"));
    }

    @Test
    public void addInputsTest(){
        Codelet testCodelet = generateCodelet();
        List<Memory> dummyInputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        testCodelet.addInputs(dummyInputs);
        assertEquals(2, testCodelet.getInputs().size());
    }


    @Test
    public void removesInputTest(){
        Codelet testCodelet = generateCodelet();
        Memory toRemove = new MemoryObject();

        testCodelet.addInput(toRemove);
        assertEquals(1, testCodelet.getInputs().size());

        testCodelet.removesInput(toRemove);
        assertEquals(0, testCodelet.getInputs().size());
    }

    @Test
    public void removeFromInputTest(){
        Codelet testCodelet = generateCodelet();
        List<Memory> toRemove = Arrays.asList(new MemoryObject(), new MemoryObject());

        testCodelet.addInputs(toRemove);
        assertEquals(2, testCodelet.getInputs().size());

        testCodelet.removeFromInput(toRemove);
        assertEquals(0, testCodelet.getInputs().size());
    }

    @Test
    public void removeFromOutputTest(){
        Codelet testCodelet = generateCodelet();
        List<Memory> toRemove = Arrays.asList(new MemoryObject(), new MemoryObject());

        testCodelet.addOutputs(toRemove);
        assertEquals(2, testCodelet.getOutputs().size());

        testCodelet.removeFromOutput(toRemove);
        assertEquals(0, testCodelet.getOutputs().size());
    }

    @Test
    public void addOutputsTest(){
        Codelet testCodelet = generateCodelet();
        List<Memory> dummyOutputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        testCodelet.addOutputs(dummyOutputs);
        assertEquals(2, testCodelet.getOutputs().size());
    }

    @Test
    public void getOutputsTest(){
        Codelet testCodelet = generateCodelet();
        List<Memory> dummyOutputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        testCodelet.addOutputs(dummyOutputs);
        assertEquals(dummyOutputs, testCodelet.getOutputs());
    }

    @Test
    public void getOutputTest(){
        Codelet testCodelet = generateCodelet();
        List<Memory> dummyOutputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        dummyOutputs.get(0).setName("testName3");
        testCodelet.addOutputs(dummyOutputs);
        assertEquals(dummyOutputs.get(0), testCodelet.getOutput("testName3"));
    }

    @Test
    public void getOutputNullReturnTest(){
        Codelet testCodelet = generateCodelet();
        List<Memory> dummyOutputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        testCodelet.addOutputs(dummyOutputs);
        assertNull(testCodelet.getOutput("testName4"));
    }

    @Test
    public void getOutputEnableFalseTest(){
        boolean exceptionThrown = false;
        Codelet testCodelet = null;
        testCodelet = generateCodelet();
        testCodelet.setTimeStep(50);
        testCodelet.setName("thisCodeletWillFail");
        List<Memory> dummyOutputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        testCodelet.setOutputs(dummyOutputs);
    
    
        Memory output = testCodelet.getOutput("testType", 3); 
        assertNull(output);

        Mind mind = new Mind();
        mind.insertCodelet(testCodelet);
        mind.start();
        try{
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {

        }
        

        mind.shutDown();

        assertFalse(testCodelet.getEnabled());
        testCodelet.setEnabled(true);
        assertTrue(testCodelet.getEnabled());
    }

    @Test
    public void setOutputsTest(){
        Codelet testCodelet = generateCodelet();
        List<Memory> dummyOutputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        testCodelet.setOutputs(dummyOutputs);
        assertEquals(2, testCodelet.getOutputs().size());
    }

    @Test
    public void getInputsOfTypeTest(){
        Codelet testCodelet = generateCodelet();
        List<Memory> dummyInputs = Arrays.asList(new MemoryObject(), new MemoryObject(), new MemoryObject(), new MemoryObject());

        dummyInputs.get(0).setName("toGet");
        dummyInputs.get(1).setName("toGet");

        testCodelet.addInputs(dummyInputs);
        assertEquals(2, testCodelet.getInputsOfType("toGet").size());
    }

    @Test
    public void getOutputsOfTypeTest(){
        Codelet testCodelet = generateCodelet();
        List<Memory> dummyOutputs = Arrays.asList(new MemoryObject(), new MemoryObject(), new MemoryObject(), new MemoryObject());

        dummyOutputs.get(0).setName("toGet");
        dummyOutputs.get(1).setName("toGet");

        testCodelet.addOutputs(dummyOutputs);
        assertEquals(2, testCodelet.getOutputsOfType("toGet").size());
    }

    @Test
    public void getBroadcastNullTest(){
        Codelet testCodelet = generateCodelet();
        assertNull(testCodelet.getBroadcast("testName5"));
    }

    @Test
    public void getBroadcastTypeTest(){
        Codelet testCodelet = generateCodelet();
        List<Memory> dummyOutputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        dummyOutputs.get(0).setName("testName6");
        testCodelet.addBroadcasts(dummyOutputs);
        assertEquals(dummyOutputs.get(0), testCodelet.getBroadcast("testName6", 0));
    }

    @Test
    public void getBroadcastTypeIndexTest(){
        Codelet testCodelet = generateCodelet();
        List<Memory> dummyOutputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        dummyOutputs.get(0).setName("testName");
        dummyOutputs.get(1).setName("testName");
        testCodelet.addBroadcasts(dummyOutputs);
        assertEquals(dummyOutputs.get(1), testCodelet.getBroadcast("testName", 1));
    }

    @Test
    public void addBroadcastsTest(){
        Codelet testCodelet = generateCodelet();
        List<Memory> dummyOutputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        testCodelet.addBroadcasts(dummyOutputs);
        assertEquals(2, testCodelet.getBroadcast().size());
    }

    @Test
    public void getThreadNameTest(){
        Codelet testCodelet = generateCodelet();
        Thread.currentThread().setName("newThreadName");
        assertEquals("newThreadName", testCodelet.getThreadName());
    }

    @Test
    public void toStringTest(){
        Codelet testCodelet = generateCodelet();
        List<Memory> dummyInputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        List<Memory> dummyBroadcasts = Arrays.asList(new MemoryObject(), new MemoryObject());
        String expectedString = "Codelet [activation=" + 0.5 + ", " + "name=" + "testName" + ", "
                + ("broadcast=" + dummyBroadcasts.subList(0, Math.min(dummyBroadcasts.size(), 10)) + ", ")
                + ("inputs=" + dummyInputs.subList(0, Math.min(dummyInputs.size(), 10)) + ", ")
                + ("outputs=" + "[]") + "]";

        testCodelet.setName("testName");
        try{testCodelet.setActivation(0.5);}
        catch (Exception e){
            e.printStackTrace();
        }
        testCodelet.setInputs(dummyInputs);
        testCodelet.setBroadcast(dummyBroadcasts);
        assertEquals(expectedString, testCodelet.toString());
    }

    @Test
    public void setThresholdTest(){
        Codelet testCodelet = generateCodelet();
        try {
            testCodelet.setThreshold(0.5);
        } catch (CodeletThresholdBoundsException e) {
            e.printStackTrace();
        }
        assertEquals(0.5, testCodelet.getThreshold(), 0);
    }

    @Test
    public void upperThresholdBoundTest(){
        Codelet testCodelet = generateCodelet();
        Exception exception = assertThrows(CodeletThresholdBoundsException.class, () -> {
            testCodelet.setThreshold(2.0);
        });
        String expectedMessage = "Codelet threshold set to value > 1.0";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals(1.0, testCodelet.getThreshold(), 0);

    }

    @Test
    public void lowerThresholdBoundTest(){
        Codelet testCodelet = generateCodelet();
        Exception exception = assertThrows(CodeletThresholdBoundsException.class, () -> {
            testCodelet.setThreshold(-1.0);
        });
        String expectedMessage = "Codelet threshold set to value < 0.0";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals(0.0, testCodelet.getThreshold(), 0);
    }

    @Test
    public void getTimeStepTest(){
        Codelet testCodelet = generateCodelet();
        testCodelet.setTimeStep(222);
        assertEquals(222, testCodelet.getTimeStep());
    }


    @Test
    public void runProfilingTest(){
        Codelet testCodelet = generateCodelet();
        testCodelet.setProfiling(true);
        testCodelet.setTimeStep(50);

        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        Mind mind = new Mind();
        mind.insertCodelet(testCodelet);
        mind.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(testCodelet.isProfiling());
    }


}
