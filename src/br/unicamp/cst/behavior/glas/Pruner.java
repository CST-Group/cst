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

package br.unicamp.cst.behavior.glas;

import java.util.ArrayList;
import java.util.Set;

/**
 * Prunes a from a GLAS tree a list of nodes.
 * 
 * @author Klaus
 *
 */
public class Pruner {

	private static int[] solution_tree;
	private static int[] structure;
	private static int[] stimuli;
	private static int[] actions;
	
	
	
	public Pruner(int[] solution_tree){
		//TODO check if it is a valid tree
		this.solution_tree=solution_tree;
		
		int nNodes=solution_tree.length/3;
		
		this.structure = new int[nNodes];
		this.stimuli = new int[nNodes];
		this.actions = new int[nNodes];
		
		
		for(int i=0; i<nNodes;i++){
			structure[i]=solution_tree[i];
		}
		
		int count=0;
		for(int i=nNodes; i<2*nNodes;i++){
			stimuli[count]=solution_tree[i];
			count++;
		}
		count=0;
		for(int i=2*nNodes; i<3*nNodes;i++){
			actions[count]=solution_tree[i];
			count++;
		}
		
	}

	/**
	 *  Returns a new tree, which is a pruned version of the original one.
	 *  
	 * @param nodes_to_be_pruned List of nodes that should be pruned from the original tree
	 * @return
	 */
	public int[] pruneNodes(Set<Integer> nodes_to_be_pruned){
		//TODO check if the list of nodes to be pruned is valid

		//TODO update nodes_to_be_pruned with children


		//--------------
//		System.out.println("nodes_to_be_pruned:");
//		for(Integer inteiro : nodes_to_be_pruned){
//			System.out.print(inteiro+", ");
//		}
//		System.out.println("");

		//--------------

//		System.out.println("Initial tree: ");
//		for(Integer inteiro : tree){
//			System.out.print(inteiro+", ");
//		}
//		System.out.println("");




//		int[] indexes = new int[tree.length];
//		System.out.println("indexes: ");
//		for(int i = 0; i<tree.length;i++){
//			indexes[i]=i+1;
//			System.out.print(indexes[i]+", ");
//		}
//		System.out.println("");


		ArrayList<Integer> tree_arraylist= new ArrayList<Integer>();
		ArrayList<Integer> nodes_numbers_arraylist= new ArrayList<Integer>();

		for(int i=0; i<structure.length;i++){
			if(!nodes_to_be_pruned.contains(i+1)){
				tree_arraylist.add(structure[i]);
				nodes_numbers_arraylist.add(i+1);	
			}
		}



//		//--------------
//		System.out.println("tree_arraylist:");
//		for(Integer inteiro : tree_arraylist){
//			System.out.print(inteiro+", ");
//		}
//		System.out.println("");
//
//		System.out.println("nodes_numbers_arraylist:");
//		for(Integer inteiro : nodes_numbers_arraylist){
//			System.out.print(inteiro+", ");
//		}
//		System.out.println("");
//		//--------------





		for(int i = 0; i < nodes_numbers_arraylist.size()-1;i++){

			int current_node_index=nodes_numbers_arraylist.get(i);
			int next_node_index = nodes_numbers_arraylist.get(i+1);

			if((current_node_index+1)!=next_node_index){
				for(int j = i; j<nodes_numbers_arraylist.size();j++){
					if(nodes_numbers_arraylist.get(j)==next_node_index){
						// replace
						nodes_numbers_arraylist.set(j, current_node_index+1);
					}
					if(tree_arraylist.get(j)==next_node_index){
						// replace
						tree_arraylist.set(j, current_node_index+1);
					}
				}
			}
		}

		
//		//--------------
//		System.out.println("pruned tree_arraylist:");
//		for(Integer inteiro : tree_arraylist){
//			System.out.print(inteiro+", ");
//		}
//		System.out.println("");
//
//		System.out.println("pruned nodes_numbers_arraylist:");
//		for(Integer inteiro : nodes_numbers_arraylist){
//			System.out.print(inteiro+", ");
//		}
//		System.out.println("");
//		//--------------

		
		
		
		ArrayList<Integer> pruned_solution_array=new ArrayList<Integer>();
		for(int i = 0; i<tree_arraylist.size();i++){
			pruned_solution_array.add(tree_arraylist.get(i));
		}
		for(int i = 0; i<this.stimuli.length;i++){
			if(!nodes_to_be_pruned.contains(i+1)){
				pruned_solution_array.add(this.stimuli[i]);
			}
		}
		for(int i = 0; i<this.actions.length;i++){
			if(!nodes_to_be_pruned.contains(i+1)){
				pruned_solution_array.add(this.actions[i]);
			}
		}
		
		
		
		int[] pruned_solution=new int[pruned_solution_array.size()];		
		for(int i = 0; i<pruned_solution_array.size();i++){
			pruned_solution[i]=pruned_solution_array.get(i);
		}
		
			
		return pruned_solution;

	}

	


}
