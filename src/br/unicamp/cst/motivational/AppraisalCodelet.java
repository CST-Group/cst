/**
 *
 */
package br.unicamp.cst.motivational;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.cst.representation.owrl.Configuration;

import java.util.List;

public abstract class AppraisalCodelet extends Codelet {

    public final static String OUTPUT_APPRAISAL_MEMORY = "OUTPUT_APPRAISAL_MEMORY";
    public final static String INPUT_CONFIGURATIONS_MEMORY = "INPUT_CONFIGURATIONS_MEMORY";

    private String name;
    private List<Configuration> inputConfigurations;
    private Appraisal appraisal;
    private Memory inputConfigurationsMO;
    private Memory appraisalMO;

    public AppraisalCodelet(String name) {
        setName(name);
    }

    @Override
    public void accessMemoryObjects() {
        if (getInputConfigurationsMO() != null) {
            setInputConfigurationsMO(getInput(INPUT_CONFIGURATIONS_MEMORY, 0));
            setInputConfigurations((List<Configuration>) getInputConfigurationsMO().getI());
        }

        if (getAppraisalMO() != null) {
            setAppraisalMO(getOutput(OUTPUT_APPRAISAL_MEMORY, 0));
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
        setAppraisal(appraisalGeneration(getInputConfigurations()));
        getAppraisal().setName(getName());
        getAppraisalMO().setI(getAppraisal());
    }


    public abstract Appraisal appraisalGeneration(List<Configuration> inputConfigurations);

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }


    public Memory getAppraisalMO() {
        return appraisalMO;
    }

    public void setAppraisalMO(Memory appraisalMO) {
        this.appraisalMO = appraisalMO;
    }

    public List<Configuration> getInputConfigurations() {
        return inputConfigurations;
    }

    public void setInputConfigurations(List<Configuration> inputConfigurations) {
        this.inputConfigurations = inputConfigurations;
    }

    public Appraisal getAppraisal() {
        return appraisal;
    }

    public void setAppraisal(Appraisal appraisal) {
        this.appraisal = appraisal;
    }

    public Memory getInputConfigurationsMO() {
        return inputConfigurationsMO;
    }

    public void setInputConfigurationsMO(Memory inputConfigurationsMO) {
        this.inputConfigurationsMO = inputConfigurationsMO;
    }
}

