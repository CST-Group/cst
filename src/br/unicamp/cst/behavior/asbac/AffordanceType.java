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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author rgpolizeli
 */
public abstract class AffordanceType {
    
    //OWN ATTRIBUTES
    private final String affordanceName;
    private final double minCost;
    private final double maxCost;
    
    //HIERARCHY ATTRIBUTES
    private final AffordanceType parent;
    private final List<AffordanceType> children; //affordances that turn this affordance possible.
    private final int level;
    private final Map<String,List<String>> relevantPerceptsCategories; // one percept category can be filled with more than one type.
    
    public AffordanceType(String affordanceName, double minCost, double maxCost, AffordanceType parent, int level){
        this.affordanceName = affordanceName;
        this.minCost = minCost;
        this.maxCost = maxCost;
        
        this.parent = parent;
        this.children = new ArrayList<>();
        this.level = level;
        this.relevantPerceptsCategories = new HashMap<>();
    }
    
    //////////////////////
    // AUXILIARY METHODS //
    //////////////////////
    
    public String getAffordanceName(){
        return this.affordanceName;
    }
    
    public double getActionMinCost(){
        return this.minCost;
    }
    
    public double getActionMaxCost(){
        return this.maxCost;
    }
    
    /**
     * Get the parent of this AffordanceType in its hierarchy.
     * @return a shallow AffordanceType.
     */
    public AffordanceType getParent(){
        return this.parent;
    }
    
    /**
     * Get the list of affordances types that can turn this affordance possible.
     * @return a shallow list of AffordanceType.
     */
    public List<AffordanceType> getChildren(){
        return children;
    }
    
    /**
     * Get the level of this AffordanceType in its hierarchy.
     * @return a int.
     */
    public int getLevel(){
        return this.level;
    }
    
    public Map<String,List<String>> getRelevantPerceptsCategories(){
        return this.relevantPerceptsCategories;
    }
    
    public void addChild(AffordanceType childAffordance){
        this.children.add(childAffordance);
    }
    
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
    
    public double normalize(double value, double max, double min){
        return ((value-min)/(max-min));
    }
    
    ////////////////////
    // ABSTRACT METHODS
    ///////////////////
    
    /**
     * Define if this affordance is executable for the actual context.
     * @param relevantPercepts
     * @return a boolean.
     */
    public abstract boolean isExecutable(Map<String, Percept> relevantPercepts);
    
    /**
     * Define if a percept is relevant for the affordance. For each types of percepts relevant to affordance, it is necessary specify conditions  
     * based on relevant percepts' properties.
     * @param percept
     * @return a boolean.
     */
    public abstract boolean isRelevantPercept(Percept percept);
    
    /**
     * Compute the cost to realize the affordance.
     * @param relevantPercepts
     * @return a double representing the cost.
     */
    public abstract double calculateExecutionCost(Map<String, Percept> relevantPercepts);
   
    /**
     * Compute the benefit of this affordance type to current situation.
     * @param situation
     * @param relevantPercepts
     * @return a double representing the benefit.
     */
    public abstract double computeAffordanceTypeBenefit(Map<String,List<Percept>> situation, Map<String,Percept> relevantPercepts);
    
    /**
     * Compute the percepts' benefits for the current situation. 
     * @param situation
     * @param relevantPercepts
     * @return a double representing the benefit.
     */
    public abstract double computeParametersBenefit(Map<String,List<Percept>> situation, Map<String,Percept> relevantPercepts);
}
