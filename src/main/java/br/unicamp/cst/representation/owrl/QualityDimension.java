/*******************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors to this module:
 *     S. M. de Paula and R. R. Gudwin 
 ******************************************************************************/

package br.unicamp.cst.representation.owrl;

import java.util.HashMap;

/**
 *
 * @author suelenmapa
 */
public class QualityDimension implements Entity {

    private String name;
    private Object value;

    public QualityDimension(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public QualityDimension(String name) {
        this.name = name;
        this.value = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
    public boolean isDouble() {
        String objectClassName = value.getClass().getName();
        if (objectClassName.equals("java.lang.Double"))
            return(true);
        return(false);   
    }
    
    public boolean isLong() {
        String objectClassName = value.getClass().getName();
        if (objectClassName.equals("java.lang.Long"))
            return(true);
        return(false);   
    }
    
    public boolean isNumber() {
        String objectClassName = value.getClass().getName();
        if (objectClassName.equals("java.lang.Float") || objectClassName.equals("java.lang.Double") || objectClassName.equals("java.lang.Integer") || objectClassName.equals("java.lang.Long"))
            return(true);
        return(false);
    }

    public boolean isHashMap(){
        String objectClassName = value.getClass().getName();
        if (objectClassName.equals("java.util.HashMap"))
            return(true);
        return(false);
    }
    
    public boolean isString() {
        String objectClassName = value.getClass().getName();
        if (objectClassName.equals("java.lang.String"))
            return(true);
        return(false);
    }
    
    public boolean isBoolean() {
        String objectClassName = value.getClass().getName();
        if (objectClassName.equals("java.lang.Boolean"))
            return(true);
        return(false);
    }

    protected QualityDimension clone() {
        return new QualityDimension(getName(), getValue());
    }
    
    public String toString(int level) {
        String out="";
        out += name+" : "+value+"\n";
        return out;
    }

}
