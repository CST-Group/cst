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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


/**
 * @author wander
 *
 */
public class MemoryObjectTest {

    @Test
    public void idTest(){
        MemoryObject mo = new MemoryObject();

        mo.setId(2000L);

        assertEquals(2000L, (long)mo.getId());
    }


    @Test
    public void toStringTest(){
        MemoryObject mo = new MemoryObject();
        Object I = new Object();

        mo.setId(2000L);
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

        mo.setId(id);
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

        mo.setI(0.0);
        otherMO.setI(0.0);
        thirdMO.setI(1.0);

        assertNotEquals(fourthMO, mo);
        assertNotEquals(mo, thirdMO);

        mo.setEvaluation(0.0);
        otherMO.setEvaluation(0.0);
        thirdMO.setEvaluation(1.0);

        fourthMO.setI(0.0);
        fourthMO.setEvaluation(null);

        assertNotEquals(fourthMO, mo);
        assertNotEquals(mo, thirdMO);


        mo.setId(1000L);
        otherMO.setId(2000L);
        thirdMO.setId(2000L);


        fourthMO.setEvaluation(0.0);
        fourthMO.setId(null);

        assertNotEquals(fourthMO, mo);
        assertNotEquals(mo, otherMO);

        otherMO.setId(1000L);
        fourthMO.setId(1000L);

        mo.setType("firstName");
        otherMO.setType("firstName");
        thirdMO.setType("secondName");

        assertNotEquals(fourthMO, mo);
        assertNotEquals(mo, thirdMO);

        fourthMO.setType("firstName");

        mo.setTimestamp(100L);
        otherMO.setTimestamp(100L);
        thirdMO.setTimestamp(200L);
        fourthMO.setTimestamp(null);

        assertNotEquals(fourthMO, mo);
        assertNotEquals(mo, thirdMO);

        fourthMO.setTimestamp(200L);
        assertNotEquals(fourthMO, mo);

        assertEquals(mo, otherMO);
    }



    @Test
    public void equalsFalseNullTest(){
        MemoryObject mo = new MemoryObject();
        MemoryObject otherMO = new MemoryObject();
        MemoryObject thirdMO = new MemoryObject();
        MemoryObject fourthMO = new MemoryObject();
        Mind mind = new Mind();

        /*
        * id
        * id
        * name
        * timestamp
        * timestamp
        * */

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


        mo.setId(1000L);
        otherMO.setId(1000L);
        thirdMO.setId(2000L);

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
