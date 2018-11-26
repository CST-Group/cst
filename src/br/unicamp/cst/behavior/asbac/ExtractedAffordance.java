/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.behavior.asbac;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rgpolizeli
 */
public class ExtractedAffordance implements Cloneable{

    private AffordanceType affordance;
    private Map<String,Percept> perceptsPermutation;
    private List<ConsummatoryPathInfo> consummatoryPaths;
    
    public ExtractedAffordance(AffordanceType affordance, Map<String, Percept> perceptsPermutation, int hierarchyContribution) {
        this.affordance = affordance;
        this.perceptsPermutation = perceptsPermutation;
        this.consummatoryPaths = new ArrayList<>();
    }

    
    public Object getClone(){
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(ExtractedAffordance.class.getName()).log(Level.SEVERE, null, ex);
            return this;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExtractedAffordance other = (ExtractedAffordance) obj;
        if ( !this.affordance.getAffordanceName().equals(other.getAffordanceType().getAffordanceName()) ) {
            return false;
        }
        if ( !Objects.equals(this.perceptsPermutation, other.perceptsPermutation) ) {
            return false;
        }
        return true;
    }
    
    
    
    public AffordanceType getAffordanceType(){
        return this.affordance;
    }
    
    public List<ConsummatoryPathInfo> getConsummatoryPaths(){
        return this.consummatoryPaths;
    }
    
    public Map<String, Percept> getPerceptsPermutation() {
        return perceptsPermutation;
    }
    
    public ConsummatoryPathInfo getConsummatoryPathInfo(ConsummatoryAffordanceType consummatoryAffordance){
        for (ConsummatoryPathInfo pathInfo : this.getConsummatoryPaths()) {
            if (pathInfo.getConsummatoryAffordance().equals(consummatoryAffordance)) {
                return pathInfo;
            }
        }
        return null;
    }
    
    
    /**
     * Create and add a consummatoryPath to this extractedAffordance.
     * @param consummatoryAffordance
     * @param factor
     * @param interAff
     * @param hierarchyContribution 
     */
    
    public void addConsummatoryPath(ConsummatoryAffordanceType consummatoryAffordance, Drive factor, IntermediateAffordanceType interAff, int hierarchyContribution){
        
        ConsummatoryPathInfo path = this.getConsummatoryPathInfo(consummatoryAffordance);
        
        if (path == null) {
            path = new ConsummatoryPathInfo(consummatoryAffordance);
            this.getConsummatoryPaths().add(path);
        }
        
        if (interAff != null) { //aff isn't consummatory
            path.addAffordanceRole(factor,interAff,hierarchyContribution);
        } else{
            path.addDecisionFactor(factor);
        }
        
    }
    
    /**
     * Create and add a consummatoryPath to this extractedAffordance.
     * @param consummatoryAffordance
     * @param factor
     * @param interAff
     * @param hierarchyContribution
     * @param parentPermutation
     * @param compAff
     * @param composePermutation 
     */
    public void addConsummatoryPath(ConsummatoryAffordanceType consummatoryAffordance, Drive factor, IntermediateAffordanceType interAff, int hierarchyContribution, Map<String, Percept> parentPermutation, ComposeAffordanceType compAff, Map<String, Percept> composePermutation){
        ConsummatoryPathInfo path = this.getConsummatoryPathInfo(consummatoryAffordance);
        
        if (path == null) { 
            path = new ConsummatoryPathInfo(consummatoryAffordance);
            this.getConsummatoryPaths().add(path);
        } else{ //replace clone composeAffordance with already added composeAffordance because the intermediates Affordances associated with composeAffordance Object.
            compAff = path.getComposeAffordance(factor, compAff.getAffordance(), composePermutation);
        }
        
        path.addComposeAffordance(factor,interAff, hierarchyContribution, parentPermutation, compAff);
    }
    
    /**
     * Check if this IntermediateAffordanceType is the only in ComposeAffordanceType. For it, this method remove the IntermediateAffordanceType from ComposeAffordanceType and return .isEmpty() on IntermediateAffordanceType list.
     * @return
     */
    public boolean hasIntermediateInComposeAffordance(){
        
        if (this.getConsummatoryPaths().isEmpty()) {
            return false;
        } else{
            for (ConsummatoryPathInfo path : this.getConsummatoryPaths()) {
                if (this.getDrives().isEmpty()) {
                    return false;
                } else{
                    for (Drive factor : this.getDrives()) {
                        for (ComposeAffordanceType composeAffordance : path.getComposeAffordancesToDrive(factor)) {
                            if(!composeAffordance.getIntermediateAffordances().isEmpty()){
                                return true;
                            }
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    public void refreshIntermediateAffordanceInComposeAffordance(ConsummatoryAffordanceType consummatoryAffordance, Drive factor, IntermediateAffordanceType interAff, Map<String, Percept> parentPermutation, AffordanceType composeAffordanceType, Map<String, Percept> composePermutation){
        ConsummatoryPathInfo path = this.getConsummatoryPathInfo(consummatoryAffordance);
        if (path!=null) {
            ComposeAffordanceType composeAffordance = path.getComposeAffordance(factor, composeAffordanceType, composePermutation);
            if (composeAffordance != null) {
                composeAffordance.removeIntermediateAffordance(interAff, parentPermutation);
                if (composeAffordance.getIntermediateAffordances().isEmpty()) {
                    path.removeComposeAffordance(factor, composeAffordanceType, composePermutation);
                    if (path.getComposeAffordancesToDrive(factor).isEmpty()) {
                        path.removeDecisionFactor(factor);
                        if (path.getDrives().isEmpty()) {
                            this.removeConsummatoryPathInfo(path);
                        }
                    }

                }
            }
        }
    }
    
    public void removeConsummatoryPathInfo(ConsummatoryPathInfo path){
        this.consummatoryPaths.remove(path);
    }    
        
    public List<Drive> getDrives(){
        List<Drive> drives = new ArrayList<>();
        
        for (ConsummatoryPathInfo pathInfo : this.getConsummatoryPaths()) {
            drives.addAll(pathInfo.getDrives());
        }
        
        return drives;
    }
    
}
