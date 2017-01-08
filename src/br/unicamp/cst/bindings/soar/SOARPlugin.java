/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.bindings.soar;

import br.unicamp.cst.representation.owrl.Property;
import br.unicamp.cst.representation.owrl.QualityDimension;
import br.unicamp.cst.representation.owrl.WorldObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import org.jsoar.kernel.Agent;
import org.jsoar.kernel.memory.Wme;
import org.jsoar.kernel.memory.Wmes;
import org.jsoar.kernel.symbols.DoubleSymbol;
import org.jsoar.kernel.symbols.Identifier;
import org.jsoar.kernel.symbols.StringSymbol;
import org.jsoar.kernel.symbols.Symbol;
import org.jsoar.kernel.symbols.SymbolFactory;
import org.jsoar.runtime.ThreadedAgent;
import org.jsoar.util.commands.SoarCommands;

/**
 *
 * @author wander
 */

public class SOARPlugin {
    Logger logger = Logger.getLogger(SOARPlugin.class.getName());
    ThreadedAgent threaded = null;
    Agent agent = null;
    Identifier inputLink = null;
    
    // Ordinary Variables
    String agentName;
    File productionPath;
    
    public String InputLinkAsString="";
    public String OutputLinkAsString="";
    public String JSONOutputLinkAsString="";
    
    //contrutor default soh pra realizar alguns testes
    public SOARPlugin(){};
    
    public SOARPlugin(String _agentName, File _productionPath, Boolean startSOARDebugger){
        try
        {
            // Inicial variables
            agentName = _agentName;
            productionPath = _productionPath;

            // create Soar kernel And Agent
            threaded = ThreadedAgent.create();
            agent = threaded.getAgent();
            agent.setName(agentName);
            
            // Load some productions
            String path = productionPath.getAbsolutePath();
            SoarCommands.source(agent.getInterpreter(), path);
            inputLink = agent.getInputOutput().getInputLink();//.GetInputLink();

            // Start the Debugger if it is the case
            if (startSOARDebugger)
            {
                agent.openDebugger();
            }
            
        }
        catch (Exception e)
        {
            logger.severe("Error while creating SOAR Kernel"+e);
        }
        
    }
    
    public void finalizeKernel() {
        try
        {
            agent.dispose();
        }
        catch (Exception e)
        {
            logger.severe("Error while shuting down SOAR"+e);
        }
    }
    
    public void resetSOAR() {
        agent.initialize();
        inputLink = agent.getInputOutput().getInputLink();
    }
    
    public void runSOAR() {
        InputLinkAsString = getWMEStringInput();
        try
        {
            if (agent != null)
            {
                agent.runForever(); //.RunSelfTilOutput();
            }
        }
        catch (Exception e)
        {
            logger.severe("Error while running SOAR step"+e);
        }
        OutputLinkAsString = getWMEStringOutput();
    }
    
    private String GetParameterValue(String par) {
        List<Wme> Commands = Wmes.matcher(agent).filter(agent.getInputOutput().getOutputLink());
        List<Wme> Parameters = Wmes.matcher(agent).filter(Commands.get(0));
        String parvalue = "";
        for (Wme w : Parameters) 
           if (w.getAttribute().toString().equals(par)) parvalue = w.getValue().toString();
        //System.out.println("Getting parameter: "+parvalue);
        return(parvalue);
    }
    
//    public void printInputWMEs(){
//        Iterator<Wme> It = agent.getInputOutput().getInputLink().getWmes();
//        while(It.hasNext()){
//            Wme next = It.next();
//            System.out.println(next.getAttribute().toString());
//            System.out.println(next.getValue().toString());
//            
//            Iterator<Wme> children = next.getChildren();
//            while(children.hasNext()){
//                Wme child = children.next();
//                System.out.println(child.getAttribute().toString());
//                System.out.println(child.getValue().toString());
//            }
//        }
//    }
    
    public void printWMEs(List<Wme> Commands){
        for (Wme wme: Commands) {
            System.out.print("("+wme.getIdentifier().toString()+","+wme.getAttribute().toString()+","+wme.getValue().toString()+")\n");            
            Iterator<Wme> children = wme.getChildren();
            while(children.hasNext()){
                Wme child = children.next();
                System.out.print("("+child.getIdentifier().toString()+","+child.getAttribute().toString()+","+child.getValue().toString()+")\n");            
            }
        }
    }
    
