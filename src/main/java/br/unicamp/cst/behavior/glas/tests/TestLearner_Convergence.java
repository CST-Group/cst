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


import org.junit.Test;

import br.unicamp.cst.behavior.glas.GlasEvent;
import br.unicamp.cst.behavior.glas.GlasSequence;
import br.unicamp.cst.learning.glas.GlasLearner;

public class TestLearner_Convergence {

	static int nNodes = 7; //static int nNodes = 7;
	static int nReRuns=10;
			
	static int nActions = 3;
	static int nStimuli = 7;

	static int[] sample_stimuli =		{1, 4, 3, 5, 2, 3, 4, 6,    1, 4, 3, 5,  2, 3, 4, 6,  2, 4, 3, 5,  1, 5, 4, 6,  2, 3, 3, 6};    
	static int[] expected_actions =	{1, 1, 1, 2, 1, 1, 1, 2,    2, 1, 1, 1,  1, 0, 1, 2,  1, 1, 1, 1,  1, 1, 1, 1,  1, 1, 1, 2};	//Some wrong choices
	static double[]	rewards = 		{1, 1, 1,10, 1, 1, 1,10,   -1, 1, 1,-10, 1, -1, 1,10, 1, 1, 1, 1,  1, 1, 1, 1,  1, 1, 1,-10};

	static int[] known_final_solution={1, 1, 2, 3, 4, 5,    1, 2, 3, 4, 5, 6,   1, 1, 1, 1, 2, 2};


	@Test
	public void test() {
		System.out.println("Testing convergence... ");
		boolean show_gui = false;


		GlasSequence mySequence = new GlasSequence();
		for(int e=0;e<sample_stimuli.length;e++){
			mySequence.addEvent(new GlasEvent(sample_stimuli[e],expected_actions[e],rewards[e]));	
		}

		GlasLearner myLearner = new GlasLearner(nNodes, nStimuli, nActions);
		myLearner.setShow_gui(show_gui);
		myLearner.setnReRuns(nReRuns);
		//		int max_number_reRuns=500;  //int max_number_reRuns=500;
		//		int nParticles = 1000;				//int nParticles = 1000;
		//		
		//		myLearner.setMax_number_reRuns(max_number_reRuns);
		//		myLearner.setnParticles(nParticles);

		myLearner.learnSequence(mySequence);

		int[] best_found_int = myLearner.getBest_found_solution();

		double best_found_fit = myLearner.getBest_found_fit();
		
		double[] best_found_double=new double[best_found_int.length];

		for(int i=0;i<best_found_double.length;i++){
			best_found_double[i]=((double)best_found_int[i]);				
		}

		System.out.println("Best solution found (fit= "+best_found_fit+" ): ");
		System.out.print("[");
		for(int i=0;i<best_found_double.length;i++){
			System.out.print((int)best_found_double[i]+" ");				
		}
		System.out.println("]");
		//		GLASplot glasPlot_merged_solution = new GLASplot(best_found_double);
		//		glasPlot_merged_solution.setPlotTitle("best_found");
		//		glasPlot_merged_solution.plot();

		//		System.out.println(myLearner.getBest_found_fit());
		//		assertTrue(myLearner.getBest_found_fit()>=50.1428);
		////		50.1428

		if(show_gui){
			while(true){
				try {
					Thread.currentThread();
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}


	}

}
