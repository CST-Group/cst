/** *****************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * Contributors to this module:
 *     S. M. de Paula and R. R. Gudwin
 ***************************************************************************** */
package br.unicamp.cst.representation.owrl;

import br.unicamp.cst.motivational.Appraisal;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Suelen Mapa
 */
public class Configuration {

    private List<WorldObject> objects;
    //String name;
    private Appraisal appraisal;

    public Configuration(List<WorldObject> objs, Appraisal appraisal) {
        objects = objs;
        this.appraisal = appraisal;
    }

    public Configuration(List<WorldObject> objs) {
        objects = objs;
    }

    public Configuration() {
        objects = new ArrayList<>();
    }

    public List<WorldObject> getObjects() {
        return objects;
    }

    public void addObject(WorldObject obj) {
        objects.add(obj);
    }

    public boolean removeObject(int id) {
        for (int i = 0; i < objects.size(); ++i) {
            if (objects.get(i).getID() == id) {
                objects.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean changeObject(int id, WorldObject modifications) {
        for (int i = 0; i < objects.size(); ++i) {
            if (objects.get(i).getID() == id) {
                objects.get(i).modify(modifications);
                return true;
            }
        }
        return false;
    }

    public Appraisal getAppraisal() {
        return appraisal;
    }

    public void setAppraisal(Appraisal appraisal) {
        this.appraisal = appraisal;
    }

    // TODO: Implement clone() inside Appraisal and call it here
    public Configuration clone() {
        List<WorldObject> newObjects = new ArrayList<WorldObject>();
        for (WorldObject object : objects) {
            newObjects.add(object.clone());
        }
        return new Configuration(newObjects);
    }
}
