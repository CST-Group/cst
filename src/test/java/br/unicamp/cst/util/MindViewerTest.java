/**
 * 
 */
package br.unicamp.cst.util;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;

/**
 * @author gudwin
 *
 */
public class MindViewerTest {
	
	@BeforeClass
    public static void beforeAllTestMethods() {
    }

	@AfterClass
    public static void afterAllTestMethods() {
    }
    
    @Test
    public void testMindViewer() throws InterruptedException {
    	
    	Mind m = new Mind();
        MemoryObject m1 = m.createMemoryObject("M1", 0.12);
        MemoryObject m2 = m.createMemoryObject("M2", 0.32);
        MemoryObject m3 = m.createMemoryObject("M3", 0.44);
        MemoryObject m4 = m.createMemoryObject("M4", 0.52);
        MemoryObject m5 = m.createMemoryObject("M5", 0.12);
        MemoryContainer m6 = m.createMemoryContainer("C1");
        MemoryContainer m7 = m.createMemoryContainer("C2");
        m7.setI(0.55, 0.23);
        m6.setI(0.33, 0.22);
        m6.setI(0.12, 0.13);
        m6.setI(m7);
        Codelet c = new TestCodelet("Codelet 1");
        c.addInput(m1);
        c.addInput(m2);
        c.addOutput(m3);
        c.addOutput(m4);
        c.addBroadcast(m5);
        m.insertCodelet(c);
        Codelet c2 = new TestCodelet("Codelet 2");
        c2.addInput(m4);
        c2.addInput(m5);
        c2.addOutput(m6);
        c2.addOutput(m3);
        c2.addBroadcast(m5);
        m.insertCodelet(c2);
        MindViewer ov = new MindViewer(m, "Mind", new ArrayList<>());
        ov.setVisible(true);
        System.out.println("MindViewerTest: running for 60s ");
        // ov.updateTree(m);  
        
        Thread.sleep(1000);
        
        ov.setVisible(false);
    }
}
