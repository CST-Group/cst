/**
 * 
 */
package br.unicamp.cst.util;

import org.junit.Test;

/**
 * @author gudwin
 *
 */
public class DialogFactoryTest {
	
	@Test
	public void testDialogFactory() {
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DialogFactory dialog = new DialogFactory(new javax.swing.JFrame(), false);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
                
                try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                                           
                dialog.setVisible(false);
            }
        });
	}

}
