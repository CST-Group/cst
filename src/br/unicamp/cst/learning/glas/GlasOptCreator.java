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

package br.unicamp.cst.learning.glas;
import org.opt4j.core.genotype.IntegerGenotype;
import org.opt4j.core.problem.Creator;

import com.google.inject.Inject;

/**
 * 
 */

/**
 * @author Klaus
 *
 */


public class GlasOptCreator implements Creator<IntegerGenotype> {
	
//	private int nNodes=7;
	
//	Random random = new Random();
	
	
 GlasOptProblem problem;

     @Inject
     public GlasOptCreator(GlasOptProblem problem) {
             this.problem = problem;
     }

	public IntegerGenotype create() {
		
		IntegerGenotype genotype = problem.getGlasGenotype();
		
		return genotype;
	}

}
