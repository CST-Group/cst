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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The update equation for TD Q-learning is:
 * Q(s,a)= Q(s,a) + alpha * (r(s) + gamma * Max(s', all actions) - Q(s,a))
 * 
 * which is calculated whenever action a is executed in state s leading to state s'.
 * acting randomly on some fraction of steps, where the fraction decreases over time,
 *   we can dispense with keeping statistics about taken actions.
 *   
 * [1] Ganapathy 2009 "Utilization of Webots and the Khepera II as a Platform for Neural Q-Learning Controllers"
 * [2] http://people.revoledu.com/kardi/tutorial/ReinforcementLearning/Q-Learning-Matlab.htm
 * [3] Norvig
 * @author klaus
 *
 */

public class QLearning {

	private boolean showDebugMessages=false;
	private ArrayList<String> statesList;
	private ArrayList<String> actionsList;
	private String fileName="QTable.txt";
	private HashMap<String, HashMap<String,Double>> Q;


	private double e=0.1; //Probability of choosing the best action instead of a random one
	private double alpha=0.5; //Here, alpha is the learning rate parameter
	private double gamma=0.9; //discount factor
	private double b=0.95; // probability of random action choice deciding for the previous action instead of randomly choosing one from the action list
	//	private int statesCount,actionsCount;
	private String s="",a="",sl="",al="";
	private double reward=0;
	private Random r=new Random();

        /**
         * Default Constructor.
         */
	public QLearning(){
		statesList=new ArrayList<String>();
		actionsList=new ArrayList<String>();
		Q = new HashMap<String, HashMap<String,Double>>(); // Q learning
	}
        
        
       
        /**
         * This method set Q value with parameters Qval, state and action.
         * @param Qval
         * @param state
         * @param action 
         */
	public void setQ(double Qval, String state, String action){
		HashMap<String,Double> tempS=this.Q.get(state);
		if(tempS!=null){
			//This state already exists, So I have to check if it already contains this action
			if(tempS.get(action)!=null){
				//the action already exists, So I just update it to the new one
				tempS.put(action, Qval);
			}
			else{
				if(!actionsList.contains(action)){//TODO something wicked here. I shouldn't need to perform this test...
					actionsList.add(action);
				}
				tempS.put(action, Qval);				
			}
		}else{
			//this state doesn't exist yet, so I must create it and populate it with nActions-1 valued 0 and one action valued Qval
			HashMap<String,Double> tempNew= new  HashMap<String,Double>();
			tempNew.put(action, Qval);
			statesList.add(state);
			this.Q.put(state, tempNew);
		}
	}
        
        /**
        * Returns the utility value Q related to the given state/action pair
        * @param state
        * @param action
        * @return
        */
	public double getQ(String state,String action){
		double dQ=0;
		if(!(Q.get(state)==null || Q.get(state).get(action)==null)){
			dQ=Q.get(state).get(action);
		}
		return  dQ;
	}

        /**
        * Returns the maximum Q value for sl. 
        * @param sl
        * @return Q Value
        */
	public double maxQsl(String sl){
		double maxQinSl=0;
		String maxAl="";
		double val=0;
		if(this.Q.get(sl)!=null){
			HashMap<String,Double> tempSl=this.Q.get(sl);
			ArrayList<String> tempA=new ArrayList<String>();
			tempA.addAll(this.actionsList);

			// Finds out the action with maximum value for sl
			Iterator<Entry<String, Double>> it = tempSl.entrySet().iterator(); 

			while (it.hasNext()) { 
				Entry<String, Double> pairs = it.next(); 
				val= pairs.getValue(); 
				tempA.remove(pairs.getKey());
				if(val>maxQinSl){
					maxAl=pairs.getKey();
					maxQinSl=val;
				} 
			}
			if(!tempA.isEmpty() && maxQinSl<0){maxQinSl=0;} //Assigning 0 to unknown state/action pair
		}
		return maxQinSl;
	}

        /**
        * This methods is responsible for update the state.
        * @param stateIWas state I was previously
        * @param actionIDid action I did while at the previous state
        * @param rewardIGot reward I got after moving from previous state to the present one
        */
	public void update(String stateIWas,String actionIDid, double rewardIGot) {
		//which is calculated whenever action a is executed in state s leading to state s'
		this.sl=stateIWas;
		this.al=actionIDid;

		if(!a.equals("")&& !s.equals("")){
			//			if(!s.equals(sl)){//Updates only if state changes, is this correct?
			double Qas=this.getQ(s, a);
			double MaxQ=this.maxQsl(this.sl);
			double newQ= Qas  + alpha * (rewardIGot + gamma * MaxQ - Qas); //TODO  not sure if its reward or rewardIGot
			this.setQ(newQ, s, a);
			//				System.out.println("== Update ============");
			//				System.out.println("a: "+a+"  s: "+s+"  al: "+al+"  sl: "+sl+"  Qas: "+Qas+"  MaxQ: "+MaxQ+"  newQ: "+newQ);
			//				System.out.println("======================");
			//				this.printQ();
			//			}
		}

		a=this.al;
		s=this.sl;
		reward=rewardIGot;
	}


        /**
        * This print Q values.
        */
	public void printQ() {
		System.out.println("------ Printed Q -------");
		Iterator<Entry<String, HashMap<String, Double>>> itS = this.Q.entrySet().iterator(); 
		while (itS.hasNext()) { 
			Entry<String, HashMap<String, Double>> pairs = itS.next(); 			
			HashMap<String,Double> tempA = pairs.getValue(); 
			Iterator<Entry<String, Double>> itA = tempA.entrySet().iterator();
			double val=0;
			System.out.print("State("+pairs.getKey()+") actions: ");
			while(itA.hasNext()){
				Entry<String, Double> pairsA = itA.next();
				val=pairsA.getValue();
				System.out.print("["+pairsA.getKey()+": "+val+"] ");
			}			
			System.out.println("");
		} 

		System.out.println("----------------------------");
	}

        /**
         *  Store Q values to file using JSON structure.
         */
	public void storeQ(){
		String textQ="";
		//		JSONArray actionValueArray=new JSONArray();
		JSONObject actionValuePair = new JSONObject();

		JSONObject actionsStatePair = new JSONObject();
		//		JSONArray statesArray= new JSONArray();
		try {
			Iterator<Entry<String, HashMap<String, Double>>> itS = this.Q.entrySet().iterator(); 
			while (itS.hasNext()) { 
				Entry<String, HashMap<String, Double>> pairs = itS.next(); 			
				HashMap<String,Double> tempA = pairs.getValue(); 
				Iterator<Entry<String, Double>> itA = tempA.entrySet().iterator();
				double val=0;
				//				System.out.print("State("+pairs.getKey()+") actions: ");
				actionValuePair=new JSONObject();
				while(itA.hasNext()){
					Entry<String, Double> pairsA = itA.next();
					val=pairsA.getValue();
					actionValuePair.put(pairsA.getKey(), val);
				}		
				//				System.out.println(actionsStatePair+" "+pairs.getKey()+" "+actionValuePair);
				actionsStatePair.put(pairs.getKey(),actionValuePair);
			} 

		} catch (JSONException e) {e.printStackTrace();}

		//use buffering
		Writer output;
		try {
			output = new BufferedWriter(new FileWriter(fileName));

			try {
				//FileWriter always assumes default encoding is OK!
				output.write( actionsStatePair.toString() );
			}
			finally {
				output.close();
			}

		} catch (IOException e) {e.printStackTrace();}


		//		System.out.println("------ Stored Q -------");
		//		System.out.println("Q: "+actionsStatePair.toString());
		//		System.out.println("----------------------------");
	}

        /**
         *  Recover Q values from file in JSON structure.
         */
	public void recoverQ(){
		//...checks on aFile are elided
		StringBuilder contents = new StringBuilder();

		try {
			//use buffering, reading one line at a time
			//FileReader always assumes default encoding is OK!
			BufferedReader input  =  new BufferedReader(new FileReader(fileName));
			try {
				String line = null; //not declared within while loop
				/*
				 * readLine is a bit quirky :
				 * it returns the content of a line MINUS the newline.
				 * it returns null only for the END of the stream.
				 * it returns an empty String if two newlines appear in a row.
				 */
				while (( line = input.readLine()) != null){
					contents.append(line);
					//contents.append(System.getProperty("line.separator"));
				}
			}
			finally {
				input.close();
			}
		}
		catch (IOException ex){
			ex.printStackTrace();
		}


		//		actionValuePair.put(pairsA.getKey(), val);
		//	}			

		//		System.out.println("contents: "+contents.toString());
		JSONObject actionsStatePairs;
		try {
			actionsStatePairs = new JSONObject(contents.toString());
			//			System.out.println("actionsStatePairs.toString(): "+actionsStatePairs.toString());


			Iterator itS = actionsStatePairs.keys(); 
			while (itS.hasNext()) { 
				String state=itS.next().toString();
				//				System.out.println("itS.next(): "+state);
				JSONObject pairAS =  (JSONObject) actionsStatePairs.get(state); 	

				Iterator itA = pairAS.keys();
				while(itA.hasNext()){
					String action=itA.next().toString();

					double value = pairAS.getDouble(action);

					this.setQ(value, state, action);

				}
			}

		} catch (JSONException e1) {

			e1.printStackTrace();
		} 

	}
        
        /**
         * Clear Q values.
         */
	public void clearQ(){
		this.Q.clear();
	}

        
        /**
         * Gets alpha value.
         * @return 
         */
	public double getAlpha() {
		return alpha;
	}


        /**
        *  Sets the learning rate parameter alpha.
        *  Should be between 0 and 1
        * @param alpha
        */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
            
        /**
         * Gets gamma value.
         * @return 
         */
	public double getGamma() {
		return gamma;
	}
        
        
        /**
        * Sets the discount factor.
        * Should be between 0 and 1.
        * @param gamma
        */
	public void setGamma(double gamma) {
		this.gamma = gamma;
	}
	
        /**
	 * Selects the best action for this state with probability "e", 
	 * and a random one with probability (1-e) 
	 *  If a given state has no record of one or more actions, it will consider them as valued 0.
	 * @param state
	 * 
         * @return selectedAction
	 */
	public String getAction(String state){//TODO should improve this. It should consider all non explored actions as being equally 0 for all purposes
		//		System.out.println("Inside get action");
		String selectedAction=null;
		if(r.nextDouble()<=e){ //TODO Use boltzmann distribution here?
			//			if(ql.getAction(stringState)!=null){
			//				action=ql.getAction(stringState);//
			//-----

			if(this.Q.get(state)!=null){
				ArrayList<String> actionsLeft=new ArrayList<String>();
				actionsLeft.addAll(this.actionsList);
				HashMap<String, Double> actionsQ =	this.Q.get(state);
				double bestQval=-Double.POSITIVE_INFINITY;
				Iterator<Entry<String, Double>> it = actionsQ.entrySet().iterator();

				while(it.hasNext()){
					Entry<String,Double> pairs =  it.next(); 			
					double qVal = pairs.getValue(); 
					String qAct=pairs.getKey();
					if(qVal>bestQval){
						bestQval=qVal;
						selectedAction=qAct;
					}
					actionsLeft.remove(qAct);
				}
				if((bestQval<0)&&(actionsLeft.size()>0)){
					//this means we should randomly choose from the other actions;
					selectedAction=selectRandomAction(actionsLeft);
				}
				if(showDebugMessages){System.out.println("Selected the best available action.");}
			}else{
				//				System.out.println("Inside else null");
				//					selectedAction=null;
				selectedAction=selectRandomAction(actionsList);
				if(showDebugMessages){System.out.println("Selected a random action because there was no available suggestion.");}
			}		


			//			}else{
			//				action=selectRandomAction();
			//			}
		}else{
			if(showDebugMessages){System.out.println("Naturally selected a random action.");}
			selectedAction=selectRandomAction(actionsList);
		}
		return selectedAction;
	}


	/**
         * Gets states list.
	 * @return the statesList
	 */
	public ArrayList<String> getStatesList() {
		return statesList;
	}


	/**
         * Sets action list.
	 * @param statesList the statesList to set
	 */
	public void setStatesList(ArrayList<String> statesList) {
		this.statesList = statesList;
	}


	/**
         * Gets the action list.
	 * @return the actionsList
	 */
	public ArrayList<String> getActionsList() {
		return actionsList;
	}


	/**
         * This sets action list.
	 * @param actionsList the actionsList to set
	 */
	public void setActionsList(ArrayList<String> actionsList) {
		this.actionsList=new ArrayList<String>();
		this.actionsList.addAll(actionsList);
	}


	/**
         * Gets E value.
	 * @return e
	 */
	public double getE() {
		return e;
	}


	/**
	 * Sets the chances of getting the best possible action.
	 * With e=0.9 for instance, there is a .9 chance of getting the best action for the given state, and .1 probability of getting a random action.
	 * 
	 * @param e the e to set
	 */
	public void setE(double e) {
		this.e = e;
	}

        /**
         * Gets all actions from state.
         * @param state
         * @return actons
         */
	public String getAllActionsFromState(String state){
		String actions="";
		if(this.Q.get(state)!=null){
			HashMap<String, Double> actionsH=this.Q.get(state);

			Iterator<Entry<String, Double>> it = actionsH.entrySet().iterator();

			while(it.hasNext()){
				Entry<String, Double> pairs =  it.next(); 			
				double qVal = (Double) pairs.getValue(); 
				String act = (String) pairs.getKey();
				actions=actions+"{"+act+":"+qVal+"} ";

			}

		}else{
			actions="{}";
		}

		return actions;		

	}
        
        /**
         * Select randomically a action.
         * @param localActionsList
         * @return actionR 
         */
	private String selectRandomAction(ArrayList<String> localActionsList) {
		String actionR=this.a;
		double pseudoRandomNumber=r.nextDouble();

		if((pseudoRandomNumber>=b)||actionR==null||actionR.equals("")){
			int	actionI=r.nextInt(localActionsList.size());
			actionR=localActionsList.get(actionI);
		}

		//		System.out.println("INSIDE RANDOM: "+actionR);

		return  actionR;//TODO should I use boltzman distribution?



		/*
		 * Simulating a Pareto random variable. The Pareto distribution is often used to model 
		 * insurance claims damages, financial option holding times, and Internet traffic activity.
		 *  The probability that a Pareto random variable with parameter a is less than x is
		 *   F(x) = 1 - (1 + x)-a for x >= 0. To generate a random deviate from the distribution,
		 *    use the inverse function method: output (1-U)-1/a - 1, where U is a uniform random number between 0 and 1.
		 */


	}
	/**
	 * This  method "maxwellBoltzmann()" returns a pseudo-random value from a Maxwell-Boltzmann distribution
	 * with parameter sigma. Take the sum of the squares of three gaussian random variables 
	 * with mean 0, and standard deviation sigma, and return the square root.
	 * double  e = random.nextGaussian();  // Gaussian with mean 0 and stddev = 1
	 * 
	 * @return sum
	 */
	public double maxwellBoltzmann(){
		double sum=0;
		sum=sum+Math.pow(r.nextGaussian(),2);
		sum=sum+Math.pow(r.nextGaussian(),2);
		sum=sum+Math.pow(r.nextGaussian(),2);

		sum=Math.sqrt(sum);

		return sum;

	}

	/**
         * Gets B value.
	 * @return the b
	 */
	public double getB() {
		return b;
	}


	/**
         * Sets B value.
	 * @param b the b to set
	 */
	public void setB(double b) {
		this.b = b;
	}


	/**
         * Gets S value.
	 * @return the s
	 */
	public String getS() {
		return s;
	}


	/**
         * Sets S value.
	 * @param s the s to set
	 */
	public void setS(String s) {
		this.s = s;
	}


	/**
         * Gets A value.
	 * @return the a
	 */
	public String getA() {
		return a;
	}


	/**
         * Sets A value.
	 * @param a the a to set
	 */
	public void setA(String a) {
		this.a = a;
	}


	/**
         * Gets SL value.
	 * @return the sl
	 */
	public String getSl() {
		return sl;
	}


	/**
         * Sets SL value.
	 * @param sl the sl to set
	 */
	public void setSl(String sl) {
		this.sl = sl;
	}


	/**
         * Gets the AL value.
	 * @return the al
	 */
	public String getAl() {
		return al;
	}


	/**
         * Sets AL value.
	 * @param al the al to set
	 */
	public void setAl(String al) {
		this.al = al;
	}

        
        /**
         * Gets all Q values.
         * @return 
         */
	public HashMap getAllQ() {

		return this.Q;
	}
}





