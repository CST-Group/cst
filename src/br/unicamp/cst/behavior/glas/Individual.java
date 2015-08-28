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
import java.util.Random;

/**
 * @author Klaus Raizer
 *
 */
public class Individual {

	private int nNodes;
	private int nStimuli;
	private int nActions;
	private int[] chromossome;
	private GlasActionSelection state_machine;
	private double nf=0; 

	public Individual(int nNodes, int nStimuli, int nActions) {
		this.nNodes=nNodes;
		this.nStimuli=nStimuli;
		this.nActions=nActions;

		chromossome= new int[3*nNodes];

		Random generator = new Random();

		//Random contents
		for(int i=nNodes+1;i<2*nNodes;i++){
			chromossome[i]=generator.nextInt(nStimuli); 
		}
		chromossome[nNodes+1]=0; //unknown

		//Random actions
		for(int i=2*nNodes+1;i<3*nNodes;i++){
			chromossome[i]=generator.nextInt(nActions); 
		}

		//Random structure

		for(int node=1;node<nNodes;node++){
			chromossome[node]=generator.nextInt(node)+1; 
		}



		//		%- Initial structures
		//		treeStructure=sample_individual(1:nNodes); 
		//		randomStructure=repmat(treeStructure,N,1); 
		//		structureNum=randomStructure(:,1:nNodes);
		//		shouldMutate=(ones(N,nNodes));
		//		shouldMutate(:,1)=zeros(N,1); %avoid mutating root node
		//		for node=2:nNodes
		//		    mutation(:,node)=randi(node-1,N,1);
		//		end
		//		 mutated_statesNum=mutation.*shouldMutate+structureNum.*not(shouldMutate);
		//		randomStructure(:,1:nNodes)=mutated_statesNum;
		//		%--------


		this.state_machine=new GlasActionSelection(chromossome);
	}

	public int[] getChromossome() {
		return chromossome;
	}

	public void setChromossome(int[] chromossome) {
		this.chromossome = chromossome;
		this.state_machine=new GlasActionSelection(chromossome);		
	}

	/**
	 *  Evaluates this individual's evaluation. The lower the better.
	 * @param sequence 
	 * @return individual's evaluation
	 */
	public double getEvaluation(GlasSequence sequence){
		double eval=Double.MAX_VALUE;
		double fitRewards=getFitness(sequence);

		if(fitRewards!=0){ //Eval = inverse fitness
			eval=1/fitRewards;
		}else{
			eval=0;
		}
		return (eval);

	}

	/**
	 *  Evaluates this individual's fitness. The higher the better.
	 * @param sequence 
	 * @return individual's fitness
	 */
	public double getFitness(GlasSequence sequence){
		//		double eval=Double.MAX_VALUE;

		ArrayList<GlasEvent> events = sequence.getEvents();
		int[] stimuli=new int[events.size()];
		int[] actions=new int[events.size()];
		double[] rewards=new double[events.size()];
		GlasEvent event = events.get(0);

		for (int ev=0;ev<events.size();ev++){
			event = events.get(ev);
			stimuli[ev]=event.getStimulus();
			actions[ev]=event.getAction();
			rewards[ev]=event.getReward();
		}

		state_machine.reset();
		int[] selected_actions = state_machine.runStimuli(stimuli);




		//		System.out.println("");
		//		System.out.print("selected_actions = [");
		//		for(int index=0; index<selected_actions.length;index++){
		//			System.out.print(selected_actions[index]+", ");
		//		}
		//		System.out.println("]");



		//stimuliNum=sequence(1,:);
		//actionsNum=sequence(2,:);
		//rewards=sequence(3,:);
		//N=size(population,1);
		//
		//currentStateNum=1; %'Starting'
		//fitRewards=zeros(1,N);
		//% 
		//%% Fitness due to rewards

		double fitRewards=0;

		for(int i=0; i<events.size();i++){
			if(actions[i]==selected_actions[i]){//Otherwise, I just can't tell
				fitRewards=fitRewards+rewards[i];
			}
		}


		// --- Bonus for visiting  a smaller number of intermediate nodes //TODO Check if this is valid for all cases
		int[] nodes_history = state_machine.getNodes_history();
		int[] nodes_types = state_machine.getNodes_types();

		double bonus_int=0;
		for(int i=0; i<nodes_history.length;i++){
			if(nodes_history[i]!=0){				
				if(nodes_types[nodes_history[i]-1]==state_machine.INTERMEDIATE_NODE){
					bonus_int=bonus_int+1;	
				}			
			}
		}

		bonus_int=bonus_int/nodes_history.length;
		bonus_int=1-bonus_int;
		// --------------------------

		// --- Bonus for having a smaller number of nodes ---
		//		double bonus_smaller = 1.0/nNodes;		
		double bonus_smaller = -nNodes;
		// -----------


		// --- Bonus for more sequence starters ---

		double bonus_sn=0;


		for(int i=0; i<this.nNodes;i++){
			if(this.chromossome[i]==1){
				bonus_sn++;
			}
		}
		bonus_sn=bonus_sn/nNodes;

		// --------


		double alpha=1;
		double beta=0;
		double gamma=1;
		double delta=1;

		fitRewards=fitRewards*alpha + bonus_int*beta + bonus_smaller*gamma + bonus_sn*delta;

		return fitRewards;
	}
	/**
	 * Stores a normalized fitness value.
	 * WARNING: As it is, it should only be used for debug purposes. 
	 * @param nf
	 */
	public void setNormalizedFitness(double nf) {
		this.nf=nf;		
	}

	/**
	 * Returns a normalized fitness value.
	 * WARNING: As it is, it should only be used for debug purposes. 
	 * @return the normalized fitness nf
	 */
	public double getNormalizedFitness() {
		return nf;
	}





}
