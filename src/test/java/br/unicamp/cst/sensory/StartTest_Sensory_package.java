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
import org.junit.Test;

/**
 *
  * @author L. L. Rossi (leolellisr)
 */
public class StartTest_Sensory_package {
    
    @Test
    public void testSensoryCodelets() {
        //SensorBufferCodelet test_sensorbuffer = new SensorBufferCodeletTest().generateSensorBufferCodelet();
        //test_sensorbuffer.accessMemoryObjects();
        //test_sensorbuffer.calculateActivation();
        //test_sensorbuffer.proc();
        FeatMapCodelet test_featmap = new FeapMapCodeletTest().generateFeatMapCodelet();
        test_featmap.accessMemoryObjects();
        test_featmap.calculateActivation();
        test_featmap.proc();
        CombFeatMapCodelet test_cfm = new CombFeapMapCodeletTest().generateCombFeatMapCodelet();
        test_cfm.accessMemoryObjects();
        test_cfm.calculateActivation();
        test_cfm.calculateCombFeatMap();
        test_cfm.proc();
    }
   
}
