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
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rgpolizeli
 */
public class Percept implements Cloneable{
    
    private String name;
    private String category;
    private List<Property> properties;
    private List<Relation> relations;
    private List<Property> keyProperties;
    private List<Relation> keyRelations;
    
    public Percept(String name, String category){
        //this.id = this.getId();
        this.name = name;
        this.category = category;
        
        this.keyProperties = new ArrayList<>();
        this.keyRelations = new ArrayList<>();
        this.properties = new ArrayList<>();
        this.relations = new ArrayList<>();
    }
    
    
    public void addProperty(Property prop){
        this.properties.add(prop);
    }
    
    public void addKeyProperty(Property keyProp){
        this.keyProperties.add(keyProp);
    }
    
    public void addRelation(Relation rel){
        this.relations.add(rel);
    }
    
    public void addKeyRelation(Relation keyRel){
        this.keyRelations.add(keyRel);
    }
    
    public void removeProperty(Property prop){
        this.properties.remove(prop);
    }
    
    public void removeKeyProperty(Property keyProp){
        this.keyProperties.remove(keyProp);
    }
    
    public List<Relation> getAllRelations(){
        return new ArrayList<>(this.relations);
    }
    
    public List<Relation> getAllKeyRelations(){
        return new ArrayList<>(this.keyRelations);
    }
    
    public int removeRelation(Relation relationToDelete){
        int removed = -1;
        
        for (Relation rel : this.getAllRelations()) {
            if (rel.getType().equals(relationToDelete.getType())) {
                this.relations.remove(rel);
                removed = 1;
            }
        }
        
        for (Relation rel : this.getAllKeyRelations()) {
            if (rel.getType().equals(relationToDelete.getType())) {
                this.keyRelations.remove(rel);
                removed = 1;
            }
        }
        
        return removed;
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getCategory(){
        return this.category;
    }
    
    public List<Property> getProperties(){
        return new ArrayList<>(this.properties);
    }
    
    public List<Relation> getRelations(){
        return new ArrayList<>(this.relations);
    }
    
    public Relation getRelationByType(String type){
        for (Relation rel : this.getRelations()) {
            if (rel.getType().equals(type)) {
                return rel;
            }
        }
        return null;
    }
    
    public List<Percept> getPerceptOfRelation(String relationName){
        for (Relation rel : this.getRelations()) {
            if (rel.getType().equals(relationName)) {
                return rel.getRelationPercepts();
            }
        }
        return null;
    }
    
    public Property getPropertyByType(String propertyType){
        List<Property> properties = this.getProperties();
        for (Property p: properties) {
            if (p.getType().equals(propertyType)) {
                return p;
            }
        }
        return null;
    }
    
    public Object getClone(){
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Percept.class.getName()).log(Level.SEVERE, null, ex);
            return this;
        }
    }
    
    public boolean hasPropertiesTypes(List<Property> targetProps){
        List<String> localPropertiesTypes = new ArrayList<>();
        List<String> targetPropertiesTypes = new ArrayList<>();
        
        for (Property prop : this.getProperties()) {
            localPropertiesTypes.add(prop.getType());
        }
        
        for (Property prop : targetProps) {
            targetPropertiesTypes.add(prop.getType());
        }
        
        return localPropertiesTypes.containsAll(targetPropertiesTypes);
    }
    
    @Override
    public boolean equals(Object otherPercept) {
        if (this == otherPercept) {
            return true;
        }
        if (otherPercept == null) {
            return false;
        }
        if (getClass() != otherPercept.getClass()) {
            return false;
        }
        final Percept targetPercept = (Percept) otherPercept;
        
        if (this.keyProperties.isEmpty()) {
            //error
        }
        
        for (Property keyProperty: this.keyProperties) {
            Property keyPropertyA = this.getPropertyByType(keyProperty.getType());
            Property keyPropertyB = targetPercept.getPropertyByType(keyProperty.getType());
            
            if (keyPropertyA == null || keyPropertyB == null) {
                return false;
            } else if (!keyPropertyA.equalsValue(keyPropertyB)) {
                return false;
            }
        }
        
        for (Relation keyRelation: this.keyRelations) {
            Relation keyRelationA = this.getRelationByType(keyRelation.getType());
            Relation keyRelationB = targetPercept.getRelationByType(keyRelation.getType());
            
            if (keyRelationA == null || keyRelationB == null) {
                return false;
            } else if (!keyRelationA.isEquals(keyRelationB)) {
                return false;
            }
        }
        
        
        return true;
    }

    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.keyProperties);
        hash = 29 * hash + Objects.hashCode(this.keyRelations);
        return hash;
    }
    
    
    public void setPropertyValue(String propertyType, String newValue){
        Property p = this.getPropertyByType(propertyType);
        try{
        p.setValue(newValue);}
        catch(Exception e){
            System.out.print("");
        }
    }
    
    /**
     * Replace relation if exist or add new relation otherwise.
     * @param currentRelationType
     * @param newRelation
     */
    public void replaceRelation(String currentRelationType, Relation newRelation){
        boolean replaced = false;
        
        for (int i =0; i < this.relations.size() && !replaced; i++) {
            Relation rel = this.relations.get(i);
            if (rel.getType().equals(currentRelationType)) {
                this.relations.set(i, newRelation);
                replaced = true;
            }
        }
        
        if (!replaced) {
            this.addRelation(newRelation);
        } 
    }
    
    
}
