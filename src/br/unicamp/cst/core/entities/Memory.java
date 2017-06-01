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

/**
 * @author andre
 *
 */
public interface Memory {
	
	/**
	 * 
	 * @return the info in memory
	 */
	Object getI();
	
	/**
	 * 
	 * @param info the updated info to set in memory
	 * @return index of the memory inside the container or -1 if not a container
	 */
	int setI(Object info);
	
	
	/**
	 * 
	 * @return the evaluation of this memory
	 */
	Double getEvaluation();

	
	/**
	 * 
	 * @return the type of the memory
	 */
	String getName();

	/**
	 * 
	 * @param eval
	 */
	void setEvaluation(Double eval);

}
