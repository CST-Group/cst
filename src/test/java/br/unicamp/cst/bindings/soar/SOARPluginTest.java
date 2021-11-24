package br.unicamp.cst.bindings.soar;

import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.representation.wme.Idea;
import com.google.gson.*;
import org.jsoar.kernel.Agent;
import org.jsoar.kernel.SoarException;
import org.jsoar.kernel.symbols.Identifier;
import org.jsoar.util.commands.SoarCommandInterpreter;
import org.jsoar.util.commands.SoarCommands;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * @author wander
 *
 */
public class SOARPluginTest {

    @Test
    public void simplestSOARTest(){
        SOARPlugin soarPlugin = new SOARPlugin();
        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\",\"NUMBER\":4.0}},\"SMARTCAR_INFO\":\"NO\"}}}}";
        JsonObject jsonInput = JsonParser.parseString(jsonString).getAsJsonObject();

        String soarRulesPath="src/test/resources/smartCar.soar";

        soarPlugin.setAgentName("testName");
        soarPlugin.setProductionPath(new File(soarRulesPath));

        Agent agent = new Agent();
        agent.setName("testName");

        soarPlugin.setAgent(agent);

        try {
            // Load some productions
            String path = soarPlugin.getProductionPath().getAbsolutePath();
            SoarCommands.source(soarPlugin.getAgent().getInterpreter(), path);
            soarPlugin.setInputLinkIdentifier(soarPlugin.getAgent().getInputOutput().getInputLink());
        } catch (SoarException e) {
            e.printStackTrace();
        }

        soarPlugin.setInputLinkIdea((Idea)soarPlugin.createIdeaFromJson(jsonInput));
        soarPlugin.runSOAR();

        try{
            Thread.sleep(5000L);
        }
        catch (Exception e){
            e.printStackTrace();
        }


        String expectedOutput = "(I3,SoarCommandChange,C1)\n" +
                "   (C1,productionName,change)\n" +
                "   (C1,quantity,2)\n" +
                "   (C1,apply,true)\n";

        String actualOutput = soarPlugin.getOutputLinkAsString();
        assertEquals(expectedOutput, actualOutput);
        soarPlugin.stopSOAR();
    }

    @Test
    public void finishMStepsSOARTest(){
        String soarRulesPath="src/test/resources/smartCar.soar";
        SOARPlugin soarPlugin = new SOARPlugin("testName", new File(soarRulesPath), false);

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\",\"NUMBER\":4.0}},\"SMARTCAR_INFO\":\"NO\"}}}}";
        JsonObject jsonInput = JsonParser.parseString(jsonString).getAsJsonObject();

        soarPlugin.setInputLinkIdea((Idea)soarPlugin.createIdeaFromJson(jsonInput));
        soarPlugin.setPhase(2);

        assertEquals(2, soarPlugin.getPhase(), 0);
        soarPlugin.runSOAR();

        try{
            Thread.sleep(5000L);
        }
        catch (Exception e){
            e.printStackTrace();
        }


        String expectedOutput = "(I3,SoarCommandChange,C1)\n" +
                "   (C1,productionName,change)\n" +
                "   (C1,quantity,2)\n" +
                "   (C1,apply,true)\n";

        String actualOutput = soarPlugin.getOutputLinkAsString();
        assertEquals(expectedOutput, actualOutput);
        soarPlugin.stopSOAR();
    }

    @Test
    public void moveToFinalStepSOARTest(){
        String soarRulesPath="src/test/resources/smartCar.soar";
        SOARPlugin soarPlugin = new SOARPlugin("testName", new File(soarRulesPath), false);

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\",\"NUMBER\":4.0}},\"SMARTCAR_INFO\":\"NO\"}}}}";
        JsonObject jsonInput = JsonParser.parseString(jsonString).getAsJsonObject();


        soarPlugin.setInputLinkIdea((Idea)soarPlugin.createIdeaFromJson(jsonInput));
        soarPlugin.setPhase(2);

        assertEquals(2, soarPlugin.getPhase(), 0);

        soarPlugin.moveToFinalStep();
        assertEquals(-1, soarPlugin.getPhase(), 0);

        soarPlugin.runSOAR();

        try{
            Thread.sleep(5000L);
        }
        catch (Exception e){
            e.printStackTrace();
        }


        String expectedOutput = "(I3,SoarCommandChange,C1)\n" +
                "   (C1,productionName,change)\n" +
                "   (C1,quantity,2)\n" +
                "   (C1,apply,true)\n";

        String actualOutput = soarPlugin.getOutputLinkAsString();
        assertEquals(expectedOutput, actualOutput);
        soarPlugin.stopSOAR();
    }

