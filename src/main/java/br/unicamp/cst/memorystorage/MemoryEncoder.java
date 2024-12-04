package br.unicamp.cst.memorystorage;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;

import br.unicamp.cst.core.entities.Memory;

public class MemoryEncoder {
    static Gson gson = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();

    private MemoryEncoder() {

    }

    public static Map<String, String> toDict(Memory memory) {
        Map<String, String> data = new HashMap<>();
        
        data.put("name", memory.getName());
        data.put("evaluation", memory.getEvaluation().toString());
        data.put("id", memory.getId().toString());
        data.put("timestamp", memory.getTimestamp().toString());        

        Object info = memory.getI();
        if(String.class.isInstance(info))
        {
            data.put("I", (String) info);
        }
        else
        {
            data.put("I", gson.toJson(info));
        }
    
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
