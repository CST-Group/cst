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


import org.opt4j.core.Individual;
import org.opt4j.core.genotype.IntegerGenotype;
import org.opt4j.core.optimizer.Archive;
import org.opt4j.core.start.Opt4JTask;
import org.opt4j.optimizers.sa.SimulatedAnnealingModule;
import org.opt4j.viewer.ViewerModule;

import br.unicamp.cst.behavior.glas.GlasSequence;




/**
 *  Tries to find correct vector of integers
 * @author Klaus
 *
 */
public class GlasLearner {

	boolean show_gui=false;

	int nNodes = 7; //int nNodes = 7;
	int nActions = 3;
	int nStimuli = 7;
	int solutionSize=3*(nNodes-1);
	GlasSequence mySequence = null;	

	double[] maxPositions =null;
	double[] minPositions = null;

	// PARAMETERS	
	String filename = "experiment_glas.txt";
	int nExperiment_events=1;

	int max_number_reRuns=500;  //int max_number_reRuns=500;
	int nParticles = 100;				//int nParticles = 1000;
	int neighborhoodSize=5;				//int neighborhoodSize=5;	
	double neighborhoodIncrement=2;		//double neighborhoodIncrement=2;
	double inertia=0.75;				//double inertia=0.75;
	double maxMinVelocity=20;			//maxMinVelocity=20;			
	int numberOfIterations = 100; 		//int numberOfIterations = 100; 

	int nReRuns = 30;

	double best_found_fit=Double.NEGATIVE_INFINITY;
	int[] best_found_solution = new int[solutionSize];
	double[] history_fit= new double[max_number_reRuns];


	//	static int[] known_final_solution={1, 1, 2, 3, 4, 5,    1, 2, 3, 4, 5, 6,   1, 1, 1, 1, 2, 2};



	public GlasLearner(int nNodes, int nStimuli, int nActions) {
		// TODO Auto-generated constructor stub
		this.nNodes=nNodes;
		this.nStimuli=nStimuli;
		this.nActions=nActions;
		this.solutionSize=3*(nNodes-1);
		best_found_solution = new int[solutionSize];

	}