    @Test
    public void stopResetFinalizeTest(){
        String soarRulesPath="src/test/resources/smartCar.soar";
        SOARPlugin soarPlugin = new SOARPlugin("testName", new File(soarRulesPath), false);

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\",\"NUMBER\":4.0}},\"SMARTCAR_INFO\":\"NO\"}}}}";
        JsonObject jsonInput = JsonParser.parseString(jsonString).getAsJsonObject();


        soarPlugin.setInputLinkIdea((Idea)soarPlugin.createIdeaFromJson(jsonInput));

        soarPlugin.runSOAR();

        String prePhase = soarPlugin.getAgent().getCurrentPhase().toString();

        soarPlugin.stopSOAR();
        assertEquals(prePhase, soarPlugin.getAgent().getCurrentPhase().toString());

        SoarCommandInterpreter preInterpreter = soarPlugin.getAgent().getInterpreter();
        soarPlugin.finalizeKernel();

        assertNotEquals(preInterpreter, soarPlugin.getAgent().getInterpreter());

        Identifier preInput = soarPlugin.getInputLinkIdentifier();
        soarPlugin.resetSOAR();
        assertNotEquals(preInput, soarPlugin.getInputLinkIdentifier());

        soarPlugin.runSOAR();

        try{
            Thread.sleep(5000L);
        }
        catch (Exception e){
            e.printStackTrace();
        }


        String expectedOutput = "(I3,SoarCommandChange,C1)\n" +
                "   (C1,productionName,change)\n" +
                "   (C1,quantity,2)\n" +
                "   (C1,apply,true)\n";

        String actualOutput = soarPlugin.getOutputLinkAsString();
        assertEquals(expectedOutput, actualOutput);
        soarPlugin.stopSOAR();
    }

