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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        protected ConcurrentHashMap<String,ArrayList> codeletGroups;
        protected ConcurrentHashMap<String,ArrayList> memoryGroups;

	/**
	 * Creates the Mind.
	 */
	public Mind() {
		codeRack = new CodeRack();
		rawMemory = new RawMemory();
		codeletGroups = new ConcurrentHashMap();
		memoryGroups = new ConcurrentHashMap();
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
            codeletGroups.put(groupName,group);
        }
        
        /**
	 * Creates a Memory Group
	 * 
	 * @param groupName The Group name
	 * 
	 */
        public synchronized void createMemoryGroup(String groupName) {
            ArrayList<Memory> group = new ArrayList<Memory>();
            memoryGroups.put(groupName,group);
        }
        
        /**
         * Returns the full HashMap which for every codelet group Name it is associated a list of codeletGroups
         * 
         * @return the HashMap with all pairs (groupname,list of codeletGroups belonging to groupname)
         */
        public ConcurrentHashMap<String,ArrayList> getCodeletGroups() {
            return(codeletGroups);
        }
        
        /**
         * Returns the full HashMap which for every memory group Name it is associated a list of codeletGroups
         * 
         * @return the HashMap with all pairs (groupname,list of codeletGroups belonging to groupname)
         */
        public ConcurrentHashMap<String,ArrayList> getMemoryGroups() {
            return(memoryGroups);
        }
        
        /**
         * Returns the number of registered codelet groups
         * 
         * @return the number of registered groups
         */
        public int getCodeletGroupsNumber() {
            return(codeletGroups.size());
        }
        
        /**
         * Returns the number of registered memory groups
         * 
         * @return the number of registered groups
         */
        public int getMemoryGroupsNumber() {
            return(memoryGroups.size());
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
	 * @param hostname
	 *            hostname of the REST server
	 * @param port
	 *            port of the REST server
	 * @return mo created MemoryObject.
	 */
	public synchronized RESTMemoryObject createRESTMemoryObject(String name, String hostname, int port) {
		RESTMemoryObject mo = null;

		if (rawMemory != null)
			mo = rawMemory.createRESTMemoryObject(name, hostname, port);

		return mo;
	}

	/**
	 * Creates a new MemoryObject and adds it to the Raw Memory, using provided
	 * type.
	 * 
	 * @param name
	 *            memory object type.
	 * @param port
	 *            port of the REST server
	 * @return created MemoryObject.
	 */
	public synchronized RESTMemoryObject createRESTMemoryObject(String name, int port) {
		return createRESTMemoryObject(name, "localhost", port);
	}



	/**
	 * Creates a new MemoryObject and adds it to the Raw Memory, using provided
	 * info and type.
	 *
	 * @param name
	 *            memory object name.
	 * @param hostname
	 *            hostname of the REST server
	 * @param port
	 *            port of the REST server
	 * @return mo created MemoryObject.
	 */
	public synchronized RESTMemoryContainer createRESTMemoryContainer(String name, String hostname, int port) {
		RESTMemoryContainer mo = null;

		if (rawMemory != null)
			mo = rawMemory.createRESTMemoryContainer(name, hostname, port);

		return mo;
	}

	/**
	 * Creates a new MemoryObject and adds it to the Raw Memory, using provided
	 * type.
	 *
	 * @param name
	 *            memory object type.
	 * @param port
	 *            port of the REST server
	 * @return created MemoryObject.
	 */
	public synchronized RESTMemoryContainer createRESTMemoryContainer(String name, int port) {
		return createRESTMemoryContainer(name, "localhost", port);
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
            ArrayList<Codelet> groupList = codeletGroups.get(groupName);
            if (groupList != null) groupList.add(co);
            else Logger.getAnonymousLogger().log(Level.INFO,"The Codelet Group {0} still does not have been created ... create it first with createCodeletGroup",groupName);
        }
        
        /**
         * Register a Memory within a group
         * 
         * @param m the Memory
         * @param groupName the group name
         */
        public void registerMemory(Memory m, String groupName) {
            ArrayList<Memory> groupList = memoryGroups.get(groupName);
            if (groupList != null) groupList.add(m);
            else Logger.getAnonymousLogger().log(Level.INFO,"The Memory Group {0} still does not have been created ... create it first with createMemoryGroup",groupName);    
        }
        
        
        /**
         * Register a Memory within a group by name.
         * 
         * @param m the Memory
         * @param groupName the group name
         */
        public void registerMemory(String m, String groupName) {
            ArrayList<Memory> groupList = memoryGroups.get(groupName);
            RawMemory rm = getRawMemory();
            if (groupList != null && rm != null) {
                List<Memory> all = rm.getAllOfType(m);
                for (Memory mem : all) {
                    groupList.add(mem);
                }
            }
        }

        /**
         * Get a list of all Codelets belonging to a group
         * 
         * @param groupName the group name to which the Codelets belong
         * @return A list of all codeletGroups belonging to the group indicated by groupName
         */
        public ArrayList<Codelet> getCodeletGroupList(String groupName) {
                return(codeletGroups.get(groupName));
        }
        
        /**
         * Get a list of all Memories belonging to a group
         * 
         * @param groupName the group name to which the Memory belong
         * @return A list of all memoryGroups belonging to the group indicated by groupName
         */
        public ArrayList<Memory> getMemoryGroupList(String groupName) {
                return(memoryGroups.get(groupName));
        }
        
        
	/**
	 * Starts all codeletGroups in coderack.
	 */
	public void start() {
		if (codeRack != null)
			codeRack.start();
	}

	/**
	 * Stops codeletGroups thread.
	 */
	public void shutDown() {
		if (codeRack != null)
			codeRack.shutDown();
	}

}
