/** *****************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * Contributors:
 *     S. M. de Paula and R. R. Gudwin
 ***************************************************************************** */
package br.unicamp.cst.representation.owrl;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author suelenmapa
 */
public class AbstractObject implements Cloneable {

    private List<Property> properties;//1-n;
    private List<AbstractObject> compositeList;//1-n;
    private List<AbstractObject> aggregateList;//1-n;
    private String name;
    private int ID;
    static int ncode = 0;

    public AbstractObject(String name) {
        setName(name);
        setID(ncode++);
        properties = new ArrayList<Property>();
        compositeList = new ArrayList<AbstractObject>();
        aggregateList = new ArrayList<AbstractObject>();
    }

    public AbstractObject(String name, int id) {
        setName(name);
        setID(id);
        properties = new ArrayList<Property>();
        compositeList = new ArrayList<AbstractObject>();
        aggregateList = new ArrayList<AbstractObject>();
    }

    public AbstractObject(String name, int id, List<Property> props) {
        setID(id);
        setName(name);
        setProperties(props);
        compositeList = new ArrayList<AbstractObject>();
        aggregateList = new ArrayList<AbstractObject>();

    }
    
    
    public AbstractObject(String name, int id, List<Property> props, List<AbstractObject> composite, List<AbstractObject> aggregate) {
        setID(id);
        setName(name);
        setCompositeParts(composite);
        setAggregatePart(aggregate);
        setProperties(props);

    }
    
    public AbstractObject(String name, int id, List<Property> props, List<AbstractObject> composite) {
        setID(id);
        setName(name);
        setCompositeParts(composite);
        setProperties(props);

    }
    
    //To do: est� dando conflito por causa do c�digo anterior. A assinatura dos m�todos
    // s�o iguais
  /*  public AbstractObject(String name, int id, List<Property> props, List<AbstractObject> aggregate) {
        setID(id);
        setId(name);
        setAggregatedPart(aggregate);
        setProperties(props);

    }*/
    
    

    public void modify(AbstractObject modifications) {
        for (Property prop_change : modifications.getProperties()) {
            for (Property prop_base : getProperties()) {
                if (prop_base.getName().compareTo(prop_change.getName()) == 0) {
                    // prop_base.setQualityDimension(prop_change.getQualityDimensions());
                }
            }
        }
    }

    public List<AbstractObject> getCompositeParts() {
        return compositeList;
    }

    public void setCompositeParts(List<AbstractObject> parts) {
        this.compositeList = parts;
    }

    public void addCompositePart(AbstractObject part) {
        compositeList.add(part);
    }

    public List<AbstractObject> getAggregatePart() {
        return aggregateList;
    }

    public void setAggregatePart(List<AbstractObject> aggregatedList) {
        this.aggregateList = aggregatedList;
    }
    
    public void addAggregatePart(AbstractObject part) {
        aggregateList.add(part);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> props) {
        this.properties = props;
    }

    public void addProperty(Property prop) {
        properties.add(prop);
    }

    @Override
    public AbstractObject clone() {
        List<Property> newProperties = new ArrayList<>();
        for (Property p : getProperties()) {
            newProperties.add(p.clone());
        }
        List<AbstractObject> newParts = new ArrayList<>();
        for (AbstractObject wo : getCompositeParts()) {
            newParts.add(wo.clone());
        }
        return new AbstractObject(getName(), getID(), newProperties, newParts);
    }
    
    public void deleteChild(Object child) {
        String childclass = child.getClass().getCanonicalName();
        //System.out.println("Childclass: "+childclass);
        if (childclass.equals("br.unicamp.cst.representation.owrl.AbstractObject")) {
            boolean cres = compositeList.remove(child);
            boolean ares = aggregateList.remove(child);
            //System.out.println("cres: "+cres+" ares: "+ares);
        }
        else if (childclass.equals("br.unicamp.cst.representation.owrl.Property")) {
            properties.remove(child);
        }
        
    }
}
