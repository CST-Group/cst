/**
 * 
 */
package br.unicamp.cst.util;

import org.junit.Test;

/**
 * @author suelen
 *
 */
public class NameGeneratorTest {
	
	@Test
	public void testNameGenerator() {
        int cont = 0;

        while (cont < 10) {
            NameGenerator ng = new NameGenerator();
            System.out.println(" >> " + ng.generateWord());
            cont++;
        }
	}

}
