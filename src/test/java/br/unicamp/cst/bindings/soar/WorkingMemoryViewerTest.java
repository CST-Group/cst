/**
 * 
 */
package br.unicamp.cst.bindings.soar;

import javax.swing.JFileChooser;

import org.junit.Test;

/**
 * @author rgudwin
 *
 */
public class WorkingMemoryViewerTest {
	
	@Test
	public void testWorkingMemoryViewer() {
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
            java.util.logging.Logger.getLogger(WorkingMemoryViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(WorkingMemoryViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(WorkingMemoryViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(WorkingMemoryViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
//        try {
//           NativeUtils.loadFileFromJar("/soar-rules.soar");
//        } catch(Exception e) {
//            e.printStackTrace();
//        }   
        String soarRulesPath = "src/test/java/br/unicamp/cst/bindings/soar/soarRules.soar";
//        try {
//		JFileChooser chooser = new JFileChooser();
//		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
//		  soarRulesPath = chooser.getSelectedFile().getCanonicalPath();
//		  //System.out.println("You chose to open this file: "+soarRulesPath);
//                }
//            } catch (Exception e) { e.printStackTrace(); }
        JSoarCodelet soarCodelet = new TestJSoarCodelet(soarRulesPath);
        WorkingMemoryViewer ov = new WorkingMemoryViewer("Teste",soarCodelet);
        ov.setVisible(true);
        ov.updateTree(soarCodelet.getJsoar().getStates());
	}
}
