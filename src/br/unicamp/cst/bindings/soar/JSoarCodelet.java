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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.representation.owrl.AbstractObject;
import br.unicamp.cst.representation.owrl.Property;
import br.unicamp.cst.representation.owrl.QualityDimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoar.kernel.symbols.Identifier;
import org.slf4j.LoggerFactory;

/**
 *
 * @author wander
 */
public abstract class JSoarCodelet extends Codelet {
    
    private String agentName;
    private File productionPath;

    private SOARPlugin jsoar;

    private static final String ARRAY = "ARRAY";

    public static final String OUTPUT_COMMAND_MO = "OUTPUT_COMMAND_MO";
    
    public void SilenceLoggers() {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.jsoar")).setLevel(ch.qos.logback.classic.Level.OFF);
        Logger.getLogger("Simulation").setLevel(Level.SEVERE);
    }
    
    public void initSoarPlugin(String _agentName, File _productionPath, Boolean startSOARDebugger){
        this.setJsoar(new SOARPlugin(_agentName, _productionPath, startSOARDebugger));
    }


    public synchronized String getOutputLinkAsString(){
        return getJsoar().getOutputLinkAsString();
    }

    public synchronized String getInputLinkAsString(){
        return getJsoar().getInputLinkAsString();
    }

    public synchronized int getPhase(){
        return getJsoar().getPhase();
    }

    public synchronized void setDebugState(int state){
        getJsoar().setDebugState(state);
    }

    public synchronized int getDebugState(){
        return getJsoar().getDebugState();
    }

    public synchronized ArrayList<Object> getOutputInObject(String package_with_beans_classes){

        ArrayList<Object> commandList = null;
        AbstractObject ol = getJsoar().getOutputLinkAO();

        if(ol != null) {
            commandList = new ArrayList<Object>();
            for (AbstractObject command : ol.getCompositeParts()) {
                commandList.add(buildObject(command, package_with_beans_classes));
            }
        }
        else {
            System.out.println("Error in cst.JSoarCodelet: getOutputInObject was not able to get a reference to Soar OutputLink");
        }
        return commandList;
    }


