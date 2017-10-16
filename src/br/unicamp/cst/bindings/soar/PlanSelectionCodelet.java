package br.unicamp.cst.bindings.soar;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

import java.util.*;


public abstract class PlanSelectionCodelet extends Codelet {

    private MemoryObject inputNextActionMO;
    private MemoryContainer inputDataMC;

    private MemoryObject outputSelectedPlanMO;
    private MemoryObject outputPlansSetMO;


    private HashMap<Integer, Plan> plansMap;

    private int index;

    private Plan currentPlanInExecution;

    public static final String OUTPUT_PLANS_SET_MO = "OUTPUT_PLANS_SET_MO";
    public static final String OUPUT_SELECTED_PLAN_MO = "OUPUT_SELECTED_PLAN_MO";
    public static final String INPUT_DATA_MC = "INPUT_DATA_MC";

    public PlanSelectionCodelet(String id) {
        setName(id);
        setPlansMap(new HashMap<Integer, Plan>());
        this.setIndex(0);
        this.setCurrentPlanInExecution(null);
    }

    @Override
    public void accessMemoryObjects() {


        if (getInputNextActionMO() == null) {
            setInputNextActionMO((MemoryObject) this.getInput(JSoarCodelet.OUTPUT_COMMAND_MO));
        }

        if (getOutputSelectedPlanMO() == null) {
            setOutputSelectedPlanMO((MemoryObject) this.getOutput(OUPUT_SELECTED_PLAN_MO));
        }

        if (getOutputPlansSetMO() == null) {
            setOutputPlansSetMO((MemoryObject) this.getOutput(OUTPUT_PLANS_SET_MO));
        }

        if(getInputDataMC() == null){
            setInputDataMC((MemoryContainer) this.getInput(INPUT_DATA_MC));
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

        if (getInputNextActionMO().getI() != null) {
            List<Object> nextAction = new ArrayList<>(Collections.synchronizedList((List<Object>) getInputNextActionMO().getI()));

            Object soarPlan = nextAction.get(0);

            Plan newPlan = new Plan(soarPlan);

            if (getPlansMap().size() == 0) {
                getPlansMap().put(getIndex(), newPlan);
                setIndex(getIndex() + 1);
            } else {
                if (!verifyExistPlan(newPlan) && !verifyIfPlanWasFinished(newPlan)) {
                    getPlansMap().put(getIndex(), newPlan);
                    setIndex(getIndex() + 1);
                }
            }
        }

        for (Map.Entry<Integer, Plan> plan : getPlansMap().entrySet()) {
            Plan value = plan.getValue();
            value.setFinished(verifyIfPlanWasFinished(value));
        }

        if (getCurrentPlanInExecution() != null) {
            if (getCurrentPlanInExecution().isFinished()) {
                setCurrentPlanInExecution(selectPlanToExecute(getPlansMap()));
            }
        } else {
            if (getPlansMap().size() > 0) {
                setCurrentPlanInExecution(selectPlanToExecute(getPlansMap()));
            }
        }

        getOutputSelectedPlanMO().setI(getCurrentPlanInExecution());
        getOutputPlansSetMO().setI(getPlansMap());
    }

    public abstract Plan selectPlanToExecute(HashMap<Integer, Plan> plans);

    public abstract boolean verifyIfPlanWasFinished(Plan plan);

    public abstract boolean verifyExistPlan(Plan plan);

    public MemoryObject getInputNextActionMO() {
        return inputNextActionMO;
    }

    public void setInputNextActionMO(MemoryObject inputNextActionMO) {
        this.inputNextActionMO = inputNextActionMO;
    }

    public MemoryObject getOutputSelectedPlanMO() {
        return outputSelectedPlanMO;
    }

    public void setOutputSelectedPlanMO(MemoryObject outputSelectedPlanMO) {
        this.outputSelectedPlanMO = outputSelectedPlanMO;
    }

    public HashMap<Integer, Plan> getPlansMap() {
        return plansMap;
    }

    public void setPlansMap(HashMap<Integer, Plan> plansMap) {
        this.plansMap = plansMap;
    }

    public Plan getCurrentPlanInExecution() {
        return currentPlanInExecution;
    }

    public void setCurrentPlanInExecution(Plan currentPlanInExecution) {
        this.currentPlanInExecution = currentPlanInExecution;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public MemoryContainer getInputDataMC() {
        return inputDataMC;
    }

    public void setInputDataMC(MemoryContainer inputDataMO) {
        this.inputDataMC = inputDataMO;
    }

    public MemoryObject getOutputPlansSetMO() {
        return outputPlansSetMO;
    }

    public void setOutputPlansSetMO(MemoryObject outputPlansSetMO) {
        this.outputPlansSetMO = outputPlansSetMO;
    }
}
