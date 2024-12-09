package br.unicamp.cst.memorystorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;

public class MemoryEncoderTest {
    
    @Test
    public void toDictTest()
    {
        MemoryObject memory = new MemoryObject();
        memory.setName("MemoryName");
        memory.setI(123.456);
        memory.setId(123l);
        memory.setEvaluation(0.5);
    
        Map<String, String> memoryDict = MemoryEncoder.toDict(memory);
        
        assertEquals("123.456", memoryDict.get("I"));
        assertEquals(memory.getTimestamp().toString(), memoryDict.get("timestamp"));
        assertEquals(Float.toString(0.5f), memoryDict.get("evaluation"));
        assertEquals("MemoryName", memoryDict.get("name"));
        assertEquals(memory.getId().toString(), memoryDict.get("id"));
    }

    @Test
    public void loadMemoryTest()
    {
        Memory memory = new MemoryObject();
        Map<String, String> memoryDict = new HashMap<String,String>();
        memoryDict.put("evaluation", "0.5");
        memoryDict.put("id", "123");
        memoryDict.put("I", "[5, 3, 4]");

        MemoryEncoder.loadMemory(memory, memoryDict);
        
        assertEquals(0.5, memory.getEvaluation());
        assertEquals(123, memory.getId());

        List<Long> info = (List<Long>) memory.getI();

        assertEquals(3, info.size());
        assertEquals(5, info.get(0));
        assertEquals(3, info.get(1));
        assertEquals(4, info.get(2));

    }
}
