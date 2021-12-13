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

import br.unicamp.cst.motivational.ToString;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 *
 * @author rgudwin
 */
public class Idea {
    private long id;
    private String name="";
    private Object value="";
    private List<Idea> l= new CopyOnWriteArrayList<>();
    private int type=0;
    private IdeaComparator ideaComparator = new IdeaComparator();
    public static ConcurrentHashMap<String,Idea> repo = new ConcurrentHashMap<>();
    transient static CopyOnWriteArrayList<Object> listtoavoidloops = new CopyOnWriteArrayList<>();
    private static long lastId = 0;
    
    public static long genId() {
        return(lastId++);
    }
    
    public Idea() {
        id = genId();
    }
    
    public Idea(String name) {
        this.name = name;
        id = genId();
    }
    
    public Idea(String name, Object value, int type) {
        this.name = name;
        if (value != null) this.value = value;
        else this.value = "null";
        this.type = type;
        id = genId();
    }
    
    public Idea(String name, Object value) {
        type = 1;
        this.name = name;
        id = genId();
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
    
    public synchronized static Idea createIdea(String name, Object value, int type) {
        Idea ret = repo.get(name+"."+type);
        if (ret == null) {
            ret = new Idea(name,value,type);
            repo.put(name+"."+type, ret);
        }
        else if (ret.getType() != type) {
            ret = new Idea(name,value,type);
        }
        else { 
            ret.setValue(value);
            ret.l= new CopyOnWriteArrayList<>();
        }    
        return(ret);
    }

    public String getName() {
        return ToString.getSimpleName(name);
    }
    
    public String getFullName() {
        return(name);
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
        sort();
        return(node);
    }
    
    public static void reset() {
        listtoavoidloops = new CopyOnWriteArrayList<>();
    }
    
    public String toString() {
        return(name);
    }
    
    public String toStringFull() {
        return(toStringFull(false));
    }
    
    public String toStringFull(boolean withid) {
        reset();
        return(toStringFull(1,withid));
    }
    
    public String toStringPlus(boolean withid) {
        String appendix = "";
        String out;
        switch(getType()) {
            default:
            case 0: if (value != null && !value.equals("")) appendix = " ["+value+"]";
                    if (withid) appendix += " <"+id+">";
                    out = "* "+ getName()+appendix;
                    break;
            case 1: if (withid) appendix += " <"+id+">"; 
                    out = "- "+getName()+": "+value+appendix;
                    break;
            case 2: if (value != null && !value.equals("")) appendix = " ["+value+"]";
                    if (withid) appendix += " <"+id+">";
                    appendix += " #";
                    out = "* "+ getName()+appendix;
        }           
        return(out);
                    
    }
    
    public String toStringFull(int level, boolean withid) {
        String out; 
        if (isType(1)) {
           out = toStringPlus(withid)+"\n";
           return out; 
        }
        else {
            out = toStringPlus(withid)+"\n";
            listtoavoidloops.add(toStringPlus(withid));
            for (Idea ln : l) {
                for (int i=0;i<level;i++) out += "   ";
                if (listtoavoidloops.contains(ln.toStringPlus(withid)) || already_exists(ln.toStringPlus(withid))) {
                    out += ln.toStringPlus(withid)+"\n";
                }
                    
                else out += ln.toStringFull(level+1,withid);
            }
            return(out);
        }
    }
    
    public Idea get(String path) {
        String[] spath = path.split("\\.");
        if (spath.length == 1) {
            for (Idea i : getL()) {
               if (i != null && i.getName() != null && spath != null && spath.length > 0) {
                    if (i.getName().equals(spath[0])) {
                        return i;
                    }
                }
            }   
            return null;
        }
        else {
            Idea i = this;
            for (String s : spath) {
               i = i.get(s);
               if (i == null) return(null);
            }
            return i;
        }
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
    
    private Float tryParseFloat(String value) {
        Float returnValue = null;

        try {
            returnValue = Float.parseFloat(value);
        } catch (Exception ex) {
            returnValue = null;
        }

        return returnValue;
    }

    private Double tryParseDouble(String value) {
        Double returnValue = null;

        try {
            returnValue = Double.parseDouble(value);
        } catch (Exception ex) {
            returnValue = null;
        }

        return returnValue;
    }

    private Integer tryParseInteger(String value) {
        Integer returnValue = null;

        try {
            returnValue = Integer.parseInt(value);
        } catch (Exception ex) {
            returnValue = null;
        }

        return returnValue;
    }

    private Long tryParseLong(String value) {
        Long returnValue = null;

        try {
            returnValue = Long.parseLong(value);
        } catch (Exception ex) {
            returnValue = null;
        }

        return returnValue;
    }

    private Short tryParseShort(String value) {
        Short returnValue = null;

        try {
            returnValue = Short.parseShort(value);
        } catch (Exception ex) {
            returnValue = null;
        }

        return returnValue;
    }
    
    private Byte tryParseByte(String value) {
        Byte returnValue = null;
        try {
            returnValue = Byte.parseByte(value,16);
        } catch (Exception ex) {
            returnValue = null;
        }
        return returnValue;
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
    
    
    public boolean isPrimitive(Object o) {
        if (o == null) return(true);
        if (o.getClass().isPrimitive()) return(true);
        if (o instanceof Integer || 
            o instanceof Long || 
            o instanceof Double || 
            o instanceof Float || 
            o instanceof Boolean ||
            o instanceof Short ||    
            o instanceof Byte) return(true);
        return(false);
    }
    
    public boolean already_exists(Object o) {
        if (o == null || isPrimitive(o)) return false;
        for (Object oo : listtoavoidloops)
           if (oo.hashCode() == o.hashCode()) return true;
        return false;
    }
    
    public void sort() {
        Collections.sort(l, ideaComparator);
    }
    
    public Object createJavaObject(String classname) {
        if (classname.equals("java.lang.Double")) {
            return new Double(0.0);
        }
        else if (classname.equals("java.lang.Float")) {
            return new Float(0.0);
        }
        else if (classname.equals("java.lang.Integer")) {
            return new Integer(0);
        }
        else if (classname.equals("java.lang.Long")) {
            return new Long(0);
        }
        else if (classname.equals("java.lang.Short")) {
            short ret = 0;
            return new Short(ret);
        }
        else if (classname.equals("java.lang.Byte")) {
            byte ret = 0;
            return new Byte(ret);
        }
        else if (classname.equals("java.lang.Boolean")) {
            return new Boolean(false);
        }
        Class type = null;
        Object javaObject = null;
        try {
            type = Class.forName(classname);
            javaObject = type.newInstance();
            type.cast(javaObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (javaObject);
    }
    
    public Object convertObject(Object origin, String className) {
        if (origin == null) return(null);
        if (origin.getClass().getCanonicalName().equals("java.lang.String") && ((String)origin).equalsIgnoreCase("null")) return(null);
        String objectClass = origin.getClass().getName();
        if (className.equals("double") || className.equals("java.lang.Double")) {
            double value;
            if (objectClass.equals("java.lang.String")) {
                value = tryParseDouble((String) origin);
            } else {
                value = ((Number) origin).doubleValue();
            }
            return (value);
        } else if (className.equals("float") || className.equals("java.lang.Float")) {
            float value;
            if (objectClass.equals("java.lang.String")) {
                value = tryParseFloat((String) origin);
            } else {
                value = ((Number) origin).floatValue();
            }
            return (value);
        } else if (className.equals("long") || className.equals("java.lang.Long")) {
            long value;
            if (objectClass.equals("java.lang.String")) {
                value = tryParseLong((String) origin);
            } else {
                value = ((Number) origin).longValue();
            }
            return (value);
        } else if (className.equals("int") || className.equals("java.lang.Integer")) {
            int value;
            if (objectClass.equals("java.lang.String")) {
                value = tryParseInteger((String) origin);
            } else {
                value = ((Number) origin).intValue();
            }
            return (value);
        } else if (className.equals("short") || className.equals("java.lang.Short")) {
            short value;
            if (objectClass.equals("java.lang.String")) {
                value = tryParseShort((String) origin);
            } else {
                value = ((Number) origin).shortValue();
            }
            return (value);
        } else if (className.equals("byte") || className.equals("java.lang.Byte")) {
            Byte value;
            if (objectClass.equals("java.lang.String")) {
                value = tryParseByte((String) origin);
            } else {
                value = ((Number) origin).byteValue();
            }
            return (value);
        } else if (className.equals("boolean") || className.equals("java.lang.Boolean")) {
            boolean value;
            if (objectClass.equals("java.lang.String")) {
                if (((String)origin).equals("true"))
                   value = true;
                else value = false;
            } else {
                value = false;
            }
            return (value);
        } else if (className.equals("java.util.Date")) {
            Date value;
            try {
                SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
                parser.setTimeZone(TimeZone.getTimeZone("GMT"));
                value = parser.parse((String) origin);
                
            } catch(Exception e) {
                value = null;
            }
            return (value);
        } else return (origin);
    }
    
    public Object mountArray(Idea o, String classname) {
        if (classname.equals("int[]")) {
            int[] out = new int[o.getL().size()];
            int j=0;
            for (Idea i : o.getL()) {
               out[j++] = (Integer) convertObject(i.getValue(),"java.lang.Integer");
            }
            return(out);
        }
        else if (classname.equals("double[]")) {
            double[] out = new double[o.getL().size()];
            int j=0;
            for (Idea i : o.getL()) {
               out[j++] = (Double) convertObject(i.getValue(),"java.lang.Double");
            }
            return(out);
        }    
        else if (classname.equals("float[]")) {
            float[] out = new float[o.getL().size()];
            int j=0;
            for (Idea i : o.getL()) {
               out[j++] = (Float) convertObject(i.getValue(),"java.lang.Float");
            }
            return(out);
        }
        else if (classname.equals("long[]")) {
            long[] out = new long[o.getL().size()];
            int j=0;
            for (Idea i : o.getL()) {
               out[j++] = (Long) convertObject(i.getValue(),"java.lang.Long");
            }
            return(out);
        }
        else if (classname.equals("short[]")) {
            short[] out = new short[o.getL().size()];
            int j=0;
            for (Idea i : o.getL()) {
               out[j++] = (Short) convertObject(i.getValue(),"java.lang.Short");
            }
            return(out);
        }
        else if (classname.equals("byte[]")) {
            byte[] out = new byte[o.getL().size()];
            int j=0;
            for (Idea i : o.getL()) {
               out[j++] = (Byte) convertObject(i.getValue(),"java.lang.Byte");
            }
            return(out);
        }
        else if (classname.equals("boolean[]")) {
            boolean[] out = new boolean[o.getL().size()];
            int j=0;
            for (Idea i : o.getL()) {
               out[j++] = (Boolean) convertObject(i.getValue(),"java.lang.Boolean");
            }
            return(out);
        }    
        else {
            Object out=null;
            String realclassname = classname.split("\\[\\]")[0];
            try {
               Class<?> c = Class.forName(realclassname);
               out = Array.newInstance(c,o.getL().size());
            } catch(Exception e) {
               e.printStackTrace();
            }
            int j=0;
            for (Idea i : o.getL()) {
                if (i.getL().size() > 0) {
                    Array.set(out, j++,getObject(o.getName()+"."+i.getName(),realclassname) );
                }    
                else
                    Array.set(out,j++,null);
            }
            return(out);
        }                    
    }
    
    public boolean isArray(String classname) {
        String sname[] = classname.split("\\[");
        if (sname.length == 2) return true;
        else return false;
    }
    
    public Object getObject(String name, String classname) {
        if (classname.equals("java.lang.Double") ||
            classname.equals("java.lang.Float") ||
            classname.equals("java.lang.Integer") ||
            classname.equals("java.lang.Short") ||
            classname.equals("java.lang.Long") ||
            classname.equals("java.lang.Byte") ||
            classname.equals("java.lang.String") ||
            classname.equals("java.util.Date") ) {
            return convertObject(getValue(),classname);
        }
        if (isArray(classname)) {
            Idea i = get(name);
            if (i != null) {
                return mountArray(i,classname);
            }
            else {
                return null;
            }
        }
        if (classname.equals("java.util.List")) {
            classname = "java.util.ArrayList";
        }
        Object ret = createJavaObject(classname);
        try {
            Field[] fieldList = ret.getClass().getDeclaredFields();
            for (Field field : fieldList) {
                String fieldClass = field.getType().getCanonicalName();
                Idea o = get(name+"."+field.getName());
                if (o == null) {
                    //System.out.println("I was not able to get "+getFullName()+"."+name+"."+field.getName());
                }
                else {
                    if (field.getType().isArray()) {
                          Object out = mountArray(o,field.getType().getCanonicalName());
                          try {
                                if (!field.isAccessible()) field.setAccessible(true);
                                field.set(ret,out);
                            }
                            catch(Exception e) {
                                System.out.println(e.getMessage());
                                System.out.println("Array "+field.getName()+" should be of type "+field.getType().getCanonicalName()+" but I received "+value.toString()+": "+value.getClass().getCanonicalName()+"");
                            }
                    }
                    else if (field.getType().getCanonicalName().equals("java.util.List")) {
                        List out = new ArrayList();
                        for (Idea i : o.getL()) {
                            ParameterizedType type = (ParameterizedType) field.getGenericType();
                            String stype = type.getActualTypeArguments()[0].getTypeName();
                            out.add(i.getObject(i.getName(), stype));
                        }
                        try {
                            if (!field.isAccessible()) field.setAccessible(true);
                            field.set(ret,out);
                        }
                        catch(Exception e) {
                             System.out.println("Field "+field.getName()+" should be of type "+field.getType().getCanonicalName()+" but I received "+value.toString()+": "+value.getClass().getCanonicalName()+"");
                        }
                    }
                    else {
                        Object value = o.getValue();
                        if (value == null) System.out.println("Warning: value of "+field.getName()+" is null");
                        value = convertObject(value,field.getType().getCanonicalName());
                        try {
                            if (!field.isAccessible()) field.setAccessible(true);
                            field.set(ret,value);
                        }
                        catch(Exception e) {
                            o = get(name);
                            Object out;
                            if (o.getL().size() > 0) out = o.getObject(field.getName(),field.getType().getCanonicalName());
                            else out = null;
                            try {
                                if (!field.isAccessible()) field.setAccessible(true);
                                field.set(ret,out);
                            } catch(Exception e2) {
                                System.out.println(">> Field "+field.getName()+" should be of type "+field.getType().getCanonicalName()+" but I received "+value.toString()+": "+value.getClass().getCanonicalName()+"");
                            }
                            
                        }
                    }
                }
            }    
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception: " + e.getStackTrace().toString());
        }
        return(ret);
    }
    
    public synchronized void addObject(Object obj, String fullname) {
        addObject(obj,fullname,true);
    }
    
    public synchronized void addObject(Object obj, String fullname, boolean reset) {
        if (reset) reset();
        if (obj == null) {
            Idea child = createIdea(getFullName()+"."+fullname,"null",0);
            add(child);
            return;
        }
        if (listtoavoidloops.contains(obj) || already_exists(obj)) {
             Idea child = createIdea(getFullName()+"."+fullname,obj.toString(),2);
             Idea alternative = repo.get(getFullName()+"."+fullname);
             if (alternative != null) System.out.println("Ah ... I already found "+getFullName()+"."+fullname);
             else System.out.println("Strange ... it seems that "+getFullName()+"."+fullname+" is already in the repo but I can't find it");
             add(child);
             return;            
        }
        String s = ToString.from(obj);
        if (s != null) {
            Idea child = createIdea(getFullName()+"."+fullname,s,1);
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
            else {
                type = type.split("\\[\\]")[0];
            }
            if (type.equalsIgnoreCase("Double") || type.equalsIgnoreCase("Integer") || 
                type.equalsIgnoreCase("String") || type.equalsIgnoreCase("Float") || 
                type.equalsIgnoreCase("Long") || type.equalsIgnoreCase("Boolean") ||
                type.equalsIgnoreCase("Short") || type.equalsIgnoreCase("Byte") ) {
                Idea anode = Idea.createIdea(getFullName()+"."+fullname,"",0);
                for (int i=0;i<l;i++) {
                    Object oo = Array.get(obj,i);
                    Idea node = createIdea(ToString.el(getFullName()+"."+fullname, i),oo,1);
                    anode.add(node);
                }
                this.add(anode);
            } 
            else {
                Idea onode = createIdea(getFullName()+"."+fullname,"",0);
                for (int i=0;i<l;i++) {
                    Object oo = Array.get(obj,i);
                    onode.addObject(oo,ToString.el(fullname,i),false);
                    listtoavoidloops.add(obj);
                }
                this.add(onode);
            }    
            return;
        }
        else if (obj instanceof List) {
            List ll = (List) obj;
            String label = "";
            if (ll.size() > 0) label = "{"+ll.size()+"} of "+ll.get(0).getClass().getSimpleName();
            else label = "{0}";
            Idea onode = createIdea(getFullName()+"."+fullname,label,0);
            int i=0;
            for (Object o : ll) {
                onode.addObject(o,ToString.el(ToString.getSimpleName(fullname),i),false);
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
            Idea ao = createIdea(getFullName()+"."+ToString.getSimpleName(fullname),"",0);
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
                    if (!already_exists(fo)) {
                        ao.addObject(fo,fname,false);  
                    }    
                    else {
                        String ideaname = getFullName()+"."+ToString.getSimpleName(fullname)+"."+fname;
                        Idea fi = createIdea(ideaname,"",2);
                        Idea alternative2 = null;
                        if (!Modifier.isStatic(field.getModifiers())) {
                            for (Map.Entry<String,Idea> entry : repo.entrySet()) {
                                String key = entry.getKey();
                                Idea v = entry.getValue();
                                if (ToString.getSimpleName(ideaname).equals(ToString.getSimpleName(key))) {
                                    System.out.println("The Idea "+ideaname+" is already in the repository");
                                }
                            }
                            System.out.println(fo.getClass().getCanonicalName());
                            Idea alternative = repo.get(ideaname);
                            if (alternative != null) System.out.println("Ah ... I already found "+ideaname);
                            else System.out.println("Strange ... it seems that "+ideaname+" is already in the repo but I can't find it");
                        }
                        ao.add(fi);
                    }
                } catch (Exception e) {
                }   
            }
            this.add(ao);
            return;
        }
    }
    
}
