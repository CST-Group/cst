package br.unicamp.cst.motivational;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.cst.representation.owrl.AbstractObject;

public abstract class GoalCodelet extends Codelet {

    private String id;
    private Memory inputHypotheticalSituationsMO;
    private Memory goalMO;

    private AbstractObject hypotheticalSituation;
    private Goal goal;


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
        setGoal(goalGeneration(getHypotheticalSituation()));
        getGoal().setId(getId());
        getGoalMO().setI(getGoal());
    }


    public abstract Goal goalGeneration(AbstractObject hypoteticalSituation);

    public Memory getInputHypotheticalSituationsMO() {
        return inputHypotheticalSituationsMO;
    }

    public void setInputHypotheticalSituationsMO(Memory inputHypotheticalSituationsMO) {
        this.inputHypotheticalSituationsMO = inputHypotheticalSituationsMO;
    }

    public Memory getGoalMO() {
        return goalMO;
    }

    public void setGoalMO(Memory goalMO) {
        this.goalMO = goalMO;
    }

    public AbstractObject getHypotheticalSituation() {
        return hypotheticalSituation;
    }

    public void setHypotheticalSituation(AbstractObject hypotheticalSituation) {
        this.hypotheticalSituation = hypotheticalSituation;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
