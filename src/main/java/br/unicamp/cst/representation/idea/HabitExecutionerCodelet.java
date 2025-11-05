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
 * This codelet executes a habit found in its input memories.
 * 
 * It looks for a habit in a memory whose name is the
 * same as the name of the codelet plus "Habits".
 * 
 * It also looks for ideas in the other input memories and adds them
 * to a root idea, which is passed as a parameter to the habit execution.
 * 
 * If there is another habit in the input memories without the "Habits"
 * suffix, it is added to the root idea as well, but not executed.
 * 
 * The habit returns a root idea, which may contain several ideas inside it.
 * The codelet looks for ideas inside the root idea whose names match (case insensitive)
 * the names of its output memories, and sets those ideas to the respective memories.
 * 
 * If the output memory is a MemoryContainer, it sets the MemoryObjects inside it
 * with the name of the Habit being executed by the HabitExecutionerCodelet itslef.
 * 
 * @author rgudwin
 */
public class HabitExecutionerCodelet extends Codelet {
    Habit h;
    Idea root;
    String habitName;

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
                    habitName = id.getName();
                }    
                else {
                    root.add((Idea)o);
                }
            }
        }
        if (root.isLeaf() && this.inputs.size() > 1) {
            Logger.getAnonymousLogger().log(Level.FINE, "I was not able to find any valid Idea at inputs");
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

        Idea outputRoot = h.exec(root);
        if (outputRoot == null) return;

        try{this.setActivation(getActivationValue(outputRoot));}catch(Exception e){};

        setTimeStepValue(outputRoot);

        setPublishSubscribeValue(outputRoot);

        setIdeasToOutputMemories(outputRoot);
    }

    private double getActivationValue(Idea idea) {
        double act = 0.0d;
        Idea actIdea = idea.get("activation");
        if (actIdea != null && actIdea.getValue() instanceof Double) {
            act = (double) actIdea.getValue();
        }
        return act;
    }

    private void setTimeStepValue(Idea idea) {
        Idea timeStepIdea = idea.get("timeStep");
        if (timeStepIdea != null && timeStepIdea.getValue() instanceof Long) {
            long timeStep = (long) timeStepIdea.getValue();
            try{this.setTimeStep(timeStep);}catch(Exception e){};
        }
    }

    private void setPublishSubscribeValue(Idea idea) {
        Idea publishSubscribeIdea = idea.get("publishSubscribe");
        if (publishSubscribeIdea != null && publishSubscribeIdea.getValue() instanceof Boolean) {
            boolean publishSubscribe = (boolean) publishSubscribeIdea.getValue();
            try{this.setPublishSubscribe(publishSubscribe);}catch(Exception e){};
        }
    }

    /**
	 * Gets the ideas from the outputRoot and sets them to the output memories
     * that match by name (case insensitive).
     * 
     * If the memory is a MemoryContainer, it sets the MemoryObjects inside it
     * with the name of the Habit being executed by the HabitExecutionerCodelet itslef.
     * It does so to differentiate between different Habits writing to the same MemoryContainer.
	 * 
	 * @param outputRoot
	 *            the root idea that comes from the habit execution
	 */
    private void setIdeasToOutputMemories(Idea outputRoot) {
        Map<String, Memory> outputsMap = new HashMap<>();
        for (Memory mem : this.outputs) outputsMap.put(mem.getName().toLowerCase(), mem);
        
        for (Idea outputIdea : outputRoot.getL()) { 
            Memory m = outputsMap.get(outputIdea.getName().toLowerCase());
            if (m == null) continue; // Skip to the next idea if no match is found

            if (m instanceof MemoryContainer) {
                MemoryContainer mc = (MemoryContainer) m;
                mc.setI(outputIdea, getActivationValue(outputIdea), habitName);
            }
            else {
                m.setI(outputIdea);
            }
        }
    }
}