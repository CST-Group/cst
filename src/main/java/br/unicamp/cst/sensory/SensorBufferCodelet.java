/*******************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     K. Raizer, A. L. O. Paraense, R. R. Gudwin - initial API and implementation
 ******************************************************************************/

package br.unicamp.cst.sensory;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Codelet implementation of SensorBuffers.  In order to obtain data observation
 * to generate the feature maps for each dimension that will be used to compute 
 * salience, a temporal window of data have to be stored. 
 * @author L. M. Berto
 * @author L. L. Rossi (leolellisr)
 * @see Codelet
 * @see MemoryObject
 * @see ByteArrayInputStream
 * @see ByteArrayOutputStream
 * @see ObjectInputStream
 * @see ObjectOutputStream
 */
public class SensorBufferCodelet extends Codelet {
    private MemoryObject sensor_input;
    private MemoryObject buffer_output;
    private String sensorName;
    private String bufferName;
    private int maxcapacity;
    
    /**
     * init SensorBufferCodelet
     * @param sensorName
     *          input sensor name
     * @param bufferName
     *          output SensorBuffer name
     * @param maxcpcty 
     *          output SensorBuffer max. capacity
     */
    public SensorBufferCodelet(String sensorName, String bufferName, int maxcpcty) {
        super();
        this.bufferName = bufferName;
        this.sensorName = sensorName;
        maxcapacity = maxcpcty;
    }

    @Override
    /**
     * access MemoryObjects: input sensor 
     * define output: bufferName
     */
    public void accessMemoryObjects() {
        sensor_input = (MemoryObject) this.getInput(sensorName);
        buffer_output = (MemoryObject) this.getOutput(bufferName);
    }

    @Override
    public void calculateActivation() {
        // we don't need to calculate activation here
    }

    @Override
    /**
     * proc: codelet logical process
     * gets a serialized sensor_input queue of size maxcapacity
     * 
     * Java serialization:
     * https://docs.oracle.com/javase/8/docs/technotes/guides/serialization/index.html
     */
    public void proc() {
        try {
            Thread.sleep(10);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
                
        List buffer_list = (List) buffer_output.getI();
        
        if(buffer_list.size() == maxcapacity){
            buffer_list.remove(0);
        }
        
        MemoryObject cloned_data = null;
        
        ObjectOutputStream oos;
        ObjectInputStream ois;
        try
        {
           ByteArrayOutputStream bos = 
                 new ByteArrayOutputStream(); // A
           oos = new ObjectOutputStream(bos); // B
           // serialize and pass the object
           oos.writeObject(sensor_input);   // C
           oos.flush();               // D
           ByteArrayInputStream bin = 
              new ByteArrayInputStream(bos.toByteArray()); // E
           ois = new ObjectInputStream(bin);                  // F
           // return the new object
           cloned_data = (MemoryObject) ois.readObject(); // G
           
           oos.close();
           ois.close();
        }
        catch(IOException | ClassNotFoundException e)
        {
           System.out.println("Exception in ObjectCloner = " + e);
           e.printStackTrace();
        }
        
        buffer_list.add(cloned_data);
        buffer_output.setI(buffer_list); // This is necessary to set a new TimeStamp for the MemoryObject
    }
        
}
