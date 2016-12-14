/*******************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     K. Raizer, A. L. O. Paraense, R. R. Gudwin - initial API and implementation
 ******************************************************************************/

package br.unicamp.cst.behavior.glas.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import br.unicamp.cst.behavior.glas.GlasEvent;
import br.unicamp.cst.behavior.glas.GlasSequence;

/**
 * @author Klaus Raizer
 *
 */
public class TestGlasSequence {

	@Test
	public void testAddingEventsToSequence() {
		
		GlasSequence mySequence = new GlasSequence();
		
		int stimulus0=0;
		int action0 = 0;
		double reward0 = 0.5;
		
		

		GlasEvent myEvent = new GlasEvent(stimulus0,action0,reward0);
		
		
		
		mySequence.addEvent(myEvent);
		
		int stimulus1=1;
		int action1 = 2;
		double reward1 = -0.5;
		
		myEvent = new GlasEvent(stimulus1,action1,reward1);
		
		mySequence.addEvent(myEvent);

		
		ArrayList<GlasEvent> newSequence= mySequence.getEvents();

		GlasEvent event0 = newSequence.get(0);
		GlasEvent event1 = newSequence.get(1);
		
		
		assertTrue(event0.getStimulus()==stimulus0);
		assertTrue(event0.getAction()==action0);
		assertTrue(event0.getReward()==reward0);
		
		assertTrue(event1.getStimulus()==stimulus1);
		assertTrue(event1.getAction()==action1);
		assertTrue(event1.getReward()==reward1);
		
	}
	
	

}
