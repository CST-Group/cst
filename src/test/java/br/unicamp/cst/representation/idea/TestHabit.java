/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.unicamp.cst.representation.idea;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author rgudwin
 */
public class TestHabit {
    
    Idea incrementIdea;
    Idea decrementIdea;
    
    public TestHabit() {
        Habit increment = new Habit() { 
        @Override 
        public List<Idea> op(Idea idea) { 
             //Check if belongs to category return membershipDegree;
             if (idea.getValue() instanceof Integer) {
                  int number = (int) idea.getValue();
                  Idea modifiedIdea = new Idea("incremented",number+1);
                  List<Idea> li = new ArrayList<Idea>();
                  li.add(modifiedIdea);
                  return(li);
             }    
             return(null);
        }
        };
        // Creating a habit of incrementing integer numbers
        incrementIdea = new Idea("evenIdea",increment,"Property",2);
        // Creating a category for odd numbers
        Habit decrement = new Habit() { 
        @Override 
        public List<Idea> op(Idea idea) { 
             //Check if belongs to category return membershipDegree;
             if (idea.getValue() instanceof Integer) {
                  int number = (int) idea.getValue();
                  Idea modifiedIdea = new Idea("incremented",number-1);
                  List<Idea> li = new ArrayList<Idea>();
                  li.add(modifiedIdea);
                  return(li);
             }    
             return(null);
        }
        };
        // Creating a habit of decrementing integer numbers
        decrementIdea = new Idea("evenIdea",decrement,"Property",2);
    }
    
    @Test 
    public void testHabitIdeas() {
        // Creating a category for even numbers
        TestHabit tc = new TestHabit();
        Habit increment = (Habit) tc.incrementIdea.getValue();
        Habit decrement = (Habit) tc.decrementIdea.getValue();
        System.out.println("Creating and testing increment habits ...");
        for (int i=0;i<100;i++) {
            int rnumber = new Random().nextInt();
            Idea newnumber = new Idea("number",rnumber);
            Idea modnumber = increment.op(newnumber).get(0);
            System.out.print(" "+newnumber.getValue()+"->"+modnumber.getValue());
            int tt = (int) newnumber.getValue();
            assertEquals(modnumber.getValue(),tt+1);
        }
        System.out.println("\nCreating and testing decrement ...");
        for (int i=0;i<100;i++) {
            Idea newnumber = new Idea("number",new Random().nextInt());
            Idea modnumber = decrement.op(newnumber).get(0);
            System.out.print(" "+newnumber.getValue()+"->"+modnumber.getValue());
            int tt = (int) newnumber.getValue();
            assertEquals(modnumber.getValue(),tt-1);
        }
        System.out.println("\nfinished !");
    }
    
}
