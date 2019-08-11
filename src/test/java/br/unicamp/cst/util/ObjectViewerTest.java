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
	public void testObjectViewer() {
		/* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ObjectViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ObjectViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ObjectViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ObjectViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
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
	}

}
