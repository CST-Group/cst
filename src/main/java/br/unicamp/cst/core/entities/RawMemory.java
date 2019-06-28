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

package br.unicamp.cst.core.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * The Raw Memory contains all memories in the system.
 * 
 * @author A. L. O. Paraense
 * @author K. Raizer
 * @see Memory
 * @see MemoryObject
 * @see MemoryContainer
 *
 */
public class RawMemory {

	/**
	 * List of all memories in the system
	 */
	private List<Memory> allMemories;

	/**
	 * Crates a Raw Memory.
	 */
	public RawMemory() {
		allMemories = Collections.synchronizedList(new ArrayList<Memory>());
	}

	/**
	 * Gets all memories inside the raw memory.
	 * 
	 * @return the allMemoryObjects
	 */
	public synchronized List<Memory> getAllMemoryObjects() {
		synchronized (allMemories) {
			return allMemories;
		}
	}

	/**
	 * Returns a list of all memories in raw memory of a given type
	 * 
	 * @param type
	 *            of memory
	 * @return list of Ms of a given type
	 */
	public synchronized List<Memory> getAllOfType(String type) {
		List<Memory> listOfType = new ArrayList<Memory>();

		synchronized (allMemories) {
			for (Memory mo : this.allMemories) {
				if (mo.getName().equalsIgnoreCase(type)) {
					listOfType.add(mo);
				}
			}
		}

		return listOfType;

	}

	/**
	 * Sets the list of all memories inside raw memory.
	 * 
	 * @param allMemories
	 *            the allMemoryObjects to set.
	 */
	public synchronized void setAllMemoryObjects(List<Memory> allMemories) {
		synchronized (this.allMemories) {
			this.allMemories = allMemories;
		}
	}

	/**
	 * Print Raw Memory contents.
	 */
	public synchronized void printContent() {
		synchronized (allMemories) {
			for (Memory mo : allMemories) {
				System.out.println(mo.toString());
			}
		}
	}

	/**
	 * Adds a new Memory to the Raw Memory.
	 * 
	 * @param mo
	 *            memory to be added.
	 */
	@Deprecated
	public synchronized void addMemoryObject(Memory mo) {
		synchronized (allMemories) {
			allMemories.add(mo);
		}
	}

	/**
	 * Adds a new Memory to the Raw Memory.
	 * 
	 * @param mo
	 *            memory to be added.
	 */
	public synchronized void addMemory(Memory mo) {
		synchronized (allMemories) {
			allMemories.add(mo);
		}
	}

	/**
	 * Creates a memory container of the type passed.
	 * 
	 * @param name
	 *            the type of the memory container passed.
	 * @return the memory container created.
	 */
	public synchronized MemoryContainer createMemoryContainer(String name) {

		MemoryContainer mc = new MemoryContainer(name);

		this.addMemory(mc);

		return mc;

	}

	/**
	 * Creates a new MemoryObject and adds it to the Raw Memory, using provided
	 * info and type.
	 * 
	 * @param name
	 *            memory object type.
	 * @param info
	 *            memory object info.
	 * @return mo created MemoryObject.
	 */
	public synchronized MemoryObject createMemoryObject(String name, Object info) {
		// memory object to be added to rawmemory
		MemoryObject mo = new MemoryObject();
		mo.setI(info);
		mo.setTimestamp(System.currentTimeMillis());
		mo.setEvaluation(0.0d);
		mo.setType(name);

		// adding the new object to raw memory
		this.addMemory(mo);
		return mo;
	}

	/**
	 * Creates a memory object of the type passed.
	 * 
	 * @param name
	 *            the type of the memory object created.
	 * @return the memory object created.
	 */
	public synchronized MemoryObject createMemoryObject(String name) {
		return createMemoryObject(name, "");
	}

	/**
	 * Destroys a given memory from raw memory
	 * 
	 * @param mo
	 *            the memory to destroy.
	 */
	public synchronized void destroyMemoryObject(Memory mo) {
		synchronized (allMemories) {
			allMemories.remove(mo);
		}
	}

	/**
	 * Gets the size of the raw memory.
	 * 
	 * @return size of Raw Memory.
	 */
	public synchronized int size() {
		synchronized (allMemories) {
			return allMemories.size();
		}
	}

	/**
	 * Removes all memory objects from RawMemory.
	 */
	public void shutDown() {
		synchronized (allMemories) {
			this.allMemories.clear();
		}
	}
}
