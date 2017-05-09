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

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.unicamp.cst.behavior.glas.GlasEvent;
import br.unicamp.cst.behavior.glas.GlasSequence;
import br.unicamp.cst.behavior.glas.GlasSequenceElements;
import br.unicamp.cst.behavior.glas.Individual;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.RawMemory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.cst.memory.WorkingStorage;

/**
 * @author klaus
 *
 */
public class LearnerCodelet extends Codelet 
{
	ArrayList<Individual> indi_list = new ArrayList<Individual>();

	private static int maxEventsSequenceLenght = Integer.MAX_VALUE; // Defines a maximum number of events that can be stored in EVENTS_SEQUENCE_MO (used mostly for experiments and debug purposes)
	boolean first_run=true;
	double goal_fitness=Double.POSITIVE_INFINITY;

	private Memory EVENTS_SEQUENCE_MO;
	private Memory SOLUTION_TREE_MO;  //Holds a phenotype of the best solution until now
	private boolean plot_solution=false;

	//GlasPlot ploter =null;


	//TODO The algorithm should be able to find out the following variables on its own	
	static int nNodes = 7; 
	static int nReRuns=1;

	static int nActions = 1;
	static int nStimuli = 1;

	JSONArray best_solution_tree = new JSONArray();
	private int last_number_of_events=0;

	private boolean enabled=true;
	private boolean printSequenceUsedForLearning=false;

	private boolean printLearnedSolutionTree;

	private int maxNumberOfSolutions=Integer.MAX_VALUE; //When learner reaches maxNumberOfSolutions, it stops learning. Used primarily for debug and experimental purposes. 

	private int maxNumberOfNodes=10;

	private int minNumberOfNodes=2;

	private RawMemory rawMemory;

	public LearnerCodelet(int nStimuli, int nActions, RawMemory rawMemory, WorkingStorage ws)
	{
		this.nStimuli = nStimuli;
		this.nActions = nActions;
		
		this.rawMemory = rawMemory;
		
		//this.setTimeStep(0); // No waiting between reruns?

		if(rawMemory!=null)
		this.SOLUTION_TREE_MO=rawMemory.createMemoryObject("SOLUTION_TREE", "");
		this.addOutput(this.SOLUTION_TREE_MO);
		if(ws!=null)
			ws.putMemoryObject(this.SOLUTION_TREE_MO);


		GlasOptProblem gop = new GlasOptProblem(nNodes, nStimuli, nActions);
		//TODO Which is better here, getting a random initial solution or a fixed standard one? 
		// Such as: [0,1,1,1,1,1,1,0,1,2,3,4,5,6,0,1,1,1,1,1,1]

		//		IntegerGenotype random_tree_gen = gop.getGlasGenotype();
		//		int[] random_tree_gen_fixed = {1,1,1,1,1,1,1,2,3,4,5,6,1,1,1,1,1,1};
		int[] initial_solution_tree_int = {0,1, 0,1, 0,1};

		JSONArray initial_solution_tree_json = new JSONArray();

		for(int i = 0; i<initial_solution_tree_int.length;i++){
			initial_solution_tree_json.put(initial_solution_tree_int[i]);
		}

		this.SOLUTION_TREE_MO.setI(initial_solution_tree_json.toString());


		//		[0,1,1,2,3,4,5,0,1,2,3,4,5,6,0,1,1,1,1,2,2]
		if(ws!=null)
			ws.registerCodelet(this,"EVENTS_SEQUENCE", 0);

	}


	/* (non-Javadoc)
	 * @see br.unicamp.cogsys.core.entities.Codelet#accessMemoryObjects()
	 */
	@Override
	public void accessMemoryObjects() {

		EVENTS_SEQUENCE_MO = this.getInput("EVENTS_SEQUENCE", 0);

	}

