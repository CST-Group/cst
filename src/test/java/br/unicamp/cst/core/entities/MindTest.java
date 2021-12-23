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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author wander
 *
 */
public class MindTest {
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
    public void createCodeletGroupTest(){
        Mind mind = new Mind();

        mind.createCodeletGroup("testGroup");
        assertTrue(mind.getCodeletGroups().containsKey("testGroup"));
    }

    @Test
    public void createMemoryGroupTest(){
        Mind mind = new Mind();

        mind.createMemoryGroup("testGroup");
        assertTrue(mind.getMemoryGroups().containsKey("testGroup"));
    }

    @Test
    public void insertCodeletWGroupTest(){
        Mind mind = new Mind();
        mind.createCodeletGroup("testGroup");
        mind.insertCodelet(testCodelet, "testGroup");


        assertEquals(1,mind.getCodeRack().getAllCodelets().size());
        assertTrue(mind.getCodeletGroups().containsKey("testGroup"));
        assertEquals(testCodelet, mind.getCodeletGroupList("testGroup").get(0));
    }

    @Test
    public void registerMemoryWGroupTest(){
        Mind mind = new Mind();
        MemoryObject mo = new MemoryObject();

        mind.createMemoryGroup("testGroup");
        mind.registerMemory(mo, "testGroup");

        assertTrue(mind.getMemoryGroups().containsKey("testGroup"));
        assertEquals(mo, mind.getMemoryGroups().get("testGroup").get(0));
        assertEquals(1, mind.getMemoryGroupList("testGroup").size());
    }

    @Test
    public void registerMemoryByNnameTest(){
        Mind mind = new Mind();
        MemoryObject mo = new MemoryObject();
        mo.setType("testName");

        mind.createMemoryGroup("testGroup");
        mind.rawMemory.addMemory(mo);
        mind.registerMemory("testName", "testGroup");

        assertTrue(mind.getMemoryGroups().containsKey("testGroup"));
        assertEquals(mo, mind.getMemoryGroups().get("testGroup").get(0));
        assertEquals(1, mind.getMemoryGroups().get("testGroup").size());
    }
}
