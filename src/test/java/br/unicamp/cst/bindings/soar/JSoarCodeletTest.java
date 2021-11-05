package br.unicamp.cst.bindings.soar;

import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.representation.wme.Idea;
import com.google.gson.*;
import org.junit.Test;
import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * @author wander
 *
 */
public class JSoarCodeletTest {

    JSoarCodelet jSoarCodelet = new JSoarCodelet() {
        @Override
        public void accessMemoryObjects() {

        }

        @Override
        public void calculateActivation() {

        }

        @Override
        public void proc() {
            getJsoar().step();
        }
    };

    @Test
    public void basicTest(){
        Mind mind = new Mind();

        String soarRulesPath="src/test/resources/mac.soar";
        jSoarCodelet.initSoarPlugin("testAgent", new File(soarRulesPath), false);
        mind.insertCodelet(jSoarCodelet);

        mind.start();

        try{
            Thread.sleep(5000L);
        }catch(Exception e){
            e.printStackTrace();
        }

        String inputLink = jSoarCodelet.getInputLinkAsString();
        String outputLink = jSoarCodelet.getOutputLinkAsString();

        System.out.println(inputLink);

        assertNotNull(outputLink);
        assertNotEquals("", outputLink);
        mind.shutDown();

    }

    @Test
    public void inputOutputLinkTest(){
        Mind mind = new Mind();

        Idea il = Idea.createIdea("InputLink", "", 0);
        Idea cp = Idea.createIdea("CURRENT_PERCEPTION", "", 1);
        Idea conf = Idea.createIdea("CONFIGURATION", "", 2);
        Idea smart = Idea.createIdea("SMARTCAR_INFO", "", 3);
        Idea tf = Idea.createIdea("TRAFFIC_LIGHT", "", 4);
        Idea current_phase = Idea.createIdea("CURRENT_PHASE","", 5);
        Idea phase = Idea.createIdea("PHASE", "RED", 6);
        Idea numb = Idea.createIdea("NUMBER", "4", 7);


        current_phase.add(numb);
        current_phase.add(phase);
        tf.add(current_phase);
        conf.add(tf);
        conf.add(smart);
        cp.add(conf);
        il.add(cp);

        String expectedInput = "(I2,CURRENT_PERCEPTION,W1)\n" +
                "   (W1,CONFIGURATION,W2)\n" +
                "      (W2,TRAFFIC_LIGHT,W4)\n" +
                "         (W4,CURRENT_PHASE,W5)\n" +
                "            (W5,NUMBER,4.0)\n" +
                "            (W5,PHASE,RED)\n" +
                "      (W2,SMARTCAR_INFO,W3)\n";

        String expectedOutput = "(I3,SoarCommandChange,C1)\n" +
                "   (C1,productionName,change)\n" +
                "   (C1,quantity,2)\n" +
                "   (C1,apply,true)\n";

        String soarRulesPath="src/test/resources/smartCar.soar";
        jSoarCodelet.initSoarPlugin("testAgent", new File(soarRulesPath), false);
        jSoarCodelet.setInputLinkIdea(il);

        mind.insertCodelet(jSoarCodelet);

        mind.start();
        try{
            Thread.sleep(3000L);
        }catch(Exception e){
            e.printStackTrace();
        }

        String inputLink = jSoarCodelet.getInputLinkAsString();
        String outputLink = jSoarCodelet.getOutputLinkAsString();

        assertEquals(expectedInput, inputLink);
        assertEquals(expectedOutput, outputLink);
        System.out.println(inputLink);
        mind.shutDown();
    }


