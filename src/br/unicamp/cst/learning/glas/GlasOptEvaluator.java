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

package br.unicamp.cst.learning.glas;
import java.util.ArrayList;

import org.opt4j.core.Objective.Sign;
import org.opt4j.core.Objectives;
import org.opt4j.core.problem.Evaluator;

import com.google.inject.Inject;
import com.google.inject.Provider;

import br.unicamp.cst.behavior.glas.GlasSequence;
import br.unicamp.cst.behavior.glas.Individual;

/**
 * http://kaskavalci.com/?p=231
 */

/**
 * @author Klaus
 *
 */
public class GlasOptEvaluator implements Evaluator<int[]> {
//	static int[] known_final_solution_genotype={ 1, 1, 2, 3, 4, 5,    1, 2, 3, 4, 5, 6,      1, 1, 1, 1, 2, 2};
	static int[] known_final_solution_phenotype={0, 1, 1, 2, 3, 4, 5,   0, 1, 2, 3, 4, 5, 6,     0, 1, 1, 1, 1, 2, 2};
	Provider<GlasSequence> provided_sequence;
	private GlasSequence sequence;

	double best_eval;
	private int nStimuli;
	private int nActions;


	@Inject
	public GlasOptEvaluator( Provider<GlasSequence> provided_sequence) {
		this.provided_sequence= provided_sequence;
		sequence = provided_sequence.get();
		//Now you can access the elements!
		
		best_eval=Double.NEGATIVE_INFINITY; //For maximization
		
		nStimuli = sequence.getNStimuli(); 
		nActions = sequence.getNActions(); 
		
	}

	@Override
	public Objectives evaluate(int[] phenotype) {

		int nNodes=Math.round(phenotype.length/3)+1;
		
		Individual indi =new Individual(nNodes, nStimuli, nActions);

		indi.setChromossome(phenotype);
		double current_eval=indi.getFitness(sequence);
		
				
		if(current_eval>best_eval){ //For maximization
			best_eval=current_eval;	
		}
		
//		System.out.println(current_eval);
		// --- Return evaluation ---

		Objectives objectives = new Objectives();
		objectives.add("reward", Sign.MAX, current_eval);
//		objectives.add("scaled_tree_depth", Sign.MIN, scaled_tree_depth_penalty);
		
		
		
//		System.out.println(current_eval);
		
		return objectives;
	}





	private int getTreeDepth(int[] my_tree){ //TODO should I move this to Individual?
		int nNodes=Math.round(my_tree.length/3)+1;
		//Find leaves

		ArrayList<Integer> leaves = new ArrayList<Integer>();

		for(int n=1;n<=nNodes;n++){
			boolean isLeaf=true;
			for(int i=1; i<nNodes;i++){
				if(my_tree[i]==n){
					isLeaf=false;
				}
			}
			if(isLeaf){
				leaves.add(n);
			}
		}

		//For each leaf, trace back to root

		int deepest=0;

		for(int l=0;l<leaves.size();l++){
			int current_node=leaves.get(l);

			int depth=0;
			while(current_node>0) { 
				current_node=my_tree[current_node-1];
				depth++;
			}
			if(depth>deepest){
				deepest=depth;			
			}

		}








		return deepest;
	}





}