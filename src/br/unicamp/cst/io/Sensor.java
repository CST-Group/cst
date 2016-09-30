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

package br.unicamp.cst.io;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.memory.WorkingStorage;
/**
 *  The Sensor class is a Codelet which writes to working storage every memory object written to its output list.
 *  If you remove a memory object from this sensor's output list, it will be removed from working storage as well (without deletion from raw memory).
 * @author Klaus
 *
 */
public abstract class Sensor extends Codelet
{
	private WorkingStorage ws;
	
	public Sensor(WorkingStorage ws)
	{
		this.ws=ws;
	}
	
	@Override
	public synchronized void addOutput(Memory mo)
	{
		this.getOutputs().add(mo);
		if(ws!=null)
			ws.putMemoryObject(mo);
	}
	@Override
	public synchronized void removesOutput(Memory mo)
	{
		this.getOutputs().remove(mo);
		if(ws!=null)
			ws.removeFromWorkingStorageWithoutDelete(mo);		
	}
}