    @Test
    public void getSetDebugTest(){
        Mind mind = new Mind();

        Idea il = Idea.createIdea("InputLink", "", 0);
        Idea cp = Idea.createIdea("CURRENT_PERCEPTION", "", 1);
        Idea conf = Idea.createIdea("CONFIGURATION", "", 2);
        Idea smart = Idea.createIdea("SMARTCAR_INFO", "", 3);
        Idea tf = Idea.createIdea("TRAFFIC_LIGHT", "", 4);
        Idea current_phase = Idea.createIdea("CURRENT_PHASE","", 5);
        Idea phase = Idea.createIdea("PHASE", "RED", 6);
        Idea numb = Idea.createIdea("NUMBER", "4", 7);


        current_phase.add(numb);
        current_phase.add(phase);
        tf.add(current_phase);
        conf.add(tf);
        conf.add(smart);
        cp.add(conf);
        il.add(cp);


        String soarRulesPath="src/test/resources/smartCar.soar";
        jSoarCodelet.initSoarPlugin("testAgent", new File(soarRulesPath), false);
        jSoarCodelet.setInputLinkIdea(il);

        mind.insertCodelet(jSoarCodelet);

        mind.start();
        try{
            Thread.sleep(3000L);
        }catch(Exception e){
            e.printStackTrace();
        }

        jSoarCodelet.setDebugState(0);
        assertEquals(0, jSoarCodelet.getDebugState());

        jSoarCodelet.setDebugState(1);
        assertEquals(1, jSoarCodelet.getDebugState());

        mind.shutDown();
    }

    @Test
    public void buildJavaObjectTest(){
        Mind mind = new Mind();

        Idea il = Idea.createIdea("InputLink", "", 0);
        Idea cp = Idea.createIdea("CURRENT_PERCEPTION", "", 1);
        Idea conf = Idea.createIdea("CONFIGURATION", "", 2);
        Idea smart = Idea.createIdea("SMARTCAR_INFO", "", 3);
        Idea tf = Idea.createIdea("TRAFFIC_LIGHT", "", 4);
        Idea current_phase = Idea.createIdea("CURRENT_PHASE","", 5);
        Idea phase = Idea.createIdea("PHASE", "RED", 6);
        Idea numb = Idea.createIdea("NUMBER", "4", 7);


        current_phase.add(numb);
        current_phase.add(phase);
        tf.add(current_phase);
        conf.add(tf);
        conf.add(smart);
        cp.add(conf);
        il.add(cp);

        String soarRulesPath="src/test/resources/smartCar.soar";
        jSoarCodelet.initSoarPlugin("testAgent", new File(soarRulesPath), false);
        jSoarCodelet.setInputLinkIdea(il);

        mind.insertCodelet(jSoarCodelet);

        mind.start();
        try{
            Thread.sleep(3000L);
        }catch(Exception e){
            e.printStackTrace();
        }

        ArrayList<Object> outputList = jSoarCodelet.getOutputInObject("br.unicamp.cst.bindings.soar");

        mind.shutDown();
        assertTrue(outputList.get(0) instanceof SoarCommandChange);
        assertEquals("change", ((SoarCommandChange)outputList.get(0)).getProductionName());
        assertEquals(2, ((SoarCommandChange)outputList.get(0)).getQuantity(), 0);
    }

    @Test
    public void buildJavaObjectNestedTest(){
        Mind mind = new Mind();

        Idea il = Idea.createIdea("InputLink", "", 0);
        Idea cp = Idea.createIdea("CURRENT_PERCEPTION", "", 1);
        Idea conf = Idea.createIdea("CONFIGURATION", "", 2);
        Idea smart = Idea.createIdea("SMARTCAR_INFO", "", 3);
        Idea tf = Idea.createIdea("TRAFFIC_LIGHT", "", 4);
        Idea current_phase = Idea.createIdea("CURRENT_PHASE","", 5);
        Idea phase = Idea.createIdea("PHASE", "RED", 6);
        Idea numb = Idea.createIdea("NUMBER", "4", 7);


        current_phase.add(numb);
        current_phase.add(phase);
        tf.add(current_phase);
        conf.add(tf);
        conf.add(smart);
        cp.add(conf);
        il.add(cp);

        String soarRulesPath="src/test/resources/smartCarNested.soar";
        jSoarCodelet.initSoarPlugin("testAgent", new File(soarRulesPath), false);
        jSoarCodelet.setInputLinkIdea(il);

        mind.insertCodelet(jSoarCodelet);

        mind.start();
        try{
            Thread.sleep(3000L);
        }catch(Exception e){
            e.printStackTrace();
        }

        ArrayList<Object> outputList = jSoarCodelet.getOutputInObject("br.unicamp.cst.bindings.soar");

        mind.shutDown();
        assertTrue(outputList.get(0) instanceof SoarCommandNested);
        assertTrue(((SoarCommandNested)outputList.get(0)).getNestedClass() instanceof SoarCommandChange);
        assertEquals("change", ((SoarCommandChange)outputList.get(0)).getProductionName());
        assertEquals(2, ((SoarCommandChange)outputList.get(0)).getQuantity(), 0);
    }


