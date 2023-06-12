/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.unicamp.cst.representation.idea;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author rgudwin
 */
public class HabitExecutionerCodeletTest {
    
    Mind m;
    MemoryContainer mc;
    MemoryObject moi;
    MemoryObject moo;
    
    public HabitExecutionerCodeletTest() {
        m = new Mind();
        mc = m.createMemoryContainer("HabitsMemory");
        Idea sh = new Idea("Summer");
        sh.setValue(summer);
        sh.setScope(2);
        Idea dh = new Idea("Decrementer");
        dh.setValue(decrementer);
        dh.setScope(2);
        mc.setI(sh);
        mc.setI(dh);
        moi = m.createMemoryObject("InputIdeasMemory");
        moo = m.createMemoryObject("OutputIdeasMemory");
        HabitExecutionerCodelet hec = new HabitExecutionerCodelet();
        hec.addInput(mc);
        hec.addInput(moi);
        hec.addOutput(moo);
        hec.setPublishSubscribe(true);
        m.insertCodelet(hec);
        m.start();
    }
    
    Habit summer = new Habit() { 
        @Override 
        public List<Idea> exec(Idea idea) {
             Idea adder = idea.get("value.add");
             int valuetoadd=0;
             if (adder != null && adder.getValue() instanceof Integer) {
                 valuetoadd = (int) adder.getValue();
             }
             if (idea.get("value").getValue() instanceof Integer) {
                  int number = (int) idea.get("value").getValue();
                  Idea modifiedIdea = new Idea("incremented",number+valuetoadd);
                  List<Idea> li = new ArrayList<Idea>();
                  li.add(modifiedIdea);
                  return(li);
             }
             System.out.println("Something wrong happened");
             return(null);
        }
        };
    Habit decrementer = new Habit() { 
        @Override 
        public List<Idea> exec(Idea idea) {
             Idea adder = idea.get("value.add");
             int valuetodec=0;
             if (adder != null && adder.getValue() instanceof Integer) {
                 valuetodec = (int) adder.getValue();
             }
             if (idea.get("value").getValue() instanceof Integer) {
                  int number = (int) idea.get("value").getValue();
                  Idea modifiedIdea = new Idea("decremented",number-valuetodec);
                  List<Idea> li = new ArrayList<Idea>();
                  li.add(modifiedIdea);
                  return(li);
             }
             System.out.println("Something wrong happened");
             return(null);
        }
        };
    
    private void doTest() {
        Object oo;
        Random r = new Random();
        for (int k=0;k<100;k++) {
            int major = r.nextInt(100);
            if (major < 50) {
                mc.setEvaluation(0.7,0);
                mc.setEvaluation(0.3,1);
            }
            else {
                mc.setEvaluation(0.3,0);
                mc.setEvaluation(0.7,1);
            }
            int minor = r.nextInt(10);
            Idea i = new Idea("value",major);
            i.add(new Idea("add",minor));
            moi.setI(i);
            long ti = moi.getTimestamp();
            while(moo.getTimestamp() < ti) System.out.print(".");
            oo = moo.getI();
            if (oo != null) {
                List<Idea> ooi = (List<Idea>) oo;
                for (Idea ii : ooi) {
                   int sum = (int) ii.getValue();
                   Idea ih = (Idea) mc.getLastI();
                   int op;
                   String ops;
                   if (ih.getName().equals("Summer")) {
                       op = 0;
                       ops = "+";
                   }
                   else {
                       op = 1;
                       ops = "-";
                   }
                   System.out.println(ih+" "+major+ops+minor+"="+sum);
                   if (op == 0)
                      assertEquals(sum,major+minor);
                   else 
                      assertEquals(sum,major-minor); 
                }   
            }
            else fail("The output memory object is null");
        } 
    }
    
    @Test 
    public void testHabitExecutionerCodeletMAX() {
        HabitExecutionerCodeletTest test = new HabitExecutionerCodeletTest();
        mc.setPolicy(MemoryContainer.Policy.MAX);
        System.out.println("\nTesting the MAX Policy - Sums for < 50 Decs for > 50");
        doTest();
    }

    @Test 
    public void testHabitExecutionerCodeletMIN() {
        HabitExecutionerCodeletTest test = new HabitExecutionerCodeletTest();
        mc.setPolicy(MemoryContainer.Policy.MIN);
        System.out.println("\nTesting the MIN Policy - Sums for > 50 Decs for < 50");
        doTest();
    }
    
    @Test 
    public void testHabitExecutionerCodeletIterate() {
        HabitExecutionerCodeletTest test = new HabitExecutionerCodeletTest();
        mc.setPolicy(MemoryContainer.Policy.ITERATE);
        System.out.println("\nTesting the ITERATE Policy - Iterate Sums and Decs");
        doTest(); 
    }
    
    @Test 
    public void testHabitExecutionerCodeletRandom() {
        HabitExecutionerCodeletTest test = new HabitExecutionerCodeletTest();
        mc.setPolicy(MemoryContainer.Policy.RANDOM_FLAT);
        System.out.println("\nTesting the RANDOM_FLAT Policy - Sums and Decs at Random");
        doTest(); 
    }
    
}