	/* (non-Javadoc)
	 * @see br.unicamp.cogsys.core.entities.Codelet#calculateActivation()
	 */
	@Override
	public void calculateActivation() {
		try {
			this.setActivation(0.0);
		} catch (CodeletActivationBoundsException e) {
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see br.unicamp.cogsys.core.entities.Codelet#proc()
	 */
	@Override
	public void proc() {

		if(enabled){



			if( (first_run || (SOLUTION_TREE_MO.getEvaluation()<this.getGoal_fitness())) && !((String)EVENTS_SEQUENCE_MO.getI()).isEmpty()){

				//			System.out.println("Init proc ... ");

				try {
					JSONArray sequence_json = new JSONArray(EVENTS_SEQUENCE_MO.getI());

					System.out.print(".");
					int sequence_lenght = sequence_json.length();
					//If (maxEventsSequenceLenght==Integer.MAX_VALUE), it tries to learn a new tree as soon as possible (if it has new events and previous learning is over)
					//TODO Increment this condition for it to start learning only if it makes a mistake?
					//If maxEventsSequenceLenght is a finite integer (set by the user) it waits until maxEventsSequenceLenght new events are presented to the current solution. Only then does it start learning a new sequence.
					if(maxEventsSequenceLenght==Integer.MAX_VALUE || (sequence_lenght-last_number_of_events)>=maxEventsSequenceLenght){

						while(sequence_json.length()>maxEventsSequenceLenght){ // learns only with the last MAX_EVENTS_SEQUENCE_LENGHT events
							sequence_json.remove(0);
						}
						if(this.printSequenceUsedForLearning){System.out.println("");}
						GlasSequence mySequence = new GlasSequence();
						if(this.printSequenceUsedForLearning){System.out.println("Sequence used for learning: ");}
						for(int e=0;e<sequence_json.length();e++){
							//TODO Should be inside GlasSequence?				
							JSONObject event_json = sequence_json.getJSONObject(e);
							int stim = event_json.getInt(GlasSequenceElements.SENSED_STIMULUS.toString());
							int act = event_json.getInt(GlasSequenceElements.EXPECTED_ACTION.toString());
							double rew = event_json.getDouble(GlasSequenceElements.REWARD_RECEIVED.toString());
							
//							Sequence used for learning: 
//								0,2,0,-1.0 //TODO
							
							if(this.printSequenceUsedForLearning){System.out.println(e+","+stim+","+act+","+rew);}

							mySequence.addEvent(new GlasEvent(stim,act,rew));	
						}

						//TODO Store WHO acted on this sequence, and its results



						JSONArray solution_tree_phenotype_jsonarray = new JSONArray(SOLUTION_TREE_MO.getI());
						int[] solution_tree_phenotype_int = new int[solution_tree_phenotype_jsonarray.length()];
						for(int i=0; i<solution_tree_phenotype_jsonarray.length();i++){
							solution_tree_phenotype_int[i]=solution_tree_phenotype_jsonarray.getInt(i);
						}

						int[] genotype_int = this.getGenotypeFromPhenotype(solution_tree_phenotype_int);
						

						int nNodesIndi = (solution_tree_phenotype_int.length/3); 
						Individual indi =new Individual(nNodesIndi, nStimuli, nActions);												
						indi.setChromossome(genotype_int);

						double max_fit = this.getMaxFitnessForSequence(mySequence);
						double fit = indi.getFitness(mySequence);
						indi.setNormalizedFitness(fit/max_fit);

						indi_list.add(indi);
						if(this.printLearnedSolutionTree){
//							System.out.println("");
							System.out.print(fit+",");
							System.out.print(fit/max_fit+",");
							System.out.print(nNodesIndi+",");
							for(int i=0; i<genotype_int.length;i++){
								System.out.print(genotype_int[i]+",");
							}
							System.out.print(indi_list.size());
							System.out.println("");
						}



						//LEARNING PHASE
						System.out.println("I just started learning from a new sequence...");
						int[] temp_best_found_int = {1,1,1};
						double temp_best_found_fit = Double.NEGATIVE_INFINITY;
						double normalized_fitness = Double.NEGATIVE_INFINITY;
						
						GlasLearner myLearner = new GlasLearner(nNodes, nStimuli, nActions);
						for(int local_nNodes =minNumberOfNodes; local_nNodes<=maxNumberOfNodes; local_nNodes++){

							myLearner = new GlasLearner(local_nNodes, nStimuli, nActions);
							boolean show_gui = false;
							myLearner.setShow_gui(show_gui);
							myLearner.setnReRuns(nReRuns);
							//		int max_number_reRuns=500;  //int max_number_reRuns=500;
							//		int nParticles = 1000;				//int nParticles = 1000;
							//		myLearner.setMax_number_reRuns(max_number_reRuns);
							//		myLearner.setnParticles(nParticles);

							//							
							myLearner.learnSequence(mySequence); 
							//							
							if(myLearner.getBest_found_fit()>temp_best_found_fit){
								temp_best_found_int = myLearner.getBest_found_solution();
								temp_best_found_fit = myLearner.getBest_found_fit();
							}

							if(this.printLearnedSolutionTree){
								double temp_max_fit = this.getMaxFitnessForSequence(mySequence);
//								System.out.println("");
								System.out.print(temp_best_found_fit+",");
								normalized_fitness=temp_best_found_fit/temp_max_fit;
								System.out.print(normalized_fitness+",");
								System.out.print(local_nNodes+",");
								for(int i=0; i<temp_best_found_int.length-1;i++){
									System.out.print(temp_best_found_int[i]+",");
								}
								System.out.println(temp_best_found_int[temp_best_found_int.length-1]);
								
								
							}

						}
						System.out.println("...finished learning.");
						int[] best_found_int = temp_best_found_int; //TODO Unnecessary?

						int[] new_solution_tree_int = this.getPhenotypeFromGenotype(best_found_int);
						
						
						double best_found_fit = temp_best_found_fit; //TODO Unnecessary?

						best_solution_tree = new JSONArray();
						for(int i=0;i<new_solution_tree_int.length;i++){
							best_solution_tree.put(new_solution_tree_int[i]);
						}

												
						
						
						SOLUTION_TREE_MO.setI(best_solution_tree.toString()); 
//						SOLUTION_TREE_MO.setEvaluation(best_found_fit);
						SOLUTION_TREE_MO.setEvaluation(normalized_fitness);						
						first_run=false;


						//						}
						if(SOLUTION_TREE_MO.getEvaluation()>=this.getGoal_fitness()){
							System.out.println("Found goal fitness = "+SOLUTION_TREE_MO.getEvaluation());
						}

						if(plot_solution){

							double[] best_found_double=new double[best_found_int.length];

							for(int i=0;i<best_found_double.length;i++){
								best_found_double[i]=((double)best_found_int[i]);				
							}

							double[] sol = new double[nNodes*3];
							int count=0;
							for(int i=0;i<sol.length;i++){
								if((i%nNodes)==0){
									sol[i]=0;

								}else{
									sol[i]=best_found_double[count];
									count++;
								}

							}

//							ploter = new GlasPlot(sol);

//							ploter.plot();

						}



						sequence_json = new JSONArray(EVENTS_SEQUENCE_MO.getI());
						last_number_of_events=sequence_json.length();

						//						System.out.println("##########################################");


					} //if(sequence_json.length()>=MAX_EVENTS_SEQUENCE_LENGHT)


				} catch (JSONException e) {
					System.out.println("This should not happen! (at LearnerCodelet)");
					e.printStackTrace();
				}

			}

			if(indi_list.size()>=this.maxNumberOfSolutions){
				System.out.println("Stopped learning.");
				this.setEnabled(false);
			}
		}else{//if enabled
			//			System.out.println("Learning is halted."); //Do nothing
		}

	}//proc




	private int[] getPhenotypeFromGenotype(int[] genotype) {


		
		
		int number_of_nodes = (genotype.length/3)+1;
		int[] solution_tree_phenotype = new int[number_of_nodes*3];
		

			int count=0;
			solution_tree_phenotype[count]=0;
			
			count++;
			
			for(int i = 0; i < genotype.length/3; i++){			
				solution_tree_phenotype[count]=genotype[i];
				count++;				
			}
			
			solution_tree_phenotype[count]=0;
			count++;
			
			for(int i = genotype.length/3; i < 2*genotype.length/3; i++){				
				solution_tree_phenotype[count]=genotype[i];			
				count++;				
			}
			
			solution_tree_phenotype[count]=0;
			count++;
			
			for(int i = 2*genotype.length/3; i < 3*genotype.length/3; i++){				
				solution_tree_phenotype[count]=genotype[i];			
				count++;				
			}
			

		return solution_tree_phenotype;
	}


	private double getMaxFitnessForSequence(GlasSequence mySequence) {
		double max_fit = 0;
		ArrayList<GlasEvent> events = mySequence.getEvents();
		for(GlasEvent event : events){
			max_fit=max_fit+Math.abs(event.getReward());
		}

		return max_fit;
	}


	public double getGoal_fitness() {
		return goal_fitness;
	}


	public void setGoalFitness(double goal_fitness) {
		this.goal_fitness = goal_fitness;
	}


	/**
	 * @return the maxEventsSequenceLenght
	 */
	public static int getMaxEventsSequenceLenght() {
		return maxEventsSequenceLenght;
	}



	/**
	 * @param Max_Events_Sequence_Lenght the Max_Events_Sequence_Lenght to set
	 */
	public void setMaxEventsSequenceLenght(int Max_Events_Sequence_Lenght) {
		maxEventsSequenceLenght = Max_Events_Sequence_Lenght;
	}


	public int[] getGenotypeFromPhenotype(int[] phenotype) {


		int number_of_nodes=Math.round(phenotype.length/3);
		int[] genotype = new int[(number_of_nodes-1)*3];

		int count=0;
		for(int i = 1; i<number_of_nodes;i++){
			genotype[count]=phenotype[i];
			count++;
		}
		for(int i = number_of_nodes+1; i<2*number_of_nodes;i++){
			genotype[count]=phenotype[i];
			count++;
		}
		for(int i = 2*number_of_nodes+1; i<3*number_of_nodes;i++){
			genotype[count]=phenotype[i];
			count++;
		}
		
		
		return genotype;
	}


	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}


	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the nNodes
	 */
	public static int getnNodes() {
		return nNodes;
	}


	/**
	 * @param nNodes the nNodes to set
	 */
	public static void setnNodes(int nNodes) {
		LearnerCodelet.nNodes = nNodes;
	}


	/**
	 * @return the nActions
	 */
	public static int getnActions() {
		return nActions;
	}


	/**
	 * @param nActions the nActions to set
	 */
	public static void setnActions(int nActions) {
		LearnerCodelet.nActions = nActions;
	}


	/**
	 * @return the nStimuli
	 */
	public static int getnStimuli() {
		return nStimuli;
	}


	/**
	 * @param nStimuli the nStimuli to set
	 */
	public static void setnStimuli(int nStimuli) {
		LearnerCodelet.nStimuli = nStimuli;
	}


	/**
	 * @return the nReRuns
	 */
	public static int getnReRuns() {
		return nReRuns;
	}


	/**
	 * @param nReRuns the nReRuns to set
	 */
	public static void setnReRuns(int nReRuns) {
		LearnerCodelet.nReRuns = nReRuns;
	}


	/**
	 * @return the printLearnedSolutionTree
	 */
	public boolean isPrintLearnedSolutionTree() {
		return printLearnedSolutionTree;
	}


	/**
	 * @param printLearnedSolutionTree the printLearnedSolutionTree to set
	 */
	public void setPrintLearnedSolutionTree(boolean printLearnedSolutionTree) {
		this.printLearnedSolutionTree = printLearnedSolutionTree;
	}


	/**
	 * @return the printSequenceUsedForLearning
	 */
	public boolean isPrintSequenceUsedForLearning() {
		return printSequenceUsedForLearning;
	}


	/**
	 * @param printSequenceUsedForLearning the printSequenceUsedForLearning to set
	 */
	public void setPrintSequenceUsedForLearning(boolean printSequenceUsedForLearning) {
		this.printSequenceUsedForLearning = printSequenceUsedForLearning;
	}


	/**
	 * @return the maxNumberOfSolutions
	 */
	public int getMaxNumberOfSolutions() {
		return maxNumberOfSolutions;
	}


	/**
	 * @param maxNumberOfSolutions the maxNumberOfSolutions to set
	 */
	public void setMaxNumberOfSolutions(int maxNumberOfSolutions) {
		this.maxNumberOfSolutions = maxNumberOfSolutions;
	}


	/**
	 * @return the maxNumberOfNodes
	 */
	public int getMaxNumberOfNodes() {
		return maxNumberOfNodes;
	}


	/**
	 * Higher bound for number of nodes the solution tree can have.
	 * Must be: minNumberOfNodes less or equal maxNumberOfNodes
	 * Default: maxNumberOfNodes equals 10
	 * 
	 * Warning: Large values of maxNumberOfNodes might make learning too slow
	 * @param maxNumberOfNodes the maxNumberOfNodes to set
	 */
	public void setMaxNumberOfNodes(int maxNumberOfNodes) {
		if(maxNumberOfNodes>=minNumberOfNodes){
			this.maxNumberOfNodes = maxNumberOfNodes;
		}
	}


	/**
	 * Lower bound for number of nodes the solution tree can have.
	 * Must be: minNumberOfNodes less or equals 1 and minNumberOfNodes less or equals maxNumberOfNodes
	 * Default: minNumberOfNodes equals 1
	 * @return the minNumberOfNodes
	 */
	public int getMinNumberOfNodes() {
		return minNumberOfNodes;
	}


//	/** //TODO Verify the utility for this method..
//	 * Lower bound for number of nodes the solution tree can assume.
//	 * Must be: minNumberOfNodes>=2 and minNumberOfNodes<= maxNumberOfNodes
//	 * Default: minNumberOfNodes=2
//	 * 
//	 * @param minNumberOfNodes the minNumberOfNodes to set
//	 */
//	public void setMinNumberOfNodes(int minNumberOfNodes) {
//		if(minNumberOfNodes>=2 && minNumberOfNodes<= maxNumberOfNodes){
//			this.minNumberOfNodes = minNumberOfNodes;
//
//
//
//			int[] random_tree_gen_fixed = new int[(minNumberOfNodes-1)*3];
//			int count=1;
//			for(int i=0;i<(minNumberOfNodes-1)*3;i++){
//				if(i<(minNumberOfNodes-1) || i>=2*(minNumberOfNodes-1)){
//					random_tree_gen_fixed[i]=1;
//				}else{
//					random_tree_gen_fixed[i]=count;
//					count++;
//				}
//			}
//
//
//			JSONArray initial_solution_tree = new JSONArray();
//
//			for(int i = 0; i<random_tree_gen_fixed.length;i++){
//				initial_solution_tree.put(random_tree_gen_fixed[i]);
//			}
//
//			this.SOLUTION_TREE_MO.updateInfo(initial_solution_tree.toString());
//
//
//		}
//	}

}
