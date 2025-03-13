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
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * MemoryBuffer is a generic holder for memory objects. It may be used to
 * produce sensory buffers, action buffers or other very short term memory
 * structures.
 * 
 * @author K. Raizer
 */

public class MemoryBuffer {

	/** A memory buffer is essentially a fifo list of MO */
	private ArrayList<MemoryObject> memoryObjects;

	private int maxCapacity;

	/** Safe lock for multithread access */
	public volatile Lock lock = new ReentrantLock();

	private RawMemory rawMemory;

	/**
	 * Safe access through reentrant locks.
	 * 
	 * @param accessing
	 *            the accessing Codelet
	 * @return true if it is impeding access
	 */
	public boolean impendingAccess(Codelet accessing) {
		Boolean myLock = false;
		Boolean yourLock = false;
		try {
			myLock = lock.tryLock();
			yourLock = accessing.lock.tryLock();
		} finally {
			if (!(myLock && yourLock)) {
				if (myLock) {
					lock.unlock();
				}
				if (yourLock) {
					accessing.lock.unlock();
				}
			}
		}
		return myLock && yourLock;
	}

	/**
	 * Creates a MemoryBuffer.
	 * 
	 * @param maxCapacity
	 *            maximum number of elements this buffer holds at a given time.
	 * @param rawMemory
	 *            singleton instance of the system's raw memory.
	 */
	public MemoryBuffer(int maxCapacity, RawMemory rawMemory) {
		memoryObjects = new ArrayList<MemoryObject>();
		this.maxCapacity = maxCapacity;
		this.rawMemory = rawMemory;
	}

	/**
	 * Adds a list of memory objects.
	 * 
	 * @param contents
	 *            list of Memory Objects to be added
	 */
	public synchronized void putList(List<MemoryObject> contents) {

		for (MemoryObject thisContent : contents) {
			if (memoryObjects.size() == maxCapacity) {

				memoryObjects.remove(0); // Gets rid of older content

			}
			memoryObjects.add(thisContent);
		}

	}

	/**
	 * Adds one memory object.
	 * 
	 * @param content
	 *            one memory object to be added.
	 */

	public synchronized void put(MemoryObject content) {

		if (memoryObjects.size() == maxCapacity) {
			if (rawMemory != null)
				rawMemory.destroyMemory(memoryObjects.get(0));// Gets rid
																	// of older
																	// content
																	// and
																	// deletes
																	// it from
																	// raw
																	// memory
			memoryObjects.remove(0);
		}

		memoryObjects.add(content);

	}

	/**
	 * Returns the original list of memory objects in this buffer.
	 * 
	 * @return the list of Memory objects inside Buffer.
	 */
	public synchronized List<MemoryObject> get() {
		return memoryObjects;
	}

	/**
	 * Returns a copy of the list of memory objects in this buffer.
	 * 
	 * @return the list of Memory objects inside Buffer.
	 */
	public synchronized ArrayList<MemoryObject> getAll() {
		ArrayList<MemoryObject> all = new ArrayList<MemoryObject>();
		all.addAll(this.memoryObjects);
		return memoryObjects;
	}

	/**
	 * Pops the first memory object that went into this list.
	 * 
	 * @return the first memory object that went into this list.
	 */
	public synchronized MemoryObject pop() {
		MemoryObject mo = memoryObjects.get(0);
		memoryObjects.remove(0);
		return mo;
	}

	/**
	 * Gets the size of the list of Memory objects inside Buffer.
	 * 
	 * @return size of the list of Memory objects inside Buffer.
	 */
	public synchronized int size() {
		return memoryObjects.size();
	}

	/**
	 * Gets the most recent Memory Object of this type.
	 * 
	 * @return the most recent Memory Object of this type.
	 */
	public synchronized MemoryObject getMostRecent() {
		MemoryObject mostRecent;

		if (memoryObjects.size() > 0) {
			mostRecent = memoryObjects.get(memoryObjects.size() - 1);

		} else {

			mostRecent = null;
		}

		return mostRecent;
	}

	/**
	 * Gets the oldest MemoryObject in this list.
	 * 
	 * @return the oldest MemoryObject in this list.
	 */
	public synchronized MemoryObject getOldest() {
		MemoryObject oldest;
		if (memoryObjects.size() > 0) {
			oldest = memoryObjects.get(0);
		} else {
			oldest = null;
		}
		return oldest;
	}

	/**
	 * Removes this memory object from this buffer and eliminates it from raw
	 * memory.
	 * 
	 * @param mo
	 *            memory object to be removed.
	 * @return result of of the removal process.
	 */
	public synchronized boolean remove(MemoryObject mo) {
		if (rawMemory != null)
			rawMemory.destroyMemory(mo);// removes this mo form RawMemory

		return memoryObjects.remove(mo);// removes this mo from this buffer;

	}

	/**
	 * Clears all memory objects from this buffer.
	 */
	public synchronized void clear() {
		if (rawMemory != null)
			for (int i = 0; i < memoryObjects.size(); i++) {
				rawMemory.destroyMemory(memoryObjects.get(i));
			}
		memoryObjects.clear();
	}

	/**
	 * Prints the status of the Buffer.
	 */
	public synchronized void printStatus() {
		System.out.println("###### Memory Buffer ########");
		System.out.println("# Content: " + this.get());
		System.out.println("# Size: " + this.get().size());
		System.out.println("###############################");

	}
}
