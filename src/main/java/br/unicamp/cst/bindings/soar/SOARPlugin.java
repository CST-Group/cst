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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.jsoar.kernel.Agent;
import org.jsoar.kernel.Phase;
import org.jsoar.kernel.RunType;
import org.jsoar.kernel.io.InputWme;
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

import br.unicamp.cst.representation.owrl.AbstractObject;
import br.unicamp.cst.representation.owrl.Property;
import br.unicamp.cst.representation.owrl.QualityDimension;

/**
 * @author wander
 */

public class SOARPlugin {

    Logger logger = Logger.getLogger(SOARPlugin.class.getName());

    private ThreadedAgent threaded;

    private Agent agent;

    private Identifier inputLinkIdentifier;

    private AbstractObject inputLinkAO;

    private AbstractObject outputLinkAO;

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
            //setThreaded(ThreadedAgent.create());
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
    
    //int kk=0;
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
        //System.out.println(kk++ + " Micro-step: "+phase);
    }
    
    public void finish_msteps() {
        while (phase != -1) mstep();
    }
    
    public void dofullcycle() {
        do mstep(); while (phase != -1); 
    }
    
    public void post_mstep()  {
        processOutputLink();
        try {
                Thread.sleep(1); // why this is needed ? 
        } catch (InterruptedException e) {
                e.printStackTrace();
        }
        setOperatorsPathList(new ArrayList<>());
    }
    
    
    
    /*************************************************/

    public void step_old() {
        Date initDate=null;
        
        if (getDebugState() == 1) {
            initDate = new Date();
            if (getPhase() == 0) processInputLink();           
            setPhase(stepSOAR(1, RunType.PHASES));
        }    
        else {
            //System.out.println("Starting SOAR step");
            resetSimulation();
            processInputLink();
            getWMEStringInput();
            //System.out.println("Before:\n"+getWMEString(getInitialState()));
            runSOAR();
            //System.out.println("After:\n"+getWMEString(getInitialState()));
            processOutputLink();
            getWMEStringOutput();
            //System.out.println("Finishing SOAR step");
        }
            
        if (getPhase() == 3 && getDebugState() == 1) {
            getOperatorsPathList().addAll(getOperatorsInCurrentPhase(getStates()));
        }

        if (getPhase() == 5) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setPhase(-1);
            resetSimulation();
            setOperatorsPathList(new ArrayList<>());
        }

        if(getDebugState() == 1) {
            double diff = (new Date()).getTime() - initDate.getTime();
            logger.info("Time of Soar Cycle :" + diff);
        }
        	
    }

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
        setInputLinkAsString(getWMEStringInput());
        try {
            if (getAgent() != null) {
                getAgent().runForever();
            }
        } catch (Exception e) {
            logger.severe("Error while running SOAR step" + e);
        }
        setOutputLinkAsString(getWMEStringOutput());
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

    private String getParameterValue(String par) {
        List<Wme> Commands = Wmes.matcher(getAgent()).filter(getAgent().getInputOutput().getOutputLink());
        List<Wme> Parameters = Wmes.matcher(getAgent()).filter(Commands.get(0));
        String parvalue = "";
        for (Wme w : Parameters)
            if (w.getAttribute().toString().equals(par)) parvalue = w.getValue().toString();
        return (parvalue);
    }

    public void printWMEs(List<Wme> Commands) {
        String s = getWMEsAsString(Commands);
        System.out.println(s);
//        for (Wme wme : Commands) {
//            System.out.print("(" + wme.getIdentifier().toString() + "," + wme.getAttribute().toString() + "," + wme.getValue().toString() + ")\n");
//            Iterator<Wme> children = wme.getChildren();
//            while (children.hasNext()) {
//                Wme child = children.next();
//                System.out.print("(" + child.getIdentifier().toString() + "," + child.getAttribute().toString() + "," + child.getValue().toString() + ")\n");
//            }
//        }
    }

    public String getWMEsAsString(List<Wme> Commands) {
        String result = "";
        String preference = "";
        for (Wme wme : Commands) {
            preference = "";
            if (wme.isAcceptable()) preference = " +";
            result += "(" + wme.getIdentifier().toString() + "," + wme.getAttribute().toString() + "," + wme.getValue().toString() + preference + ")\n";
            Iterator<Wme> children = wme.getChildren();
            while (children.hasNext()) {
                Wme child = children.next();
                preference = "";
                if (child.isAcceptable()) preference = " +";
                result += "(" + child.getIdentifier().toString() + "," + child.getAttribute().toString() + "," + child.getValue().toString() + preference + ")\n";
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
        List<Wme> olwme = Wmes.matcher(getAgent()).filter(getAgent().getInputOutput().getOutputLink());
        return (olwme);
    }

    public List<Wme> getInputLink_WME() {
        List<Wme> ilwme = Wmes.matcher(getAgent()).filter(getAgent().getInputOutput().getInputLink());
        return (ilwme);
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
                    String parameter = command.getAttribute().toString();
                    String parvalue = command.getValue().toString();
                    Iterator<Wme> children = command.getChildren();
                    while (children.hasNext()) {
                        Wme child = children.next();
                        parameter = child.getAttribute().toString();
                        parvalue = child.getValue().toString();
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


    //CreateWME methods
    public Identifier createIdWME(Identifier id, String s) {
        SymbolFactoryImpl sf = (SymbolFactoryImpl) getAgent().getSymbols();
        Identifier newID = sf.createIdentifier('W');
        getAgent().getInputOutput().addInputWme(id, sf.createString(s), newID);
        return (newID);
    }

    public Identifier createIdWME(String s) {
        return (createIdWME(getInputLinkIdentifier(), s));
    }

    public void createFloatWME(Identifier id, String s, double value) {
        SymbolFactory sf = getAgent().getSymbols();
        DoubleSymbol newID = sf.createDouble(value);
        getAgent().getInputOutput().addInputWme(id, sf.createString(s), newID);
    }

    public void createFloatWME(String s, double value) {
        createFloatWME(getInputLinkIdentifier(), s, value);
    }

    public InputWme createStringWME(Identifier id, String s, String value) {
        SymbolFactory sf = getAgent().getSymbols();
        StringSymbol newID = sf.createString(value);
        return getAgent().getInputOutput().addInputWme(id, sf.createString(s), newID);
    }

    public void createStringWME(String s, String value) {
        createStringWME(getInputLinkIdentifier(), s, value);
    }

    public Identifier getOutputLinkIdentifier() {
        Identifier ol = getAgent().getInputOutput().getOutputLink();
        return (ol);
    }

    public Identifier getInputLinkIdentifier() {
        return inputLinkIdentifier;
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

    //organizar
    private void removeWME(Identifier Attribute) {
        while (getAgent().getInputOutput().getInputLink().getWmes().hasNext()) {
            Wme candidate = getAgent().getInputOutput().getInputLink().getWmes().next();
            if (candidate.getAttribute() == Attribute) {
                getAgent().getInputOutput().getInputLink().getWmes().remove();
            }
        }
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

    public void buildWmeInputTreeFromJson(JsonObject json, Identifier id) {
        Set<Map.Entry<String, JsonElement>> entryset = json.entrySet();
        Entry<String, JsonElement> entry;
        Object value;
        Iterator<Entry<String, JsonElement>> itr = entryset.iterator();
        while (itr.hasNext()) {
            entry = itr.next();
            String key = entry.getKey();
            if (entry.getValue().isJsonPrimitive()) {
                if (entry.getValue().getAsJsonPrimitive().isNumber()) {
                    value = (double) entry.getValue().getAsJsonPrimitive().getAsDouble();
                    createFloatWME(id, key, (double) value);
                } else if (entry.getValue().getAsJsonPrimitive().isString()) {
                    value = (String) entry.getValue().getAsJsonPrimitive().getAsString();
                    createStringWME(id, key, (String) value);
                } else if (entry.getValue().getAsJsonPrimitive().isBoolean()) {
                    value = (Boolean) entry.getValue().getAsJsonPrimitive().getAsBoolean();
                    createStringWME(id, key, value.toString());
                }
            } else if (entry.getValue().isJsonObject()) {
                Identifier newID = createIdWME(id, key);
                if (entry.getValue().getAsJsonObject().size() == 0) {
                    continue;
                }
                buildWmeInputTreeFromJson(entry.getValue().getAsJsonObject(), newID);
            }
        }
    }

    //
    public void addBranchToJson(String newBranch, JsonObject json, double value) {
        String[] newNodes = newBranch.split("\\.");
        JsonObject temp;// = new JsonObject();

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
        JsonObject temp;// = new JsonObject();

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
        JsonObject temp;// = new JsonObject();

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


    //testar
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


    public void addBranchToWme(String newBranch, double value, Identifier ID) {
        String[] newNodes = newBranch.split("\\.");

        List<Wme> WM = Wmes.matcher(getAgent()).filter(getAgent().getInputOutput().getInputLink());

        if (containsWme(WM, newNodes[0])) {
            Identifier id = WM.get(WM.indexOf(newNodes[0])).getIdentifier();
            addBranchToWme(newBranch.substring(newNodes[0].length() + 1), value, id);
        } else {
            if (newNodes.length > 1) {
                Identifier newID = createIdWME(ID, newNodes[0]);
                addBranchToWme(newBranch.substring(newNodes[0].length() + 1), value, newID);
            } else if (newNodes.length == 1) {
                createFloatWME(ID, newNodes[0], value);
            }
        }
    }

    public void addBranchToWme(String newBranch, String value, Identifier ID) {
        String[] newNodes = newBranch.split("\\.");

        List<Wme> WM = Wmes.matcher(getAgent()).filter(getAgent().getInputOutput().getInputLink());

        if (containsWme(WM, newNodes[0])) {
            Identifier id = WM.get(WM.indexOf(newNodes[0])).getIdentifier();
            addBranchToWme(newBranch.substring(newNodes[0].length() + 1), value, id);
        } else {
            if (newNodes.length > 1) {
                Identifier newID = createIdWME(ID, newNodes[0]);
                addBranchToWme(newBranch.substring(newNodes[0].length() + 1), value, newID);
            } else if (newNodes.length == 1) {
                createStringWME(ID, newNodes[0], value);
            }
        }
    }

    //new
    public void removeBranchFromWme(String pathToNode) {
        String[] newNodes = pathToNode.split("\\.");
        List<Wme> WM = Wmes.matcher(getAgent()).filter(getAgent().getInputOutput().getInputLink());
        if (containsWme(WM, newNodes[0])) {
            removeBranchFromWme(pathToNode.substring(newNodes[0].length() + 1));
            if (newNodes.length == 1) {
                removeWME(getAgent().getSymbols().createString(newNodes[0]).asIdentifier());
            }
        }
    }

    public JsonObject fromBeanToJson(Object bean) {
        JsonObject json = new JsonObject();
        Class type = bean.getClass();

        json.add(type.getName(), new JsonObject());
        try {
            Object obj = type.newInstance();
            type.cast(obj);

            for (Field field : type.getFields()) {
                json.addProperty(field.getName(), field.get(bean).toString());
            }
        } catch (Exception e) {
        }

        return json;
    }

    public boolean containsWme(final List<Wme> list, final String name) {
        boolean found = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(getAgent().getSymbols().createString(name))) {
                found = true;
                break;
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
        //printWME(id, 0);

    }

//    public void printWME(Identifier id, int level) {
//        Iterator<Wme> It = id.getWmes();
//        while (It.hasNext()) {
//            Wme wme = It.next();
//            Identifier idd = wme.getIdentifier();
//            Symbol a = wme.getAttribute();
//            Symbol v = wme.getValue();
//            Identifier testv = v.asIdentifier();
//            for (int i = 0; i < level; i++) System.out.print("   ");
//            if (testv != null) {
//                System.out.print("(" + idd.toString() + "," + a.toString() + "," + v.toString() + ")\n");
//                printWME(testv, level + 1);
//            } else System.out.print("(" + idd.toString() + "," + a.toString() + "," + v.toString() + ")\n");
//        }
//    }

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
        //out += "Input --->\n";
        out += getWMEString(il);
        setInputLinkAsString(out);
        return (out);
    }

    public String getWMEStringOutput() {
        String out = "";
        Identifier ol = getAgent().getInputOutput().getOutputLink();
        //out += "Output --->\n";
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

    public AbstractObject getWorldObject(Identifier id, String name) {
        AbstractObject newwo = null;
        Iterator<Wme> It = id.getWmes();
        if (!It.hasNext()) {
            // This situation happens when the OutputLink is empty
            newwo = new AbstractObject(name);
        }
        while (It.hasNext()) {

            if (newwo == null) {
                newwo = new AbstractObject(name);
            }

            Wme wme = It.next();
            Symbol a = wme.getAttribute();
            Symbol v = wme.getValue();
            Identifier testv = v.asIdentifier();
            if (testv != null) { // The value is an identifier
                AbstractObject child = getWorldObject(testv, a.toString());
                newwo.addCompositePart(child);
            } else { // The value is a property
                QualityDimension qd;
                Object value;
                if (v.asDouble() != null) value = v.asDouble().getValue();
                else if (v.asInteger() != null) value = v.asInteger().getValue();
                else value = v.toString();
                qd = new QualityDimension(a.toString(), value);
                Property pp = new Property(a.toString(), qd);
                //pp.setQualityDimension("VALUE", v.toString());
                newwo.addProperty(pp);
            }
        }
        return (newwo);
    }

    public void processOutputLink() {
        Identifier ol = getAgent().getInputOutput().getOutputLink();
        if (ol == null) logger.severe("Error in cst.SOARPlugin: Unable to get access to OutputLink");

        AbstractObject olao = getWorldObject(ol, "OutputLink");
        setOutputLinkAO(olao);
    }

    public void processInputLink() {
        setInputLinkIdentifier(getAgent().getInputOutput().getInputLink());
        ((IdentifierImpl) getInputLinkIdentifier()).removeAllInputWmes();
        SymbolFactoryImpl sf = (SymbolFactoryImpl) getAgent().getSymbols();
        sf.reset();
        processInputLink(getInputLinkAO(), getInputLinkIdentifier());
    }

    public void processInputLink(AbstractObject il, Identifier id) {
        if (il != null) {
            List<AbstractObject> parts = il.getCompositeParts();
            for (AbstractObject w : parts) {
                Identifier id2 = createIdWME(id, w.getName());
                processInputLink(w, id2);
            }
            List<Property> properties = il.getProperties();
            for (Property p : properties) {
                Identifier id3 = createIdWME(id, p.getName());
                for (QualityDimension qd : p.getQualityDimensions()) {
                    processQualityDimensionAtCreation(qd, id3);
                }
            }
        }

    }

    public Identifier searchInInputLink(String idName, Identifier id) {

        List<Wme> wmes = Wmes.matcher(getAgent()).filter(id);

        Identifier resultId = null;

        for (Wme wme : wmes) {
            Symbol a = wme.getAttribute();
            Symbol v = wme.getValue();

            if (a.asString().getValue().equals(idName)) {
                resultId = a.asIdentifier();
                break;
            } else {
                if (v.asIdentifier() != null) {
                    resultId = searchInInputLink(idName, v.asIdentifier());
                } else {
                    resultId = null;
                }
            }
        }

        if (resultId != null)
            return resultId;
        else
            return id;

    }

    public void processQualityDimensionAtCreation(QualityDimension qd, Identifier id) {
        try {
            if (qd.isNumber()) {
                Double value = (Double) qd.getValue();
                createFloatWME(id, qd.getName(), (double) value);
            } else if (qd.isString()) {
                String value = (String) qd.getValue();
                createStringWME(id, qd.getName(), (String) value);
            } else if (qd.isBoolean()) {
                Boolean value = (Boolean) qd.getValue();
                createStringWME(id, qd.getName(), value.toString());
            } else if (qd.isHashMap()) {

                Identifier id4 = createIdWME(id, qd.getName());

                HashMap<String, Object> value = (HashMap) qd.getValue();

                for (HashMap.Entry<String, Object> entry : value.entrySet()) {
                    if (entry.getValue() instanceof Double ||
                            entry.getValue() instanceof Integer ||
                            entry.getValue() instanceof Float ||
                            entry.getValue() instanceof Long) {
                        createFloatWME(id4, entry.getKey(), (double) entry.getValue());
                    } else if (entry.getValue() instanceof Boolean) {
                        createStringWME(id4, entry.getKey(), entry.getValue().toString());
                    } else if (entry.getValue() instanceof String) {
                        createStringWME(id4, entry.getKey(), entry.getValue().toString());
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        } else return (origin);
    }

    public void setField(Object o, String fieldName, Object value) {
        Class type = o.getClass();
        try {
            Field[] fieldList = type.getFields();
            for (Field field : fieldList) {
                //String valueClass = value.getClass().getId();
                String fieldClass = field.getType().getCanonicalName();
                if (field.getName().equals(fieldName)) {
                    //System.out.println("Class: "+o.getClass().getId()+" Field: "+field.getId()+" type: "+fieldClass+" Value: "+valueClass);
                    field.set(o, convertObject(value, fieldClass));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception: " + e.getStackTrace().toString());
        }
    }

    public Object getJavaObject(Identifier id, Object parent, String package_with_beans_classes) {
        //String commandType = command.getId();

        Object javaObject = null;
        Class type = null;


        Iterator<Wme> It = id.getWmes();
        while (It.hasNext()) {
            Wme wme = It.next();
            Identifier idd = wme.getIdentifier();
            Symbol a = wme.getAttribute();
            Symbol v = wme.getValue();
            Identifier testv = v.asIdentifier();
            if (testv != null) { // The value is an identifier: recursion
                //System.out.println("Class name: "+a.toString()+" "+package_with_beans_classes+"."+a.toString());
                Object child = createJavaObject(package_with_beans_classes + "." + a.toString());
                if (parent != null) setField(parent, a.toString(), child);
                javaObject = getJavaObject(testv, child, package_with_beans_classes);
            } else { // The value is a property
                Object value;
                if (v.asDouble() != null) value = v.asDouble().getValue();
                else if (v.asInteger() != null) value = v.asInteger().getValue();
                else value = v.toString();
                setField(parent, a.toString(), value);
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

    public AbstractObject getInputLinkAO() {
        return inputLinkAO;
    }

    public void setInputLinkAO(AbstractObject inputLinkAO) {
        this.inputLinkAO = inputLinkAO;
    }

    public AbstractObject getOutputLinkAO() {
        return outputLinkAO;
    }

    public void setOutputLinkAO(AbstractObject outputLinkAO) {
        this.outputLinkAO = outputLinkAO;
    }
}
