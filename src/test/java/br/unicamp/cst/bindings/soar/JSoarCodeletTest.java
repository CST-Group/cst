package br.unicamp.cst.bindings.soar;

import br.unicamp.cst.bindings.soar.PlansSubsystemModule;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.representation.wme.Idea;
import ch.qos.logback.core.encoder.EchoEncoder;
import org.junit.Test;

import java.io.File;

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
                "   (C1,productionName,change)\n";

        String soarRulesPath="src/test/resources/smartCar.soar";
        jSoarCodelet.initSoarPlugin("testAgent", new File(soarRulesPath), false);
        jSoarCodelet.setInputLinkIdea(il);

        mind.insertCodelet(jSoarCodelet);

        mind.start();
        try{
            Thread.sleep(8000L);
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


}
