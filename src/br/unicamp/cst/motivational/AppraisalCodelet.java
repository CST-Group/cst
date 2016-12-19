/*******************************************************************************
 * Copyright (c) 2016  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * Contributors:
 *     E. M. Froes, R. R. Gudwin - initial API and implementation
 ******************************************************************************/

package br.unicamp.cst.motivational;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.cst.representation.owrl.Configuration;

/**
 * Created by du on 14/12/16.
 */
public abstract class AppraisalCodelet extends Codelet {

    public final static String INPUT_CURRENT_CONFIGURATION_MEMORY = "INPUT_CURRENT_CONFIGURATION_MEMORY";
    public final static String INPUT_PREDICTED_SITUATION_MEMORY = "INPUT_PREDICTED_SITUATION_MEMORY";
    public final static String OUTPUT_APPRAISAL_MEMORY = "OUTPUT_APPRAISAL_MEMORY";

    private Memory currentConfigurationMO;
    private Memory predictedSituationMO;
    private Memory appraisalMO;

    private Configuration currentConfiguration;
    private Configuration predictedSituation;
    private Appraisal appraisal;

    @Override
    public void accessMemoryObjects() {
        if(getCurrentConfigurationMO() != null){
            setCurrentConfigurationMO(getInput(INPUT_CURRENT_CONFIGURATION_MEMORY, 0));
            setCurrentConfiguration((Configuration) getCurrentConfigurationMO().getI());

        }

        if(getPredictedSituationMO() != null){
            setPredictedSituationMO(getInput(INPUT_PREDICTED_SITUATION_MEMORY, 0));
            setPredictedSituation((Configuration) getPredictedSituationMO().getI());
        }

        if(getAppraisalMO() != null){
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
        setAppraisal(genarateAppraisal(getCurrentConfiguration(), getPredictedSituation()));
        getAppraisalMO().setI(getAppraisal());
    }


    public abstract Appraisal genarateAppraisal(Configuration currentConfiguration, Configuration predictedSituation);


    public Memory getCurrentConfigurationMO() {
        return currentConfigurationMO;
    }

    public void setCurrentConfigurationMO(Memory currentConfigurationMO) {
        this.currentConfigurationMO = currentConfigurationMO;
    }

    public Memory getPredictedSituationMO() {
        return predictedSituationMO;
    }

    public void setPredictedSituationMO(Memory predictedSituationMO) {
        this.predictedSituationMO = predictedSituationMO;
    }

    public Memory getAppraisalMO() {
        return appraisalMO;
    }

    public void setAppraisalMO(Memory appraisalMO) {
        this.appraisalMO = appraisalMO;
    }

    public Configuration getCurrentConfiguration() {
        return currentConfiguration;
    }

    public void setCurrentConfiguration(Configuration currentConfiguration) {
        this.currentConfiguration = currentConfiguration;
    }

    public Configuration getPredictedSituation() {
        return predictedSituation;
    }

    public void setPredictedSituation(Configuration predictedSituation) {
        this.predictedSituation = predictedSituation;
    }

    public Appraisal getAppraisal() {
        return appraisal;
    }

    public void setAppraisal(Appraisal appraisal) {
        this.appraisal = appraisal;
    }
}
