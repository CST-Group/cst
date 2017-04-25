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

    private String id;

    private double evaluation;

    private String currentState;


    public Appraisal(String name, String currentState, double evaluation){
        setId(name);
        setEvaluation(evaluation);
        setCurrentState(currentState);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

}
