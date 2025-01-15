/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.unicamp.cst.behavior.bn;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.RawMemory;
import br.unicamp.cst.memory.WorkingStorage;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author karenlima
 */
public class TestBehavior {
    GlobalVariables gv;
    WorkingStorage ws;
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
    
    public MemoryObject createMemoryObject(String name, Object info) {
            // memory object to be added to rawmemory
            MemoryObject mo = new MemoryObject();
            mo.setI(info);
            mo.setTimestamp(System.currentTimeMillis());
            mo.setEvaluation(0.0d);
            mo.setName(name);

            return mo;
    }
    
    @BeforeEach 
    public void setUp() {
        gv =  new GlobalVariables();
        ws = new WorkingStorage(100, new RawMemory());
        
        exploreCompetence = new MockBehavior(ws, gv);
        eatCompetence = new MockBehavior(ws, gv);
        forageFoodCompetence = new MockBehavior(ws, gv);
    }
    
    @Test
    public void testInputs() {
        Memory goals1MO = createMemoryObject("PERMANENT_GOAL", stateNotHungry);
        Memory goals2MO = createMemoryObject("PERMANENT_GOAL", stateExploring);
        
        exploreCompetence.addInput(goals1MO);
        exploreCompetence.addInput(goals2MO);
        
        assertEquals(exploreCompetence.getInputs().get(0), goals1MO);
        assertEquals(exploreCompetence.getInputs().get(1), goals2MO);
        assertEquals(exploreCompetence.getInputs().get(0).getName(),"PERMANENT_GOAL");
        assertEquals(exploreCompetence.getInputs().get(1).getName(), "PERMANENT_GOAL");
        assertEquals(exploreCompetence.getInputs().get(0).getI(),stateNotHungry);
        assertEquals(exploreCompetence.getInputs().get(1).getI(), stateExploring);
    }
    
    @Test
    public void testRetrieveGoals() {
        Memory goals1MO = createMemoryObject("PERMANENT_GOAL", stateNotHungry);
        Memory goals2MO = createMemoryObject("PERMANENT_GOAL", stateExploring);
        
        exploreCompetence.addInput(goals1MO);
        exploreCompetence.addInput(goals2MO);
        exploreCompetence.proc();
        
        assertEquals(exploreCompetence.getGoals().get(0).getName(),"PERMANENT_GOAL");
        assertEquals(exploreCompetence.getGoals().get(1).getName(), "PERMANENT_GOAL");
        assertEquals(exploreCompetence.getGoals().get(0).getI(),stateNotHungry);
        assertEquals(exploreCompetence.getGoals().get(1).getI(), stateExploring);
    }
    
    @Test
    public void testRetrieveState() {
        Memory worldStateHungryMO = createMemoryObject("WORLD_STATE", stateNotHungry);
        Memory worldStateFoundFoodMO = createMemoryObject("WORLD_STATE", stateNotFoundFood);
        Memory worldStateExploringMO = createMemoryObject("WORLD_STATE", stateNotExploring);
              
        exploreCompetence.addInput(worldStateHungryMO);
        exploreCompetence.addInput(worldStateFoundFoodMO);
        exploreCompetence.addInput(worldStateExploringMO);
        
        exploreCompetence.proc();
        
        assertEquals(exploreCompetence.getWorldState().get(0).getName(),"WORLD_STATE");
        assertEquals(exploreCompetence.getWorldState().get(1).getName(), "WORLD_STATE");
        assertEquals(exploreCompetence.getWorldState().get(2).getName(), "WORLD_STATE");
        assertEquals(exploreCompetence.getWorldState().get(0).getI(),stateNotHungry);
        assertEquals(exploreCompetence.getWorldState().get(1).getI(), stateNotFoundFood);
        assertEquals(exploreCompetence.getWorldState().get(2).getI(), stateNotExploring);
        
    }
    
    @Test 
    public void testCheckIfExecutableExploreCompetenceIsExecutable() {
        Memory worldStateHungryMO = createMemoryObject("WORLD_STATE", stateNotHungry);
        Memory worldStateFoundFoodMO = createMemoryObject("WORLD_STATE", stateNotFoundFood);
        Memory worldStateExploringMO = createMemoryObject("WORLD_STATE", stateNotExploring);
              
        exploreCompetence.addInput(worldStateHungryMO);
        exploreCompetence.addInput(worldStateFoundFoodMO);
        exploreCompetence.addInput(worldStateExploringMO);
        
        //Define HARD-PRE-CONDITIONS
        ArrayList<Memory> preconditionsListExplore = new ArrayList<>();
        preconditionsListExplore.add(stateNotHungryMO);
        exploreCompetence.setListOfPreconditions(preconditionsListExplore);

        exploreCompetence.proc();
        
        assertEquals(exploreCompetence.isExecutable(),true);
    }
    
