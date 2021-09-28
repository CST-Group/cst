/**
 * 
 */
package br.unicamp.cst.util;

import org.junit.Test;

import br.unicamp.cst.representation.owrl.AbstractObject;
import br.unicamp.cst.representation.owrl.Property;
import br.unicamp.cst.representation.owrl.QualityDimension;

/**
 * @author gudwin
 *
 */
public class ObjectViewerTest {
	
	@Test
	public void testObjectViewer() throws InterruptedException {
        AbstractObject robot = new AbstractObject("Robot");
        AbstractObject sensor = new AbstractObject("Sensor");
        Property position = new Property("Position");
        position.addQualityDimension(new QualityDimension("x",0.5));
        position.addQualityDimension(new QualityDimension("y",0.6));
        sensor.addProperty(position);
        robot.addCompositePart(sensor);
        AbstractObject actuator = new AbstractObject("Actuator");
        actuator.addProperty(new Property("velocity",new QualityDimension("intensity",-0.12)));
        robot.addCompositePart(actuator);
        robot.addProperty(new Property("Model",new QualityDimension("Serial#","1234XDr56")));   
        ObjectViewer ov = new ObjectViewer("Teste");
        ov.setVisible(true);
        System.out.println("Teste:");
        ov.updateTree(robot);
        
        Thread.sleep(1000);
        
        ov.setVisible(false);
	}

}
