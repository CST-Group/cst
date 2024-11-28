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


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class RawMemoryTest {


    @Test
    public void getAllOfTypeTest(){
        RawMemory rawMemory = new RawMemory();
        List<Memory> testList = Arrays.asList(new MemoryObject(), new MemoryObject(), new MemoryObject(), new MemoryObject());
        testList.get(0).setType("TYPE");
        testList.get(1).setType("TYPE");
        rawMemory.setAllMemories(testList);

        assertEquals(2, rawMemory.getAllOfType("TYPE").size());
        assertEquals(testList.subList(0,2), rawMemory.getAllOfType("TYPE"));

    }

    @Test
    public void printContentTest(){
        RawMemory rawMemory = new RawMemory();
        MemoryObject mem = new MemoryObject();
        mem.setType("TYPE");
        rawMemory.addMemory(mem);
        String expectedMessage = "MemoryObject [idmemoryobject=" + mem.getId() + ", timestamp=" + mem.getTimestamp() + ", evaluation="
                + 0.0 + ", I=" + null + ", name=" + "TYPE" + "]";

        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        rawMemory.printContent();
        assertTrue(outputStreamCaptor.toString().trim().contains(expectedMessage));
    }

    @Test
    public void createAndDestroyMemoryTest(){
        RawMemory rawMemory = new RawMemory();
        rawMemory.createMemoryObject("TYPE");

        assertEquals(1, rawMemory.size());
        rawMemory.destroyMemory(rawMemory.getAllMemoryObjects().get(0));

        assertEquals(0, rawMemory.size());
    }

    @Test
    public void shutdownTest(){
        RawMemory rawMemory = new RawMemory();
        List<Memory> testList = Arrays.asList(new MemoryObject(), new MemoryObject(), new MemoryObject(), new MemoryObject());
        rawMemory.setAllMemories(testList);

        assertEquals(4, rawMemory.size());

        rawMemory.shutDown();
        assertEquals(0, rawMemory.size());
    }
    
}
