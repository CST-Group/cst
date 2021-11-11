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

    /*@Test
    public void createIDWMETest(){

    }*/

}
