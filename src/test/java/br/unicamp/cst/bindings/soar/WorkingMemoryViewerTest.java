/**
 * 
 */
package br.unicamp.cst.bindings.soar;

import org.junit.Test;

/**
 * @author rgudwin
 *
 */
public class WorkingMemoryViewerTest {
	
	@Test
	public void testWorkingMemoryViewer() throws InterruptedException {
        String soarRulesPath = "src/test/java/br/unicamp/cst/bindings/soar/soarRules.soar";
        JSoarCodelet soarCodelet = new TestJSoarCodelet(soarRulesPath);
        WorkingMemoryViewer ov = new WorkingMemoryViewer("Teste",soarCodelet);
        ov.setVisible(true);
        ov.updateTree(soarCodelet.getJsoar().getStates());
        
        Thread.sleep(1000);
        
        ov.setVisible(false);
	}
}
