/***********************************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * K. Raizer, A. L. O. Paraense, E. M. Froes, R. R. Gudwin - initial API and implementation
 ***********************************************************************************************/
package br.unicamp.cst.consciousness;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

/**
 * @author andre
 *
 */
public class LowActivationCodelet extends Codelet{
	
	public LowActivationCodelet(String name) {
		setName(name);		
	}

	@Override
	public void accessMemoryObjects() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void calculateActivation() {
		try{
			setActivation(0.1d);
		} catch (CodeletActivationBoundsException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void proc() {
		// TODO Auto-generated method stub
		
	}

}
