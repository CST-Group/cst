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
package br.unicamp.cst.representation.idea;

import br.unicamp.cst.support.ToString;
import ch.qos.logback.classic.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.LoggerFactory;


/**
 * This class is used as the standard knowledge representation entity within CST
 * 
 * @author rgudwin
 */
public class Idea implements Category,Habit {
    private long id;
    private String name;
    private Object value;
    private List<Idea> l= new CopyOnWriteArrayList<>();
    private int type=1;
    private String category;
    private double belief=0.0;
    private double threshold=0.5;
    private transient IdeaComparator ideaComparator = new IdeaComparator();
    private static transient Logger logger = (Logger) LoggerFactory.getLogger(Idea.class);
    // This list is used while building a toStringFull()
    /**
     * The repo hashmap is a list of known Ideas used by the createIdea factory method to 
     * avoid that Ideas with a same name to be created. This list is used to reuse an 
     * Idea with the same name. This variable provides a global list with all known 
     * Ideas created so far. 
     */
    public static ConcurrentHashMap<String,Idea> repo = new ConcurrentHashMap<>();
    // This list is used while converting a Java Object to an Idea, to avoid recursions
    transient static CopyOnWriteArrayList<Object> listtoavoidloops = new CopyOnWriteArrayList<>();
    /**
     * This variable stores the last Id used while creating new Ideas. 
     */
    public static long lastId = 0;
    
    private static final String CDOUBLE = "java.lang.Double";
    private static final String CFLOAT = "java.lang.Float";
    private static final String CINT = "java.lang.Integer";
    private static final String CLONG = "java.lang.Long";
    private static final String CSHORT = "java.lang.Short";
    private static final String CBYTE = "java.lang.Byte";
    private static final String CBOOLEAN = "java.lang.Boolean";
    private static final String CSTRING = "java.lang.String";
    
    private static final String DEFAULT_CATEGORY = "Property";
        
    
    /**
     * This function generates a new id to be assigned to a new Idea being created. 
     * This id is generated in a serialized format, being the first one with value 0 
     * and incrementally increased by one for each new created Idea. The value of the 
     * last id created so far is stored in the <i>lastId</i> static variable. 
     * @return the generated new id
     */
    public static long genId() {
        return(lastId++);
    }
    /**
     * This is the simpler constructor for an Idea. Basically, it finds a new id and creates an empty Idea. 
     */
    public Idea() {
        // The default is to have ideas with name "", value null, of type 1, category Property with Existence scope
        this("",null,DEFAULT_CATEGORY,1.0);
    }
    
    /**
     * This construction initializes the Idea just with a name (and an id). 
     * @param name The name to be assigned to the Idea
     */
    public Idea(String name) {
        this(name,null,DEFAULT_CATEGORY,1.0);
    }
    
    /**
     * This constructor is a wrapper for an Idea with type 1. If the value is a String, it is parsed in order to create a numeric value with a proper type. Up to now, the constructor recognizes Integers and Doubles.  
     * @param name The name assigned to the Idea
     * @param value The value assigned to the Idea. If this value is a String, it is parsed to check if this String describes an Integer or a Double and converts the number to a prpper type (an int or a double). 
     */
    public Idea(String name, Object value) {
        this(name,value,DEFAULT_CATEGORY,1.0);
    }
    
    /**
     * This constructor initializes the Idea with a name, a value and a type. 
     
     * 
     * @param name The name assigned to the Idea.
     * @param value The value to be assigned to the Idea (can be an empty String, or null). If the value is given a null value, it is substituted by the String "null". 
     * @param type The type assigned to the Idea
     */
    public Idea(String name, Object value, int type) {
        this(name,value,type,DEFAULT_CATEGORY,0.0,0.5);
        correctCategoryAndScope();
    }
    
    /**
     * This constructor initializes the Idea with a name, a value, a category and a belief. 
     * The Idea type is guessed, based on its category
     * @param name The name assigned to the Idea
     * @param value The value to be assigned to the Idea (can be an empty String, or null). If the value is given a null value, it is substituted by the String "null". 
     * @param category The category assigned to the Idea
     * @param belief The belief that the Idea represents a part of Existence
     */
    public Idea(String name, Object value, String category, double belief) {
        this(name,value,guessType(category,value,belief,0.5),category,belief,0.5);
    }
    
    /**
     * This constructor initializes the Idea with a name, a value, a type, a category and a scope.
     * The value can be any Java object. The type is an integer number, which
     * describes the category of the Idea. Even though the type can be used for any purpose,
     * the following reference for Idea types is used as a reference:
     * 0 - AbstractObject
     * 1 - Property
     * 2 - Link or Reference to another Idea
     * 3 - QualityDimension
     * 4 - Episode
     * 5 - Composite
     * 6 - Aggregate
     * 7 - Configuration
     * 8 - TimeStep
     * 9 - PropertyCategory
     * 10 - ObjectCategory
     * 11 - EpisodeCategory
     * 12 - PropertyPossibility
     * 13 = ObjectPossibility
     * 14 - EpisodePossibility
     * 15 - ActionPossibility
     * 16 - Action
     * 17 - ActionCategory
     * 18 - Goal
     * @param name The name assigned to the Idea
     * @param value The value to be assigned to the Idea (can be an empty String, or null). If the value is given a null value, it is substituted by the String "null". 
     * @param type The type assigned to the Idea
     * @param category The category assigned to the Idea
     * @param belief The belief that the Idea represents a part of Existence
     * @param threshold The belief threshold to consider the Idea a part of Existence
     */
    public Idea(String name, Object value, int type, String category, double belief, double threshold) {
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
        this.type = type;
        this.category = category;
        this.belief = belief;
        this.threshold = threshold;
    }
    
    
    
