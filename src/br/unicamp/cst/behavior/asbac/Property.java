/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.behavior.asbac;

import java.util.Objects;

/**
 *
 * @author rgpolizeli
 */
public class Property {
    public String name;
    public String value;
    
    public Property(String name, String value){
        this.name = name;
        this.value = value;
    }

    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.name);
        hash = 89 * hash + Objects.hashCode(this.value);
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
        final Property other = (Property) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }
    
    
    
    public String getType(){
        return this.name;
    }
    
    public String getValue(){
        return this.value;
    }
    
    public boolean equalsType(Property p){
        return this.getType().equals(p.getType());
    }
    
    public boolean equalsValue(Property targetProperty){
        return this.getValue().equals(targetProperty.getValue());
    }
    
    public void setValue(String newValue){
        this.value = newValue;
    }
}
