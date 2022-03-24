package br.unicamp.cst.core.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeletContainer implements Memory {
	
	
	/**
	 * Input memories, the ones that are read.
	 */
	protected HashMap<String, List<Memory>> inputs = new HashMap<String, List<Memory>>();
	
	/**
	 * Output memories, the ones that are written.
	 */
	protected HashMap<String, List<Memory>> outputs = new HashMap<String, List<Memory>>();
	
	/**
	 * Input memories, the ones that were broadcasted.
	 */
	protected HashMap<String, List<Memory>> broadcast = new HashMap<String, List<Memory>>();

	
	private ArrayList<Codelet> codelets;
	
	/**
	 * Type of the codelet container
	 */
	private String name;
	
	
	public CodeletContainer() {
		super();
		this.codelets = new ArrayList<>();
	}


	public CodeletContainer(ArrayList<Codelet> codelets) {
		super();
		this.codelets = new ArrayList<Codelet>();
		codelets.forEach((codelet) -> {
			this.addCodelet(codelet);
		});
	}
	
	/**
	 * Gets this Codelet activation.
	 * 
	 * @return the activation
	 */
	public synchronized double getActivation() {
		double maxActivation = 0.0d;

		for (Codelet codelet : codelets) {

			double codeletActivation = codelet.getActivation();

			if (codeletActivation >= maxActivation) {

				maxActivation = codeletActivation;
			}

		}
		return maxActivation;
	}
	
	/**
	 * Gets the input memories list.
	 * 
	 * @return the inputs.
	 */
	public synchronized HashMap<String, List<Memory>> getInputs() {
		return inputs;
	}

	/**
	 * Sets the input memories list.
	 * 
	 * @param inputs
	 *            the inputs to set.
	 */
	public synchronized void setInputs(List<Memory> inputs) {
		for (Map.Entry<String, List<Memory>> set :
			this.inputs.entrySet()) {
			set.getValue().addAll(inputs);
		}
		for (Codelet codelet : codelets) {
			codelet.setInputs(inputs);
		}
	}
	
	/**
	 * Gets the list of broadcast memories.
	 * 
	 * @return the broadcast.
	 */
	public synchronized HashMap<String, List<Memory>> getBroadcast() {
		return broadcast;
	}

	/**
	 * Sets the list of broadcast memories.
	 * 
	 * @param broadcast
	 *            the broadcast to set.
	 */
	public synchronized void setBroadcast(List<Memory> broadcast) {
		for (Map.Entry<String, List<Memory>> set :
			this.broadcast.entrySet()) {
			set.getValue().addAll(broadcast);
		}
		for (Codelet codelet : codelets) {
			codelet.setBroadcast(broadcast);
		}
	}

	public void addCodelet(Codelet codelet) {
		List<Memory> addedInputs = new ArrayList<Memory>(codelet.inputs);
		//add the inputs from added codelet to each codelet from container, all must have the same inputs
		codelets.forEach((cod) -> {
			cod.addInputs(addedInputs);
		});
		//add the inputs from the other codelets to the added codelet, all must have the same inputs
		inputs.forEach((key, value) -> {
			codelet.addInputs(value);
		});
		//add the added codelet to the hashmap so it can be deleted when codelet is removed
		inputs.put(codelet.name, addedInputs);
		
		//same logic from inputs
		List<Memory> addedBroadcasts =  new ArrayList<Memory>(codelet.broadcast);
		codelets.forEach((cod) -> {
			cod.addBroadcasts(addedBroadcasts);
		});
		broadcast.forEach((key, value) -> {
			codelet.addBroadcasts(value);
		});
		broadcast.put(codelet.name, addedBroadcasts);
		
		//same logic from inputs
		List<Memory> addedOutputs =  new ArrayList<Memory>(codelet.outputs);
		codelets.forEach((cod) -> {
			cod.addOutputs(addedOutputs);
		});
		outputs.forEach((key, value) -> {
			codelet.addOutputs(value);
		});
		outputs.put(codelet.name, addedOutputs);
		
		this.codelets.add(codelet);

	}
	
	public void removeCodelet(Codelet codelet) {
		this.codelets.remove(codelet);
		
		List<Memory> inputsToRemoveFromEachCodelet = inputs.get(codelet.name);
		codelets.forEach((cod) -> {
			cod.inputs.removeAll(inputsToRemoveFromEachCodelet);
		});
		inputs.remove(codelet.name);
		
		List<Memory> broadcastsToRemoveFromEachCodelet = broadcast.get(codelet.name);
		codelets.forEach((cod) -> {
			cod.broadcast.removeAll(broadcastsToRemoveFromEachCodelet);
		});
		broadcast.remove(codelet.name);
		
		
		List<Memory> outputsToRemoveFromEachCodelet = outputs.get(codelet.name);
		codelets.forEach((cod) -> {
			cod.outputs.removeAll(outputsToRemoveFromEachCodelet);
		});
		outputs.remove(codelet.name);
		
		this.codelets.remove(codelet);
	}

	@Override
	public Object getI() {
		Object I = null;

		double maxActivation = 0.0d;

		for (Codelet codelet : codelets) {

			double codeletActivation = codelet.getActivation();

			if (codeletActivation >= maxActivation) {

				maxActivation = codeletActivation;
				I = codelet;
			}

		}
		return I;
	}

	@Override
	public int setI(Object info) {
		for (Codelet codelet : codelets) {
			for (Memory memoryInput : codelet.inputs) {
				memoryInput.setI(info);
			}
			for (Memory memoryBroadcast : codelet.broadcast) {
				memoryBroadcast.setI(info);
			}
		}
		return -1;
	}

	/**
	 * Gets the greatest evaluation of the codelet with the greatest activation
	 * 
	 * @return the greatest evaluation of the memories in codelet with the greatest activation.
	 */
	@Override
	public synchronized Double getEvaluation() {

		Double maxInputEvaluation = 0.0d;
		Double maxBroadcastEvaluation = 0.0d;
		double maxActivation = 0.0d;
		Codelet codeletMaxActivation = null;

		for (Codelet codelet : codelets) {

			double codeletActivation = codelet.getActivation();

			if (codeletActivation >= maxActivation) {

				maxActivation = codeletActivation;
				codeletMaxActivation = codelet;
			}

		}

		if (codeletMaxActivation != null) {
			for (Memory memory : codeletMaxActivation.inputs) {

				double memoryEval = memory.getEvaluation();

				if (memoryEval >= maxInputEvaluation)
					maxInputEvaluation = memoryEval;

			}
			for (Memory memory : codeletMaxActivation.broadcast) {
				double memoryEval = memory.getEvaluation();

				if (memoryEval >= maxBroadcastEvaluation)
					maxBroadcastEvaluation = memoryEval;
			}
		}
		
		return maxInputEvaluation > maxBroadcastEvaluation ? maxInputEvaluation : maxBroadcastEvaluation;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setType(String type) {
		this.name = type;

	}

	@Override
	public void setName(String name) {
		this.name = name;

	}

	@Override
	public void setEvaluation(Double eval) {
		throw new UnsupportedOperationException(
				"This method is not available for CodeletContainer.");

	}

	@Override
	public Long getTimestamp() {
		Double maxInputEvaluation = 0.0d;
		Double maxBroadcastEvaluation = 0.0d;
		double maxActivation = 0.0d;
		Codelet codeletMaxActivation = null;
		Long timestampInput = null;
		Long timestampBroadcast = null;

		for (Codelet codelet : codelets) {

			double codeletActivation = codelet.getActivation();

			if (codeletActivation >= maxActivation) {

				maxActivation = codeletActivation;
				codeletMaxActivation = codelet;
			}

		}

		if (codeletMaxActivation != null) {
			for (Memory memory : codeletMaxActivation.inputs) {

				double memoryEval = memory.getEvaluation();

				if (memoryEval >= maxInputEvaluation) {
					maxInputEvaluation = memoryEval;
					timestampInput = memory.getTimestamp();
				}
					

			}
			for (Memory memory : codeletMaxActivation.broadcast) {
				double memoryEval = memory.getEvaluation();

				if (memoryEval >= maxBroadcastEvaluation) {
					maxBroadcastEvaluation = memoryEval;
					timestampBroadcast = memory.getTimestamp();
				}
					
			}
		}
		return maxBroadcastEvaluation > maxInputEvaluation ? timestampBroadcast : timestampInput;
	}

	@Override
	public void addMemoryObserver(MemoryObserver memoryObserver) {
		for (Codelet codelet : codelets) {
			for (Memory memory : codelet.inputs) {
				memory.addMemoryObserver(memoryObserver);
			}
			for (Memory memory : codelet.broadcast) {
				memory.addMemoryObserver(memoryObserver);
			}
		}
	}
	
	public synchronized HashMap<String, List<Memory>> getOutputs() {
		return outputs;
	}
	
	public List<Codelet> getAll() {
		return codelets;
	}
	
	public Codelet getCodelet(String name) {
		Codelet selectedCodelet = null;
		for (Codelet codelet : codelets) {
			if (codelet.name.equals(name)) {
				selectedCodelet = codelet;
				break;
			}
		}
		return selectedCodelet;
	}

}
