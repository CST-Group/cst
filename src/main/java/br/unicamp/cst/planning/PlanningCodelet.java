package br.unicamp.cst.planning;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

import java.util.Optional;

public abstract class PlanningCodelet extends Codelet {

    private String id;

    private Memory inputInitialState;

    private Memory inputObservations;

    private Memory inputGoals;

    private Memory inputProceduralMemory;

    private Memory inputTransitionFunctions;

    private Memory inputPlanningRequest;

    private Memory outputPlan;

    public PlanningCodelet(String id) {
        setId(id);
    }

    @Override
    public void accessMemoryObjects() {
        inputInitialState = Optional.ofNullable(inputInitialState)
                .orElse(getInput(PlanningMemoryNames.INPUT_INITIAL_STATE.toString()));

        inputObservations = Optional.ofNullable(inputObservations)
                .orElse(getInput(PlanningMemoryNames.INPUT_OBSERVATIONS.toString()));

        inputGoals = Optional.ofNullable(inputGoals)
                .orElse(getInput(PlanningMemoryNames.INPUT_GOALS.toString()));

        inputProceduralMemory = Optional.ofNullable(inputProceduralMemory)
                .orElse(getInput(PlanningMemoryNames.INPUT_PROCEDURAL_MEMORY.toString()));

        inputTransitionFunctions = Optional.ofNullable(inputTransitionFunctions)
                .orElse(getInput(PlanningMemoryNames.INPUT_TRANSITION_FUNCTIONS.toString()));

        inputPlanningRequest = Optional.ofNullable(inputPlanningRequest)
                .orElse(getInput(PlanningMemoryNames.INPUT_PLANNING_REQUEST.toString()));

        outputPlan = Optional.ofNullable(outputPlan)
                .orElse(getInput(PlanningMemoryNames.OUTPUT_PLAN.toString()));
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
        Optional.ofNullable(outputPlan).ifPresent(memory -> {
            memory.setI(planning(inputInitialState, inputObservations, inputGoals, inputProceduralMemory, inputTransitionFunctions, inputPlanningRequest).getI());
        });
    }

    public abstract Memory planning(Memory initialState, Memory observations, Memory goals, Memory proceduralMemory, Memory transitionFunctions, Memory planningRequest);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
