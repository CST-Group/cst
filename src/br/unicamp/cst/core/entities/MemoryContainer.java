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
	 * 
	 * @param phase
	 * @param activation
	 * @param type
	 */
	public void setI(String info, double evaluation, String type) {

		if(memories != null){
			
			boolean set = false;

			for(Memory memory : memories){

				if(memory != null && memory instanceof MemoryObject){

					MemoryObject memoryObject = (MemoryObject) memory;

					if(memoryObject.getName().equalsIgnoreCase(type)){

						memory.setI(info);
						memory.setEvaluation(evaluation);
						
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
				
			}
		}	
	}
	
        public ArrayList<Memory> getAllMemories() {
            return memories;
        }
	
}