    @Test
    public void buildJavaObjectWrongPackageExceptionTest(){
        Mind mind = new Mind();

        Idea il = Idea.createIdea("InputLink", "", 0);
        Idea cp = Idea.createIdea("CURRENT_PERCEPTION", "", 1);
        Idea conf = Idea.createIdea("CONFIGURATION", "", 2);
        Idea smart = Idea.createIdea("SMARTCAR_INFO", "", 3);
        Idea tf = Idea.createIdea("TRAFFIC_LIGHT", "", 4);
        Idea current_phase = Idea.createIdea("CURRENT_PHASE","", 5);
        Idea phase = Idea.createIdea("PHASE", "RED", 6);
        Idea numb = Idea.createIdea("NUMBER", "4", 7);


        current_phase.add(numb);
        current_phase.add(phase);
        tf.add(current_phase);
        conf.add(tf);
        conf.add(smart);
        cp.add(conf);
        il.add(cp);

        String soarRulesPath="src/test/resources/smartCar.soar";
        jSoarCodelet.initSoarPlugin("testAgent", new File(soarRulesPath), false);
        jSoarCodelet.setInputLinkIdea(il);

        mind.insertCodelet(jSoarCodelet);

        mind.start();
        try{
            Thread.sleep(3000L);
        }catch(Exception e){
            e.printStackTrace();
        }

        //wrong package
        ArrayList<Object> outputList = jSoarCodelet.getOutputInObject("br.unicamp.cst.bindings.ros");


        assertNull(outputList.get(0));
        mind.shutDown();
    }


    @Test
    public void buildJavaObjectNoOutputLinkTest(){
        Mind mind = new Mind();

        Idea il = Idea.createIdea("InputLink", "", 0);
        Idea cp = Idea.createIdea("CURRENT_PERCEPTION", "", 1);

        il.add(cp);

        String soarRulesPath="src/test/resources/smartCar.soar";
        jSoarCodelet.initSoarPlugin("testAgent", new File(soarRulesPath), false);
        jSoarCodelet.setInputLinkIdea(il);

        mind.insertCodelet(jSoarCodelet);

        mind.start();
        try{
            Thread.sleep(3000L);
        }catch(Exception e){
            e.printStackTrace();
        }

        ArrayList<Object> outputList = jSoarCodelet.getOutputInObject("br.unicamp.cst.bindings.soar");

        mind.shutDown();
        assertTrue(outputList.isEmpty());
        }


    @Test
    public void getCommandsJSONTest(){
        Mind mind = new Mind();

        Idea il = Idea.createIdea("InputLink", "", 0);
        Idea cp = Idea.createIdea("CURRENT_PERCEPTION", "", 1);
        Idea conf = Idea.createIdea("CONFIGURATION", "", 2);
        Idea smart = Idea.createIdea("SMARTCAR_INFO", "", 3);
        Idea tf = Idea.createIdea("TRAFFIC_LIGHT", "", 4);
        Idea current_phase = Idea.createIdea("CURRENT_PHASE","", 5);
        Idea phase = Idea.createIdea("PHASE", "RED", 6);
        Idea numb = Idea.createIdea("NUMBER", "4", 7);


        current_phase.add(numb);
        current_phase.add(phase);
        tf.add(current_phase);
        conf.add(tf);
        conf.add(smart);
        cp.add(conf);
        il.add(cp);

        String soarRulesPath="src/test/resources/smartCar.soar";
        jSoarCodelet.initSoarPlugin("testAgent", new File(soarRulesPath), false);
        jSoarCodelet.setInputLinkIdea(il);

        mind.insertCodelet(jSoarCodelet);

        mind.start();
        try{
            Thread.sleep(3000L);
        }catch(Exception e){
            e.printStackTrace();
        }

        ArrayList<Object> outputList = jSoarCodelet.getCommandsJSON("br.unicamp.cst.bindings.soar");

        mind.shutDown();
        assertTrue(outputList.get(0) instanceof SoarCommandChange);
        assertEquals("change", ((SoarCommandChange)outputList.get(0)).getProductionName());
        assertEquals(2, ((SoarCommandChange)outputList.get(0)).getQuantity(), 0);
    }

