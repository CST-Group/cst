package br.unicamp.cst.behavior.glas;
import org.opt4j.core.problem.ProblemModule;
import org.opt4j.core.start.Constant;

import com.google.inject.Provides;

/**
 * http://kaskavalci.com/?p=231
 */

/**
 * @author Klaus
 * 
 */
public class GlasOptModule extends ProblemModule {
	
	@Constant(value = "nNodes") int nNodes = 7;
	@Constant(value = "nStimuli") int nStimuli = 7;
	@Constant(value = "nActions") int nActions = 7;
	
	@Provides GlasSequence getSequence() {
		return sequence;
	}

	protected void config() {
		
		bindProblem(GlasOptCreator.class, GlasOptDecoder.class, GlasOptEvaluator.class);
	}

	public void setSequence(GlasSequence sequence) {
		this.sequence = sequence;
	}


	protected GlasSequence sequence;
	
	public int getnNodes() {
		return nNodes;
	}


	public void setnNodes(int nNodes) {
		this.nNodes = nNodes;
	}

	/**
	 * @return the nStimuli
	 */
	public int getnStimuli() {
		return nStimuli;
	}

	/**
	 * @param nStimuli the nStimuli to set
	 */
	public void setnStimuli(int nStimuli) {
		this.nStimuli = nStimuli;
	}

	/**
	 * @return the nActions
	 */
	public int getnActions() {
		return nActions;
	}

	/**
	 * @param nActions the nActions to set
	 */
	public void setnActions(int nActions) {
		this.nActions = nActions;
	}



	
	
	
}