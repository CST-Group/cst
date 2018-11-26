/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.behavior.asbac;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author rgpolizeli
 */
public abstract class ConsummatoryAffordanceType extends AffordanceType{
   
    private List<Drive> drives;
    
    
    public ConsummatoryAffordanceType(String affordanceName, String affordanceCodeletName, double minCost, double maxCost){
        super(affordanceName, affordanceCodeletName, true, minCost, maxCost);
        this.drives = new ArrayList<>();
    }
    
    
    //////////////////////
    // AUXILIARY METHODS //
    //////////////////////
    
    public void addDrive(Drive factor){
        this.drives.add(factor);
    }
    
    @Override
    public List<AffordanceType> getIntermediateAffordancesAsAffordancesList(){
        return super.getIntermediateAffordancesAsAffordancesList();
    }
    
    @Override
    public Map<String,List<String>> getRelevantPerceptsCategories(){
        return super.getRelevantPerceptsCategories();
    }
    
    @Override
    public List<IntermediateAffordanceType> getIntermediateAffordances(){
        return super.getIntermediateAffordances();
    }
    
    @Override
    public ComposeAffordanceType getComposeAffordance(){
        return super.getComposeAffordance();
    }
    
    @Override
    public String getAffordanceCodeletName(){
        return super.getAffordanceCodeletName();
    }
    
    @Override
    public String getAffordanceName(){
        return super.getAffordanceName();
    }
    
    @Override
    public double getActionMinCost(){
        return super.getActionMinCost();
    }
    
    @Override
    public double getActionMaxCost(){
        return super.getActionMaxCost();
    }
    
    public List<Drive> getDrives(){
        return this.drives;
    }
    
    @Override
    public void setIntermediateAffordances(List<IntermediateAffordanceType> intermediateAffordances){
        super.setIntermediateAffordances(intermediateAffordances);
    }
    
    @Override
    public void setComposeAffordance(ComposeAffordanceType aff){
        super.setComposeAffordance(aff);
    }
    
    ////////////////////
    // ABSTRACT METHODS
    ///////////////////
    
    /**
     * Compute the percepts' benefits for the current situation. 
     * @param situation
     * @param relevantPercepts
     * @return
     */
    public abstract double computeParametersBenefit(Map<String,List<Percept>> situation, Map<String,Percept> relevantPercepts);
    
    /**
     * Compute the AffordanceType's benefit to each Drive realized by this consummatory affordance.
     * @param factor
     * @param situation
     * @param relevantPercepts
     * @return 
     */
    public abstract double calculateConsummatoryAffordanceTypeBenefit(Drive factor, Map<String,List<Percept>> situation, Map<String,Percept> relevantPercepts);
    
    
    /**
     * Define if this affordance is executable for the actual context.
     * @param relevantPercepts
     * @return 
     */
    @Override
    public abstract boolean isExecutable(Map<String, Percept> relevantPercepts);
    
    
    /**
     * Define if a percept is relevant for the affordance. For each this relevant percept type of the affordance, it is necessary specify conditions  
     * based on relevant percept type' properties.
     * @param percept
     * @return 
     */
    @Override
    public abstract boolean isRelevantPercept(Percept percept);
    
    /**
     * Compute the cost to realize the affordance.
     * @param relevantPercepts
     * @return 
     */
    @Override
    public abstract double calculateExecutionCost(Map<String, Percept> relevantPercepts);
    
    @Override
    public double calculateNormalizedExecutionCost(Map<String, Percept> relevantPercepts){
        return super.normalize(calculateExecutionCost(relevantPercepts), super.getActionMaxCost(), super.getActionMinCost());
    }
}
