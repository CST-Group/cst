/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.behavior.asbac;

import br.unicamp.cst.motivational.Drive;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author rgpolizeli
 */
public class ConsummatoryPathInfo {

    private ConsummatoryAffordanceType consummatoryAffordance;
    private Map<Drive, List<IntermediateAffordanceType>> affordanceRolesMap;
    private Map<Drive, List<ComposeAffordanceType>> affordanceComposeMap;

    public ConsummatoryPathInfo(ConsummatoryAffordanceType consummatoryAffordance) {
        this.consummatoryAffordance = consummatoryAffordance;
        this.affordanceRolesMap = new HashMap<>();
        this.affordanceComposeMap = new HashMap<>();
    }
   
    public void addDecisionFactor(Drive factor){
        if (!this.affordanceRolesMap.containsKey(factor)) {
            this.affordanceRolesMap.put(factor, new ArrayList<>());
        }
    }
    
    public void removeDecisionFactor(Drive factor){
        this.affordanceComposeMap.remove(factor);
    }
    
    public void addAffordanceRole(Drive factor, IntermediateAffordanceType interAff, int hierarchyContribution){
        interAff.setHierarchyContribution(hierarchyContribution);
        
        if (this.affordanceRolesMap.containsKey(factor)) {
            List<IntermediateAffordanceType> interAffs = this.affordanceRolesMap.get(factor);
            if (!interAffs.contains(interAff)) {
                interAffs.add(interAff);
            }
        } else{
            List<IntermediateAffordanceType> interAffs = new ArrayList<>();
            interAffs.add(interAff);
            this.affordanceRolesMap.put(factor, interAffs);
        }
    }
    
    public void addComposeAffordance(Drive factor, IntermediateAffordanceType interAff, int hierarchyContribution, Map<String, Percept> parentPermutation, ComposeAffordanceType composeAff){
        if (this.affordanceComposeMap.containsKey(factor)) {
            List<ComposeAffordanceType> composeAffs = this.affordanceComposeMap.get(factor);
            int composePosition = composeAffs.indexOf(composeAff);
            if (composePosition == -1) {
                composeAff.addParentIntermediateAffordance(interAff, hierarchyContribution, parentPermutation);
                composeAffs.add(composeAff);
            } else{
                composeAffs.get(composePosition).addParentIntermediateAffordance(interAff, hierarchyContribution, parentPermutation);
            }
        } else{
            List<ComposeAffordanceType> composeAffs = new ArrayList<>();
            composeAff.addParentIntermediateAffordance(interAff, hierarchyContribution, parentPermutation);
            composeAffs.add(composeAff);
            this.affordanceComposeMap.put(factor, composeAffs);
        }
    }
    
    public ComposeAffordanceType getComposeAffordance(Drive factor, AffordanceType composeAffordanceType, Map<String, Percept> composePermutation){
        
        for (ComposeAffordanceType compose : this.getComposeAffordancesToDrive(factor)) {
            if (compose.getAffordance().equals(composeAffordanceType) && compose.getComposePermutation().equals(composePermutation)) {
                return compose;
            }
        }
        
        return null;
    }
    
    public void removeComposeAffordance(Drive factor, AffordanceType composeAffordanceType, Map<String, Percept> composePermutation){
       List<ComposeAffordanceType> composeAffordancesBkp = new ArrayList<>(this.affordanceComposeMap.get(factor));
        
        for (ComposeAffordanceType compose : composeAffordancesBkp) {
            if (compose.getAffordance().equals(composeAffordanceType) && compose.getComposePermutation().equals(composePermutation)) {
                this.affordanceComposeMap.get(factor).remove(compose);
            }
        }
    }
    
    public ConsummatoryAffordanceType getConsummatoryAffordance() {
        return this.consummatoryAffordance;
    }
    
    public List<Drive> getDrives(){ 
        //return new ArrayList<>(this.affordanceRolesMap.keySet());
        List<Drive> drivesList = new ArrayList<>();
        
        HashSet <Drive> drivesSet = new HashSet<>();
        drivesSet.addAll(this.affordanceRolesMap.keySet());
        drivesSet.addAll(this.affordanceComposeMap.keySet());
        
        drivesList.addAll(drivesSet);
        
        return drivesList;
    }
    
    public List<IntermediateAffordanceType> getIntermediateAffordancesToDrive(Drive factor){
        return this.affordanceRolesMap.get(factor);
    }
    
    public List<ComposeAffordanceType> getComposeAffordancesToDrive(Drive factor){
        if (this.affordanceComposeMap.containsKey(factor)) {
            return this.affordanceComposeMap.get(factor);
        } else{
            return new ArrayList<>();
        }   
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.consummatoryAffordance);
        return hash;
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
        final ConsummatoryPathInfo other = (ConsummatoryPathInfo) obj;
        if (!Objects.equals(this.consummatoryAffordance, other.consummatoryAffordance)) {
            return false;
        }
        return true;
    }
    
}
