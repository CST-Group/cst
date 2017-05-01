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
    private List<Affordance> affordances;
    private String name;
    

    public AbstractObject(String name) {
        setName(name);
        properties = new ArrayList<Property>();
        compositeList = new ArrayList<AbstractObject>();
        aggregateList = new ArrayList<AbstractObject>();
        affordances = new ArrayList<>();
        
    }

   

    public AbstractObject(String name, List<Property> props) {
        setName(name);
        setProperties(props);
        compositeList = new ArrayList<AbstractObject>();
        aggregateList = new ArrayList<AbstractObject>();
        affordances = new ArrayList<>();

    }
    
    
    public AbstractObject(String name, List<Property> props, List<AbstractObject> composite, List<AbstractObject> aggregate) {
       
        setName(name);
        setCompositeParts(composite);
        setAggregatePart(aggregate);
        setProperties(props);
        affordances = new ArrayList<>();

    }
    
    public AbstractObject(String name, List<Property> props, List<AbstractObject> composite) {
       
        setName(name);
        setCompositeParts(composite);
        setProperties(props);
        affordances = new ArrayList<>();
    }
    
  
   public AbstractObject(List<AbstractObject> aggregate, List<Property> props, String name) {
        
        setName(name);
        setAggregatePart(aggregate);
        setProperties(props);
        affordances = new ArrayList<>();

    }
    
    public List<Object> search(String path) {
        path = path.trim();
        int dot = path.indexOf(".");
        String name = path;
        String subPath = null;
        if (dot > -1) {
            name = path.substring(0, dot);
            subPath = path.substring(dot + 1);
        }
        List<Object> results = new ArrayList<>();
        if (getName().equals(name)) {
            if (subPath != null) {
                results.addAll(search(subPath));
            } else {
                results.add(this);
            }
        }
        for (AbstractObject composite : compositeList) {
            if (composite.getName().equals(name)) {
                if (subPath != null) {
                    results.addAll(composite.search(subPath));
                } else {
                    results.add(composite);
                }
            }
            results.addAll(composite.search(path));
        }
        for (AbstractObject aggregate : aggregateList) {
            if (aggregate.getName().equals(name)) {
                if (subPath != null) {
                    results.addAll(aggregate.search(subPath));
                } else {
                    results.add(aggregate);
                }
            }
            results.addAll(aggregate.search(path));
        }
        for (Property property : properties) {
            if (property.getName().equals(name)) {
                if (subPath != null) {
                    results.addAll(property.search(subPath));
                } else {
                    results.add(property);
                }
            }
            results.addAll(property.search(path));
        }
        return results;
    }
    
    
    public void delete(String path) {
        String parentPath = null;
        String name = path;
        int dot = path.lastIndexOf(".");
        if (dot > -1) {
            parentPath = path.substring(0, dot);
            name = path.substring(dot + 1);
        }
        Object parent = this;
        List<Object> children = new ArrayList<>();
        if (parentPath != null) {
            List<Object> parents = search(parentPath);
            int p = parents.size();
            do {
                p--;
                parent = parents.get(p);
                if (parent instanceof AbstractObject) {
                    children = ((AbstractObject) parent).search(name);
                } else {
                    children = ((Property) parent).search(name);
                }
            } while (p > 0 && children.isEmpty());
        } else {
            children = search(name);
        }
        if (!children.isEmpty()) {
            Object child = children.get(children.size() - 1);
            if (parent instanceof AbstractObject) {
                AbstractObject ao = (AbstractObject) parent;
                if (child instanceof AbstractObject) {
                    ao.removeAggregatePart((AbstractObject) child);
                    ao.removeCompositePart((AbstractObject) child);
                } else {
                    ao.removeProperty((Property) child);
                }
            } else {
                ((Property) parent).deleteChild(child);
            }
        }
    }

    public Affordance detectAffordance(AbstractObject after) {
        for (Affordance affordance : getAffordances()) {
            if (affordance.getDetector().compare(this, after) == 1) {
                return affordance;
            }
        }
        return null;
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

    public void removeCompositePart(AbstractObject part) {
        compositeList.remove(part);
    }

    public List<AbstractObject> getAggregateParts() {
        return aggregateList;
    }

    public void setAggregatePart(List<AbstractObject> aggregatedList) {
        this.aggregateList = aggregatedList;
    }
    
    public void addAggregatePart(AbstractObject part) {
        aggregateList.add(part);
    }
    
    public void removeAggregatePart(AbstractObject part) {
        aggregateList.remove(part);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void removeProperty(Property prop) {
        properties.remove(prop);
    }

    public List<Affordance> getAffordances() {
        return affordances;
    }

    public void setAffordances(List<Affordance> affordances) {
        this.affordances = affordances;
    }

    @Override
    public AbstractObject clone() {
        List<Property> newProperties = new ArrayList<>();
        for (Property p : getProperties()) {
            newProperties.add(p.clone());
        }
        List<AbstractObject> newComposite = new ArrayList<>();
        for (AbstractObject wo : getCompositeParts()) {
            newComposite.add(wo.clone());
        }
        List<AbstractObject> newAggregate = new ArrayList<>();
        for (AbstractObject wo : getAggregateParts()) {
            newAggregate.add(wo.clone());
        }
        AbstractObject result = new AbstractObject(getName(), newProperties, newComposite, newAggregate);
        result.setAffordances(getAffordances());
        return result;
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
