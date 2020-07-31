/**********************************************************************************************
 * Copyright (c) 2012-2017  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * Contributors:
 *     K. Raizer, A. L. O. Paraense, E. M. Froes, R. R. Gudwin - initial API and implementation
 **********************************************************************************************/

package br.unicamp.cst.core.entities;

import br.unicamp.cst.bindings.soar.PlansSubsystemModule;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class represents the Mind of the agent, wrapping all the CST's core
 * entities.
 * 
 * @author A. L. O. Paraense
 * @author E. M. Froes
 */
public class Mind {

	protected CodeRack codeRack;
	protected RawMemory rawMemory;
        protected ConcurrentHashMap<String,ArrayList> codelets;

	private PlansSubsystemModule plansSubsystemModule;

	/**
	 * Creates the Mind.
	 */
	public Mind() {
		codeRack = new CodeRack();
		rawMemory = new RawMemory();
                codelets = new ConcurrentHashMap();
		plansSubsystemModule = new PlansSubsystemModule();
	}

	/**
	 * Gets the CodeRack.
	 * 
	 * @return the codeRack.
	 */
	public synchronized CodeRack getCodeRack() {
		return codeRack;
	}

	/**
	 * Gets the RawMemory.
	 * 
	 * @return the rawMemory.
	 */
	public synchronized RawMemory getRawMemory() {
		return rawMemory;
	}

	/**
	 * Creates a Codelet Group
	 * 
	 * @param groupName The Group name
	 * 
	 */
        public synchronized void createCodeletGroup(String groupName) {
            ArrayList<Codelet> group = new ArrayList<Codelet>();
            codelets.put(groupName,group);
        }
        
        /**
         * Returns the full HashMap which for every group Name it is associated a list of codelets
         * 
         * @return the HashMap with all pairs (groupname,list of codelets belonging to groupname)
         */
        public ConcurrentHashMap<String,ArrayList> getGroups() {
            return(codelets);
        }
        
        /**
         * Returns the number of registered groups
         * 
         * @return the number of registered groups
         */
        public int getGroupsNumber() {
            return(codelets.size());
        }
        
	/**
	 * Creates a Memory Container inside the Mind of a given type.
	 * 
	 * @param name
	 *            the type of the Memory Container to be created inside the
	 *            Mind.
	 * @return the Memory Container created.
	 */
	public synchronized MemoryContainer createMemoryContainer(String name) {

		MemoryContainer mc = null;

		if (rawMemory != null)
			mc = rawMemory.createMemoryContainer(name);

		return mc;

	}

	/**
	 * Creates a new MemoryObject and adds it to the Raw Memory, using provided
	 * info and type.
	 *
	 * @param name
	 *            memory object name.
	 * @param info
	 *            memory object info.
	 * @return mo created MemoryObject.
	 */
	public synchronized MemoryObject createMemoryObject(String name, Object info) {
		MemoryObject mo = null;

		if (rawMemory != null)
			mo = rawMemory.createMemoryObject(name, info);

		return mo;
	}

	/**
	 * Creates a new MemoryObject and adds it to the Raw Memory, using provided
	 * type.
	 * 
	 * @param name
	 *            memory object type.
	 * @return created MemoryObject.
	 */
	public synchronized MemoryObject createMemoryObject(String name) {
		return createMemoryObject(name, null);
	}

	/**
	 * Inserts the Codelet passed in the Mind's CodeRack.
	 * 
	 * @param co
	 *            the Codelet passed
	 * @return the Codelet.
	 */
	public Codelet insertCodelet(Codelet co) {
		if (codeRack != null)
			codeRack.addCodelet(co);
		return co;
	}

	/**
	 * Inserts the Codelet passed in the Mind's CodeRack.
	 * 
	 * @param co the Codelet to be inserted in the Mind
         * @param groupName the Codelet group name
	 * @return the Codelet.
	 */
	public Codelet insertCodelet(Codelet co, String groupName) {
                insertCodelet(co);
                registerCodelet(co,groupName);
                return co;
	}

        /**
         * Register a Codelet within a group
         * 
         * @param co the Codelet
         * @param groupName the group name
         */
        public void registerCodelet(Codelet co, String groupName) {
            ArrayList<Codelet> groupList = codelets.get(groupName);
                if (groupList != null) groupList.add(co);
        }
        

        /**
         * Get a list of all Codelets belonging to a group
         * 
         * @param groupName the group name to which the Codelets belong
         * @return A list of all codelets belonging to the group indicated by groupName
         */
        public ArrayList<Codelet> getGroupList(String groupName) {
                return(codelets.get(groupName));
        }
        
        
	/**
	 * Starts all codelets in coderack.
	 */
	public void start() {
		if (codeRack != null)
			codeRack.start();
	}

	/**
	 * Stops codelets thread.
	 */
	public void shutDown() {
		if (codeRack != null)
			codeRack.shutDown();
	}

	/**
	 * Gets the Plans Subsystem Module.
	 * 
	 * @return the Plans Subsystem Module.
	 */
	public PlansSubsystemModule getPlansSubsystemModule() {
		return plansSubsystemModule;
	}

	/**
	 * Sets the Plans Subsystem Module.
	 * 
	 * @param plansSubsystemModule
	 *            the Plans Subsystem Module to set.
	 */
	public void setPlansSubsystemModule(PlansSubsystemModule plansSubsystemModule) {
		this.plansSubsystemModule = plansSubsystemModule;
	}
}
