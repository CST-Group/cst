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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.cst.memory.WorkingStorage;

/**
 * This competence class extends Codelet. "A competence resemble the operators of a classical planning system. A competence module i can be described by a list of preconditions and expected effects."[Maes 1989]
 * 
 * @author klaus
 * 
 */
public abstract class Behavior extends Codelet
{

	//Debug variables
	private boolean showActivationSpread = false; // Enable this to see the activation flow in the console
	private boolean showCoalitionLinks = false; // Enable this to see the coalition links in the console
	private boolean printNoActivationCases = false;//Enable this to see if there are any problems with absent preconditions

	private String name = ""; //Each behavior must have a name.  If not given by the user, it will be the same as its running thread's one
	protected ArrayList<String> actionList = new ArrayList<String>();// Actions that are to be performed by actuators and which constitute this behavior
	protected JSONArray jsonActionList = new JSONArray();

	protected ArrayList<Memory> preconList = new ArrayList<Memory>(); // ci list of preconditions that must be fulfilled before the competence module can become active
	protected ArrayList<Memory> addList = new ArrayList<Memory>(); // ai expected effects of this action in terms of an add list
	protected ArrayList<Memory> deleteList = new ArrayList<Memory>(); // di expected effects of this action in terms of a delete list
	//TODO I'm modifying MAES original model to encompass a "soft" precondition list. Is is "soft" in the sense that it is not a must have.
	// a soft precondition affects activation spread in the same way a traditional precondition does, but its absense does not prevent the behavior to be executed.	
	protected ArrayList<Memory> softPreconList = new ArrayList<Memory>(); // ci list of preconditions that are desirable to be fulfilled before the competence module can become active

	// Alpha level of activation is the codelet's own A (activation level) [Hypothesis to be investigated]
	protected ArrayList<Behavior> allBehaviors = new ArrayList<Behavior>();//Pointers to all behaviors in the network. Basal ganglia should support this hypothesis. 
	protected ArrayList<Behavior> coalition = new ArrayList<Behavior>(); //A subset of all behaviors, given by consciousness.

	protected Hashtable<Behavior, ArrayList<Memory>> predecessors = new Hashtable<Behavior, ArrayList<Memory>>();
	protected Hashtable<Behavior, ArrayList<Memory>> successors = new Hashtable<Behavior, ArrayList<Memory>>();
	protected Hashtable<Behavior, ArrayList<Memory>> conflicters = new Hashtable<Behavior, ArrayList<Memory>>();

	private ArrayList<Memory> permanentGoals = new ArrayList<Memory>();
	private ArrayList<Memory> onceOnlyGoals = new ArrayList<Memory>();
	private ArrayList<Memory> protectedGoals = new ArrayList<Memory>();
	private ArrayList<Memory> goals = new ArrayList<Memory>();
	private ArrayList<Memory> worldState = new ArrayList<Memory>();
	private ArrayList<Object> listOfWorldBeliefStates=new ArrayList<Object>();
	private ArrayList<Object> listOfPreviousWorldBeliefStates=new ArrayList<Object>();

	private GlobalVariables globalVariables; //Behavior network global variables

	private boolean executable; //Defines if this behavior is executable or not
	private boolean active; //Defines if this behavior is active at the moment
	private boolean firstTime; // Checks if this behaviour is trying to perform actions for the first time since it got active
	private double maxA=1; // maximum activation for normalization
	
	//TODO resourceList is a list of all the action buffers this behavior needs to complete its actions.
	private ArrayList<String> resourceList=new ArrayList<String>();
	private boolean setToZeroWhenActivated=true;
	private double activationMA=0;
	
	private WorkingStorage ws;

	public Behavior(WorkingStorage ws,GlobalVariables globalVariables)
	{
		this.ws = ws;
		this.globalVariables = globalVariables;
		
		//All behaviors subscribe their input list to receive news from Working Storage about WORLD_STATE, and goals memory objects.
		int io=0; //Input list
		ws.registerCodelet(this, "WORLD_STATE", io);
		ws.registerCodelet(this, "ONCE_ONLY_GOAL", io);
		ws.registerCodelet(this, "PERMANENT_GOAL", io);
		ws.registerCodelet(this, "PERMANENT_GOAL", io);

		this.setExecutable(false); // Every competence starts as non-executable
		this.setActive(false); // Every competence starts as inactive
		try 
		{
			setActivation(0);
		} catch (CodeletActivationBoundsException e) 
		{
			e.printStackTrace();
		}// Initial activation should be zero;
		
		this.setFirstTime(true);

		if(this.getName().equals("")||this.getName().equals(null)){
			this.setName( this.getClass().getName()); //If the user did not set a name, lets use the name of the Class
		}

		Thread.currentThread().setName(this.getName());// If this behavior has a name, let's rename this thread's name
		
	}

	/**
	 * Actions to be performed by the behavior net. Must be implemented by the user, or learned.
	 */
	public abstract void operation();

	/**
	 * This proc method runs the behavior network's cycle. And performs the actions described in operation() once this competence is activated.
	 */
	public void proc()
	{

		retrieveGoals(); // This should be done often because goals might change over time
		retrieveState(); // This should be done often because world state might change over time
		spreadActivation();
		checkIfExecutable();

		if (isActive()){

			operation(); // operation this behavior should perform
		}


	}


