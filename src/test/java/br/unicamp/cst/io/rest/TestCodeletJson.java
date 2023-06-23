package br.unicamp.cst.io.rest;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCodeletJson {

    Codelet interCodelet = new Codelet() {
        Memory in;
        Memory out;
        Memory broad;

        @Override
        public void accessMemoryObjects() {
            if (in == null) {
                this.in = this.getInput("M3");
            }
            if (out == null) {
                this.out = this.getOutput("M4");
            }
            if(broad == null){
                this.broad = this.getBroadcast("M4");
            }
        }

        @Override
        public void calculateActivation() {

        }

        @Override
        public void proc() {

        }
    };

    @Test
    public void testCod(){
        Memory m3 = new MemoryObject();
        m3.setName("M3");

        Memory m4 = new MemoryObject();
        m4.setName("M4");

        interCodelet.addInput(m3);
        interCodelet.addOutput(m4);
        interCodelet.addBroadcast(m4);
        interCodelet.setName("name");

        CodeletJson codeletJson = new CodeletJson(interCodelet, "testGroup");

        List<MemoryJson> inputs = codeletJson.getInputs();
        List<MemoryJson> outputs = codeletJson.getOutputs();
        List<MemoryJson> broadcast = codeletJson.getBroadcast();
        String group = codeletJson.getGroup();
        String name = codeletJson.getName();

        assertEquals(m3.getName(), inputs.get(0).name);
        assertEquals(m4.getName(), outputs.get(0).name);
        assertEquals(m4.getName(), broadcast.get(0).name);
        assertEquals(group, "testGroup");
        assertEquals(interCodelet.getName(), name);
    }
}