    public String getWMEsAsString(List<Wme> Commands){
        String result="";
        for (Wme wme: Commands) {
            result += "("+wme.getIdentifier().toString()+","+wme.getAttribute().toString()+","+wme.getValue().toString()+")\n";            
            Iterator<Wme> children = wme.getChildren();
            while(children.hasNext()){
                Wme child = children.next();
                result += "("+child.getIdentifier().toString()+","+child.getAttribute().toString()+","+child.getValue().toString()+")\n";            
            }
        }
        return(result);
    }
    
    public List<Wme> getOutputLink_WME() {
        List<Wme> olwme = Wmes.matcher(agent).filter(agent.getInputOutput().getOutputLink());
        return(olwme);
    }
    
    public JsonObject getOutputLink() {
       JsonObject json = new JsonObject();
        try
        {
            if (agent != null)
            {
                List<Wme> Commands = getOutputLink_WME();
                //OutputLinkAsString = getWMEsAsString(Commands);
                OutputLinkAsString = getWMEStringOutput();
                //System.out.println("Selected Commands: "+Commands.size());
                //printWMEs(Commands);
                for(Wme command : Commands){
                    String commandType = command.getAttribute().toString();
                    json.add(commandType,new JsonObject());
                    String parameter = command.getAttribute().toString();
                    String parvalue = command.getValue().toString();
                    //json.get(commandType).getAsJsonObject().addProperty(parameter, parvalue);
                    Iterator<Wme> children = command.getChildren();
                    while(children.hasNext()){
                        Wme child = children.next();
                        parameter = child.getAttribute().toString();
                        parvalue = child.getValue().toString();
                        Float floatvalue = tryParseFloat(parvalue);
                        if(floatvalue != null){
                           json.get(commandType).getAsJsonObject().addProperty(parameter, floatvalue);
                        }
                        else{
                           json.get(commandType).getAsJsonObject().addProperty(parameter, parvalue);
                        }
                    }    
                }
            }
        }
        catch (Exception e)
        {
            logger.severe("Error while creating SOAR Kernel"+e);
        }
        //System.out.println("Selected Command (json): "+json.toString());
        JSONOutputLinkAsString = json.toString();
        return(json);
    }
    
    
    /**
     * Try Parse a Float Element
     * @param value Float Value
     * @return The Float Value or null otherwise
     */
    private Float tryParseFloat (String value){
        Float returnValue = null;

        try
        {
            returnValue = Float.parseFloat(value);
        }
        catch (Exception ex)
        {
            returnValue = null;
        }

        return returnValue;
    }
     
    
    
    //CreateWME methods
    public Identifier CreateIdWME(Identifier id, String s) {
        SymbolFactory sf = agent.getSymbols();
        Identifier newID = sf.createIdentifier('I');
        agent.getInputOutput().addInputWme(id, sf.createString(s), newID);
        return(newID);
    }
    
    public Identifier CreateIdWME(String s) {
        return(CreateIdWME(inputLink,s));
    }
    
    public void CreateFloatWME(Identifier id, String s, double value) {
        SymbolFactory sf = agent.getSymbols();
        DoubleSymbol newID = sf.createDouble(value);
        agent.getInputOutput().addInputWme(id, sf.createString(s), newID);
    }
    
    public void CreateFloatWME(String s, double value) {
        CreateFloatWME(inputLink,s,value);
    }
    
    public void CreateStringWME(Identifier id, String s, String value) {
        SymbolFactory sf = agent.getSymbols();
        StringSymbol newID = sf.createString(value);
        agent.getInputOutput().addInputWme(id, sf.createString(s), newID);
    }
    
    public void CreateStringWME(String s, String value) {
        CreateStringWME(inputLink,s,value);
    }
    
        //organizar
    private void RemoveWME(Identifier Attribute){
        //agent.getInputOutput().getInputLink().getWmes();
        while(agent.getInputOutput().getInputLink().getWmes().hasNext()){
            Wme candidate = agent.getInputOutput().getInputLink().getWmes().next();
            
            if(candidate.getAttribute()==Attribute){
               agent.getInputOutput().getInputLink().getWmes().remove();
            }
        }
    }
    
  
    public JsonObject createJsonFromString(String pathToLeaf, double value){
        String[] treeNodes = pathToLeaf.split("\\.");
        JsonObject json = new JsonObject();
        
        for(int i = treeNodes.length -1; i>=0; i--){
            JsonObject temp = new JsonObject();
            
            if(i == treeNodes.length-1){
                temp.addProperty(treeNodes[i], value);
            }else {
              temp.add(treeNodes[i], json);
            }
            json = temp;
        }
        return json;
    }
    
