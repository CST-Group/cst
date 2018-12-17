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

import br.unicamp.cst.motivational.Drive;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author rgpolizeli
 */
public class CountCodelet extends Codelet{

    private Map<ExtractedAffordance, Double> extractedAffordancesActivations;
    private Map<ExtractedAffordance, Double> drivesIncrements;

    private MemoryObject extractedAffordancesMO;
    private MemoryObject activatedAffordanceMO;
    private MemoryObject workingMO;
    private MemoryObject synchronizerMO;
    
    private List<ExtractedAffordance> extractedAffordances;
    private ExtractedAffordance activatedAffordance;
    private Map<String, List<Percept>> workingMemory;
    
    private double maxAffordanceActivation;
    private double minAffordanceActivation;
    private double activationThreshold;
    private double decrementPerCount;
    private double deleteThreshold;
    
    public CountCodelet(double maxAffordanceActivation, double minAffordanceActivation,double activationThreshold, double decrementPerCount) {
        this.maxAffordanceActivation = maxAffordanceActivation;
        this.minAffordanceActivation = minAffordanceActivation;
        this.activationThreshold = activationThreshold;
        this.decrementPerCount = decrementPerCount;
        this.deleteThreshold = this.minAffordanceActivation;
        
        this.extractedAffordancesActivations = new HashMap<>();
    }
    

    //////////////////////
    // AUXILIARY METHODS //
    //////////////////////
    
    
    private double computeLocalBenefit(ExtractedAffordance extAff, ConsummatoryPathInfo consummatoryPath, Drive factor){
        
        double localBenefit = 0.0;
        List<ComposeAffordanceType> composeAffordances = consummatoryPath.getComposeAffordancesToDrive(factor);
        if (!composeAffordances.isEmpty()) { //compose affordance
            for (ComposeAffordanceType compAff : composeAffordances) {
                for (IntermediateAffordanceType interAff : compAff.getIntermediateAffordances()) {
                    
                    localBenefit += factor.getActivation() * 
                        (
                            ( 
                                compAff.computeComposeAffordanceTypeBenefitToParent(this.workingMemory, compAff.getComposePermutation(), compAff.getParentPermutation(interAff)) + 
                                interAff.computeAffordanceTypeBenefit(this.workingMemory, compAff.getParentPermutation(interAff)) +
                                interAff.computeParametersBenefit(this.workingMemory, compAff.getParentPermutation(interAff))
                            )/3
                            -
                            interAff.getAffordance().calculateExecutionCost(compAff.getParentPermutation(interAff))
                        )
                    ;
                    
                }
            }
            
        } else{ //NOT compose affordance
            for (IntermediateAffordanceType interAff : consummatoryPath.getIntermediateAffordancesToDrive(factor)) {
                
                localBenefit += factor.getActivation() *  
                        (
                            (
                                interAff.computeAffordanceTypeBenefit(this.workingMemory,extAff.getPerceptsPermutation()) + 
                                interAff.computeParametersBenefit(this.workingMemory, extAff.getPerceptsPermutation())
                            )/2 
                            -
                            interAff.getAffordance().calculateExecutionCost(extAff.getPerceptsPermutation())
                        )
                ;
            }
        }
        
      
        return localBenefit;
    }
    
    private double computeGlobalBenefit(ExtractedAffordance extAff, ConsummatoryAffordanceType consummatoryAffordance, Drive factor){
        
        double globalBenefit = 0.0;
        
        if (extAff.getAffordanceType().equals((AffordanceType) consummatoryAffordance)) {
            globalBenefit += factor.getActivation() *  
                (
                    (
                        consummatoryAffordance.calculateConsummatoryAffordanceTypeBenefit(factor, this.workingMemory, extAff.getPerceptsPermutation()) + 
                        consummatoryAffordance.computeParametersBenefit(this.workingMemory, extAff.getPerceptsPermutation())
                    )/2 
                    -
                    consummatoryAffordance.calculateExecutionCost(extAff.getPerceptsPermutation())
                )
            ;
        } 
        
        return globalBenefit;
    }
    
