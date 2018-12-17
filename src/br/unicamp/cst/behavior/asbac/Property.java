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