	public void learnSequence(GlasSequence mySequence) {
		this.mySequence=mySequence;


		long initial_time = System.nanoTime();

		best_found_fit=Double.NEGATIVE_INFINITY;

		for(int reRun=0;reRun<nReRuns;reRun++){ 

			//			DifferentialEvolutionModule opt_algorithm = new DifferentialEvolutionModule(); // DifferentialEvolution is restricted to class org.opt4j.core.genotype.DoubleGenotype
			//			opt_algorithm.setGenerations(500);
			//			opt_algorithm.setAlpha(100);

			//			EvolutionaryAlgorithmModule opt_algorithm = new EvolutionaryAlgorithmModule();
			//			opt_algorithm.setGenerations(10000); //number of generations.
			//			opt_algorithm.setAlpha(50); //population size alpha.
			//			//						opt_algorithm.setMu(25); //25 number of parents mu.
			//			opt_algorithm.setCrossoverRate(0.5);  //0.95 crossover rate.
			//			//						opt_algorithm.setLambda(25);  //25  number of children lambda.



			//				MOPSOModule opt_algorithm = new MOPSOModule(); //MOPSO is restricted to class org.opt4j.core.genotype.DoubleGenotype
			//				opt_algorithm.setIterations(500); // Number of iterations
			//				opt_algorithm.setParticles(500);  // Number of particles
			////				opt_algorithm.setPerturbation(90);

			//SA is the best one so far
			SimulatedAnnealingModule opt_algorithm = new SimulatedAnnealingModule();
			opt_algorithm.setIterations(1000000); //opt_algorithm.setIterations(1000000);



			//		RandomSearchModule opt_algorithm = new RandomSearchModule();		
			//		opt_algorithm.setIterations(2000);
			//		opt_algorithm.setBatchsize(100);


			GlasOptModule gom = new GlasOptModule();		
			gom.setnNodes(nNodes);
			gom.setnActions(nActions);
			gom.setnStimuli(nStimuli);			
			gom.setSequence(mySequence);//TODO could I get nActions and nStimuli from sequence instead?


			Opt4JTask task = new Opt4JTask(false);

			if(show_gui){

				ViewerModule viewer = new ViewerModule();
				viewer.setCloseOnStop(false);
				task.init(opt_algorithm,gom,viewer);
			}

			task.init(opt_algorithm,gom);

			int[] found_solution = new int[this.solutionSize];
			double found_solution_first_eval=0;
			try {
//				System.out.println("Antes: task.execute();");
				task.execute();
//				System.out.println("Depois: task.execute();");

				Archive archive = task.getInstance(Archive.class);
				//			System.out.println("Archive size: "+archive.size());

				for (Individual individual : archive) {
					IntegerGenotype gen = (IntegerGenotype) individual.getGenotype();
					double[] found_solution_eval = individual.getObjectives().array();


					found_solution_first_eval=found_solution_eval[0];

					//				System.out.println("Found solution: ");
					//				System.out.print("[ ");
//					System.out.print(reRun+" ");
					for(int g = 0; g<gen.size();g++){
						found_solution[g]=(int) Math.round(gen.get(g));		                	
//						System.out.print(found_solution[g]+" ");
					}
					//				System.out.println("]");


//					for(int fits=0; fits<found_solution_eval.length;fits++){
//						System.out.print(found_solution_eval[fits]+" ");
//					}
					//					System.out.print(" "+found_solution_eval[1]);
					break;
				}
				//			System.out.print("Solution evaluation: ");
				//				System.out.println(found_solution_first_eval);									
//				System.out.println("");

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				task.close();
			} 
			//		System.out.println("-----------------");

			//Recording best found solution
			if((-found_solution_first_eval)>this.best_found_fit){ //the more negative the better
				best_found_fit=(-found_solution_first_eval);
				for(int s=0; s<found_solution.length;s++){
					best_found_solution[s]=found_solution[s];
				}
			}




		}//end rerun loop



		long end_time = System.nanoTime();
		//		//		System.out.println("initial_time: "+initial_time);
		//		//		System.out.println("end_time: "+end_time);
		double elapsed_time = (end_time-initial_time);
		//		elapsed_time=elapsed_time/1000000000;
		//		elapsed_time=elapsed_time/60;		
		//		//		System.out.println("Elapsed time: "+elapsed_time+" minutes");


	}


	/**
	 * @return the max_number_reRuns
	 */
	public int getMax_number_reRuns() {
		return max_number_reRuns;
	}


	/**
	 * @param max_number_reRuns the max_number_reRuns to set
	 */
	public void setMax_number_reRuns(int max_number_reRuns) {
		this.max_number_reRuns = max_number_reRuns;
	}


	/**
	 * @return the nParticles
	 */
	public int getnParticles() {
		return nParticles;
	}


	/**
	 * @param nParticles the nParticles to set
	 */
	public void setnParticles(int nParticles) {
		this.nParticles = nParticles;
	}


	/**
	 * @return the best_found_fit
	 */
	public double getBest_found_fit() {
		return best_found_fit;
	}


	/**
	 * @param best_found_fit the best_found_fit to set
	 */
	public void setBest_found_fit(double best_found_fit) {
		this.best_found_fit = best_found_fit;
	}


	/**
	 * @return the best_found_solution
	 */
	public int[] getBest_found_solution() {
		return best_found_solution;
	}


	/**
	 * @param best_found_solution the best_found_solution to set
	 */
	public void setBest_found_solution(int[] best_found_solution) {
		this.best_found_solution = best_found_solution;
	}


	/**
	 * @return the history_fit
	 */
	public double[] getHistory_fit() {
		return history_fit;
	}


	/**
	 * @param history_fit the history_fit to set
	 */
	public void setHistory_fit(double[] history_fit) {
		this.history_fit = history_fit;
	}


	public boolean isShow_gui() {
		return show_gui;
	}


	public void setShow_gui(boolean show_gui) {
		this.show_gui = show_gui;
	}


	/**
	 * @return the nReRuns
	 */
	public int getnReRuns() {
		return nReRuns;
	}


	/**
	 * @param nReRuns the nReRuns to set
	 */
	public void setnReRuns(int nReRuns) {
		this.nReRuns = nReRuns;
	}



}
