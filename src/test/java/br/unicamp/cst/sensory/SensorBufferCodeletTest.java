
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
 * Test for Codelet implementation of SensorBuffers.  In order to obtain data observation
 * to generate the feature maps for each dimension that will be used to compute 
 * salience, a temporal window of data have to be stored. 
 * @author L. L. Rossi (leolellisr)
 */
public class SensorBufferCodeletTest {

    // This class contains tests covering some core Codelet methods
    
    // This method is used to generate a new Codelet
    SensorBufferCodelet generateSensorBufferCodelet() {

        SensorBufferCodelet testSensorBufferCodelet = new SensorBufferCodelet("test name", "test buffer name", 32) {

        @Override
        public void accessMemoryObjects() {}
   
        
        
        @Override
        public void proc() {
            System.out.println("proc method in SensorBufferCodeletTest ran correctly!");
        }
        @Override
        public void calculateActivation() {}

     
    };
     return(testSensorBufferCodelet);   
    }
}

