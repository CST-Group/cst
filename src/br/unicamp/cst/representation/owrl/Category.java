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
 * Created by du on 26/12/16.
 */
public class Category {

    private String name;
    private List<AbstractObject> listOfWorldObjects;

    public Category(String name){
        this.setName(name);
        this.setListOfWorldObjects(new ArrayList<AbstractObject>());
    }

    public Category(String name, List<AbstractObject> listOfWorldObjects){
        this.setName(name);
        this.setListOfWorldObjects(listOfWorldObjects);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AbstractObject> getListOfWorldObjects() {
        return listOfWorldObjects;
    }

    public void setListOfWorldObjects(List<AbstractObject> listOfWorldObjects) {
        this.listOfWorldObjects = listOfWorldObjects;
    }
}