    @Test
    public void getCommandsJSONWrongPackageExceptionTest(){
        Mind mind = new Mind();

        Idea il = Idea.createIdea("InputLink", "", 0);
        Idea cp = Idea.createIdea("CURRENT_PERCEPTION", "", 1);
        Idea conf = Idea.createIdea("CONFIGURATION", "", 2);
        Idea smart = Idea.createIdea("SMARTCAR_INFO", "", 3);
        Idea tf = Idea.createIdea("TRAFFIC_LIGHT", "", 4);
        Idea current_phase = Idea.createIdea("CURRENT_PHASE","", 5);
        Idea phase = Idea.createIdea("PHASE", "RED", 6);
        Idea numb = Idea.createIdea("NUMBER", "4", 7);


        current_phase.add(numb);
        current_phase.add(phase);
        tf.add(current_phase);
        conf.add(tf);
        conf.add(smart);
        cp.add(conf);
        il.add(cp);

        String soarRulesPath="src/test/resources/smartCar.soar";
        jSoarCodelet.initSoarPlugin("testAgent", new File(soarRulesPath), false);
        jSoarCodelet.setInputLinkIdea(il);

        mind.insertCodelet(jSoarCodelet);

        mind.start();
        try{
            Thread.sleep(3000L);
        }catch(Exception e){
            e.printStackTrace();
        }

        //wrong package
        ArrayList<Object> outputList = jSoarCodelet.getCommandsJSON("br.unicamp.cst.bindings.ros");

        mind.shutDown();
        assertTrue(outputList.isEmpty());
    }



    @Test
    public void createJsonTest(){
        Mind mind = new Mind();
        Idea il = Idea.createIdea("InputLink", "", 0);

        String soarRulesPath="src/test/resources/smartCar.soar";
        jSoarCodelet.initSoarPlugin("testAgent", new File(soarRulesPath), false);
        jSoarCodelet.setInputLinkIdea(il);

        mind.insertCodelet(jSoarCodelet);

        mind.start();

        try{
            Thread.sleep(3000L);
        }catch(Exception e){
            e.printStackTrace();
        }

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\"}}}}}}";
        String jsonStringNumber = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"NUMBER\":4.0}}}}}}";
        JsonObject expectedJsonString = JsonParser.parseString(jsonString).getAsJsonObject();
        JsonObject expectedJsonNumber = JsonParser.parseString(jsonStringNumber).getAsJsonObject();

        JsonObject testJson = jSoarCodelet.createJson(
                "InputLink.CURRENT_PERCEPTION.CONFIGURATION.TRAFFIC_LIGHT.CURRENT_PHASE.PHASE", "RED");

        JsonObject testJsonNumber = jSoarCodelet.createJson(
                "InputLink.CURRENT_PERCEPTION.CONFIGURATION.TRAFFIC_LIGHT.CURRENT_PHASE.NUMBER", 4);


        mind.shutDown();
        assertEquals(expectedJsonString, testJson);
        assertEquals(expectedJsonNumber, testJsonNumber);
    }