    @Test 
    public void testCheckIfNotExecutableExploreCompetenceIsNotExecutable() {
        Memory worldStateHungryMO = createMemoryObject("WORLD_STATE", stateHungry);
        Memory worldStateFoundFoodMO = createMemoryObject("WORLD_STATE", stateNotFoundFood);
        Memory worldStateExploringMO = createMemoryObject("WORLD_STATE", stateNotExploring);
              
        exploreCompetence.addInput(worldStateHungryMO);
        exploreCompetence.addInput(worldStateFoundFoodMO);
        exploreCompetence.addInput(worldStateExploringMO);
        
        //Define HARD-PRE-CONDITIONS
        ArrayList<Memory> preconditionsListExplore = new ArrayList<>();
        preconditionsListExplore.add(stateNotHungryMO);
        exploreCompetence.setListOfPreconditions(preconditionsListExplore);

        exploreCompetence.proc();
        
        assertEquals(exploreCompetence.isExecutable(),false);
    }
    
    @Test 
    public void testCheckIfExecutableEatCompetenceIsExecutable() {
        Memory worldStateHungryMO = createMemoryObject("WORLD_STATE", stateHungry);
        Memory worldStateFoundFoodMO = createMemoryObject("WORLD_STATE", stateFoundFood);
        Memory worldStateExploringMO = createMemoryObject("WORLD_STATE", stateExploring);
        
        eatCompetence.addInput(worldStateHungryMO);
        eatCompetence.addInput(worldStateFoundFoodMO);
        eatCompetence.addInput(worldStateExploringMO);
        
        //Define HARD-PRE-CONDITIONS
        ArrayList<Memory> preconditionsListExplore = new ArrayList<>();
        preconditionsListExplore.add(stateHungryMO);
        preconditionsListExplore.add(stateFoundFoodMO);
        eatCompetence.setListOfPreconditions(preconditionsListExplore);

        eatCompetence.proc();
        
        assertEquals(eatCompetence.isExecutable(),true);
    }
    
