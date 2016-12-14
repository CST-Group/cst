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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.unicamp.cst.core.entities.Mind;


/**
 * An implementation of a subsumption architecture as defined in [Rodney A. Brooks 1991] "Intelligence without representation".
 * This architecture is aimed at selecting the actions of a reactive-only creature, in the reptilian level.
 * A  subsumption architecture is composed of behavior layers of actions, in the sense that each layer is a group of actions 
 * which define a behavior.
 * 
 * @author andre
 *
 */
public class SubsumptionArchitecture 
{
	private List<SubsumptionBehaviourLayer> behaviourLayers;	
	private Map<SubsumptionAction,List<SubsumptionAction>> suppressorActionsMapList;
	private Map<SubsumptionAction,List<SubsumptionAction>> inhibitorActionsMapList;
	private Mind mind;

	/**
	 * Constructor
	 */
	public SubsumptionArchitecture(Mind nm) 
	{
		super();
		mind = nm;
		behaviourLayers = new ArrayList<SubsumptionBehaviourLayer>();
		suppressorActionsMapList = new HashMap<SubsumptionAction, List<SubsumptionAction>>();
		inhibitorActionsMapList = new HashMap<SubsumptionAction, List<SubsumptionAction>>();
	}

	public List<SubsumptionBehaviourLayer> getBehaviourLayers() 
	{
		return behaviourLayers;
	}

	public void setBehaviourLayers(List<SubsumptionBehaviourLayer> behaviourLayers) 
	{
		this.behaviourLayers = behaviourLayers;
	}

	public void addLayer(SubsumptionBehaviourLayer layer)
	{
		behaviourLayers.add(layer);		
	}

	public boolean permissionToSuppress(SubsumptionAction subsumptionAction, SubsumptionAction suppressedAction) 
	{
		boolean permission = false;
		
		 List<SubsumptionAction> suppressorActionList = suppressorActionsMapList.get(suppressedAction);
		 if(suppressorActionList!=null&&suppressorActionList.size()>0&&suppressorActionList.contains(subsumptionAction))
		 {
			 if(isSubSumptionActionActivationGreatestInSubsumptionActionList(subsumptionAction,suppressorActionList))
			 {
				 permission=true;
			 }
		 }
		
		return permission;
	}
	
	public boolean permissionToInhibit(SubsumptionAction subsumptionAction, SubsumptionAction inhibitedAction) 
	{
		boolean permission = false;
		
		 List<SubsumptionAction> inhibitorActionList = inhibitorActionsMapList.get(inhibitedAction);
		 if(inhibitorActionList!=null&&inhibitorActionList.size()>0&&inhibitorActionList.contains(subsumptionAction))
		 {
			 if(isSubSumptionActionActivationGreatestInSubsumptionActionList(subsumptionAction,inhibitorActionList))
			 {
				 permission=true;
			 }
		 }
		
		return permission;
	}

	private boolean isSubSumptionActionActivationGreatestInSubsumptionActionList(SubsumptionAction subsumptionAction,List<SubsumptionAction> subsumptionActionList) 
	{
		boolean isGreatest = true;
		
		for(SubsumptionAction suppressorAction : subsumptionActionList)
		{
			if(!suppressorAction.equals(subsumptionAction))
			{
				if(suppressorAction.getActivation() > subsumptionAction.getActivation())
				{
					isGreatest = false;
					break;
				}
			}			
		}		
		return isGreatest;
	}
	
	public void addSuppressedAction(SubsumptionAction suppressorAction, SubsumptionAction suppressedAction) 
	{
		suppressorAction.setSuppressedAction(suppressedAction);
		suppressorAction.addOutputs(suppressedAction.getOutputs());
		
		List<SubsumptionAction> suppressorActionList = suppressorActionsMapList.get(suppressedAction);
		if(suppressorActionList==null)
		{
			suppressorActionList = new ArrayList<SubsumptionAction>();
			suppressorActionList.add(suppressorAction);
			suppressorActionsMapList.put(suppressedAction, suppressorActionList);
		}else
		{
			suppressorActionList.add(suppressorAction);
		}				
	}

	public void addInhibitedAction(SubsumptionAction inhibitorAction, SubsumptionAction inhibitedAction) 
	{
		inhibitorAction.setInhibitedAction(inhibitedAction);	
		
		List<SubsumptionAction> inhibitorActionList = inhibitorActionsMapList.get(inhibitedAction);
		if(inhibitorActionList==null)
		{
			inhibitorActionList = new ArrayList<SubsumptionAction>();
			inhibitorActionList.add(inhibitorAction);
			inhibitorActionsMapList.put(inhibitedAction, inhibitorActionList);
		}else
		{
			inhibitorActionList.add(inhibitorAction);
		}	
	}

	/**
	 * @return the mind
	 */
	public synchronized Mind getMind() {
		return mind;
	}
	
}
