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


import org.junit.Test;

import br.unicamp.cst.behavior.glas.GlasEvent;
import br.unicamp.cst.behavior.glas.Individual;
import br.unicamp.cst.behavior.glas.GlasSequence;

public class TestGlassFitness {
	static int nNodes = 7;
	static int nActions = 3;
	static int nStimuli = 7;

	static int[] sample_stimuli =	{1, 4, 3, 5, 2, 3, 4, 6,    1, 4, 3, 5,  2, 3, 4, 6,  2, 4, 3, 5,  1, 5, 4, 6,  2, 3, 3, 6};    
	static int[] expected_actions =	{1, 1, 1, 2, 1, 1, 1, 2,    2, 1, 1, 1,  1, 0, 1, 2,  1, 1, 1, 1,  1, 1, 1, 1,  1, 1, 1, 2};	//Some wrong choices
	static double[]	rewards = 		{1, 1, 1,10, 1, 1, 1,10,   -1, 1, 1,-10, 1, -1, 1,10, 1, 1, 1, 1,  1, 1, 1, 1,  1, 1, 1,-10};

	static double expected_reward_for_good_solution=51; //without heuristics
//	static double expected_reward_for_good_solution=51.75; //with heuristics
	
	
	@Test
	public void testGoodSolution_known() {

		int[] good_solution_phenotype={0, 1, 1, 2, 3, 4, 5,   0, 1, 2, 3, 4, 5, 6,     0, 1, 1, 1, 1, 2, 2};
		
		
		
		GlasSequence mySequence = new GlasSequence();
		for(int e=0;e<sample_stimuli.length;e++){
			mySequence.addEvent(new GlasEvent(sample_stimuli[e],expected_actions[e],rewards[e]));	
		}
		
		
		Individual indi =new Individual(nNodes, nStimuli, nActions);

		indi.setChromossome(good_solution_phenotype);


		double fit=indi.getFitness(mySequence);
		System.out.println("testGoodSolution_known fit: "+fit);


		assertTrue(expected_reward_for_good_solution<=fit);
	}

	
	@Test
	public void testGoodSolution18() {

		int[] good_solution_phenotype={0, 1, 1, 3, 2, 5, 4,   0, 2, 1, 3, 4, 6, 5,     0, 1, 1, 1, 1, 2, 2};
		
		
		
		GlasSequence mySequence = new GlasSequence();
		for(int e=0;e<sample_stimuli.length;e++){
			mySequence.addEvent(new GlasEvent(sample_stimuli[e],expected_actions[e],rewards[e]));	
		}
		
		
		Individual indi =new Individual(nNodes, nStimuli, nActions);

		indi.setChromossome(good_solution_phenotype);


		double fit=indi.getFitness(mySequence);
		System.out.println("testGoodSolution18 fit: "+fit);


		assertTrue(expected_reward_for_good_solution<=fit);
	}


	@Test
	public void testBadSolution28() {

		int[] bad_solution_phenotype={0, 1, 2, 3, 4, 5, 2,   0, 1, 4, 5, 2, 6, 3,     0, 1, 1, 2, 1, 2, 0};
		
		
		
		GlasSequence mySequence = new GlasSequence();
		for(int e=0;e<sample_stimuli.length;e++){
			mySequence.addEvent(new GlasEvent(sample_stimuli[e],expected_actions[e],rewards[e]));	
		}
		
		
		Individual indi =new Individual(nNodes, nStimuli, nActions);

		indi.setChromossome(bad_solution_phenotype);


		double fit=indi.getFitness(mySequence);
		System.out.println("testBadSolution28 fit: "+fit);


		assertTrue(expected_reward_for_good_solution>fit);
	}

	@Test
	public void testBadSolution0() {

		int[] bad_solution_phenotype={0, 1, 2, 2, 4, 5, 6,   0, 1, 3, 4, 5, 2, 6,     0, 1, 1, 1, 2, 1, 2};
		
		
		
		GlasSequence mySequence = new GlasSequence();
		for(int e=0;e<sample_stimuli.length;e++){
			mySequence.addEvent(new GlasEvent(sample_stimuli[e],expected_actions[e],rewards[e]));	
		}
		
		
		Individual indi =new Individual(nNodes, nStimuli, nActions);

		indi.setChromossome(bad_solution_phenotype);


		double fit=indi.getFitness(mySequence);
		System.out.println("testBadSolution0 fit: "+fit);


		assertTrue(expected_reward_for_good_solution>fit);
	}

