package MindViewerTest;

import br.unicamp.cst.core.entities.Codelet;
import pb.Service;

import java.util.ArrayList;
import java.util.List;

public class CodeletObjectServer {
    private double activation;
    private long timestamp;
    private String name;
    private List<MemoryObjectServer> broadcast = new ArrayList<MemoryObjectServer>();
    private List<MemoryObjectServer> inputs = new ArrayList<MemoryObjectServer>();
    private List<MemoryObjectServer> outputs = new ArrayList<MemoryObjectServer>();

    public CodeletObjectServer(Codelet cod) {
        this.activation = cod.getActivation();
        this.timestamp = System.currentTimeMillis();
        this.name = cod.getName();
        for (int i = 0; i < cod.getBroadcast().size(); i++) {
            this.broadcast.add(new MemoryObjectServer(cod.getBroadcast().get(i)));
        }
        for (int i = 0; i < cod.getInputs().size(); i++) {
            this.inputs.add(new MemoryObjectServer(cod.getInputs().get(i)));
        }
        for (int i = 0; i < cod.getOutputs().size(); i++) {
            this.outputs.add(new MemoryObjectServer(cod.getOutputs().get(i)));
        }
    }
    public Service.ICodeletProperties.Builder getICodeletProperties (){
        Service.ICodeletProperties.Builder codelet = Service.ICodeletProperties.newBuilder();
        codelet.setActivation(this.activation);
        codelet.setTimestamp(this.timestamp);
        codelet.setName(this.name);

        for (int i = 0; i < this.broadcast.size();i++){
            codelet.addBroadcast(i, this.broadcast.get(i).getIMemoryProperties());
        }
        for (int i = 0; i < this.inputs.size();i++){
            codelet.addInputs(i, this.inputs.get(i).getIMemoryProperties());
        }
        for (int i = 0; i < this.outputs.size();i++){
            codelet.addInputs(i, this.outputs.get(i).getIMemoryProperties());
        }
        return codelet;
    }
}