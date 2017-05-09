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

import javax.lang.model.element.NestingKind;

/**
 *  This class is used to perform a simple reinforcement learning with multiple state dimensions.
 *  
 *  It is composed by a multidimensional Q table with all dimensions but the last meaning states, and with the last dimension meaning actions.
 *  
 *  For instance, with a 2d state space we would have:
 *
 *  	Q(s1,s2,a) = Q(s1,s2,a) + R
 *    
 *     Where R is the reward for performing action a at states s1 and s2.
 *     
 *    Important: This method does not account for state transitions like Q-Learning or SARSA do.
 *  
 * @author Klaus
 *
 */
public class Simple2dRLearn {

	double[][][] Q = null;
	int Na=0; //number of actions
	int Ns=0; //number of states
	Random rnd = new Random();

        /**
         * Default Constructor.
         * @param Ns
         * @param Na 
         */
	public Simple2dRLearn(int Ns,int Na){
		this.Na=Na; //number of actions
		this.Ns=Ns; //number of states
		this.Q =	new double[Ns][Ns][Na]; //All set as zero from the start
	}

	/**
	 * Updates Q table of values
	 * 
	 * @param s1 previous state
	 * @param s2 current state
	 * @param a chosen action
	 * @param r reward for performing action a in state s1/s2, should be between -1 and 1
	 */
	public void update(int s1,int s2, int a, double r){
		if(r<=1 && r>=-1){
			if(s1>=Ns || s2>=Ns || a>=Na){
				if(s1>=Ns || s2>=Ns){
					//				System.out.println("State "+s+" doesn't exist.");
					throw new Error("State [s1="+s1+", s2="+s2+"] doesn't exist.");
				}
				if(a>=Na){
					throw new Error("Action "+a+" doesn't exist.");
				}
			}else{
				Q[s1][s2][a]=Q[s1][s2][a]+r;
				//Must scale for each state
				double new_value = Q[s1][s2][a];

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
						Q[s1][s2][ac]=(Q[s1][s2][ac]-min)/(max-min);
					}
				}else{
					for(int ac=0;ac<Na;ac++){
						Q[s1][s2][ac]=(Q[s1][s2][ac]-min);
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
		double[][][] new_Q = new double[Ns][Ns][newNa];
		for(int s1=0;s1<Ns;s1++){
			for(int s2=0;s2<Ns;s2++){
				for(int ac=0;ac<Na;ac++){
					new_Q[s1][s2][ac]=Q[s1][s2][ac];
				}
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
		double[][][] new_Q = new double[newNs][newNs][Na];

		for(int s1=0;s1<Ns;s1++){
			for(int s2=0;s2<Ns;s2++){
				for(int ac=0;ac<Na;ac++){
					new_Q[s1][s2][ac]=Q[s1][s2][ac];
				}
			}
		}
		this.Ns=newNs;
		Q=new_Q;
	}

        /**
         * Print Q values.
         */
	public void printQ(){
		System.out.println("--- Q table ---");
		for(int ac=0;ac<Na;ac++){
			System.out.println("------ Action "+ac+" ---");
			for(int s1=0;s1<Ns;s1++){
				for(int s2=0;s2<Ns;s2++){
					System.out.print(Q[s1][s2][ac]+" ");
				}
				System.out.println("");
			}
		}
		System.out.println("---------------");
	}


	/**
	 * Gets the best action for state s.
	 * If there is a tie between best actions, it chooses randomly between them. 
	 * @param s1 first 
         * @param s2 second 
	 * @return best action for state s
	 */
	public int getBestAction(int s1, int s2){
		int best_a=-1;

		if((s1<Ns && s1>=0)&& (s2<Ns && s2>=0)){

			double best_value=Double.NEGATIVE_INFINITY;
			for(int act=0;act<Na;act++){
				if(Q[s1][s2][act]>best_value){
					best_value=Q[s1][s2][act];
					best_a=act;
				}
				if(Q[s1][s2][act]==best_value){
					if(rnd.nextBoolean()){
						best_value=Q[s1][s2][act];
						best_a=act;
					}
				}

			}
		}else{
			throw new Error("State [s1: "+s1+" and s2: "+s2+"] doesn't exist.");			
		}

		return best_a;
	}

        /**
         * Returns the best chosen action and its value in Q.
         * NOTE: Remember that best_action should probably be cast to int for use outside this class.
         * @param s1
         * @param s2
         * @return a double array {best_action, best_value} 
        */
	public double[] getBestActionAndValue(int s1, int s2) {
		double best_a=-1;
		double best_value=Double.NEGATIVE_INFINITY;
		if((s1<Ns && s1>=0)&& (s2<Ns && s2>=0)){

			for(int act=0;act<Na;act++){
				if(Q[s1][s2][act]>best_value){
					best_value=Q[s1][s2][act];
					best_a=act;
				}
				if(Q[s1][s2][act]==best_value){
					if(rnd.nextBoolean()){
						best_value=Q[s1][s2][act];
						best_a=act;
					}
				}

			}
		}else{
			throw new Error("State [s1: "+s1+" and s2: "+s2+"] doesn't exist.");			
		}

		double[] best={best_a,best_value};
		
		return best;
	}
	
	/**
         * Gets Q value.
	 * @return the q
	 */
	public double[][][] getQ() {
		return Q;
	}


	/**
         * Sets Q value.
	 * @param q the q to set
	 */
	public void setQ(double[][][] q) {
		this.Ns=q.length;
		this.Na=q[0][0].length;
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
         * Sets NS value.
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