    @Test
    public void addToJsonPropertyTest(){
        Mind mind = new Mind();
        Idea il = Idea.createIdea("InputLink", "", 0);

        String soarRulesPath="src/test/resources/smartCar.soar";
        jSoarCodelet.initSoarPlugin("testAgent", new File(soarRulesPath), false);
        jSoarCodelet.setInputLinkIdea(il);

        mind.insertCodelet(jSoarCodelet);

        mind.start();

        try{
            Thread.sleep(3000L);
        }catch(Exception e){
            e.printStackTrace();
        }

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\"}},\"SMARTCAR\":{\"INFO\":\"NO\"}}}}}";
        JsonObject expectedJson = JsonParser.parseString(jsonString).getAsJsonObject();

        JsonObject testJson = jSoarCodelet.createJson(
                "InputLink.CURRENT_PERCEPTION.CONFIGURATION.TRAFFIC_LIGHT.CURRENT_PHASE.PHASE", "RED");

        JsonObject toAdd = new JsonObject();
        toAdd.add("INFO", new JsonPrimitive("NO"));

        JsonObject toReceive = testJson.get("InputLink").getAsJsonObject().get("CURRENT_PERCEPTION").getAsJsonObject()
                .get("CONFIGURATION").getAsJsonObject();

        jSoarCodelet.addToJson(toAdd, toReceive, "SMARTCAR");

        mind.shutDown();
        assertEquals(expectedJson, testJson);
    }

    @Test
    public void addToJsonTest(){
        Mind mind = new Mind();
        Idea il = Idea.createIdea("InputLink", "", 0);

        String soarRulesPath="src/test/resources/smartCar.soar";
        jSoarCodelet.initSoarPlugin("testAgent", new File(soarRulesPath), false);
        jSoarCodelet.setInputLinkIdea(il);

        mind.insertCodelet(jSoarCodelet);

        mind.start();

        try{
            Thread.sleep(3000L);
        }catch(Exception e){
            e.printStackTrace();
        }

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\",\"NUMBER\":4.0,\"INFO\":\"OK\"}},\"SMARTCAR\":{\"INFO\":\"NO\"}}}}}";
        JsonObject expectedJson = JsonParser.parseString(jsonString).getAsJsonObject();

        JsonObject testJson = jSoarCodelet.createJson(
                "InputLink.CURRENT_PERCEPTION.CONFIGURATION.TRAFFIC_LIGHT.CURRENT_PHASE.PHASE", "RED");

        jSoarCodelet.addToJson("InputLink.CURRENT_PERCEPTION.CONFIGURATION.TRAFFIC_LIGHT.CURRENT_PHASE.NUMBER", testJson, 4.0);
        jSoarCodelet.addToJson("InputLink.CURRENT_PERCEPTION.CONFIGURATION.TRAFFIC_LIGHT.CURRENT_PHASE.INFO", testJson, "OK");
        jSoarCodelet.addToJson("InputLink.CURRENT_PERCEPTION.CONFIGURATION.SMARTCAR", testJson, JsonParser.parseString("{\"INFO\":\"NO\"}").getAsJsonObject());

        mind.shutDown();
        assertEquals(expectedJson, testJson);
    }


    @Test
    public void setInputLinkJsonTest(){
        Mind mind = new Mind();

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\",\"NUMBER\":4.0}},\"SMARTCAR_INFO\":\"NO\"}}}}";
        JsonObject jsonInput = JsonParser.parseString(jsonString).getAsJsonObject();

        String expectedInput = "(I2,CURRENT_PERCEPTION,W1)\n" +
                "   (W1,CONFIGURATION,W2)\n" +
                "      (W2,TRAFFIC_LIGHT,W3)\n" +
                "         (W3,CURRENT_PHASE,W4)\n" +
                "            (W4,PHASE,RED)\n" +
                "            (W4,NUMBER,4.0)\n" +
                "      (W2,SMARTCAR_INFO,NO)\n";

        String expectedOutput = "(I3,SoarCommandChange,C1)\n" +
                "   (C1,productionName,change)\n" +
                "   (C1,quantity,2)\n" +
                "   (C1,apply,true)\n";

        String soarRulesPath="src/test/resources/smartCar.soar";
        jSoarCodelet.initSoarPlugin("testAgent", new File(soarRulesPath), false);
        //Idea inputIdea  =createIdeaFromJson(jsonInput);
        jSoarCodelet.setInputLinkJson(jsonInput);

        mind.insertCodelet(jSoarCodelet);

        mind.start();
        try{
            Thread.sleep(3000L);
        }catch(Exception e){
            e.printStackTrace();
        }

        String inputLink = jSoarCodelet.getInputLinkAsString();
        String outputLink = jSoarCodelet.getOutputLinkAsString();

        assertEquals(expectedInput, inputLink);
        assertEquals(expectedOutput, outputLink);
        mind.shutDown();
    }

