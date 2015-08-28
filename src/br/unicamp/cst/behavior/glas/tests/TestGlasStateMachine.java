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

import br.unicamp.cst.behavior.glas.GlasActionSelection;

/**
 * @author Klaus Raizer
 *
 */
public class TestGlasStateMachine {
	//Solution chromossome for the 1AX task

	static int nNodes = 7;
	static int nActions = 3;
	static int nStimuli = 7;

	static int[] sampled_stimuli =		{1,4,3,5,2,3,4,		6,1,4,3,5,2,3, 	4,6,2,4,3,5,1,	5,4,6,2,3,3,6};    
	static int[] selected_actions =		{1,1,1,2,1,1,1,		2,2,1,1,1,1,0, 	1,2,1,1,1,1,1,	1,1,1,1,1,1,2};	//Some wrong choices
	static double[]	rewards_received = 	{1,1,1,10,1,1,1,	10,-1,1,1,-10,1,-1,	1,10,1,1,1,1,1,	1,1,1,1,1,1,-10};

	static double expected_reward_for_good_solution=51; 




//	@Test
//	public void testGoodSolution_known() {
//
//		int[] good_solution_phenotype=					{0,1,1,2,3,4,5,		0,2,1,4,3,6,5,		0,1,1,1,1,2,2};
//		int[] good_solution_actions=					{1,1,1,2,1,1,1,		2,1,1,1,2,1,1,	1,2,1,1,1,1,1,	1,1,1,1,1,1,1};		
//		int [] good_solution_expected_nodes_history= 	{3,3,5,7,2,2,4,		6,3,3,5,7,2,2,	4,6,2,4,4,4,3,	3,3,3,2,2,2,2};
//
//
//		StateMachine sm = new StateMachine(good_solution_phenotype);
//
//		sm.reset();
//
//		int[] local_selected_actions = sm.runStimuli(sampled_stimuli);
//
//		int[] local_nodes_history = sm.getNodes_history();
//
//		int a=1;
//		a=2;
//
//
//		assertTrue(local_selected_actions.length==good_solution_actions.length);
//
//
//		for(int index=0; index<local_nodes_history.length;index++){
//			assertTrue(local_nodes_history[index]==good_solution_expected_nodes_history[index]);		
//		}
//
//	}


//	
//	@Test
//	public void testBadSolution28() {
//
//		int[] bad_solution_phenotype=					{0,1,2,3,4,5,2,		0,1,4,5,2,6,3,		0,1,1,2,1,2,0};
//		int[] bad_solution_expected_nodes_history=		{2,3,3,4,5,5,5,		6,2,3,3,4,5,5,		5,6,2,3,3,4,2,		2,3,3,3,3,3,3};
//		
//		
////		int[] good_solution_phenotype=					{0,1,1,2,3,4,5,		0,2,1,4,3,6,5,		0,1,1,1,1,2,2};
////		int[] good_solution_actions=					{1,1,1,2,1,1,1,		2,1,1,1,2,1,1,	1,2,1,1,1,1,1,	1,1,1,1,1,1,1};		
////		int [] good_solution_expected_nodes_history= 	{3,3,5,7,2,2,4,		6,3,3,5,7,2,2,	4,6,2,4,4,4,3,	3,3,3,2,2,2,2};
//
//
//		StateMachine sm = new StateMachine(bad_solution_phenotype);
//
//		sm.reset();
//
//		int[] local_selected_actions = sm.runStimuli(sampled_stimuli);
//
//		int[] local_nodes_history = sm.getNodes_history();
//
//		int a=1;
//		a=2;
//
//
//
//
//		for(int index=0; index<local_nodes_history.length;index++){
//			assertTrue(local_nodes_history[index]==bad_solution_expected_nodes_history[index]);		
//		}
//
//	}

	@Test
	public void testGoBackToOriginalSN(){

		int[] good_solution_phenotype=					{0,1,1,2,3,4,5,		0,2,1,4,3,6,5,		0,1,1,1,1,2,2};
		int[] good_solution_actions=					{1,1,1,2,1,1,1,		2,1,1,1,2,1,1,	1,2,1,1,1,1,1,	1,1,1,1,1,1,1};		
		int [] good_solution_expected_nodes_history= 	{3,3,5,7,2,2,4,		6,3,3,5,7,2,2,	4,6,2,4,4,4,3,	3,3,3,2,2,2,2};


		GlasActionSelection sm = new GlasActionSelection(good_solution_phenotype);

		sm.reset();
		
		
		int stim=3333; //Unknown
		int current_node_minus_1 = 7-1;
		
		System.out.println("Before: "+current_node_minus_1);
		
		current_node_minus_1=sm.goBackToOriginalSN(current_node_minus_1,stim);
		
		System.out.println("After: "+current_node_minus_1);
		System.out.println("Should be: "+(3-1));
		
		assertTrue(current_node_minus_1==(3-1));
		
		//TODO Reproduce here the anomalous case from solution 28
	}


