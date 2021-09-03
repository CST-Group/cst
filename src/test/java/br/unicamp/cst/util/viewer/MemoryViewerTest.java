/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.util.viewer;

import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.util.TestComplexMemoryObjectInfo;
import java.awt.AWTException;
import java.awt.Robot;
import java.util.Random;
import org.junit.Test;

/**
 *
 * @author rgudwin
 */
public class MemoryViewerTest {
    
    @Test
    public void testMemoryInspector() {

        Robot rob = startRobot();
        rob.delay(1000);         //delay to let the application load
        rob.setAutoDelay(20); 
        TestComplexMemoryObjectInfo m = new TestComplexMemoryObjectInfo();
        m.complextest = new TestComplexMemoryObjectInfo();
        for (int i=0;i<3;i++)
            m.complextestarray[i] = new TestComplexMemoryObjectInfo();
        MemoryObject mo = new MemoryObject();
        mo.setType("TestObject");
        mo.setI(m);
        MemoryViewer mv = new MemoryViewer(mo);
        mv.setVisible(true);
        rob.delay(5000);
        float[] m2 = new float[256];
        Random n = new Random();
        for (int i=0;i<256;i++)
            m2[i] = n.nextFloat();
        mo.setI(m2);
        MemoryViewer mv2 = new MemoryViewer(mo);
        mv2.setVisible(true);
        rob.delay(5000);
        mv.setVisible(false);
        mv2.setVisible(false);
    }    
    
    Robot startRobot() {
        Robot rob=null;
        try
        {
            rob = new Robot();
        }
        catch (AWTException ex)
        {
            System.err.println("Can't start Robot: " + ex);
            System.exit(0);
        }
        return(rob);
    }
    
    
}
