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

public class Appraisal {

    private String name;

    private double evaluation;

    private String currentStateEvaluation;


    public Appraisal(String name, String currentStateEvaluation, double evaluation){
        setName(name);
        setEvaluation(evaluation);
        setCurrentStateEvaluation(currentStateEvaluation);
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

    public String getCurrentStateEvaluation() {
        return currentStateEvaluation;
    }

    public void setCurrentStateEvaluation(String currentState) {
        this.currentStateEvaluation = currentState;
    }

    @Override
    public String toString(){
        return "Appraisal [name:"+ getName()+", evaluation:"+getEvaluation()+", currentStateEvaluation:"+getCurrentStateEvaluation()+"]";
    }

}
