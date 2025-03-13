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
import java.util.List;

import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

/**
 * 
 * Pool of all alive codelets in the system.
 * 
 * Inspired in Hofstadter and Mitchell
 * "The copycat project: A model of mental fluidity and analogy-making".  The whole arena in the Baars-Franklin
 * metaphor.
 * 
 * @author A. L. O. Paraense
 * @author K. Raizer
 * 
 */
public class CodeRack {
	/**
	 * List of all alive codelets in the system
	 */
	private List<Codelet> allCodelets;

	/**
	 * Creates a CodeRack.
	 */
	public CodeRack() {
		allCodelets = new ArrayList<Codelet>();
	}

	/**
	 * Gets the list of all Codelets.
	 * 
	 * @return the allCodelets.
	 */
	public synchronized List<Codelet> getAllCodelets() {
		return allCodelets;
	}

	/**
	 * Sets the list of all Codelets.
	 * 
	 * @param allCodelets
	 *            the allCodelets to set
	 */
	public void setAllCodelets(List<Codelet> allCodelets) {
		this.allCodelets = allCodelets;
	}

	/**
	 * Adds a new Codelet to the Coderack
	 * 
	 * @param co
	 *            codelet to be added.
	 */
	public void addCodelet(Codelet co) {
		allCodelets.add(co);
	}

	/**
	 * Insert a codelet to the CodeRack.
	 * 
	 * Same as addCodelet.
	 * 
	 * @param co
	 *            codelet to be inserted.
	 * @return the own codelet inserted, if it is needed to concatenate to
	 *         further methods calls.
	 */
	public Codelet insertCodelet(Codelet co) {

		addCodelet(co);

		return co;
	}

	/**
	 * Creates a codelet and adds it to this coderack.
	 * 
	 * @param activation
	 *            codelet's activation.
	 * @param broadcast
	 *            list of memory objects which were broadcast lately (treated as
	 *            input memories).
	 * @param inputs
	 *            list of input memories.
	 * @param outputs
	 *            list o output memories.
	 * @param co
	 *            codelet to be created.
	 * @return the codelet created.
	 */
	public Codelet createCodelet(double activation, List<Memory> broadcast, List<Memory> inputs, List<Memory> outputs,
			Codelet co) {
		try {
			co.setActivation(activation);
		} catch (CodeletActivationBoundsException e) {
			e.printStackTrace();
		}
		co.setBroadcast(broadcast);
		co.setInputs(inputs);
		co.setOutputs(outputs);
		addCodelet(co);
		return co;
	}

	/**
	 * Removes a codelet from coderack.
	 * 
	 * @param co
	 *            the codelet to be destroyed.
	 */
	public void destroyCodelet(Codelet co) {
		co.stop();
		this.allCodelets.remove(co);

	}

	/**
	 * Destroys all codelets. Stops the codelet threads.
	 */
	public void shutDown() {
		for (Codelet co : this.getAllCodelets()) {
			co.stop();
		}

		this.allCodelets.clear();
	}

	/**
	 * Starts all codelets in coderack.
	 */
	public void start() {
		for (Codelet co : this.getAllCodelets()) {
			synchronized (co) {
				co.start();
			}
		}
	}

	/**
	 * Stops all codelets within CodeRack.
	 */
	public void stop() {
		for (Codelet co : this.getAllCodelets()) {
			co.stop();
		}
	}
}
