package br.unicamp.cst.core.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author wander
 *
 */
public class CodeletBasicTests {

    @Test
    public void testExceptionOnRun() {
    }

    @Test
    public void getIsLoopTest(){

        Codelet testCodelet = new Codelet() {

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

        // Any instantiated Codelet, if not changed, should be looping
        assertTrue(testCodelet.isLoop());
    }
}
