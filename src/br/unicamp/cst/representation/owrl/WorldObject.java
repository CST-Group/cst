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
public class WorldObject implements Cloneable {

    private List<Property> properties;//1-n;
    private List<WorldObject> parts;//1-n;
    private String name;
    private int ID;
    static int ncode = 0;

    public WorldObject(String name) {
        setName(name);
        setID(ncode++);
        properties = new ArrayList<Property>();
        parts = new ArrayList<WorldObject>();
    }

    public WorldObject(String name, int id) {
        setName(name);
        setID(id);
        properties = new ArrayList<Property>();
        parts = new ArrayList<WorldObject>();
    }

    public WorldObject(String name, int id, List<Property> props) {
        setID(id);
        setName(name);
        setProperties(props);
        parts = new ArrayList<WorldObject>();

    }

    public WorldObject(String name, int id, List<Property> props, List<WorldObject> parts) {
        setID(id);
        setName(name);
        setParts(parts);
        setProperties(props);

    }

    public void modify(WorldObject modifications) {
        for (Property prop_change : modifications.getProperties()) {
            for (Property prop_base : getProperties()) {
                if (prop_base.getName().compareTo(prop_change.getName()) == 0) {
                    // prop_base.setQualityDimension(prop_change.getQualityDimensions());
                }
            }
        }
    }

    public List<WorldObject> getParts() {
        return parts;
    }

    public void setParts(List<WorldObject> parts) {
        this.parts = parts;
    }

    public void addPart(WorldObject part) {
        parts.add(part);
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
    public WorldObject clone() {
        List<Property> newProperties = new ArrayList<>();
        for (Property p : getProperties()) {
            newProperties.add(p.clone());
        }
        List<WorldObject> newParts = new ArrayList<>();
        for (WorldObject wo : getParts()) {
            newParts.add(wo.clone());
        }
        return new WorldObject(getName(), getID(), newProperties, newParts);
    }
}
