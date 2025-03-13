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
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static br.unicamp.cst.behavior.bn.GlobalVariables.Goals.PROTECTED_GOALS;

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
    
    Memory testStateMO = createMemoryObject("anythingName", "AnyInfo");     
    
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    
    @BeforeEach 
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        
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
    
    @Test 
    public void testTakenAway() {
    
        double activationTakenAway = eatCompetence.takenAway();
        assertEquals(0.0, activationTakenAway);
    }
    
    @Test
    public void testUpdateLinks() {
        defineCompetenceLists(true);
        
        ArrayList<Behavior> listOfAllBehaviors=new ArrayList<>();
        listOfAllBehaviors.add(exploreCompetence);
        listOfAllBehaviors.add(eatCompetence);
        listOfAllBehaviors.add(forageFoodCompetence);
        
        eatCompetence.setCoalition(listOfAllBehaviors);
        
        Hashtable<Behavior, ArrayList<Memory>> listOfSuccessors  = new Hashtable<>();
        ArrayList<Memory> intersectionSuccessors = new ArrayList<>();
        intersectionSuccessors.add(stateNotHungryMO);
        listOfSuccessors.put(exploreCompetence, intersectionSuccessors);
        
        Hashtable<Behavior, ArrayList<Memory>> listOfPredecessors  = new Hashtable<Behavior, ArrayList<Memory>>();
        
        ArrayList<Memory> intersectionPredecessorsForage = new ArrayList<Memory>();
        intersectionPredecessorsForage.add(stateFoundFoodMO);
        listOfPredecessors.put(forageFoodCompetence, intersectionPredecessorsForage);
        
        ArrayList<Memory> intersectionPredecessorsExplore = new ArrayList<Memory>();
        intersectionPredecessorsExplore.add(stateHungryMO);
        listOfPredecessors.put(exploreCompetence, intersectionPredecessorsExplore);
        
        Hashtable<Behavior, ArrayList<Memory>> listOfConflicters  = new Hashtable<Behavior, ArrayList<Memory>>();
        
        ArrayList<Memory> intersectionConflicters = new ArrayList<Memory>();
        intersectionConflicters.add(testStateMO);
        
        listOfConflicters.put(forageFoodCompetence, intersectionConflicters);
        
        eatCompetence.proc();
        assertEquals(listOfSuccessors, eatCompetence.getSuccessors());
        assertEquals(listOfPredecessors, eatCompetence.getPredecessors());
        assertEquals(listOfConflicters, eatCompetence.getConflicters());
    }
    
    
    @Test
    public void testTakenAwayWithConflicters() {
        
        defineCompetenceLists(false);
        
        ArrayList<Behavior> listOfAllBehaviors=new ArrayList<>();
        listOfAllBehaviors.add(exploreCompetence);
        listOfAllBehaviors.add(eatCompetence);
        listOfAllBehaviors.add(forageFoodCompetence);
        
        eatCompetence.setCoalition(listOfAllBehaviors);
        
        Memory goalMO = createMemoryObject("WORLD_STATE", stateHungry);
        
        try {
            forageFoodCompetence.setActivation(0.5);
        } catch (CodeletActivationBoundsException ex) {
            Logger.getLogger(TestBehavior.class.getName()).log(Level.SEVERE, null, ex);
        }
        eatCompetence.addInput(goalMO);
        eatCompetence.proc();
        
        double activationTakenAway = eatCompetence.takenAway();
        assertEquals(0.125, activationTakenAway);
    }
    
    @Test 
    public void testIntersectionSet() {
        
        ArrayList<Memory> A  = new ArrayList<Memory>();
        ArrayList<Memory> B = new ArrayList<Memory>();
        A.add(stateHungryMO);
        A.add(stateFoundFoodMO);
        B.add(stateHungryMO);
        B.add(stateExploringMO);
        
        ArrayList<Memory> intersection = new ArrayList<Memory>();
        intersection.add(stateHungryMO);
        
        assertEquals(intersection, eatCompetence.getIntersectionSet(A, B));
    }
    
    @Test 
    public void testIntersectionSetWorldStateGetsSecondArrayMemoryAsIntersection() {
        
        ArrayList<Memory> A  = new ArrayList<Memory>();
        ArrayList<Memory> B = new ArrayList<Memory>();
        A.add(stateHungryMO);
        A.add(stateFoundFoodMO);
        Memory worldStateHungryMO = createMemoryObject("WORLD_STATE", stateHungry);
        B.add(worldStateHungryMO);
        B.add(stateExploringMO);
        
        ArrayList<Memory> intersection = new ArrayList<Memory>();
        intersection.add(worldStateHungryMO);
        assertNotEquals(intersection, eatCompetence.getIntersectionSet(B, A));
        assertEquals(intersection, eatCompetence.getIntersectionSet(A, B));
    }
    
    @Test 
    public void testIntersectionSetPropositions() {
        
        ArrayList<Memory> A  = new ArrayList<>();
        ArrayList<Memory> B = new ArrayList<>();
        Memory prop1 = createMemoryObject("PROPOSITION", stateHungry);
        Memory prop2 = createMemoryObject("WORLD_STATE", stateFoundFood);
        Memory prop3 = createMemoryObject("PERMANENT_GOAL", stateExploring);
        
        
        A.add(prop1);
        A.add(prop2);
        
        B.add(prop2);
        B.add(prop3);
        
        ArrayList<Memory> intersection = new ArrayList<>();
        intersection.add(prop2);
        assertEquals(intersection, eatCompetence.getIntersectionSet(A, B));
        assertEquals(intersection, eatCompetence.getIntersectionSet(B, A));
    }
    
    @Test 
    public void testSpreadActivationExploreSelected() {
        defineWorldStateAndGoals(stateNotHungry, stateNotFoundFood, stateNotExploring);
        defineCompetenceLists(false);
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
        defineCompetenceLists(false);
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
        defineCompetenceLists(false);
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
    
    //TODO: voltar neste aqui
    @Test 
    public void testSpreadFwForageFoodCompetence(){
        defineCompetenceLists(false);
        ArrayList<Behavior> listOfAllBehaviors=new ArrayList<>();
        listOfAllBehaviors.add(exploreCompetence);
        listOfAllBehaviors.add(eatCompetence);
        listOfAllBehaviors.add(forageFoodCompetence);
        try {
            exploreCompetence.setActivation(0.5);
        } catch (CodeletActivationBoundsException ex) {
            Logger.getLogger(TestBehavior.class.getName()).log(Level.SEVERE, null, ex);
        }
        exploreCompetence.setExecutable(true);
        eatCompetence.setExecutable(true);
        forageFoodCompetence.setCoalition(listOfAllBehaviors);
        
        assertEquals(0.0625, forageFoodCompetence.spreadFw());
    }
    
    @Test 
    public void testSpreadBwExploreCompetence(){
        defineCompetenceLists(false);
        ArrayList<Behavior> listOfAllBehaviors=new ArrayList<>();
        listOfAllBehaviors.add(exploreCompetence);
        listOfAllBehaviors.add(eatCompetence);
        listOfAllBehaviors.add(forageFoodCompetence);
        forageFoodCompetence.setExecutable(false);
        try {
            forageFoodCompetence.setActivation(0.5);
        } catch (CodeletActivationBoundsException ex) {
            Logger.getLogger(TestBehavior.class.getName()).log(Level.SEVERE, null, ex);
        }
        eatCompetence.setExecutable(false);
        exploreCompetence.setCoalition(listOfAllBehaviors);
        
        assertEquals(0.0625, exploreCompetence.spreadBw());
    }
    
    @Test
    public void testInputFromGoalsOfTypeProtected() {
        
        Memory goalProtectedMO = createMemoryObject("PROTECTED_GOAL", stateNotHungry);
        
        defineCompetenceLists(false);
        exploreCompetence.addInput(goalProtectedMO);
        
        ArrayList<Behavior> listOfAllBehaviors=new ArrayList<>();
        listOfAllBehaviors.add(exploreCompetence);
        listOfAllBehaviors.add(eatCompetence);
        listOfAllBehaviors.add(forageFoodCompetence);
        exploreCompetence.proc();
        exploreCompetence.setCoalition(listOfAllBehaviors);
        
        assertEquals(0.05, exploreCompetence.inputFromGoalsOfType(PROTECTED_GOALS));
    }
    
    @Test
    public void testActionList() {
        String actionString = "HANDS EAT FOOD 30 40";
        eatCompetence.addAction(actionString);

        JSONArray expectedJsonActionList = new JSONArray();
        JSONObject expectedJsonObject = new JSONObject();
        expectedJsonObject.put("RESOURCE", "HANDS");
        expectedJsonObject.put("ACTION", "EAT");
        expectedJsonObject.put("P1", "FOOD");
        expectedJsonObject.put("P2", "30");
        expectedJsonObject.put("P3", "40");
        expectedJsonActionList.put(expectedJsonObject);

        assertEquals(expectedJsonActionList.toString(), eatCompetence.getActionList());
        eatCompetence.clearActionList();
        assertEquals("[]", eatCompetence.getActionList());
    }
    
    
    @Test
    public void testAddList() {
        //Define ADD-LIST
        ArrayList<Memory> addListExplore = new ArrayList<>();
        addListExplore.add(stateExploringMO);
        addListExplore.add(stateHungryMO);
        exploreCompetence.setAddList(addListExplore);
        
        assertEquals(addListExplore, exploreCompetence.getAddList());
        
        exploreCompetence.delAddList(stateHungryMO);
        exploreCompetence.delAddList(stateExploringMO);
        
        assertEquals(new ArrayList<Memory> (), exploreCompetence.getAddList());
    }
    
    @Test
    public void testDeleteList() {
        //Define DELETE-LIST
        ArrayList<Memory> deleteListExplore= new ArrayList<>();
        deleteListExplore.add(stateNotHungryMO);
        exploreCompetence.setDeleteList(deleteListExplore);
        
        assertEquals(deleteListExplore, exploreCompetence.getDeleteList());
        
        exploreCompetence.delDelList(stateNotHungryMO);
                
        
        assertEquals(new ArrayList<Memory> (), exploreCompetence.getDeleteList());
    }
    
    @Test
    public void testPreconditionsList() {
        //Define HARD-PRE-CONDITIONS
        ArrayList<Memory> preconditionsListForage = new ArrayList<>();
        preconditionsListForage.add(stateHungryMO);
        forageFoodCompetence.setListOfPreconditions(preconditionsListForage);
        
        assertEquals(preconditionsListForage, forageFoodCompetence.getListOfPreconditions());
        
        forageFoodCompetence.delPreconList(stateHungryMO);
        assertEquals(new ArrayList<Memory> (), forageFoodCompetence.getListOfPreconditions());
    }
    
    @Test
    public void testSoftPreconditionsList() {
        //Define SOFT-PRE-CONDITIONS
        ArrayList<Memory> softPreconditionsListEat = new ArrayList<>();
        softPreconditionsListEat.add(stateHungryMO);
        eatCompetence.setSoftPreconList(softPreconditionsListEat);
        
        assertEquals(softPreconditionsListEat, eatCompetence.getSoftPreconList());
        
        eatCompetence.delSoftPreconList(stateHungryMO);
        assertEquals(new ArrayList<Memory> (), eatCompetence.getSoftPreconList());
    }
    
    @Test
    public void testGoals() {
        Memory goals1MO = createMemoryObject("PERMANENT_GOAL", stateNotHungry);
        Memory goals2MO = createMemoryObject("PERMANENT_GOAL", stateExploring);
        
        Memory goalProtectedMO = createMemoryObject("PROTECTED_GOAL", stateNotHungry);
        Memory goalOnceOnlyMO = createMemoryObject("ONCE_ONLY_GOAL", stateExploring);
        
        //Define GOALS as inputs
        exploreCompetence.addInput(goals1MO);
        exploreCompetence.addInput(goals2MO);
        exploreCompetence.addInput(goalProtectedMO);
        exploreCompetence.addInput(goalOnceOnlyMO);
        
        exploreCompetence.proc();
        
        
        ArrayList<Memory> goals = new ArrayList<Memory>();
        
        
        goals.add(goalOnceOnlyMO);
        goals.add(goalProtectedMO);
        goals.add(goals1MO);
        goals.add(goals2MO);
        
        assertEquals(goals, exploreCompetence.getGoals());
    }
    
    @Test
    public void testChangedWorldBeliefState() {
        Memory worldStateHungryMO = createMemoryObject("WORLD_STATE", stateHungry);
        
        forageFoodCompetence.addInput(worldStateHungryMO);
        assertEquals(false, forageFoodCompetence.changedWorldBeliefState());
        
        forageFoodCompetence.proc();
        assertEquals(true, forageFoodCompetence.changedWorldBeliefState());
        
        forageFoodCompetence.setActive(true);
        assertEquals(false, forageFoodCompetence.changedWorldBeliefState());

        worldStateHungryMO.setI(stateNotHungry);
        forageFoodCompetence.proc();
        assertEquals(true, forageFoodCompetence.changedWorldBeliefState());
    }
    
    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }
    
    
    
    public void defineCompetenceLists(Boolean includeSoftPrecon) {
        
        //Define HARD-PRE-CONDITIONS
        ArrayList<Memory> preconditionsListExplore = new ArrayList<>();
        preconditionsListExplore.add(stateNotHungryMO);
        exploreCompetence.setListOfPreconditions(preconditionsListExplore);
        if (includeSoftPrecon) {
            //Define SOFT-PRE-CONDITIONS
            ArrayList<Memory> softPreconditionsListExplore = new ArrayList<>();
            softPreconditionsListExplore.add(stateNotHungryMO);
            exploreCompetence.setSoftPreconList(softPreconditionsListExplore);
        }
        
        
        //Define ADD-LIST
        ArrayList<Memory> addListExplore = new ArrayList<>();
        addListExplore.add(stateExploringMO);
        addListExplore.add(stateHungryMO);
        exploreCompetence.setAddList(addListExplore);

        //Define DELETE-LIST
        ArrayList<Memory> deleteListExplore= new ArrayList<>();
        deleteListExplore.add(stateNotHungryMO);
        exploreCompetence.setDeleteList(deleteListExplore);
        
        if (includeSoftPrecon) {
            //Define SOFT-PRE-CONDITIONS
            ArrayList<Memory> softPreconditionsListForage = new ArrayList<>();
            softPreconditionsListForage.add(testStateMO);
            forageFoodCompetence.setSoftPreconList(softPreconditionsListForage);
        }
        
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
        
        if (includeSoftPrecon) {
            //Define SOFT-PRE-CONDITIONS
            ArrayList<Memory> softPreconditionsListEat = new ArrayList<>();
            softPreconditionsListEat.add(stateFoundFoodMO);
            eatCompetence.setSoftPreconList(softPreconditionsListEat);
        } else {
            preconditionsListEat.add(stateFoundFoodMO);
        }
        
        eatCompetence.setListOfPreconditions(preconditionsListEat);
        //Define ADD-LIST
        ArrayList<Memory> addListEat = new ArrayList<>();
        addListEat.add(stateNotHungryMO);
        eatCompetence.setAddList(addListEat);

        
        //Define DELETE-LIST
        ArrayList<Memory> deleteListEat = new ArrayList<>();
        deleteListEat.add(stateHungryMO);
        if(includeSoftPrecon) {
            deleteListEat.add(testStateMO);
        }
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
    
    public MemoryObject createMemoryObject(String name, Object info) {
            // memory object to be added to rawmemory
            MemoryObject mo = new MemoryObject();
            mo.setI(info);
            mo.setTimestamp(System.currentTimeMillis());
            mo.setEvaluation(0.0d);
            mo.setName(name);

            return mo;
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