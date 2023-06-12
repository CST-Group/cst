/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.unicamp.cst.representation.idea;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import java.util.List;

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
        if (root.getL().size() == 0) {
            System.out.println("I was not able to find any valid Idea at inputs");
        }
        if (h == null) {
            System.out.println("I found no habit to execute");
        }
    }

    @Override
    public void calculateActivation() {
        
    }

    @Override
    public void proc() {
        if (h != null) {
            List<Idea> ois = h.exec(root);
            for (Memory m : outputs)
               m.setI(ois);
        }
    }
}
