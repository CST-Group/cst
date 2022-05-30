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

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.Mind;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MindJson {
    public List<MemoryJson> memories = new ArrayList<MemoryJson>();
    public List<CodeletJson> codelets = new ArrayList<CodeletJson>();

    public MindJson(Mind m) {
        if (m.getMemoryGroupsNumber() == 0) {
            List<Memory> mems = m.getRawMemory().getAllMemoryObjects();
            
            for (int i = 0; i < mems.size(); i++) {
                this.memories.add(new MemoryJson(mems.get(i)));
            }
        }    
        else {
            ConcurrentHashMap<String, ArrayList> mems = m.getMemoryGroups();
            
            for (String key : mems.keySet()) {
                ArrayList<Memory> memoList = mems.get(key);
                for (int i = 0; i < memoList.size(); i++) {
                    this.memories.add(new MemoryJson(memoList.get(i), key));
                }
            }
        }
        if (m.getCodeletGroupsNumber() == 0) {
            List<Codelet> cods = m.getCodeRack().getAllCodelets();        
            for (int i = 0; i < cods.size(); i++) {
                this.codelets.add(new CodeletJson(cods.get(i)));
            }
        }
        else {
            ConcurrentHashMap<String, java.util.ArrayList> cods = m.getCodeletGroups();
            for (String key : cods.keySet()) {
                ArrayList<Codelet> codList = cods.get(key);
                for (int i = 0; i < codList.size(); i++) {
                    this.codelets.add(new CodeletJson(codList.get(i), key));
                }
            }    
        }
    }
}
