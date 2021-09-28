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
package br.unicamp.cst.util.viewer;

import br.unicamp.cst.representation.owrl.AbstractObject;
import br.unicamp.cst.representation.owrl.Affordance;
import br.unicamp.cst.representation.owrl.Property;
import br.unicamp.cst.representation.owrl.QualityDimension;
import br.unicamp.cst.representation.wme.Idea;
import br.unicamp.cst.util.TreeElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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
    
    public void delNode(String nodename) {
        DefaultMutableTreeNode node = updateMap.get(nodename);
        if (node != null) { // This means it found nodename in the Tree
            updateMap.remove(nodename);
            DefaultMutableTreeNode nodeparent = (DefaultMutableTreeNode) node.getParent();
            if (node.getParent() != null) {
                nodeparent.remove(node);
            }
            else System.out.println("Trying to remove node "+nodename+" but could not find its parent");
        }
    }
    
    public DefaultMutableTreeNode addItem(String fullname, String value, Object ob, int icon_type) {
        Object o = new TreeElement(fullname, value, TreeElement.NODE_NORMAL, ob, icon_type);
        DefaultMutableTreeNode memoryNode = new DefaultMutableTreeNode(o);
        updateMap.put(fullname,memoryNode);
        return (memoryNode);
    }
    
    public DefaultMutableTreeNode addString(String s, String fullname) {
        DefaultMutableTreeNode sNode = addItem(fullname,s,null,TreeElement.ICON_PROPERTY);
        return sNode;
    }
    
    public DefaultMutableTreeNode addObject(Object obj, String fullname) {
        if (obj == null) {
            DefaultMutableTreeNode node = addItem(fullname,"<NULL>",obj,TreeElement.ICON_OBJECT);
            return(node);
        }
        if (listtoavoidloops.contains(obj)) {
            DefaultMutableTreeNode node = addString(obj.toString(),fullname);
            return(node);            
        }
        String s = ToString.from(obj);
        if (s != null) {
            DefaultMutableTreeNode node = addString(s,fullname);
            return(node);
        }
        else if (obj.getClass().isArray()) {
            int l = Array.getLength(obj);
            String type = obj.getClass().getSimpleName();
            if (l>0) {
                Object otype = Array.get(obj,0);
                if (otype != null)
                    type = otype.getClass().getSimpleName();
            }
            DefaultMutableTreeNode objNode = addItem(fullname,"Array["+l+"] of "+type,obj,TreeElement.ICON_OBJECT);
            for (int i=0;i<l;i++) {
                Object oo = Array.get(obj,i);
                DefaultMutableTreeNode arr = addObject(oo,ToString.el(fullname, i));
                objNode.add(arr);
            }
            return(objNode);
        }
        else if (obj instanceof List) {
            List ll = new CopyOnWriteArrayList((List) obj);
            String label = "";
            if (ll.size() > 0) label = "List["+ll.size()+"] of "+ll.get(0).getClass().getSimpleName();
            else label = "List[0]";
            DefaultMutableTreeNode objNode = addItem(fullname,label,obj,TreeElement.ICON_OBJECT);
            int i=0;
            for (Object o : ll) {
                DefaultMutableTreeNode node = addObject(o,ToString.el(fullname,i));
                objNode.add(node);
                i++;
            }
            return(objNode);
        }
//        else if (obj instanceof Map) {
//            ConcurrentHashMap<? extends Object,? extends Object> ll = new ConcurrentHashMap<>((Map) obj);
//            ll.forEach((k,v)->(k.toString(),v.toString()));
//            String label = "";
//            Iterator<? extends Object> i = ll.
//            if (ll.size() > 0) label = "Map["+ll.size()+"] of "+ll.keySet().'.get(0).getClass().getSimpleName();
//            else label = "Map[0]";
//            DefaultMutableTreeNode objNode = addItem(fullname,label,obj,TreeElement.ICON_OBJECT);
//            int i=0;
//            for (Map.Entry<Object,Object> entry : ll.entrySet()) {
//                DefaultMutableTreeNode node = addObject(o,ToString.el(fullname,i));
//                objNode.add(node);
//                i++;
//            }
//            return(objNode);
//        }
        else if (obj instanceof AbstractObject) {
            AbstractObject ao = (AbstractObject) obj;
            DefaultMutableTreeNode objNode = addAbstractObject(fullname,ao,false); //addItem(fullname,ao.getName(),obj,TreeElement.ICON_OBJECT);
            listtoavoidloops.add(obj);            
            return(objNode);
        }
        else if (obj instanceof Idea) {
            Idea ao = (Idea) obj;
            DefaultMutableTreeNode objNode = addIdea(fullname,ao); 
            listtoavoidloops.add(obj);            
            return(objNode);
        }
        else {
            DefaultMutableTreeNode objNode = addItem(fullname,obj.toString(),obj,TreeElement.ICON_OBJECT);
            listtoavoidloops.add(obj);
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                String fname = field.getName();
                if (!field.isAccessible()) field.setAccessible(true);
                Object fo=null;
                try {
                    fo = field.get(obj);
                } catch (Exception e) {e.printStackTrace();}  
                DefaultMutableTreeNode fieldNode = addObject(fo,fullname+"."+fname);
                objNode.add(fieldNode);
            }
            return(objNode);
        }
    }
    
    // The following were included in order to visualize AbstractObjects
    
    private DefaultMutableTreeNode addAbstractObject(String fullname, AbstractObject wo, boolean composite) {
        DefaultMutableTreeNode objectNode;
        if (composite) objectNode = addItem(fullname,wo.getName(),wo,TreeElement.ICON_COMPOSITE); //new DefaultMutableTreeNode(new TreeElement(name, TreeElement.NODE_NORMAL, wo, TreeElement.ICON_COMPOSITE));
        else objectNode = addItem(fullname,wo.getName(),wo,TreeElement.ICON_AGGREGATE);//new DefaultMutableTreeNode(new TreeElement(name, TreeElement.NODE_NORMAL, wo, TreeElement.ICON_AGGREGATE));
        List<AbstractObject> parts = wo.getCompositeParts();
        for (AbstractObject oo : parts) {
            DefaultMutableTreeNode part = addAbstractObject(fullname+"."+oo.getName(),oo,true);
            objectNode.add(part);
        }
        List<AbstractObject> aggregates = wo.getAggregateParts();
        for (AbstractObject oo : aggregates) {
            DefaultMutableTreeNode part = addAbstractObject(fullname+"."+oo.getName(),oo,false);
            objectNode.add(part);
        }
        List<Property> props = wo.getProperties();
        for (Property p : props) {
            DefaultMutableTreeNode propertyNode = addProperty(fullname+"."+p.getName(),p);
            objectNode.add(propertyNode);
        }
        List<Affordance> affordances = wo.getAffordances();
        for (Affordance a : affordances) {
            DefaultMutableTreeNode propertyNode = addAffordance(fullname+"."+a.getName(),a);
            objectNode.add(propertyNode);
        }
        
        return(objectNode);    
    }
    
    
    
    private DefaultMutableTreeNode addProperty(String fullname, Property p) {
        DefaultMutableTreeNode propertyNode = addItem(fullname,p.getName(),p,TreeElement.ICON_PROPERTY);//new DefaultMutableTreeNode(new TreeElement(p.getName(), TreeElement.NODE_NORMAL, p, TreeElement.ICON_PROPERTY));
        int size = ((Property) p).getQualityDimensions().size();
        for (int s = 0; s < size; s++) {
            QualityDimension qd = ((Property) p).getQualityDimensions().get(s);
            String chave = qd.getName();
            String value = qd.getValue().toString();
            DefaultMutableTreeNode qualityDimensionNode = addItem(fullname+"."+qd.getName(),value,qd,TreeElement.ICON_QUALITYDIM);//new DefaultMutableTreeNode(new TreeElement(chave,value, TreeElement.NODE_NORMAL, qd, TreeElement.ICON_QUALITYDIM));
            propertyNode.add(qualityDimensionNode);
            

        }
        return(propertyNode);
    }
    
    private DefaultMutableTreeNode addAffordance(String fullname, Affordance a) {
        DefaultMutableTreeNode affordanceNode = addItem(fullname,a.getName(),a,TreeElement.ICON_PROPERTY);//new DefaultMutableTreeNode(new TreeElement(a.getName(), TreeElement.NODE_NORMAL, a, TreeElement.ICON_AFFORDANCE));
        return(affordanceNode);
    }
    
    
    // Beginnint of add functions for Ideas
    
    private DefaultMutableTreeNode addIdea(String fullname, Idea wo) {
        DefaultMutableTreeNode objectNode;
        switch(wo.getType()) {
            default:
            case 0: objectNode = addItem(fullname,wo.getValue().toString(),wo,TreeElement.ICON_OBJECT3);
                    break;
            case 1: objectNode = addItem(fullname,wo.getValue().toString(),wo,TreeElement.ICON_QUALITYDIM);
                    break;
            case 2: objectNode = addItem(fullname,wo.getValue().toString(),wo,TreeElement.ICON_OBJECT2);
                    break;
        }
        if (wo.getType() == 0)
        for (Idea oo : wo.getL()) {
            DefaultMutableTreeNode part = addIdea(fullname+"."+oo.getName(),oo);
            objectNode.add(part);
        }
        return(objectNode);
    }    
    
}
