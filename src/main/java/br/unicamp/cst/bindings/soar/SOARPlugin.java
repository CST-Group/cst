/*******************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * Contributors to this module:
 *     W. Gibaut, R. R. Gudwin 
 ******************************************************************************/

package br.unicamp.cst.bindings.soar;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.google.common.primitives.Doubles;
import org.jsoar.kernel.Agent;
import org.jsoar.kernel.Phase;
import org.jsoar.kernel.RunType;
import org.jsoar.kernel.memory.Wme;
import org.jsoar.kernel.memory.Wmes;
import org.jsoar.kernel.symbols.DoubleSymbol;
import org.jsoar.kernel.symbols.Identifier;
import org.jsoar.kernel.symbols.IdentifierImpl;
import org.jsoar.kernel.symbols.StringSymbol;
import org.jsoar.kernel.symbols.Symbol;
import org.jsoar.kernel.symbols.SymbolFactory;
import org.jsoar.kernel.symbols.SymbolFactoryImpl;
import org.jsoar.runtime.ThreadedAgent;
import org.jsoar.util.commands.SoarCommands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import br.unicamp.cst.representation.wme.Idea;

/**
 * @author wander
 */

public class SOARPlugin {

    Logger logger = Logger.getLogger(SOARPlugin.class.getName());

    private ThreadedAgent threaded;

    private Agent agent;

    private Identifier inputLinkIdentifier;

    private Idea inputLinkIdea;

    private Idea outputLinkIdea;

    // Ordinary Variables
    private String agentName;
    private File productionPath;
    private String inputLinkAsString = "";
    private String outputLinkAsString = "";
    private String jsonOutputLinkAsString = "";

    private int phase = -1;
    private int debugState = 0;

    private List<Identifier> operatorsPathList = new ArrayList<>();

    public SOARPlugin() {
    }

    public SOARPlugin(String _agentName, File _productionPath, Boolean startSOARDebugger) {
        try {
            // Inicial variables
            setAgentName(_agentName);
            setProductionPath(_productionPath);

            // create Soar kernel And Agent
            Agent agent = new Agent();
            agent.setName(_agentName);
            setAgent(agent);

            // Load some productions
            String path = getProductionPath().getAbsolutePath();
            SoarCommands.source(getAgent().getInterpreter(), path);
            setInputLinkIdentifier(getAgent().getInputOutput().getInputLink());

            // Start the Debugger if it is the case
            if (startSOARDebugger) {
                getAgent().openDebugger();
            }

        } catch (Exception e) {
            logger.severe("Error while creating SOAR Kernel" + e);
        }

    }
    
    /**************************************************/
    
    /**
     * Perform a complete SOAR step
     * 
     */
    public void step() 
    {
        if (phase != -1) finish_msteps();
        dofullcycle();
        processOutputLink();
    }
    
    public void prepare_mstep() {
        resetSimulation();
        processInputLink(); // Transform AO into WMEs
    }

    int oldphase = -1;
    public void mstep()
    {
        if (phase == -1) prepare_mstep();
        getWMEStringInput(); // Copy InputLink into the a String readable version
        phase = stepSOAR(1,RunType.PHASES);
        getWMEStringOutput(); // Copy OutputLink into the a String readable version
        if (getPhase() == 3 && getDebugState() == 1) {
            getOperatorsPathList().addAll(getOperatorsInCurrentPhase(getStates()));
        }
        if (phase == 5 || phase == oldphase) { // oldphase included to avoid infinite recursion
            post_mstep();
            phase = -1;
        }
        oldphase = phase;
    }
    
    public void finish_msteps() {
        while (phase != -1) mstep();
    }
    
    public void dofullcycle() {
        do mstep(); while (phase != -1); 
    }
    
    public void post_mstep()  {
        setOperatorsPathList(new ArrayList<>());
    }
    
    /*************************************************/

    public void moveToFinalStep() {
        while (getPhase() != -1) {
            step();
        }
    }

