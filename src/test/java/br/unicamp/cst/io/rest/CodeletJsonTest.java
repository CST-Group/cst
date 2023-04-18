package br.unicamp.cst.io.rest;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodeletJsonTest {

    Codelet interCodelet = new Codelet() {
        Memory in;
        Memory out;

        @Override
        public void accessMemoryObjects() {
            if (in == null) {
                this.in = this.getInput("M3");
            }
            if (out == null) {
                this.out = this.getOutput("M4");
            }
        }

        @Override
        public void calculateActivation() {

        }

        @Override
        public void proc() {
            Memory m3 = new MemoryObject();
            m3.setName("M3");

            Memory m4 = new MemoryObject();
            m4.setName("M4");

            interCodelet.addInput(m3);
            interCodelet.addOutput(m4);
            interCodelet.addBroadcast(m4);
            interCodelet.setName("name");

            CodeletJson codeletJson = new CodeletJson(interCodelet, "testGroup");

            assertEquals(m3.getName(), codeletJson.getInputs().get(0).name);
            assertEquals(m4.getName(), codeletJson.getOutputs().get(0).name);
            assertEquals(m4.getName(), codeletJson.getBroadcast().get(0).name);
            assertEquals(codeletJson.getGroup(), "testGroup");
            assertEquals(interCodelet.getName(), codeletJson.getName());
        }
    };

        @Test
    public void testCod(){}
}
