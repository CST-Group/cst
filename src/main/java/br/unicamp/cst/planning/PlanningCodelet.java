package br.unicamp.cst.planning;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

import java.util.Optional;

public abstract class PlanningCodelet extends Codelet {

    private String id;

    private Memory inputInitialStateMO;
    
    private Memory inputGoalsMO;
    private Memory inputActionsMO;
    private Memory inputTransitionFunctionsMO;

    private Memory outputPlanMO;

    public PlanningCodelet(String id) {
        setId(id);
    }

    @Override
    public void accessMemoryObjects() {
        inputInitialStateMO = Optional.ofNullable(inputGoalsMO)
                .orElse(getInput(PlanningMemoryNames.INPUT_INITIAL_STATE_MEMORY.toString()));

        inputGoalsMO = Optional.ofNullable(inputGoalsMO)
                .orElse(getInput(PlanningMemoryNames.INPUT_GOALS_MEMORY.toString()));

        inputActionsMO = Optional.ofNullable(inputActionsMO)
                .orElse(getInput(PlanningMemoryNames.INPUT_ACTIONS_MEMORY.toString()));

        inputTransitionFunctionsMO = Optional.ofNullable(inputTransitionFunctionsMO)
                .orElse(getInput(PlanningMemoryNames.INPUT_TRANSITION_FUNCTIONS_MEMORY.toString()));

        outputPlanMO = Optional.ofNullable(outputPlanMO)
                .orElse(getInput(PlanningMemoryNames.INPUT_INITIAL_STATE_MEMORY.toString()));
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
        outputPlanMO.setI(planning(inputInitialStateMO, inputGoalsMO, inputActionsMO).getI());
    }

    public abstract Memory planning(Memory currentState, Memory goal, Memory actions);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
