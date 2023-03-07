
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

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertEquals;



/**
 * Test for Codelet implementation of SensorBuffers.  In order to obtain data observation
 * to generate the feature maps for each dimension that will be used to compute 
 * salience, a temporal window of data have to be stored. 
 * @author L. L. Rossi (leolellisr)
 */
public class SensorBufferCodeletTest {
    
    MemoryObject source;
    MemoryObject destination;
    
    public final void printList(List l) {
        for (int i=0;i<l.size();i++) {
            Memory mo = (Memory) l.get(i); 
            System.out.print(mo.getI()+" ");
        }
        System.out.print("\n");
    }
    
    public SensorBufferCodeletTest() {
        Mind testMind = new Mind();
        source = testMind.createMemoryObject("SOURCE");
        //source.setI(0);
        destination = testMind.createMemoryObject("DESTINATION");
        destination.setI(new ArrayList<Integer>());
        SensorBufferCodelet testSensorBufferCodelet = new SensorBufferCodelet("SOURCE", "DESTINATION", 32);
        testMind.insertCodelet(testSensorBufferCodelet);
        testSensorBufferCodelet.addInput(source);
        testSensorBufferCodelet.addOutput(destination);
        testSensorBufferCodelet.setIsMemoryObserver(true);
	source.addMemoryObserver(testSensorBufferCodelet);
        testMind.start();
        
        
        //List fulllist = (List)destination.getI();
        
        
    }
    
    @Test
    public void testSensoryBufferCodelet() {
        SensorBufferCodeletTest test = new SensorBufferCodeletTest();
        for (int i=0;i<64;i++) {
            System.out.println("Testing ... "+i);
            test.source.setI(i);
            System.out.println("  Input: "+test.source.getI());
            System.out.print("  Output: ");
            List fulllist = (List)test.destination.getI();
            if (fulllist != null && fulllist.size() > 0) {
                printList(fulllist);
                System.out.println("          size: "+((List)(test.destination.getI())).size()+"\n");
                MemoryObject first = (MemoryObject)fulllist.get(0);
                int firsti = (int)(first.getI());
                MemoryObject last = (MemoryObject)fulllist.get(fulllist.size()-1);
                int lasti = (int)(last.getI());
                assertEquals(lasti-firsti,fulllist.size()-1);
            }    
        }
    }

    // This class contains tests covering some core Codelet methods
    
    // This method is used to generate a new Codelet
//    SensorBufferCodelet generateSensorBufferCodelet() {
//
//        SensorBufferCodelet testSensorBufferCodelet = new SensorBufferCodelet("test name", "test buffer name", 32) {
//
//        @Override
//        public void accessMemoryObjects() {}
//   
//        
//        
//        @Override
//        public void proc() {
//            System.out.println("proc method in SensorBufferCodeletTest ran correctly!");
//        }
//        @Override
//        public void calculateActivation() {}
//
//     
//    };
//     return(testSensorBufferCodelet);   
//    }
}