    @Test
    public void printWMEsTest(){
        String soarRulesPath="src/test/resources/smartCar.soar";
        SOARPlugin soarPlugin = new SOARPlugin("testName", new File(soarRulesPath), false);

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\",\"NUMBER\":4.0}},\"SMARTCAR_INFO\":\"NO\"}}}}";
        JsonObject jsonInput = JsonParser.parseString(jsonString).getAsJsonObject();


        soarPlugin.setInputLinkIdea((Idea)soarPlugin.createIdeaFromJson(jsonInput));

        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        soarPlugin.runSOAR();

        try{
            Thread.sleep(5000L);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        soarPlugin.printWMEs(soarPlugin.getInputLink_WME());

        String expectedInputWME = "(I2,CURRENT_PERCEPTION,W1)";
        String expectedOutput = "(I3,SoarCommandChange,C1)\n" +
                "   (C1,productionName,change)\n" +
                "   (C1,quantity,2)\n" +
                "   (C1,apply,true)\n";

        String actualOutput = soarPlugin.getOutputLinkAsString();


        assertTrue(outputStreamCaptor.toString().trim().contains(expectedInputWME));

        assertEquals(expectedOutput, actualOutput);
        soarPlugin.stopSOAR();
    }

    @Test
    public void getWMEsAsStringTest(){
        String soarRulesPath="src/test/resources/smartCar.soar";
        SOARPlugin soarPlugin = new SOARPlugin("testName", new File(soarRulesPath), false);

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\",\"NUMBER\":4.0}},\"SMARTCAR_INFO\":\"NO\"}}}}";
        JsonObject jsonInput = JsonParser.parseString(jsonString).getAsJsonObject();


        soarPlugin.setInputLinkIdea((Idea)soarPlugin.createIdeaFromJson(jsonInput));

        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        soarPlugin.runSOAR();

        try{
            Thread.sleep(5000L);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        String actualString = soarPlugin.getWMEsAsString(soarPlugin.getOutputLink_WME());

        String expectedString = "(I3,SoarCommandChange,C1)\n" +
                "   (C1,productionName,change)\n" +
                "   (C1,quantity,2)\n" +
                "   (C1,apply,true)\n   ";


        String expectedOutput = "(I3,SoarCommandChange,C1)\n" +
                "   (C1,productionName,change)\n" +
                "   (C1,quantity,2)\n" +
                "   (C1,apply,true)\n";

        String actualOutput = soarPlugin.getOutputLinkAsString();


        assertTrue(expectedString.contains(actualString));

        assertEquals(expectedOutput, actualOutput);
        soarPlugin.stopSOAR();
    }

    @Test
    public void parseTest(){
        String soarRulesPath="src/test/resources/smartCar.soar";
        SOARPlugin soarPlugin = new SOARPlugin("testName", new File(soarRulesPath), false);

        assertEquals(2.0, (Double)soarPlugin.convertObject(2, "double"), 0);
        assertEquals(2.0, (Double)soarPlugin.convertObject("2", "double"), 0);
        assertNull((Float)soarPlugin.convertObject("SSSSSSSSSSSS", "double"));
        assertEquals("java.lang.Double", soarPlugin.convertObject(2, "double").getClass().getCanonicalName());

        assertEquals(2.0, (Float)soarPlugin.convertObject(2, "float"), 0);
        assertEquals(2.0, (Float)soarPlugin.convertObject("2", "float"), 0);
        assertNull((Float)soarPlugin.convertObject("SSSSSSSSSSSS", "float"));
        assertEquals("java.lang.Float", soarPlugin.convertObject(2, "float").getClass().getCanonicalName());

        assertEquals(2, (Integer)soarPlugin.convertObject(2, "int"), 0);
        assertEquals(2, (Integer)soarPlugin.convertObject("2", "int"), 0);
        assertNull((Float)soarPlugin.convertObject("SSSSSSSSSSSS", "int"));
        assertEquals("java.lang.Integer", soarPlugin.convertObject(2, "int").getClass().getCanonicalName());

        assertEquals(2, (Short)soarPlugin.convertObject(2, "short"), 0);
        assertEquals(2, (Short)soarPlugin.convertObject("2", "short"), 0);
        assertNull((Float)soarPlugin.convertObject("SSSSSSSSSSSS", "short"));
        assertEquals("java.lang.Short", soarPlugin.convertObject(2, "short").getClass().getCanonicalName());

        assertEquals(2, (Long)soarPlugin.convertObject(2, "long"), 0);
        assertEquals(2, (Long)soarPlugin.convertObject("2", "long"), 0);
        assertNull((Float)soarPlugin.convertObject("SSSSSSSSSSSS", "long"));
        assertEquals("java.lang.Long", soarPlugin.convertObject(2, "long").getClass().getCanonicalName());
    }

    @Test
    public void getOutputLinkIdentifierTest(){
        String soarRulesPath="src/test/resources/smartCar.soar";
        SOARPlugin soarPlugin = new SOARPlugin("testName", new File(soarRulesPath), false);

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\",\"NUMBER\":4.0}},\"SMARTCAR_INFO\":\"NO\"}}}}";
        JsonObject jsonInput = JsonParser.parseString(jsonString).getAsJsonObject();


        soarPlugin.setInputLinkIdea((Idea)soarPlugin.createIdeaFromJson(jsonInput));

        soarPlugin.runSOAR();

        try{
            Thread.sleep(2000L);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        String expectedOutput = "(I3,SoarCommandChange,C1)\n" +
                "   (C1,productionName,change)\n" +
                "   (C1,quantity,2)\n" +
                "   (C1,apply,true)\n";

        String actualOutput = soarPlugin.getOutputLinkAsString();
        assertEquals("I3", soarPlugin.getOutputLinkIdentifier().toString());
        assertEquals(expectedOutput, actualOutput);
        soarPlugin.stopSOAR();
    }

    @Test
    public void createJsonFromStringObjectTest(){
        String soarRulesPath="src/test/resources/smartCar.soar";
        SOARPlugin soarPlugin = new SOARPlugin("testName", new File(soarRulesPath), false);

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\"}}}}}}";
        JsonObject jsonInput = JsonParser.parseString(jsonString).getAsJsonObject();


        soarPlugin.setInputLinkIdea((Idea)soarPlugin.createIdeaFromJson(jsonInput));

        soarPlugin.runSOAR();

        try{
            Thread.sleep(2000L);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        JsonObject value = new JsonObject();
        value.add("PHASE", new JsonPrimitive("RED"));

        JsonObject parsed = soarPlugin.createJsonFromString("InputLink.CURRENT_PERCEPTION.CONFIGURATION.TRAFFIC_LIGHT.CURRENT_PHASE", value);
        soarPlugin.stopSOAR();

        assertEquals(jsonInput, parsed);
    }

    @Test
    public void createIdeaFromJsonBooleanTest(){
        String soarRulesPath="src/test/resources/smartCar.soar";
        SOARPlugin soarPlugin = new SOARPlugin("testName", new File(soarRulesPath), false);

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\": true}}}}}}";
        JsonObject jsonInput = JsonParser.parseString(jsonString).getAsJsonObject();


        Idea idea = (Idea)soarPlugin.createIdeaFromJson(jsonInput);


        assertTrue((Boolean) idea.getL().get(0).getL().get(0).getL().get(0).getL().get(0).getL().get(0).getValue());
    }


    @Test
    public void addBranchToJsonNonExistingTest(){
        String soarRulesPath="src/test/resources/smartCar.soar";
        SOARPlugin soarPlugin = new SOARPlugin("testName", new File(soarRulesPath), false);

        String toCreateString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\"}},\"SMARTCAR\":{\"INFO\":\"NO\"}}}}}";
        JsonObject expectedJsonString = JsonParser.parseString(toCreateString).getAsJsonObject();

        String toCreateDouble = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\"}},\"SMARTCAR\":{\"INFO\":4.0}}}}}";
        JsonObject expectedJsonDouble = JsonParser.parseString(toCreateDouble).getAsJsonObject();

        String toCreateObject = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\"}},\"SMARTCAR\":{\"INFO\":{\"PRESENT\":\"NO\"}}}}}}";
        JsonObject expectedJsonObject = JsonParser.parseString(toCreateObject).getAsJsonObject();

        JsonObject testJsonString = soarPlugin.createJsonFromString(
                "InputLink.CURRENT_PERCEPTION.CONFIGURATION.TRAFFIC_LIGHT.CURRENT_PHASE.PHASE", "RED");

        JsonObject testJsonDouble = soarPlugin.createJsonFromString(
                "InputLink.CURRENT_PERCEPTION.CONFIGURATION.TRAFFIC_LIGHT.CURRENT_PHASE.PHASE", "RED");

        JsonObject testJsonObject = soarPlugin.createJsonFromString(
                "InputLink.CURRENT_PERCEPTION.CONFIGURATION.TRAFFIC_LIGHT.CURRENT_PHASE.PHASE", "RED");

        JsonObject toAdd = new JsonObject();
        toAdd.add("PRESENT", new JsonPrimitive("NO"));

        //JsonObject toReceive = testJson.get("InputLink").getAsJsonObject().get("CURRENT_PERCEPTION").getAsJsonObject()
        //        .get("CONFIGURATION").getAsJsonObject();

        soarPlugin.addBranchToJson("InputLink.CURRENT_PERCEPTION.CONFIGURATION.SMARTCAR.INFO", testJsonString, "NO");
        soarPlugin.addBranchToJson("InputLink.CURRENT_PERCEPTION.CONFIGURATION.SMARTCAR.INFO", testJsonDouble, 4);
        soarPlugin.addBranchToJson("InputLink.CURRENT_PERCEPTION.CONFIGURATION.SMARTCAR.INFO", testJsonObject, toAdd);

        assertEquals(expectedJsonString, testJsonString);
        assertEquals(expectedJsonDouble, testJsonDouble);
        assertEquals(expectedJsonObject, testJsonObject);
    }


    @Test
    public void fromBeanToJsonTest(){

        String soarRulesPath="src/test/resources/smartCar.soar";
        SOARPlugin soarPlugin = new SOARPlugin("testName", new File(soarRulesPath), false);

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\",\"NUMBER\":4.0}},\"SMARTCAR_INFO\":\"NO\"}}}}";
        JsonObject jsonInput = JsonParser.parseString(jsonString).getAsJsonObject();


        soarPlugin.setInputLinkIdea((Idea)soarPlugin.createIdeaFromJson(jsonInput));

        soarPlugin.runSOAR();

        try{
            Thread.sleep(2000L);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        SoarCommandChange soarCommandChange = new SoarCommandChange();
        soarCommandChange.setApply("true");
        soarCommandChange.setQuantity(2);
        soarCommandChange.setProductionName("soarCommandChange");

        JsonObject jsonObjectFromBean = soarPlugin.fromBeanToJson(soarCommandChange);

        JsonObject expectedFromBean = JsonParser.parseString("{\"br.unicamp.cst.bindings.soar.SoarCommandChange\":" +
                "{\"productionName\":\"soarCommandChange\",\"quantity\":\"2.0\",\"apply\":\"true\"}}").getAsJsonObject();


        String expectedOutput = "(I3,SoarCommandChange,C1)\n" +
                "   (C1,productionName,change)\n" +
                "   (C1,quantity,2)\n" +
                "   (C1,apply,true)\n";

        String actualOutput = soarPlugin.getOutputLinkAsString();
        assertEquals("I3", soarPlugin.getOutputLinkIdentifier().toString());
        assertEquals(expectedOutput, actualOutput);
        assertEquals(expectedFromBean, jsonObjectFromBean);
        soarPlugin.stopSOAR();
    }


    @Test
    public void containsWmeTest(){
        //getIndexOfWME
        String soarRulesPath="src/test/resources/smartCar.soar";
        SOARPlugin soarPlugin = new SOARPlugin("testName", new File(soarRulesPath), false);

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\",\"NUMBER\":4.0}},\"SMARTCAR_INFO\":\"NO\"}}}}";
        JsonObject jsonInput = JsonParser.parseString(jsonString).getAsJsonObject();


        soarPlugin.setInputLinkIdea((Idea)soarPlugin.createIdeaFromJson(jsonInput));

        soarPlugin.runSOAR();

        try{
            Thread.sleep(2000L);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        String expectedOutput = "(I3,SoarCommandChange,C1)\n" +
                "   (C1,productionName,change)\n" +
                "   (C1,quantity,2)\n" +
                "   (C1,apply,true)\n";

        String actualOutput = soarPlugin.getOutputLinkAsString();
        assertEquals("I3", soarPlugin.getOutputLinkIdentifier().toString());
        assertEquals(expectedOutput, actualOutput);
        assertTrue(soarPlugin.containsWme(soarPlugin.getInputLink_WME(), "CONFIGURATION"));
        assertFalse(soarPlugin.containsWme(soarPlugin.getInputLink_WME(), "DISRUPTION"));
        assertFalse(soarPlugin.containsWme(new ArrayList<>(), "DISRUPTION"));
        soarPlugin.stopSOAR();
    }


    @Test
    public void prettyPrintTest(){

        String soarRulesPath="src/test/resources/smartCar.soar";
        SOARPlugin soarPlugin = new SOARPlugin("testName", new File(soarRulesPath), false);

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\",\"NUMBER\":4.0}},\"SMARTCAR_INFO\":\"NO\"}}}}";
        JsonObject jsonInput = JsonParser.parseString(jsonString).getAsJsonObject();

        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        soarPlugin.setInputLinkIdea((Idea)soarPlugin.createIdeaFromJson(jsonInput));

        soarPlugin.runSOAR();

        try{
            Thread.sleep(2000L);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        String expectedPrint = "{\n" +
                "  \"InputLink\": {\n" +
                "    \"CURRENT_PERCEPTION\": {\n" +
                "      \"CONFIGURATION\": {\n" +
                "        \"TRAFFIC_LIGHT\": {\n" +
                "          \"CURRENT_PHASE\": {\n" +
                "            \"PHASE\": \"RED\",\n" +
                "            \"NUMBER\": 4.0\n" +
                "          }\n" +
                "        },\n" +
                "        \"SMARTCAR_INFO\": \"NO\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String expectedOutput = "(I3,SoarCommandChange,C1)\n" +
                "   (C1,productionName,change)\n" +
                "   (C1,quantity,2)\n" +
                "   (C1,apply,true)\n";

        System.out.println(soarPlugin.toPrettyFormat(jsonInput));

        String actualOutput = soarPlugin.getOutputLinkAsString();
        assertEquals("I3", soarPlugin.getOutputLinkIdentifier().toString());
        assertEquals(expectedOutput, actualOutput);
        assertTrue(outputStreamCaptor.toString().trim().contains(expectedPrint));
        soarPlugin.stopSOAR();
    }

    @Test
    public void printInputWMEsTest(){
        String soarRulesPath="src/test/resources/smartCar.soar";
        SOARPlugin soarPlugin = new SOARPlugin("testName", new File(soarRulesPath), false);

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\",\"NUMBER\":4.0}},\"SMARTCAR_INFO\":\"NO\"}}}}";
        JsonObject jsonInput = JsonParser.parseString(jsonString).getAsJsonObject();

        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        soarPlugin.setInputLinkIdea((Idea)soarPlugin.createIdeaFromJson(jsonInput));

        soarPlugin.runSOAR();

        try{
            Thread.sleep(2000L);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        String expectedPrint = "Input --->\n" +
                "(I2,CURRENT_PERCEPTION,W1)\n" +
                "   (W1,CONFIGURATION,W2)\n" +
                "      (W2,TRAFFIC_LIGHT,W3)\n" +
                "         (W3,CURRENT_PHASE,W4)\n" +
                "            (W4,PHASE,RED)\n" +
                "            (W4,NUMBER,4.0)";
        soarPlugin.printInputWMEs();

        String expectedOutput = "(I3,SoarCommandChange,C1)\n" +
                "   (C1,productionName,change)\n" +
                "   (C1,quantity,2)\n" +
                "   (C1,apply,true)\n";

        String actualOutput = soarPlugin.getOutputLinkAsString();
        assertEquals("I3", soarPlugin.getOutputLinkIdentifier().toString());
        assertEquals(expectedOutput, actualOutput);
        assertTrue(outputStreamCaptor.toString().trim().contains(expectedPrint));
        soarPlugin.stopSOAR();
    }


    @Test
    public void getInitialStateTest(){
        String soarRulesPath="src/test/resources/smartCar.soar";
        SOARPlugin soarPlugin = new SOARPlugin("testName", new File(soarRulesPath), false);

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\",\"NUMBER\":4.0}},\"SMARTCAR_INFO\":\"NO\"}}}}";
        JsonObject jsonInput = JsonParser.parseString(jsonString).getAsJsonObject();


        soarPlugin.setInputLinkIdea((Idea)soarPlugin.createIdeaFromJson(jsonInput));

        soarPlugin.runSOAR();

        try{
            Thread.sleep(2000L);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        String expectedOutput = "(I3,SoarCommandChange,C1)\n" +
                "   (C1,productionName,change)\n" +
                "   (C1,quantity,2)\n" +
                "   (C1,apply,true)\n";

        String actualOutput = soarPlugin.getOutputLinkAsString();
        assertEquals("I3", soarPlugin.getOutputLinkIdentifier().toString());
        assertEquals(expectedOutput, actualOutput);
        assertEquals("S1", soarPlugin.getInitialState().toString());
        soarPlugin.stopSOAR();
    }

    @Test
    public void searchInputLinkTest(){
        String soarRulesPath="src/test/resources/smartCar.soar";
        SOARPlugin soarPlugin = new SOARPlugin("testName", new File(soarRulesPath), false);

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\",\"NUMBER\":4.0}},\"SMARTCAR_INFO\":\"NO\"}}}}";
        JsonObject jsonInput = JsonParser.parseString(jsonString).getAsJsonObject();


        soarPlugin.setInputLinkIdea((Idea)soarPlugin.createIdeaFromJson(jsonInput));

        soarPlugin.runSOAR();

        try{
            Thread.sleep(2000L);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        String id = soarPlugin.searchInInputLink("CONFIGURATION", soarPlugin.getInputLinkIdentifier()).toString();

        String expectedOutput = "(I3,SoarCommandChange,C1)\n" +
                "   (C1,productionName,change)\n" +
                "   (C1,quantity,2)\n" +
                "   (C1,apply,true)\n";

        String actualOutput = soarPlugin.getOutputLinkAsString();
        assertEquals("I3", soarPlugin.getOutputLinkIdentifier().toString());
        assertEquals(expectedOutput, actualOutput);
        assertEquals("W2", id);
        assertNull(soarPlugin.searchInInputLink("DISRUPTION", soarPlugin.getInputLinkIdentifier()));

    }

    /*@Test
    public void getOutputLinkIdentifierTest(){
        String soarRulesPath="src/test/resources/smartCar.soar";
        SOARPlugin soarPlugin = new SOARPlugin("testName", new File(soarRulesPath), false);

        String jsonString = "{\"InputLink\":{\"CURRENT_PERCEPTION\":{\"CONFIGURATION\":{\"TRAFFIC_LIGHT\":{\"CURRENT_PHASE\":{\"PHASE\":\"RED\",\"NUMBER\":4.0}},\"SMARTCAR_INFO\":\"NO\"}}}}";
        JsonObject jsonInput = JsonParser.parseString(jsonString).getAsJsonObject();


        soarPlugin.setInputLinkIdea((Idea)soarPlugin.createIdeaFromJson(jsonInput));

        soarPlugin.runSOAR();

        try{
            Thread.sleep(2000L);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        String expectedOutput = "(I3,SoarCommandChange,C1)\n" +
                "   (C1,productionName,change)\n" +
                "   (C1,quantity,2)\n" +
                "   (C1,apply,true)\n";

        String actualOutput = soarPlugin.getOutputLinkAsString();
        assertEquals("I3", soarPlugin.getOutputLinkIdentifier().toString());
        assertEquals(expectedOutput, actualOutput);
        soarPlugin.stopSOAR();
    }*/
}
