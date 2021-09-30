package br.unicamp.cst.core.entities;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author wander
 *
 */
public class MemoryObjectBasicTest {

    @Test
    public void idTest(){
        MemoryObject mo = new MemoryObject();

        mo.setIdmemoryobject(2000L);

        assertEquals(2000L, (long)mo.getIdmemoryobject());
    }


    @Test
    public void toStringTest(){
        MemoryObject mo = new MemoryObject();
        Object I = new Object();

        mo.setIdmemoryobject(2000L);
        mo.setEvaluation(0.8);
        mo.setI(I);
        mo.setType("testName");

        String expectedString = "MemoryObject [idmemoryobject=" + 2000L + ", timestamp=" + mo.getTimestamp() + ", evaluation="
                + 0.8 + ", I=" + I + ", name=" + "testName" + "]";

        assertEquals(expectedString, mo.toString());
    }

    @Test
    public void hashCodeTest(){
        MemoryObject mo = new MemoryObject();
        Object I = new Object();
        Double eval = 0.8;
        Long id = 2000L;
        String name = "testName";

        mo.setIdmemoryobject(id);
        mo.setEvaluation(eval);
        mo.setI(I);
        mo.setType(name);

        final int prime = 31;
        int expectedValue = 1;
        expectedValue = prime * expectedValue + (I.hashCode());
        expectedValue = prime * expectedValue + (eval.hashCode());
        expectedValue = prime * expectedValue + (id.hashCode());
        expectedValue = prime * expectedValue + (name.hashCode());
        expectedValue = prime * expectedValue + ((mo.getTimestamp() == null) ? 0 : mo.getTimestamp().hashCode());

        assertEquals(expectedValue, mo.hashCode());
    }

    @Test
    public void equalsTest(){
        MemoryObject mo = new MemoryObject();
        MemoryObject otherMO = new MemoryObject();
        MemoryObject thirdMO = new MemoryObject();
        MemoryObject fourthMO = new MemoryObject();
        Mind mind = new Mind();

        assertFalse(mo.equals(null));
        assertFalse(mo.equals(mind));

        mo.setI(0.0);
        otherMO.setI(0.0);
        thirdMO.setI(1.0);

        assertFalse(fourthMO.equals(mo));
        assertFalse(mo.equals(thirdMO));

        mo.setEvaluation(0.0);
        otherMO.setEvaluation(0.0);
        thirdMO.setEvaluation(1.0);

        assertFalse(fourthMO.equals(mo));
        assertFalse(mo.equals(thirdMO));


        mo.setIdmemoryobject(1000L);
        otherMO.setIdmemoryobject(1000L);
        thirdMO.setIdmemoryobject(2000L);

        assertFalse(fourthMO.equals(mo));
        assertFalse(mo.equals(thirdMO));


        mo.setType("firstName");
        otherMO.setType("firstName");
        thirdMO.setType("secondName");

        assertFalse(fourthMO.equals(mo));
        assertFalse(mo.equals(thirdMO));


        mo.setTimestamp(100L);
        otherMO.setTimestamp(100L);
        thirdMO.setTimestamp(200L);
        fourthMO.setTimestamp(null);

        assertFalse(fourthMO.equals(mo));
        assertFalse(mo.equals(thirdMO));

        assertTrue(mo.equals(otherMO));
    }
}
