/**
 * ****************************************************************************
 * Copyright (c) 2018  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * R. G. Polizeli and R. R. Gudwin
 * ****************************************************************************
 */
package br.unicamp.cst.behavior.asbac;

/**
 *
 * @author rgpolizeli
 */
public class ExecutableAffordance {

    private AffordanceType affordance;
    private Integer hierarchyContribution;
    private IntermediateAffordanceType intermediateAffordance;
    
    public ExecutableAffordance(AffordanceType affordance, Integer hierarchyContribution, IntermediateAffordanceType intermediateAffordance) {
        this.affordance = affordance;
        this.hierarchyContribution = hierarchyContribution;
        this.intermediateAffordance = intermediateAffordance;
    }
    
    public AffordanceType getAffordance() {
        return affordance;
    }

    public void setAffordance(AffordanceType affordance) {
        this.affordance = affordance;
    }

    public Integer getHierarchyContribution() {
        return hierarchyContribution;
    }

    public void setHierarchyContribution(Integer hierarchyContribution) {
        this.hierarchyContribution = hierarchyContribution;
    }

    public IntermediateAffordanceType getIntermediateAffordance() {
        return intermediateAffordance;
    }

    public void setIntermediateAffordance(IntermediateAffordanceType intermediateAffordance) {
        this.intermediateAffordance = intermediateAffordance;
    }
}
