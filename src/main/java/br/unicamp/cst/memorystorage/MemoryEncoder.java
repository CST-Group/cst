package br.unicamp.cst.memorystorage;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;

import br.unicamp.cst.core.entities.Memory;

/**
 * Encodes and decodes Memories.
 */
public class MemoryEncoder {
    static Gson gson = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();
    
    private MemoryEncoder() {

    }

    /**
     * Encodes a memory to a Map.
     * 
     * @param memory memory to encode.
     * @return the encoded memory.
     */
    public static Map<String, String> toDict(Memory memory) {
        Map<String, String> data = new HashMap<>();
        
        data.put("name", memory.getName());
        data.put("evaluation", memory.getEvaluation().toString());
        data.put("id", memory.getId().toString());
        data.put("timestamp", memory.getTimestamp().toString());        

        data.put("I", gson.toJson(memory.getI()));
    
        return data;
    }

    /**
     * Load a memory from a Map.
     * 
     * @param memory memory to store the loaded info.
     * @param memoryDict map encoded memory.
     */
    public static void loadMemory(Memory memory, Map<String, String> memoryDict) {
        memory.setEvaluation(Double.parseDouble(memoryDict.get("evaluation")));
        memory.setId(Long.parseLong(memoryDict.get("id")));

        String infoJSON = memoryDict.get("I");
        Object info = gson.fromJson(infoJSON, Object.class);
        memory.setI(info);
    }

}
