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
import org.opt4j.core.problem.Decoder;

/**
 * 
 */

/**
 * Turns genotype into phenotype
 * 
 * @author Klaus
 *
 */
public class GlasOptDecoder implements Decoder<IntegerGenotype, int[]> {

	@Override
	public int[] decode(IntegerGenotype genotype) {

		
		int nNodes=Math.round(genotype.size()/3)+1;
		int[] phenotype = new int[nNodes*3];

		phenotype[0]=0;//First nodes always zero
		phenotype[nNodes]=0;//First nodes always zero
		phenotype[2*nNodes]=0;//First nodes always zero 
		
		
		
		int index = 0;
		for(int i=1;i<nNodes;i++){		
//			tempChromossome[i] = (int) Math.round(phenotype[index]); // is rounding here ok?
			phenotype[i] = genotype.get(index);
			index++;
		}
		index = nNodes-1;
		for(int i=nNodes+1;i<2*nNodes;i++){		
//			tempChromossome[i] = (int) Math.round(phenotype[index]); // is rounding here ok?
			phenotype[i] = genotype.get(index);
			index++;
		}
		index = 2*nNodes-2;
		for(int i=2*nNodes+1;i<3*nNodes;i++){		
//			tempChromossome[i] = (int) Math.round(phenotype[index]); // is rounding here ok?
			phenotype[i] = genotype.get(index);
			index++;
		}
		
			
		
		return phenotype;
	}
}