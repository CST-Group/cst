package br.unicamp.cst.io.rest;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMemoryJson {



    @Test
    public void testEquals() {
        MemoryObject memoryObject = new MemoryObject();

        memoryObject.setName("test");
        String group = "group";

        MemoryJson m1 = new MemoryJson(memoryObject);

        MemoryJson m2 = new MemoryJson(memoryObject, group);


        assertEquals(memoryObject.getName(), m1.name);
        assertEquals(memoryObject.getName(), m2.name);
        assertEquals(group, m2.group);

    }
}
