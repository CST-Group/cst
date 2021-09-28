/**
 * 
 */
package br.unicamp.cst.bindings.soar;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author gudwin
 *
 */
public class SOARInspectorTest {
	
	@BeforeClass
    public static void beforeAllTestMethods() {
    }

	@AfterClass
    public static void afterAllTestMethods() {
    }
    
    @Test
    public void testSOARInspector() throws InterruptedException {
    	
    	//String soarRulesPath="src/test/java/br/unicamp/cst/bindings/soar/soarRules.soar";
        String soarRulesPath="src/test/resources/mac.soar";
        JSoarCodelet soarCodelet = new TestJSoarCodelet(soarRulesPath);
        SOARInspector si = new SOARInspector(soarCodelet);
        si.setVisible(true);	
        
        Thread.sleep(60000);
        
        si.setVisible(false);	
    }
}
