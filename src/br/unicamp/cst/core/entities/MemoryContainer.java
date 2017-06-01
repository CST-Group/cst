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
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

/**
 * @author andre
 *
 */
public class MemoryContainer implements Memory {	

	private volatile ArrayList<Memory> memories;

	/**
	 * Type of the memory container
	 */
	private String name;

	public MemoryContainer(){

		memories = new ArrayList<>();

	}

	public MemoryContainer(String type) {

		memories = new ArrayList<>();

		this.name = type;
	}

	public synchronized void setType(String name)
	{
		this.name = name;
	}

	/** 
	 * @return the info of the memory which has the greatest evaluation
	 */
	@Override
	public synchronized Object getI() {

		Object I = null;

		double maxEval = 0.0d;

		for(Memory memory : memories){

			double memoryEval = memory.getEvaluation();

			if( memoryEval >= maxEval){

				maxEval = memoryEval;
				I = memory.getI();
			}

		}

		return I;
	}

	public synchronized Object getI(int index) {

		if (index >= 0 && index < memories.size()) {
			return(memories.get(index).getI());
		}
		else {
			System.out.println("Index for the "+getName()+".getI(index) method greater than the number of MemoryObjects within the MemoryContainer");
			return(null);
		}
	}

	/**
	 * 
	 * @param predicate
	 * @return
	 */
	public synchronized Object getI(Predicate<Memory> predicate){

		Object object = null;

		if(memories!=null && memories.size()>0){

			Optional<Memory> optional = memories.stream().filter(predicate).findFirst();

			if(optional.isPresent()) {//Check whether optional has element you are looking for

				Memory memory = optional.get();//get it from optional
				object = memory.getI();
			}

		}

		return object;

	}
	
	/**
	 * 
	 * @param accumulator
	 * @return
	 */
	public synchronized Object getI(BinaryOperator<Memory> accumulator){

		Object object = null;

		if(memories!=null && memories.size()>0){

			Optional<Memory> optional = memories.stream().reduce(accumulator);

			if(optional.isPresent()) {//Check whether optional has element you are looking for

				Memory memory = optional.get();//get it from optional
				object = memory.getI();
			}

		}

		return object;

	}

	/** 
	 * MemoryContainer inserts the info as a new MemoryObject in its Memory list
	 */
	@Override
	public synchronized int setI(Object info) {
		return setI(info,-1.0d);
	}

	public synchronized int setI(Object info, Double evaluation) {

		MemoryObject mo = new MemoryObject(); 
		mo.setI(info);
		if(evaluation!=-1.0)
			mo.setEvaluation(evaluation);
		mo.setType("");

		memories.add(mo);

		return memories.indexOf(mo);

	}

	/**
	 * 
	 * @param info the information to be set in the 
	 * @param index
	 */
	public synchronized void setI(Object info, int index){

		if(memories != null && memories.size() > index){

			Memory memory = memories.get(index);

			if(memory != null){

				if(memory instanceof MemoryObject){
					memory.setI(info);
				} else if(memory instanceof MemoryContainer){
					((MemoryContainer) memory).setI(info, index);
				}					
			}
		}


	}

	/**
	 * 
	 * @param info the information to be set in the 
	 * @param index
	 * @param evaluation
	 */
	public synchronized void setI(Object info, Double evaluation, int index){

		if(memories != null && memories.size() > index){

			Memory memory = memories.get(index);

			if(memory != null){

				if(memory instanceof MemoryObject){
					memory.setI(info);
					memory.setEvaluation(evaluation);
				} else if(memory instanceof MemoryContainer){
					((MemoryContainer) memory).setI(info, evaluation, index);
				}					
			}
		}


	}

	/**
	 * @return the greatest evaluation of the memories in the memory list
	 */
	@Override
	public synchronized Double getEvaluation() {

		Double maxEvaluation = 0.0d;

		for(Memory memory : memories){

			double memoryEval = memory.getEvaluation();

			if( memoryEval >= maxEvaluation)
				maxEvaluation = memoryEval;

		}

		return maxEvaluation;
	}

