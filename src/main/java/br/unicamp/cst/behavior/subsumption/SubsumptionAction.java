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

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Mind;

/**
 * A state/action in the subsumption architecture
 * 
 * @author andre
 *
 */
public abstract class SubsumptionAction extends Codelet
{
	private SubsumptionArchitecture subsumptionArchitecture;
	
	private SubsumptionAction suppressedAction;
	
	private SubsumptionAction inhibitedAction;	
	
	/** object which permits pausing the thread*/
	private final Object LOCK = new Object();
	
	/** flag for pausing the thread*/
    private boolean pauseThreadFlag = false;
	
	private String name;
	
	private Mind mind;
	
	/**
	 * @param subsumptionArchitecture
	 */
	public SubsumptionAction(SubsumptionArchitecture subsumptionArchitecture, String name) 
	{
		super();
		this.subsumptionArchitecture = subsumptionArchitecture;
		this.name=name;
		if(subsumptionArchitecture!=null)
		{
			this.mind = subsumptionArchitecture.getMind();
			mind.insertCodelet(this);
		}
	}
	
	/**
	 * @param subsumptionArchitecture
	 */
	public SubsumptionAction(SubsumptionArchitecture subsumptionArchitecture) 
	{
		super();
		this.subsumptionArchitecture = subsumptionArchitecture;
		if(subsumptionArchitecture!=null)
		{
			this.mind = subsumptionArchitecture.getMind();
			mind.insertCodelet(this);
		}
	}
	
	/**
	 * Verifies if this action should try to suppress
	 * @return true if condition met
	 */
	public abstract boolean suppressCondition();
	
	/**
	 * Verifies if this action should try to inhibit
	 * @return true if condition met
	 */
	public abstract boolean inhibitCondition();
	
	/**
	 * The action performed by the subsumption action
	 */
	public abstract void act();
	
	/* (non-Javadoc)
	 * @see br.unicamp.cogsys.core.entities.Codelet#proc()
	 */
	@Override
	public void proc() 
	{
		try 
		{
			checkForPause();
		} catch (InterruptedException e1) 
		{			
			e1.printStackTrace();
		}
		
		if(suppressedAction!=null)
		{
			if(suppressCondition())
			{
				suppressAction();
			}			
			else
			{
				unSuppressAction();
			}
		}else if(inhibitedAction!=null)
		{
			if(inhibitCondition())
			{
				inhibitAction();
			}			
			else
			{
				uninhibitAction();
			}
		}else
		{
			act();
		}
	}
	
		
	/**
	 * @return the suppressedAction
	 */
	public SubsumptionAction getSuppressedAction() {
		return suppressedAction;
	}
	/**
	 * @param suppressedAction the suppressedAction to set
	 */
	public void setSuppressedAction(SubsumptionAction suppressedAction) {
		this.suppressedAction = suppressedAction;
	}
	/**
	 * @return the inhibitedAction
	 */
	public SubsumptionAction getInhibitedAction() {
		return inhibitedAction;
	}
	/**
	 * @param inhibitedAction the inhibitedAction to set
	 */
	public void setInhibitedAction(SubsumptionAction inhibitedAction) {
		this.inhibitedAction = inhibitedAction;
	}
	
	public boolean suppressAction()
	{
		boolean suppressed = false;
		
		if(suppressedAction!=null)
		{
			if(subsumptionArchitecture.permissionToSuppress(this,suppressedAction))
			{
				suppressedAction.pauseThread();
				act();
				suppressed = true;
			}
		}		
		return suppressed;
	}
	
	public boolean inhibitAction()
	{
		boolean inhibited = false;
		
		if(inhibitedAction!=null)
		{
			if(subsumptionArchitecture.permissionToInhibit(this,inhibitedAction))
			{
				inhibitedAction.pauseThread();
				act();
				inhibited = true;
			}
			
		}
		return inhibited;
	}	
	
	public void unSuppressAction()
	{
		if(suppressedAction!=null)
		{
			if(subsumptionArchitecture.permissionToSuppress(this,suppressedAction))
			{	
				suppressedAction.resumeThread();				
			}
		}
	}
	
	public void uninhibitAction()
	{
		if(inhibitedAction!=null)
		{
			if(subsumptionArchitecture.permissionToInhibit(this,inhibitedAction))
			{
				inhibitedAction.resumeThread();				
			}			
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	 protected void checkForPause() throws InterruptedException
	    {
	        if(pauseThreadFlag)
	        {
	            if(Thread.currentThread().getState().equals(Thread.State.RUNNABLE))
	            {
	                synchronized(LOCK)
	                {
	                    LOCK.wait();
	                    pauseThreadFlag = false;
	                }
	            }
	        }
	    }
	    
	    public void pauseThread()
	    {
	        pauseThreadFlag = true;
	    }
	    
	    public void resumeThread()
	    {    	
	    	synchronized(LOCK)
	    	{            	
	    		LOCK.notify();
	    		pauseThreadFlag = false;
	    	}
	    }
}
