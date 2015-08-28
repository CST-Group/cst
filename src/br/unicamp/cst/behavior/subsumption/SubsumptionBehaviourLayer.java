/**
 * 
 */
package br.unicamp.cst.behavior.subsumption;

import java.util.ArrayList;
import java.util.List;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;

/**
 * Group of actions which define a behaviour in a subsumption architecture
 * 
 * @author andre
 *
 */
public class SubsumptionBehaviourLayer 
{
	private List<SubsumptionAction> actionsList;

	/**
	 * Constructor
	 */
	public SubsumptionBehaviourLayer() 
	{
		super();
		actionsList = new ArrayList<SubsumptionAction>();
	}

	/**
	 * @return the actionsList
	 */
	public List<SubsumptionAction> getActionsList() 
	{
		return actionsList;
	}

	/**
	 * @param actionsList the actionsList to set
	 */
	public void setActionsList(List<SubsumptionAction> actionsList) 
	{
		this.actionsList = actionsList;
	}

	public void addAction(SubsumptionAction action) 
	{
		actionsList.add(action);	
	}
	
	public void attachActionToSensor(SubsumptionAction action, Codelet sensor) 
	{
		action.pushInputs(sensor.getOutputs());
	}

	public void attachActionToActuator(SubsumptionAction action, Codelet actuator)
	{
		action.pushOutputs(actuator.getInputs());		
	}

	public void attachTwoActions(SubsumptionAction previousAction,SubsumptionAction nextAction,List<MemoryObject> memoryObjectsLinkingPreviousAndNextAction) 
	{
		previousAction.pushOutputs(memoryObjectsLinkingPreviousAndNextAction);
		nextAction.pushInputs(memoryObjectsLinkingPreviousAndNextAction);	
	}
}
