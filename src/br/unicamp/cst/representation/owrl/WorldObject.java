/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.representation.owrl;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author suelenmapa
 */
public class WorldObject implements Cloneable {

    private List<Property> properties;//1-n;
    private List<WorldObject> parts;//1-n;
    private String name;
    private int ID;

    public WorldObject(String name, List<Property> props, int id) {

        setID(id);
        setName(name);
        setProperties(props);
        setParts(null);

    }

    public WorldObject(String name, int id, List<WorldObject> parts, List<Property> props) {

        setID(id);
        setName(name);
        setParts(parts);
        setProperties(props);

    }

    public List<WorldObject> getParts() {
        return parts;
    }

    public void setParts(List<WorldObject> parts) {
        this.parts = parts;
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

    @Override
    public WorldObject clone() {
        List<Property> newProperties = new ArrayList<>();
        for (Property p : getProperties()) {
            newProperties.add(p.clone());
        }
        return new WorldObject(getName(), newProperties, getID());
    }
}