    public synchronized Object buildObject(AbstractObject command, String package_with_beans_classes){

        ArrayList<Object> arrayList = new ArrayList<>();
        String commandType = command.getName();
        Object commandObject = null;
        Class type = null;
        if(!commandType.toUpperCase().contains(ARRAY)) {
            try {
                type = Class.forName(package_with_beans_classes + "." + commandType);
                commandObject = type.newInstance();
                type.cast(commandObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
            for (Property p : command.getProperties()) {
                try {
                    for (Field field : type.getDeclaredFields()) {
                        if (p.getName().equals(field.getName())) {
                            QualityDimension value = p.getQualityDimensions().get(0);
                            if (value.isDouble()) {
                                float fvalue = ((Double) value.getValue()).floatValue();
                                field.set(commandObject, fvalue);
                            } else if (value.isLong()) {
                                float fvalue = ((Long) value.getValue()).floatValue();
                                field.set(commandObject, fvalue);
                            } else {
                                field.set(commandObject, value.getValue());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        for (AbstractObject comp: command.getCompositeList()) {
            Object object = buildObject(comp, package_with_beans_classes);

            if(commandType.toUpperCase().contains(ARRAY)){
                arrayList.add(object);
            }else{
                for (Field field: type.getDeclaredFields()) {
                    if(comp.getName().toUpperCase().contains(field.getName().toUpperCase())){
                        try {
                            field.set(commandObject, object);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return arrayList.size() > 0 ? arrayList : commandObject;
    }
    
    public ArrayList<Object> getCommandsJSON(String package_with_beans_classes){
        ArrayList<Object> commandList = new ArrayList<Object>();
        JsonObject templist = getJsoar().getOutputLinkJSON();
        Set<Map.Entry<String,JsonElement>> set = templist.entrySet();
        Iterator <Entry<String,JsonElement>> it = set.iterator();
        while(it.hasNext()){
            Entry<String,JsonElement> entry = it.next();
            String key = entry.getKey();
            JsonObject commandtype = entry.getValue().getAsJsonObject();
            try{
                Class type = Class.forName(package_with_beans_classes+"."+key);
                Object command = type.newInstance();
                type.cast(command);
                for(Field field : type.getFields()){
                    if(commandtype.has(field.getName())){
                        if(commandtype.get(field.getName()).getAsJsonPrimitive().isNumber()){
                            field.set(command, commandtype.get(field.getName()).getAsFloat());
                            
                        }else if(commandtype.get(field.getName()).getAsJsonPrimitive().isBoolean()){
                            field.set(command, commandtype.get(field.getName()).getAsBoolean());
                            
                        }else{
                            field.set(command, commandtype.get(field.getName()).getAsString());
                        }
                    }
                }
                commandList.add(command);
                
            }catch(Exception e){
                 e.printStackTrace();
            }
        }
        return commandList;
    }

    public List<Identifier> getOperatorsPathList(){
        return getJsoar().getOperatorsPathList();
    }
    
    public JsonObject createJson(String pathToLeaf, Object value){
        JsonObject json = new JsonObject();
        Class a = value.getClass();
        if(a==String.class){
            String specvalue =(String)value;
            json = getJsoar().createJsonFromString(pathToLeaf,specvalue);
        }
        else if(a==double.class){
            double specvalue =(double)value;
            json = getJsoar().createJsonFromString(pathToLeaf,specvalue);
        }
        return json;
    }
    
    public void addToJson(JsonObject newBranch, JsonObject json, String property){
        json.add(property, newBranch);
    }

    public void addToJson(String newBranch, JsonObject json, Object value){
        if(value==null){
            JsonObject specvalue =(JsonObject)value;
            getJsoar().addBranchToJson(newBranch, json, specvalue);
            return;
        }
        Class a = value.getClass();
        if(a==String.class){
            String specvalue =(String)value;
            getJsoar().addBranchToJson(newBranch, json, specvalue);
        }
        else if(a==double.class || a==float.class){
            double specvalue =(double)value;
            getJsoar().addBranchToJson(newBranch, json, specvalue);
        }
        else if(a==Integer.class){
            Integer spec = (Integer) value;
            double specvalue = spec.doubleValue();
            //double specvalue =(double)value;
            getJsoar().addBranchToJson(newBranch, json, specvalue);
        }
        else if(a==Long.class){
            Long spec = (Long) value;
            double specvalue = spec.doubleValue();
            //double specvalue =(double)value;
            getJsoar().addBranchToJson(newBranch, json, specvalue);
        }
        else if(a==Double.class){
            Double spec = (Double) value;
            double specvalue = spec;//.doubleValue();
            //double specvalue =(double)value;
            getJsoar().addBranchToJson(newBranch, json, specvalue);
        }
        
        else{
            JsonObject specvalue = (JsonObject)value;
            getJsoar().addBranchToJson(newBranch, json, specvalue);
        }
    }
    
    public void addToWme(String newBranch, Object value){
        Class a = value.getClass();
        if(a==String.class){
            String specvalue =(String)value;
            getJsoar().addBranchToWme(newBranch,specvalue, getJsoar().getInputLinkIdentifier());
        }
        else if(a==double.class){
            double specvalue =(double)value;
            getJsoar().addBranchToWme(newBranch,specvalue, getJsoar().getInputLinkIdentifier());
        }
        
    }
    
    public void setInputLinkJson(JsonObject json){
        getJsoar().buildWmeInputTreeFromJson(json, getJsoar().getInputLinkIdentifier());
    }
    
    public void setInputLinkAO(AbstractObject wo){
        getJsoar().setInputLinkAO(wo);
    }

    public void processInputLink(){
        getJsoar().processInputLink();
    }
    
    public void removeWme(String pathToNode){
        getJsoar().removeBranchFromWme(pathToNode);
    }
    
    public void removeJson(String pathToOldBranch, JsonObject json){
        getJsoar().removeBranchFromJson(pathToOldBranch, json);
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

    public SOARPlugin getJsoar() {
        return jsoar;
    }

    public void setJsoar(SOARPlugin jsoar) {
        this.jsoar = jsoar;
    }
}