    /**
     * The createIdea method is used as a static Idea factory, which tries to reuse Ideas with the same name. 
     * It can be used, for example, in cases where Ideas are created in a periodic way, being disposed in the sequence. 
     * The use of this method allows for a better use of memory, avoiding an excessive use of garbage collection mechanism. 
     * An Idea created with this method will not create a new Idea if given a name already used in the past. In this case, 
     * it will return a reference to this already created Idea. Ideas with the same name, with a different type will be
     * treated as different Ideas. 
     * @param name The name associated to the created Idea
     * @param value The value assigned to the Idea
     * @param type The type assigned to the Idea
     * @return The newly created Idea
     */
    public synchronized static Idea createIdea(String name, Object value, int type) {
        Idea ret = repo.get(name+"."+type);
        if (ret == null) {
            ret = new Idea(name,value,type);
            repo.put(name+"."+type, ret);
        }
        else { 
            ret.setValue(value);
            ret.l= new CopyOnWriteArrayList<>();
        }    
        return(ret);
    }
    
    public static int guessScope(Object value, double belief, double threshold) {
        if (value != null && (value instanceof Category || value instanceof Habit)) return(2);
        else if (belief >= threshold) return(1);
        else return(0);
    }
    
    public static int guessType(String category, Object value, double belief, double threshold) {
        int guess = 0;
        int scope = guessScope(value, belief, threshold);
        if (category != null) {
            if (category.equalsIgnoreCase("AbstractObject") && scope == 1) {
                guess = 0;
            }
            if (category.equalsIgnoreCase("Property") && scope == 1) {
                guess = 1;
            }
            else if (category.equalsIgnoreCase("Link")) {
                guess = 2;
            }
            else if (category.equalsIgnoreCase("QualityDimension")) {
                guess = 3;
            }
            else if (category.equalsIgnoreCase("Episode") && scope == 1) {
                guess = 4;
            }
            else if (category.equalsIgnoreCase("Composite")) {
                guess = 5;
            }
            else if (category.equalsIgnoreCase("Aggregate")) {
                guess = 6;
            }
            else if (category.equalsIgnoreCase("Configuration")) {
                guess = 7;
            }
            else if (category.equalsIgnoreCase("TimeStep")) {
                guess = 8;
            }
            else if (category.equalsIgnoreCase("Property") && scope == 2) {
                guess = 9;
            }
            else if (category.equalsIgnoreCase("AbstractObject") && scope == 2) {
                guess = 10;
            }
            else if (category.equalsIgnoreCase("Episode") && scope == 2) {
                guess = 11;
            }
            else if (category.equalsIgnoreCase("Property") && scope == 0) {
                guess = 12;
            }
            else if (category.equalsIgnoreCase("AbstractObject") && scope == 0) {
                guess = 13;
            }
            else if (category.equalsIgnoreCase("Episode") && scope == 0) {
                guess = 14;
            }
            else if (category.equalsIgnoreCase("Action") && scope == 0) {
                guess = 15;
            }
            else if (category.equalsIgnoreCase("Action") && scope == 1) {
                guess = 16;
            }
            else if (category.equalsIgnoreCase("Action") && scope == 2) {
                guess = 17;
            }
            else if (category.equalsIgnoreCase("Goal")) {
                guess = 18;
            }
        }
        return(guess);
    }

    /**
     * This method returns the simple name of the given Idea.
     * This method returns what is called the "Simple" name of an Idea. 
     * An Idea with a registered name "workingMemory.PhonologicLoop.VowelA" 
     * will return just "VowelA". If you need the full name assigned to the Idea, 
     * use the getFullName() method instead. 
     * @return The "simple name" of the given Idea
     */
    public String getName() {
        return ToString.getSimpleName(name);
    }
    
    /**
     * This method returns the full name of the given Idea.
     * If the Idea name is a structured one, like e.g. "workingMemory.PhonologicLoop.VowelA",
     * this method will return the full registered name. If you want just the simple name
     * (in the example "VowelA", use getName() instead. 
     * @return The full name registered for the Idea. 
     */
    public String getFullName() {
        return(name);
    }
    
    
    /**
     * Sets a new name for the Idea
     * @param name The new name to be assigned to the Idea
     */
    public void setName(String name) {
        this.name = name;
    }

    /** This method returns the list of child Ideas associated to the current Idea. 
     * 
     * @return The list of Ideas associated with the current Idea
     */
    public List<Idea> getL() {
        return l;
    }

    /**
     * This method sets a new list of associated or child Ideas, to the current Idea. 
     * @param l The list of Ideas to substitute the old list of associated Ideas. 
     */
    public void setL(List<Idea> l) {
        this.l = l;
    }
    
    /**
     * This method returns the id associated to the current Idea
     * @return the id of the current Idea
     */
    public long getId() {
        return(id);
    }

    /**
     * This method associates another Idea to the current Idea
     * @param node the Idea to be associated as a child Idea
     * @return the add method also returns the associated Idea, for nesting the association of Ideas with a single call. This return can be safely ignored. 
     */
    public Idea add(Idea node) {
        l.add(node);
        sort();
        return(node);
    }
    
    /**
     * This static method is used to reset the list of known names used by the createIdea factory
     */
    public static void reset() {
        listtoavoidloops = new CopyOnWriteArrayList<>();
    }
    
    /**
     * This method returns a String short version of the Idea
     * @return the name of the Idea, used as a short version of the Idea
     */
    @Override
    public String toString() {
        return(name);
    }
    
