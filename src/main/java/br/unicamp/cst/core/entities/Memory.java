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

/**
 * This class represents the interface for all kinds of memories that exist in
 * CST. In order to be recognized as a Memory, an entity must implement this
 * interface. Currently, there are to kinds of Memory: MemoryObject and
 * MemoryContainer. However, other forms of Memory might come up as CST
 * develops.
 * 
 * @author A. L. O. Paraense
 * @see MemoryObject
 * @see MemoryContainer
 *
 */
public interface Memory {

	/**
	 * Gets the info inside this memory.
	 * 
	 * @return the info in memory.
	 */
	Object getI();

	/**
	 * Sets the info inside this Memory.
	 * 
	 * @param info
	 *            the updated info to set in memory.
	 * @return index of the memory inside the container or -1 if not a
	 *         container.
	 */
	int setI(Object info);

	/**
	 * Gets the evaluation of this memory.
	 * 
	 * @return the evaluation of this memory.
	 */
	Double getEvaluation();

	/**
	 * Gets the type of this memory.
	 * 
	 * @return the type of the memory.
	 */
	String getName();

	/**
	 * Sets the type of this memory.
	 *
	 *@param type
	 * 	 *            the value to be set as type.
	 */
	void setType(String type);

	/**
	 * Sets the evaluation of this memory.
	 * 
	 * @param eval
	 *            the value to be set as evaluation.
	 */
	void setEvaluation(Double eval);
        
        /**
	 * Gets the timestamp of this Memory.
	 * 
	 * @return the timestamp of this Memory.
	 */
	public Long getTimestamp();
	
	/**
	 * Add a memory observer to its list
	 * @param memoryObserver
	 */
	public void addMemoryObserver(MemoryObserver memoryObserver);
	

}
