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
package br.unicamp.cst.behavior.bn;
/**
 * 
 * @author klauslocal
 *
 */

public class GlobalVariables 
{
	//Global variables 
	private volatile double theta; // Threshold for becoming active
	private volatile double thetaTemp; //Threshold for becoming active that starts as theta but gets reduced by a percentage in case no behavior is selected
	private volatile double pi; // mean level of activation
	private volatile double phi; //amount of activation energy a proposition that is observed to be true injects into the network
	private volatile double gamma;//amount of activation energy a goal injects into the network
	private volatile double delta; //amount of activation energy a protected goal takes away from the network
	private volatile double decay; //amount of energy that is naturally lost by the behavior at each iteration
	//Debug variables
	private volatile boolean worldStateInHashMap;//defines if we should use a single list for all objects in working storage (false) or a HashMap of lists (true)
	private double decreaseRate; //Fraction of decrease suffered by theta coming from each behavior codelet

	/**
	 * Default Constructor
	 */
	public GlobalVariables()
	{ 
		//TODO how about automatically defining these variables with an optimization algorithm?
		//Initial states
		this.theta=1; 
		this.thetaTemp=this.theta;
		this.pi=0.020; 
		this.phi=0.05;
		this.gamma=0.2;
		this.delta=0.050; 
		this.decay=1;
		//DebugVariables
		this.decreaseRate=0.01;
		this.worldStateInHashMap=true;
	}
	public synchronized boolean isWorldStateInHashMap() 
	{
		return worldStateInHashMap;
	}

	public synchronized void setWorldStateInHashMap(boolean worldStateInHashMap) 
	{
		this.worldStateInHashMap = worldStateInHashMap;
	}


	/**
	 * 
	 * Avoids cloning
	 */
	public Object clone() throws CloneNotSupportedException{
		throw new CloneNotSupportedException();
	}

	/**
	 * amount of activation energy a goal injects into the network
	 * @return the gamma
	 */
	public synchronized double getGamma() {
		return gamma;
	}

	/** amount of activation energy a goal injects into the network
	 * @param gamma the gamma to set
	 */
	public synchronized void setGamma(double gamma) {
		this.gamma = gamma;
	}

	/**
	 * threshold for becoming active
	 * 
	 * @return the theta
	 */
	public synchronized double getTheta() {
		return theta;
	}
	/**
	 * threshold for becoming active
	 * 
	 * @param theta the theta to set
	 */
	public synchronized void setTheta(double theta) {
		this.theta = theta;
	}
	/**
	 * mean level of activation
	 * @return the pi
	 */
	public synchronized double getPi() {
		return pi;
	}
	/**
	 * mean level of activation
	 * @param pi the pi to set
	 */
	public synchronized void setPi(double pi) {
		this.pi = pi;
	}
	/**
	 * amount of activation energy a proposition that is observed to be true injects into the network
	 * @return the phi
	 */
	public synchronized double getPhi() {
		return phi;
	}
	/**
	 * amount of activation energy a proposition that is observed to be true injects into the network
	 * @param phi the phi to set
	 */
	public synchronized void setPhi(double phi) {
		this.phi = phi;
	}

	/**
	 * amount of activation energy a protected goal takes away from the network
	 * @return the delta
	 */
	public synchronized double getDelta() {
		return delta;
	}
	/**
	 * amount of activation energy a protected goal takes away from the network
	 * @param delta the delta to set
	 */
	public synchronized void setDelta(double delta) {
		this.delta = delta;
	}
	/**
	 * threshold for becoming active that starts as theta but gets reduced by a percentage in case no behavior is selected
	 * @return the thetaTemp
	 */
	public synchronized double getThetaTemp() {
		return thetaTemp;
	}
	/**
	 * threshold for becoming active that starts as theta but gets reduced by a percentage in case no behavior is selected
	 * @param thetaTemp the thetaTemp to set
	 */
	public synchronized void setThetaTemp(double thetaTemp) {
		this.thetaTemp = thetaTemp;
	}

	/**
	 * amount of energy that is naturally lost by the behavior at each iteration
	 * 
	 * @return the decay
	 */
	public double getDecay() {
		return this.decay;
	}

	/**
	 * amount of energy that is naturally lost by the behavior at each iteration
	 * @param decay
	 */
	public void setDecay(double decay) {
		this.decay = decay;
	}
	/**
	 * Fraction of decrease suffered by theta coming from each behavior codelet
	 * @return the decrease rate
	 */
	public synchronized double getThetaTempDecreaseRate() {
		return this.decreaseRate;
	}
	/**
	 * Fraction of decrease suffered by theta (temp theta) 
	 * @param decreaseRate the decreaseRate to set
	 */
	public synchronized void setThetaTempDecreaseRate(double decreaseRate) {
		this.decreaseRate = decreaseRate;
	}

	/**
	 * Decreases the values of used thetas by multiplying them by a certain fraction in case no behaviour is used
	 */
	public synchronized void decreaseThetaTemps()
	{
		this.setThetaTemp(this.getThetaTemp()*this.getThetaTempDecreaseRate());
	}
        
        public enum Goals {
            ALL_GOALS, PROTECTED_GOALS
        }
        
        public enum ListType {
            DELETE, ADD, PRECONDITION
        }

}