	/**
	 * 
	 * @return list of resources used by this behavior
	 */
	private ArrayList<String> getResourceList() {//TODO must develop this idea further


		return this.resourceList;
	}
	/**
	 * @return the actionsSet
	 */
	public String getActionList()//TODO a simpler version of this might be interesting
	{
		//		return actionList;
		try {
			JSONArray teste = new JSONArray(jsonActionList.toString());

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return jsonActionList.toString();
	}
	/**
	 * retrieves the lists of world and self states from working storage through inputs
	 */
	private synchronized void retrieveState()
	{
		worldState.clear();
		String moType;
		for (Memory mo : getInputs())
		{
			if(mo!=null){
				moType = mo.getName();
				if (moType.equalsIgnoreCase("WORLD_STATE"))

				{
					worldState.add(mo);
				}
			}
		}
		//		System.out.println("====> "+this.getId()+" worldState: "+worldState);

	}

	/**
	 * Updates the links lists based on conscious codelets from the network
	 * 
	 * "There is a successor link from competence module x to competence module y (x has y as successor) for every proposition p that is a member of the add list of x and also member of the precondition list of y (there can be more than one successor link between 2 competence modules)"
	 * 
	 * "A predecessor link from module x to module y (x has y as predecessor) exists for every successor link from y to x."
	 * 
	 * "There is a conflicter link from module x to module y (y conflicts with x) for every proposition p that is a member of the delete list of y and a member of the precondition linst of x" [Maes 1989]
	 */
	private void updateLinks()//TODO it would be best if we avoided accessing other codelet's inner states directly.  How about doing all this using broadcast?
	{
		successors.clear();
		predecessors.clear();
		conflicters.clear();

//		if(this.getId().contains("TV")){
//			int a=4;
//			System.out.println(this.getId());
//			
//		}
		

		ArrayList<Memory> intersection = new ArrayList<Memory>();
		for (Behavior competence : coalition)
		{
//			if(competence.getId().contains("ROOM13")){
//				int a=4;
//				System.out.println(competence.getId());
//				
//			}
			if (impendingAccess(competence))
			{
				try
				{
					if (competence != this)
					{
						//------
						intersection = getIntersectionSet(this.getAddList(), competence.getListOfPreconditions()); 
						if (!intersection.isEmpty())
						{
							this.successors.put(competence, intersection);
						}

						intersection = getIntersectionSet(this.getAddList(), competence.getSoftPreconList()); 
						if (!intersection.isEmpty())
						{
							this.successors.put(competence, intersection);
						}
						//------
						intersection = getIntersectionSet(this.getListOfPreconditions(), competence.getAddList());
						if (!intersection.isEmpty())
						{
							this.predecessors.put(competence, intersection);
						}

						intersection = getIntersectionSet(this.getSoftPreconList(), competence.getAddList());
						if (!intersection.isEmpty())
						{
							this.predecessors.put(competence, intersection);
						}
						//------
						intersection = getIntersectionSet(competence.getListOfPreconditions(), this.getDeleteList()); // because we are looking for how this module messes up others preconditions, and thats why it must be inhibited by them
						if (!intersection.isEmpty())
						{
							this.conflicters.put(competence, intersection);
						}
						intersection = getIntersectionSet(competence.getSoftPreconList(), this.getDeleteList()); // because we are looking for how this module messes up others preconditions, and thats why it must be inhibited by them
						if (!intersection.isEmpty())
						{
							this.conflicters.put(competence, intersection);
						}
					}
				} finally
				{
					lock.unlock();
					competence.lock.unlock();
				}
			}
		}
	}

	/**
	 * Retrieves the lists of goals from working storage
	 */
	private void retrieveGoals()
	{
		onceOnlyGoals.clear();
		permanentGoals.clear();
		protectedGoals.clear();
		String moType;

		ArrayList<Memory> my_inputs=new ArrayList<Memory>();
		my_inputs.addAll(getInputs());
		for (Memory mo : my_inputs)
		{
			if(mo!=null){
				moType = mo.getName();
				if (moType.equalsIgnoreCase("ONCE_ONLY_GOAL"))
				{
					onceOnlyGoals.add(mo);
				} else if (moType.equalsIgnoreCase("PROTECTED_GOAL"))
				{
					protectedGoals.add(mo);
				} else if (moType.equalsIgnoreCase("PERMANENT_GOAL"))
				{
					permanentGoals.add(mo);
				}
			}
		}


		goals.clear();
		goals.addAll(onceOnlyGoals);
		goals.addAll(protectedGoals);
		goals.addAll(permanentGoals);


	}

	public synchronized boolean changedWorldBeliefState() {
		ArrayList<Object> temp1= new ArrayList<Object>();
		ArrayList<Object> temp2= new ArrayList<Object>();

		temp1.addAll(listOfWorldBeliefStates);
		temp2.addAll(listOfPreviousWorldBeliefStates);

		return 	(!temp1.equals(temp2));
	}

	/**
	 *  Checks if there is a current behavior proposition in working storage that is using the resources this behavior needs
	 * @return if there is conflict of resources
	 */
	private boolean resourceConflict() {//TODO must develop this idea further
		boolean resourceConflict=false;
		ArrayList<Memory> allOfType=new ArrayList<Memory>();

		if(ws!=null)
			allOfType.addAll(ws.getAllOfType("BEHAVIOR_PROPOSITION"));

		ArrayList<String> usedResources=new ArrayList<String>();


		if(allOfType!=null){

			for(Memory bp:allOfType){
				try {
					JSONObject jsonBp=new JSONObject(bp.getI());
					//System.out.println("=======> bp.getInfo(): "+bp.getInfo());
					boolean performed=jsonBp.getBoolean("PERFORMED");
					if(!performed){//otherwise it is not consuming those resources

						JSONArray resourceList=jsonBp.getJSONArray("RESOURCELIST");

						for(int i=0;i<resourceList.length();i++){
							usedResources.add(resourceList.getString(i));
						}
					}
				} catch (JSONException e) {e.printStackTrace();}
			}
		}
		//		System.out.println("%%% usedResources: "+usedResources);
		//		System.out.println("%%% getResourceList: "+getResourceList());
		int sizeBefore=usedResources.size();
		usedResources.removeAll(this.getResourceList());
		int sizeLater=usedResources.size();

		if(sizeLater!=sizeBefore){
			resourceConflict=true;
			//			System.out.println("%%%%%%%%%%%%%%%%%%%%% There was a conflict here");
		}

		return resourceConflict;
	}

	/**
	 * A behavior module is executable at time t when all of its preconditions are observed to be true at time t.
	 *
	 */
	private boolean checkIfExecutable()
	{
		listOfWorldBeliefStates = new ArrayList<Object>();
		for(Memory mo:this.getInputsOfType("WORLD_STATE")){
			listOfWorldBeliefStates.add(mo.getI());
			//				System.out.println("###########adding world state");
		}
		ArrayList<Memory> tempPreconList=new ArrayList<Memory>();
		//Comparison between two MOs is performed between their infos
		tempPreconList.addAll(preconList);

		for(Memory precon:preconList){ 

			for(Object ws:listOfWorldBeliefStates){
				if(precon.getI().equals(ws)){
					tempPreconList.remove(precon);
					break;
				}
			}

		}

		if (tempPreconList.isEmpty())
		{ 
			setExecutable(true);
		} else
		{
			setExecutable(false);
		}

		//TODO  I should probably check for conflicts between behaviors here...
		return executable;

	}

	/**
	 * 1-compute impact of state, goals and protected goals on the activation level of a module 2-compute the way the competence module activates and inhibits related modules through its successor links, predecessor links and conflicters 3- decay function ensures that the overall activation level remains constant
	 */
	private void spreadActivation()
	{
		if(!this.isActive()){ //If active, it should remain at zero
			double activation = 0;
			// TODO this can be optimized, I could get all this information with only one method (this would iterate only once through the coalition list
			double inputfromstate = inputFromState();
			double inputfromgoals = inputFromGoals();
			double takenawaybyprotectedgoals = takenAwayByProtectedGoals();
			double spreadbw = spreadBw();
			double spreadfw = spreadFw();
			double takenaway = takenAway();

			activation = inputfromstate + inputfromgoals - takenawaybyprotectedgoals + (spreadbw + spreadfw - takenaway);

			if(maxA<activation){//calculates maximum activation added for normalization
				maxA=activation;
			}

			if (!this.isActive())
			{
				//						activation = activation + this.getValue(); //TODO Without normalization
				activation=activation*(1-this.getActivation())/maxA+this.getActivation(); //With Normalization
			}



			//TODO I'm trying to migrate this decay property to BehaviorsWTA.java
			// decay();//TODO Decay process that scales the mean level of energy to pi  


			//		//		activation=activation-globalVariables.getDecay(); //TODO Test with subtractive decay
			//
			if(globalVariables!=null)
				activation=activation*globalVariables.getDecay(); //TODO test with multiplicative decay


			if (activation < 0)
			{
				activation = 0;
			} 
			try 
			{
				this.setActivation(activation);
			} catch (CodeletActivationBoundsException e) 
			{
				e.printStackTrace();
			}
						
		}else{//should remain at zero  [Maes 1989]
			try 
			{
				this.setActivation(0);
			} catch (CodeletActivationBoundsException e) 
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Decay function. Hypothesis: each time activation is spread from/towards a module this same module inhibits/excites the activation of all other modules making the mean activation level constant.
	 */
	private void decay()
	{
		// TODO this could be optimized
		double activation = 0;
		if (!this.coalition.isEmpty())
		{
			for (Behavior module : this.coalition)
			{
				if (impendingAccess(module))
				{
					try
					{
						activation = activation + module.getActivation();
						activation = activation / this.coalition.size();
					} finally
					{
						lock.unlock();
						module.lock.unlock();
					}
				}
			}
		}

	}

	/**
	 * @param executable
	 *           sets this competence as being executable
	 */
	public void setExecutable(boolean executable)
	{
		this.executable = executable;
	}

	/**
	 * @return the executable
	 */
	public boolean isExecutable()
	{
		return executable;
	}

	/**
	 * @param condition
	 *           condition to listOfPreconditions
	 */
	public void addPreconList(Memory condition)
	{
		this.preconList.add(condition);
		this.addInput(condition);
	}


	/**
	 * @return deletes condition from listOfPreconditions
	 */
	public boolean delPreconList(Memory condition)
	{
		this.removesInput(condition);
		return preconList.remove(condition);
	}

	/**
	 * @param condition
	 *           condition to listOfPreconditions
	 */
	public void addSoftPreconList(Memory condition)
	{
		this.softPreconList.add(condition);
		this.addInput(condition);
	}


	/**
	 * @return deletes condition from listOfPreconditions
	 */
	public boolean delSoftPreconList(Memory condition)
	{
		this.removesInput(condition);
		return softPreconList.remove(condition);
	}

	/**
	 * @param condition
	 *           condition to addList
	 */
	public void addAddList(Memory condition)
	{
		this.addList.add(condition);
		this.addOutput(condition);
	}

	/**
	 * @return deletes condition from addList
	 */
	public boolean delAddList(Memory condition)
	{	
		this.removesOutput(condition);
		return addList.remove(condition);
	}

	/**
	 * @param condition
	 *           condition to deleteList
	 */
	public void addDelList(Memory condition)
	{
		this.addOutput(condition);
		this.deleteList.add(condition);
	}

	/**
	 * @return deletes condition from deleteList
	 */
	public boolean delDelList(Memory condition)
	{	this.removesOutput(condition);
	return deleteList.remove(condition);
	}

	/**
	 * @param active
	 *           the active to set
	 */
	public void setActive(boolean active)
	{
		this.active = active;
		listOfPreviousWorldBeliefStates=listOfWorldBeliefStates;
	}

	/**
	 * @return the active
	 */
	public boolean isActive()
	{
		return active;
	}

	/**
	 * 
	 * @return list of preconditions
	 */
	public ArrayList<Memory> getListOfPreconditions()
	{
		return preconList;
	}

	/**
	 * 
	 * @param listOfPreconditions
	 *           List of MemoryObjects with preconditions
	 */
	public void setListOfPreconditions(ArrayList<Memory> listOfPreconditions)
	{
		this.preconList = listOfPreconditions;
	}

	/**
	 * 
	 * @return add list
	 */
	public ArrayList<Memory> getAddList()
	{
		return addList;
	}

	/**
	 * 
	 * @param addList
	 *           sets the add list
	 */
	public void setAddList(ArrayList<Memory> addList)
	{
		this.addList = addList;
	}

	/**
	 * 
	 * @return the delete list
	 */
	public ArrayList<Memory> getDeleteList()
	{
		return deleteList;
	}

	/**
	 * 
	 * @param deleteList
	 *           sets the delete list
	 */
	public void setDeleteList(ArrayList<Memory> deleteList)
	{
		this.deleteList = deleteList;
	}

	/**
	 * @return the coalition
	 */
	public ArrayList<Behavior> getCoalition()
	{
		return coalition;
	}

	/**
	 * @param coalition
	 *           the coalition to set
	 */
	public void setCoalition(ArrayList<Behavior> coalition)
	{
		this.coalition = coalition;
		updateLinks(); // Isto soh deve ser feito quando a coalizao muda
		if(showCoalitionLinks){
			System.out.println("************* " + getName() + "'s Links ************");
			System.out.println("* successors ------");
			Enumeration e = this.successors.keys();
			while (e.hasMoreElements())
			{
				Behavior module = (Behavior) e.nextElement();
				if (impendingAccess(module))
				{
					try
					{
						System.out.println("Name: " + module.getName() + " |Props: " + this.successors.get(module));
					} finally
					{
						lock.unlock();
						module.lock.unlock();
					}
				}
			}
			System.out.println("* predecessors ----");
			e = this.predecessors.keys();
			while (e.hasMoreElements())
			{
				Behavior module = (Behavior) e.nextElement();
				if (impendingAccess(module))
				{
					try
					{
						System.out.println(module.getName() + " |Props: " + this.predecessors.get(module));
					} finally
					{
						lock.unlock();
						module.lock.unlock();
					}
				}
			}
			System.out.println("* conflicters -----");
			e = this.conflicters.keys();
			while (e.hasMoreElements())
			{
				Behavior module = (Behavior) e.nextElement();
				if (impendingAccess(module))
				{
					try
					{
						System.out.println(module.getName() + " |Props: " + this.conflicters.get(module));
					} finally
					{
						lock.unlock();
						module.lock.unlock();
					}
				}
			}
			System.out.println("************************************************");
		}   

	}

	/**
	 * @return the successors
	 */
	public Hashtable<Behavior, ArrayList<Memory>> getSuccessors()
	{
		return successors;
	}

	/**
	 * @param successors
	 *           the successors to set
	 */
	public void setSuccessors(Hashtable<Behavior, ArrayList<Memory>> successors)
	{
		this.successors = successors;
	}

	/**
	 * @return the predecessors
	 */
	public Hashtable<Behavior, ArrayList<Memory>> getPredecessors()
	{
		return predecessors;
	}

	/**
	 * @param predecessors
	 *           the predecessors to set
	 */
	public void setPredecessors(Hashtable<Behavior, ArrayList<Memory>> predecessors)
	{
		this.predecessors = predecessors;
	}

	/**
	 * @return the conflicters
	 */
	public Hashtable<Behavior, ArrayList<Memory>> getConflicters()
	{
		return conflicters;
	}

	/**
	 * @param conflicters
	 *           the conflicters to set
	 */
	public void setConflicters(Hashtable<Behavior, ArrayList<Memory>> conflicters)
	{
		this.conflicters = conflicters;
	}

	/**
	 * @param name
	 *           the name of this competence
	 */
	public void setName(String name)
	{
		this.name = name;
		Thread.currentThread().setName(name);
	}

	/**
	 * @return the name of this competence
	 */
	public String getName()
	{
		return name;
		// return Thread.currentThread().getId();
	}

	/**
	 * @return the amount of activation from the state
	 */
	public double inputFromState()
	{ 		
		double activation = 0;
		ArrayList<Behavior> tempCodelets = new ArrayList<Behavior>();
		ArrayList<Memory> THIS_softPrecon_and_ClassicPrecon=new ArrayList<Memory>();
		THIS_softPrecon_and_ClassicPrecon.addAll(this.getListOfPreconditions());
		THIS_softPrecon_and_ClassicPrecon.addAll(this.getSoftPreconList());

		tempCodelets.addAll(this.getAllBehaviors());

		if (!tempCodelets.isEmpty())
		{
			ArrayList<Memory> intersection = getIntersectionSet(this.getWorldState(), THIS_softPrecon_and_ClassicPrecon);
			
			for (Memory j : intersection)
			{
				double sharpM = 0;
				for (Behavior module : tempCodelets)
				{
					if (impendingAccess(module))
					{
						try
						{
							ArrayList<Memory> MODULE_softPrecon_and_ClassicPrecon=new ArrayList<Memory>();
							MODULE_softPrecon_and_ClassicPrecon.addAll(module.getListOfPreconditions());
							MODULE_softPrecon_and_ClassicPrecon.addAll(module.getSoftPreconList());
							
							if (MODULE_softPrecon_and_ClassicPrecon.contains(j))
							{
								sharpM = sharpM + 1;
							}
						} finally
						{
							lock.unlock();
							module.lock.unlock();
						}
					}
				}
				// What if sharpM or listOfPreconditions.size == zero?
				// sharpM could zero because j comes from the intersection of S and c, so if no preconditions from the module is found in world state, the intersection shall be zero
				// synchronized(listOfPreconditions){
				if ((sharpM > 0) && (THIS_softPrecon_and_ClassicPrecon.size() > 0))
				{
					double activationfromstate = globalVariables.getPhi() * (1 / sharpM) * (1 / (double) THIS_softPrecon_and_ClassicPrecon.size());
					if (showActivationSpread)
					{
						System.out.println(this.getName() + " got " + activationfromstate + " energy from the world state");
					}
					activation = activation + activationfromstate;
				} else
				{
					if(printNoActivationCases){System.out.println("No activation in the case [" + this.getName() + " getting energy from the world state]: either the number of satisfying modules is zero or there are no preconditions in this module.");}
				}
				// }//end synch
			}
		}
		// }//coalitionIsBusy=false;
		return activation;
	}

	/**
	 * @return the allBehaviors
	 */
	public ArrayList<Behavior> getAllBehaviors() {
		return allBehaviors;
	}
	/**
	 * @param allBehaviors the allBehaviors to set
	 */
	public void setAllBehaviors(ArrayList<Behavior> allBehaviors) {
		this.allBehaviors = allBehaviors;
	}
	/**
	 * @return the amount of activation from goalsthis.coalition.lock();
	 */
	public double inputFromGoals()
	{
		double activation = 0;
		ArrayList<Behavior> tempCodelets = new ArrayList<Behavior>(); //TODO Should we get this input from the coalition or from the full set of codelets?
		tempCodelets.addAll(this.getAllBehaviors());
		if (!tempCodelets.isEmpty())
		{
			ArrayList<Memory> intersection = getIntersectionSet(this.getGoals(), this.getAddList());
			for (Memory j : intersection)
			{
				double sharpA = 0;
				for (Behavior module : tempCodelets)
				{
					if (impendingAccess(module))
					{
						try
						{
							if (module.getAddList().contains(j))
							{
								sharpA = sharpA + 1;
							}
						} finally
						{
							lock.unlock();
							module.lock.unlock();
						}
					}
				}
				// synchronized(this.addList){
				if ((sharpA > 0) && (this.getAddList().size() > 0))
				{
					double othermoduleActivation = globalVariables.getGamma() * ((1 / sharpA) * (1 / (double) this.getAddList().size()));
					if (showActivationSpread)
					{
						System.out.println(this.getName() + " receives " + othermoduleActivation + " energy from goal " + j);
					}
					activation = activation + othermoduleActivation;
				} else
				{
					if (showActivationSpread)
					{
						System.out.println("No activation from situation [" + this.getName() + " getting activation from goals]: either the number of satisfying modules is zero or there are no preconditions in this module.");
					}
				}
				// }//end synch
			}
		}
		// activation=activation*globalVariables.getGamma();
		return activation;
	}

	/**
	 * @return the amount of activation to be removed by the goals that are protected
	 */
	public double takenAwayByProtectedGoals()
	{
		double activation = 0;
		// synchronized(this.coalition){
		if (!this.getCoalition().isEmpty())
		{
			ArrayList<Memory> intersection = getIntersectionSet(this.getProtectedGoals(), this.getDeleteList());
			for (Memory j : intersection)
			{
				double sharpU = 0;
				for (Behavior module : this.getCoalition())
				{
					if (impendingAccess(module))
					{
						try
						{
							if (module.getDeleteList().contains(j))
							{
								sharpU = sharpU + 1;
							}
						} finally
						{
							lock.unlock();
							module.lock.unlock();
						}
					}
				}
				// synchronized(this.deleteList){
				if ((sharpU > 0) && (this.getDeleteList().size() > 0))
				{
					double takenEnergy = (1 / sharpU) * (1 / (double) this.getDeleteList().size()) * globalVariables.getDelta();
					if (showActivationSpread)
					{
						System.out.println(this.getName() + " has " + takenEnergy + " taken away from it by protected goals.");
					}
					activation = activation + takenEnergy;
				} else
				{
					if (showActivationSpread)
					{
						System.out.println("No activation from situation [" + this.getName() + " having energy taken away by protected goals]: either the number of satisfying modules is zero or there are no preconditions in this module.");
					}
				}
				// }//end synch
			}
		}
		// }//end synch
		return activation;
	}

	/**
	 * @return the amount of activation that is spread backwards from other modules in the direction of this module
	 * 
	 *         Note: this approach is slightly different from the one proposed at the article by [Maes 1989] since here we try to avoid meddling with another codelet's states.
	 */
	public double spreadBw()
	{
		
		// In this case x= other modules, y= this module
		double activation = 0;
		// synchronized(this.successors){
		if (!this.getSuccessors().isEmpty())
		{
			Enumeration e = this.getSuccessors().keys();
			// iterate through Hashtable keys Enumeration
			while (e.hasMoreElements())
			{
				Behavior module = (Behavior) e.nextElement();
				if (impendingAccess(module))
				{
					try
					{
						double amount = 0;
						if (!module.isExecutable())
						{// A competence module x that is not executable spreads activation backward.

							ArrayList<Memory> intersection = new ArrayList<Memory>();
							ArrayList<Memory> preconPlusSoftPrecon=new ArrayList<Memory>();

							preconPlusSoftPrecon.addAll(module.getListOfPreconditions());

							intersection.addAll(getIntersectionSet(preconPlusSoftPrecon, this.getAddList()));
							intersection.removeAll(worldState);
							for (Memory item : intersection)
							{
								amount = amount + ((1.0 / this.competencesWithPropInAdd(item)) * (1.0 / (double) this.getAddList().size()));
							}
							amount = amount * module.getActivation() * (globalVariables.getPhi() / globalVariables.getGamma());
							if (showActivationSpread)
							{
								System.out.println(this.getName() + " receives " + amount + " backwarded energy from " + module.getName() + " [which has A= " + module.getActivation() + " ]");
							}

						}
						// --------------------------
						activation = activation + amount;
					} finally
					{
						lock.unlock();
						module.lock.unlock();
					}
				}
			}
		}

		return activation;
	}

	/**
	 * @return the amount of activation that is spread forward from other modules in the direction of this module
	 * 
	 *         Note: this approach is slightly different from the one proposed at the article by [Maes 1989] since here we try to avoid meddling with another codelet's states.
	 */
	public double spreadFw()
	{
		// In this case x= other modules, y= this module
		double activation = 0;
		// synchronized(this.predecessors){
		if (!this.getPredecessors().isEmpty())
		{
			Enumeration e = this.getPredecessors().keys();
			// iterate through Hashtable keys Enumeration
			while (e.hasMoreElements())
			{
				Behavior module = (Behavior) e.nextElement();
				if (impendingAccess(module))
				{
					try
					{
						double amount = 0;
						if (module.isExecutable())
						{// An executable competence module x spreads activation forward.
							ArrayList<Memory> intersection = new ArrayList<Memory>();

							ArrayList<Memory> preconPlusSoftPrecon=new ArrayList<Memory>();

							preconPlusSoftPrecon.addAll(this.getListOfPreconditions());
							preconPlusSoftPrecon.addAll(this.getSoftPreconList());


							intersection.addAll(getIntersectionSet(module.getDeleteList(), preconPlusSoftPrecon));
							intersection.removeAll(worldState);
							for (Memory item : intersection)
							{
								amount = amount + ((1.0 / this.competencesWithPropInPrecon(item)) * (1.0 / (double) preconPlusSoftPrecon.size()));
							}
							amount = amount * module.getActivation() * (globalVariables.getPhi() / globalVariables.getGamma());
							if (showActivationSpread)
							{
								System.out.println(this.getName() + " receives " + amount + " forwarded energy from " + module.getName() + " [which has A= " + module.getActivation() + " ]");
							}

						}
						// ------------------------------------------------
						activation = activation + amount;
					} finally
					{
						lock.unlock();
						module.lock.unlock();
					}
				}
			}
		}
		// }//end synch
		return activation;
	}

	/**
	 * @return the amount of activation that is taken away from this module by other modules through conflict links
	 * 
	 *         Note: this approach is slightly different from the one proposed at the article by [Maes 1989] since here we try to avoid meddling with another codelet's states. Note: I am not using the "max" strategy described by maes
	 */
	public double takenAway()
	{
		// In this case x= other modules, y= this module
		double activation = 0;
		// synchronized(this.conflicters){
		if (!this.getConflicters().isEmpty())
		{
			Enumeration e = this.getConflicters().keys();
			// iterate through Hashtable keys Enumeration
			while (e.hasMoreElements())
			{
				Behavior module = (Behavior) e.nextElement();
				if (impendingAccess(module))
				{
					try
					{
						// ---------------------------------
						double amount = 0;
						ArrayList<Memory> intersection = new ArrayList<Memory>();

						ArrayList<Memory> preconPlusSoftPrecon=new ArrayList<Memory>();						
						preconPlusSoftPrecon.addAll(this.getListOfPreconditions());
						preconPlusSoftPrecon.addAll(this.getSoftPreconList());

						intersection=getIntersectionSet(preconPlusSoftPrecon, module.getDeleteList());
						intersection=getIntersectionSet(intersection, worldState);

						if (!((module.getActivation() < this.getActivation()) && (!intersection.isEmpty())))
						{ // this is the else case due to !
							preconPlusSoftPrecon=new ArrayList<Memory>();						
							preconPlusSoftPrecon.addAll(module.getListOfPreconditions());
							preconPlusSoftPrecon.addAll(module.getSoftPreconList());

							intersection = getIntersectionSet(this.getDeleteList(), preconPlusSoftPrecon);
							intersection = getIntersectionSet(intersection, worldState);
							for (Memory item : intersection)
							{
								amount = amount + ((1.0 / this.competencesWithPropInDel(item)) * (1.0 / (double) this.getDeleteList().size()));
							}
							// amount = (b1.activation[0] *
							// (self.conf_energy / self.goal_energy) *
							// amount)
							amount = module.getActivation() * (globalVariables.getDelta() / globalVariables.getGamma()) * amount;
							ArrayList<Memory> modulos = this.getConflicters().get(module);
							double numberOfConflicters = 0;
							if (modulos != null)
							{
								numberOfConflicters = (double) modulos.size();
							}// TODO  Eu nao deveria precisar fazer este teste!
							double moduleActivation = module.getActivation();
							double activationFromModule = (globalVariables.getDelta() / globalVariables.getGamma()) * numberOfConflicters * moduleActivation;
							if (showActivationSpread)
							{
								System.out.println(this.getName() + " has " + amount + " of its energy decreased by " + module.getName() + " [which has A= " + module.getActivation() + " ]");
							}
						}
						// ------------------------------------
						activation = activation + amount;
					} finally
					{
						lock.unlock();
						module.lock.unlock();
					}
				}
			}
		}

		return activation;
	}

	/**
	 * 
	 * @param A
	 *           double value to be compared
	 * @param B
	 * @return the highest value
	 */
	private double max(double A, double B)
	{
		if (A > B)
		{
			return A;
		} else
		{
			return B;
		}
	}

	/**
	 * Returns a list of PROPOSITION MOs constituting the intersection between A and B.
	 * For this method to work, at least one of the lists must be of propositions
	 * 
	 * @param A
	 *           list A
	 * @param B
	 *           list B
	 * @return the list with the intersection between A and B lists
	 * 
	 */

	public ArrayList<Memory> getIntersectionSet(ArrayList<Memory> A, ArrayList<Memory> B)
	{//TODO Should this comparison be performed as pattern matching?
		ArrayList<Memory> currentList = new ArrayList<Memory>();
		ArrayList<Memory> intersection = new ArrayList<Memory>();


		if((A.isEmpty()||B.isEmpty())||(A==null||B==null)){
			return intersection;
		}
		//		System.out.println(B);
		//		System.out.println(B.get(0));
		//Gives preference to returning PROPOSITIONs
		if(A.get(0).getName().equalsIgnoreCase("PROPOSITION")){
			currentList.addAll(B);
			intersection.addAll(A);	
		}else if((B.get(0).getName().equalsIgnoreCase("PROPOSITION"))){ 
			currentList.addAll(A);
			intersection.addAll(B);
		}else{
			//			return null; //Should throw an exception?
			currentList.addAll(A);
			intersection.addAll(B);

		}

		for (int i = intersection.size() - 1; i > -1; --i)
		{
			Memory mo = intersection.get(i);

			if (!removeByInfo(currentList,mo.getI()))
				removeByInfo(intersection,mo.getI());
		}
		return intersection;
	}

	public boolean removeByInfo(ArrayList<Memory> moList, Object target){
		boolean removed=false;
		Memory mustRemove=null;
		for(Memory mo:moList){
			if(mo.getI().equals(target)){
				mustRemove=mo;
				break;
			}
		}

		if(mustRemove!=null){
			moList.remove(mustRemove);
			removed=true;
		}

		return removed;
	}


	/**
	 * @return the worldState
	 */
	public ArrayList<Memory> getWorldState()
	{
		return worldState;
	}

	/**
	 * @param worldState
	 *           the worldState to set
	 */
	public void setWorldState(ArrayList<Memory> worldState)
	{
		this.worldState = worldState;
	}

	/**
	 * @return the protectedGoals
	 */
	public ArrayList<Memory> getProtectedGoals()
	{
		return protectedGoals;
	}

	/**
	 * @param protectedGoals
	 *           the protectedGoals to set
	 */
	public void setProtectedGoals(ArrayList<Memory> protectedGoals)
	{
		this.protectedGoals = protectedGoals;
	}

	/**
	 * @return the permanentGoals
	 */
	public ArrayList<Memory> getPermanentGoals()
	{
		return permanentGoals;
	}

	/**
	 * @param permanentGoals
	 *           the permanentGoals to set
	 */
	public void setPermanentGoals(ArrayList<Memory> permanentGoals)
	{
		this.permanentGoals = permanentGoals;
	}

	/**
	 * @return the onceOnlyGoals
	 */
	public ArrayList<Memory> getOnceOnlyGoals()
	{
		return onceOnlyGoals;
	}

	/**
	 * @param onceOnlyGoals
	 *           the onceOnlyGoals to set
	 */
	public void setOnceOnlyGoals(ArrayList<Memory> onceOnlyGoals)
	{
		this.onceOnlyGoals = onceOnlyGoals;
	}

	/**
	 * @return the goals
	 */
	public ArrayList<Memory> getGoals()
	{
		return goals;
	}

	/**
	 * @param goals
	 *           the goals to set
	 */
	public void setGoals(ArrayList<Memory> goals)
	{
		this.goals = goals;
	}

	/**
	 * @return the globalVariables
	 */
	public GlobalVariables getGlobalVariables()
	{
		return globalVariables;
	}

	/**
	 * @param globalVariables
	 *           the globalVariables to set
	 */
	public void setGlobalVariables(GlobalVariables globalVariables)
	{
		this.globalVariables = globalVariables;
	}

	/**
	 * @return the firstTime
	 */
	public boolean isFirstTime()
	{
		return firstTime;
	}

	/**
	 * @param firstTime
	 *           the firstTime to set
	 */
	public void setFirstTime(boolean firstTime)
	{
		this.firstTime = firstTime;
	}

	/**
	 * @param actionsSet
	 *           the actionsSet to set
	 * 
	 */
	public void setActionList(ArrayList<String> actionsSet)
	{
		this.actionList = actionsSet;
	}

	/**
	 * @param action
	 *           the action to be added to actionSet
	 */
	public void addAction(String action)
	{
		this.actionList.add(action);
		String pNumber;
		String[] actionDecomposition=action.split(" ");
		JSONObject jsonAction=new JSONObject();
		try {
			if(!this.resourceList.contains(actionDecomposition[0])){
				this.resourceList.add(actionDecomposition[0]); //Stores this resource in this behavior's resource list
			}

			jsonAction.put("RESOURCE", actionDecomposition[0]);
			jsonAction.put("ACTION", actionDecomposition[1]);
			for(int i=2;i<actionDecomposition.length;i++){
				pNumber="P"+String.valueOf(i-1);
				jsonAction.put(pNumber, actionDecomposition[i]);
			}

		} catch (JSONException e) {e.printStackTrace();}


		this.jsonActionList.put(jsonAction);
		//		System.out.println("---JSON ACTION: "+jsonAction);
	}
	/**
	 * Clears this behavior's action list
	 */
	public void clearActionList(){
		this.jsonActionList=new JSONArray();
		this.actionList.clear();
	}

	/**
	 * Returns the number of competences in coalition with the given proposition in their precondition lists
	 */
	private double competencesWithPropInPrecon(Memory proposition)
	{
		double compWithProp = 0;
		for (Behavior comp : this.getCoalition())
		{
			if (impendingAccess(comp))
			{
				try
				{
					if (comp.getListOfPreconditions().contains(proposition)||comp.getSoftPreconList().contains(proposition))
					{
						compWithProp = compWithProp + 1;
					}
				} finally
				{
					lock.unlock();
					comp.lock.unlock();
				}
			}
		}
		return compWithProp;
	}


	/**
	 * Returns the list of competences from coalition with the given proposition in their add lists
	 */
	private double competencesWithPropInAdd(Memory proposition)
	{
		double compWithProp = 0;
		for (Behavior comp : this.getCoalition())
		{
			if (impendingAccess(comp))
			{
				try
				{
					if (comp.getAddList().contains(proposition))
					{
						compWithProp = compWithProp + 1;
					}
				} finally
				{
					lock.unlock();
					comp.lock.unlock();
				}
			}
		}
		return compWithProp;
	}


	/**
	 * Returns the list of competences from coalition with the given proposition in its del lists
	 */
	private double competencesWithPropInDel(Memory proposition)
	{
		double compWithProp = 0;
		for (Behavior comp : this.getCoalition())
		{
			if (impendingAccess(comp))
			{
				try
				{
					if (comp.getDeleteList().contains(proposition))
					{
						compWithProp = compWithProp + 1;
					}
				} finally
				{
					lock.unlock();
					comp.lock.unlock();
				}
			}
		}
		return compWithProp;
	}

	/**
	 * Sets the list of actions constituting this behavior
	 * 
	 * @param actions
	 *           list of actions
	 */
	private void addAllActions(ArrayList<String> actions)
	{
		this.actionList = actions;
	}

	/**
	 * Returns the list of actions constituting this behavior
	 * 
	 * @return list of actions
	 */
	private List<String> getAllActions()
	{
		return this.actionList;
	}

	public void setBehaviors(ArrayList<Behavior> competences) {
		this.allBehaviors=competences;

	}
	/**
	 * @return the setToZeroWhenActivated
	 */
	public boolean isSetToZeroWhenActivated() {
		return setToZeroWhenActivated;
	}
	/**
	 * @param setToZeroWhenActivated the setToZeroWhenActivated to set
	 */
	public void setSetToZeroWhenActivated(boolean setToZeroWhenActivated) {
		this.setToZeroWhenActivated = setToZeroWhenActivated;
	}
	/**
	 *  Returns the moving average of the last n activations. 
	 *  n must be previously defined, otherwise it will return the current activation (n=1)
	 * @return
	 */
	public synchronized double getActivationMA(){
		return activationMA;
	}

	/**
	 * @return the softPreconList
	 */
	public ArrayList<Memory> getSoftPreconList() {
		return softPreconList;
	}
	/**
	 * @param softPreconList the softPreconList to set
	 */
	public void setSoftPreconList(ArrayList<Memory> softPreconList) {
		this.softPreconList = softPreconList;
	}
}
