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

import java.util.Random;

import org.opt4j.core.genotype.IntegerBounds;
import org.opt4j.core.genotype.IntegerGenotype;
import org.opt4j.core.start.Constant;

import com.google.inject.Inject;

public class GlasOptProblem {

//	DoubleGenotype genotype;
	IntegerGenotype genotype;
	private int nNodes;
//	private DoubleBounds dbs;
	private IntegerBounds dbs;
	private int nStimuli;
	private int nActions;
	private int solutionSize;

	@Inject
	public GlasOptProblem(@Constant(value = "nNodes") int nNodes, @Constant(value = "nStimuli") int nStimuli, @Constant(value = "nActions") int nActions) {

		this.nNodes=nNodes;

//		
		this.nStimuli=nStimuli;
		this.nActions=nActions;
		this.solutionSize=3*(nNodes-1);

//		int[] lower={1,1,1,1,1,1,   0,0,0,0,0,0,   0,0,0,0,0,0};
//		int[] upper={1,2,3,4,5,6,   6,6,6,6,6,6,   2,2,2,2,2,2};
		
		int[] lower= new int[solutionSize];
		int[] upper= new int[solutionSize];

		// DEFINING MAX AND MIN RANGE FOR OPTIMIZATION
		int index=0;
		for(int i = 1; i<nNodes;i++){
			upper[index]=i;
			lower[index]=1;//TODO should be 1
			index++;
		}

		for(int i = 1; i<nNodes;i++){
			upper[index]=nStimuli-1;
			lower[index]=0;
			index++;
		}

		for(int i = 1; i<nNodes;i++){
			upper[index]=nActions-1;
			lower[index]=0;
			index++;
		}	
		
		dbs = new IntegerBounds(lower, upper);


	}

	public IntegerGenotype getGlasGenotype() {

		boolean problem=false;
//		genotype = new DoubleGenotype(dbs);
		genotype = new IntegerGenotype(dbs);

		for(int index = 0; index< genotype.size();index++){
			if(Double.isNaN(genotype.get(index))){
				problem =true;
			}

		}

		genotype.init(new Random(), (nNodes-1)*3);

		for(int index = 0; index< genotype.size();index++){
			if(Double.isNaN(genotype.get(index))){
				problem =true;
			}
		}

		return genotype;
	}

}
