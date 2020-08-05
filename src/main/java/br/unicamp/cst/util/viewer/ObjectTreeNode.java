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

import br.unicamp.cst.util.TimeStamp;
import br.unicamp.cst.util.TreeElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
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
        if (n instanceof Long) {
            long i = (long) n; 
            s = String.format("%d",i);
        }
        else if (n instanceof Integer) {
            int i = (int) n; 
            s = String.format("%d",i);
        }
        else if (n instanceof Float) {
            float d = (float) n;
            s = String.format("%4.2f", d);
        }
        else if (n instanceof Double) {
            double d = (double) n;
            s = String.format("%4.2f", d);
        }
        else if (n instanceof Byte) {
            byte b = (byte) n;
            s = String.format("%x", b);
        }
        return(addString(s,name));
    }
    
    public DefaultMutableTreeNode addDate(Date d, String name) {
        String date = TimeStamp.getStringTimeStamp(d.getTime(),"dd/MM/yyyy HH:mm:ss.SSS");
        return(addString(date,name));
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
        else if (obj instanceof Date) {
            DefaultMutableTreeNode node = addDate((Date)obj,name);
            return(node);            
        }
        else if (obj.getClass().isArray()) {
            int l = Array.getLength(obj);
            String type = obj.getClass().getSimpleName();
            if (l>0) type = Array.get(obj,0).getClass().getSimpleName();
            DefaultMutableTreeNode objNode = addItem(name,"Array["+l+"] of "+type,obj,TreeElement.ICON_OBJECT);
            for (int i=0;i<l;i++) {
                Object oo = Array.get(obj,i);
                DefaultMutableTreeNode arr = addObject(oo,name+"["+i+"]");
                objNode.add(arr);
            }
            return(objNode);
        }
        else if (obj instanceof List) {
            List ll = (List) obj;
            String label = "";
            if (ll.size() > 0) label = "List["+ll.size()+"] of "+ll.get(0).getClass().getSimpleName();
            else label = "List[0]";
            DefaultMutableTreeNode objNode = addItem(name,label,obj,TreeElement.ICON_OBJECT);
            int i=0;
            for (Object o : ll) {
                DefaultMutableTreeNode node = addObject(o,name+"["+i+"]");
                objNode.add(node);
                i++;
            }
            return(objNode);
        }
        else {
            DefaultMutableTreeNode objNode = addItem(name,obj.toString(),obj,TreeElement.ICON_OBJECT);
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
