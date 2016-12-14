/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.representation.owrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author suelenmapa
 */
public class Property {

    private Map<String, Object> qualityDimensions;//1-n

    private String Name;

    public Property(String name) {
        setName(name);
        qualityDimensions = new HashMap<String, Object>();
    }

    public Property(String name, Map<String, Object> ob) {
        setName(name);
        qualityDimensions = ob;
    }

    public Map<String, Object> getProperty() {
        return qualityDimensions;
    }

    public List<Object> getValuesOfDimensionsTogether() {

        List<Object> o = new ArrayList<>();

        Set<String> dimensionName = qualityDimensions.keySet();
        for (String key : dimensionName) {
            o.add(qualityDimensions.get(key));
        }

        return o;
    }

    public void setQualityDimension(String name, Object object) {
        getProperty().put(name, object);
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public Property clone() {
        HashMap<String, Object> newQualityDimensions = (HashMap<String, Object>) ((HashMap<String, Object>) qualityDimensions).clone();
        return new Property(getName(), newQualityDimensions);
    }

}
