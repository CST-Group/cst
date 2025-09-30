/***********************************************************************************************
 * Copyright (c) 2012-2023  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors for this file:
 * R. R. Gudwin 
 ***********************************************************************************************/
package br.unicamp.cst.representation.idea;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rgudwin
 */
public class HabitExecutionerCodelet extends Codelet {
    Habit h;
    Idea root;

    public HabitExecutionerCodelet() {
        this.name = "Default";
    }

    public HabitExecutionerCodelet(String name) {
        this.name = name;
    }
    
    @Override
    public void accessMemoryObjects() {
        root = new Idea("root", "");
        for (Memory m : this.inputs) {
            Object o = m.getI();
            if (o instanceof Idea) {
                Idea id = (Idea)o;
                Object value = id.getValue();
                if (m.getName().equalsIgnoreCase(this.getName()+"Habits") && value instanceof Habit) {
                    h = (Habit) value;
                }    
                else {
                    root.add((Idea)o);
                }
            }
        }
        if (root.isLeaf() && this.inputs.size() > 1) {
            Logger.getAnonymousLogger().log(Level.INFO, "I was not able to find any valid Idea at inputs");
        }
        if (h == null) {
            Logger.getAnonymousLogger().log(Level.INFO, "I found no habit to execute");
        }
    }

    @Override
    public void calculateActivation() {
        /* We are not using activation in this codelet */
    }

    @Override
    public void proc() {
        if (h == null) return;

        // Get the ideas from the habit execution
        Idea outputRoot = h.exec(root);
        if (outputRoot == null) return;

        // Set the activation of the output root idea in this codelet
        double outputRootActivation = getActivationValue(outputRoot);
        try{setActivation(outputRootActivation);}catch(Exception e){};

        // Create a map of output memories for quick access
        Map<String, Memory> outputsMap = new HashMap<>();
        for (Memory mem : outputs) outputsMap.put(mem.getName(), mem);

        // Iterate through the output ideas and update corresponding memories
        for (Idea outputIdea : outputRoot.getL()) { 
            Memory m = outputsMap.get(outputIdea.getName());
            if (m == null) continue; // Skip to the next idea if no match is found

            if (m instanceof MemoryContainer) {
                MemoryContainer mc = (MemoryContainer) m;
                mc.setI(outputIdea, getActivationValue(outputIdea), this.getName());
            }
            else {
                m.setI(outputIdea);
            }
        }
    }

    private double getActivationValue(Idea idea) {
        double act = 0.0d;
        Idea actIdea = idea.get("activation");
        if (actIdea != null && actIdea.getValue() instanceof Double) {
            act = (double) actIdea.getValue();
        }
        return act;
    }
}
