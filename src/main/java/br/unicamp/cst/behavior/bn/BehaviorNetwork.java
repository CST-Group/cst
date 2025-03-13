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


import java.util.ArrayList;
import java.util.Iterator;



import br.unicamp.cst.core.entities.CodeRack;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.memory.WorkingStorage;

/**
 * Implementation of a Behavior network as described in [Maes 1989] "How to do the Right Thing"
 * The behavior network controls an artificial agent, and is essentially a list of competence modules.
 * 
 * @author klaus
 */

public class BehaviorNetwork 
{

	private ArrayList<Behavior> behaviors=new ArrayList<Behavior>(); //List of Competence codelets
	//TODO this list of all available competences should be given to the consciousness module so it can return a coalition list of relevant codelets
	private ArrayList<Behavior> coalition = new ArrayList<Behavior>(); //List of conscious Competence codelets
	//TODO the list of conscious codelets is a subset of all competences, and is formed by the coalition manager. This is the list passed to all consciouss codelets
	private boolean singleCodeletBN=false; //if set true, this behavior network starts a single thread to take care of executing all behaviors, instead of one thread for each one.

	private BehaviorsWTA kwta=null;
	
	private Codelet monitor=null;
	
	private CodeRack codeRack;
	
	private WorkingStorage ws;
	
	private GlobalVariables globalVariables;

	public BehaviorNetwork(CodeRack codeRack,WorkingStorage ws)
	{
		this.ws=ws;
		
		globalVariables = new GlobalVariables();
		
		if(codeRack!=null)
		{
			this.codeRack = codeRack;
			kwta = (BehaviorsWTA) codeRack.insertCodelet(new BehaviorsWTA(globalVariables));
		}
		
	}
        
        

	
	/**
	 *  Starts all competences threads
	 */
	public void startCodelets() 
	{
		if(codeRack!=null)
		{
			if(!singleCodeletBN)
			{
				for(Codelet oneCompetence:this.behaviors)
				{
					oneCompetence.start();
				}
			}else{
				SingleThreadBHCodelet singleCodelet = new SingleThreadBHCodelet(this.behaviors);
				singleCodelet.setTimeStep(singleCodelet.getTimeStep()*this.behaviors.size()); // so the won't get more processing time than other codelets
				singleCodelet.start();
			}

		}
	}

	/**
	 *  Stops all competences threads
	 */
	public void stopCompetences() {
		for(Codelet oneCompetence:this.behaviors){
			oneCompetence.stop();
		}
	}	

	/**
	 * @param arrayList the consciousCompetences to set
	 */
	public void setCoalition(ArrayList<Behavior> arrayList) {
		this.coalition = arrayList;
		//TODO implement lock here?
		// Forwards the information of current coalition to all codelets
		Iterator itr = this.coalition.iterator(); 
		while(itr.hasNext()) {
			Behavior competence = (Behavior) itr.next(); //TODO este cast pode ser desnecessário
			synchronized(competence){
				competence.setCoalition(this.coalition);
			}


		} 

	}
	/**
	 * Passes to each behavior codelet the link to all the others 
	 */
	public void setBehaviorsInsideCodelets() {
		//TODO implement lock here
		// Forwards the information of current coalition to all codelets

		Iterator itr = this.behaviors.iterator(); 
		while(itr.hasNext()) {
			Behavior competence = (Behavior) itr.next(); //TODO este cast pode ser desnecessario

			synchronized(competence){
				competence.setBehaviors(this.behaviors);
			}


		} 

	}

	/**
	 * @return the consciousCompetences
	 */
	public ArrayList<Behavior> getCoalition() {
		return coalition;
	}


	/**
	 * @return the competences
	 */
	public ArrayList<Behavior> getBehaviors() {
		return behaviors;
	}



	/**
	 * @param codelet the competences to set
	 */
	public void addCodelet(Codelet codelet) 
	{
		//Every new godelet's input list gets registered at working storage for WORLD_STATE memory objects
		if(ws!=null)
			ws.registerCodelet(codelet, "WORLD_STATE",0); //TODO How about putting this inside Behavior.java?
		Behavior be = (Behavior)codelet;
		this.behaviors.add(be);
		kwta.addBehavior(be);
		
		setBehaviorsInsideCodelets();
	}
	

	/**
	 * @param codelet the competences to set
	 */
	public void removeCodelet(Codelet codelet) 
	{
		if(ws!=null)
			ws.unregisterCodelet(codelet, "WORLD_STATE",0); //TODO How about putting this inside Behavior.java?
		Behavior be = (Behavior)codelet;
		this.behaviors.remove(be);
		kwta.removeBehavior(be);
		
		setBehaviorsInsideCodelets();
	}

	/**
	 * @return the singleCodeletBN
	 */
	public boolean isSingleCodeletBN() {
		return singleCodeletBN;
	}

	/**
	 * @param singleCodeletBN the singleCodeletBN to set
	 */
	public void setSingleCodeletBN(boolean singleCodeletBN) {
		this.singleCodeletBN = singleCodeletBN;
	}
	/**
	 * Defines whether or not if the behaviors in this BN must have their activations reset to zero after being activated.
	 * @param val
	 */
	public void setBehaviorsToZeroWhenActivated(boolean val){
		for(Behavior be: this.behaviors){
			be.setSetToZeroWhenActivated(val);
		}
	}

// The following code was commented due to use a graphics tool to show the state of the BehaviorNetwork. 
// This should be moved out from this class and be used within the cst-utils project        

//	/**
//	 * Creates a new graphic, showing all behaviors and its activations along time. And destroys any previous running graphics of this instance.
//	 */
//	public void showGraphics()
//	{
//		if(codeRack!=null)
//		{
//			if(monitor!=null)
//			{			
//				codeRack.destroyCodelet(monitor);
//			}
//			monitor = codeRack.insertCodelet(new BHMonitor(this));
//			monitor.start();
//		}
//	}
//	
//	public void showGraphics(ArrayList<String> behaviorsIWantShownInGraphics) 
//	{
//		if(codeRack!=null)
//		{
//			if(monitor!=null)
//			{
//				codeRack.destroyCodelet(monitor);
//			}
//			monitor = codeRack.insertCodelet(new BHMonitor(this,behaviorsIWantShownInGraphics,globalVariables));
//			monitor.start();
//		}
//	}        
//        /**
//	 * Plots a graph with all behaviors. 
//	 * Simple arrows denote activation connections 
//	 * whilst circled arrows illustrate inhibitive connections.
//	 */
//	public void plotBN() {
//		BNplot bnPlot = new BNplot(this.getBehaviors());
//		
//		bnPlot.plot();
//		
//	}
//	
//	/**
//	 * Plots a graph with only the given behaviors. 
//	 * Simple arrows denote activation connections 
//	 * whilst circled arrows illustrate inhibitive connections.
//	 */
//	public void plotBN(ArrayList<String> behaviorsIWantShownInGraphics) {
//		ArrayList<Behavior> beIWannaShow=new ArrayList<Behavior>();
//		for(Behavior be:this.getBehaviors()){
//			if(behaviorsIWantShownInGraphics.contains(be.getName())){
//				beIWannaShow.add(be);
//			}
//		}
//		
//		
//		BNplot bnPlot = new BNplot(beIWannaShow);
//		
//		bnPlot.plot();
//		
//	}
	
	


}
