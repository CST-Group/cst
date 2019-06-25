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

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author rgpolizeli
 */
public class Relation {
    public String name;
    public LinkedList<Percept> percepts;
    
    public Relation(String name){
        this.name = name;
        this.percepts = new LinkedList<>();
    }
    
    
    
    public String getType(){
        return this.name;
    }
    
    public void setType(String newType){
        this.name = newType;
    }
    
    public List<Percept> getRelationPercepts(){
        return this.percepts;
    }
    
    public boolean isPerceptInRelation(Percept targetPercept){
        for (Percept p : this.getRelationPercepts()) {
            if (p.equals(targetPercept)) {
                return true;
            }
        }
        return false;
    }
    
    public void addPercept(Percept p){
        this.percepts.add(p);
    }
    
    public void removePercept(Percept p){
        this.percepts.remove(p);
    }

    public boolean isEquals(Relation targetRelation){
        if (!this.getType().equals(targetRelation.getType())) {
            return false;
        }
        
        return this.getRelationPercepts().containsAll(targetRelation.getRelationPercepts());
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
        final Relation other = (Relation) obj;
        
        return this.isEquals(other);
    }

    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.name);
        hash = 79 * hash + Objects.hashCode(this.percepts);
        return hash;
    }
   
    
}
