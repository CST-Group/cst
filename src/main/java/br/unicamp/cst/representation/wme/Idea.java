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

import br.unicamp.cst.util.viewer.ToString;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
//import java.lang.reflect.InaccessibleObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 *
 * @author rgudwin
 */
public class Idea {
    private String name="";
    private Object value="";
    private List<Idea> l= new CopyOnWriteArrayList<>();
    private int type=0;

    public Idea() {
        
    }
    
    public Idea(String name) {
        this.name = name;
    }
    
    public Idea(String name, Object value, int type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }
    
    public Idea(String name, Object value) {
        type = 1;
        this.name = name;
        if (value instanceof String) {
            String svalue = (String) value;
            try {
                int ivalue = Integer.parseInt(svalue);
                this.value = ivalue;
            } catch(Exception e) {
                try {
                    double dvalue = Double.parseDouble(svalue);
                    this.value = dvalue;
                } catch(Exception e2) {
                    this.value = svalue;
                }   
            } 
        }
        else this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Idea> getL() {
        return l;
    }

    public void setL(List<Idea> l) {
        this.l = l;
    }

    public Idea add(Idea node) {
        l.add(node);
        return(node);
    }
    
    public String toString() {
        return(name);
    }
    
    public String toStringFull() {
        listtoavoidloops = new ArrayList<>();
        return(toStringFull(1));
    }
    
    public String toStringPlus() {
        if (isType(1)) return("- "+name+": "+value);
        else {
            String appendix = "";
            if (value != null && !value.equals("")) appendix = " ["+value+"]";
            return("* "+ name+appendix);
        }            
    }
    
    public String toStringFull(int level) {
        String out; 
        if (isType(1)) {
           out = toStringPlus()+"\n";
           return out; 
        }
        else {
            out = toStringPlus()+"\n";
            listtoavoidloops.add(toStringPlus());
            for (Idea ln : l) {
                for (int i=0;i<level;i++) out += "   ";
                if (listtoavoidloops.contains(ln.toStringPlus()) || already_exists(ln.toStringPlus())) {
                    out += ln.toStringPlus()+"\n";
                }
                    
                else out += ln.toStringFull(level+1);
            }
            return(out);
        }
    }
    
    public List<Object> get(String path) {
        return(get(path,false));
    }
    
    public List<Object> get(String path, boolean value) {
        path = path.trim();
        int dot = path.indexOf(".");
        String name = path;
        String subPath = null;
        if (dot > -1) {
            name = path.substring(0, dot);
            subPath = path.substring(dot + 1);
        }
        List<Object> results = new ArrayList<>();
        for (Idea n : this.l) {
            if (n.getName().equals(name)) {
                if (subPath != null) {
                    results.addAll(n.get(subPath,value));
                } else {
                    if (value) results.add(n.getValue());
                    else results.add(n);
                }
            }
        }    
        return results;
    }    
    
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
        type = 1;
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public boolean isType(int type) {
        return(this.type==type);
    }
    
    public boolean isDouble() {
        String objectClassName = value.getClass().getName();
        if (objectClassName.equals("java.lang.Double"))
            return(true);
        return(false);   
    }
    
    public boolean isFloat() {
        String objectClassName = value.getClass().getName();
        if (objectClassName.equals("java.lang.Double"))
            return(true);
        return(false);   
    }
    
    public boolean isInteger() {
        String objectClassName = value.getClass().getName();
        if (objectClassName.equals("java.lang.Long"))
            return(true);
        return(false);   
    }
    
    public boolean isLong() {
        String objectClassName = value.getClass().getName();
        if (objectClassName.equals("java.lang.Long"))
            return(true);
        return(false);   
    }
    
    public boolean isNumber() {
        String objectClassName = value.getClass().getName();
        if (objectClassName.equals("java.lang.Float") || objectClassName.equals("java.lang.Double") || objectClassName.equals("java.lang.Integer") || objectClassName.equals("java.lang.Long"))
            return(true);
        return(false);
    }

    public boolean isHashMap(){
        String objectClassName = value.getClass().getName();
        if (objectClassName.equals("java.util.HashMap"))
            return(true);
        return(false);
    }
    
    public boolean isString() {
        String objectClassName = value.getClass().getName();
        if (objectClassName.equals("java.lang.String"))
            return(true);
        return(false);
    }
    
    public boolean isBoolean() {
        String objectClassName = value.getClass().getName();
        if (objectClassName.equals("java.lang.Boolean"))
            return(true);
        return(false);
    }

    public Idea clone() {
        Idea newnode;
           newnode = new Idea(getName(), getValue(), getType());
           newnode.l = new CopyOnWriteArrayList(newnode.l);
        return newnode;
    }
    
    public String getResumedValue() {
        String result; 
        if (isFloat() || isDouble()) {
            result = String.format("%4.1f",getValue());
        }
        else {
            try {
               int trial = Integer.parseInt(getValue().toString());
               result = String.format("%d",trial);
            } catch(Exception ee) {
               try { 
                    double trial = Double.parseDouble(getValue().toString());
                    result = String.format("%4.1f",trial);
               }
               catch(Exception e) {
                   result = getValue().toString();
               }
            }   
        }           
        return(result);
    }
    
    transient static ArrayList<Object> listtoavoidloops = new ArrayList<>();
    
    public boolean already_exists(Object o) {
        for (Object oo : listtoavoidloops)
           if (oo.hashCode() == o.hashCode()) return true;
        return false;
    }
    
    public void addObject(Object obj, String fullname) {
        if (obj == null) {
            Idea child = new Idea(ToString.getSimpleName(fullname),"null",0);
            add(child);
            return;
        }
        if (listtoavoidloops.contains(obj) || already_exists(obj)) {
             Idea child = new Idea(ToString.getSimpleName(fullname),obj.toString(),2);
             add(child);
             return;            
        }
        String s = ToString.from(obj);
        if (s != null) {
            Idea child = new Idea(ToString.getSimpleName(fullname),s,1);
            add(child);
            return;
        }
        else if (obj.getClass().isArray()) {
            int l = Array.getLength(obj);
            String type = obj.getClass().getSimpleName();
            if (l>0) {
                Object otype = Array.get(obj,0);
                if (otype != null)
                    type = otype.getClass().getSimpleName();
            }
            if (type.equalsIgnoreCase("Double") || type.equalsIgnoreCase("Integer") || 
                type.equalsIgnoreCase("String") || type.equalsIgnoreCase("Float") || 
                type.equalsIgnoreCase("Long") || type.equalsIgnoreCase("Boolean")) {
                Idea anode = new Idea(ToString.getSimpleName(fullname));
                for (int i=0;i<l;i++) {
                    Object oo = Array.get(obj,i);
                    Idea node = new Idea(ToString.el(ToString.getSimpleName(fullname), i),oo,1);
                    anode.add(node);
                }
                this.add(anode);
            } 
            else {
                Idea onode = new Idea(ToString.getSimpleName(fullname));
                for (int i=0;i<l;i++) {
                    Object oo = Array.get(obj,i);
                    onode.addObject(oo,ToString.el(ToString.getSimpleName(fullname), i));
                    listtoavoidloops.add(obj);
                }
                this.add(onode);
            }    
            return;
        }
        else if (obj instanceof List) {
            List ll = (List) obj;
            String label = "";
            if (ll.size() > 0) label = "List["+ll.size()+"] of "+ll.get(0).getClass().getSimpleName();
            else label = "List[0]";
            Idea onode = new Idea(ToString.getSimpleName(fullname));
            int i=0;
            for (Object o : ll) {
                onode.addObject(o,ToString.el(ToString.getSimpleName(fullname),i));
                listtoavoidloops.add(obj);
                i++;
            }
            this.add(onode);
            return;
        }
        else if (obj instanceof Idea) {
            Idea ao = (Idea) obj;
            this.add(ao);
            listtoavoidloops.add(obj);            
            return;
        }
        else {
            Idea ao = new Idea(ToString.getSimpleName(fullname));
            listtoavoidloops.add(obj);
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                String fname = field.getName();
                try {
                   if (!field.isAccessible()) field.setAccessible(true);
                   Object fo=null;
                    try {
                        fo = field.get(obj);
                    } catch (Exception e) {
                        e.printStackTrace();} 
                    if (fo != null && !already_exists(fo))
                        ao.addObject(fo,fullname+"."+fname);  
                } catch (Exception e) {
                }   
            }
            this.add(ao);
            return;
        }
    }
    
}
