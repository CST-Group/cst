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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.RawMemory;
import br.unicamp.cst.memory.WorkingStorage;

/**
 * 
 * Selects action based on solution tree retrieved from working storage.
 * @author klaus
 *
 */
public class ActionSelectionCodelet extends Codelet{

	Memory SOLUTION_TREE_MO;	//input (should be found in working storage)
	Memory ACTION_MO; //output
	Memory STIMULUS_MO; //input
	int[] empty_solution_tree={0, 1, 0, 1, 0, 1};
	private GlasActionSelection sm;
	private Timestamp last_stimulus_time_stamp;
	private boolean first_run=true;
	private JSONArray current_solution_tree_jsonarray=new JSONArray();
	private Memory NEW_STIM_MO;
	//	private Memory NEW_EVENT_DETECTED_MO;
	private Memory NEW_ACTION_MO;
	private Memory NEW_REWARD_MO;
	private boolean enabled=true;
	private double exp_factor=0; //Exploratory factor
	private double solution_tree_fitness = Double.NEGATIVE_INFINITY;
	private boolean dynamicExplorationOn=false;
	
	private RawMemory rawMemory;
	
	private WorkingStorage ws;

	public ActionSelectionCodelet(RawMemory rawMemory,WorkingStorage ws)
	{
		this.rawMemory = rawMemory;
		this.ws=ws;
		if(ws!=null)
		{
			ws.registerCodelet(this,"SOLUTION_TREE", 0);
			ws.registerCodelet(this,"STIMULUS", 0);
			ws.putMemoryObject(ACTION_MO);
			ws.registerCodelet(this,"NEW_STIM", 0);
			ws.registerCodelet(this,"NEW_ACTION", 0);
			ws.registerCodelet(this,"NEW_REWARD", 0);
//			ws.registerCodelet(this,MemoryObjectTypesGlas.ACTION, 1);
//			ws.registerCodelet(this,MemoryObjectTypesGlas.NEW_EVENT_DETECTED, 0);
		}
			
		
		if(rawMemory!=null)
			ACTION_MO = rawMemory.createMemoryObject("ACTION", "");
		
		

		for(int i=0; i<empty_solution_tree.length;i++){
			current_solution_tree_jsonarray.put(empty_solution_tree[i]);
		}

		sm = new GlasActionSelection(empty_solution_tree);
		sm.reset();
	}

	@Override
	public void accessMemoryObjects() 
	{		
		SOLUTION_TREE_MO=this.getInput("SOLUTION_TREE", 0);
		ArrayList<Memory> teste = ws!=null ? ws.getAllOfType("SOLUTION_TREE") : null;
		STIMULUS_MO=this.getInput("STIMULUS", 0);
		//		System.out.println("(STIMULUS_MO.getInfo() (antes): "+STIMULUS_MO.getInfo());
		//		ACTION_MO=this.getOutput(MemoryObjectTypesGlas.ACTION, 0);

		//		NEW_EVENT_DETECTED_MO = this.getInput(MemoryObjectTypesGlas.NEW_EVENT_DETECTED,0);

		int index=0;
		NEW_STIM_MO = this.getInput("NEW_STIM", index);
		NEW_ACTION_MO = this.getInput("NEW_ACTION", index);
		NEW_REWARD_MO = this.getInput("NEW_REWARD", index);
	}

	@Override
	public void calculateActivation() {
		// TODO Auto-generated method stub
	}

	@Override
	public void proc() {
		if(enabled){


			//Update Solution Tree if needed
			Object new_solution_tree_string = SOLUTION_TREE_MO.getI();
			if(!new_solution_tree_string.equals(this.current_solution_tree_jsonarray.toString())){
				//			System.out.println("Action Selection Found a new Solution Tree: "+new_solution_tree_string);
				JSONArray new_solution_tree_jsonarray;
				try {
					new_solution_tree_jsonarray = new JSONArray(new_solution_tree_string);
					int[] new_solution_tree_phenotype = new int[new_solution_tree_jsonarray.length()];

					for(int i=0; i<new_solution_tree_phenotype.length;i++){
						new_solution_tree_phenotype[i]=new_solution_tree_jsonarray.getInt(i);
					}
					current_solution_tree_jsonarray=new_solution_tree_jsonarray;

					sm = new GlasActionSelection(new_solution_tree_phenotype);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				solution_tree_fitness = SOLUTION_TREE_MO.getEvaluation();
//				exp_factor=1-(1/(1+Math.exp(-solution_tree_fitness)));
				//1/(1+EXP(AB3*10))
				if(dynamicExplorationOn){
					exp_factor=(1/(1+Math.exp(10*solution_tree_fitness)));
				}
				
			}



			//Selects action based on stimulus

			boolean new_stim=(NEW_STIM_MO.getI().equals(String.valueOf(true)));
			boolean new_action=(NEW_ACTION_MO.getI().equals(String.valueOf(true)));
			boolean new_reward=(NEW_REWARD_MO.getI().equals(String.valueOf(true)));

			if(!STIMULUS_MO.getI().equals("") && new_stim && !new_action && !new_reward){

				int[] stimulus = {Integer.valueOf((String)STIMULUS_MO.getI())};
				int[] selected_action = sm.runStimuli(stimulus); //TODO Ugly solution


				//TODO Add an exploratory element here?


				Random rnd_exp = new Random();

				if(rnd_exp.nextFloat()<=exp_factor){
					int[] actions = sm.getActions();
					selected_action[0]=actions[rnd_exp.nextInt(actions.length)];

				}


				ACTION_MO.setI(Integer.toString(selected_action[0])); //TODO is [0] correct?
				//			System.out.println("ACTION_MO.updateInfo("+selected_action[0]+")");

				new_action=true;
				//			System.out.println("new_action=true;");
			}

			NEW_ACTION_MO.setI(String.valueOf(new_action));

		}//end if(enabled)
	}// end proc()

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	//	public int[] getGenotypeFromPhenotype(int[] phenotype) {
	//
	//
	//		int nN=Math.round(phenotype.length/3)+1;
	//		int[] genotype = new int[nN*3];
	//
	//		genotype[0]=0;//First nodes always zero
	//		genotype[nN]=0;//First nodes always zero
	//		genotype[2*nN]=0;//First nodes always zero 
	//
	//		int index = 0;
	//		for(int i=1;i<nN;i++){	
	//			genotype[i] = phenotype[index];
	//			index++;
	//		}
	//		index = nN-1;
	//		for(int i=nN+1;i<2*nN;i++){		
	//			genotype[i] = phenotype[index];
	//			index++;
	//		}
	//		index = 2*nN-2;
	//		for(int i=2*nN+1;i<3*nN;i++){		
	//			genotype[i] = phenotype[index];
	//			index++;
	//		}
	//
	//		return genotype;
	//	}

	/**
	 * @return the exp_factor
	 */
	public double getExp_factor() {
		return exp_factor;
	}

	/**
	 * Probability (0-1) of getting a random action
	 * @param ef the exp_factor to set
	 */
	public void setExp_factor(double ef) {
		if(ef<0.0){
			this.exp_factor = 0.0;
		}else if(ef>1){
			this.exp_factor = 1.0;
		}else{
			this.exp_factor = ef;
		}

	}

	/**
	 * @return the dynamicExplorationOn
	 */
	public boolean isDynamicExplorationOn() {
		return dynamicExplorationOn;
	}

	/**
	 * @param dynamicExplorationOn the dynamicExplorationOn to set
	 */
	public void setDynamicExplorationOn(boolean dynamicExplorationOn) {
		this.dynamicExplorationOn = dynamicExplorationOn;
	}


}
