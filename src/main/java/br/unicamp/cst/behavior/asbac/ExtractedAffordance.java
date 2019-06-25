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
import java.util.ArrayList;
import java.util.HashMap;
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

    private String affordanceName;
    private Map<String,Percept> perceptsPermutation;
    private Map<Drive,List<AffordanceType>> hierachiesNodes;
    
    public ExtractedAffordance(String affordanceName, Map<String, Percept> perceptsPermutation) {
        this.affordanceName = affordanceName;
        this.perceptsPermutation = perceptsPermutation;
        this.hierachiesNodes = new HashMap<>();
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
        if ( !this.affordanceName.equals(other.getAffordanceName()) ) {
            return false;
        }
        return Objects.equals(this.perceptsPermutation, other.perceptsPermutation);
    }
    
    public String getAffordanceName(){
        return this.affordanceName;
    }
    
    public Map<Drive,List<AffordanceType>> getHierachiesNodes(){
        return this.hierachiesNodes;
    }
    
    public Map<String, Percept> getPerceptsPermutation() {
        return this.perceptsPermutation;
    }
    
    /**
     * Create and add an hierarchy node to this extractedAffordance.
     * @param drive
     * @param affordanceType 
     */
    public void addHierarchyNode(Drive drive, AffordanceType affordanceType){
        List<AffordanceType> nodes = this.getHierachiesNodes().get(drive);
        
        if (nodes == null) {
            nodes = new ArrayList<>();
            nodes.add(affordanceType);
            this.getHierachiesNodes().put(drive,nodes);
        } else{
            if(nodes.indexOf(affordanceType) == -1)
                nodes.add(affordanceType); 
        }
    }
    
    /**
     * Remove an hierarchyNode. It is assumed that two AffordanceType objects are equals only if the same instance. 
     * @param drive
     * @param affordanceType 
     */
    public void removeHierarchyNode(Drive drive, AffordanceType affordanceType){
        List<AffordanceType> nodes = this.getHierachiesNodes().get(drive);
        if (nodes != null) {
            nodes.remove(affordanceType);
            if(nodes.isEmpty()){
                this.getHierachiesNodes().remove(drive);
            }
        }
    }
}
