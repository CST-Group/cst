/***********************************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * K. Raizer, A. L. O. Paraense, E. M. Froes, R. R. Gudwin - initial API and implementation
 ***********************************************************************************************/
package br.unicamp.cst.representation.idea;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
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
        public Idea exec(Idea idea) { 
             //Check if belongs to category return membershipDegree;
             if (idea.getValue() instanceof Integer) {
                  int number = (int) idea.getValue();
                  Idea modifiedIdea = new Idea("incremented",number+1);
                  return(modifiedIdea);
             }    
             return(null);
        }
        };
        // Creating a habit of incrementing integer numbers
        incrementIdea = new Idea("evenIdea",increment,"Property",2);
        // Creating a category for odd numbers
        Habit decrement = new Habit() { 
        @Override 
        public Idea exec(Idea idea) { 
             //Check if belongs to category return membershipDegree;
             if (idea.getValue() instanceof Integer) {
                  int number = (int) idea.getValue();
                  Idea modifiedIdea = new Idea("incremented",number-1);
                  return(modifiedIdea);
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
            Idea modnumber = increment.exec(newnumber);
            System.out.print(" "+newnumber.getValue()+"->"+modnumber.getValue());
            int tt = (int) newnumber.getValue();
            assertEquals(modnumber.getValue(),tt+1);
        }
        System.out.println("\nCreating and testing decrement habits ...");
        for (int i=0;i<100;i++) {
            Idea newnumber = new Idea("number",new Random().nextInt());
            Idea modnumber = decrement.exec(newnumber);
            System.out.print(" "+newnumber.getValue()+"->"+modnumber.getValue());
            int tt = (int) newnumber.getValue();
            assertEquals(modnumber.getValue(),tt-1);
        }
        System.out.println("\nfinished !");
    }
    
    @Test 
    public void testHabitIdeasDirect() {
        // Creating a category for even numbers
        TestHabit tc = new TestHabit();
        System.out.println("Creating and testing increment habits from idea ...");
        for (int i=0;i<100;i++) {
            int rnumber = new Random().nextInt();
            Idea newnumber = new Idea("number",rnumber);
            Idea modnumber = tc.incrementIdea.exec(newnumber);
            System.out.print(" "+newnumber.getValue()+"->"+modnumber.getValue());
            int tt = (int) newnumber.getValue();
            assertEquals(modnumber.getValue(),tt+1);
        }
        System.out.println("\nCreating and testing decrement habits from idea...");
        for (int i=0;i<100;i++) {
            Idea newnumber = new Idea("number",new Random().nextInt());
            Idea modnumber = tc.decrementIdea.exec(newnumber);
            System.out.print(" "+newnumber.getValue()+"->"+modnumber.getValue());
            int tt = (int) newnumber.getValue();
            assertEquals(modnumber.getValue(),tt-1);
        }
        System.out.println("\nfinished !");
    }
    
    @Test 
    public void testHabitIdeasDirectWithOp0() {
        // Creating a category for even numbers
        TestHabit tc = new TestHabit();
        System.out.println("Creating and testing increment habits using mind...");
        for (int i=0;i<100;i++) {
            int rnumber = new Random().nextInt();
            Idea orignumber = new Idea("number",rnumber);
            Idea modnumber = tc.incrementIdea.exec(orignumber);
            System.out.print(" "+orignumber.getValue()+"->"+modnumber.getValue());
            int tt = (int) orignumber.getValue();
            assertEquals(modnumber.getValue(),tt+1);
        }
        System.out.println("\nCreating and testing decrement habits using mind...");
        for (int i=0;i<100;i++) {
            Idea newnumber = new Idea("number",new Random().nextInt());
            Idea modnumber = tc.decrementIdea.exec(newnumber);
            System.out.print(" "+newnumber.getValue()+"->"+modnumber.getValue());
            int tt = (int) newnumber.getValue();
            assertEquals(modnumber.getValue(),tt-1);
        }
        System.out.println("\nfinished !");
    }
    
    private Idea getRandomNumberIdea() {
        int rnumber = new Random().nextInt();
        Idea orignumber = new Idea("number",rnumber);
        return(orignumber);
    }
    
    @Test
    public void testHabitExecutionerCodelet() {
        TestHabit tc = new TestHabit();
        Idea id = new Idea();
        assertEquals(id.isLeaf(),true);
        id.add(new Idea("leaf"));
        assertEquals(id.isLeaf(),false);
        assertEquals(id.isHabit(),false);
        assertEquals(tc.incrementIdea.isHabit(),true);
        assertEquals(tc.decrementIdea.isHabit(),true);
        Mind testMind = new Mind();
        MemoryObject input_number = testMind.createMemoryObject("INPUT_NUMBER");
        MemoryObject input_habit = testMind.createMemoryObject("INPUT_HABIT",tc.incrementIdea);
        MemoryObject output = testMind.createMemoryObject("OUTPUT_NUMBER");
        Codelet c = new Codelet() {
            Idea input_number;
            Idea habit;
            MemoryObject output_mo;
            @Override
            public void accessMemoryObjects() {
                MemoryObject input1 = (MemoryObject) this.getInput("INPUT_NUMBER");
                input_number = (Idea) input1.getI();
                MemoryObject input2 = (MemoryObject) this.getInput("INPUT_HABIT");
                habit = (Idea) input2.getI();
                output_mo = (MemoryObject) this.getOutput("OUTPUT_NUMBER");
            }

            @Override
            public void calculateActivation() {
                // not used
            }

            @Override
            public void proc() {
                Idea result = habit.exec(input_number);
                output_mo.setI(result);
            }
        };
        c.addInput(input_number);
        c.addInput(input_habit);
        c.addOutput(output);
        c.setIsMemoryObserver(true);
        input_number.setI(getRandomNumberIdea());
	input_number.addMemoryObserver(c);
        input_habit.addMemoryObserver(c);
        testMind.start();
        for (int i=0;i<200;i++) {
            Idea orignumber = getRandomNumberIdea();
            int rnumber = (int) orignumber.getValue();
            input_number.setI(orignumber);
            int result = rnumber;
            while (result == rnumber) {
                Idea iresult = (Idea) output.getI();
                result = (int) iresult.getValue();
            }
            assertEquals(result,rnumber+1);
        }
    }
    
}