     public JsonObject createJsonFromString(String pathToLeaf, String value){
        String[] treeNodes = pathToLeaf.split("\\.");
        JsonObject json = new JsonObject();
        
        for(int i = treeNodes.length -1; i>=0; i--){
            JsonObject temp = new JsonObject();
            
            if(i == treeNodes.length-1){
                temp.addProperty(treeNodes[i], value);
            }else {
              temp.add(treeNodes[i], json);
            }
            json = temp;
        }
        return json;
    }
     
     
    public JsonObject createJsonFromString(String pathToLeaf, JsonObject value){
        String[] treeNodes = pathToLeaf.split("\\.");
        JsonObject json = new JsonObject();
        
        for(int i = treeNodes.length -1; i>=0; i--){
            JsonObject temp = new JsonObject();
            
            if(i == treeNodes.length-1){
                temp.add(treeNodes[i], value);
            }else {
              temp.add(treeNodes[i], json);
            }
            json = temp;
        }
        return json;
    } 
    
    public void BuildWmeInputTreeFromJson(JsonObject json, Identifier id){
        //Set<Map.Entry<String,JsonElement>> entryset = CatchMapFromJson(json);
        Set<Map.Entry<String,JsonElement>> entryset = json.entrySet();
        
        Entry<String, JsonElement> entry;
        Object value;
        
        Iterator <Entry<String,JsonElement>> itr = entryset.iterator();
        //if(json.size()==0){
        //}
        
        while(itr.hasNext()){
            //Iterator itr = entryset.iterator();
            //Object obj = itr..next();
            entry = itr.next();
            String key = entry.getKey();
            
            if(entry.getValue().isJsonPrimitive()){
                if(entry.getValue().getAsJsonPrimitive().isNumber()){
                    value = (double)entry.getValue().getAsJsonPrimitive().getAsDouble();
                    CreateFloatWME(id, key, (double)value);
                }
                else if(entry.getValue().getAsJsonPrimitive().isString()){
                    value = (String)entry.getValue().getAsJsonPrimitive().getAsString();
                    CreateStringWME(id, key, (String)value);
                }
                else if(entry.getValue().getAsJsonPrimitive().isBoolean()){
                    value = (Boolean)entry.getValue().getAsJsonPrimitive().getAsBoolean();
                    CreateStringWME(id, key, value.toString());
                }
            } 
            else if(entry.getValue().isJsonObject()){
                Identifier newID = CreateIdWME(id, key);
                if(entry.getValue().getAsJsonObject().size()==0){
                    continue;
                }
                BuildWmeInputTreeFromJson(entry.getValue().getAsJsonObject(), newID);
            }
        }
    }
    
     
    //
    public void addBranchToJson(String newBranch, JsonObject json, double value){
        String[] newNodes = newBranch.split("\\.");
        int size = newNodes.length;
        JsonObject temp;// = new JsonObject();
        
        if(newNodes.length>1){
            if(json.has(newNodes[0])){
                addBranchToJson(newBranch.substring(newNodes[0].length()+1),json.getAsJsonObject(newNodes[0]),value);
            }
            else{
                temp = createJsonFromString(newBranch.substring(newNodes[0].length()+1), value);
                json.add(newNodes[0], temp);
            }   
        }
        else{
            json.addProperty(newNodes[0], value);
        } 
    }
    
    public void addBranchToJson(String newBranch, JsonObject json, String value){
        String[] newNodes = newBranch.split("\\.");
        int size = newNodes.length;
        JsonObject temp;// = new JsonObject();
        
        if(newNodes.length>1){
            if(json.has(newNodes[0])){
                addBranchToJson(newBranch.substring(newNodes[0].length()+1),json.getAsJsonObject(newNodes[0]),value);
            }
            else{
                temp = createJsonFromString(newBranch.substring(newNodes[0].length()+1), value);
                json.add(newNodes[0], temp);
            }   
        }
        else{
            json.addProperty(newNodes[0], value);
        } 
    }
    
