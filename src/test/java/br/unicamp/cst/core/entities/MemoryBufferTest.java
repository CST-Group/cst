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


import static org.junit.Assert.*;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author wander
 *
 */
public class MemoryBufferTest {

    @Test
    public void basicCallTest(){
        RawMemory rawMemory = new RawMemory();
        MemoryBuffer memoryBuffer = new MemoryBuffer(3, rawMemory);

        List<MemoryObject> testList = Arrays.asList(new MemoryObject(), new MemoryObject(), new MemoryObject());
        testList.get(0).setType("memory_0");
        testList.get(1).setType("memory_1");
        testList.get(2).setType("memory_2");

        memoryBuffer.putList(testList);

        assertEquals(3, memoryBuffer.size());
        assertEquals(memoryBuffer.get(), memoryBuffer.getAll());
        assertEquals(testList.get(2), memoryBuffer.getMostRecent());
        assertEquals(testList.get(0), memoryBuffer.getOldest());
    }

    @Test
    public void putsMoreThanMaxTest(){
        RawMemory rawMemory = new RawMemory();
        MemoryBuffer memoryBuffer = new MemoryBuffer(3, rawMemory);

        List<MemoryObject> testList = Arrays.asList(new MemoryObject(), new MemoryObject(), new MemoryObject(), new MemoryObject());
        testList.get(0).setType("memory_0");
        testList.get(1).setType("memory_1");
        testList.get(2).setType("memory_2");
        testList.get(3).setType("memory_3");
        memoryBuffer.putList(testList);

        assertEquals(3, memoryBuffer.size());
        assertEquals(memoryBuffer.get(), memoryBuffer.getAll());
        assertEquals(testList.get(1), memoryBuffer.get().get(0));

        memoryBuffer.put(new MemoryObject());
        assertEquals(testList.get(2), memoryBuffer.get().get(0));
    }

    @Test
    public void putPopTest(){
        RawMemory rawMemory = new RawMemory();
        MemoryBuffer memoryBuffer = new MemoryBuffer(3, rawMemory);

        MemoryObject testMemory = new MemoryObject();
        testMemory.setType("memory_0");
        memoryBuffer.put(testMemory);

        assertEquals(testMemory, memoryBuffer.pop());
        assertEquals(0, memoryBuffer.size());
    }

    @Test
    public void nullOldestAndNewestTest(){
        RawMemory rawMemory = new RawMemory();
        MemoryBuffer memoryBuffer = new MemoryBuffer(3, rawMemory);

        assertNull(memoryBuffer.getOldest());
        assertNull(memoryBuffer.getMostRecent());
    }

    @Test
    public void removeAndClearTest(){
        RawMemory rawMemory = new RawMemory();
        MemoryBuffer memoryBuffer = new MemoryBuffer(3, rawMemory);

        List<MemoryObject> testList = Arrays.asList(new MemoryObject(), new MemoryObject(), new MemoryObject());
        testList.get(0).setType("memory_0");
        testList.get(1).setType("memory_1");
        testList.get(2).setType("memory_2");

        memoryBuffer.putList(testList);
        memoryBuffer.remove(testList.get(1));

        assertEquals(2, memoryBuffer.size());
        assertEquals(testList.get(2), memoryBuffer.get().get(1));

        memoryBuffer.clear();
        assertEquals(0, memoryBuffer.size());
    }

    @Test
    public void pintStatusTest(){
        RawMemory rawMemory = new RawMemory();
        MemoryBuffer memoryBuffer = new MemoryBuffer(3, rawMemory);

        List<MemoryObject> testList = Arrays.asList(new MemoryObject());
        testList.get(0).setType("memory_0");
        memoryBuffer.putList(testList);

        String expectedMessage = "###### Memory Buffer ########\n# Content: [MemoryObject [idmemoryobject=null, timestamp=null, evaluation=0.0, I=null, name=memory_0]]" +
                "\n# Size: 1\n###############################";

        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        memoryBuffer.printStatus();

        assertTrue(outputStreamCaptor.toString().trim().contains(expectedMessage));
    }

}
