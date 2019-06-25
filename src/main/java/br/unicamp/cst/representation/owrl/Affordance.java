/*******************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     K. Raizer, A. L. O. Paraense, R. R. Gudwin - initial API and implementation
 ******************************************************************************/
package br.unicamp.cst.representation.owrl;

import java.util.HashMap;

/**
 * Created by du on 23/05/17.
 */
public abstract class Affordance {

    private String name;
    private HashMap<String, AbstractObject> aggregateDetectorObjects;
    private HashMap<String, AbstractObject> compositeDetectorObjects;
    private HashMap<String, Property> propertyDetectorObjects;

    public Affordance(String name) {
        setName(name);

    }

    public Affordance(String name, HashMap<String, AbstractObject> aggregateDetectorObjects, HashMap<String, AbstractObject> compositeDetectorObjects, HashMap<String, Property> propertyDetectorObjects) {
        setName(name);
        setAggregateDetectorObjects(aggregateDetectorObjects);
        setCompositeDetectorObjects(compositeDetectorObjects);
        setPropertyDetectorObjects(propertyDetectorObjects);

    }

    public abstract Object apply(String applyName, Object... args);

    public abstract Object detector(String detectorName, Object... args);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, AbstractObject> getAggregateDetectorObjects() {
        return aggregateDetectorObjects;
    }

    public void setAggregateDetectorObjects(HashMap<String, AbstractObject> aggregateDetectorObjects) {
        this.aggregateDetectorObjects = aggregateDetectorObjects;
    }

    public HashMap<String, AbstractObject> getCompositeDetectorObjects() {
        return compositeDetectorObjects;
    }

    public void setCompositeDetectorObjects(HashMap<String, AbstractObject> compositeDetectorObjects) {
        this.compositeDetectorObjects = compositeDetectorObjects;
    }

    public HashMap<String, Property> getPropertyDetectorObjects() {
        return propertyDetectorObjects;
    }

    public void setPropertyDetectorObjects(HashMap<String, Property> propertyDetectorObjects) {
        this.propertyDetectorObjects = propertyDetectorObjects;
    }
}
