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

package br.unicamp.cst.behavior.subsumption;

import java.util.ArrayList;
import java.util.List;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;

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
		action.addInputs(sensor.getOutputs());
	}

	public void attachActionToActuator(SubsumptionAction action, Codelet actuator)
	{
		action.addOutputs(actuator.getInputs());		
	}

	public void attachTwoActions(SubsumptionAction previousAction,SubsumptionAction nextAction,List<Memory> memoryObjectsLinkingPreviousAndNextAction) 
	{
		previousAction.addOutputs(memoryObjectsLinkingPreviousAndNextAction);
		nextAction.addInputs(memoryObjectsLinkingPreviousAndNextAction);	
	}
}
