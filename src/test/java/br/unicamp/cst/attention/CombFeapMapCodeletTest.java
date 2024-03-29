
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
package br.unicamp.cst.attention;

import br.unicamp.cst.core.entities.Codelet;

import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.support.TimeStamp;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;



/**
 *
 * Test for Codelet implementation of Feature Maps generated by the Attentional System of 
 * Conscious Attention-Based Integrated Model (CONAIM). The combined feature 
 * map is a weighted sum of the previous defined feature maps (bottom-up and
 * top-down).
 * @author L. L. Rossi (leolellisr)
 * @see Codelet
 * @see MemoryObject
 * @see FeatMapCodelet
 * @see CombFeatMapCodelet
 * @see CFM
 */
public class CombFeapMapCodeletTest {

    
    
    public MemoryObject source,source2,source3,weights;
    public MemoryObject destination,destination_type;
    public CFM testFeapMapCodelet, testFeapMapCodelet2;
    
    /**
     * Test class initialization for the Combined Feature Map. Creates a test 
     * mind, with 4 inputs (3 sources and 1 weight) and 2 outputs. The codelet 
     * to be tested is initialized as a CFM and inserted into the created mind. 
     * Inputs and outputs are added to the codelet and it is set to 
     * publish-subscribe. The mind is then initiated.
     * 
     */
    public CombFeapMapCodeletTest() {
        Mind testMind = new Mind();
        weights = testMind.createMemoryObject("FM_WEIGHTS");
        source = testMind.createMemoryObject( "SOURCE");
        source2 = testMind.createMemoryObject("SOURCE2");
        source3 = testMind.createMemoryObject("SOURCE3");
        //source.setI(0);
        destination_type = testMind.createMemoryObject("TYPE");
        destination_type.setI(new CopyOnWriteArrayList<Float>());
        destination = testMind.createMemoryObject("COMB_FM");
        destination.setI(new CopyOnWriteArrayList<Float>());
        CopyOnWriteArrayList<String> FMnames = new CopyOnWriteArrayList<>();
        FMnames.add("SOURCE");
        FMnames.add("SOURCE2");
        FMnames.add("SOURCE3");
        testFeapMapCodelet = new CFM(FMnames, 100, 16, false, true);
        testMind.insertCodelet(testFeapMapCodelet);
        testFeapMapCodelet.addInput(source);
        testFeapMapCodelet.addInput(source2);
        testFeapMapCodelet.addInput(source3);
        testFeapMapCodelet.addInput(weights);
        testFeapMapCodelet.addOutput(destination);
        testFeapMapCodelet.addOutput(destination_type);
        testFeapMapCodelet.setIsMemoryObserver(true);
	source.addMemoryObserver(testFeapMapCodelet);
        source2.addMemoryObserver(testFeapMapCodelet);
        source3.addMemoryObserver(testFeapMapCodelet);
        weights.addMemoryObserver(testFeapMapCodelet);
        
        testFeapMapCodelet2 = new CFM(FMnames, 100, 16, true, false);
        testMind.insertCodelet(testFeapMapCodelet2);
        testFeapMapCodelet2.addInput(source);
        testFeapMapCodelet2.addInput(source2);
        testFeapMapCodelet2.addInput(source3);
        testFeapMapCodelet2.addInput(weights);
        testFeapMapCodelet2.addOutput(destination);
        testFeapMapCodelet2.addOutput(destination_type);
        testFeapMapCodelet2.setIsMemoryObserver(true);
	source.addMemoryObserver(testFeapMapCodelet2);
        source2.addMemoryObserver(testFeapMapCodelet2);
        source3.addMemoryObserver(testFeapMapCodelet2);
        weights.addMemoryObserver(testFeapMapCodelet2);
        
        testMind.start();
        
        
        //List fulllist = (List)destination.getI();
        
        
    }
    
