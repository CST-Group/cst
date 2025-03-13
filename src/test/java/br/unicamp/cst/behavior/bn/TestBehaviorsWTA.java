/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.unicamp.cst.behavior.bn;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.RawMemory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.cst.memory.WorkingStorage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author karenlima
 */
public class TestBehaviorsWTA {
    GlobalVariables gv;
    WorkingStorage ws;
    MockBehaviorWTA competence1;
    MockBehaviorWTA competence2;
    MockBehaviorWTA competence3;
    BehaviorsWTA kWTA;
    
    MockBehavior exploreCompetence;
    MockBehavior eatCompetence;
    MockBehavior forageFoodCompetence;
    
    String stateHungry = "hungry";
    String stateNotHungry = "NOT_hungry";
    String stateFoundFood = "foundFood";
    String stateNotFoundFood = "NOT_foundFood";
    String stateExploring = "exploring";
    String stateNotExploring = "NOT_exploring";
    
    Memory stateHungryMO = createMemoryObject("stateHungryMO", stateHungry);
    Memory stateNotHungryMO = createMemoryObject("stateNOTHungryMO", stateNotHungry);
    Memory stateFoundFoodMO = createMemoryObject("stateFoundFoodMO", stateFoundFood);
    Memory stateExploringMO = createMemoryObject("stateExploringMO", stateExploring);
    
    Memory worldStateHungryMO;
    Memory worldStateFoundFoodMO;
    Memory worldStateExploringMO;
    
    @BeforeEach 
    public void setUp() {
        gv =  new GlobalVariables();
        ws = new WorkingStorage(100, new RawMemory());
        kWTA = new BehaviorsWTA(gv);
        
        competence1 = new MockBehaviorWTA(ws, gv);
        competence2 = new MockBehaviorWTA(ws, gv);
        competence3 = new MockBehaviorWTA(ws, gv);
        competence1.setExecutable(true);
        competence2.setExecutable(true);
        competence3.setExecutable(true);
        
        exploreCompetence = new MockBehavior(ws, gv);
        eatCompetence = new MockBehavior(ws, gv);
        forageFoodCompetence = new MockBehavior(ws, gv);
    }
    
    
    
