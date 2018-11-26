/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.behavior.asbac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author rgpolizeli
 */
public abstract class ComposeAffordanceType {
    public AffordanceType affordance;
    public Map<String, Percept> composePermutation;
    public Map<IntermediateAffordanceType, Map<String, Percept>> intermediateParentAffordances; // it is necessary because for the same drive, for the same consummatotyPath, a composeAffordance can be child of a AffordanceType that is child of different AffordanceTypes.
    
    public ComposeAffordanceType(AffordanceType aff){
        this.affordance = aff;
        this.composePermutation = new HashMap<>();
        this.intermediateParentAffordances = new HashMap<>();
    }
    
    
    public Map<String,Percept> mountComposePermutation(Map<String,Percept> parentPermutation){
        Map<String,Percept> composePermutation = new HashMap<>();
        
        for (Map.Entry<String,Percept> entry : parentPermutation.entrySet()){
            String perceptCategory = entry.getKey();
            Percept percept = entry.getValue();
            
            if (this.getAffordance().isRelevantPercept(percept)) {
                composePermutation.put(perceptCategory, percept);
            }
        }
        
        return composePermutation;
    }
    
    public void setComposePermutation(Map<String, Percept> newComposePermutation){
        this.composePermutation = newComposePermutation;
    }
    
    public void addParentIntermediateAffordance(IntermediateAffordanceType interAff, int hierarchyContribution, Map<String, Percept> permutation){
        interAff.setHierarchyContribution(hierarchyContribution);
        if (this.intermediateParentAffordances.containsKey(interAff)) {
            this.intermediateParentAffordances.put(interAff, permutation);
        } else{
            this.intermediateParentAffordances.put(interAff, permutation);
        }
    }
    
    
    public AffordanceType getAffordance(){
        return this.affordance;
    }
    
    /**
     * Remove this intermediateAffordance from the ComposeAffordanceType.
     * @param interAff
     */
    public void removeIntermediateAffordance(IntermediateAffordanceType interAff, Map<String, Percept> parentPermutation){
        if (this.intermediateParentAffordances.get(interAff).equals(parentPermutation)) {
            this.intermediateParentAffordances.remove(interAff);
        }
    }
    
    public List<IntermediateAffordanceType> getIntermediateAffordances(){
        List<IntermediateAffordanceType> intermediateAffordances = new ArrayList<>(this.intermediateParentAffordances.keySet());
        return intermediateAffordances;
    }
    
    public Map<String, Percept> getParentPermutation(IntermediateAffordanceType interAff){
        return this.intermediateParentAffordances.get(interAff);
    }
    
    public Map<String, Percept> getComposePermutation(){
        return this.composePermutation;
    }
    
    @Override
    public boolean equals(Object composeObj){
        ComposeAffordanceType compose = (ComposeAffordanceType)composeObj;
        
        if (!compose.getAffordance().equals(this.getAffordance())) {
            return false;
        }
        if (!compose.getComposePermutation().equals(this.getComposePermutation())) {
            return false;
        }
        
        return true;
    }
    
    public abstract double computeComposeAffordanceTypeBenefitToParent(Map<String,List<Percept>> situation, Map<String,Percept> relevantPerceptsToCompose, Map<String,Percept> relevantPerceptsToParent);
    
    public abstract ComposeAffordanceType getClone();
}
