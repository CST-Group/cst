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

/**
 * This class evaluates how nodes change given a solution tree and a sequence of stimuli.
 * This function implements a state machine with 4 sates. Each states
 *  defines at which kind of node we are currently at.
 * 
 * @author Klaus
 *
 */
public class GlasActionSelection {

	private int[] solution_tree_phenotype;//Phenotype
/**
 * Chromossome (Phenotype) of the new StateMachine
 * @param solution_tree_phenotype
 */
	public GlasActionSelection(int[] solution_tree_phenotype) {
		this.solution_tree_phenotype=solution_tree_phenotype;
		nNodes=(int)(solution_tree_phenotype.length/3);
		nodes_types=new int[nNodes];
		updateNodesTypes();
	}

	private int[] nodes_types;
	private int nNodes;
	public static int ROOT_NODE=0;
	public static int SEQUENCE_START_NODE=1;
	public static int INTERMEDIATE_NODE=2;
	public static int SEQUENCE_END_NODE=3;

	private int current_node_type=0;
	int[] structure;
	int[] contents;
	int[] actions;
	private int[] actions_history;
	private int[] nodes_history;
	private int current_node_minus_1=0;

	public void updateNodesTypes(){

		structure=new int[nNodes];
		for (int i=0; i<nNodes;i++){
			structure[i]=solution_tree_phenotype[i];
		}

		contents=new int[nNodes];
		for (int i=0; i<nNodes;i++){
			contents[i]=solution_tree_phenotype[i+nNodes];
		}

		actions=new int[nNodes];
		for (int i=0; i<nNodes;i++){
			actions[i]=solution_tree_phenotype[i+2*nNodes];
		}


		//--- Identify current node
		int max_node=0;
		for (int i=0; i<nNodes;i++){
			if(structure[i]>max_node){
				max_node=structure[i];
			}
		}		


		for (int i=1; i<=nNodes;i++){

			if(structure[i-1]==0){
				nodes_types[i-1]=ROOT_NODE;
			}else if(structure[i-1]==1){
				nodes_types[i-1]=SEQUENCE_START_NODE;
			}else if (this.getChildren(structure,i).isEmpty()){// (i>max_node) { //TODO this criterium is wrong
				nodes_types[i-1]=SEQUENCE_END_NODE;
			}else{
				nodes_types[i-1]=INTERMEDIATE_NODE;
			}

		}
	}


	private ArrayList<Integer> getChildren(int[] structure2, int i) {
		// TODO Auto-generated method stub
		ArrayList<Integer> children = new ArrayList<Integer>();

		for(int n = 0; n<structure2.length;n++){
			if(structure2[n]==i){
				children.add(n+1);
			}
		}

		return children;
	}



	/**
	 * @return the chromossome
	 */
	public int[] getChromossome() {
		return solution_tree_phenotype;
	}

	/**
	 * @param chromossome the chromossome to set
	 */
	public void setChromossome(int[] chromossome) {
		this.solution_tree_phenotype = chromossome;
	}

	/**
	 * @return the nodes_types
	 */
	public int[] getNodes_types() {
		return nodes_types;
	}

	/**
	 * @param nodes_types the nodes_types to set
	 */
	public void setNodes_types(int[] nodes_types) {
		this.nodes_types = nodes_types;
	}

	/**
	 * @return the current_node_type
	 */
	public int getCurrent_node_type() {
		return current_node_type;
	}

	/**
	 * @param current_node_type the current_node_type to set
	 */
	public void setCurrent_node_type(int current_node_type) {
		this.current_node_type = current_node_type;
	}

