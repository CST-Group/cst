package br.unicamp.cst.bodyInterface.actuators;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.memory.WorkingStorage;

/**
 *  The Actuator class is a Codelet which writes to working storage every memory object written to its input list.
 *  If you remove a memory object from this actuator's input list, it will be removed from working storage as well (without deletion from raw memory).
 * @author Klaus
 *
 */

public abstract class Actuator extends Codelet
{
	private WorkingStorage ws;
	
	public Actuator(WorkingStorage ws)
	{
		this.ws=ws;
	}
	
	@Override
	public void pushInput(MemoryObject mo)
	{
		this.getInputs().add(mo);
		if(ws!=null)
			ws.putMemoryObject(mo);
	}
	@Override
	public void removesInput(MemoryObject mo)
	{
		this.getInputs().remove(mo);
		if(ws!=null)
			ws.removeFromWorkingStorageWithoutDelete(mo);		
	}
	
}