     public void addBranchToJson(String newBranch, JsonObject json, JsonObject value){
        String[] newNodes = newBranch.split("\\.");
        int size = newNodes.length;
        JsonObject temp;// = new JsonObject();
        
        if(newNodes.length>1){
            if(json.has(newNodes[0])){
                addBranchToJson(newBranch.substring(newNodes[0].length()+1),json.getAsJsonObject(newNodes[0]),value);
            }
            else{
                temp = createJsonFromString(newBranch.substring(newNodes[0].length()+1), value);
                json.add(newNodes[0], temp);
            }   
        }
        else{
            //json.addProperty(newNodes[0], value);
            json.add(newNodes[0], value);
        } 
    }
    
    
    //testar
    public void removeBranchFromJson(String pathToOldBranch, JsonObject json){
        String[] oldNodes = pathToOldBranch.split("\\.");
        int size = oldNodes.length;
        JsonObject temp;// = new JsonObject();
        
        if(oldNodes.length>1){
            if(json.has(oldNodes[0])){
                //addBranchToJson(newBranch.substring(newNodes[0].length()+1),json.getAsJsonObject(newNodes[0]),value);
                removeBranchFromJson(pathToOldBranch.substring(oldNodes[0].length()+1),json.getAsJsonObject(oldNodes[0]));
            }
            //else{
                //temp = ParseWmeFromString(newBranch.substring(newNodes[0].length()+1), value);
              //  json.remove(oldNodes[0]);
                //json.add(newNodes[0], temp);
            //}   
        }
        else{
            //json.addProperty(newNodes[0], value);
            json.remove(oldNodes[0]);
        } 
    }
    

    
    
    public void addBranchToWme(String newBranch, double value, Identifier ID){
        String[] newNodes = newBranch.split("\\.");
        JsonObject json = new JsonObject();
        
        int size = newNodes.length;
        List<Wme> WM = Wmes.matcher(agent).filter(agent.getInputOutput().getInputLink());
        
        if(containsWme(WM,newNodes[0])){
            Identifier id = WM.get(WM.indexOf(newNodes[0])).getIdentifier();
            //String attr = WM.get(WM.indexOf(newNodes[0])).getAttribute().toString();

            addBranchToWme(newBranch.substring(newNodes[0].length()+1), value,id);
        }
        else{
            if(newNodes.length >1){
                Identifier newID = CreateIdWME(ID,newNodes[0]);
                addBranchToWme(newBranch.substring(newNodes[0].length()+1),value,newID);
            }
            else if(newNodes.length == 1){
                CreateFloatWME(ID,newNodes[0],value);
            }
        } 
    }
    
    public void addBranchToWme(String newBranch, String value, Identifier ID){
        String[] newNodes = newBranch.split("\\.");
        JsonObject json = new JsonObject();
        
        int size = newNodes.length;
        List<Wme> WM = Wmes.matcher(agent).filter(agent.getInputOutput().getInputLink());
        
        if(containsWme(WM,newNodes[0])){
            Identifier id = WM.get(WM.indexOf(newNodes[0])).getIdentifier();
            //String attr = WM.get(WM.indexOf(newNodes[0])).getAttribute().toString();

            addBranchToWme(newBranch.substring(newNodes[0].length()+1), value,id);
        }
        else{
            if(newNodes.length >1){
                Identifier newID = CreateIdWME(ID,newNodes[0]);
                addBranchToWme(newBranch.substring(newNodes[0].length()+1),value,newID);
            }
            else if(newNodes.length == 1){
                CreateStringWME(ID,newNodes[0],value);
            }
        } 
    }
    
    //new
    public void removeBranchFromWme(String pathToNode){
        String[] newNodes = pathToNode.split("\\.");
        JsonObject json = new JsonObject();
        
        int size = newNodes.length;
        List<Wme> WM = Wmes.matcher(agent).filter(agent.getInputOutput().getInputLink());
        
        if(containsWme(WM,newNodes[0])){
            Identifier id = WM.get(WM.indexOf(newNodes[0])).getIdentifier();
            //String attr = WM.get(WM.indexOf(newNodes[0])).getAttribute().toString();

            removeBranchFromWme(pathToNode.substring(newNodes[0].length()+1));
        //}
        //else{
            //if(newNodes.length >1){
            //    Identifier newID = CreateIdWME(ID,newNodes[0],agent);
            //    addBranchToWme(newBranch.substring(newNodes[0].length()+1),value,agent,newID);
            //}
            if(newNodes.length == 1){
                //agent.getInputOutput().getInputLink().getWmes()
                //CreateStringWME(ID,newNodes[0],value,agent);
                RemoveWME(agent.getSymbols().createString(newNodes[0]).asIdentifier());
            }
        } 
    }
    
    public JsonObject fromBeanToJson(Object bean){
        JsonObject json = new JsonObject();
        Class type = bean.getClass();
        
        json.add(type.getName(), new JsonObject());
        try{
            Object obj = type.newInstance();
            type.cast(obj);
            
            for(Field field : type.getFields()){
                json.addProperty(field.getName(), field.get(bean).toString());
            }
        }catch(Exception e){}
        
        return json;
    }

    
    
    public void jsonToString(JsonObject json){
        
    }
    
    
    
