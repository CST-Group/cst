/**
 * 
 */
package br.unicamp.cst.perception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.memory.WorkingStorage;

/**
 * This Perception class allows us to create codelets with additional methods.
 * A perception codelet has the property of always sending its output memory objects to working storage.
 * Note: A Perception Codelet clears its output list at every iteration.
 * 
 * 
 * @author Klaus Raizer
 *
 */
public abstract class Perception extends Codelet{

	private HashMap<MemoryObject, Boolean> world_state_log = new HashMap<MemoryObject, Boolean>();
	private ArrayList<MemoryObject> list_of_true_world_states=new ArrayList<MemoryObject>();
	private ArrayList<MemoryObject> list_of_false_world_states=new ArrayList<MemoryObject>();
	
	private ArrayList<MemoryObject> known_percepts=new ArrayList<MemoryObject>();//list of all known percepts (only grows with time)
	private ArrayList<MemoryObject> detected_percepts=new ArrayList<MemoryObject>();//list of all percepts that were detected by the codelet (is reset after uptadeOutputWithPercepts is called)
	
	/**
	 * This method pushes a memory object representing a percept detected in the world into a list, so this perception codelet knows what is true.
	 * Important: Remember to call uptadeOutputWithPercepts() in order to keep pushed percepts into this codelet's output list.
	 * @param pe
	 */
	public synchronized void pushDetectedPercept(MemoryObject pe){
		if(!this.detected_percepts.contains(pe)){
			this.detected_percepts.add(pe);
		}
		if(!this.known_percepts.contains(pe)){
			this.known_percepts.add(pe);
		}
		
	}
	/**
	 * This method makes sure all detected percepts are kept in this codelet's output list.
	 * All known percepts that were not detected should be removed from output list if any of them are still there.
	 * Being a Perception codelet, it keeps its output list synched with Working Storage at all times.
	 */
	public synchronized void uptadeOutputWithPercepts(){

		ArrayList<MemoryObject> D = new ArrayList<MemoryObject>();
		ArrayList<MemoryObject> K = new ArrayList<MemoryObject>();
		ArrayList<MemoryObject> O = new ArrayList<MemoryObject>();
		
		ArrayList<MemoryObject> addDK = new ArrayList<MemoryObject>();
		ArrayList<MemoryObject> addDO = new ArrayList<MemoryObject>();
		ArrayList<MemoryObject> remove = new ArrayList<MemoryObject>();
		
		D.addAll(this.detected_percepts);
		K.addAll(this.known_percepts);
		O.addAll(this.getOutputs());
		
		//Intersection between D and K, minus O
		addDK.addAll(D);
		addDK.retainAll(K);
		addDK.removeAll(O);
		//Intersection between D and O, minus K
		addDO.addAll(D);
		addDO.retainAll(O);
		addDO.removeAll(K);
		//Intersection between O and K minus D
		remove.addAll(O);
		remove.retainAll(K);
		remove.removeAll(D);
		
		
		for(MemoryObject mo : addDK){
			this.pushOutput(mo);
		}
		for(MemoryObject mo : addDO){
			this.pushOutput(mo);
		}
		for(MemoryObject mo : remove){
			this.removesOutput(mo);
		}

		//Resets detected list
		this.detected_percepts.clear();
		
		
//		
//		
//				ArrayList<MemoryObject> outputs = new ArrayList<MemoryObject>();
//				outputs.addAll(this.getOutputs());
//				
//				ArrayList<MemoryObject> detected=new ArrayList<MemoryObject>();
//				detected.addAll(this.detected_percepts);
//				detected.removeAll(outputs);
//
//				//Removing false propositions
//				for(MemoryObject mo:outputs){
//					if(!detected_percepts.contains(mo.getInfo())){
//						this.removesOutput(mo);
//					}
//				}
//
//
//				outputs = new ArrayList<MemoryObject>();
//				outputs.addAll(this.getOutputs());
//
//				ArrayList<MemoryObject> undetected=new ArrayList<MemoryObject>();
//				undetected.addAll(this.known_percepts);
//				undetected.removeAll(outputs);
//				
//				
//				
//				//Adding true propositions
//				for(String trueProp:trueStates){
//					MemoryObject mo = worldState_string_mo.get(trueProp);
//					if(!outputs.contains(mo)){
//						this.pushOutput(mo);
//					}
//				}
	}
	
	@Override
	public synchronized void pushOutput(MemoryObject output)
	{
		this.getOutputs().add(output);
		
		list_of_true_world_states.add(output);
		list_of_false_world_states.remove(output);
		
		this.synchWithWorkingStorage();
	}
	
	@Override
	public synchronized void removesOutput(MemoryObject output)
	{	
		this.getOutputs().remove(output);
		
		list_of_true_world_states.remove(output);
		list_of_false_world_states.add(output);
		
		this.synchWithWorkingStorage();
	}
/**
 * Makes sure what this perception codelet perceives as true, remains in working storage, while what it perceives as being false does not.
 * This method resets the list of detected percepts.
 */
	private void synchWithWorkingStorage(){
		//TODO Remember to acquire lock!
		ArrayList<MemoryObject> alreadyInWorkingStorage = new ArrayList<MemoryObject>();
		WorkingStorage localWS=WorkingStorage.getInstance();
		alreadyInWorkingStorage.addAll(localWS.getAll());
//		alreadyInWorkingStorage.retainAll(this.getOutputs());
		
		ArrayList<MemoryObject> mustAdd = new ArrayList<MemoryObject>();
		mustAdd.addAll(list_of_true_world_states);
		mustAdd.removeAll(alreadyInWorkingStorage);

		ArrayList<MemoryObject> mustRemove = new ArrayList<MemoryObject>();
		mustRemove.addAll(list_of_false_world_states);
		
		for(MemoryObject mo : mustRemove){
			localWS.removeFromWorkingStorageWithoutDelete(mo);
		}
		for(MemoryObject mo:mustAdd){
			localWS.putMemoryObject(mo);
		}
	}



}
