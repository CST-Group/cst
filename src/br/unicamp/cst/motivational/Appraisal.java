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

import br.unicamp.cst.representation.owrl.Configuration;

/**
 * Created by du on 14/12/16.
 */
public class Appraisal {

    private String name;

    private double evaluation;

    private String currentState;

    private Configuration currentConfiguration;

    private Configuration predictedSituation;


    public Appraisal(String name, String currentState, double evaluation, Configuration currentConfiguration, Configuration predictedSituation){
        setName(name);
        setEvaluation(evaluation);
        setCurrentState(currentState);
        setCurrentConfiguration(currentConfiguration);
        setPredictedSituation(predictedSituation);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(double evaluation) {
        this.evaluation = evaluation;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
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
}
