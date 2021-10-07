package br.unicamp.cst.io.rest;

import br.unicamp.cst.core.entities.Codelet;

import java.util.ArrayList;
import java.util.List;

public class CodeletJson {
    private double activation;
    private long timestamp;
    private String name;
    private List<MemoryJson> broadcast = new ArrayList<MemoryJson>();
    private List<MemoryJson> inputs = new ArrayList<MemoryJson>();
    private List<MemoryJson> outputs = new ArrayList<MemoryJson>();

    public CodeletJson(Codelet cod) {
        this.activation = cod.getActivation();
        this.timestamp = System.currentTimeMillis();
        this.name = cod.getName();
        for (int i = 0; i < cod.getBroadcast().size(); i++) {
            this.broadcast.add(new MemoryJson(cod.getBroadcast().get(i)));
        }
        for (int i = 0; i < cod.getInputs().size(); i++) {
            this.inputs.add(new MemoryJson(cod.getInputs().get(i)));
        }
        for (int i = 0; i < cod.getOutputs().size(); i++) {
            this.outputs.add(new MemoryJson(cod.getOutputs().get(i)));
        }
    }
}
