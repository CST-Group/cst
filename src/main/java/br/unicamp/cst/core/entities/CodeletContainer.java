package br.unicamp.cst.core.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeletContainer implements Memory {
	
	private HashMap<String, List<Memory>> mapInputs = new HashMap<String, List<Memory>>();
	
	
	private HashMap<String, List<Memory>> mapOutputs = new HashMap<String, List<Memory>>();
	

	private HashMap<String, List<Memory>> mapBroadcast = new HashMap<String, List<Memory>>();
	
	/**
	 * Output memories, the ones that are written.
	 */
	protected List<Memory> outputs = new ArrayList<Memory>();
	
	/**
	 * Input memories, the ones that were broadcasted.
	 */
	private List<Memory> broadcast = new ArrayList<Memory>();
	
	/**
	 * Input memories, the ones that are read.
	 */
	protected List<Memory> inputs = new ArrayList<Memory>();
	
	private ArrayList<Codelet> codelets;
	
	/**
	 * Type of the codelet container
	 */
	private String name;
	
	
	public CodeletContainer() {
		super();
		this.codelets = new ArrayList<>();
	}


	public CodeletContainer(ArrayList<Codelet> codelets, boolean isToStartCodelets) {
		super();
		this.codelets = new ArrayList<Codelet>();
		codelets.forEach((codelet) -> {
			this.addCodelet(codelet, isToStartCodelets);
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
	public synchronized List<Memory> getInputs() {
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
			this.mapInputs.entrySet()) {
			set.setValue(inputs);
		}
		this.inputs = inputs;
	}
	
	/**
	 * Gets the list of broadcast memories.
	 * 
	 * @return the broadcast.
	 */
	public synchronized List<Memory> getBroadcast() {
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
			this.mapBroadcast.entrySet()) {
			set.setValue(broadcast);
		}
		this.broadcast = broadcast;
	}

	public void addCodelet(Codelet codelet, boolean isToStartCodelet) {
			
		List<Memory> addedInputs = new ArrayList<Memory>(codelet.inputs);
		//add the inputs from added codelet to the list of inputs from container which all of its codelets share
		inputs.addAll(addedInputs);
		codelet.setInputs(inputs);
		//keep track of each codelets inputs, so it can removed when needed
		mapInputs.put(codelet.name, addedInputs);
		
		//same logic from inputs
		List<Memory> addedBroadcasts =  new ArrayList<Memory>(codelet.broadcast);
		broadcast.addAll(addedBroadcasts);
		codelet.setBroadcast(broadcast);
		mapBroadcast.put(codelet.name, addedBroadcasts);
		
		//same logic from inputs
		List<Memory> addedOutputs =  new ArrayList<Memory>(codelet.outputs);
		outputs.addAll(addedOutputs);
		codelet.setOutputs(outputs);
		mapOutputs.put(codelet.name, addedOutputs);

		this.codelets.add(codelet);
		if (isToStartCodelet) {
			codelet.start();
		}

	}
	
	public void removeCodelet(Codelet codelet) {
		this.codelets.remove(codelet);
		
		List<Memory> inputsToRemoveFromEachCodelet = mapInputs.get(codelet.name);
		inputs.removeAll(inputsToRemoveFromEachCodelet);
		mapInputs.remove(codelet.name);
		
		List<Memory> broadcastsToRemoveFromEachCodelet = mapBroadcast.get(codelet.name);
		broadcast.removeAll(broadcastsToRemoveFromEachCodelet);
		mapBroadcast.remove(codelet.name);
		
		List<Memory> outputsToRemoveFromEachCodelet = mapOutputs.get(codelet.name);
		outputs.removeAll(outputsToRemoveFromEachCodelet);
		mapOutputs.remove(codelet.name);
		
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
	public synchronized void setEvaluation(Double eval) {
		for (Memory memory : inputs) {
			memory.setEvaluation(eval);
		}

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
	
	public synchronized List<Memory> getOutputs() {
		return outputs;
	}
	
	/**
	 * Sets the list of output memories.
	 * 
	 * @param outputs
	 *            the outputs to set.
	 */
	public synchronized void setOutputs(List<Memory> outputs) {
		for (Map.Entry<String, List<Memory>> set :
			this.mapOutputs.entrySet()) {
			set.setValue(outputs);
		}
		this.outputs = outputs;
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