    /**
    * Test 1. Inputs have sequential elements from 1 to 4. The weight vector is 
    * initialized with 1s only. Thus, the output element will be the sum of the 
    * elements of the same position [3, 6, 9, 12 ...].
    * 
    * Test 2. Inputs have elements equal to 1. The weight vector is initialized 
    * with 1s only. Thus, the output element will be the sum of elements of the 
    * same position [3, 3, 3, 3 ...].
    * 
    * Test 3. Inputs have elements equal to 1. The weight vector is initialized 
    * with sequential elements from 1 to 3. Thus, the output element will be the 
    * sum of elements in the same position [1*1+1*2+1* 3=6, 6, 6, 6...]
    * 
    */
    @Test
    public void testCombFeapMapCodelet() {
            CombFeapMapCodeletTest test = new CombFeapMapCodeletTest();

            System.out.println("Testing ... ");
            long oldtimestamp = test.destination.getTimestamp();
            System.out.println("steps: "+test.testFeapMapCodelet.steps+" Timestamp before: "+TimeStamp.getStringTimeStamp(oldtimestamp, "dd/MM/yyyy HH:mm:ss.SSS"));
            
            CopyOnWriteArrayList<CopyOnWriteArrayList<Float>> arrList_test = new CopyOnWriteArrayList<CopyOnWriteArrayList<Float>>();
            
            // Test 1
            CopyOnWriteArrayList<Float> arrList_i = new CopyOnWriteArrayList<Float>();
            for (int i = 0; i < 4*4; i++) {
                arrList_i.add((float)(i % 4) + 1);
            }
            CopyOnWriteArrayList<Float> arrList_goal = new CopyOnWriteArrayList<Float>();
            for (int i = 0; i < 4; i++) {
                arrList_goal.add((float) 3.0);
                arrList_goal.add((float) 6.0);
                arrList_goal.add((float) 9.0);
                arrList_goal.add((float) 12.0);                
            }
                    
            arrList_test.add(arrList_i);
            System.out.println("arrList_test: "+arrList_test.size()+" arrList_i: "+arrList_i.size()+" arrList_goal: "+arrList_goal.size());
            
            CopyOnWriteArrayList<Float> arrList_weig = new CopyOnWriteArrayList<Float>();
            for (int i = 0; i < 3; i++) {
                arrList_weig.add((float) 1.0);
            }
            long newtimestamp = test.destination.getTimestamp();
            test.testFeapMapCodelet.resetTriggers();
            test.source.setI(arrList_test);
            //sleep(10);
            System.out.println("source: "+"steps: "+test.testFeapMapCodelet.steps+" Timestamp after: "+TimeStamp.getStringTimeStamp(test.source.getTimestamp(),"dd/MM/yyyy HH:mm:ss.SSS"));
            test.source2.setI(arrList_test);
            //sleep(10);
            System.out.println("source2: "+"steps: "+test.testFeapMapCodelet.steps+" Timestamp after: "+TimeStamp.getStringTimeStamp(test.source2.getTimestamp(),"dd/MM/yyyy HH:mm:ss.SSS"));
            test.source3.setI(arrList_test);
            //sleep(10);
            System.out.println("source3: "+"steps: "+test.testFeapMapCodelet.steps+" Timestamp after: "+TimeStamp.getStringTimeStamp(test.source3.getTimestamp(),"dd/MM/yyyy HH:mm:ss.SSS"));
            test.weights.setI(arrList_weig);
            //sleep(10);
            System.out.println("source: "+"steps: "+test.testFeapMapCodelet.steps+" Timestamp after: "+TimeStamp.getStringTimeStamp(test.weights.getTimestamp(),"dd/MM/yyyy HH:mm:ss.SSS"));
            while(test.testFeapMapCodelet.steps < 4) {
                newtimestamp = test.destination.getTimestamp();
                System.out.println("steps: "+test.testFeapMapCodelet.steps+" Timestamp while waiting: "+TimeStamp.getStringTimeStamp(newtimestamp,"dd/MM/yyyy HH:mm:ss.SSS"));
            }
            System.out.println("steps: "+test.testFeapMapCodelet.steps+" Timestamp after: "+TimeStamp.getStringTimeStamp(newtimestamp,"dd/MM/yyyy HH:mm:ss.SSS"));
            System.out.println("  Inputs 11: "+((List)((List)(test.source.getI())).get(0))+"\n size: "+((List)((List)(test.source.getI())).get(0)).size());
            System.out.println("  Inputs 12: "+((List)((List)(test.source2.getI())).get(0))+"\n size: "+((List)((List)(test.source2.getI())).get(0)).size());
            System.out.println("  Inputs 13: "+((List)((List)(test.source3.getI())).get(0))+"\n size: "+((List)((List)(test.source3.getI())).get(0)).size());
            System.out.println("   weights 1: "+test.weights.getI()+" size: "+((List)(test.weights.getI())).size());
            System.out.println("   Output 1: "+ test.destination.getI());
            System.out.println("   Goal 1: "+arrList_goal);
            List fulllist = (List) test.destination.getI();
            if (fulllist != null && fulllist.size() > 0) {
                System.out.println("          sizef: "+fulllist.size()+"\n");
                assertEquals(((List)(test.destination.getI())).size(),16);
                assertEquals(((List)(test.destination.getI())),arrList_goal);
                
                
            }  
            
            // Test 2
            oldtimestamp = test.destination.getTimestamp();
            System.out.println("steps: "+test.testFeapMapCodelet.steps+" Timestamp before: "+TimeStamp.getStringTimeStamp(oldtimestamp, "dd/MM/yyyy HH:mm:ss.SSS"));
            
            arrList_i = new CopyOnWriteArrayList<Float>();
            for (int i = 0; i < 4*4; i++) {
                arrList_i.add((float) 1);
            }
            arrList_goal = new CopyOnWriteArrayList<Float>();
            for (int i = 0; i < 16; i++) {
                arrList_goal.add((float) 3.0);
                
            }
                    
            newtimestamp = test.destination.getTimestamp();
            test.testFeapMapCodelet.resetTriggers();
            System.out.println("steps: "+test.testFeapMapCodelet.steps+" Timestamp before: "+TimeStamp.getStringTimeStamp(oldtimestamp, "dd/MM/yyyy HH:mm:ss.SSS"));
            //arrList_test = new CopyOnWriteArrayList<CopyOnWriteArrayList<Float>>();
            arrList_test.add(arrList_i);
            //System.out.println(arrList_test);
            System.out.println("arrList_test: "+arrList_test.size()+" arrList_i: "+arrList_i.size()+" arrList_goal: "+arrList_goal.size());
            test.source.setI(arrList_test);
            //sleep(10);
            System.out.println("source: "+"steps: "+test.testFeapMapCodelet.steps+" Timestamp after: "+TimeStamp.getStringTimeStamp(test.source.getTimestamp(),"dd/MM/yyyy HH:mm:ss.SSS"));
            test.source2.setI(arrList_test);
            //sleep(10);
            System.out.println("source2: "+"steps: "+test.testFeapMapCodelet.steps+" Timestamp after: "+TimeStamp.getStringTimeStamp(test.source2.getTimestamp(),"dd/MM/yyyy HH:mm:ss.SSS"));
            test.source3.setI(arrList_test);
            //sleep(10);
            System.out.println("source3: "+"steps: "+test.testFeapMapCodelet.steps+" Timestamp after: "+TimeStamp.getStringTimeStamp(test.source3.getTimestamp(),"dd/MM/yyyy HH:mm:ss.SSS"));
            arrList_weig = new CopyOnWriteArrayList<Float>();
            for (int i = 0; i < 3; i++) {
                arrList_weig.add((float) 1.0);
            }
            test.weights.setI(arrList_weig);
            //sleep(10);
            System.out.println("source: "+"steps: "+test.testFeapMapCodelet.steps+" Timestamp after: "+TimeStamp.getStringTimeStamp(test.weights.getTimestamp(),"dd/MM/yyyy HH:mm:ss.SSS"));
            while(test.testFeapMapCodelet.steps < 4) {
                newtimestamp = test.destination.getTimestamp();
                System.out.println("steps: "+test.testFeapMapCodelet.steps+" Timestamp while waiting: "+TimeStamp.getStringTimeStamp(newtimestamp,"dd/MM/yyyy HH:mm:ss.SSS"));
            }
            System.out.println("steps: "+test.testFeapMapCodelet.steps+" Timestamp after: "+TimeStamp.getStringTimeStamp(newtimestamp,"dd/MM/yyyy HH:mm:ss.SSS"));
            
            System.out.println("  Inputs 21: "+((List)((List)(test.source.getI())).get(1))+"\n size: "+((List)((List)(test.source.getI())).get(1)).size());
            System.out.println("  Inputs 22: "+((List)((List)(test.source2.getI())).get(1))+"\n size: "+((List)((List)(test.source2.getI())).get(1)).size());
            System.out.println("  Inputs 23: "+((List)((List)(test.source3.getI())).get(1))+"\n size: "+((List)((List)(test.source3.getI())).get(1)).size());
            System.out.println("   weights 2: "+test.weights.getI()+((List)(test.weights.getI())).size());
            System.out.print("  Output 2: "+ test.destination.getI());
            fulllist = (List) test.destination.getI();
            if (fulllist != null && fulllist.size() > 0) {
                System.out.println("          sizef: "+((List)(test.destination.getI())).size()+"\n");
                assertEquals(fulllist.size(),16);
                assertEquals(fulllist,arrList_goal);
                
            } 
            
            // Test 3
            oldtimestamp = test.destination.getTimestamp();
            System.out.println("steps: "+test.testFeapMapCodelet.steps+" Timestamp before: "+TimeStamp.getStringTimeStamp(oldtimestamp, "dd/MM/yyyy HH:mm:ss.SSS"));
            
            arrList_i = new CopyOnWriteArrayList<Float>();
            for (int i = 0; i < 16; i++) {
                arrList_i.add((float) 1);
            }
            arrList_goal = new CopyOnWriteArrayList<Float>();
            for (int i = 0; i < 16; i++) {
                arrList_goal.add((float) 6.0);
                
            }
            
            newtimestamp = test.destination.getTimestamp();
            test.testFeapMapCodelet.resetTriggers();
            System.out.println("steps: "+test.testFeapMapCodelet.steps+" Timestamp before: "+TimeStamp.getStringTimeStamp(oldtimestamp, "dd/MM/yyyy HH:mm:ss.SSS"));
            //arrList_test = new CopyOnWriteArrayList<CopyOnWriteArrayList<Float>>();
            arrList_test.add(arrList_i);
            System.out.println("arrList_test: "+arrList_test.size()+" arrList_i: "+arrList_i.size()+" arrList_goal: "+arrList_goal.size());
            test.source.setI(arrList_test);
            //sleep(10);
            test.source2.setI(arrList_test);
            //sleep(10);
            test.source3.setI(arrList_test);
            //sleep(10);
            arrList_weig = new CopyOnWriteArrayList<Float>();
            for (int i = 1; i < 4; i++) {
                arrList_weig.add((float) i);
            }
            test.weights.setI(arrList_weig);
            while(test.testFeapMapCodelet.steps < 4) {
                newtimestamp = test.destination.getTimestamp();
                System.out.println("steps: "+test.testFeapMapCodelet.steps+" Timestamp while waiting: "+TimeStamp.getStringTimeStamp(newtimestamp,"dd/MM/yyyy HH:mm:ss.SSS"));
            }
            System.out.println("steps: "+test.testFeapMapCodelet.steps+" Timestamp after: "+TimeStamp.getStringTimeStamp(newtimestamp,"dd/MM/yyyy HH:mm:ss.SSS"));
            System.out.println("  Inputs 31: "+((List)((List)(test.source.getI())).get(2))+"\n size: "+((List)((List)(test.source.getI())).get(2)).size());
            System.out.println("  Inputs 32: "+((List)((List)(test.source2.getI())).get(2))+"\n size: "+((List)((List)(test.source2.getI())).get(2)).size());
            System.out.println("  Inputs 33: "+((List)((List)(test.source3.getI())).get(2))+"\n size: "+((List)((List)(test.source3.getI())).get(2)).size());
            System.out.println("   weights 3: "+test.weights.getI()+"\n size: "+((List)(test.weights.getI())).size());
            System.out.print("  Output 3: "+ test.destination.getI());
            fulllist = (List) test.destination.getI();
            if (fulllist != null && fulllist.size() > 0) {
                System.out.println("          sizef: "+fulllist.size()+"\n");
                assertEquals(fulllist.size(),16);
                assertEquals(fulllist,arrList_goal);
                
            }
    }
    
    private void sleep(int time) {
        try{ Thread.sleep(time); } catch(Exception e){e.printStackTrace();}
    }
}

