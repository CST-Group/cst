/***********************************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * K. Raizer, A. L. O. Paraense, E. M. Froes, R. R. Gudwin - initial API and implementation
 ***********************************************************************************************/
package br.unicamp.cst.representation.wme;

import br.unicamp.cst.util.TreeElement;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author rgudwin
 */
public class ExpandStateLibrary {
    
    static HashMap<String,Boolean> repository = new HashMap<>();
    
    public static boolean alreadyExists(String key) {
        Boolean expandstate = repository.get(key);
        if (expandstate != null) return(true);
        else return(false);
    }
    
    public static Boolean get(String identifier, String attribute, String value) {
        String key = identifier+"."+attribute+"."+value;
        Boolean retorno = repository.get(key);
        if (retorno == null) {
            set(key,false);
            return(false);
        }
        else return(retorno);
    }
    
//    public static Boolean get(WmeNode node) {
//        WmeNode parent = (WmeNode) node.getParent();
//        String identifier;
//        if (parent != null) identifier = ((TreeElement)parent.getUserObject()).getName();
//        else identifier = "";
//        String attribute = ((TreeElement)node.getUserObject()).getName();
//        String value = ((TreeElement)node.getUserObject()).getValue();
//        return(get(identifier,attribute,value));
//    }
    
    public static Boolean get(IdeaTreeNode node) {
        IdeaTreeNode parent = (IdeaTreeNode) node.getParent();
        String identifier;
        if (parent != null) identifier = ((TreeElement)parent.getUserObject()).getName();
        else identifier = "";
        String attribute = ((TreeElement)node.getUserObject()).getName();
        String value = ((TreeElement)node.getUserObject()).getValue();
        return(get(identifier,attribute,value));
    }
    
    public static void set(String identifier, String attribute, String value, boolean expandstate) {
        String key = identifier+"."+attribute+"."+value;
        set(key,expandstate);
    }
    
    public static void set(String key, boolean expandstate) {
        if (alreadyExists(key)) repository.replace(key, expandstate);
        repository.put(key,expandstate);
    }
    
//    public static void set(WmeNode node,boolean expandstate) {
//        WmeNode parent = (WmeNode) node.getParent();
//        String identifier;
//        if (parent != null) identifier = ((TreeElement)parent.getUserObject()).getName();
//        else identifier = "";
//        String attribute = ((TreeElement)node.getUserObject()).getName();
//        String value = ((TreeElement)node.getUserObject()).getValue();
//        set(identifier,attribute,value,expandstate);
//    }
    
    public static void set(IdeaTreeNode node,boolean expandstate) {
        IdeaTreeNode parent = (IdeaTreeNode) node.getParent();
        String identifier;
        if (parent != null) identifier = ((TreeElement)parent.getUserObject()).getName();
        else identifier = "";
        String attribute = ((TreeElement)node.getUserObject()).getName();
        String value = ((TreeElement)node.getUserObject()).getValue();
        set(identifier,attribute,value,expandstate);
    }
    
    public static int size() {
        return repository.size();
    }
    
    public static String dump() {
        String s = "";
        for (Map.Entry<String,Boolean> e : repository.entrySet()) {
                String ss = e.getKey();
                Boolean exp = e.getValue();
                s += ss+" "+exp+"\n";
        }
        return s;        
    }
    
}

