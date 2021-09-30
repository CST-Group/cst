package br.unicamp.cst.core.entities;

import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.cst.core.exceptions.CodeletThresholdBoundsException;
import java.util.Arrays;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author wander
 *
 */
public class CodeletBasicTest {
    // This class contains tests covering some core Codelet methods
    Codelet testCodelet = new Codelet() {

        @Override
        public void accessMemoryObjects() {}
        @Override
        public void proc() {
            //ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
            //System.setOut(new PrintStream(outputStreamCaptor));
            System.out.println("proc method ran correctly!");
        }
        @Override
        public void calculateActivation() {}
    };


    @Test
    public void testExceptionOnRun() {
    }


    @Test
    public void getIsLoopTest(){
        // Any instantiated Codelet, if not changed, should be looping
        assertTrue(testCodelet.isLoop());
    }

    @Test
    public void upperActivationBoundException(){
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
        List<Memory> dummyInputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        testCodelet.setInputs(dummyInputs);
        assertEquals(2, testCodelet.getInputs().size());
    }

    @Test
    public void getInputTest(){
        List<Memory> dummyInputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        dummyInputs.get(0).setType("testName");
        testCodelet.setInputs(dummyInputs);
        assertEquals(dummyInputs.get(0), testCodelet.getInput("testName"));
    }

    @Test
    public void getInputNullTest(){
        List<Memory> dummyInputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        testCodelet.setInputs(dummyInputs);

        assertNull(testCodelet.getInput("testName"));
    }

    @Test
    public void addInputsTest(){
        List<Memory> dummyInputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        testCodelet.addInputs(dummyInputs);
        assertEquals(2, testCodelet.getInputs().size());
    }


    @Test
    public void removesInputTest(){
        Memory toRemove = new MemoryObject();

        testCodelet.addInput(toRemove);
        assertEquals(1, testCodelet.getInputs().size());

        testCodelet.removesInput(toRemove);
        assertEquals(0, testCodelet.getInputs().size());
    }

    @Test
    public void removeFromInputTest(){
        List<Memory> toRemove = Arrays.asList(new MemoryObject(), new MemoryObject());

        testCodelet.addInputs(toRemove);
        assertEquals(2, testCodelet.getInputs().size());

        testCodelet.removeFromInput(toRemove);
        assertEquals(0, testCodelet.getInputs().size());
    }

    @Test
    public void removeFromOutputTest(){
        List<Memory> toRemove = Arrays.asList(new MemoryObject(), new MemoryObject());

        testCodelet.addOutputs(toRemove);
        assertEquals(2, testCodelet.getOutputs().size());

        testCodelet.removeFromOutput(toRemove);
        assertEquals(0, testCodelet.getOutputs().size());
    }

    @Test
    public void addOutputsTest(){
        List<Memory> dummyOutputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        testCodelet.addOutputs(dummyOutputs);
        assertEquals(2, testCodelet.getOutputs().size());
    }

    @Test
    public void getOutputsTest(){
        List<Memory> dummyOutputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        testCodelet.addOutputs(dummyOutputs);
        assertEquals(dummyOutputs, testCodelet.getOutputs());
    }

    @Test
    public void getOutputTest(){
        List<Memory> dummyOutputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        dummyOutputs.get(0).setType("testName");
        testCodelet.addOutputs(dummyOutputs);
        assertEquals(dummyOutputs.get(0), testCodelet.getOutput("testName"));
    }

    @Test
    public void getOutputNullReturnTest(){
        List<Memory> dummyOutputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        testCodelet.addOutputs(dummyOutputs);
        assertNull(testCodelet.getOutput("testName"));
    }

    @Test
    public void getOutputEnableFalseTest(){
        List<Memory> dummyOutputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        testCodelet.setOutputs(dummyOutputs);
        testCodelet.getOutput("testType", 3);


        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        Mind mind = new Mind();
        mind.insertCodelet(testCodelet);
        mind.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(outputStreamCaptor.toString().trim().contains("proc method ran correctly!"));
        mind.shutDown();
    }

    @Test
    public void setOutputsTest(){
        List<Memory> dummyOutputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        testCodelet.setOutputs(dummyOutputs);
        assertEquals(2, testCodelet.getOutputs().size());
    }

    @Test
    public void getInputsOfTypeTest(){
        List<Memory> dummyInputs = Arrays.asList(new MemoryObject(), new MemoryObject(), new MemoryObject(), new MemoryObject());

        dummyInputs.get(0).setType("toGet");
        dummyInputs.get(1).setType("toGet");

        testCodelet.addInputs(dummyInputs);
        assertEquals(2, testCodelet.getInputsOfType("toGet").size());
    }

    @Test
    public void getOutputsOfTypeTest(){
        List<Memory> dummyOutputs = Arrays.asList(new MemoryObject(), new MemoryObject(), new MemoryObject(), new MemoryObject());

        dummyOutputs.get(0).setType("toGet");
        dummyOutputs.get(1).setType("toGet");

        testCodelet.addOutputs(dummyOutputs);
        assertEquals(2, testCodelet.getOutputsOfType("toGet").size());
    }

    @Test
    public void getBroadcastNullTest(){
        assertNull(testCodelet.getBroadcast("testName"));
    }

    @Test
    public void getBroadcastTypeTest(){
        List<Memory> dummyOutputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        dummyOutputs.get(0).setType("testName");
        testCodelet.addBroadcasts(dummyOutputs);
        assertEquals(dummyOutputs.get(0), testCodelet.getBroadcast("testName", 0));
    }

    @Test
    public void getBroadcastTypeIndexTest(){
        List<Memory> dummyOutputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        dummyOutputs.get(0).setType("testName");
        dummyOutputs.get(1).setType("testName");
        testCodelet.addBroadcasts(dummyOutputs);
        assertEquals(dummyOutputs.get(1), testCodelet.getBroadcast("testName", 1));
    }

    @Test
    public void addBroadcastsTest(){
        List<Memory> dummyOutputs = Arrays.asList(new MemoryObject(), new MemoryObject());
        testCodelet.addBroadcasts(dummyOutputs);
        assertEquals(2, testCodelet.getBroadcast().size());
    }

    @Test
    public void getThreadNameTest(){
        Thread.currentThread().setName("testName");
        assertEquals("testName", testCodelet.getThreadName());
    }

    @Test
    public void toStringTest(){
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
        try {
            testCodelet.setThreshold(0.5);
        } catch (CodeletThresholdBoundsException e) {
            e.printStackTrace();
        }
        assertEquals(0.5, testCodelet.getThreshold(), 0);
    }

    @Test
    public void upperThresholdBoundTest(){
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
        testCodelet.setTimeStep(222);
        assertEquals(222, testCodelet.getTimeStep());
    }


    @Test
    public void runProfilingTest(){
        testCodelet.setProfiling(true);

        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        Mind mind = new Mind();
        mind.insertCodelet(testCodelet);
        mind.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(testCodelet.isProfiling());
    }


}
