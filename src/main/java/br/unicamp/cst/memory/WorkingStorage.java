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

package br.unicamp.cst.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.RawMemory;
import br.unicamp.cst.core.entities.Subject;



/**
 * "In the brain, working storage is believed to involve the medial
 * temporal cortex and prefrontal regions. Working storage is dynamic-
 * i.e. it involves active populations of neurons called cell 
 * assemblies, which can 'crystalize' into permanent memories."[Baars and Gage 2010]
 * 
 * @author klaus raizer
 */

public class WorkingStorage  implements Subject
{
	private HashMap<Codelet,HashMap<String,List<Memory>>> what_ws_sent_to_codelets_inputs = new HashMap<Codelet, HashMap<String,List<Memory>>>();
	private HashMap<Codelet,HashMap<String,List<Memory>>> what_ws_sent_to_codelets_outputs = new HashMap<Codelet, HashMap<String,List<Memory>>>();

	private HashMap<String, List<Codelet>>  codelet_input_registry_by_type = new HashMap<String,List<Codelet>>();
	private HashMap<String, List<Codelet>>  codelet_output_registry_by_type = new HashMap<String,List<Codelet>>();


	private HashMap<String,ArrayList<Memory>> workingStorageContents = new HashMap<String, ArrayList<Memory>>();
	private ArrayList<Memory> workingStorageContentList = new ArrayList<Memory>();
	private int maxCapacity;
	private boolean useHashMap=false;
	
	private RawMemory rawMemory;

	public WorkingStorage(int maxCapacity,RawMemory rawMemory)
	{
		this.rawMemory = rawMemory;
		setMaxCapacity(maxCapacity);

	}

	/**
	 * 
	 * Avoids cloning
	 */
	public Object clone() throws CloneNotSupportedException{
		throw new CloneNotSupportedException();
	}

	/**
	 * Returns a list of all memory objects of a given type. 
	 * @param type of the wished memory objects
	 * @return list of memory objects to be returned
	 */
	public synchronized ArrayList<Memory> getAllOfType(String type){
		ArrayList<Memory> allOfType=new ArrayList<Memory>();
		if(useHashMap){

			ArrayList<Memory> temp=workingStorageContents.get(type);
			if(temp!=null){
				allOfType.addAll(workingStorageContents.get(type));
			}
		}else{

			for(Memory mo:workingStorageContentList){
				if(mo.getName().equalsIgnoreCase(type)){
					allOfType.add(mo);
				}
			}
		}
		//System.out.println("ALL OF TYPE new: "+allOfType);
		return allOfType;
	}

