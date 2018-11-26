/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.behavior.asbac;

import java.util.List;

/**
 *
 * @author rgpolizeli
 */
public abstract class Drive{

    private String name;
    private List<String> propertiesNames;
    private List<ConsummatoryAffordanceType> consummatoryAffordances;
    private List<String> relevantPerceptsCategories;
    private double value = 0.0;
    private double minActivation = 0.0;
    private double maxActivation = 1.0;
    
    
    /**
     *
     * @param name Name to identify drive.
     * @param propertiesNames Properties names in self percept to get value of the drive
     * @param consummatoryAffordances The ConsummatoryAffordance's that realize this drive.
     * @param minActivation The minimum activation value of this drive.
     * @param maxActivation The maximum activation value of this drive.
     */
    public Drive(String name, List<String> propertiesNames, List<ConsummatoryAffordanceType> consummatoryAffordances, List<String> relevantPerceptsCategories, double minActivation, double maxActivation) {
        this.name = name;
        this.setRelevantPropertiesNames(propertiesNames);
        this.setConsummatoryAffordances(consummatoryAffordances);
        this.relevantPerceptsCategories = relevantPerceptsCategories;
        this.setMinActivation(minActivation);
        this.setMaxActivation(maxActivation);
    }
    
    /////////////////////
    // AUXILIARY METHODS //
    /////////////////////
    
    public String getName() {
        return name;
    }
    
    public List<String> getRelevantPropertiesNames() {
        return this.propertiesNames;
    }
    
    public List<ConsummatoryAffordanceType> getConsummatoryAffordances() {
        return this.consummatoryAffordances;
    }
    
    public List<String> getRelevantPerceptsCategories() {
        return this.relevantPerceptsCategories;
    }
    
    public double getValue(){
        return this.value;
    }
    
     public double getMaxActivation(){
        return this.maxActivation;
    }
    
    public double getMinActivation(){
        return this.minActivation;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setRelevantPropertiesNames(List<String> propertiesNames) {
        this.propertiesNames = propertiesNames;
    }
    
    public void setConsummatoryAffordances(List<ConsummatoryAffordanceType> consummatoryAffordances) {
        this.consummatoryAffordances = consummatoryAffordances;
    }

    public void setValue(double newValue){
        if (newValue > this.maxActivation) {
            newValue = this.maxActivation;
        } else if (newValue < this.minActivation) {
            newValue = this.minActivation;
        }
        this.value = newValue;
    }
    
    public void setMinActivation(double minActivation){
        this.minActivation = minActivation;
    }
    
    public void setMaxActivation(double maxActivation){
        this.maxActivation = maxActivation;
    }
    
    /////////////////////
    // ABSTRACT METHODS //
    /////////////////////
    
    
    /**
     * Define if a percept is relevant for the drive. For each types of percepts relevant to drive, it is necessary specify conditions  
     * to definy if it is or isn't relevant.
     * @param percept
     * @return 
     */
    public abstract boolean isRelevantPercept(Percept percept);
    
    /**
     * Compute the drive's value based on properties of the self percept and decrement or increment per cycle.
     * @param relevantProperties
     * @return 
     */
    public abstract double computeActivation(List<Property> relevantProperties);
    
}
