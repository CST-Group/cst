/**
 * 
 */
package br.unicamp.cst.core.entities;

import java.util.ArrayList;

/**
 * @author andre
 *
 */
public class MemoryContainer implements Memory {

	private volatile ArrayList<Memory> memories;

	public MemoryContainer(){

		memories = new ArrayList<>();

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

			if( memoryEval > maxEval){

				maxEval = memoryEval;
				I = memory.getI();
			}

		}

		return I;
	}

	/** 
	 * MemoryContainer inserts the info as a new MemoryObject in its Memory list
	 */
	@Override
	public synchronized void setI(Object info) {

		MemoryObject mo = new MemoryObject(); 
		mo.setI(info);
		mo.setEvaluation(0.0d);
		mo.setType("");

		memories.add(mo);

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
	 * @return the greatest evaluation of the memories in the memory list
	 */
	@Override
	public synchronized Double getEvaluation() {

		Double maxEvaluation = 0.0d;

		for(Memory memory : memories){

			double memoryEval = memory.getEvaluation();

			if( memoryEval > maxEvaluation)
				maxEvaluation = memoryEval;

		}

		return maxEvaluation;
	}

	/** 
	 * @return the type of the memory which has the greatest evaluation
	 */
	@Override
	public synchronized String getName() {

		String name = null;

		double maxEval = 0.0d;

		for(Memory memory : memories){

			double memoryEval = memory.getEvaluation();

			if( memoryEval > maxEval){

				maxEval = memoryEval;
				name = memory.getName();
			}

		}

		return name;
	}

	@Override
	public void setEvaluation(Double eval) {

		MemoryObject mo = new MemoryObject(); 		
		mo.setEvaluation(eval);
		mo.setType("");

		memories.add(mo);

	}	

	/**
	 * 
	 * @param memory the memory to be added in this container
	 */
	public void add(Memory memory) {

		if(memory != null){

			memories.add(memory);

		} 

	}

	/**
	 * TODO - This main is only for fast testing purposes. Should be deleted later.
	 * 
	 * @param args
	 */
//	public static void main(String[] args) {
//
//		MemoryContainer memoryContainer = new MemoryContainer();
//
//		MemoryObject memory1 = new MemoryObject();
//		memory1.setI("MemoryObject 1");
//		memory1.setEvaluation(0.5D);
//		memory1.setType("String");
//
//		memoryContainer.add(memory1);
//
//		MemoryContainer memory2 = new MemoryContainer();
//
//		MemoryObject memoryI1 = new MemoryObject();
//		memoryI1.setI("MemoryObject Inside 1");
//		memoryI1.setEvaluation(0.6D);
//		memoryI1.setType("String");
//
//		memory2.add(memoryI1);
//
//		MemoryObject memoryI2 = new MemoryObject();
//		memoryI2.setI("MemoryObject Inside 2");
//		memoryI2.setEvaluation(0.5D);
//		memoryI2.setType("String");
//
//		memory2.add(memoryI2);
//
//		memoryContainer.add(memory2);
//
//		MemoryObject memory3 = new MemoryObject();
//		memory3.setI("MemoryObject 2");
//		memory3.setEvaluation(0.4D);
//		memory3.setType("String");
//
//		memoryContainer.add(memory3);
//
//		System.out.println(memoryContainer.getI());
//		System.out.println(memoryContainer.getName());
//		System.out.println(memoryContainer.getEvaluation());
//
//	}

}