    @Test
    public void setAndGetNameTest(){
        Mind mind = new Mind();

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\",\"NUMBER\":4.0}},\"SMARTCAR_INFO\":\"NO\"}}}}";
        JsonObject jsonInput = JsonParser.parseString(jsonString).getAsJsonObject();

        String soarRulesPath="src/test/resources/smartCar.soar";
        jSoarCodelet.initSoarPlugin("testAgent", new File(soarRulesPath), false);
        jSoarCodelet.setInputLinkJson(jsonInput);

        mind.insertCodelet(jSoarCodelet);


        String name = "testName";
        jSoarCodelet.setAgentName(name);


        assertEquals(name, jSoarCodelet.getAgentName());
    }

    @Test
    public void setAndGetProductionPathTest(){
        Mind mind = new Mind();

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\",\"NUMBER\":4.0}},\"SMARTCAR_INFO\":\"NO\"}}}}";
        JsonObject jsonInput = JsonParser.parseString(jsonString).getAsJsonObject();

        String soarRulesPath="src/test/resources/smartCar.soar";
        jSoarCodelet.initSoarPlugin("testAgent", null, false);
        jSoarCodelet.setInputLinkJson(jsonInput);

        mind.insertCodelet(jSoarCodelet);

        jSoarCodelet.setProductionPath(new File(soarRulesPath));


        assertEquals(new File(soarRulesPath), jSoarCodelet.getProductionPath());
    }

    @Test
    public void removeFromJsonTest(){
        Mind mind = new Mind();
        Idea il = Idea.createIdea("InputLink", "", 0);

        String soarRulesPath="src/test/resources/smartCar.soar";
        jSoarCodelet.initSoarPlugin("testAgent", new File(soarRulesPath), false);
        jSoarCodelet.setInputLinkIdea(il);

        mind.insertCodelet(jSoarCodelet);

        mind.start();

        try{
            Thread.sleep(3000L);
        }catch(Exception e){
            e.printStackTrace();
        }

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\",\"NUMBER\":4.0}},\"SMARTCAR\":{\"INFO\":\"NO\"}}}}}";
        JsonObject expectedJson = JsonParser.parseString(jsonString).getAsJsonObject();

        JsonObject testJson = jSoarCodelet.createJson(
                "InputLink.CURRENT_PERCEPTION.CONFIGURATION.TRAFFIC_LIGHT.CURRENT_PHASE.PHASE", "RED");

        jSoarCodelet.addToJson("InputLink.CURRENT_PERCEPTION.CONFIGURATION.TRAFFIC_LIGHT.CURRENT_PHASE.NUMBER", testJson, 4.0);
        jSoarCodelet.addToJson("InputLink.CURRENT_PERCEPTION.CONFIGURATION.TRAFFIC_LIGHT.CURRENT_PHASE.INFO", testJson, "OK");
        jSoarCodelet.addToJson("InputLink.CURRENT_PERCEPTION.CONFIGURATION.SMARTCAR", testJson, JsonParser.parseString("{\"INFO\":\"NO\"}").getAsJsonObject());

        jSoarCodelet.removeJson("InputLink.CURRENT_PERCEPTION.CONFIGURATION.TRAFFIC_LIGHT.CURRENT_PHASE.INFO", testJson);
        mind.shutDown();
        assertEquals(expectedJson, testJson);
    }

}
