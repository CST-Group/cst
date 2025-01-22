
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.unicamp.cst.behavior.bn;

import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.memory.WorkingStorage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author karenlima
 */
public class TestBehaviorNetwork {
    
    GlobalVariables gv;
    WorkingStorage ws;
    Mind mind;
    BehaviorNetwork bn;
    
    MockBehaviorForBN exploreCompetence;
    MockBehaviorForBN eatCompetence;
    MockBehaviorForBN forageFoodCompetence;
    
    @BeforeEach 
    public void setUp() {
        mind = new Mind();
        gv =  new GlobalVariables();
        ws = null;
        bn = new BehaviorNetwork(mind.getCodeRack(), ws);
        
        exploreCompetence = new MockBehaviorForBN(ws, gv);
        eatCompetence = new MockBehaviorForBN(ws, gv);
        forageFoodCompetence = new MockBehaviorForBN(ws, gv);
    }
    
    @Test
    public void testStartCodeletsBehaviorNetwork() {
        mind.insertCodelet(exploreCompetence);
        mind.insertCodelet(eatCompetence);
        mind.insertCodelet(forageFoodCompetence);

        bn.addCodelet(eatCompetence);
        bn.addCodelet(exploreCompetence);
        bn.addCodelet(forageFoodCompetence);
        
        bn.startCodelets();

        assertEquals(true, eatCompetence.hasStart);
        assertEquals(true, exploreCompetence.hasStart);
        assertEquals(true, forageFoodCompetence.hasStart);
    }
    
    @Test
    public void testStartCodeletSingleThreadBHCodeletBehaviorNetwork() {
        mind.insertCodelet(exploreCompetence);
        mind.insertCodelet(eatCompetence);
        mind.insertCodelet(forageFoodCompetence);

        bn.addCodelet(eatCompetence);
        bn.addCodelet(exploreCompetence);
        bn.addCodelet(forageFoodCompetence);
        
        bn.setSingleCodeletBN(true);
        bn.startCodelets();
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestBehaviorNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }

        assertEquals(true, eatCompetence.procExecuted);
        assertEquals(true, exploreCompetence.procExecuted);
        assertEquals(true, forageFoodCompetence.procExecuted);
        assertEquals(true, bn.isSingleCodeletBN());
    }
    
    @Test
    public void testStopCodeletsBehaviorNetwork() {
        mind.insertCodelet(exploreCompetence);
        mind.insertCodelet(eatCompetence);
        mind.insertCodelet(forageFoodCompetence);

        bn.addCodelet(eatCompetence);
        bn.addCodelet(exploreCompetence);
        bn.addCodelet(forageFoodCompetence);
        
        bn.startCodelets();
        bn.stopCompetences();

        assertEquals(false, eatCompetence.hasStart);
        assertEquals(false, exploreCompetence.hasStart);
        assertEquals(false, forageFoodCompetence.hasStart);
    }
    
    @Test
    public void testBehaviorNetworkSize() {
        
        mind.insertCodelet(exploreCompetence);
        mind.insertCodelet(eatCompetence);
        mind.insertCodelet(forageFoodCompetence);

        bn.addCodelet(eatCompetence);
        bn.addCodelet(exploreCompetence);
        bn.addCodelet(forageFoodCompetence);
        
        ArrayList<Behavior> listOfAllBehaviors=new ArrayList<>();
        listOfAllBehaviors.add(eatCompetence);
        listOfAllBehaviors.add(exploreCompetence);
        listOfAllBehaviors.add(forageFoodCompetence);

        assertEquals(listOfAllBehaviors, bn.getBehaviors());
        assertEquals(4, mind.getCodeRack().getAllCodelets().size());
    }
    
    @Test
    public void testRemoveCodeletBehaviorNetwork() {
        mind.insertCodelet(exploreCompetence);
        mind.insertCodelet(eatCompetence);
        mind.insertCodelet(forageFoodCompetence);

        bn.addCodelet(eatCompetence);
        bn.addCodelet(exploreCompetence);
        bn.addCodelet(forageFoodCompetence);
        
        bn.removeCodelet(forageFoodCompetence);
        
        ArrayList<Behavior> listOfAllBehaviors=new ArrayList<>();
        listOfAllBehaviors.add(eatCompetence);
        listOfAllBehaviors.add(exploreCompetence);

        assertEquals(listOfAllBehaviors, bn.getBehaviors());
        assertEquals(4, mind.getCodeRack().getAllCodelets().size());
    }
    
    @Test
    public void testSetBehaviorsToZeroWhenActivated() {
        mind.insertCodelet(exploreCompetence);
        mind.insertCodelet(eatCompetence);
        mind.insertCodelet(forageFoodCompetence);

        bn.addCodelet(eatCompetence);
        bn.addCodelet(exploreCompetence);
        bn.addCodelet(forageFoodCompetence);
        
        bn.setBehaviorsToZeroWhenActivated(false);
        assertEquals(false, eatCompetence.isSetToZeroWhenActivated());
        assertEquals(false, exploreCompetence.isSetToZeroWhenActivated());
        assertEquals(false, forageFoodCompetence.isSetToZeroWhenActivated());
        
    }
    
    @Test
    public void testSetCoalitionBehaviorNetwork() {
        mind.insertCodelet(exploreCompetence);
        mind.insertCodelet(eatCompetence);
        mind.insertCodelet(forageFoodCompetence);

        bn.addCodelet(eatCompetence);
        bn.addCodelet(exploreCompetence);
        bn.addCodelet(forageFoodCompetence);
        
        ArrayList<Behavior> listOfAllBehaviors=new ArrayList<>();
        listOfAllBehaviors.add(eatCompetence);
        listOfAllBehaviors.add(exploreCompetence);
        listOfAllBehaviors.add(forageFoodCompetence);
        
        bn.setCoalition(listOfAllBehaviors);

        assertEquals(listOfAllBehaviors, bn.getCoalition());
        assertEquals(listOfAllBehaviors, eatCompetence.getCoalition());
        assertEquals(listOfAllBehaviors, exploreCompetence.getCoalition());
        assertEquals(listOfAllBehaviors, forageFoodCompetence.getCoalition());
    }
}

class MockBehaviorForBN extends Behavior {
    
    public Boolean hasStart = false;
    public Boolean procExecuted = false;
    
    public MockBehaviorForBN(WorkingStorage ws,GlobalVariables globalVariables) {
        super(ws, globalVariables);
    }
    @Override
    public void proc() {
        procExecuted = true;
    }
    
    @Override
    public void operation() {
    }
    
    @Override
    public void calculateActivation() {
    }

    @Override
    public void accessMemoryObjects() {
    }
    
    @Override
    public void start() {
        hasStart = true;
    }
    
    @Override
    public void stop() {
        hasStart = false;
    }
}