	/**
	 * @param maxCapacity the maxCapacity to set
	 */
	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}
	/**
	 * @return the maxCapacity
	 */
	public int getMaxCapacity() {
		return maxCapacity;
	}
	/**
	 * Prints all memory objects in Working Storage, organized by TYPE.
	 */
	public synchronized void printStatus() {

		ArrayList<Memory> allMOs=this.getAll();
		HashMap<String,ArrayList<Memory>> organizedMOs= new HashMap<String,ArrayList<Memory>>();
		for(Memory mo : allMOs){
			String key = mo.getName();
			if(organizedMOs.containsKey(key)){
				organizedMOs.get(key).add(mo);
			}else{
				ArrayList<Memory> newList = new ArrayList<Memory>();
				newList.add(mo);
				organizedMOs.put(key, newList);
			}
		}

		System.out.println("###### Working Storage ########");
		System.out.println("# Number of Memory Objects: "+allMOs.size());
//		System.out.println(allMOs);
		Iterator it = organizedMOs.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        
	        System.out.println("--- Type: "+pairs.getKey()+" ---");
	        ArrayList<Memory> listaMOs=(ArrayList<Memory>) pairs.getValue();
	        for(Memory mo: listaMOs){
	        	System.out.println(mo);
	        }
	        
	    }
		
		
		System.out.println("###############################");

	}
	/**
	 * @return All memory objects in Working Storage
	 */
	public synchronized ArrayList<Memory> getAll() {

		ArrayList<Memory> allMOs=new ArrayList<Memory>();
		allMOs.addAll(workingStorageContentList);

		return allMOs;
	}

	public synchronized boolean contains(Memory mo) {

		String type = mo.getName();
		ArrayList<Memory> allOfType = this.getAllOfType(type);

		boolean contains=false;

		if(allOfType!=null){
			contains=allOfType.contains(mo);
		}else{
			contains=false; //not needed
		}

		return contains;
	}

	/**
	 *  Adds a memory object to working storage
	 * @param mo one memory object to be added
	 */

	public synchronized void putMemoryObject(Memory mo)
	{
		if((mo!=null)){//TODO should i get rid of older content?
			
//			boolean not_contain_old = !workingStorageContentList.contains(mo); //Compares by content!
			boolean not_contain=true;
			ArrayList<Memory> tempList= new ArrayList<Memory>();
			tempList.addAll(workingStorageContentList);
			
			for(Memory oldMo:tempList){
				if(oldMo==mo){
					not_contain=false;
				}
			}
			
			
			if(not_contain){//TODO This is not working properlly!  
				workingStorageContentList.add(mo);
				//				System.out.println("Trying to add: "+mo);

				//In update, I must check if this type of mo should be sent to any registered codelet
				// if so, I need to take note at what_ws_sent_to_codelets
				String type = mo.getName();

				if(codelet_input_registry_by_type.containsKey(type)){
					List<Codelet> setOfCodelets = codelet_input_registry_by_type.get(type);
					updateCodeletsList(setOfCodelets,type,0);
				}
				if(codelet_output_registry_by_type.containsKey(type)){
					List<Codelet> setOfCodelets = codelet_output_registry_by_type.get(type);
					updateCodeletsList(setOfCodelets,type,1);
				}


			}
		}

	}


	private synchronized void updateCodeletsList(List<Codelet> setOfCodelets,String type, int io) {
		//TODO remember to acquire lock!
		List<Memory> sentLista=null;


		for(Codelet co : setOfCodelets){
			List<Memory> lista=null;
			if(io==0){//update inputs
				lista=co.getInputs();
				if(what_ws_sent_to_codelets_inputs.containsKey(co)){
					HashMap<String, List<Memory>> listOfTypes = what_ws_sent_to_codelets_inputs.get(co);
					if(listOfTypes.containsKey(type)){
						sentLista = what_ws_sent_to_codelets_inputs.get(co).get(type);
					}
				}else{
					HashMap<String, List<Memory>> listOfTypes = new HashMap<String, List<Memory>>();
					sentLista = new ArrayList<Memory>();
					listOfTypes.put(type, sentLista);
					what_ws_sent_to_codelets_inputs.put(co, listOfTypes);

				}

			}else if(io==1){//update outputs
				lista=co.getOutputs();
				if(what_ws_sent_to_codelets_outputs.containsKey(co)){
					HashMap<String, List<Memory>> listOfTypes = what_ws_sent_to_codelets_outputs.get(co);
					if(listOfTypes.containsKey(type)){
						sentLista = what_ws_sent_to_codelets_outputs.get(co).get(type);
					}
				}else{
					HashMap<String, List<Memory>> listOfTypes = new HashMap<String, List<Memory>>();
					sentLista = new ArrayList<Memory>();
					listOfTypes.put(type, sentLista);
					what_ws_sent_to_codelets_outputs.put(co, listOfTypes);

				}

			}else{
				throw new IllegalArgumentException();
			}



			ArrayList<Memory> whats_missing = new ArrayList<Memory>();
			whats_missing.addAll(this.getAllOfType(type));
			whats_missing.removeAll(lista);

			if(io==0){//update inputs
				co.addInputs(whats_missing);
			}else if(io==1){//update outputs
				co.addOutputs(whats_missing);
			}else{
				throw new IllegalArgumentException();
			}



			sentLista.addAll(whats_missing);//recording what was sent to codelets
		}

	}

	/**
	 *  Removes this memory object from Working Storage if it is there, but keeps it in Raw Memory.
	 *  Be careful when using this!
	 * @param mo
	 */
	public synchronized void removeFromWorkingStorageWithoutDelete(Memory mo) {
		//		System.out.println("--> Working storage before removal: "+this.getAll());

		workingStorageContentList.remove(mo);

		// Must also remove from registered codelet's lists
		//If I do so, check if I was responsible for putting it there in the first place, by looking at what_ws_sent_to_codelets
		//Only then should I remove it from the codelet's IO list
		// (if it was not in what_ws_sent_to_codelets, then it is not my responsibility to remove it)

		String type=mo.getName();

		if(codelet_input_registry_by_type.containsKey(type)){
			List<Codelet> codelets = codelet_input_registry_by_type.get(type);

			//TODO preciso fazer um loop aqui

			//			private HashMap<Codelet,HashMap<MemoryObjectType,List<Memory>>> what_ws_sent_to_codelets_inputs = new HashMap<Codelet, HashMap<MemoryObjectType,List<Memory>>>();
			//			private HashMap<Codelet,HashMap<MemoryObjectType,List<Memory>>> what_ws_sent_to_codelets_outputs = new HashMap<Codelet, HashMap<MemoryObjectType,List<Memory>>>();



			for(Codelet co :codelets){
				if(what_ws_sent_to_codelets_inputs.containsKey(co)){
					HashMap<String, List<Memory>> listOfTypes = what_ws_sent_to_codelets_inputs.get(co);
					if(listOfTypes.containsKey(type)){
						List<Memory> mos = listOfTypes.get(type);		
						List<Memory> tempMo= new ArrayList<Memory>();
						tempMo.add(mo);
						co.removeFromInput(tempMo);
						mos.remove(mo);
					}
				}
			}
		}
		if(codelet_output_registry_by_type.containsKey(type)){
			List<Codelet> codelets = codelet_output_registry_by_type.get(type);

			for(Codelet co :codelets){
				if(what_ws_sent_to_codelets_outputs.containsKey(co)){
					HashMap<String, List<Memory>> listOfTypes = what_ws_sent_to_codelets_outputs.get(codelets);
					if(listOfTypes!=null && listOfTypes.containsKey(type)){
						List<Memory> mos = listOfTypes.get(type);		
						List<Memory> tempMo= new ArrayList<Memory>();
						tempMo.add(mo);
						co.removeFromOutput(tempMo);
						mos.remove(mo);
					}
				}
			}
		}

	}

	/**
	 * WARNING!
	 *  Removes this memory object from Working Storage if it is there, and also removes it from Raw Memory
	 * @param bpMo
	 */

	public synchronized void removeFromWorkingStorageWithDelete(Memory bpMo) 
	{
		workingStorageContentList.remove(bpMo);
		if(rawMemory!=null)
			rawMemory.destroyMemory(bpMo);

	}

	/**
	 * Removes all memory objects from Working Memory and delete them from raw memory.
	 * Be careful when using this method!
	 */
	public void clearWithDelete() 
	{
		if(rawMemory!=null && workingStorageContentList!=null)
			for(Memory mo: workingStorageContentList)
			{	
				rawMemory.destroyMemory(mo);
			}
		this.workingStorageContentList.clear();

	}


	//	public enum IO{
	//		INPUT,
	//		OUTPUT;
	//	}


	@Override
	public void registerCodelet(Codelet co, String type, int io) {
		//		IO ioList;

		if(io==0){
			//			ioList = IO.INPUT;
			if(codelet_input_registry_by_type.containsKey(type)){
				List<Codelet> setOfCodelets = codelet_input_registry_by_type.get(type);
				setOfCodelets.add(co);

			}else{
				List<Codelet> setOfCodelets = new ArrayList<Codelet>();
				setOfCodelets.add(co);
				codelet_input_registry_by_type.put(type, setOfCodelets);
			}

		}else if(io==1){
			//			ioList = IO.OUTPUT;
			if(codelet_output_registry_by_type.containsKey(type)){
				List<Codelet> setOfCodelets = codelet_output_registry_by_type.get(type);
				setOfCodelets.add(co);

			}else{
				List<Codelet> setOfCodelets = new ArrayList<Codelet>();
				setOfCodelets.add(co);
				codelet_output_registry_by_type.put(type, setOfCodelets);
			}
		}else{
			throw new IllegalArgumentException();
		}
		//		//TODO I need to look for memory objects that are already in working storage and add them to the newly registered codelet
		//		ArrayList<Memory> allOfType = new ArrayList<Memory>();
		//		
		//		allOfType.addAll(this.getAllOfType(type));
		//		//TODO Get a lock here?
		//		
		//		ArrayList<Codelet> setOfCodelets = new ArrayList<Codelet>();
		//		setOfCodelets.add(co);
		//		updateCodeletsList(setOfCodelets,type,io);
		//		
		//I need to look for memory objects that are already in working storage and add them to the newly registered codelet
		//After that, I need to register those memory objects at what_ws_sent_to_codelets_inputs or what_ws_sent_to_codelets_outputs.

		ArrayList<Memory> allOfType = new ArrayList<Memory>();
		allOfType.addAll(this.getAllOfType(type));

		//TODO Get a lock here?

		if(io==0){
			allOfType.removeAll(co.getInputs());
			co.addInputs(allOfType); //Adding to codelet
			if(what_ws_sent_to_codelets_inputs.containsKey(co)){
				HashMap<String, List<Memory>> temp = what_ws_sent_to_codelets_inputs.get(co);
				if(temp.containsKey(type)){
					List<Memory> temp2 = temp.get(type);
					temp2.addAll(allOfType);
				}else{
					List<Memory> temp2 = new ArrayList<Memory>();
					temp2.addAll(allOfType);
					temp.put(type,temp2);
				}
			}else{
				HashMap<String, List<Memory>> temp = new HashMap<String, List<Memory>>();
				List<Memory> temp2 = new ArrayList<Memory>();
				temp2.addAll(allOfType);
				temp.put(type,temp2);
				what_ws_sent_to_codelets_inputs.put(co, temp);				
			}

		}
		if(io==1){
			allOfType.removeAll(co.getOutputs());//TODO WEIRD!!  It is removing something that doesn't exist!
			co.addOutputs(allOfType); //Adding to codelet
			if(what_ws_sent_to_codelets_outputs.containsKey(co)){
				HashMap<String, List<Memory>> temp = what_ws_sent_to_codelets_outputs.get(co);
				if(temp.containsKey(type)){
					List<Memory> temp2 = temp.get(type);
					temp2.addAll(allOfType);
				}else{
					List<Memory> temp2 = new ArrayList<Memory>();
					temp2.addAll(allOfType);
					temp.put(type,temp2);
				}
			}else{
				HashMap<String, List<Memory>> temp = new HashMap<String, List<Memory>>();
				List<Memory> temp2 = new ArrayList<Memory>();
				temp2.addAll(allOfType);
				temp.put(type,temp2);
				what_ws_sent_to_codelets_outputs.put(co, temp);				
			}
		}

		//		
		//		//These hashmaps keep track of which memory objects Working Storage is responsible for sending to a given codelet.
		//		private HashMap<Codelet,HashMap<MemoryObjectType,List<Memory>>> what_ws_sent_to_codelets_inputs = new HashMap<Codelet, HashMap<MemoryObjectType,List<Memory>>>();
		//		private HashMap<Codelet,HashMap<MemoryObjectType,List<Memory>>> what_ws_sent_to_codelets_outputs = new HashMap<Codelet, HashMap<MemoryObjectType,List<Memory>>>();
		//
		//		//These hashmaps keep a registry of what codelets are registered to receive a given type of memory object. One hashmap keeps track of input lists, and the other of output lists.
		//		private HashMap<MemoryObjectType, List<Codelet>>  codelet_input_registry_by_type = new HashMap<MemoryObjectType,List<Codelet>>();
		//		private HashMap<MemoryObjectType, List<Codelet>>  codelet_output_registry_by_type = new HashMap<MemoryObjectType,List<Codelet>>();
		//
		//		
		//		



	}

	@Override
	public void unregisterCodelet(Codelet co, String type, int io) {
		if(io==0){
			//			ioList = IO.INPUT;
			if(codelet_input_registry_by_type.containsKey(type)){
				codelet_input_registry_by_type.get(type).remove(co);
				HashMap<String, List<Memory>> reg0 = what_ws_sent_to_codelets_inputs.get(co);
				List<Memory> setOfInputMOs = reg0.get(type);

				co.removeFromInput((List<Memory>) setOfInputMOs);
				reg0.remove(type);
			}else{
				//nothing here to be removed
			}

		}else if(io==1){
			//			ioList = IO.OUTPUT;
			if(codelet_output_registry_by_type.containsKey(type)){
				what_ws_sent_to_codelets_outputs.get(type).remove(co);
				HashMap<String, List<Memory>> reg1 = what_ws_sent_to_codelets_outputs.get(co);
				List<Memory> setOfOutputMOs = reg1.get(type);

				co.removeFromInput((List<Memory>) setOfOutputMOs);
				reg1.remove(type);
			}else{
				//nothing here to be removed
			}
		}else{
			throw new IllegalArgumentException();
		}

		//Now i must check if what was sent has nothing inside co, if so, remove co as well
		HashMap<String, List<Memory>> reg0 = what_ws_sent_to_codelets_inputs.get(co);
		HashMap<String, List<Memory>> reg1 = what_ws_sent_to_codelets_outputs.get(co);

		List<Memory> t0 = reg0.get(type);
		List<Memory> t1 = reg1.get(type);

		if(t0.isEmpty()){
			reg0.remove(type);
		}
		if(t1.isEmpty()){
			reg1.remove(type);
		}

		if(reg0.isEmpty()){
			what_ws_sent_to_codelets_inputs.remove(co);
		}
		if(reg1.isEmpty()){
			what_ws_sent_to_codelets_outputs.remove(co);
		}
	}

	@Override
	public void notifyCodelets() {
		// TODO Auto-generated method stub
	}
/**
 * Destroys all memory object from WorkingStorage.
 * Cleans up registered codelets.
 */
	public void shutDown() {
		ArrayList<Memory> list = new ArrayList<Memory>();
		list.addAll(this.getAll());
		for(Memory mo:list){
			this.removeFromWorkingStorageWithDelete(mo);
		}
		this.codelet_input_registry_by_type.clear();
		this.codelet_output_registry_by_type.clear();
		
		this.workingStorageContentList.clear();
		this.workingStorageContents.clear();
		this.what_ws_sent_to_codelets_inputs.clear();
		this.what_ws_sent_to_codelets_outputs.clear();
	}


}
