package br.unicamp.cst.bodyInterface.sensors;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.memory.WorkingStorage;
/**
 *  The Sensor class is a Codelet which writes to working storage every memory object written to its output list.
 *  If you remove a memory object from this sensor's output list, it will be removed from working storage as well (without deletion from raw memory).
 * @author Klaus
 *
 */
public abstract class Sensor extends Codelet
{
	private WorkingStorage ws= WorkingStorage.getInstance();
	public Sensor()
	{
		
	}
	
	@Override
	public synchronized void pushOutput(MemoryObject mo){
		this.getOutputs().add(mo);
		ws.putMemoryObject(mo);
	}
	@Override
	public synchronized void removesOutput(MemoryObject mo){
		this.getOutputs().remove(mo);
		ws.removeFromWorkingStorageWithoutDelete(mo);		
	}
}
