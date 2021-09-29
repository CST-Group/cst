package br.unicamp.cst.core.entities;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author wander
 *
 */

public class CodeletsMonitorTest {
    Codelet testCodelet = new Codelet() {

        @Override
        public void accessMemoryObjects() {}
        @Override
        public void proc() {
            System.out.println("proc method ran correctly!");
        }
        @Override
        public void calculateActivation() {}
    };

    Codelet otherCodelet = new Codelet() {

        @Override
        public void accessMemoryObjects() {}
        @Override
        public void proc() {

            System.out.println("proc method ran correctly!");
        }
        @Override
        public void calculateActivation() {}
    };


    @Test
    public void basicCallTest(){
        testCodelet.setName("testCodelet");
        otherCodelet.setName("otherCodelet");
        List<Codelet> testList = Arrays.asList(testCodelet, otherCodelet);
        CodeletsMonitor codeletsMonitor = new CodeletsMonitor(testList, 500, "testMonitor");

        codeletsMonitor.setTitle("testMonitor_2");

        codeletsMonitor.start();
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        codeletsMonitor.stop();
        assertEquals("testMonitor_2", codeletsMonitor.getTitle());
        assertFalse(codeletsMonitor.isAutoFixedRange());

        codeletsMonitor.setAutoFixedRange(true);
        assertTrue(codeletsMonitor.isAutoFixedRange());
    }

    @Test
    public void altCallTest(){
        testCodelet.setName("testCodelet");
        otherCodelet.setName("otherCodelet");
        List<Codelet> testList = Arrays.asList(testCodelet, otherCodelet);
        CodeletsMonitor codeletsMonitor = new CodeletsMonitor(testList, 500, "testMonitor", true, 1000);

        codeletsMonitor.setAutoRangeValue(500);

        codeletsMonitor.start();

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        assertEquals("testMonitor", codeletsMonitor.getTitle());
        assertTrue(codeletsMonitor.isAutoFixedRange());
        assertEquals(500, codeletsMonitor.getAutoRangeValue());

    }


}
