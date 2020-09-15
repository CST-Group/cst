package br.unicamp.cst.planning;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

public abstract class PlanningCodelet extends Codelet {

    private String id;

    private Memory inputInitialState;
    
    private Memory inputGoals;
    private Memory inputActions;
    private Memory inputTransitionFunctions;

    private Memory outputPlan;

    public PlanningCodelet(String id) {
        setId(id);
    }

    @Override
    public void accessMemoryObjects() {
        if(inputInitialState == null
            && inputGoals == null
            && inputActions == null
            && inputTransitionFunctions == null
            && outputPlan == null
        ){
            inputInitialState = getInput(PlanningMemoryNames.INPUT_INITIAL_STATE_MAMORY.toString());
            inputGoals = getInput(PlanningMemoryNames.INPUT_GOALS_MEMORY.toString());
            inputActions = getInput(PlanningMemoryNames.INPUT_ACTIONS_MEMORY.toString());
            inputTransitionFunctions = getInput(PlanningMemoryNames.INPUT_TRANSITION_FUNCTIONS_MEMORY.toString());
            outputPlan = getOutput(PlanningMemoryNames.OUTPUT_PLAN_MEMORY.toString());
        }
    }

    @Override
    public void calculateActivation() {
        try {
            setActivation(0d);
        } catch (CodeletActivationBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void proc() {
        outputPlan.setI(planning(inputInitialState, inputGoals, inputActions).getI());
    }

    public abstract Memory planning(Memory currentState, Memory goal, Memory actions);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
