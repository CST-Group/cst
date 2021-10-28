package MindViewerTest;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class APIResponseObject {
    private List<MemoryObjectServer> memories;
    private List<CodeletObjectServer> codelets;

    public APIResponseObject(List<Memory> memories, List<Codelet> codelets) {
        this.memories = new ArrayList<>();
        if (!Objects.isNull(memories))
            for (int i = 0; i < memories.size(); i++) {
                this.memories.add(new MemoryObjectServer(memories.get(i)));
            }
        this.codelets = new ArrayList<>();
        if (!Objects.isNull(codelets))
            for (int i = 0; i < codelets.size(); i++) {
                this.codelets.add(new CodeletObjectServer(codelets.get(i)));
            }
    }

    public List<MemoryObjectServer> getMemories(){
        return this.memories;
    }
    public MemoryObjectServer getMemorie(int pos){
        return this.memories.get(pos);
    }
    public List<CodeletObjectServer> getCodelets(){
        return this.codelets;
    }
    public CodeletObjectServer getCodelet(int pos){
        return this.codelets.get(pos);
    }
}
