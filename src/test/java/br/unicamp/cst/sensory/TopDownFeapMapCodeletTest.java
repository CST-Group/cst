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
package br.unicamp.cst.sensory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.support.TimeStamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
  * @author L. L. Rossi (leolellisr)
 */
public class TopDownFeapMapCodeletTest {

    // This class contains tests covering some core Codelet methods
    
     public MemoryObject source;
    public MemoryObject destination;
    
    public TopDownFeapMapCodeletTest() {
        Mind testMind = new Mind();
        source = testMind.createMemoryObject("SOURCE");
        //source.setI(0);
        destination = testMind.createMemoryObject("DESTINATION");
        destination.setI(new CopyOnWriteArrayList<Float>());
        CopyOnWriteArrayList<String> FMnames = new CopyOnWriteArrayList<>();
        FMnames.add("SOURCE");
        FMnames.add("SOURCE2");
        FMnames.add("SOURCE3");
        CopyOnWriteArrayList<Float> goal = new CopyOnWriteArrayList<>();
        goal.add((float) 255);
        goal.add((float) 0);
        goal.add((float) 0);
        TopDownFM testFeapMapCodelet = new TopDownFM(3, FMnames, "DESTINATION", 100, 16, goal, 255, 16, 4, 3, false);
        testMind.insertCodelet(testFeapMapCodelet);
        testFeapMapCodelet.addInput(source);
        testFeapMapCodelet.addOutput(destination);
        testFeapMapCodelet.setIsMemoryObserver(true);
	source.addMemoryObserver(testFeapMapCodelet);
        testMind.start();
        
        
        //List fulllist = (List)destination.getI();
        
        
    }
    
    @Test
    public void testTopDownFeapMapCodelet() {
        TopDownFeapMapCodeletTest test = new TopDownFeapMapCodeletTest();
        //for (int i=0;i<64;i++) {
            System.out.println("Testing ... ");
            long oldtimestamp = test.destination.getTimestamp();
            System.out.println("Timestamp before: "+TimeStamp.getStringTimeStamp(oldtimestamp, "dd/MM/yyyy HH:mm:ss.SSS"));
            
            
            ArrayList<MemoryObject> mo_arrList = new ArrayList<MemoryObject>();
            MemoryObject source_arrList = new MemoryObject();
            
            // Test 1
            ArrayList<Float> int_arrList = new ArrayList<Float>(256);
            for (int i = 0; i < 256*3; i++) {
                int_arrList.add((float) 255);
            }
            ArrayList<Float> ass_arrList = new ArrayList<Float>(16);
            for (int i = 0; i < 16; i++) {
                ass_arrList.add((float) 0);
            }
            
            long newtimestamp = test.destination.getTimestamp();
            source_arrList.setI(int_arrList);
            mo_arrList.add(source_arrList);
            test.source.setI(mo_arrList);
            
            while(newtimestamp == oldtimestamp) {
                newtimestamp = test.destination.getTimestamp();
                System.out.println("Timestamp after: "+TimeStamp.getStringTimeStamp(newtimestamp,"dd/MM/yyyy HH:mm:ss.SSS"));
            }
            System.out.println("\n   Input 1: "+test.source.getI());
            
            
            System.out.print("\n   Output 1: "+ test.destination.getI());
            List fulllist = (List) test.destination.getI();
            if (fulllist != null && fulllist.size() > 0) {
                //printList(fulllist);
                System.out.println("          sizef: "+((List)(test.destination.getI())).size()+"\n");
                
                
                assertEquals(fulllist.size(),16);
                assertEquals(fulllist,ass_arrList);
                
            }
            
            // Test 2
            oldtimestamp = test.destination.getTimestamp();
            System.out.println("Timestamp before: "+TimeStamp.getStringTimeStamp(oldtimestamp, "dd/MM/yyyy HH:mm:ss.SSS"));
            
            int_arrList = new ArrayList<Float>(256);
            for (int i = 0; i < (int)256/2; i++) {
                int_arrList.add((float) 255);
            }
            for (int i = (int)256/2; i < 256; i++) {
                int_arrList.add((float)0);
            }
            ass_arrList = new ArrayList<Float>(16);
            for (int i = 0; i < 3; i++) {
                ass_arrList.add((float) 0.25);
            }
            for (int i = 0; i < 2; i++) {
                ass_arrList.add((float) 0.5);
            }
            for (int i = 0; i < 3; i++) {
                ass_arrList.add((float) 0.25);
            }
            for (int i = 0; i < 8; i++) {
                ass_arrList.add((float) 0.0);
            }
            
                    
            source_arrList.setI(int_arrList);
            mo_arrList.add(source_arrList);
            newtimestamp = test.destination.getTimestamp();
            test.source.setI(mo_arrList);
            while(newtimestamp == oldtimestamp) {
                newtimestamp = test.destination.getTimestamp();
                System.out.println("Timestamp after: "+TimeStamp.getStringTimeStamp(newtimestamp,"dd/MM/yyyy HH:mm:ss.SSS"));
            }
            
            System.out.println("\n \n   Input 2: "+test.source.getI());
            
            
            System.out.print("\n   Output 2: "+ test.destination.getI());
            fulllist = (List) test.destination.getI();
            if (fulllist != null && fulllist.size() > 0) {
                //printList(fulllist);
                System.out.println("          sizef: "+((List)(test.destination.getI())).size()+"\n");
                
                
                
                
                assertEquals(fulllist.size(),16);
                assertEquals(fulllist,ass_arrList);
                
            }
            
            oldtimestamp = test.destination.getTimestamp();
            System.out.println("Timestamp before: "+TimeStamp.getStringTimeStamp(oldtimestamp, "dd/MM/yyyy HH:mm:ss.SSS"));
            
            // Test 3
            int_arrList = new ArrayList<Float>(256);
            for (int i = 0; i < (int)256/2; i++) {
                int_arrList.add((float) 0);
            }
            for (int i = (int)256/2; i < 256; i++) {
                int_arrList.add((float) 255);
            }
            ass_arrList = new ArrayList<Float>(16);
            for (int i = 0; i < 3; i++) {
                ass_arrList.add((float) 0.25);
            }
            for (int i = 0; i < 1; i++) {
                ass_arrList.add((float) 0.5);
            }
            
            for (int i = 0; i < 12; i++) {
                ass_arrList.add((float) 0);
            }
            
            newtimestamp = test.destination.getTimestamp();
            source_arrList.setI(int_arrList);
            mo_arrList.add(source_arrList);
            test.source.setI(mo_arrList);
            
            while(newtimestamp == oldtimestamp) {
                newtimestamp = test.destination.getTimestamp();
                System.out.println("Timestamp after: "+TimeStamp.getStringTimeStamp(newtimestamp,"dd/MM/yyyy HH:mm:ss.SSS"));
            }
            System.out.println("\n \n   Input 3: "+test.source.getI());
            
            
            System.out.print("\n   Output 3: "+ test.destination.getI());
            fulllist = (List) test.destination.getI();
            if (fulllist != null && fulllist.size() > 0) {
                //printList(fulllist);
                System.out.println("          sizef: "+((List)(test.destination.getI())).size()+"\n");
                
                
                
                assertEquals(fulllist.size(),16);
                assertEquals(fulllist,ass_arrList);
                
            }
        //}
    }
}