    @Test 
    public void testCheckIfNotExecutableEatCompetenceIsNotExecutable() {
        Memory worldStateHungryMO = createMemoryObject("WORLD_STATE", stateHungry);
        Memory worldStateFoundFoodMO = createMemoryObject("WORLD_STATE", stateNotFoundFood);
        Memory worldStateExploringMO = createMemoryObject("WORLD_STATE", stateExploring);
        
        eatCompetence.addInput(worldStateHungryMO);
        eatCompetence.addInput(worldStateFoundFoodMO);
        eatCompetence.addInput(worldStateExploringMO);
        
        //Define HARD-PRE-CONDITIONS
        ArrayList<Memory> preconditionsListExplore = new ArrayList<>();
        preconditionsListExplore.add(stateHungryMO);
        preconditionsListExplore.add(stateFoundFoodMO);
        eatCompetence.setListOfPreconditions(preconditionsListExplore);

        eatCompetence.proc();
        
        assertEquals(eatCompetence.isExecutable(),false);
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
        Memory worldStateHungryMO = createMemoryObject("WORLD_STATE", stateHungrySimulation);
        Memory worldStateFoundFoodMO = createMemoryObject("WORLD_STATE", stateFoundFoodSimulation);
        Memory worldStateExploringMO = createMemoryObject("WORLD_STATE", stateExploringSimulation);
        
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
    
    public void addAllBehaviors() {
        ArrayList<Behavior> listOfAllBehaviors=new ArrayList<>();
        listOfAllBehaviors.add(exploreCompetence);
        listOfAllBehaviors.add(eatCompetence);
        listOfAllBehaviors.add(forageFoodCompetence);
        
        exploreCompetence.setBehaviors(listOfAllBehaviors);
        eatCompetence.setBehaviors(listOfAllBehaviors);
        forageFoodCompetence.setBehaviors(listOfAllBehaviors);
    }
    
    @Test 
    public void testSpreadActivationExploreSelected() {
        defineWorldStateAndGoals(stateNotHungry, stateNotFoundFood, stateNotExploring);
        defineCompetenceLists();
        addAllBehaviors();
        
        eatCompetence.proc();
        forageFoodCompetence.proc();
        exploreCompetence.proc();
        
        assertEquals(0.1, exploreCompetence.getInputfromgoals());
        assertEquals(0.05, exploreCompetence.getInputfromstate());
        assertEquals(0.0, exploreCompetence.getSpreadbw());
        assertEquals(0.0, exploreCompetence.getSpreadfw());
        assertEquals(0.15000000000000002, exploreCompetence.getActivation());
        assertEquals(0.2, eatCompetence.getInputfromgoals());
        assertEquals(0.0, eatCompetence.getInputfromstate());
        assertEquals(0.0, eatCompetence.getSpreadbw());
        assertEquals(0.0, eatCompetence.getSpreadfw());
        assertEquals(0.2, eatCompetence.getActivation());
        assertEquals(0.0, forageFoodCompetence.getInputfromgoals());
        assertEquals(0.0, forageFoodCompetence.getInputfromstate());
        assertEquals(0.0, forageFoodCompetence.getSpreadbw());
        assertEquals(0.0, forageFoodCompetence.getSpreadfw());
        assertEquals(0.0, forageFoodCompetence.getActivation());
    }
    
    @Test 
    public void testSpreadActivationEatSelected() {
        defineWorldStateAndGoals(stateHungry, stateFoundFood, stateExploring);
        defineCompetenceLists();
        addAllBehaviors();
        
        eatCompetence.proc();
        forageFoodCompetence.proc();
        exploreCompetence.proc();
        
        assertEquals(0.1, exploreCompetence.getInputfromgoals());
        assertEquals(0.0, exploreCompetence.getInputfromstate());
        assertEquals(0.0, exploreCompetence.getSpreadbw());
        assertEquals(0.0, exploreCompetence.getSpreadfw());
        assertEquals(0.1, exploreCompetence.getActivation());
        assertEquals(0.2, eatCompetence.getInputfromgoals());
        assertEquals(0.037500000000000006, eatCompetence.getInputfromstate());
        assertEquals(0.0, eatCompetence.getSpreadbw());
        assertEquals(0.0, eatCompetence.getSpreadfw());
        assertEquals(0.23750000000000002, eatCompetence.getActivation());
        assertEquals(0.0, forageFoodCompetence.getInputfromgoals());
        assertEquals(0.025, forageFoodCompetence.getInputfromstate());
        assertEquals(0.0, forageFoodCompetence.getSpreadbw());
        assertEquals(0.0, forageFoodCompetence.getSpreadfw());
        assertEquals(0.025, forageFoodCompetence.getActivation());
    }
    
    @Test 
    public void testSpreadActivationForageFoodSelected() {
        defineWorldStateAndGoals(stateHungry, stateNotFoundFood, stateExploring);
        defineCompetenceLists();
        addAllBehaviors();
        
        eatCompetence.proc();
        forageFoodCompetence.proc();
        exploreCompetence.proc();
        
        assertEquals(0.1, exploreCompetence.getInputfromgoals());
        assertEquals(0.0, exploreCompetence.getInputfromstate());
        assertEquals(0.0, exploreCompetence.getSpreadbw());
        assertEquals(0.0, exploreCompetence.getSpreadfw());
        assertEquals(0.1, exploreCompetence.getActivation());
        assertEquals(0.2, eatCompetence.getInputfromgoals());
        assertEquals(0.0125, eatCompetence.getInputfromstate());
        assertEquals(0.0, eatCompetence.getSpreadbw());
        assertEquals(0.0, eatCompetence.getSpreadfw());
        assertEquals(0.21250000000000002, eatCompetence.getActivation());
        assertEquals(0.0, forageFoodCompetence.getInputfromgoals());
        assertEquals(0.025, forageFoodCompetence.getInputfromstate());
        assertEquals(0.0, forageFoodCompetence.getSpreadbw());
        assertEquals(0.0, forageFoodCompetence.getSpreadfw());
        assertEquals(0.025, forageFoodCompetence.getActivation());
    }
    
    
}

class MockBehavior extends Behavior {
    public MockBehavior(WorkingStorage ws,GlobalVariables globalVariables) {
        super(ws, globalVariables);
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