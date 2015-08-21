/*******************************************************************************
 * Copyright (c) 2012 K. Raizer, A. L. O. Paraense, R. R. Gudwin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     K. Raizer, A. L. O. Paraense, R. R. Gudwin - initial API and implementation
 ******************************************************************************/
package br.unicamp.cst.core.entities;

import java.util.ArrayList;
import java.util.List;

import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

/**
 * 
 * Following Hofstadter and Mitchell "The copycat project: A model of mental fluidity and analogy-making". Pool of all alive codelets in the system. The whole arena in the Baars-Franklin metaphor.
 * 
 * @author andre.paraense
 * @author klaus.raizer
 * 
 */
public class CodeRack
{
	boolean trueThread=true;
	/**
	 * @return If it is a trueThread of a pseudo thread
	 */
	public boolean isTrueThread() {
		return trueThread;
	}

	/**
	 * This should be set only from within CodeRack.
	 * @param trueThread the trueThread to set
	 */
	public void setTrueThread(boolean trueThread) {
		this.trueThread = trueThread;

	}
	/**
	 * List of all alive codelets in the system
	 */
	private List<Codelet> allCodelets;

	/**
	 * Singleton instance
	 */
	private static CodeRack instance;

	//private static DeadLockDetector dd;

	/**
	 * Default constructor
	 */
	private CodeRack()
	{
		allCodelets = new ArrayList<Codelet>();
	}

	/**
	 * 
	 * @return the singleton instance of Coderack
	 */
	public synchronized static CodeRack getInstance()
	{
		if (instance == null)
		{
			instance = new CodeRack();
			//Starts a deadlock detector
//			dd=new DeadLockDetector();
//			dd.start();
		}
		return instance;
	}

//	public synchronized static void StopDeadlockDetector(){
//		dd.stop();
//	}
//	public synchronized static void StartDeadlockDetector(){
//		dd.start();
//	}
//	
	/**
	 * 
	 * Broadcast a content to all Codelets.
	 * 
	 * @param listOfData
	 *           Data to be broadcasted
	 */
	public void broadcastToCodelets(List<byte[]> listOfData)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 * @return the allCodelets
	 */
	public List<Codelet> getAllCodelets()
	{
		return allCodelets;
	}

	/**
	 * @param allCodelets
	 *           the allCodelets to set
	 */
	public void setAllCodelets(List<Codelet> allCodelets)
	{
		this.allCodelets = allCodelets;
	}

	/**
	 * Adds a new Codelet to the Coderack
	 * 
	 * @param co
	 *           codelet to be added
	 */
	private void addCodelet(Codelet co)
	{
		allCodelets.add(co);
	}

	/**
	 * Creates a codelet and adds it to this coderack
	 * 
	 * @param co
	 *           codelet to be created
	 * @return
	 */
	public Codelet insertCodelet(Codelet co)
	{

		addCodelet(co);

		return co;
	}

	/**
	 * Creates a codelet and adds it to this coderack
	 * 
	 * @param activation codelet's activation
	 * @param broadcast list of memory objects which were broadcast lately (teated as input memory objects)
	 * @param inputs list of input memory objects
	 * @param outputs list o output memory objects
	 * @param co codelet to be created
	 * @return the codelet created
	 */
	public Codelet createCodelet(double activation, List<MemoryObject> broadcast, List<MemoryObject> inputs, List<MemoryObject> outputs, Codelet co)
	{
		try 
		{
			co.setActivation(activation);
		} catch (CodeletActivationBoundsException e) 
		{
			e.printStackTrace();
		}
		co.setBroadcast(broadcast);
		co.setInputs(inputs);
		co.setOutputs(outputs);
		addCodelet(co);
		return co;
	}
	/**
	 * removes a codelet from coderack
	 * @param co
	 */
	public void destroyCodelet(Codelet co) {
		co.stop();
		this.allCodelets.remove(co);

	}
	private int standardTimeStep=100;

	private boolean should_loop=true;
/**
 * Destroys all codelets. Stops CodeRack's thread.
 */
	public void shutDown(){
//		this.stop();
		for(Codelet co: this.getAllCodelets()){
			co.stop();
		}
//		dd.stop();//stops deadlock detector
		this.allCodelets.clear();
		this.should_loop=false;
	}

	/**
	 * This method either loops for a given timeStep, in the case of using true Threads. Or pools the coderack, calling run(), in the case of pseudo threads
	 */
	public void loop(int timeStep){
		this.standardTimeStep=timeStep;


		while(should_loop){//Puts Main Thread to sleep for a few milliseconds (could very well be zero)
			if(!this.isTrueThread()){
				for(Codelet co:this.allCodelets){
					co.run();
				}
			}
			try {Thread.currentThread().sleep(timeStep);} catch (InterruptedException e) {e.printStackTrace();}
		}


	}

	/**
	 * @return the timeStep
	 */
	public int getTimeStep() {
		return standardTimeStep;
	}

	/**
	 * @param timeStep the timeStep to set
	 */
	public void setTimeStep(int timeStep) {
		this.standardTimeStep = timeStep;
	}
	/**
	 * Starts all codelets in coderack with true threads, and keeps the main thread alive with a standard 100 ms loop
	 */
	public void start() {
		for(Codelet co: this.getAllCodelets()){
			co.start();
		}

		this.loop(this.standardTimeStep);
	}
	/**
	 * Stops all codelets within CodeRack, but keeps CodeRack running.
	 */
	public void stop() {
		for(Codelet co: this.getAllCodelets()){
			co.stop();
		}
	}	
	
	/**
	 * Starts all codelets with true threads if isTrueThread = true and keep the main thread alive with a standard time step of 100ms loop.
	 * If isTrueThread=false, it loops through all codelets executing their proc(), the system would then have a single thread.
	 * @param isTrueThread
	 */
	public void start(boolean isTrueThread) {
		this.setTrueThread(isTrueThread);

		if(this.isTrueThread()){
			for(Codelet co: this.getAllCodelets()){
				co.setTrueThread(isTrueThread);
				co.start();
			}
		}

		this.loop(this.standardTimeStep);
	}

	/**
	 * Starts all codelets with true threads if isTrueThread = true and keep the main thread alive with the given time step loop.
	 * If isTrueThread=false, it loops through all codelets executing their proc(), the system would then have a single thread.
	 * 
	 * @param isTrueThread
	 * @param givenTimeStep
	 */
	public void start(boolean isTrueThread, int givenTimeStep) {
		this.setTrueThread(isTrueThread);
		this.setTimeStep(givenTimeStep);

		for(Codelet co: this.getAllCodelets()){
			co.setTrueThread(this.isTrueThread());
			if(this.isTrueThread()){
				co.start();
			}
		}

		this.loop(this.standardTimeStep);
	}

}