    @Test
    public void testChosenBehaviorWithMaxActivation() {
        addMockWTABehavior();
        try {
            competence1.setActivation(0.5);
            competence2.setActivation(1.0);
            competence3.setActivation(0.3);

            kWTA.proc();
            assertEquals(competence2, kWTA.getChosenBehavior());
            assertTrue(competence2.isActive());
            assertFalse(competence1.isActive());
            assertFalse(competence3.isActive());
        } catch (CodeletActivationBoundsException ex) {
            Logger.getLogger(TestBehaviorsWTA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testChosenBehaviorWithoutMaxActivation() {
        addMockWTABehavior();
        try {
            competence1.setActivation(0.5);
            competence2.setActivation(0.8);
            competence3.setActivation(0.3);
            while(kWTA.getChosenBehavior() == null) {
                kWTA.proc();
            }
            assertEquals(competence2, kWTA.getChosenBehavior());
            assertTrue(competence2.isActive());
            assertFalse(competence1.isActive());
            assertFalse(competence3.isActive());
        } catch (CodeletActivationBoundsException ex) {
            Logger.getLogger(TestBehaviorsWTA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testChosenBehaviorWithoutMaxActivationAndInactiveCompetences() {
        addMockWTABehavior();
        try {
            competence1.setActivation(0.5);
            competence2.setActivation(0.8);
            competence2.setExecutable(false);
            competence3.setActivation(0.3);
            while(kWTA.getChosenBehavior() == null) {
                kWTA.proc();
            }
            assertEquals(competence1, kWTA.getChosenBehavior());
            assertTrue(competence1.isActive());
            assertFalse(competence2.isActive());
            assertFalse(competence3.isActive());
        } catch (CodeletActivationBoundsException ex) {
            Logger.getLogger(TestBehaviorsWTA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testChosenBehaviorExploreSelected() {
        defineWorldStateAndGoals(stateNotHungry, stateNotFoundFood, stateNotExploring);
        defineCompetenceLists();
        addAllBehaviors();
        
        kWTA.addBehavior(eatCompetence);
        kWTA.addBehavior(forageFoodCompetence);
        kWTA.addBehavior(exploreCompetence);
        eatCompetence.proc();
        forageFoodCompetence.proc();
        exploreCompetence.proc();
        while(kWTA.getChosenBehavior() == null) {
            kWTA.proc();
        }
        assertEquals(exploreCompetence, kWTA.getChosenBehavior());
        assertTrue(exploreCompetence.isActive());
        assertFalse(eatCompetence.isActive());
        assertFalse(forageFoodCompetence.isActive());
    }
    
    @Test
    public void testChosenBehaviorEatSelected() {
        defineWorldStateAndGoals(stateHungry, stateFoundFood, stateExploring);
        defineCompetenceLists();
        addAllBehaviors();
        
        kWTA.addBehavior(eatCompetence);
        kWTA.addBehavior(forageFoodCompetence);
        kWTA.addBehavior(exploreCompetence);
        eatCompetence.proc();
        forageFoodCompetence.proc();
        exploreCompetence.proc();
        while(kWTA.getChosenBehavior() == null) {
            kWTA.proc();
        }
        assertEquals(eatCompetence, kWTA.getChosenBehavior());
        assertTrue(eatCompetence.isActive());
        assertFalse(exploreCompetence.isActive());
        assertFalse(forageFoodCompetence.isActive());
    }
    
    @Test
    public void testChosenBehaviorForageFoodSelected() {
        defineWorldStateAndGoals(stateHungry, stateNotFoundFood, stateExploring);
        defineCompetenceLists();
        addAllBehaviors();
        
        kWTA.addBehavior(eatCompetence);
        kWTA.addBehavior(forageFoodCompetence);
        kWTA.addBehavior(exploreCompetence);
        eatCompetence.proc();
        forageFoodCompetence.proc();
        exploreCompetence.proc();
        while(kWTA.getChosenBehavior() == null) {
            kWTA.proc();
        }
        assertEquals(forageFoodCompetence, kWTA.getChosenBehavior());
        assertTrue(forageFoodCompetence.isActive());
        assertFalse(exploreCompetence.isActive());
        assertFalse(eatCompetence.isActive());
    }
    
     @Test
    public void testChosenBehaviorNotNull() {
        kWTA.addBehavior(eatCompetence);
        kWTA.addBehavior(forageFoodCompetence);
        kWTA.addBehavior(exploreCompetence);
        try {
            worldStateHungryMO = createMemoryObject("WORLD_STATE", "anything");
            forageFoodCompetence.addInput(worldStateHungryMO);
            
            eatCompetence.setExecutable(true);
            forageFoodCompetence.setExecutable(true);
            exploreCompetence.setExecutable(true);
        
            eatCompetence.setActivation(0.5);
            forageFoodCompetence.setActivation(1.0);
            exploreCompetence.setActivation(0.3);

            kWTA.proc();
            
            assertEquals(forageFoodCompetence, kWTA.getChosenBehavior());
            assertTrue(forageFoodCompetence.isActive());
            assertFalse(eatCompetence.isActive());
            assertFalse(exploreCompetence.isActive());
            
            worldStateHungryMO.setI("other thing");
            forageFoodCompetence.proc();
            
            assertEquals(true, kWTA.getChosenBehavior().changedWorldBeliefState());
            kWTA.proc();
            
            assertEquals(null, kWTA.getChosenBehavior());
        } catch (CodeletActivationBoundsException ex) {
            Logger.getLogger(TestBehaviorsWTA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addMockWTABehavior() {
        kWTA.addBehavior(competence1);
        kWTA.addBehavior(competence2);
        kWTA.addBehavior(competence3);
    }
    
    public MemoryObject createMemoryObject(String name, Object info) {
            MemoryObject mo = new MemoryObject();
            mo.setI(info);
            mo.setTimestamp(System.currentTimeMillis());
            mo.setEvaluation(0.0d);
            mo.setName(name);

            return mo;
    }
    
    public void addAllBehaviors() {
        ArrayList<Behavior> listOfAllBehaviors=new ArrayList<Behavior>();
        listOfAllBehaviors.add(exploreCompetence);
        listOfAllBehaviors.add(eatCompetence);
        listOfAllBehaviors.add(forageFoodCompetence);
        
        exploreCompetence.setBehaviors(listOfAllBehaviors);
        eatCompetence.setBehaviors(listOfAllBehaviors);
        forageFoodCompetence.setBehaviors(listOfAllBehaviors);
    }
    
    public void defineCompetenceLists() {
        
        //Define HARD-PRE-CONDITIONS
        ArrayList<Memory> preconditionsListExplore = new ArrayList<>();
        preconditionsListExplore.add(stateNotHungryMO);
        exploreCompetence.setListOfPreconditions(preconditionsListExplore);
        
        //Define ADD-LIST
        ArrayList<Memory> addListExplore = new ArrayList<>();
        addListExplore.add(stateExploringMO);
        addListExplore.add(stateHungryMO);
        exploreCompetence.setAddList(addListExplore);

        //Define DELETE-LIST
        ArrayList<Memory> deleteListExplore= new ArrayList<>();
        deleteListExplore.add(stateNotHungryMO);
        exploreCompetence.setDeleteList(deleteListExplore);
        
        
        //Define HARD-PRE-CONDITIONS
        ArrayList<Memory> preconditionsListForage = new ArrayList<>();
        preconditionsListForage.add(stateHungryMO);
        forageFoodCompetence.setListOfPreconditions(preconditionsListForage);

        //Define ADD-LIST
        ArrayList<Memory> addListForage = new ArrayList<>();
        addListForage.add(stateFoundFoodMO);
        forageFoodCompetence.setAddList(addListForage);

        //Define DELETE-LIST
        ArrayList<Memory> deleteListForage = new ArrayList<>();
        deleteListForage.add(stateExploringMO);
        forageFoodCompetence.setDeleteList(deleteListForage);
        
        //Define HARD-PRE-CONDITIONS
        ArrayList<Memory> preconditionsListEat = new ArrayList<>();
        preconditionsListEat.add(stateHungryMO);
        preconditionsListEat.add(stateFoundFoodMO);
        eatCompetence.setListOfPreconditions(preconditionsListEat);

        //Define SOFT-PRE-CONDITIONS
        ArrayList<Memory> softPreconditionsListEat = new ArrayList<>();
        eatCompetence.setSoftPreconList(softPreconditionsListEat);

        //Define ADD-LIST
        ArrayList<Memory> addListEat = new ArrayList<>();
        addListEat.add(stateNotHungryMO);
        eatCompetence.setAddList(addListEat);

        //Define DELETE-LIST
        ArrayList<Memory> deleteListEat = new ArrayList<>();
        deleteListEat.add(stateHungryMO);
        eatCompetence.setDeleteList(deleteListEat);
    }
    
    public void defineWorldStateAndGoals(String stateHungrySimulation, String stateFoundFoodSimulation, String stateExploringSimulation) {
        worldStateHungryMO = createMemoryObject("WORLD_STATE", stateHungrySimulation);
        worldStateFoundFoodMO = createMemoryObject("WORLD_STATE", stateFoundFoodSimulation);
        worldStateExploringMO = createMemoryObject("WORLD_STATE", stateExploringSimulation);
        
        Memory goals1MO = createMemoryObject("PERMANENT_GOAL", stateNotHungry);
        Memory goals2MO = createMemoryObject("PERMANENT_GOAL", stateExploring);
        
        //Define WORLD_STATE_PARAMETERS and GOALS as inputs
        exploreCompetence.addInput(worldStateHungryMO);
        exploreCompetence.addInput(worldStateFoundFoodMO);
        exploreCompetence.addInput(worldStateExploringMO);
        exploreCompetence.addInput(goals1MO);
        exploreCompetence.addInput(goals2MO);
        
        //Define WORLD_STATE_PARAMETERS and GOALS as inputs
        forageFoodCompetence.addInput(worldStateHungryMO);
        forageFoodCompetence.addInput(worldStateFoundFoodMO);
        forageFoodCompetence.addInput(worldStateExploringMO);
        forageFoodCompetence.addInput(goals1MO);
        forageFoodCompetence.addInput(goals2MO);
        
        eatCompetence.addInput(worldStateHungryMO);
        eatCompetence.addInput(worldStateFoundFoodMO);
        eatCompetence.addInput(worldStateExploringMO);
        eatCompetence.addInput(goals1MO);
        eatCompetence.addInput(goals2MO);
    }
}

class MockBehaviorWTA extends Behavior {
    public MockBehaviorWTA(WorkingStorage ws,GlobalVariables globalVariables) {
        super(ws, globalVariables);
    }

    @Override
    public void proc() {
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
}

