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
