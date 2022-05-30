/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
