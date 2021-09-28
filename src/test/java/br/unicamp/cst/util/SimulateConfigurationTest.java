/**
 * 
 */
package br.unicamp.cst.util;

import org.junit.Test;

/**
 * @author suelen
 *
 */
public class SimulateConfigurationTest {
	
	@Test
	public void testSimulateConfiguration() {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            	SimulateConfiguration simulateConfiguration = new SimulateConfiguration();
            	simulateConfiguration.setVisible(true);
            	
            	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            	simulateConfiguration.setVisible(false);
            }
        });
	}

}
