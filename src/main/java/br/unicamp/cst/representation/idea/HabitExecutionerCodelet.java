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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rgudwin
 */
public class HabitExecutionerCodelet extends Codelet {

    Habit h;
    Idea root;
    
    @Override
    public void accessMemoryObjects() {
        root = new Idea("root");
        for (Memory m : this.inputs) {
            Object o = m.getI();
            if (o instanceof Idea) {
                Idea id = (Idea)o;
                Object value = id.getValue();
                if (value instanceof Habit) {
                    h = (Habit) value;
                }    
                else {
                    root.add((Idea)o);
                }
            }
        }
        if (root.isLeaf()) {
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
        if (h != null) {
            Idea ois = h.exec(root);
            for (Memory m : outputs)
               m.setI(ois);
        }
    }
}
