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
import br.unicamp.cst.core.entities.MemoryObject;
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
	private HashMap<Codelet,HashMap<String,List<MemoryObject>>> what_ws_sent_to_codelets_inputs = new HashMap<Codelet, HashMap<String,List<MemoryObject>>>();
	private HashMap<Codelet,HashMap<String,List<MemoryObject>>> what_ws_sent_to_codelets_outputs = new HashMap<Codelet, HashMap<String,List<MemoryObject>>>();

	private HashMap<String, List<Codelet>>  codelet_input_registry_by_type = new HashMap<String,List<Codelet>>();
	private HashMap<String, List<Codelet>>  codelet_output_registry_by_type = new HashMap<String,List<Codelet>>();


	private HashMap<String,ArrayList<MemoryObject>> workingStorageContents = new HashMap<String, ArrayList<MemoryObject>>();
	private ArrayList<MemoryObject> workingStorageContentList = new ArrayList<MemoryObject>();
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
	public synchronized ArrayList<MemoryObject> getAllOfType(String type){
		ArrayList<MemoryObject> allOfType=new ArrayList<MemoryObject>();
		if(useHashMap){

			ArrayList<MemoryObject> temp=workingStorageContents.get(type);
			if(temp!=null){
				allOfType.addAll(workingStorageContents.get(type));
			}
		}else{

			for(MemoryObject mo:workingStorageContentList){
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

		ArrayList<MemoryObject> allMOs=this.getAll();
		HashMap<String,ArrayList<MemoryObject>> organizedMOs= new HashMap<String,ArrayList<MemoryObject>>();
		for(MemoryObject mo : allMOs){
			String key = mo.getName();
			if(organizedMOs.containsKey(key)){
				organizedMOs.get(key).add(mo);
			}else{
				ArrayList<MemoryObject> newList = new ArrayList<MemoryObject>();
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
	        ArrayList<MemoryObject> listaMOs=(ArrayList<MemoryObject>) pairs.getValue();
	        for(MemoryObject mo: listaMOs){
	        	System.out.println(mo);
	        }
	        
	    }
		
		
		System.out.println("###############################");

	}
	/**
	 * @return All memory objects in Working Storage
	 */
	public synchronized ArrayList<MemoryObject> getAll() {

		ArrayList<MemoryObject> allMOs=new ArrayList<MemoryObject>();
		allMOs.addAll(workingStorageContentList);

		return allMOs;
	}

	public synchronized boolean contains(MemoryObject mo) {

		String type = mo.getName();
		ArrayList<MemoryObject> allOfType = this.getAllOfType(type);

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

	public synchronized void putMemoryObject(MemoryObject mo)
	{
		if((mo!=null)){//TODO should i get rid of older content?
			
//			boolean not_contain_old = !workingStorageContentList.contains(mo); //Compares by content!
			boolean not_contain=true;
			ArrayList<MemoryObject> tempList= new ArrayList<MemoryObject>();
			tempList.addAll(workingStorageContentList);
			
			for(MemoryObject oldMo:tempList){
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
		List<MemoryObject> sentLista=null;


		for(Codelet co : setOfCodelets){
			List<MemoryObject> lista=null;
			if(io==0){//update inputs
				lista=co.getInputs();
				if(what_ws_sent_to_codelets_inputs.containsKey(co)){
					HashMap<String, List<MemoryObject>> listOfTypes = what_ws_sent_to_codelets_inputs.get(co);
					if(listOfTypes.containsKey(type)){
						sentLista = what_ws_sent_to_codelets_inputs.get(co).get(type);
					}
				}else{
					HashMap<String, List<MemoryObject>> listOfTypes = new HashMap<String, List<MemoryObject>>();
					sentLista = new ArrayList<MemoryObject>();
					listOfTypes.put(type, sentLista);
					what_ws_sent_to_codelets_inputs.put(co, listOfTypes);

				}

			}else if(io==1){//update outputs
				lista=co.getOutputs();
				if(what_ws_sent_to_codelets_outputs.containsKey(co)){
					HashMap<String, List<MemoryObject>> listOfTypes = what_ws_sent_to_codelets_outputs.get(co);
					if(listOfTypes.containsKey(type)){
						sentLista = what_ws_sent_to_codelets_outputs.get(co).get(type);
					}
				}else{
					HashMap<String, List<MemoryObject>> listOfTypes = new HashMap<String, List<MemoryObject>>();
					sentLista = new ArrayList<MemoryObject>();
					listOfTypes.put(type, sentLista);
					what_ws_sent_to_codelets_outputs.put(co, listOfTypes);

				}

			}else{
				throw new IllegalArgumentException();
			}



			ArrayList<MemoryObject> whats_missing = new ArrayList<MemoryObject>();
			whats_missing.addAll(this.getAllOfType(type));
			whats_missing.removeAll(lista);

			if(io==0){//update inputs
				co.pushInputs(whats_missing);
			}else if(io==1){//update outputs
				co.pushOutputs(whats_missing);
			}else{
				throw new IllegalArgumentException();
			}



			sentLista.addAll(whats_missing);//recording what was sent to codelets
		}

	}

	/**
	 *  Removes this memory object from Working Storage if it is there, but keeps it in Raw Memory.
	 *  Be careful when using this!
	 * @param bpMo
	 */
	public synchronized void removeFromWorkingStorageWithoutDelete(MemoryObject mo) {
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

			//			private HashMap<Codelet,HashMap<MemoryObjectType,List<MemoryObject>>> what_ws_sent_to_codelets_inputs = new HashMap<Codelet, HashMap<MemoryObjectType,List<MemoryObject>>>();
			//			private HashMap<Codelet,HashMap<MemoryObjectType,List<MemoryObject>>> what_ws_sent_to_codelets_outputs = new HashMap<Codelet, HashMap<MemoryObjectType,List<MemoryObject>>>();



			for(Codelet co :codelets){
				if(what_ws_sent_to_codelets_inputs.containsKey(co)){
					HashMap<String, List<MemoryObject>> listOfTypes = what_ws_sent_to_codelets_inputs.get(co);
					if(listOfTypes.containsKey(type)){
						List<MemoryObject> mos = listOfTypes.get(type);		
						List<MemoryObject> tempMo= new ArrayList<MemoryObject>();
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
					HashMap<String, List<MemoryObject>> listOfTypes = what_ws_sent_to_codelets_outputs.get(codelets);
					if(listOfTypes!=null && listOfTypes.containsKey(type)){
						List<MemoryObject> mos = listOfTypes.get(type);		
						List<MemoryObject> tempMo= new ArrayList<MemoryObject>();
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

	public synchronized void removeFromWorkingStorageWithDelete(MemoryObject bpMo) 
	{
		workingStorageContentList.remove(bpMo);
		if(rawMemory!=null)
			rawMemory.destroyMemoryObject(bpMo);

	}

	/**
	 * Removes all memory objects from Working Memory and delete them from raw memory.
	 * Be careful when using this method!
	 */
	public void clearWithDelete() 
	{
		if(rawMemory!=null && workingStorageContentList!=null)
			for(MemoryObject mo: workingStorageContentList)
			{	
				rawMemory.destroyMemoryObject(mo);
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
		//		ArrayList<MemoryObject> allOfType = new ArrayList<MemoryObject>();
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

		ArrayList<MemoryObject> allOfType = new ArrayList<MemoryObject>();
		allOfType.addAll(this.getAllOfType(type));

		//TODO Get a lock here?

		if(io==0){
			allOfType.removeAll(co.getInputs());
			co.pushInputs(allOfType); //Adding to codelet
			if(what_ws_sent_to_codelets_inputs.containsKey(co)){
				HashMap<String, List<MemoryObject>> temp = what_ws_sent_to_codelets_inputs.get(co);
				if(temp.containsKey(type)){
					List<MemoryObject> temp2 = temp.get(type);
					temp2.addAll(allOfType);
				}else{
					List<MemoryObject> temp2 = new ArrayList<MemoryObject>();
					temp2.addAll(allOfType);
					temp.put(type,temp2);
				}
			}else{
				HashMap<String, List<MemoryObject>> temp = new HashMap<String, List<MemoryObject>>();
				List<MemoryObject> temp2 = new ArrayList<MemoryObject>();
				temp2.addAll(allOfType);
				temp.put(type,temp2);
				what_ws_sent_to_codelets_inputs.put(co, temp);				
			}

		}
		if(io==1){
			allOfType.removeAll(co.getOutputs());//TODO WEIRD!!  It is removing something that doesn't exist!
			co.pushOutputs(allOfType); //Adding to codelet
			if(what_ws_sent_to_codelets_outputs.containsKey(co)){
				HashMap<String, List<MemoryObject>> temp = what_ws_sent_to_codelets_outputs.get(co);
				if(temp.containsKey(type)){
					List<MemoryObject> temp2 = temp.get(type);
					temp2.addAll(allOfType);
				}else{
					List<MemoryObject> temp2 = new ArrayList<MemoryObject>();
					temp2.addAll(allOfType);
					temp.put(type,temp2);
				}
			}else{
				HashMap<String, List<MemoryObject>> temp = new HashMap<String, List<MemoryObject>>();
				List<MemoryObject> temp2 = new ArrayList<MemoryObject>();
				temp2.addAll(allOfType);
				temp.put(type,temp2);
				what_ws_sent_to_codelets_outputs.put(co, temp);				
			}
		}

		//		
		//		//These hashmaps keep track of which memory objects Working Storage is responsible for sending to a given codelet.
		//		private HashMap<Codelet,HashMap<MemoryObjectType,List<MemoryObject>>> what_ws_sent_to_codelets_inputs = new HashMap<Codelet, HashMap<MemoryObjectType,List<MemoryObject>>>();
		//		private HashMap<Codelet,HashMap<MemoryObjectType,List<MemoryObject>>> what_ws_sent_to_codelets_outputs = new HashMap<Codelet, HashMap<MemoryObjectType,List<MemoryObject>>>();
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
				HashMap<String, List<MemoryObject>> reg0 = what_ws_sent_to_codelets_inputs.get(co);
				List<MemoryObject> setOfInputMOs = reg0.get(type);

				co.removeFromInput((List<MemoryObject>) setOfInputMOs);
				reg0.remove(type);
			}else{
				//nothing here to be removed
			}

		}else if(io==1){
			//			ioList = IO.OUTPUT;
			if(codelet_output_registry_by_type.containsKey(type)){
				what_ws_sent_to_codelets_outputs.get(type).remove(co);
				HashMap<String, List<MemoryObject>> reg1 = what_ws_sent_to_codelets_outputs.get(co);
				List<MemoryObject> setOfOutputMOs = reg1.get(type);

				co.removeFromInput((List<MemoryObject>) setOfOutputMOs);
				reg1.remove(type);
			}else{
				//nothing here to be removed
			}
		}else{
			throw new IllegalArgumentException();
		}

		//Now i must check if what was sent has nothing inside co, if so, remove co as well
		HashMap<String, List<MemoryObject>> reg0 = what_ws_sent_to_codelets_inputs.get(co);
		HashMap<String, List<MemoryObject>> reg1 = what_ws_sent_to_codelets_outputs.get(co);

		List<MemoryObject> t0 = reg0.get(type);
		List<MemoryObject> t1 = reg1.get(type);

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
		ArrayList<MemoryObject> list = new ArrayList<MemoryObject>();
		list.addAll(this.getAll());
		for(MemoryObject mo:list){
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