    public void finalizeKernel() {
        try {
            getAgent().dispose();
        } catch (Exception e) {
            logger.severe("Error while shuting down SOAR" + e);
        }
    }

    public void resetSOAR() {
        getAgent().initialize();
        setInputLinkIdentifier(getAgent().getInputOutput().getInputLink());
    }

    public void stopSOAR() {
        getAgent().stop();
    }


    public void runSOAR() {
        try {
            if (getAgent() != null) {
                step();
            }
        } catch (Exception e) {
            logger.severe("Error while running SOAR step" + e);
        }
    }


    public void resetSimulation() {
        getAgent().initialize();
    }

    protected int stepSOAR(int i, RunType type) {
        getAgent().runFor(i, type);
        return getCurrentPhase();
    }

    private int getCurrentPhase() {

        Phase ph = getAgent().getCurrentPhase();

        if (ph.equals(Phase.INPUT)) return (0);
        else if (ph.equals(Phase.PROPOSE)) return (1);
        else if (ph.equals(Phase.DECISION)) return (2);
        else if (ph.equals(Phase.APPLY)) return (3);
        else if (ph.equals(Phase.OUTPUT)) {
            if (getAgent().getReasonForStop() == null) return (4);
            else return (5);
        } else return (6);

    }

    public void printWMEs(List<Wme> Commands) {
        String s = getWMEsAsString(Commands);
        System.out.println(s);
    }

    public String getWMEsAsString(List<Wme> Commands) {
        String result = "";
        String preference = "";
        for (Wme wme : Commands) {
            preference = "";
            if (wme.isAcceptable()) preference = " +";
            result += "(" + wme.getIdentifier().toString() + "," + wme.getAttribute().toString() + "," + wme.getValue().toString() + preference + ")\n   ";
            Iterator<Wme> children = wme.getChildren();
            while (children.hasNext()) {
                Wme child = children.next();
                preference = "";
                if (child.isAcceptable()) preference = " +";
                result += "(" + child.getIdentifier().toString() + "," + child.getAttribute().toString() + "," + child.getValue().toString() + preference + ")\n   ";
            }
        }
        return (result);
    }

    public List<Identifier> getOperatorsInCurrentPhase(List<Identifier> identifiers) {

        ArrayList<Identifier> operators = new ArrayList<>();

        for (Identifier id : identifiers) {
            Iterator<Wme> it = id.getWmes();
            while (it.hasNext()) {
                Wme wme = it.next();
                if (wme.getAttribute().asString().getValue().toLowerCase().equals("operator") && !wme.isAcceptable()) {
                    Symbol v = wme.getValue();
                    operators.add(v.asIdentifier());
                }
            }
        }

        return operators;
    }

    public List<Wme> getOutputLink_WME() {
        return Wmes.matcher(getAgent()).filter(getAgent().getInputOutput().getOutputLink());
    }

    public List<Wme> getInputLink_WME() {
        return Wmes.matcher(getAgent()).filter(getAgent().getInputOutput().getInputLink());
    }

