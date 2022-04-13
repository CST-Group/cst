package br.unicamp.cst.io.rest;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;

import java.util.ArrayList;
import java.util.List;

public class MemoryJson {
    public Long timestamp;
    public volatile Double evaluation;
    public volatile Object I;
    public String name;
    public ArrayList<MemoryJson> memories = new ArrayList<MemoryJson>();

    public MemoryJson(Memory memo) {
        timestamp = memo.getTimestamp();
        evaluation = memo.getEvaluation();
        I = memo.getI();
        name = memo.getName();
        if (memo instanceof MemoryContainer) {
            MemoryContainer memoAux = (MemoryContainer) memo;
            List<Memory> memoList = memoAux.getAllMemories();
            for (int i = 0; i < memoList.size(); i++) {
                this.memories.add(new MemoryJson(memoList.get(i)));
            }
        }

    }
}