    private ExtractedAffordance getExtractedAffordanceInMap(ExtractedAffordance extAff){
        for (Map.Entry<ExtractedAffordance, Double> entry : this.extractedAffordancesActivations.entrySet()) {
            if (entry.getKey().getAffordanceType().getAffordanceName().equals(extAff.getAffordanceType().getAffordanceName()) && entry.getKey().getPerceptsPermutation().equals(extAff.getPerceptsPermutation())) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    private void count(){
        double activation;
        
        for (ExtractedAffordance extAff : this.extractedAffordances) {
            
            ExtractedAffordance extAffInMap = getExtractedAffordanceInMap(extAff);
            
            if (extAffInMap!=null) {
                activation = this.extractedAffordancesActivations.get(extAffInMap);
            } else{
                activation = 0.0;
                extAffInMap = extAff;
            }
            
            if (this.drivesIncrements.containsKey(extAffInMap)) {
                activation += this.drivesIncrements.get(extAffInMap);
            }
               
            this.refreshVotes(extAffInMap, activation);
        }
    }
    
    private void computeDrivesIncrements(){
        
        this.drivesIncrements = new HashMap<>(); //renew in each cycle
        double increment;
        
        for (ExtractedAffordance extAff : this.extractedAffordances) {
            increment = 0.0;
            
            ExtractedAffordance extAffInMap = getExtractedAffordanceInMap(extAff);
            if (extAffInMap==null) {
                extAffInMap = extAff;
            }
            
            List<ConsummatoryPathInfo> paths = extAffInMap.getConsummatoryPaths();
            
            if (paths.isEmpty()) {
                this.extractedAffordancesActivations.remove(extAffInMap);
            }else{
                if (extAff.getAffordanceType().isConsummatory()) {
                    List<ConsummatoryPathInfo> consummatoryPathsBkp = new ArrayList<>(extAff.getConsummatoryPaths());
                    for (ConsummatoryPathInfo consummatoryPath : consummatoryPathsBkp) {
                        for (Drive factor: consummatoryPath.getDrives()) {
                            increment += this.computeGlobalBenefit(extAff, consummatoryPath.getConsummatoryAffordance(), factor);
                        }
                    }
                } else{
                    
                    List<ConsummatoryPathInfo> consummatoryPathsBkp = new ArrayList<>(extAff.getConsummatoryPaths());
                    for (ConsummatoryPathInfo consummatoryPath : consummatoryPathsBkp) {
                        for (Drive factor: consummatoryPath.getDrives()) {
                            increment += this.computeLocalBenefit(extAff, consummatoryPath, factor);
                        }
                    }
                    
                }
                this.drivesIncrements.put(extAffInMap, increment); 
            }
        }
    }
    
    private void refreshVotes(ExtractedAffordance aff, double newVotes){
        
        if (newVotes > this.maxAffordanceActivation) {
            newVotes = this.maxAffordanceActivation;
        }
      
        this.extractedAffordancesActivations.put(aff, newVotes);
    }
    
    private boolean isExtractedAffordanceExecutable(ExtractedAffordance extAff){
        
        for (ConsummatoryPathInfo consummatoryPath : extAff.getConsummatoryPaths()) {
            for (Drive factor: consummatoryPath.getDrives()) {
                List<ComposeAffordanceType> composeAffordances = consummatoryPath.getComposeAffordancesToDrive(factor);
                if (!composeAffordances.isEmpty()) { //compose affordance
                    for (ComposeAffordanceType compAff : composeAffordances) {
                        for (IntermediateAffordanceType interAff : compAff.getIntermediateAffordances()) {
                            
                            if (interAff.getAffordance().isExecutable(compAff.getParentPermutation(interAff)) && interAff.computeAffordanceTypeBenefit(workingMemory, compAff.getParentPermutation(interAff)) > 0.0) {
                                return true;
                            }
                        }
                    }
                } else{ //NOT compose affordance
                    
                    if (extAff.getAffordanceType().isConsummatory()) {
                        ConsummatoryAffordanceType consummatoryAffordance = consummatoryPath.getConsummatoryAffordance();
                        if (extAff.getAffordanceType().equals((AffordanceType) consummatoryAffordance)) {
                            if (consummatoryAffordance.isExecutable(extAff.getPerceptsPermutation()) && consummatoryAffordance.calculateConsummatoryAffordanceTypeBenefit(factor, workingMemory, extAff.getPerceptsPermutation()) > 0.0) {
                                return true;
                            }
                        } 
                    } else{
                        for (IntermediateAffordanceType interAff : consummatoryPath.getIntermediateAffordancesToDrive(factor)) {
                            if (interAff.getAffordance().isExecutable(extAff.getPerceptsPermutation()) && interAff.computeAffordanceTypeBenefit(workingMemory, extAff.getPerceptsPermutation()) > 0.0) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    private ExtractedAffordance getWinner(){
        
        double maxActivation = -1.0;
        ExtractedAffordance maxExtractedAffordance = null;
        
        for (Map.Entry<ExtractedAffordance, Double> entry : this.extractedAffordancesActivations.entrySet()) {
            
            if (entry.getValue() >= this.activationThreshold && entry.getValue() > maxActivation && this.isExtractedAffordanceExecutable(entry.getKey())) {
                maxActivation = entry.getValue();
                maxExtractedAffordance = entry.getKey();
            }
            
        }
        
        return maxExtractedAffordance;
    }
    
    public <K,V> Map<K,V> deepCopyMap(Map<K,V> m){
        synchronized(m){
            Map<K, V> copy = new HashMap<>();
            for(Map.Entry<K,V> entry : m.entrySet()){
                copy.put( entry.getKey(),entry.getValue() );
            }
            return copy;
        }
    }
    
    private void mountWorkingMemory(){
        this.workingMemory = new HashMap<>();
        
        synchronized(this.workingMO){
            Map<String, Map<String,List<Percept>>> workingMemoryContent = (Map<String, Map<String,List<Percept>>>) this.workingMO.getI();
            if(workingMemoryContent != null && !workingMemoryContent.isEmpty()){
                for(Map<String,List<Percept>> workingMemoryItem : workingMemoryContent.values()){
                    
                    for(Map.Entry<String,List<Percept>> e : workingMemoryItem.entrySet()){
                        List<Percept> perceptsOfCategoryInWMO = this.workingMemory.get(e.getKey());
                        if(perceptsOfCategoryInWMO != null){
                            for(Percept p : e.getValue()){
                                if(!perceptsOfCategoryInWMO.contains(p)){
                                    perceptsOfCategoryInWMO.add(p);
                                }
                            }
                        } else{
                            perceptsOfCategoryInWMO = new ArrayList<>(e.getValue());
                            this.workingMemory.put(e.getKey(), perceptsOfCategoryInWMO);
                        }
                    }
                }      
            }
        }
    }
    
    private void decrementActivationMap(){
        Map<ExtractedAffordance, Double> extractedAffordancesActivationsBkp = this.deepCopyMap(this.extractedAffordancesActivations);
        
        for (Map.Entry<ExtractedAffordance, Double> entry : extractedAffordancesActivationsBkp.entrySet()) {
            double newActivation = entry.getValue() - this.decrementPerCount;
            if (newActivation <= this.deleteThreshold) {
                this.extractedAffordancesActivations.remove(entry.getKey());
                if (this.extractedAffordances.contains(entry.getKey())) {
                    synchronized(this.extractedAffordancesMO){
                        List<ExtractedAffordance> currentExtractedAffordances = (List) this.extractedAffordancesMO.getI();
                        currentExtractedAffordances.remove(entry.getKey());
                        this.extractedAffordances = new CopyOnWriteArrayList(currentExtractedAffordances);
                    }
                }
            } else{
                this.extractedAffordancesActivations.put(entry.getKey(), newActivation);
            }
        }

    }
    
    private void selectWinner(){
        synchronized(this.activatedAffordanceMO){
            if (this.activatedAffordance == null) {
                this.activatedAffordance = this.getWinner();
                this.activatedAffordanceMO.setI(this.activatedAffordance);
            }
        }
    }
    
    //////////////////////
    // OVERRIDE METHODS //
    //////////////////////
    
    
    @Override
    public void accessMemoryObjects() {
        this.extractedAffordancesMO = (MemoryObject) this.getInput(MemoryObjectsNames.EXTRACTED_AFFORDANCES_MO);
        this.activatedAffordanceMO = (MemoryObject) this.getInput(MemoryObjectsNames.ACTIVATED_AFFORDANCE_MO);
        this.workingMO = (MemoryObject) this.getInput(MemoryObjectsNames.WORKING_MO);
        this.synchronizerMO = (MemoryObject) this.getInput(MemoryObjectsNames.SYNCHRONIZER_MO);
    }

    @Override
    public void calculateActivation() {}

    @Override
    public void proc() {
        
        this.extractedAffordances = new CopyOnWriteArrayList( (List<ExtractedAffordance>) this.extractedAffordancesMO.getI() );
        
        if (!this.extractedAffordances.isEmpty()){

            mountWorkingMemory();
            
            if (!this.workingMemory.isEmpty()) {
                this.activatedAffordance = (ExtractedAffordance) this.activatedAffordanceMO.getI();

                decrementActivationMap();
                computeDrivesIncrements();
                count();
                selectWinner();
            }
        }
        
        SynchronizationMethods.synchronize(super.getName(), this.synchronizerMO);
    }
    
}