    /**
     * This method returns an extended String version of the Idea. 
     * In the case the Idea has multiple child Ideas, this method returns a multi-line String 
     * @return a String with a multi-line version of the Idea
     */
    public String toStringFull() {
        return(toStringFull(false));
    }
    
    /**
     * This method is equivalent to the toStringFull() method, with an additional "withid" flag, to indicate if the 
     * Idea id should be included in the String version of the Idea
     * @param withid a boolean flag to indicate if the id should be included (use withid = true to include the id)
     * @return a String with a multi-line version of the Idea, including the Idea id. 
     */
    public String toStringFull(boolean withid) {
        reset();
        return(toStringFull(1,withid));
    }
    
    private String toStringPlus(boolean withid) {
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
    
    private String toStringFull(int level, boolean withid) {
        String out; 
        if (isLeaf()) {
           out = toStringPlus(withid)+"\n";
           return out; 
        }
        else {
            out = toStringPlus(withid)+"\n";
            listtoavoidloops.add(toStringPlus(withid));
            for (Idea ln : l) {
                for (int i=0;i<level;i++) out += "   ";
                if (listtoavoidloops.contains(ln.toStringPlus(withid)) || already_exists(ln.toStringPlus(withid))) {
                    out += ln.toStringPlus(withid)+" #\n";
                }
                    
                else out += ln.toStringFull(level+1,withid);
            }
            return(out);
        }
    }
    
    /**
     * This method is used to search for an Idea, which is registered as a child Idea, of the current Idea.
     * If this Idea is named "a", and Idea "b" is associated to it, and further Idea "c" is associated to "b", 
     * we can use this method, passing the argument "b.c" as a parameter, to receive back the "c" Idea.
     * In this case, we can do a.get("b.c") to receive back the "c" Idea. 
     * @param path The path to localize the associated Idea. 
     * @return The localized Idea, if it exists. Otherwise, it returns null. 
     */
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
    
    /**
     * This method returns the value of the current Idea
     * @return the value of the current Idea
     */
    public Object getValue() {
        return value;
    }

    /**
     * This method sets a new value to the current Idea
     * @param value the new value of the Idea
     */
    public void setValue(Object value) {
        this.value = value;
    }
    
    /**
     * This method returns the type of the current Idea. 
     * Even though the type can be used for any purpose,
     * the following reference for Idea types is used as a reference:
     * 0 - AbstractObject (Existent)
     * 1 - Property (Existent)
     * 2 - Link or Reference to another Idea
     * 3 - QualityDimension
     * 4 - Episode (Existent)
     * 5 - Composite
     * 6 - Aggregate
     * 7 - Configuration
     * 8 - TimeStep
     * 9 - Property (Law)
     * 10 - AbstractObject (Law)
     * 11 - Episode (Law)
     * 12 - Property (Possibility)
     * 13 - AbstractObject (Possibility)
     * 14 - Episode (Possibility)
     * 15 - ActionPossibility
     * 16 - Action
     * 17 - ActionCategory
     * 18 - Goal
     * @return an integer indicating the type of the Idea
     */
    public int getType() {
        return type;
    }
    
    private void correctCategoryAndScope() {
        switch(type) {
            case 0:setCategory("AbstractObject");
                   setScope(1);
                   break;
            case 1:setCategory("Property");
                   setScope(1);
                   break;
            case 2://2 - Link or Reference to another Idea
                   setCategory("Link");
                   break;
            case 3:setCategory("QualityDimension");
                   setScope(1);
                   break;
            case 4:setCategory("Episode");
                   setScope(1);
                   break;       
            case 5://5 - Composite
                   setCategory("Composite");
                   break;
            case 6://6 - Aggregate
                   setCategory("Aggregate");
                   break;
            case 7:// 7 - Configuration
                   setCategory("Configuration");
                   break;
            case 8:setCategory("TimeStep");
                   break;
            case 9:setCategory("Property");
                   setScope(2);
                   break;
            case 10:setCategory("AbstractObject");
                   setScope(2);
                   break;
            case 11:setCategory("Episode");
                   setScope(2);
                   break;
            case 12:setCategory("Property");
                   setScope(0);
                   break;
            case 13:setCategory("AbstractObject");
                   setScope(0);
                   break;
            case 14:setCategory("Episode");
                   setScope(0);
                   break;       
            case 15:setCategory("Action");
                   setScope(0);
                   break;
            case 16:setCategory("Action");
                   setScope(1);
                   break;
            case 17:setCategory("Action");
                   setScope(2);
                   break;
            case 18:setCategory("Goal");
                   setScope(1);
                   break;
            default:break;              
                   
        }
    }
    
    /** 
     * This method is used to set a new type for the Idea
     * @param type the new type assigned to the Idea
     */
    public void setType(int type) {
        this.type = type;
        correctCategoryAndScope();
    }
    
    /**
     * This method is used to get the category for the Idea
     * @return a String with the name of the Idea category
     */
    public String getCategory() {
        return category;
    }
    
    /**
     * This method is used to set the category for the Idea
     * @param category a String with the name of the Idea category
     */
    public void setCategory(String category) {
        this.category = category;
    }
    
    /**
     * This method is used to get the belief on the Idea
     * @return a double between 0 an 1 indicating the belief that the Idea represents some part of Existence
     */
    public double getBelief() {
        return belief;
    }
    
    /**
     * This method is used to set the belief for the Idea
     * @param belief a double between 0 and 1 indicating the belief that the Idea represents a part of Existence
     */
    public void setBelief(double belief) {
        this.belief = belief;
    }
    
    /**
     * This method is used to get the belief threshold on the Idea
     * @return a double between 0 an 1 indicating the threshold to consider the Idea as a part of Existence
     */
    public double getThreshold() {
        return threshold;
    }
    
    /**
     * This method is used to set the belief for the Idea
     * @param threshold a double between 0 and 1 indicating the belief threshold to consider the Idea as a part of Existence
     */
    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
    
    /**
     * This method is used to set the scope of the Idea
     * The scope indicates if this idea is to be interpreted as
     * a possibility (scope = 0), an existent (scope = 1) or a law
     * (scope = 2)
     * @return the Idea scope
     */
    public int getScope() {
        return(guessScope(value,belief,threshold));
    }
    
    /**
     * This method is used to return the Idea's scope
     * The scope indicates if this idea is to be interpreted as
     * a possibility (scope = 0), an existent (scope = 1) or a law
     * (scope = 2)
     * @param scope the Idea's scope (0: possibility, 1: existent, 2:law)
     */
    public void setScope(int scope) {
        switch(scope) {
            case 0: belief=0;
                    break;
            case 1:belief=1;
                   break;
            case 2:if (value == null) { 
                    value = new EntityCategory();
                   }
                   else if (value instanceof Category || value instanceof Habit) {
                      // Do nothing ... it is already considered a law !
                   }
                   else logger.error("It is not possible to set the scope to 2 because value is different from null but not a Habit or Category");
                   break;
            default:belief=0;
                    break;
        }
    }
    
    /**
     * This convenience method is used to test if an Idea is of a given type
     * @param type the type to be tested
     * @return a boolean indicating if the test succeeded or not. 
     */
    public boolean isType(int type) {
        return(this.type==type);
    }
    
    /**
     * This convenience method is used to test if the value of the current Idea is a double. 
     * @return a boolean indicating if the value of the current Idea is a double number. 
     */
    public boolean isDouble() {
        if (value == null) return false;
        if (value instanceof Double) return true;
        return(false);   
    }
    
    /**
     * This convenience method is used to test if the value of the current Idea is a float. 
     * @return a boolean indicating if the value of the current Idea is a float number. 
     */
    public boolean isFloat() {
        if (value == null) return false;
        if (value instanceof Float) return true;
        return(false);   
    }
    
    /**
     * This convenience method is used to test if the value of the current Idea is an int. 
     * @return a boolean indicating if the value of the current Idea is an integer number. 
     */
    public boolean isInteger() {
        if (value == null) return false;
        if (value instanceof Integer) return true;
        return(false);   
    }
    
    /**
     * This convenience method is used to test if the value of the current Idea is a long. 
     * @return a boolean indicating if the value of the current Idea is a long number. 
     */
    public boolean isLong() {
        if (value == null) return false;
        if (value instanceof Long) return true;
        return(false);   
    }
    
    /**
     * This convenience method is used to test if the value of the current Idea is a number.
     * It tests if the value is a float, a double, an int or a long. 
     * @return a boolean indicating if the value of the current Idea is a number. 
     */
    public boolean isNumber() {
        if (isFloat() || isDouble() || isLong() || isInteger() || value instanceof Short || value instanceof Byte) return(true);
        return(false);
    }

    /**
     * This convenience method is used to test if the value of the current Idea is a HashMap. 
     * @return a boolean indicating if the value of the current Idea is a HashMap. 
     */
    public boolean isHashMap(){
        if (value == null) return false;
        if (value instanceof HashMap) return true;
        return(false);   
    }
    
    /**
     * This convenience method is used to test if the value of the current Idea is a String. 
     * @return a boolean indicating if the value of the current Idea is a String. 
     */
    public boolean isString() {
        if (value == null) return false;
        if (value instanceof String) return true;
        return(false);   
    }
    
    /**
     * This convenience method is used to test if the value of the current Idea is a boolean. 
     * @return a boolean indicating if the value of the current Idea is a boolean. 
     */
    public boolean isBoolean() {
        if (value == null) return false;
        if (value instanceof Boolean) return true;
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

    /**
     * This method creates a clone of the current Idea.
     * In an Idea with associated Ideas, a full clone of all associated Ideas is created. 
     * @return a cloned version of this Idea
     */
    public Idea clone() {
        return(clone(true));
    }

    static private Map<Idea,Idea> clonemap;
    
    private Idea clone(boolean reset) {
        if (reset) {
            clonemap = new HashMap<Idea,Idea>();
        }
        Idea newnode;
        newnode = clonemap.get(this);
        if (newnode == null) {
           newnode = new Idea(getName(), getValue(), getType(), getCategory(), getBelief(), getThreshold());
           clonemap.put(this, newnode);
        }
        else return(newnode);
        newnode.l = new ArrayList();
        for (Idea i : getL()) {
            Idea ni = i.clone(false);
            newnode.add(ni);
        }
        return newnode;
    }
    
    /**
     * This method returns a resumed value of this Idea value, in a String format. 
     * @return A String with a resumed value of this Idea value. 
     */
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
    
    /**
     * This convenience method returns true if the parameter Object is a primitive Object
     * @param o The Object to be tested
     * @return true if the Object is a primitive or false if not. 
     */
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
    
    /**
     * This method checks if an Idea with the Object passed as a parameter was already created. 
     * It is used to avoid infinite loops while converting a Java Object to an Idea. 
     * @param o The Object to be tested 
     * @return true if there is already an Idea using this Object as a value. 
     */
    public boolean already_exists(Object o) {
        if (o == null || isPrimitive(o)) return false;
        for (Object oo : listtoavoidloops)
           if (oo.hashCode() == o.hashCode()) return true;
        return false;
    }
    
    /**
     * This method sorts the internal list of associated Ideas, using their name as a reference. 
     */
    public void sort() {
        Collections.sort(l, ideaComparator);
    }
    
    /**
     * This convenience method creates an Object of a given class, using the parameter class name, passed as a String
     * @param classname the full name of the class for the object to be created. 
     * @return the created Object
     */
    public static Object createJavaObject(String classname) {
        if (classname.equals(CDOUBLE)) {
            return Double.valueOf(0.0);
        }
        else if (classname.equals(CFLOAT)) {
            return Float.valueOf(0.0f);
        }
        else if (classname.equals(CINT)) {
            return Integer.valueOf(0);
        }
        else if (classname.equals(CLONG)) {
            return Long.valueOf(0L);
        }
        else if (classname.equals(CSHORT)) {
            short ret = 0;
            return Short.valueOf(ret);
        }
        else if (classname.equals(CBYTE)) {
            byte ret = 0;
            return Byte.valueOf(ret);
        }
        else if (classname.equals(CBOOLEAN)) {
            return Boolean.valueOf(false);
        }
        Class type = null;
        Object javaObject = null;
        try {
            type = Class.forName(classname);
            javaObject = type.getDeclaredConstructor().newInstance();
            type.cast(javaObject);
        } catch (Exception e) {
            logger.info("The class name {0} is not on the Java Library Path !", classname);
        }
        return (javaObject);
    }
    
    private Object convertObject(Object origin, String className) {
        if (origin == null) return(null);
        if (origin.getClass().getCanonicalName().equals(CSTRING) && ((String)origin).equalsIgnoreCase("null")) return(null);
        String objectClass = origin.getClass().getName();
        if (className.equals("double") || className.equals(CDOUBLE)) {
            double value;
            if (objectClass.equals(CSTRING)) {
                value = tryParseDouble((String) origin);
            } else {
                value = ((Number) origin).doubleValue();
            }
            return (value);
        } else if (className.equals("float") || className.equals(CFLOAT)) {
            float value;
            if (objectClass.equals(CSTRING)) {
                value = tryParseFloat((String) origin);
            } else {
                value = ((Number) origin).floatValue();
            }
            return (value);
        } else if (className.equals("long") || className.equals(CLONG)) {
            long value;
            if (objectClass.equals(CSTRING)) {
                value = tryParseLong((String) origin);
            } else {
                value = ((Number) origin).longValue();
            }
            return (value);
        } else if (className.equals("int") || className.equals(CINT)) {
            int value;
            if (objectClass.equals(CSTRING)) {
                value = tryParseInteger((String) origin);
            } else {
                value = ((Number) origin).intValue();
            }
            return (value);
        } else if (className.equals("short") || className.equals(CSHORT)) {
            short value;
            if (objectClass.equals(CSTRING)) {
                value = tryParseShort((String) origin);
            } else {
                value = ((Number) origin).shortValue();
            }
            return (value);
        } else if (className.equals("byte") || className.equals(CBYTE)) {
            Byte value;
            if (objectClass.equals(CSTRING)) {
                value = tryParseByte((String) origin);
            } else {
                value = ((Number) origin).byteValue();
            }
            return (value);
        } else if (className.equals("boolean") || className.equals(CBOOLEAN)) {
            boolean value;
            if (objectClass.equals(CSTRING)) {
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
    
    private Object mountArray(Idea o, String classname) {
        if (classname.equals("int[]")) {
            int[] out = new int[o.getL().size()];
            int j=0;
            for (Idea i : o.getL()) {
               out[j++] = (Integer) convertObject(i.getValue(),CINT);
            }
            return(out);
        }
        else if (classname.equals("double[]")) {
            double[] out = new double[o.getL().size()];
            int j=0;
            for (Idea i : o.getL()) {
               out[j++] = (Double) convertObject(i.getValue(),CDOUBLE);
            }
            return(out);
        }    
        else if (classname.equals("float[]")) {
            float[] out = new float[o.getL().size()];
            int j=0;
            for (Idea i : o.getL()) {
               out[j++] = (Float) convertObject(i.getValue(),CFLOAT);
            }
            return(out);
        }
        else if (classname.equals("long[]")) {
            long[] out = new long[o.getL().size()];
            int j=0;
            for (Idea i : o.getL()) {
               out[j++] = (Long) convertObject(i.getValue(),CLONG);
            }
            return(out);
        }
        else if (classname.equals("short[]")) {
            short[] out = new short[o.getL().size()];
            int j=0;
            for (Idea i : o.getL()) {
               out[j++] = (Short) convertObject(i.getValue(),CSHORT);
            }
            return(out);
        }
        else if (classname.equals("byte[]")) {
            byte[] out = new byte[o.getL().size()];
            int j=0;
            for (Idea i : o.getL()) {
               out[j++] = (Byte) convertObject(i.getValue(),CBYTE);
            }
            return(out);
        }
        else if (classname.equals("boolean[]")) {
            boolean[] out = new boolean[o.getL().size()];
            int j=0;
            for (Idea i : o.getL()) {
               out[j++] = (Boolean) convertObject(i.getValue(),CBOOLEAN);
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
    
    private boolean isArray(String classname) {
        String sname[] = classname.split("\\[");
        if (sname.length == 2) return true;
        else return false;
    }
    
    /**
     * This method tries to get an internal Idea and convert it to a Java Object of the indicated class
     * 
     * @param name The name of the internal Idea to be get
     * @param classname The name of the Java Class to be used for conversion
     * @return a new Java Object reflecting the desired Idea
     */
    public Object getObject(String name, String classname) {
        if (classname.equals(CDOUBLE) ||
            classname.equals(CFLOAT) ||
            classname.equals(CINT) ||
            classname.equals(CLONG) ||
            classname.equals(CSHORT) ||
            classname.equals(CBYTE) ||
            classname.equals(CSTRING) ||
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
        if (ret == null) return(null);
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
                            field.set(ret,value);
                        }
                        catch(Exception e) {
                            o = get(name);
                            Object out;
                            if (o.getL().size() > 0) out = o.getObject(field.getName(),field.getType().getCanonicalName());
                            else out = null;
                            try {
                                field.set(ret,out);
                            } catch(Exception e2) {
                                if (value != null)
                                   System.out.println(">> Field "+field.getName()+" should be of type "+field.getType().getCanonicalName()+" but I received "+value.toString()+": "+value.getClass().getCanonicalName());
                                else
                                   System.out.println(">> Field "+field.getName()+" should be of type "+field.getType().getCanonicalName()+" but I received <null>");
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
    
    private Object checkIfObjectHasSetGet(Object bean, String propertyName) {
    // access a no-arg method through reflection
    // following bean naming conventions
    try {
        Class<?> c =  bean.getClass();
        String methodName = "get"+propertyName.substring(0,1).toUpperCase()+propertyName.substring(1);
        Method m = c.getMethod(methodName,(Class[])null);
        Object o = m.invoke(bean);
        return o;
    }
    catch (Exception e) {
        try {
            Class<?> c = bean.getClass();
            Method m = c.getMethod(propertyName,(Class[])null);
            Object o = m.invoke(bean);
            return o;
        }
        catch (Exception e2) {
            return("<<NULL>>");
            // (gulp) -- swallow exception and move on
        }
    }
    //return null; // it would be better to throw an exception, wouldn't it?
}
    
    private boolean trySetAccessibleTrue(Field f) {
        if (Modifier.isPublic(f.getModifiers())) return(true);
        else return(false);
    }
    
    /**
     * This method uses a Java Object as template, creating an Idea with a similar structure.
     * This new Idea is added to the current Idea
     * @param obj The Java Object to be used as a template
     * @param fullname the full name to be assigned to added Idea
     */
    public synchronized void addObject(Object obj, String fullname) {
        addObject(obj,fullname,true);
    }
    
    /**
     * This method uses a Java Object as template, creating an Idea with a similar structure. 
     * This new Idea is added to the current Idea. This version allows the global list of names
     * to be reset
     * @param obj The Java Object to be used as a template.
     * @param fullname The full name to be assigned to the added Idea.
     * @param reset a boolean flag indicating if the global list of names should be reset. 
     */
    public synchronized void addObject(Object obj, String fullname, boolean reset) {
        if (reset) reset();
        if (obj == null) {
            Idea child = createIdea(getFullName()+"."+fullname,"null",0);
            add(child);
            return;
        }
        if (listtoavoidloops.contains(obj) || already_exists(obj)) {
             // If the object has already been used (in a loop) I will create a link instead of digging inside it
             Idea child = createIdea(getFullName()+"."+fullname,obj.toString(),2);
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
        else if (obj instanceof Enumeration<?>) {
            Enumeration<?> ll = (Enumeration<?>) obj;
            String label = "";
            int size = 0;
            ArrayList components = new ArrayList();
            while (ll.hasMoreElements()) {
                components.add(ll.nextElement());
                size++;
            }
            if (size == 0) label = "{0}";
            else label = "{"+size+"} of "+components.get(0).getClass().getSimpleName();
            Idea onode = createIdea(getFullName()+"."+fullname,label,0);
            int i=0;
            for (Object o : components) {
                onode.addObject(o,ToString.el(ToString.getSimpleName(fullname),i),false);
                listtoavoidloops.add(obj);
                i++;
            }
            this.add(onode);
            return;
        }
        else if (obj instanceof Idea) {
            Idea child = createIdea(getFullName()+"."+fullname,s,0);
            Idea ao = (Idea) obj;
            child.add(ao);
            this.add(child);
            //this.add((Idea)obj);
            listtoavoidloops.add(obj);  // should I include obj or child here ?           
            return;
        }
        else {
            Idea ao = createIdea(getFullName()+"."+ToString.getSimpleName(fullname),"",0);
            listtoavoidloops.add(obj);
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                String fname = field.getName();
                try {
                   boolean trysetaccessible = trySetAccessibleTrue(field);
                   Object fo;
                   if (trysetaccessible) { // if object is accessible, try to get it
                       int modifiers = field.getModifiers();
                       if (Modifier.isStatic(modifiers)) fo = field.get(null);
                       else fo = field.get(obj);
                       //System.out.println("Field "+fname+" with value "+fo+" is accessible");
                   }
                   else { // if it is inaccessible, check if it is a bean, before giving up
                       fo = checkIfObjectHasSetGet(obj, fname);
                       if (fo != null && fo.equals("<<NULL>>")) {
                           // This is the case the field is inaccessible and DOES NOT have a get Method
                           String mod = fname+":";
                           if (Modifier.isStatic(field.getModifiers())) mod+=" STATIC";
                           if (Modifier.isProtected(field.getModifiers())) mod+=" PROTECTED";
                           if (Modifier.isPrivate(field.getModifiers())) mod+=" PRIVATE";
                           if (Modifier.isTransient(field.getModifiers())) mod+=" TRANSIENT";
                           if (Modifier.isVolatile(field.getModifiers())) mod+=" VOLATILE";
                           //Logger.getAnonymousLogger().log(Level.INFO,"The field "+getFullName()+"."+ToString.getSimpleName(fullname)+"."+mod+" is inaccessible while being proceessed by addObject and does not hava a get method ! It will not be included in the Idea");
                       }
                   }
                   if (fo != null && fo.equals("<<NULL>>")) {
                       // do nothing
                   }
                   else if (!already_exists(fo)) {
                       ao.addObject(fo,fname,false);  
                       //System.out.println("Inserting a new idea with name "+fname+" with value "+fo);
                   }
                   else { // this is the case when a recursive object is detected ... inserting a link
                       String ideaname = getFullName()+"."+ToString.getSimpleName(fullname)+"."+fname;
                       //System.out.println("Im inserting a link "+fname+" here: "+ideaname);
                       Idea fi = createIdea(ideaname,"",2);
                       ao.add(fi);
                   }
                } catch (Exception e) {
                    logger.info("I got a {0} Exception in field {1} in class Idea: {2}",new Object[]{e.getClass().getName(),fname,e.getMessage()+"-->"+e.getLocalizedMessage()});
                }   
            }
            this.add(ao);
            return;
        }
    }
    
    /**
     * This method can be called from an Idea, it the Idea is a Habit. 
     * It executes the Habit without the necessity to first recover the Habit from the Idea
     * @param idea an Idea passed as a parameter to the Habit. Can be null if no parameter is required
     * @return an Idea, which is the result of the Habit execution. Can be null
     */
    @Override
    public Idea exec(Idea idea) {
        if (isHabit()) {
            Habit h = (Habit) getValue();
            return(h.exec(idea));
        }
        else return(null);
    }
    
    public Idea getInstance() {
        return getInstance(null);
    }
    
    @Override
    public Idea getInstance(Idea constraints ) {
        if (getValue() instanceof Category) {
            Category c = (Category) getValue();
            return(c.getInstance(constraints));
        }
        return(null);
    }
    
    @Override
    public double membership(Idea idea) {
        if (getValue() instanceof Category) {
            Category c = (Category) getValue();
            return(c.membership(idea));
        }
        return(0.0);
    }
    
    /**
     * This method returns true if the present Idea is a Category, i.e., have a Category as its value.
     * If it is a Habit, an user can call the methods instantiation and membership from this Idea
     * @return true if this idea is a Category or false otherwise
     */
    public boolean isCategory() {
        Object val = getValue();
        if (val instanceof Idea) return(((Idea) value).isCategory());
        else if (getValue() instanceof Category) return(true);
        else return(false);
    }
    
    /**
     * This method returns true if the present Idea is a Habit, i.e., have a Habit as its value.
     * If it is a Habit, an user can call the method exec (or exec0) from this Idea
     * @return true if this idea is a Habit or false otherwise
     */
    public boolean isHabit() {
        Object val = getValue();
        if (val instanceof Idea) return(((Idea) value).isHabit());
        if (getValue() instanceof Habit) return(true);
        else return(false);
    }
    
    /**
     * This method returns true if the present Idea is a Leaf, i.e., does not have any children Idea
     * @return true if this idea is a leaf or false otherwise
     */
    public boolean isLeaf() {
        return this.l.isEmpty();
    }
    
    public String toJSON() {
        Gson gson;
        //gson = new GsonBuilder().registerTypeAdapter(Idea.class, new InterfaceAdapter<Idea>())
        //                     .setPrettyPrinting().create();
        //gson = new Gson();
        gson = new GsonBuilder().setPrettyPrinting().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();
        String out = "";
        try {
            out = gson.toJson(this);
        } catch(Error e) {
            logger.info("It was not possible to generate a version of the idea {0}",this.getFullName());
        }    
        return(out);
    }
    
    public static Idea fromJSON(String idea_json) {
        Gson gson;
        //gson = new GsonBuilder().registerTypeAdapter(Idea.class, new InterfaceAdapter<Idea>())
        //                     .setPrettyPrinting().create();
        //gson = new Gson();
        gson = new GsonBuilder().setPrettyPrinting().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();
        Idea i = null;
        try {
            i = gson.fromJson(idea_json, Idea.class);
        } catch(Error e) {
            logger.info("It was not possible to generate an idea from the given String ... creating a null Idea");
        }
        return(i);
    }
    
    /**
     * This method identifies if the Idea is equivalent of another Idea, i.e., if they share a same set of sub-ideas
     * @param other the other Idea, which is compared to this one
     * @return a boolean indicating if the other Idea is equivalent to this one
     */
    public boolean equivalent(Idea other) {
        return(equivalent(other,true));
    } 
    
    private boolean equivalent(Idea other, boolean reset) {
        boolean ae;
        if (reset) reset();
        ae = already_exists(other);
        if (!ae) listtoavoidloops.add(other);
        if (this != null && other == null) return(false);
        boolean out = true;
        out &= this.getName().equals(other.getName());
        if (!ae) { // This is to prevent a loop to retest an Idea that was already tested
          for (Idea id : other.getL()) {
            String name = id.getName();
            Idea myversion = this.get(name);
            if (myversion != null) {
                 boolean testsub = myversion.equivalent(id,false);
//                 if (testsub == false) {
//                      System.out.println("Idea "+this.getFullName()+" has an internal subfield missing");
//                 }
                 out &= testsub;
            }
            else {
                out = false;
//                System.out.println("Idea "+this.getFullName()+" does not have subfield "+name);
            }
          }
        }
        return(out);
    }
    
    public boolean equals(Idea other) {
        return(equals(other,true));
    }
    
    private boolean equals(Idea other, boolean reset) {
        boolean ae;
        if (reset) reset();
        ae = already_exists(other);
        if (ae) return(true);
        if (this != null && other == null) return(false);
        boolean out = true;
        out &= this.getFullName().equals(other.getFullName());
        if (out == false) {
            System.out.println("Idea names are different ..."+this.getFullName()+" "+other.getFullName());
            return(false);
        }
        if (this.getValue() == null && other.getValue() != null ||
            this.getValue() != null && other.getValue() == null) {
            String valuetypethis;
            if (this.getValue() != null) valuetypethis = this.getValue().getClass().getCanonicalName();
            else valuetypethis = "null";
            String valuetypeother;
            if (other.getValue() != null) valuetypeother = other.getValue().getClass().getCanonicalName();
            else valuetypeother = "null";
            System.out.println("Values are different ..."+this.getName()+": "+this.getValue()+" ("+valuetypethis+") "+other.getName()+": "+other.getValue()+" ("+valuetypeother+")");
            return(false);
        }
        else if (this.getValue() == null && other.getValue() == null) {
            // do nothing
        }
        else {
            boolean valuediff;
            if (this.isNumber() && other.isNumber()) {
                //System.out.println("OK, "+this.getName()+" and "+other.getName()+" are both numbers");
                BigDecimal t = new BigDecimal(0);
                if (this.getValue() instanceof Integer) t = new BigDecimal((Integer)this.getValue());
                else if (this.getValue() instanceof Long) t = new BigDecimal((Long)this.getValue());
                else if (this.getValue() instanceof Short) t = new BigDecimal((Short)this.getValue());
                else if (this.getValue() instanceof Byte) t = new BigDecimal((Byte)this.getValue());
                else if (this.getValue() instanceof Float) t = new BigDecimal((Float)this.getValue());
                else if (this.getValue() instanceof Double) t = new BigDecimal((Double)this.getValue());
                BigDecimal o = new BigDecimal(0);
                if (other.getValue() instanceof Integer) o = new BigDecimal((Integer)other.getValue());
                else if (other.getValue() instanceof Long) o = new BigDecimal((Long)other.getValue());
                else if (other.getValue() instanceof Short) o = new BigDecimal((Short)other.getValue());
                else if (other.getValue() instanceof Byte) o = new BigDecimal((Byte)other.getValue());
                else if (other.getValue() instanceof Float) o = new BigDecimal((Float)other.getValue());
                else if (other.getValue() instanceof Double) o = new BigDecimal((Double)other.getValue());
                //valuediff = t.compareTo(o) == 0;
                valuediff = t.floatValue() == o.floatValue();
                if (valuediff == false) {
                    System.out.println("t."+this.getFullName()+": "+t+" o."+other.getFullName()+": "+o);
                }    
                   
            }
            else {
                valuediff = this.getValue().equals(other.getValue());
                if (valuediff == false) System.out.println("Hmmm, "+this.getName()+" and "+other.getName()+" are not both numbers");
            }
           if (valuediff == false) System.out.println("Values are different ..."+this.getName()+": "+this.getValue()+" ("+this.getValue().getClass().getCanonicalName()+") "+other.getName()+": "+other.getValue()+" ("+other.getValue().getClass().getCanonicalName()+")");
           out &= valuediff;
           if (out == false) return(out);
        }
        if (out == false) {
            System.out.println("I am false, but I don't know why !");
            return(false);
        }
        long lt = this.getType();
        long lo = other.getType();
        out &= lt == lo;
        if (out == false) {
            System.out.println("Types are different ..."+this.getName()+": "+lt+" "+other.getName()+": "+lo);
            return(out);
        }
        if (this.getCategory() == null && other.getCategory() != null ||
            this.getCategory() != null && other.getCategory() == null) {
            System.out.println("Categories are different ..."+this.getName()+": "+this.getCategory()+" "+other.getName()+": "+other.getCategory());
            return(false);
        }
        else if (this.getCategory() == null && other.getCategory() == null) {
            // do nothing
        }
        else { 
            out &= this.getCategory().equals(other.getCategory());
            if (out == false) {
                System.out.println("Categories are different ..."+this.getName()+": "+this.getCategory()+" "+other.getName()+": "+other.getCategory());
                return(out);
            }
        }   
        out &= this.getBelief() == other.getBelief();
        if (out == false) {
            System.out.println("Beliefs are different ..."+this.getName()+": "+this.getBelief()+" "+other.getName()+": "+other.getBelief());
            return(out);
        }
        out &= this.getThreshold() == other.getThreshold();
        if (out == false) {
            System.out.println("Belief Threasholds are different ..."+this.getName()+": "+this.getThreshold()+" "+other.getName()+": "+other.getThreshold());
            return(out);
        }
        if (out == true) listtoavoidloops.add(other);
        if (this.getL().size() == other.getL().size()) {
             for (int i=0;i<this.getL().size();i++) {
                 boolean testsub = this.getL().get(i).equals(other.getL().get(i),false);
                 if (testsub == false) {
                     //System.out.println("Difference in the internal fields of "+this.getName()+" idea ... "+this.getL().get(i).getFullName()+": "+this.getL().get(i).getValue()+" "+other.getL().get(i).getFullName()+": "+other.getL().get(i).getValue());
                 }
                 out &= testsub;
             }
        }
        else {
            System.out.println("Internal lists of Ideas "+this.getName()+"(this) and "+other.getName()+"(other) has different number of elements ... "+this.getName()+"(this): "+this.getL().size()+" "+other.getName()+"(other): "+other.getL().size());
            return(false);
        }
        return(out);        
    }
    
    
}
