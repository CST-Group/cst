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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author suelenmapa
 */
public final class Property {

    private List<QualityDimension> qualityDimensions;//1-n

    private String Name;

    public Property(String name) {
        setName(name);
        qualityDimensions = new ArrayList<>();
    }
    
    public Property(String name, QualityDimension qd) {
        this(name);
        addQualityDimension(qd);
    }

    public Property(String name, List<QualityDimension> qd) {
        setName(name);
        qualityDimensions = qd;
    }

    Property() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<QualityDimension> getQualityDimensions() {
        return qualityDimensions;
    }

    public void addQualityDimension(QualityDimension qd) {
        qualityDimensions.add(qd);
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    @Override
    public Property clone() {
        List<QualityDimension> newQualityDimension = new ArrayList<>();
        for (QualityDimension qd : qualityDimensions) {
            newQualityDimension.add(qd.clone());
        }
        return new Property(getName(), newQualityDimension);
    }
    
    public void deleteChild(Object child) {
        String childclass = child.getClass().getCanonicalName();
        //System.out.println("Childclass: "+childclass);
        if (childclass.equals("br.unicamp.cst.representation.owrl.QualityDimension")) {
            boolean qdres = qualityDimensions.remove(child);
            //System.out.println("qdres: "+qdres);
        }
    }
    
    public List<Object> search(String name) {
        List<Object> results = new ArrayList<>();
        for (QualityDimension qd : qualityDimensions) {
            if (qd.getName().equals(name)) {
                results.add(qd);
            }
        }
        return results;
    }

}
