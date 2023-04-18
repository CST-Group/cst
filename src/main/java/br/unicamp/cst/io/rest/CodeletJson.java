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
import java.util.ArrayList;
import java.util.List;

public class CodeletJson {
    private double activation;
    private long timestamp;
    private String name;
    private String group;
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

    public CodeletJson(Codelet cod, String group) {
        this(cod);
        this.group = group;

    }

    public List<MemoryJson> getInputs() {
        return inputs;
    }

    public List<MemoryJson> getOutputs() {
        return outputs;
    }

    public String getGroup() {
        return group;
    }
}
