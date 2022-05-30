/***********************************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * K. Raizer, A. L. O. Paraense, E. M. Froes, R. R. Gudwin - initial API and implementation
 * **********************************************************************************************/
package br.unicamp.cst.io.rest;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

/**
 * 
 * @author gudwin
 *
 */
public class TestCodelet extends Codelet{
    
        boolean ascending = true;

	public TestCodelet(String name) {
		setName(name);
	}

	@Override
	public void accessMemoryObjects() {
		
	}

	@Override
	public void calculateActivation() {
            double currentactivation = getActivation();
            if (ascending) {
                try {
                    setActivation(getActivation()+0.1);
                } catch (CodeletActivationBoundsException e) {
                    ascending = !ascending;
                }
            }
            else {
                try {
                    setActivation(getActivation()-0.1);
                } catch (CodeletActivationBoundsException e) {
                    ascending = !ascending;
                }
            }
	}

	@Override
	public void proc() {
		
	}
}        
