package br.unicamp.cst.representation.owrl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by du on 26/12/16.
 */
public class Category {

    private String name;
    private List<WorldObject> listOfWorldObjects;

    public Category(String name){
        this.setName(name);
        this.setListOfWorldObjects(new ArrayList<WorldObject>());
    }

    public Category(String name, List<WorldObject> listOfWorldObjects){
        this.setName(name);
        this.setListOfWorldObjects(listOfWorldObjects);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WorldObject> getListOfWorldObjects() {
        return listOfWorldObjects;
    }

    public void setListOfWorldObjects(List<WorldObject> listOfWorldObjects) {
        this.listOfWorldObjects = listOfWorldObjects;
    }
}
