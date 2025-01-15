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

package br.unicamp.cst.behavior.bn;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import br.unicamp.cst.core.entities.Codelet;

/**
 * This codelet implements a "winners take all" mechanism for the behavior network.
 * It takes a number of behaviors and make the one with the highest activation level ACTIVE. 
 * @author Klaus
 *
 */
public class BehaviorsWTA extends Codelet 
{
	private ArrayList<Behavior> behaviorList = new ArrayList<Behavior>();
	private ConcurrentHashMap<Codelet,Double> codeletsActivation = new ConcurrentHashMap<Codelet, Double>();
	private ConcurrentHashMap<Codelet,Boolean> codeletsActive = new ConcurrentHashMap<Codelet, Boolean>();

	private ArrayList<Behavior> behaviorsStillActive=new ArrayList<Behavior>();
	private Behavior chosenBehavior=null;
	
	private GlobalVariables globalVariables;

	/**
	 * Iterate over all behaviors.
	 * If none of them are ACTIVE, choose the one with highest activation and which is executable
	 * Make that one ACTIVE.
	 * 
	 * Once it becomes active, its activation will drop to zero, but might remain active until it performs its job.
	 * 
	 * I should only try to choose another one once they are all INACTIVE.
	 * 
	 */
	public BehaviorsWTA(GlobalVariables globalVariables)
	{
		this.globalVariables=globalVariables;
	}

	@Override
	public void accessMemoryObjects() {
	}

	@Override
	public void proc() {
		synchronized(this){
			//Just in case:
			codeletsActivation.clear();
			codeletsActive.clear();
			ArrayList<String> tempListOfExecutableCodelets = new ArrayList<String>();

			
			
			
			if(chosenBehavior==null){//If there are no active behavior codelet
				//GET A LIST OF CODELETS THAT ARE CANDIDATES FOR ACTIVATION
				boolean there_is_already_one_active=false;
				for (Behavior competence : behaviorList)
				{
					if (impendingAccess(competence)){
						try
						{
//							//Using the loop to also perform activation decay
//							competence.setValue(competence.getValue()*globalVariables.getPi());
							
							if(competence.isActive()){
								there_is_already_one_active=true;
							}else{
								if(behaviorsStillActive.contains(competence)){
									behaviorsStillActive.remove(competence);
								}
							}

							if(competence.isExecutable()&&competence.getActivation()>=globalVariables.getThetaTemp()){
								
								codeletsActivation.put(competence, competence.getActivation());

								tempListOfExecutableCodelets.add(competence.getName());//Assumes each behavior has a particular name
							}

						} finally
						{
							lock.unlock();
							competence.lock.unlock();
						}
					}
				}

				//FINDS OUT WHICH ONE IS THE CODELET WITH THE HIGHEST ACTIVATION LEVEL
				if(there_is_already_one_active){
					System.out.println("Error at KWTANetwork.java: More than one behavior active at the same time.");
				}


				double highestAct=Double.MIN_VALUE;
				for (Entry<Codelet, Double> o : codeletsActivation.entrySet()) {
					Behavior competence =(Behavior) o.getKey();
					if (impendingAccess(competence)){
						try
						{
							double activation=(Double) o.getValue();
							if(activation>=highestAct){
								chosenBehavior=competence;
								highestAct=activation;
							}
						} finally
						{
							lock.unlock();
							competence.lock.unlock();
						}
					}

				}


				//ACTIVATES EXECUTABLE CODELET WITH HIGHEST ACTIVATION LEVEL

				//				System.out.println("I believe this guy should become active (A="+chosen_codelet.getValue()+"): "+chosen_codelet.getId());
				if(chosenBehavior!=null){//otherwise, it means it could not find a suitable behavior for activation
					if (impendingAccess(chosenBehavior)){
						try
						{
							chosenBehavior.setActive(true);
						} finally
						{
							lock.unlock();
							chosenBehavior.lock.unlock();
						}
					}

					// All thetatemps must be reset back to their original values
					globalVariables.setThetaTemp(globalVariables.getTheta());
				}else{ // no active behavior yet
					globalVariables.decreaseThetaTemps(); //  only in case no behavior is used						
				}

			}else{//If there is already an active behavior codelet
				//Check if its world belief state has changed
				boolean mustSetNull=false;
				if (impendingAccess(chosenBehavior)){
					try
					{
						if(chosenBehavior.changedWorldBeliefState()){
							chosenBehavior.setActive(false);		
							mustSetNull=true;
						}

					} finally
					{
						lock.unlock();
						chosenBehavior.lock.unlock();
					}
				}

				if(mustSetNull){
					chosenBehavior=null;
				}

			}
			
		}//end synchronized
	}//end proc


	public void addBehavior(Behavior be){
		this.behaviorList.add(be);
	}

	public void removeBehavior(Behavior be){
		this.behaviorList.remove(be);
	}

	@Override
	public void calculateActivation() {
		// TODO Auto-generated method stub
		
	}
        /**
         * @return the chosenBehavior from bn iteraction
         */
        public Behavior getChosenBehavior() {
            return this.chosenBehavior;
        }

}



