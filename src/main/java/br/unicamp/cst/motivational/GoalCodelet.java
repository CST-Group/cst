/*******************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * Contributors:
 *     K. Raizer, A. L. O. Paraense, R. R. Gudwin - initial API and implementation
 ******************************************************************************/
package br.unicamp.cst.motivational;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.cst.representation.owrl.AbstractObject;

public abstract class GoalCodelet extends Codelet {

    public static final String INPUT_HYPOTHETICAL_SITUATIONS_MEMORY = "INPUT_HYPOTHETICAL_SITUATIONS_MEMORY";
    public static final String OUTPUT_GOAL_MEMORY = "OUTPUT_GOAL_MEMORY";

    private String id;
    private Memory inputHypotheticalSituationsMO;
    private Memory goalMO;

    private AbstractObject hypotheticalSituation;
    private Goal goal;

    private boolean init = false;


    public GoalCodelet(String id) {
        this.setId(id);
    }

    @Override
    public void accessMemoryObjects() {
        if (getInputHypotheticalSituationsMO() == null) {
            setInputHypotheticalSituationsMO(this.getInput(INPUT_HYPOTHETICAL_SITUATIONS_MEMORY, 0));
        }

        if (getGoalMO() == null) {
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
        if (isInit()) {
            setHypotheticalSituation((AbstractObject) ((Memory) getInputHypotheticalSituationsMO().getI()).getI());
            setGoal(goalGeneration(getHypotheticalSituation()));
            getGoalMO().setI(getGoal());
        }

        setInit(true);
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

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }
}
