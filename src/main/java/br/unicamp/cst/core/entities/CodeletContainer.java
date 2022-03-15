package br.unicamp.cst.core.entities;

import java.util.ArrayList;
import java.util.List;

public class CodeletContainer implements Memory {
	
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
		this.codelets = codelets;
	}
	
	public void addCodelet(Codelet codelet) {
		this.codelets.add(codelet);
	}
	
	public void removeCodelet(Codelet codelet) {
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
	
	public synchronized List<Memory> getOutputs() {
		List<Memory> codeletsOutputs = new ArrayList<Memory>();
		for (Codelet codelet : codelets) {
			codeletsOutputs.addAll(codelet.getOutputs());
		}
		return codeletsOutputs;
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