	/** 
	 * @return the type of the memory which has the greatest evaluation
	 */
	@Override
	public synchronized String getName() {

		return name;
	}

	@Override
	public synchronized void setEvaluation(Double eval) {

		MemoryObject mo = new MemoryObject(); 		
		mo.setEvaluation(eval);
		mo.setType("");

		memories.add(mo);

	}	

	/**
	 * 
	 * @param memory the memory to be added in this container
	 */
	public synchronized int add(Memory memory) {

		int index = -1;

		if(memory != null){

			memories.add(memory);

			index = memories.indexOf(memory);

		} 

		return index;

	}

	/**
	 * 
	 * @param info
	 * @param evaluation
	 * @param type
	 * @return
	 */
	public synchronized int setI(String info, double evaluation, String type) {

		int index = -1;

		if(memories != null){

			boolean set = false;

			for(int i = 0; i < memories.size(); i++){

				Memory memory = memories.get(i);

				if(memory != null && memory instanceof MemoryObject){

					MemoryObject memoryObject = (MemoryObject) memory;

					if(memoryObject.getName().equalsIgnoreCase(type)){

						memory.setI(info);
						memory.setEvaluation(evaluation);
						index = i;
						set = true;
						break;

					} 					
				}

			}

			if(!set){

				MemoryObject mo = new MemoryObject(); 
				mo.setI(info);
				mo.setEvaluation(evaluation);
				mo.setType(type);

				memories.add(mo);

				index = memories.indexOf(mo);

			}
		}

		return index;
	}

	/**
	 * 
	 * @param info
	 * @param evaluation
	 * @param type
	 * @return
	 */
	public synchronized int setI(Object info, double evaluation, String type) {

		int index = -1;

		if(memories != null){

			boolean set = false;

			for(int i = 0; i < memories.size(); i++){

				Memory memory = memories.get(i);

				if(memory != null && memory instanceof MemoryObject){

					MemoryObject memoryObject = (MemoryObject) memory;

					if(memoryObject.getName().equalsIgnoreCase(type)){

						memory.setI(info);
						memory.setEvaluation(evaluation);
						index = i;
						set = true;
						break;

					} 					
				}

			}

			if(!set){

				MemoryObject mo = new MemoryObject(); 
				mo.setI(info);
				mo.setEvaluation(evaluation);
				mo.setType(type);

				memories.add(mo);

				index = memories.indexOf(mo);

			}
		}

		return index;
	}

	public synchronized ArrayList<Memory> getAllMemories() {
		return memories;
	}

	/**
	 * This method is only to test lambda expressions passed as a way of getting an element from memories (code passed as data)
	 * 
	 */
	public static void main(String[] args) {


		MemoryContainer mc = new MemoryContainer();

		mc.setI("Primeiro", 0.5d);
		mc.setI("Segundo", 0.6d);
		mc.setI("Terceiro", 0.7d);    

		/*
		 * Example filtering by predicate
		 */
		System.out.println(mc.getI(x -> x.getEvaluation().equals(0.6d)));
		
		/*
		 * Example passing a lambda expression to get the element with greatest evaluation
		 */
		System.out.println(mc.getI((a,b) -> { 
			   if (a.getEvaluation() > b.getEvaluation()) return a;     
			   if (a.getEvaluation()<b.getEvaluation()) return b;
			   if (a.getEvaluation()==b.getEvaluation()) return a;
			return a;
			}));
		
		/*
		 * Example passing a lambda expression to get the element with smallest evaluation
		 */
		System.out.println(mc.getI((a,b) -> { 
			   if (a.getEvaluation() < b.getEvaluation()) return a;     
			   if (a.getEvaluation()> b.getEvaluation()) return b;
			   if (a.getEvaluation()== b.getEvaluation()) return a;
			return a;
			}));

	}

}
