package br.unicamp.cst.io.rest;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;

import java.util.ArrayList;
import java.util.List;

public class MindJson {
    private List<MemoryJson> memories = new ArrayList<MemoryJson>();
    private List<CodeletJson> codelets = new ArrayList<CodeletJson>();

    public MindJson(List<Memory> memories, List<Codelet> cods) {
        for (int i = 0; i < memories.size(); i++) {
            this.memories.add(new MemoryJson(memories.get(i)));
        }
        for (int i = 0; i < cods.size(); i++) {
            this.codelets.add(new CodeletJson(cods.get(i)));
        }
    }
}
