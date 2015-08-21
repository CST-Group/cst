/**
 * 
 */
package br.unicamp.cst.centralExecutive.glas;

import br.unicamp.cst.core.entities.CodeRack;

/**
 * Cognit is the implementation of GLAS - Gated Learning Action Selection - algorithm inside LibreCog cognitive architecture.
 * It is composed by three integrated codelets:
 * - ActionSelectionCodelet: Selects the most appropriate action based on sequence of events and a known solution tree
 * - SequenceBuilderCodelet: Builds a sequence of events in memory based on stimuli, selected action and received reward
 * - LearnerCodelet: Learns an optimized solution tree given a sequence of events 
 * 
 * @author klaus
 *
 */
public class Cognit {
	private ActionSelectionCodelet action_selection_codelet;
	private SequenceBuilderCodelet sequence_builder_codelet;
	private LearnerCodelet learner_codelet;


	public Cognit(int nStimuli, int nActions){
		//GLAS Codelets
				action_selection_codelet = new ActionSelectionCodelet();
				CodeRack.getInstance().insertCodelet(action_selection_codelet);				
				sequence_builder_codelet = new SequenceBuilderCodelet();
				CodeRack.getInstance().insertCodelet(sequence_builder_codelet);
				learner_codelet = new LearnerCodelet(nStimuli, nActions);
				CodeRack.getInstance().insertCodelet(learner_codelet);
	}
	

	/**
	 * @return the action_selection_codelet
	 */
	public ActionSelectionCodelet getActionSelectionCodelet() {
		return action_selection_codelet;
	}


	/**
	 * @return the sequence_builder_codelet
	 */
	public SequenceBuilderCodelet getSequenceBuilderCodelet() {
		return sequence_builder_codelet;
	}


	/**
	 * @return the learner_codelet
	 */
	public LearnerCodelet getLearnerCodelet() {
		return learner_codelet;
	}
	
}
