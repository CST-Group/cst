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
public abstract class AffordanceType {
    
    private final String affordanceName;
    private final Map<String,List<String>> relevantPerceptsCategories; // one percept category can be filled with more than one type.
    private final String affordanceCodeletName;
    private final double minCost;
    private final double maxCost;
    private final boolean isConsummatory;
    
    private List<IntermediateAffordanceType> intermediateAffordances; //affordances that turn this affordance possible.
    private ComposeAffordanceType composeAffordance; //this affordance type is composed of other affordance type.
    
    public AffordanceType(String affordanceName, String affordanceCodeletName, boolean isConsummatory, double minCost, double maxCost){
        this.affordanceName = affordanceName;
        this.affordanceCodeletName = affordanceCodeletName;
        this.isConsummatory = isConsummatory;
        this.minCost = minCost;
        this.maxCost = maxCost;
        
        this.relevantPerceptsCategories = new HashMap<>();
        this.intermediateAffordances = new ArrayList<>();
        this.composeAffordance = null;
    }
    
    
    
    
    //////////////////////
    // AUXILIARY METHODS //
    //////////////////////
    
    public void addRelevantPerceptCategory(String relevantPerceptCategory, String perceptCategory){
        List<String> perceptCategories = this.relevantPerceptsCategories.get(relevantPerceptCategory);
        if (perceptCategories!=null) {
            perceptCategories.add(perceptCategory);
        } else{
            perceptCategories = new ArrayList<>();
            perceptCategories.add(perceptCategory);
            this.relevantPerceptsCategories.put(relevantPerceptCategory, perceptCategories);
        }
    }
    
    
    public void addIntermediateAffordance(IntermediateAffordanceType interAff){
        this.intermediateAffordances.add(interAff);
    }
    
    public void addComposeAffordance(ComposeAffordanceType compose){
        this.composeAffordance = compose;
    }
    
    public double normalize(double value, double max, double min){
        return ((value-min)/(max-min));
    }
    
    public boolean isConsummatory(){
        return this.isConsummatory;
    }
    
    public List<AffordanceType> getIntermediateAffordancesAsAffordancesList(){
        List<AffordanceType> affordances = new ArrayList<>();
        for (IntermediateAffordanceType iAff : this.getIntermediateAffordances()) {
            affordances.add(iAff.getAffordance());
        }
        return affordances;
    }
    
    public Map<String,List<String>> getRelevantPerceptsCategories(){
        return this.relevantPerceptsCategories;
    }
    
    public List<IntermediateAffordanceType> getIntermediateAffordances(){
        List<IntermediateAffordanceType> interAffordances = new ArrayList<>();
        interAffordances.addAll(this.intermediateAffordances);
        return interAffordances;
    }
    
    public String getAffordanceCodeletName(){
        return this.affordanceCodeletName;
    }
    
    public String getAffordanceName(){
        return this.affordanceName;
    }
    
    public double getActionMinCost(){
        return this.minCost;
    }
    
    public double getActionMaxCost(){
        return this.maxCost;
    }
    
    public ComposeAffordanceType getComposeAffordance(){
        return this.composeAffordance;
    }
    
    public void setIntermediateAffordances(List<IntermediateAffordanceType> intermediateAffordances){
        this.intermediateAffordances = intermediateAffordances;
    }
    
    public void setComposeAffordance(ComposeAffordanceType aff){
        this.composeAffordance = aff;
    }
    
    ////////////////////
    // ABSTRACT METHODS
    ///////////////////
    
    /**
     * Define if this affordance is executable for the actual context.
     * @param relevantPercepts
     * @return 
     */
    public abstract boolean isExecutable(Map<String, Percept> relevantPercepts);
    
    
    /**
     * Define if a percept is relevant for the affordance. For each types of percepts relevant to affordance, it is necessary specify conditions  
     * based on relevant percepts' properties.
     * @param percept
     * @return 
     */
    public abstract boolean isRelevantPercept(Percept percept);
    
    /**
     * Eliminates irrelevant percepts, it is necessary to RememberCodelet. 
     * @param relevantPerceptsPerType 
     */
    public abstract void eliminateIrrelevantPercepts(Map<String, List<Percept>> relevantPerceptsPerType);
    
    /**
     * Compute the cost to realize the affordance.
     * @param relevantPercepts
     * @return 
     */
    public abstract double calculateExecutionCost(Map<String, Percept> relevantPercepts);
   
    public double calculateNormalizedExecutionCost(Map<String, Percept> relevantPercepts){
        return normalize(calculateExecutionCost(relevantPercepts), this.maxCost, this.minCost);
    }
    
}
