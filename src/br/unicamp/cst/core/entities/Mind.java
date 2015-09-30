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
 * @author andre
 *
 */
public class Mind 
{
	private CodeRack codeRack;

	private RawMemory rawMemory;

	public Mind()
	{
		codeRack = new CodeRack();

		rawMemory = new RawMemory();
	}

	/**
	 * @return the codeRack
	 */
	public synchronized CodeRack getCodeRack() 
	{
		return codeRack;
	}

	/**
	 * @return the rawMemory
	 */
	public synchronized RawMemory getRawMemory() 
	{
		return rawMemory;
	}

	/**
	 * Creates a new MemoryObject and adds it to the Raw Memory, using provided info and type
	 * 
	 * @param type memory object type
	 * @param info memory object info
	 * @return mo created MemoryObject
	 */
//	public synchronized MemoryObject createMemoryObject(MemoryObjectType type, String info)
//	{
//		MemoryObject mo = null;
//
//		if(rawMemory!=null)
//			mo = rawMemory.createMemoryObject(type, info);
//
//		return mo;
//
//	}
	/**
	 * Creates a new MemoryObject (Java style) and adds it to the Raw Memory, using provided info and type
	 * 
	 * @param type memory object type
	 * @param info memory object info
	 * @return mo created MemoryObject
	 */
	public synchronized MemoryObject createMemoryObject(String name, Object info)
	{
		MemoryObject mo = null;

		if(rawMemory!=null)
			mo = rawMemory.createMemoryObject(name, info);

		return mo;
	}

	public synchronized MemoryObject createMemoryObject(String name) 
	{
		return createMemoryObject(name, null);
	}

	public Codelet insertCodelet(Codelet co)
	{
		if(codeRack!=null)
			codeRack.addCodelet(co);

		return co;
	}
	
	/**
	 * Starts all codelets in coderack.
	 */
	public void start() 
	{
		if(codeRack!=null)
			codeRack.start();
	}
        
	/**
	 * Stops codelets thread.
	 */
	public void shutDown()
	{
		if(codeRack!=null)
			codeRack.shutDown();
	}
}