	@Test
	public void testBadSolution24() {

		int[] bad_solution_phenotype={0, 1, 1, 3, 3, 5, 3,   0, 1, 3, 2, 4, 6, 5,     0, 1, 1, 0, 1, 2, 2};
		
		
		
		GlasSequence mySequence = new GlasSequence();
		for(int e=0;e<sample_stimuli.length;e++){
			mySequence.addEvent(new GlasEvent(sample_stimuli[e],expected_actions[e],rewards[e]));	
		}
		
		
		Individual indi =new Individual(nNodes, nStimuli, nActions);

		indi.setChromossome(bad_solution_phenotype);


		double fit=indi.getFitness(mySequence);
		System.out.println("testBadSolution24 fit: "+fit);


		assertTrue(expected_reward_for_good_solution>fit);
	}
	
	


	@Test
	public void testExp1N7S3(){
		
		int[] bad_solution_phenotype={0, 1, 1, 2, 3, 5, 4, 0, 1, 2, 3, 4, 6, 5, 0, 1, 1, 1, 1, 2, 2};
		
		
		GlasSequence mySequence = new GlasSequence();
		for(int e=0;e<sample_stimuli.length;e++){
			mySequence.addEvent(new GlasEvent(sample_stimuli[e],expected_actions[e],rewards[e]));	
		}
		
		
		Individual indi =new Individual(nNodes, nStimuli, nActions);

		indi.setChromossome(bad_solution_phenotype);


		double fit=indi.getFitness(mySequence);
		System.out.println("testGoodSolution Exp1N7S3  fit: "+fit);
		
		
		assertTrue(expected_reward_for_good_solution<=fit);	
		
	}

