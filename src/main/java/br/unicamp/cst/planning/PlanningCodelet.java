package br.unicamp.cst.planning;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

public abstract class PlanningCodelet extends Codelet {

    private Memory inputCurrentState;
    private Memory inputGoal;
    private Memory inputActions;
    private Memory outputPlan;

    @Override
    public void accessMemoryObjects() {
        if(inputCurrentState == null
            && inputGoal == null
            && inputActions == null
            && outputPlan == null
        ){
            inputCurrentState = getInput(PlanningMemoryNames.INPUT_CURRENT_STATE_MEMORY.toString());
            inputGoal = getInput(PlanningMemoryNames.INPUT_GOAL_MEMORY.toString());
            inputActions = getInput(PlanningMemoryNames.INPUT_ACTIONS_MEMORY.toString());
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
        outputPlan.setI(planning(inputCurrentState, inputGoal, inputActions).getI());
    }

    public abstract Memory planning(Memory currentState, Memory goal, Memory actions);
}
