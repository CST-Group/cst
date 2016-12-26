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
public class Configuration {
    
     private List<WorldObject> objects = new ArrayList<>();//1-n.
     //String name;
     
     
     public Configuration (List<WorldObject> objs){
         objects = objs;
     }

    public List<WorldObject> getObjects() {
        return objects;
    }
    
}
