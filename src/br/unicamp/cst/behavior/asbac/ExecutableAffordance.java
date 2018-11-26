/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