	/**
	 *   This method implements a state machine with 4 sates. 
	 *   Each state defines at which kind of node we are currently at.
	 * 
	 */
	public int[] runStimuli(int[] stimuli) {

		actions_history= new int[stimuli.length];
		nodes_history=new int[stimuli.length];


		//		current_node_minus_1=0; //Initial node minus one (for array refferencing)
		for (int st=0; st<stimuli.length;st++){



			int stim = stimuli[st];

			if(isStimKnown(stim)){
				//State machine		
				if(nodes_types[current_node_minus_1]==ROOT_NODE){
					// From here it can only go to sequence starter nodes

					//Check if stim is a viable SN node

					boolean viableSN=false;

					for(int i=0; i<nNodes;i++){
						if((nodes_types[i]==SEQUENCE_START_NODE)&&(stim==contents[i])){
							current_node_minus_1=i;
							viableSN=true;
							break;
						}
					}
					int debug1=0;
					debug1=1;


				}else if((nodes_types[current_node_minus_1]==SEQUENCE_START_NODE)||(nodes_types[current_node_minus_1]==INTERMEDIATE_NODE)){
					// Can go up to an INTERMEDIATE NODE, a SEQUENCE_END_NODE or back to another SEQUENCE_START_NODE

					//Check if stim is a viable SN node (priority)
					boolean viableSN=false;
					for(int i=0; i<nNodes;i++){
						if((nodes_types[i]==SEQUENCE_START_NODE)&&(stim==contents[i])){//TODO something weird here
							current_node_minus_1=i;
							viableSN=true;
							break;
						}
					}

					int debug1=0;
					debug1=1;

					if(!viableSN){
						//						// Check if it is next in the sequence of children
						//						for(int i=0; i<nNodes;i++){
						//							if(structure[i]==current_node_minus_1+1){//found child
						//								if(stim==contents[i]){// found match, move there
						//									current_node_minus_1=i;
						//									break;
						//								}
						//							}
						//						}
						current_node_minus_1=moveToChildNodeIfHasStim(current_node_minus_1,stim);
					}

					debug1=1;

				}else if(nodes_types[current_node_minus_1]==SEQUENCE_END_NODE){

					//			 Can only go back to a SN
					//	         This is a special case. Even if the stimuli is not an SN, it should
					//	         climb the tree back  to the SN which originated this sequence.


					//Check if stim is a viable SN node (priority)
					boolean viableSN=false;
					for(int i=0; i<nNodes;i++){
						if((nodes_types[i]==SEQUENCE_START_NODE)&&(stim==contents[i])){
							current_node_minus_1=i;
							viableSN=true;
							break;
						}
					}

					if(!viableSN){
						// Go back to original SN												
						current_node_minus_1=goBackToOriginalSN(current_node_minus_1,stim);
					}

					//TODO Now, before moving on, we need to see if any of its children has stim
					current_node_minus_1=moveToChildNodeIfHasStim(current_node_minus_1,stim);

				}else{//should not happen
					try {
						throw new Exception("This should not happen. There is something wrong here...");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}


				//Register current node
				nodes_history[st]=current_node_minus_1+1; //Adding one for the record
				actions_history[st]=actions[current_node_minus_1];	
	
			}else{
				actions_history[st]=actions[0]; //ignores
			}


		}//End events loop




		return actions_history;
	}

	private int moveToChildNodeIfHasStim(int current_node_minus_1, int stim) {
		// Check if it is next in the sequence of children
		for(int i=0; i<nNodes;i++){
			if(structure[i]==current_node_minus_1+1){//found child
				if(stim==contents[i]){// found match, move there if different
					if(stim!=contents[current_node_minus_1]){//TODO do I really need this condition ?
						current_node_minus_1=i;					
						break;
					}
				}
			}
		}
		return current_node_minus_1;
	}

	public int goBackToOriginalSN(int current_node_minus_1, int stim) {

		int previousContent=contents[current_node_minus_1];

		boolean not_a_start_node_yet=(structure[current_node_minus_1]>1);
		boolean current_node_content_is_not_stim = (contents[current_node_minus_1]!=stim);
		boolean previous_content_is_stim = (previousContent==stim);

		//		while( not_a_start_node_yet && (current_node_content_is_not_stim || previous_content_is_stim) ){ //TODO: should it also stop if its child contains stim?
		while(not_a_start_node_yet){
			current_node_minus_1=structure[current_node_minus_1]-1;

			not_a_start_node_yet=(structure[current_node_minus_1]>1);
			current_node_content_is_not_stim = (contents[current_node_minus_1]!=stim);
		}

		return current_node_minus_1;
	}

	private boolean isStimKnown(int stim) {

		boolean stimKnown=false;

		for (int i=0; i<nNodes;i++){
			if(stim==contents[i]){
				stimKnown=true;
				break;
			}
		}


		return stimKnown;
	}

	public void reset() {
		current_node_type=ROOT_NODE;
		current_node_minus_1=0;
	}

	/**
	 * @return the actions_history
	 */
	public int[] getActions_history() {
		return actions_history;
	}

	/**
	 * @param actions_history the actions_history to set
	 */
	public void setActions_history(int[] actions_history) {
		this.actions_history = actions_history;
	}

	/**
	 * @return the nodes_history
	 */
	public int[] getNodes_history() {
		return nodes_history;
	}

	/**
	 * @param nodes_history the nodes_history to set
	 */
	public void setNodes_history(int[] nodes_history) {
		this.nodes_history = nodes_history;
	}


	/**
	 * @return the nNodes
	 */
	public int getnNodes() {
		return nNodes;
	}


	/**
	 * @param nNodes the nNodes to set
	 */
	public void setnNodes(int nNodes) {
		this.nNodes = nNodes;
	}


	/**
	 * @return the structure
	 */
	public int[] getStructure() {
		return structure;
	}


	/**
	 * @param structure the structure to set
	 */
	public void setStructure(int[] structure) {
		this.structure = structure;
	}


	/**
	 * @return the contents
	 */
	public int[] getContents() {
		return contents;
	}


	/**
	 * @param contents the contents to set
	 */
	public void setContents(int[] contents) {
		this.contents = contents;
	}


	/**
	 * @return the actions
	 */
	public int[] getActions() {
		return actions;
	}


	/**
	 * @param actions the actions to set
	 */
	public void setActions(int[] actions) {
		this.actions = actions;
	}

}