    public boolean containsWme(final List<Wme> list, final String name){
    return list.stream().filter(o -> o.getAttribute().equals(agent.getSymbols().createString(name))).findFirst().isPresent();
}
    
    
    public String toPrettyFormat(JsonObject json) {
      //JsonParser parser = new JsonParser();
      //JsonObject json = parser.parse(jsonString).getAsJsonObject();

      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      String prettyJson = gson.toJson(json);

      return prettyJson;
  }
    
    public void printWME(Identifier id) {
        printWME(id,0);
        
    }
    
    public void printWME(Identifier id, int level) {
        Iterator<Wme> It = id.getWmes();
        while (It.hasNext()) {
            Wme wme = It.next();
            Identifier idd = wme.getIdentifier();
            Symbol a = wme.getAttribute();
            Symbol v = wme.getValue();
            Identifier testv = v.asIdentifier();
            for (int i=0;i<level;i++) System.out.print("   ");
            if (testv != null) {
                System.out.print("("+idd.toString()+","+a.toString()+","+v.toString()+")\n");
                printWME(testv,level+1);
            }
            else System.out.print("("+idd.toString()+","+a.toString()+","+v.toString()+")\n");
        }
    }
    
    public void printInputWMEs(){
        Identifier il = agent.getInputOutput().getInputLink();
        System.out.println("Input --->");
        printWME(il);
    }
    
    public void printOutputWMEs(){
        Identifier ol = agent.getInputOutput().getOutputLink();
        System.out.println("Output --->");
        printWME(ol);
    }
    
    public String getWMEString(Identifier id) {
        return(getWMEString(id,0));
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
            for (int i=0;i<level;i++) out += "   ";
            if (testv != null) {
                out += String.format("(%s,%s,%s)\n",idd.toString(),a.toString(),v.toString());
                out += getWMEString(testv,level+1);
            }
            else out += String.format("(%s,%s,%s)\n",idd.toString(),a.toString(),v.toString());
        }
        return(out);
    }
    
    public String getWMEStringInput(){
        String out = "";
        Identifier il = agent.getInputOutput().getInputLink();
        out += "Input --->\n";
        out += getWMEString(il);
        return(out);
    }
    
    public String getWMEStringOutput(){
        String out = "";
        Identifier ol = agent.getInputOutput().getOutputLink();
        out += "Output --->\n";
        out += getWMEString(ol);
        return(out);
    }
    
    public WorldObject getWorldObject(Identifier id, String name) {
        WorldObject newwo = new WorldObject(name);
        Iterator<Wme> It = id.getWmes();
        while (It.hasNext()) {
            Wme wme = It.next();
            Identifier idd = wme.getIdentifier();
            Symbol a = wme.getAttribute();
            Symbol v = wme.getValue();
            Identifier testv = v.asIdentifier();
            if (testv != null) { // The value is an identifier
                WorldObject child = getWorldObject(testv,a.toString());
                newwo.addPart(child);
            }
            else { // The value is a property
                QualityDimension qd;
                Object value;
                if (v.asDouble() != null) value = v.asDouble().getValue();
                else if(v.asInteger() != null) value = v.asInteger().getValue();
                else value = v.toString();
                qd = new QualityDimension(a.toString(),value);
                Property pp = new Property(a.toString(),qd);
                //pp.setQualityDimension("VALUE", v.toString());
                newwo.addProperty(pp);
            }
        }
        return(newwo);
    }

    public WorldObject getOutputLinkOWRL() {
        Identifier ol = agent.getInputOutput().getOutputLink();
        return(getWorldObject(ol,"OutputLink"));
    }

    public void setInputLink(WorldObject ilwo) {
        Identifier il = agent.getInputOutput().getInputLink();
        setInputLink(ilwo,il);
    }
    
    public void setInputLink(WorldObject il, Identifier id){
        List<WorldObject> parts = il.getParts();
        List<Property> properties = il.getProperties();
        for (WorldObject w : parts) {
            Identifier id2 = CreateIdWME(id,w.getName());
            setInputLink(w,id2);
            
        }
        for (Property p : properties) {
            Identifier id3 = CreateIdWME(id,p.getName());
            for (QualityDimension qd : p.getQualityDimensions()) {
                  if(qd.isNumber()){
                    Double value = (Double) qd.getValue();
                    CreateFloatWME(id3, qd.getName(), (double)value);
                  }
                  else if(qd.isString()){
                    String value = (String) qd.getValue();
                    CreateStringWME(id3, qd.getName(), (String)value);
                  }
                  else if(qd.isBoolean()){
                    Boolean value = (Boolean) qd.getValue();
                    CreateStringWME(id3, qd.getName(), value.toString());
                  } 
               }
        }
        
    }
    
}
    /**
     * Doc doc doc 
     * @t
     */
