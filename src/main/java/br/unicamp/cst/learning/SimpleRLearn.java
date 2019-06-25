/*******************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     K. Raizer, A. L. O. Paraense, R. R. Gudwin - initial API and implementation
 *     E. M. Froes - documentation
 ******************************************************************************/

package br.unicamp.cst.learning;

import java.util.Random;

/**
 *  This class is used to perform a simple reinforcement learning.
 *  
 *  It is composed by a Q table with rows meaning states and columns meaning actions.
 *  
 *    For row s and column a, we have:
 *    
 *    Q(s,a) = Q(s,a) + R
 *    
 *     Where R is the reward for performing action a at state s.
 *     
 *    Important: This method does not account for state transitions like Q-Learning or SARSA do.
 *  
 * @author Klaus
 *
 */
public class SimpleRLearn {

	double[][] Q = null;
	int Na=0; //number of actions
	int Ns=0; //number of states
	Random rnd = new Random();
        
        /**
         * Default Constructor.
         * @param Ns
         * @param Na 
         */
      	public SimpleRLearn(int Ns,int Na){
		this.Na=Na; //number of actions
		this.Ns=Ns; //number of states
		this.Q =	new double[Ns][Na]; //All set as zero from the start
	}

        /**
         * Updates Q table of values.
         * @param s current state
         * @param a chosen action
         * @param r reward for performing action a in state s, should be between -1 and 1
        */
	public void update(int s, int a, double r){
		if(r<=1 && r>=-1){
		if(s>=Ns && a>=Na){ //TODO  shouldn't be || ?
			if(s>=Ns){
				//System.out.println("State "+s+" doesn't exist.");
				throw new Error("State "+s+" doesn't exist.");
			}
			if(a>=Na){
				throw new Error("Action "+a+" doesn't exist.");
			}
		}else{
			Q[s][a]=Q[s][a]+r;
			//Must normalize for each state
			double new_value = Q[s][a];

			double min=0;
			double max=1;

			if(new_value>1){
				max=new_value;
			}
			if(new_value<0){
				min=new_value;
			}

			if((max-min)!=0){
				for(int ac=0;ac<Na;ac++){
					Q[s][ac]=(Q[s][ac]-min)/(max-min);
				}
			}else{
				for(int ac=0;ac<Na;ac++){
					Q[s][ac]=(Q[s][ac]-min);
				}
			}

		}
		}else{
			throw new Error("Reward value out of range. It should be between -1 and 1");
		}
		
		
	}

        /**
         * Add action.
         */
	public void addAction(){
		int newNa = this.Na+1;
		double[][] new_Q = new double[Ns][newNa];

		for(int st=0;st<Ns;st++){
			for(int ac=0;ac<Na;ac++){
				new_Q[st][ac]=Q[st][ac];
			}
		}
		this.Na=newNa;
		Q=new_Q;
	}

        /**
         * Add state.
         */
	public void addState(){
		int newNs = this.Ns+1;
		double[][] new_Q = new double[newNs][Na];

		for(int st=0;st<Ns;st++){
			for(int ac=0;ac<Na;ac++){
				new_Q[st][ac]=Q[st][ac];
			}
		}
		this.Ns=newNs;
		Q=new_Q;
	}

        /**
         * Print Q value.
         */
	public void printQ(){
		System.out.println("--- Q table ---");
		for(int st=0;st<Ns;st++){
			for(int ac=0;ac<Na;ac++){
				System.out.print(Q[st][ac]+" ");
			}
			System.out.println("");
		}
		System.out.println("---------------");
	}
	
	
	/**
	 * Gets the best action for state s.
	 * If there is a tie between best actions, it chooses randomly between them. 
	 * @param s input state
	 * @return best action for state s
	 */
	public int getBestAction(int s){
		int best_a=-1;

		if(s<Ns){

			double best_value=Double.NEGATIVE_INFINITY;
			for(int act=0;act<Na;act++){
				if(Q[s][act]>best_value){
					best_value=Q[s][act];
					best_a=act;
				}
				if(Q[s][act]==best_value){
					if(rnd.nextBoolean()){
						best_value=Q[s][act];
						best_a=act;
					}
				}
				
			}
		}else{
			throw new Error("State "+s+" doesn't exist.");			
		}

		return best_a;
	}


	/**
         * Gets Q value.
	 * @return the q
	 */
	public double[][] getQ() {
		return Q;
	}


	/**
         * Sets Q value.
	 * @param q the q to set
	 */
	public void setQ(double[][] q) {
		Q = q;
	}


	/**
         * Gets NA value.
	 * @return the na
	 */
	public int getNa() {
		return Na;
	}


	/**
         * Sets NA value.
	 * @param na the na to set
	 */
	public void setNa(int na) {
		Na = na;
	}


	/**
         * Gets NS value.
	 * @return the ns
	 */
	public int getNs() {
		return Ns;
	}


	/**
         * Gets NS value.
	 * @param ns the ns to set
	 */
	public void setNs(int ns) {
		Ns = ns;
	}

        /**
         * Verify if is empty.
         * @return true or false
         */
	public boolean isEmpty(){
		boolean empty=(this.Na==0 || this.Ns==0);
		return empty;
	}
	
}