////// CODE SANDBOX
//synchronized(this){
//	//Just in case:
//	codeletsActivation.clear();
//	codeletsActive.clear();
//	int count=0;
//	ArrayList<String> tempListOfExecutableCodelets = new ArrayList<String>();
//
//	
//	//GET A LIST OF CODELETS THAT ARE CANDIDATES FOR ACTIVATION
//	boolean there_is_already_one_active=false;
//	for (Behavior competence : behaviorList)
//	{
//		if (impendingAccess(competence)){
//			try
//			{
//				if(competence.isActive()){
//					there_is_already_one_active=true;
//					count++;
//				}else{
//					if(behaviorsStillActive.contains(competence)){
//						behaviorsStillActive.remove(competence);
//					}
//				}
//				
//				
//				//Core functionality
//				if(competence.isExecutable()&&competence.getValue()>=globalVariables.getThetaTemp()){
//					codeletsActivation.put(competence, competence.getValue());
//
//					tempListOfExecutableCodelets.add(competence.getId());//Assumes each behavior has a particular name
//				}
//				
////TODO Where should I put this?						
////				private void checkIfShouldBecomeInactive()
////				{ 
////					if(this.isExecutable()){
////						if(this.isActive()){//reason if it should remain active
////							//In principle, if it was chosen as the best behavioral answer to a given state, it shouldn't stop working until there was a change in state (unless we consider a stochastic effect).
////							if(this.changedWorldBeliefState()){
////							
////								this.setActive(false);
////							}//else, remain active, (but keep its activation level down?)
////							else{
////								if(setToZeroWhenActivated){
////									this.setValue(0);//TODO is this in accordance with [Maes 1989] ?
////								}
////							}
////						}
////
////					}else{//If not executable, it can't be active
////						this.setActive(false);
////					}
////					
////
////				}
//				
////				changedWorldBeliefState()
//				
//				
//			} finally
//			{
//				lock.unlock();
//				competence.lock.unlock();
//			}
//		}
//	}
//	
//	
//	
//	
//	//FINDS OUT WHICH ONE IS THE CODELET WITH THE HIGHEST ACTIVATION LEVEL
//	if(count>1){
//		System.out.println("Error at KWTANetwork.java: More than one behavior active at the same time.");
//	}
//	Behavior chosen_codelet=null;
//	//			if(!there_is_already_one_active){//choose the executable one with highest activation
//	if(count==0){
//		double highestAct=Double.MIN_VALUE;
//		for (Entry<Codelet, Double> o : codeletsActivation.entrySet()) {
//			Behavior competence =(Behavior) o.getKey();
//			if (impendingAccess(competence)){
//				try
//				{
//					double activation=(Double) o.getValue();
//					if(activation>=highestAct){
//						chosen_codelet=competence;
//						highestAct=activation;
//					}
//				} finally
//				{
//					lock.unlock();
//					competence.lock.unlock();
//				}
//			}
//
//		}
//		
//		
//		//ACTIVATES EXECUTABLE CODELET WITH HIGHEST ACTIVATION LEVEL
//		if(chosen_codelet!=null){
//			//				System.out.println("I believe this guy should become active (A="+chosen_codelet.getValue()+"): "+chosen_codelet.getId());
//			if (impendingAccess(chosen_codelet)){
//				try
//				{
//					double previousActivation = chosen_codelet.getValue();
//					double previousThetaTemp = globalVariables.getThetaTemp();
//					boolean wasExecutable = chosen_codelet.isExecutable();
//					boolean wasActive = chosen_codelet.isActive();
//					
//					
//					
//					chosen_codelet.setActive(true);
//					chosen_codelet.setValue(0);// [Maes 1989]
//					
//					double nextActivation = chosen_codelet.getValue();
//					boolean isActive = chosen_codelet.isActive();
//					System.out.println("");
//
//				} finally
//				{
//					lock.unlock();
//					chosen_codelet.lock.unlock();
//				}
//			}
//			behaviorsStillActive.add(chosen_codelet);
//
//			// All thetatemps must be reset back to their original values
//			globalVariables.setThetaTemp(globalVariables.getTheta());
//
//		}
//	}
//
//	decreaseThetas(globalVariables.getThetaTempDecreaseRate()*Math.tanh((this.getTimeStep()/100)));
//
////	System.out.println("behaviorsStillActive: ");
////	for(Behavior be: behaviorsStillActive){
////		System.out.println(be.getId());
////	}
////	System.out.println("---");
//	
//	
//	
//	
////	System.out.println("WorldBeliefState: ");
////	ArrayList<MemoryObject> wbs = new ArrayList<MemoryObject>();
////	wbs.addAll(ws.getAllOfType(MemoryObjectTypesCore.WORLD_STATE));
////	
////	for(MemoryObject mo: wbs){
////		System.out.println(mo.getInfo());
////	}
////	System.out.println("---");
//	
//	
//	
////	//TODO Testing decay
////	
////	for (Behavior competence : behaviorList)
////	{
////		if (impendingAccess(competence)){
////			try
////			{
////				double decay=0.01;
////				double act=competence.getValue();
////				
////				if(act>decay){
////					act=act-decay;
////					competence.setValue(act);
////				}
////			} finally
////			{
////				lock.unlock();
////				competence.lock.unlock();
////			}
////		}
////	}
////	
////	
//}
