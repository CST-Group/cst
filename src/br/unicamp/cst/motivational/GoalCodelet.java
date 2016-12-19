package br.unicamp.cst.motivational;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.cst.representation.owrl.Configuration;

/**
 * Created by du on 19/12/16.
 */
public abstract class GoalCodelet extends Codelet {


    private static final String INPUT_CURRENT_PERCEPTION_MEMORY = "INPUT_CURRENT_PERCEPTION_MEMORY";
    private static final String OUTPUT_GOAL_MEMORY = "OUTPUT_GOAL_MEMORY";

    private Memory currentPerceptionMO;
    private Memory goalMO;

    private Configuration currentPerception;
    private Goal goal;

    @Override
    public void accessMemoryObjects() {
        if(getCurrentPerceptionMO() == null)
        {
            setCurrentPerceptionMO(this.getInput(INPUT_CURRENT_PERCEPTION_MEMORY, 0));
            setCurrentPerception((Configuration) getCurrentPerceptionMO().getI());
        }

        if(getGoalMO() == null){
            setGoalMO(this.getOutput(OUTPUT_GOAL_MEMORY, 0));
        }
    }

    @Override
    public void calculateActivation() {
        try {
            setActivation(0);
        } catch (CodeletActivationBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void proc() {
        setGoal(goalGenarate(getCurrentPerception()));
        getGoalMO().setI(getGoal());
    }


    public abstract Goal goalGenarate(Configuration currentPerception);

    public Memory getCurrentPerceptionMO() {
        return currentPerceptionMO;
    }

    public void setCurrentPerceptionMO(Memory currentPerceptionMO) {
        this.currentPerceptionMO = currentPerceptionMO;
    }

    public Memory getGoalMO() {
        return goalMO;
    }

    public void setGoalMO(Memory goalMO) {
        this.goalMO = goalMO;
    }

    public Configuration getCurrentPerception() {
        return currentPerception;
    }

    public void setCurrentPerception(Configuration currentPerception) {
        this.currentPerception = currentPerception;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }
}
