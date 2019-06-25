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

package br.unicamp.cst.consciousness;

import java.util.ArrayList;
import java.util.List;

import br.unicamp.cst.core.entities.CodeRack;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

/**
 * A codelet-based implementation of the Global Workspace Theory, originally
 * formulated in [1988 Baars] Bernard J. Baars. A Cognitive Theory of
 * Consciousness. Cambridge University Press, 1988.
 * 
 * @author A. L. O. Paraense
 * @see Codelet
 *
 */
public class SpotlightBroadcastController extends Codelet {
	
	private Codelet consciousCodelet;

	/** access to all codelets, so the broadcast can be made */
	private CodeRack codeRack;

	private double thresholdActivation = 0.9d;

	/**
	 * Creates a SpotlightBroadcastController.
	 * 
	 * @param codeRack the codeRack containing all Codelets.
	 */
	public SpotlightBroadcastController(CodeRack codeRack) {
		this.setName("SpotlightBroadcastController");
		this.codeRack = codeRack;
		consciousCodelet = null;
		this.timeStep = 300l;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.unicamp.cogsys.core.entities.Codelet#accessMemoryObjects()
	 */
	@Override
	public void accessMemoryObjects() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.unicamp.cogsys.core.entities.Codelet#calculateActivation()
	 */
	@Override
	public void calculateActivation() {
		try {
			setActivation(0.0d);
		} catch (CodeletActivationBoundsException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.unicamp.cogsys.core.entities.Codelet#proc()
	 */
	@Override
	public void proc() {

		if (consciousCodelet != null) {
			if (consciousCodelet.getActivation() < thresholdActivation) {
				consciousCodelet = null;
			}
		}

		if (codeRack != null) {
			// first, select the coalition with greater activation to gain
			// consciousness
			List<Codelet> allCodeletsList = codeRack.getAllCodelets();

			if (allCodeletsList != null) {
				for (Codelet codelet : allCodeletsList) {
					if (consciousCodelet == null) {
						if (codelet.getActivation() > thresholdActivation) {
							consciousCodelet = codelet;
						}
					} else {
						if (codelet.getActivation() > consciousCodelet.getActivation()) {
							consciousCodelet = codelet;
						}
					}
				}

				// then, broadcast its information to all codelets

				if (consciousCodelet != null) {
					List<Memory> memoriesToBeBroadcasted = consciousCodelet.getOutputs();
					if (memoriesToBeBroadcasted != null) {
						for (Codelet codelet : allCodeletsList) {
							if (!codelet.getName().equalsIgnoreCase(consciousCodelet.getName()))
								codelet.setBroadcast(memoriesToBeBroadcasted);
							else
								codelet.setBroadcast(new ArrayList<Memory>());
						}
					} else {
						for (Codelet codelet : allCodeletsList) {
							codelet.setBroadcast(new ArrayList<Memory>());
						}
					}
				} else {
					for (Codelet codelet : allCodeletsList) {
						codelet.setBroadcast(new ArrayList<Memory>());
					}
				}
			}
		}
	}
}
