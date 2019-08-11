/**
 * 
 */
package br.unicamp.cst.util;

import org.junit.Test;

/**
 * @author gudwin
 *
 */
public class TimeStampTest {

	@Test
	public void testTimeStamp() {
		TimeStamp.setStartTime();
		try {
			Thread.sleep(3358);
		} catch (Exception e) {}
		System.out.println(TimeStamp.getDelaySinceStart());
	}
}
