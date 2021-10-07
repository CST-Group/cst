package br.unicamp.cst.io.rest;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;

import java.util.ArrayList;
import java.util.List;

public class MemoryJson {
    private Long timestamp;
    private volatile Double evaluation;
    private volatile Object I;
    private String name;
    private ArrayList<MemoryJson> memories = new ArrayList<MemoryJson>();

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