	@Test
	public void testExp1N7S2(){
		
		int[] bad_solution_phenotype={0, 1, 2, 2, 3, 4, 2, 0, 1, 6, 4, 2, 5, 3, 0, 1, 2, 1, 1, 2, 1};
		
		
		GlasSequence mySequence = new GlasSequence();
		for(int e=0;e<sample_stimuli.length;e++){
			mySequence.addEvent(new GlasEvent(sample_stimuli[e],expected_actions[e],rewards[e]));	
		}
		
		
		Individual indi =new Individual(nNodes, nStimuli, nActions);

		indi.setChromossome(bad_solution_phenotype);


		double fit=indi.getFitness(mySequence);
		System.out.println("testBadSolution Exp1N7S2  fit: "+fit);
		
		
		assertTrue(expected_reward_for_good_solution>fit);	
		
	}
	
	
	@Test
	public void testSimilar(){
		int[] s0={0, 1, 2, 3, 1, 5, 6,   0, 1, 3, 5, 2, 4, 6,    0, 1, 1, 2, 1, 1, 2};		
		int[] s3={0, 1, 1, 2, 3, 4, 5,   0, 2, 1, 4, 3, 6, 5,    0, 1, 1, 1, 1, 2, 2};
		
		GlasSequence mySequence = new GlasSequence();
		for(int e=0;e<sample_stimuli.length;e++){
			mySequence.addEvent(new GlasEvent(sample_stimuli[e],expected_actions[e],rewards[e]));	
		}
		
		
		Individual indi0 =new Individual(nNodes, nStimuli, nActions);
		indi0.setChromossome(s0);
		double fit0=indi0.getFitness(mySequence);
		System.out.println("s0 fit: "+fit0);
		
		Individual indi3 =new Individual(nNodes, nStimuli, nActions);
		indi3.setChromossome(s3);
		double fit3=indi3.getFitness(mySequence);
		System.out.println("s3 fit: "+fit3);
		
		assertTrue(fit0==fit3);
	}
	
	
	@Test
	public void testN8(){
		
		

		
		int[] s0={0,1,1,3,4,1,6,2,   0,6,1,3,5,2,4,6,   0,1,1,1,2,1,1,2};		
		int[] s2={0,1,2,1,1,4,3,6,   0,1,3,2,1,4,5,6,   0,1,1,1,2,1,2,2};
		
		GlasSequence mySequence = new GlasSequence();
		for(int e=0;e<sample_stimuli.length;e++){
			mySequence.addEvent(new GlasEvent(sample_stimuli[e],expected_actions[e],rewards[e]));	
		}
		
		
		Individual indi0 =new Individual(nNodes, nStimuli, nActions);
		indi0.setChromossome(s0);
		double fit0=indi0.getFitness(mySequence);
		System.out.println("s0 fit: "+fit0);
		
		Individual indi2 =new Individual(nNodes, nStimuli, nActions);
		indi2.setChromossome(s2);
		double fit2=indi2.getFitness(mySequence);
		System.out.println("s2 fit: "+fit2);
		
		assertTrue(fit0<fit2);
		
	}
	
//	@Test 
//	public void fitWithPrune(){
//	
//		int[] sample_stimuli_2 =	{1, 4, 3, 5, 2, 3, 4, 6,    1, 4, 3, 5,  2, 3, 4, 6,  2, 4, 3, 5,  1, 5, 4, 6,  2, 3, 3, 6};
//		int[] expected_actions_2 =	{1, 1, 1, 2, 1, 1, 1, 2,    2, 1, 1, 1,  1, 0, 1, 2,  1, 1, 1, 1,  1, 1, 1, 1,  1, 1, 1, 2};	//Some wrong choices
//		double[]	rewards_2 = 	{1, 1, 1,10, 1, 1, 1,10,   -1, 1, 1,-10, 1, -1, 1,10, 1, 1, 1, 1,  1, 1, 1, 1,  1, 1, 1,-10};
//		
//		Sequence mySequence = new Sequence();
//		for(int e=0;e<sample_stimuli.length;e++){
//			mySequence.addEvent(new Event(sample_stimuli[e],expected_actions[e],rewards[e]));	
//		}
//		
//	int[] xp2_n7_s2= {0,1,1,2,4,3,6,   0,2,1,4,6,3,5,   0,1,1,1,2,1,2};
//
//	int[] xp2_n8_s5= {0,1,2,3,1,3,5,7,   0,2,4,3,1,6,3,5,   0,1,1,1,1,2,1,2};
//
//	int[] xp2_n9_s8= {0,1,1,2,4,4,3,4,7,   0,2,1,4,4,3,3,6,5,   0,1,1,1,2,1,1,2,2};//fit=51.821429	
//	int[] xp2_n9_s17={0,1,2,3,1,5,6,3,6,   0,2,4,3,1,3,5,6,2,   0,1,1,1,1,1,2,2,0};//fit=51.821429
//	
//	
//	Individual indi_xp2_n7_s2 =new Individual(7, nStimuli, nActions);
//	indi_xp2_n7_s2.setChromossome(xp2_n7_s2);
//	double fit_xp2_n7_s2=indi_xp2_n7_s2.getFitness(mySequence);
//	System.out.println("xp2_n7_s2 fit: "+fit_xp2_n7_s2);
//	
//	Individual indi_xp2_n8_s5 =new Individual(8, nStimuli, nActions);
//	indi_xp2_n8_s5.setChromossome(xp2_n8_s5);
//	double fit_xp2_n8_s5=indi_xp2_n8_s5.getFitness(mySequence);
//	System.out.println("xp2_n8_s5 fit: "+fit_xp2_n8_s5);
//	
//	Individual indi_xp2_n9_s8 =new Individual(9, nStimuli, nActions);
//	indi_xp2_n9_s8.setChromossome(xp2_n9_s8);
//	double fit_xp2_n9_s8=indi_xp2_n9_s8.getFitness(mySequence);
//	System.out.println("xp2_n9_s8 fit: "+fit_xp2_n9_s8);
//	
//	Individual indi_xp2_n9_s17 =new Individual(9, nStimuli, nActions);
//	indi_xp2_n9_s17.setChromossome(xp2_n9_s17);
//	double fit_xp2_n9_s17=indi_xp2_n9_s17.getFitness(mySequence);
//	System.out.println("xp2_n9_s17 fit: "+fit_xp2_n9_s17);
////	
////	
////	
////	// --- Pruning ---
//////	
//////	Individual pruned_indi_xp2_n8_s5 =new Individual(nNodes, nStimuli, nActions);
//////	indi_xp2_n8_s5.setChromossome(xp2_n8_s5);
//////	double fit_xp2_n8_s5=indi_xp2_n8_s5.getFitness(mySequence);
//////	System.out.println("xp2_n8_s5 fit: "+fit_xp2_n8_s5);
//////	
////		
////	
////	int[] temp_individual = xp2_n8_s5;
////	
////	nNodes=temp_individual.length/3;
////		
////	StateMachine my_sm = new StateMachine(temp_individual);	
////	my_sm.reset();
////	int[] selected_actions = my_sm.runStimuli(sample_stimuli);
////	int[] nodes_history = my_sm.getNodes_history();//TODO is there a way to prevent node 4 from being visited here?
////	int[] nodes_types = my_sm.getNodes_types();
////	
////	
////	
////	Set<Integer> nodes_to_be_pruned = new HashSet<Integer>();
////	for(int i = 1;i<=nNodes;i++){
////		nodes_to_be_pruned.add(i);
////	}
////	nodes_to_be_pruned.remove(1); //Node 1 is the root
////	for(Integer node:nodes_history){		
////			nodes_to_be_pruned.remove(node);		
////	}
////	
////	
////	Pruner my_pruner = new Pruner(temp_individual);	
////	int[] pruned_individual_int = my_pruner.pruneNodes(nodes_to_be_pruned);
////	
////	
////	Individual pruned_individual =new Individual(8, nStimuli, nActions);	
////	pruned_individual.setChromossome(pruned_individual_int);
////	double pruned_individual_fit=pruned_individual.getFitness(mySequence);
////	System.out.println("pruned_ fit: "+pruned_individual_fit);
////	
////	
//	}
//	
}
