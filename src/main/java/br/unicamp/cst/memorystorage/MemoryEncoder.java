package br.unicamp.cst.memorystorage;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import br.unicamp.cst.core.entities.Memory;

public class MemoryEncoder {
    static Gson gson = new Gson();

    private MemoryEncoder() {

    }

    public static Map<String, String> toDict(Memory memory) {
        Map<String, String> data = new HashMap<>();
        
        data.put("name", memory.getName());
        data.put("evaluation", memory.getEvaluation().toString());
        data.put("id", memory.getId().toString());
        data.put("I", gson.toJson(memory.getI()));
        data.put("timestamp", memory.getTimestamp().toString());
    
        return data;
    }


    public static void loadMemory(Memory memory, Map<String, String> memoryDict) {
        memory.setEvaluation(Double.parseDouble(memoryDict.get("evaluation")));
        memory.setId(Long.parseLong(memoryDict.get("id")));

        String infoJSON = memoryDict.get("I");
        Object info = gson.fromJson(infoJSON, Object.class);
        memory.setI(info);
    }

}
