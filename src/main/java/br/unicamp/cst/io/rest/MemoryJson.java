/***********************************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * K. Raizer, A. L. O. Paraense, E. M. Froes, R. R. Gudwin - initial API and implementation
 * **********************************************************************************************/
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
    public String group;
    public ArrayList<MemoryJson> memories; // = new ArrayList<MemoryJson>();
    public Long id;

    public MemoryJson(Memory memo) {
        timestamp = memo.getTimestamp();
        evaluation = memo.getEvaluation();
        I = memo.getI();
        name = memo.getName();
        id = memo.getId();
        if (memo instanceof MemoryContainer) {
            memories = new ArrayList<>();
            MemoryContainer memoAux = (MemoryContainer) memo;
            List<Memory> memoList = memoAux.getAllMemories();
            for (int i = 0; i < memoList.size(); i++) {
                this.memories.add(new MemoryJson(memoList.get(i)));
            }
        }

    }

    public MemoryJson(Memory memo, String group) {
        timestamp = memo.getTimestamp();
        evaluation = memo.getEvaluation();
        I = memo.getI();
        name = memo.getName();
        this.group = group;
        if (memo instanceof MemoryContainer) {
            memories = new ArrayList<>();
            MemoryContainer memoAux = (MemoryContainer) memo;
            List<Memory> memoList = memoAux.getAllMemories();
            for (int i = 0; i < memoList.size(); i++) {
                this.memories.add(new MemoryJson(memoList.get(i)));
            }
        }

    }
}

