package br.unicamp.cst.representation.owrl;

import java.util.HashMap;

/**
 * Created by du on 23/05/17.
 */
public abstract class Affordance {

    private String name;
    private HashMap<String, AbstractObject> aggregateObjects;
    private HashMap<String, AbstractObject> compositeObjects;
    private HashMap<String, Property> modifiedProperties;

    public Affordance(String name) {
        setName(name);

    }

    public Affordance(String name, HashMap<String, AbstractObject> aggregateObjects,  HashMap<String, AbstractObject> compositeObjects, HashMap<String, Property> modifiedProperties) {
        setName(name);
        setAggregateObjects(aggregateObjects);
        setCompositeObjects(compositeObjects);
        setModifiedProperties(modifiedProperties);

    }

    public abstract Object apply(String applyName, Object... args);

    public abstract Object detector(String detectorName, Object... args);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, AbstractObject> getAggregateObjects() {
        return aggregateObjects;
    }

    public void setAggregateObjects(HashMap<String, AbstractObject> aggregateObjects) {
        this.aggregateObjects = aggregateObjects;
    }

    public HashMap<String, AbstractObject> getCompositeObjects() {
        return compositeObjects;
    }

    public void setCompositeObjects(HashMap<String, AbstractObject> compositeObjects) {
        this.compositeObjects = compositeObjects;
    }

    public HashMap<String, Property> getModifiedProperties() {
        return modifiedProperties;
    }

    public void setModifiedProperties(HashMap<String, Property> modifiedProperties) {
        this.modifiedProperties = modifiedProperties;
    }
}