		int[] solution_chromossome={0, 1, 1, 2, 3, 4, 5, 0, 1, 2, 3, 4, 5, 6, 0, 1, 1, 1, 1, 2, 2};
		int[] known_types = {0, 1, 1, 2, 2, 3, 3};


	//	0 1 2 3 4 5 6
	//	0 1 2 a b x y  //0 means unknown

	// 0 1 2 
	// 0 L R

	//	@Test
	//	public void testStateMachineUpdateNodesTypes() {
	//		StateMachine sm=new StateMachine(solution_chromossome);
	//
	//
	//
	//		int[] nodes_types = sm.getNodes_types();
	//
	//		assertTrue(nodes_types.length==known_types.length);
	//		for (int i=0; i<known_types.length;i++){
	//			assertTrue(nodes_types[i]==known_types[i]);
	//		}
	//
	//	}
	//
		@Test
		public void testActionSelection(){
	
			int[] sample_stimuli =		{1,4,3,5,2,3,4,6}; //1 b a x 2 a b y
			int[] expected_actions =	{1,1,1,2,1,1,1,2};	
			
			GlasActionSelection sm = new GlasActionSelection(solution_chromossome);
	
			sm.reset();
			
			int[] selected_actions = sm.runStimuli(sample_stimuli);
	
			assertTrue(selected_actions.length==expected_actions.length);
			for(int i=0;i<selected_actions.length;i ++){
				assertTrue(expected_actions[i]==selected_actions[i]);
			}
	
		}
	//
	//
	//	@Test
	//	public void testActionSelection2(){
	//
	//		int[] sample_stimuli2 =		{2,1,3,3,5,4,6,3,5}; //"21AAXBYAX"  "LLLLRLLLR"
	//		int[] expected_actions2 =	{1,1,1,1,2,1,1,1,2};			
	//		
	//		StateMachine sm = new StateMachine(solution_chromossome);
	//
	//		sm.reset();
	//		
	//		int[] selected_actions = sm.runStimuli(sample_stimuli2);
	//
	//		assertTrue(selected_actions.length==expected_actions2.length);
	//		for(int i=0;i<selected_actions.length;i ++){
	//			assertTrue(expected_actions2[i]==selected_actions[i]);
	//		}
	//
	//	}
	//	
	//	@Test
	//	public void testActionSelection3(){
	//
	//		
	//		//	0 1 2 3 4 5 6
	//		//	0 1 2 a b x y  //0 means unknown
	//
	//		// 0 1 2 
	//		// 0 L R
	//		//Here, 9 and 8 are unknown, and should be ignored
	//		int[] sample_stimuli2 =		{1,2,9,4,8,6}; //"12CBZY"  "LLLR"
	//		int[] expected_actions2 =	{1,1,0,1,0,2};			
	//		
	//		StateMachine sm = new StateMachine(solution_chromossome);
	//
	//		sm.reset();
	//		
	//		int[] selected_actions = sm.runStimuli(sample_stimuli2);
	//
	//		assertTrue(selected_actions.length==expected_actions2.length);
	//		for(int i=0;i<selected_actions.length;i ++){
	//			assertTrue(expected_actions2[i]==selected_actions[i]);
	//		}
	//
	//	}
	//	
	//	

	//	
	//	@Test
	//	public void testActionSelection4(){
	////TODO It seems like he came back to 1 at the middle
	////		int[] sample_stimuli =		{1, 4, 3, 5, 2, 3, 4, 6,    1, 4, 3, 5,  2, 3, 4, 6};   
	////		int[] expected_actions =	{1, 1, 1, 2, 1, 1, 1, 2,    1, 2, 1, 1,  1,  1, 1, 2};	
	//		int[] sample_stimuli =		{1, 4, 3, 5, 2, 3, 4, 6,    3, 5, 1, 4, 6, 2, 3, 6};   
	//		int[] expected_actions =	{1, 1, 1, 2, 1, 1, 1, 2,    1, 1, 1, 1, 1, 1, 1, 1};
	//		
	//		StateMachine sm = new StateMachine(solution_chromossome);
	//
	//		sm.reset();
	//		
	//		int[] selected_actions = sm.runStimuli(sample_stimuli);
	//
	//		assertTrue(selected_actions.length==expected_actions.length);
	//		for(int i=0;i<selected_actions.length;i ++){
	//			assertTrue(expected_actions[i]==selected_actions[i]);
	//		}
	//
	//	}


}
