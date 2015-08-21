package br.unicamp.cst.centralExecutive.glas;
import java.util.Random;

import org.opt4j.core.genotype.DoubleBounds;
import org.opt4j.core.genotype.DoubleGenotype;
import org.opt4j.core.genotype.IntegerGenotype;
import org.opt4j.core.problem.Creator;
import org.opt4j.core.start.Constant;

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