    public JsonObject getOutputLinkJSON() {
        JsonObject json = new JsonObject();
        try {
            if (getAgent() != null) {
                List<Wme> Commands = getOutputLink_WME();
                setOutputLinkAsString(getWMEStringOutput());
                for (Wme command : Commands) {
                    String commandType = command.getAttribute().toString();
                    json.add(commandType, new JsonObject());

                    Iterator<Wme> children = command.getChildren();
                    while (children.hasNext()) {
                        Wme child = children.next();
                        String parameter = child.getAttribute().toString();
                        String parvalue = child.getValue().toString();
                        Float floatvalue = tryParseFloat(parvalue);
                        if (floatvalue != null) {
                            json.get(commandType).getAsJsonObject().addProperty(parameter, floatvalue);
                        } else {
                            json.get(commandType).getAsJsonObject().addProperty(parameter, parvalue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.severe("Error while creating SOAR Kernel" + e);
        }
        setJsonOutputLinkAsString(json.toString());
        return (json);
    }


    /**
     * Try Parse a Float Element
     *
     * @param value Float Value
     * @return The Float Value or null otherwise
     */
    private Float tryParseFloat(String value) {
        Float returnValue;

        try {
            returnValue = Float.parseFloat(value);
        } catch (Exception ex) {
            returnValue = null;
        }

        return returnValue;
    }

    //CreateWME methods
    public Identifier createIdWME(Identifier id, String s) {
        SymbolFactoryImpl sf = (SymbolFactoryImpl) getAgent().getSymbols();
        Identifier newID = sf.createIdentifier('W');
        getAgent().getInputOutput().addInputWme(id, sf.createString(s), newID);
        return (newID);
    }

    public void createFloatWME(Identifier id, String s, double value) {
        SymbolFactory sf = getAgent().getSymbols();
        DoubleSymbol newID = sf.createDouble(value);
        getAgent().getInputOutput().addInputWme(id, sf.createString(s), newID);
    }

    public void createStringWME(Identifier id, String s, String value) {
        SymbolFactory sf = getAgent().getSymbols();
        StringSymbol newID = sf.createString(value);
        getAgent().getInputOutput().addInputWme(id, sf.createString(s), newID);
    }

    public Identifier getOutputLinkIdentifier() {
        return getAgent().getInputOutput().getOutputLink();
    }

    public Identifier getInputLinkIdentifier() {
        return inputLinkIdentifier;
    }

    public Wme getInputLinkWme(){
        return Wmes.matcher(getAgent()).filter(getAgent().getInputOutput().getInputLink()).get(0);
    }

    public synchronized List<Identifier> getStates() {
        List<Identifier> li = new ArrayList<Identifier>();

        synchronized (getAgent().getAllWmesInRete()) {

            Set<Wme> allmem = Collections.synchronizedSet(getAgent().getAllWmesInRete());

            for (Wme w : allmem) {
                
                Identifier id = w.getIdentifier();
                if (id.isGoal()) {
                    boolean alreadythere = false;
                    for (Identifier icand : li)
                        if (icand == id) alreadythere = true;
                    if (alreadythere == false) {
                        li.add(id);
                    }
                }
            }
        }

        return (li);
    }


    public JsonObject createJsonFromString(String pathToLeaf, double value) {
        String[] treeNodes = pathToLeaf.split("\\.");
        JsonObject json = new JsonObject();

        for (int i = treeNodes.length - 1; i >= 0; i--) {
            JsonObject temp = new JsonObject();

            if (i == treeNodes.length - 1) {
                temp.addProperty(treeNodes[i], value);
            } else {
                temp.add(treeNodes[i], json);
            }
            json = temp;
        }
        return json;
    }

    public JsonObject createJsonFromString(String pathToLeaf, String value) {
        String[] treeNodes = pathToLeaf.split("\\.");
        JsonObject json = new JsonObject();

        for (int i = treeNodes.length - 1; i >= 0; i--) {
            JsonObject temp = new JsonObject();

            if (i == treeNodes.length - 1) {
                temp.addProperty(treeNodes[i], value);
            } else {
                temp.add(treeNodes[i], json);
            }
            json = temp;
        }
        return json;
    }


    public JsonObject createJsonFromString(String pathToLeaf, JsonObject value) {
        String[] treeNodes = pathToLeaf.split("\\.");
        JsonObject json = new JsonObject();

        for (int i = treeNodes.length - 1; i >= 0; i--) {
            JsonObject temp = new JsonObject();

            if (i == treeNodes.length - 1) {
                temp.add(treeNodes[i], value);
            } else {
                temp.add(treeNodes[i], json);
            }
            json = temp;
        }
        return json;
    }

    public Object createIdeaFromJson(JsonObject jsonInput){
        Set<Map.Entry<String, JsonElement>> entryset = jsonInput.entrySet();
        Entry<String, JsonElement> entry;
        Object value = null;
        Iterator<Entry<String, JsonElement>> itr = entryset.iterator();

        ArrayList<Idea> answerList = new ArrayList<>();
        while (itr.hasNext()) {
            entry = itr.next();
            String key = entry.getKey();

            if (entry.getValue().isJsonPrimitive()) {

                if (entry.getValue().getAsJsonPrimitive().isNumber()) {
                    value = (double) entry.getValue().getAsJsonPrimitive().getAsDouble();
                } else if (entry.getValue().getAsJsonPrimitive().isString()) {
                    value = (String) entry.getValue().getAsJsonPrimitive().getAsString();
                } else if (entry.getValue().getAsJsonPrimitive().isBoolean()) {
                    value = (Boolean) entry.getValue().getAsJsonPrimitive().getAsBoolean();
                }
                answerList.add(Idea.createIdea(key, value, 0));

            } else if (entry.getValue().isJsonObject()) {
                if (entry.getValue().getAsJsonObject().size() == 0) {
                    continue;
                }
                Idea answer = Idea.createIdea(key, "", 0);
                Object nested = createIdeaFromJson(entry.getValue().getAsJsonObject());

                if (nested instanceof ArrayList) {
                    for (Object idea : (ArrayList)nested){
                        answer.add((Idea) idea);
                    }
                }
                else{
                    answer.add((Idea) nested);
                }
                answerList.add(answer);
            }
        }
        if (answerList.size() == 1){
            return answerList.get(0);
        }
        return answerList;
    }


    public void addBranchToJson(String newBranch, JsonObject json, double value) {
        String[] newNodes = newBranch.split("\\.");
        JsonObject temp;

        if (newNodes.length > 1) {
            if (json.has(newNodes[0])) {
                addBranchToJson(newBranch.substring(newNodes[0].length() + 1), json.getAsJsonObject(newNodes[0]), value);
            } else {
                temp = createJsonFromString(newBranch.substring(newNodes[0].length() + 1), value);
                json.add(newNodes[0], temp);
            }
        } else {
            json.addProperty(newNodes[0], value);
        }
    }

    public void addBranchToJson(String newBranch, JsonObject json, String value) {
        String[] newNodes = newBranch.split("\\.");
        JsonObject temp;

        if (newNodes.length > 1) {
            if (json.has(newNodes[0])) {
                addBranchToJson(newBranch.substring(newNodes[0].length() + 1), json.getAsJsonObject(newNodes[0]), value);
            } else {
                temp = createJsonFromString(newBranch.substring(newNodes[0].length() + 1), value);
                json.add(newNodes[0], temp);
            }
        } else {
            json.addProperty(newNodes[0], value);
        }
    }

    public void addBranchToJson(String newBranch, JsonObject json, JsonObject value) {
        String[] newNodes = newBranch.split("\\.");
        JsonObject temp;
        if (newNodes.length > 1) {
            if (json.has(newNodes[0])) {
                addBranchToJson(newBranch.substring(newNodes[0].length() + 1), json.getAsJsonObject(newNodes[0]), value);
            } else {
                temp = createJsonFromString(newBranch.substring(newNodes[0].length() + 1), value);
                json.add(newNodes[0], temp);
            }
        } else {
            json.add(newNodes[0], value);
        }
    }


    public void removeBranchFromJson(String pathToOldBranch, JsonObject json) {
        String[] oldNodes = pathToOldBranch.split("\\.");
        if (oldNodes.length > 1) {
            if (json.has(oldNodes[0])) {
                removeBranchFromJson(pathToOldBranch.substring(oldNodes[0].length() + 1), json.getAsJsonObject(oldNodes[0]));
            }
        } else {
            json.remove(oldNodes[0]);
        }
    }



    public JsonObject fromBeanToJson(Object bean) {
        JsonObject json = new JsonObject();
        Class<?> type = bean.getClass();

        json.add(type.getName(), new JsonObject());
        try {
            for (Field field : type.getDeclaredFields()) {
                json.get(type.getName()).getAsJsonObject().addProperty(field.getName(), field.get(bean).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public boolean containsWme(final List<Wme> list, final String name) {
        boolean found;
        for (Wme wme : list) {
            found = hasWMEChild(wme, name);
            if (found) return true;
        }
        return false;
    }

    public boolean hasWMEChild(Wme rootWME, String name){
        boolean found = false;

        Iterator<Wme> children = rootWME.getChildren();
        while(children.hasNext()){
            Wme child = children.next();
            if(child.getAttribute().toString().equals(name)){
                return true;
            }
            else if(child.getChildren().hasNext()){
                found = hasWMEChild(child, name);
            }
        }
        return found;
    }


    public String toPrettyFormat(JsonObject json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);
        return prettyJson;
    }
    
    /* -----------------------------------------------------------------------
    Beginning of WME Print Support methods
    -------------------------------------------------------------------------*/

    public void printWME(Identifier id) {
        String ids = getWMEString(id);
        System.out.println(ids);

    }

    public void printInputWMEs() {
        Identifier il = getAgent().getInputOutput().getInputLink();
        System.out.println("Input --->");
        printWME(il);
    }

    public void printOutputWMEs() {
        Identifier ol = getAgent().getInputOutput().getOutputLink();
        System.out.println("Output --->");
        printWME(ol);
    }
    
    /* -----------------------------------------------------------------------
    Beginning of WME String Support methods
    -------------------------------------------------------------------------*/

    public String getWMEString(Identifier id) {
        return (getWMEString(id, 0));
    }

    public String getWMEString(Identifier id, int level) {
        String out = "";
        Iterator<Wme> It = id.getWmes();
        while (It.hasNext()) {
            Wme wme = It.next();
            Identifier idd = wme.getIdentifier();
            Symbol a = wme.getAttribute();
            Symbol v = wme.getValue();
            Identifier testv = v.asIdentifier();
            for (int i = 0; i < level; i++)
                out += "   ";

            if (testv != null) {
                out += "(" + idd.toString() + "," + a.toString() + "," + v.toString() + ")\n";
                out += getWMEString(testv, level + 1);
            } else
                out += "(" + idd.toString() + "," + a.toString() + "," + v.toString() + ")\n";
        }
        return (out);
    }

    public String getWMEStringInput() {
        String out = "";
        Identifier il = getAgent().getInputOutput().getInputLink();
        out += getWMEString(il);
        setInputLinkAsString(out);
        return (out);
    }

    public String getWMEStringOutput() {
        String out = "";
        Identifier ol = getAgent().getInputOutput().getOutputLink();
        out += getWMEString(ol);
        setOutputLinkAsString(out);
        return (out);
    }
    
    public Identifier getInitialState() {
        List<Identifier> ids = getStates();
        Identifier s1 = null;
        for (Identifier id : ids) {
            if (id.getNameLetter() == 'S' && id.getNameNumber() == 1)
                s1 = id;
        }
        return(s1);
    }
    
    /* -----------------------------------------------------------------------
    Beginning of WorldObject Support methods
    -------------------------------------------------------------------------*/

    public Idea getWorldObject(Identifier id, String name) {
        Idea newwo = null;
        Iterator<Wme> It = id.getWmes();
        if (!It.hasNext()) {
            // This situation happens when the OutputLink is empty
            newwo = Idea.createIdea(name,"",0);
        }
        while (It.hasNext()) {

            if (newwo == null) {
                newwo = Idea.createIdea(name,"",phase);
            }

            Wme wme = It.next();
            Symbol a = wme.getAttribute();
            Symbol v = wme.getValue();
            Identifier testv = v.asIdentifier();
            if (testv != null) { // The value is an identifier
                Idea child = getWorldObject(testv, a.toString());
                newwo.add(child);
            } else { // The value is a property
                Idea qd;
                Object value;
                if (v.asDouble() != null) value = v.asDouble().getValue();
                else if (v.asInteger() != null) value = v.asInteger().getValue();
                else value = v.toString();
                qd = new Idea(a.toString(), value);
                Idea pp = new Idea(a.toString(), qd);
                newwo.add(pp);
            }
        }
        return (newwo);
    }

    public void processOutputLink() {
        Identifier ol = getAgent().getInputOutput().getOutputLink();
        if (ol == null) logger.severe("Error in cst.SOARPlugin: Unable to get access to OutputLink");

        Idea olao = getWorldObject(ol, "OutputLink");
        setOutputLinkIdea(olao);
    }

    public void processInputLink() {
        setInputLinkIdentifier(getAgent().getInputOutput().getInputLink());
        ((IdentifierImpl) getInputLinkIdentifier()).removeAllInputWmes();
        SymbolFactoryImpl sf = (SymbolFactoryImpl) getAgent().getSymbols();
        sf.reset();
        processInputLink(getInputLinkIdea(), getInputLinkIdentifier());
    }

    public void processInputLink(Idea il, Identifier id) {
        if (il != null) {
            List<Idea> parts = il.getL();
            for (Idea w : parts) {
                if (w.getValue().equals("") ){
                    Identifier id2 = createIdWME(id, w.getName());
                    processInputLink(w, id2);
                }
                else{
                    Object value;

                    if (Doubles.tryParse(w.getValue().toString()) != null) {
                        value = Doubles.tryParse(w.getValue().toString());
                        createFloatWME(id, w.getName(), (double) value);
                    }  else if (w.getValue() instanceof String) {
                        value = (String) w.getValue();
                        createStringWME(id, w.getName(), (String) value);
                    }
                }
            }
        }

    }


    public Identifier searchInInputOutputLink(String idName, Identifier id) {
        Wme wme = searchInInputOutputLinkWME(idName, id);
        if (wme == null){ return null;}

        return wme.getIdentifier();
    }

    public Wme searchInInputOutputLinkWME(String idName, Identifier id) {

        List<Wme> wmes = Wmes.matcher(getAgent()).filter(id);

        Wme resultId = null;

        for (Wme wme : wmes) {
            Symbol a = wme.getAttribute();
            Symbol v = wme.getValue();

            if (a.toString().equals(idName)) {
                resultId = wme;
                break;
            } else {
                if (v.asIdentifier() != null) {
                    resultId = searchInInputOutputLinkWME(idName, v.asIdentifier());
                } else {
                    resultId = null;
                }
            }
        }

        return resultId;

    }

    
    /* -----------------------------------------------------------------------
    Beginning of JavaBeans Support methods
    -------------------------------------------------------------------------*/

    public Object createJavaObject(String classname) {
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

    public boolean isNumber(Object o) {
        String objectClass = o.getClass().getName();
        if (objectClass.equals("java.lang.Double") ||
                objectClass.equals("java.lang.Float") ||
                objectClass.equals("double") ||
                objectClass.equals("float") ||
                objectClass.equals("java.lang.Long") ||
                objectClass.equals("java.lang.Integer") ||
                objectClass.equals("long") ||
                objectClass.equals("integer"))
            return (true);
        else return (false);
    }

    public Object convertObject(Object origin, String className) {
        String objectClass = origin.getClass().getName();
        try {
            switch (className) {
                case "double":
                case "java.lang.Double": {
                    double value;
                    if (objectClass.equals("java.lang.String")) {
                        value = Double.parseDouble((String) origin);
                    } else {
                        value = ((Number) origin).doubleValue();
                    }
                    return (value);
                }
                case "float":
                case "java.lang.Float": {
                    float value;
                    if (objectClass.equals("java.lang.String")) {
                        value = tryParseFloat((String) origin);
                    } else {
                        value = ((Number) origin).floatValue();
                    }
                    return (value);
                }
                case "long":
                case "java.lang.Long": {
                    long value;
                    if (objectClass.equals("java.lang.String")) {
                        value = Long.parseLong((String) origin);
                    } else {
                        value = ((Number) origin).longValue();
                    }
                    return (value);
                }
                case "int":
                case "java.lang.Integer": {
                    int value;
                    if (objectClass.equals("java.lang.String")) {
                        value = Integer.parseInt((String) origin);
                    } else {
                        value = ((Number) origin).intValue();
                    }
                    return (value);
                }
                case "short":
                case "java.lang.Short": {
                    short value;
                    if (objectClass.equals("java.lang.String")) {
                        value = Short.parseShort((String) origin);
                    } else {
                        value = ((Number) origin).shortValue();
                    }
                    return (value);
                }
                default:
                    return (origin);
            }
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void setField(Object o, String fieldName, Object value) {
        Class type = o.getClass();
        try {
            Field[] fieldList = type.getFields();
            for (Field field : fieldList) {
                String fieldClass = field.getType().getCanonicalName();
                if (field.getName().equals(fieldName)) {
                    field.set(o, convertObject(value, fieldClass));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception: " + e.getStackTrace().toString());
        }
    }

    public Object getJavaObject(Wme toGetJava, Object parent, String package_with_beans_classes) {
        
        Object javaObject = null;

        Iterator<Wme> It = toGetJava.getChildren();
        while (It.hasNext()) {
            Wme wme = It.next();
            Symbol a = wme.getAttribute();
            Symbol v = wme.getValue();
            Identifier testv = v.asIdentifier();
            if (testv != null) { // The value is an identifier: recursion
                Object child = createJavaObject(package_with_beans_classes + "." + a.toString());
                if (parent != null) setField(parent, a.toString(), child);
                javaObject = getJavaObject(wme, child, package_with_beans_classes);
            } else { // The value is a property
                Object value;
                if (v.asDouble() != null) value = v.asDouble().getValue();
                else if (v.asInteger() != null) value = v.asInteger().getValue();
                else value = v.toString();
                if (parent != null) {
                    setField(parent, a.toString(), value);
                }
                else if(javaObject == null){
                    javaObject = createJavaObject(package_with_beans_classes + "." + toGetJava.getAttribute().toString());
                    setField(javaObject, a.toString(), value);
                }
                else{setField(javaObject, a.toString(), value);}
            }
        }
        if (parent == null) return (javaObject);
        else return (parent);
    }
    
    public void loadRules(String path) {
        try {
           SoarCommands.source(getAgent().getInterpreter(), path);
           resetSOAR();
           
        } catch (Exception e) {e.printStackTrace();}
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public File getProductionPath() {
        return productionPath;
    }

    public void setProductionPath(File productionPath) {
        this.productionPath = productionPath;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public ThreadedAgent getThreaded() {
        return threaded;
    }

    public void setThreaded(ThreadedAgent threaded) {
        this.threaded = threaded;
    }

    public String getInputLinkAsString() {
        return inputLinkAsString;
    }

    public void setInputLinkAsString(String inputLinkAsString) {
        this.inputLinkAsString = inputLinkAsString;
    }

    public String getOutputLinkAsString() {
        return outputLinkAsString;
    }

    public void setOutputLinkAsString(String outputLinkAsString) {
        this.outputLinkAsString = outputLinkAsString;
    }

    public void setInputLinkIdentifier(Identifier inputLinkIdentifier) {
        this.inputLinkIdentifier = inputLinkIdentifier;
    }

    public int getDebugState() {
        return debugState;
    }

    public void setDebugState(int debugState) {
        this.debugState = debugState;
    }

    public String getJsonOutputLinkAsString() {
        return jsonOutputLinkAsString;
    }

    public void setJsonOutputLinkAsString(String jsonOutputLinkAsString) {
        this.jsonOutputLinkAsString = jsonOutputLinkAsString;
    }

    public List<Identifier> getOperatorsPathList() {
        return operatorsPathList;
    }

    public void setOperatorsPathList(List<Identifier> operatorsPathList) {
        this.operatorsPathList = operatorsPathList;
    }

    public Idea getInputLinkIdea() {
        return inputLinkIdea;
    }

    public void setInputLinkIdea(Idea inputLinkAO) {
        this.inputLinkIdea = inputLinkAO;
    }

    public Idea getOutputLinkIdea() {
        return outputLinkIdea;
    }

    public void setOutputLinkIdea(Idea outputLinkAO) {
        this.outputLinkIdea = outputLinkAO;
    }
}
