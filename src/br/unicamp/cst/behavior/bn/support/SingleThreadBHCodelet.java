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

package br.unicamp.cst.behavior.bn.support;

import br.unicamp.cst.behavior.bn.Behavior;
import java.util.ArrayList;

import br.unicamp.cst.core.entities.Codelet;

/**
 * 
 * Codelet that activates behaviors in a single-threaded behavior network
 * @author Klaus
 *
 */
public class SingleThreadBHCodelet extends Codelet{
	private ArrayList<Behavior> behaviors;
	public SingleThreadBHCodelet(ArrayList<Behavior> behaviors){
		this.behaviors=behaviors;
	}

	@Override
	public void proc() {
		
		for(Behavior be:behaviors){
			be.proc();
		}
		
	}

	@Override
	public void accessMemoryObjects() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void calculateActivation() {
		// TODO Auto-generated method stub
		
	}

}
