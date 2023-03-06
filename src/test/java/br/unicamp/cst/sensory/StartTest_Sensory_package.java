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

/**
 *
  * @author L. L. Rossi (leolellisr)
 */
public class StartTest_Sensory_package {
    
    public static void main(String[] args) {
        SensorBufferCodelet test_sensorbuffer = new SensorBufferCodeletTest().generateSensorBufferCodelet();
        FeatMapCodelet test_featmap = new FeapMapCodeletTest().generateFeatMapCodelet();
        CombFeatMapCodelet test_cfm = new CombFeapMapCodeletTest().generateCombFeatMapCodelet();
    
    }
   
}
