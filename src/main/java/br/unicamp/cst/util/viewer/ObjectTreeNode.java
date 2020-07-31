/**
 * ********************************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * K. Raizer, A. L. O. Paraense, E. M. Froes, R. R. Gudwin - initial API and implementation
 * *********************************************************************************************
 */
package br.unicamp.cst.util.viewer;

import br.unicamp.cst.util.TreeElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author rgudwin
 */
public class ObjectTreeNode extends DefaultMutableTreeNode {
    
    HashMap<String,DefaultMutableTreeNode> updateMap = new HashMap<>();
    ArrayList<Object> listtoavoidloops = new ArrayList<>();
    
    
    public ObjectTreeNode(String name, int icon_type) {
        super(new TreeElement(name, TreeElement.NODE_NORMAL, name, icon_type));
    }
    
    public DefaultMutableTreeNode addItem(String name, String value, Object ob, int icon_type) {
        Object o = new TreeElement(name, value, TreeElement.NODE_NORMAL, ob, icon_type);
        DefaultMutableTreeNode memoryNode = new DefaultMutableTreeNode(o);
        updateMap.put(((TreeElement)memoryNode.getUserObject()).getName(), memoryNode);
        return (memoryNode);
    }
    
    public DefaultMutableTreeNode addNumber(Number n,String name) {
        String s="";
        if (n instanceof Long || n instanceof Integer) {
            long i = (long) n; 
            s = String.format("%d",i);
        }
        else if (n instanceof Float || n instanceof Double) {
            double d = (double) n;
            s = String.format("%4.2f", d);
        }
        else if (n instanceof Byte) {
            byte b = (byte) n;
            s = String.format("%x", b);
        }
        return(addString(s,name));
    }
    
    public DefaultMutableTreeNode addInteger(int i, String name) {
        DefaultMutableTreeNode iNode = addItem(name,String.format("%d",i),null,TreeElement.ICON_PROPERTY);
        return iNode;
    }
    
    public DefaultMutableTreeNode addFloat(float f, String name) {
        DefaultMutableTreeNode fNode = addItem(name,String.format("%4.2f",f),null,TreeElement.ICON_PROPERTY);
        return fNode;
    }
    
    public DefaultMutableTreeNode addString(String s, String name) {
        DefaultMutableTreeNode sNode = addItem(name,s,null,TreeElement.ICON_PROPERTY);
        return sNode;
    }
    
    public DefaultMutableTreeNode addObject(Object obj, String name) {
        if (listtoavoidloops.contains(obj)) {
            DefaultMutableTreeNode node = addString(obj.toString(),name);
            return(node);            
        }
        else if (obj == null) {
            DefaultMutableTreeNode node = addString("NULL",name);
            return(node);
        }
        else if (obj instanceof Boolean) {
            DefaultMutableTreeNode node;
            if ((boolean)obj == true) node = addString("true",name);
            else node = addString("false",name);
            return(node);
        }
        else if (obj instanceof Number) {
            DefaultMutableTreeNode node = addNumber((Number)obj,name);
            return(node);
        }
        else if (obj instanceof String) {
            DefaultMutableTreeNode node = addString((String)obj,name);
            return(node);            
        }
        else if (obj instanceof float[]) {
            DefaultMutableTreeNode objNode = addItem(name,"",obj,TreeElement.ICON_OBJECT);
            float fo[] = (float[]) obj;
            for (int i=0;i< fo.length;i++) {
                DefaultMutableTreeNode arr = addFloat(fo[i],name+"["+i+"]");
                objNode.add(arr);
            }
            return(objNode);
        }
        else if (obj instanceof double[]) {
            DefaultMutableTreeNode objNode = addItem(name,"",obj,TreeElement.ICON_OBJECT);
            double fo[] = (double[]) obj;
            for (int i=0;i< fo.length;i++) {
                DefaultMutableTreeNode arr = addFloat(((Double)fo[i]).floatValue(),name+"["+i+"]");
                objNode.add(arr);
            }
            return(objNode);
        }
        else if (obj instanceof List) {
            DefaultMutableTreeNode objNode = addItem(name,"",obj,TreeElement.ICON_OBJECT);
            List ll = (List) obj;
            int i=0;
            for (Object o : ll) {
                DefaultMutableTreeNode node = addString(o.toString(),name+"["+i+"]");
                objNode.add(node);
                i++;
            }
            return(objNode);
        }
        else {
            DefaultMutableTreeNode objNode = addItem(name,"",obj,TreeElement.ICON_OBJECT);
            listtoavoidloops.add(obj);
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                String fname = field.getName();
                if (!field.isAccessible()) field.setAccessible(true);
                Object fo=null;
                try {
                    fo = field.get(obj);
                } catch (Exception e) {e.printStackTrace();}  
                DefaultMutableTreeNode fieldNode = addObject(fo,fname);
                objNode.add(fieldNode);
            }
            return(objNode);
        }
    }
    
}
