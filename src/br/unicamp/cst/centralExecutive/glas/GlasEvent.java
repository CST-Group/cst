/**
 * 
 */
package br.unicamp.cst.centralExecutive.glas;

/**
 * @author Klaus Raizer
 *
 */
public class GlasEvent {

	private int stimulus;
	private int action;
	private double reward;

	public GlasEvent(int stimulus, int action, double reward) {
		this.stimulus=stimulus;
		this.action=action;
		this.reward=reward;
	}

	/**
	 * @return the stimulus
	 */
	public int getStimulus() {
		return stimulus;
	}

	/**
	 * @param stimulus the stimulus to set
	 */
	public void setStimulus(int stimulus) {
		this.stimulus = stimulus;
	}

	/**
	 * @return the action
	 */
	public int getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(int action) {
		this.action = action;
	}

	/**
	 * @return the reward
	 */
	public double getReward() {
		return reward;
	}

	/**
	 * @param reward the reward to set
	 */
	public void setReward(double reward) {
		this.reward = reward;
	}

}
