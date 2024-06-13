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

package br.unicamp.cst.core.entities;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a Memory Container. The Memory Container is responsible
 * for implementing an important element in the Dynamic Subsumption mechanism
 * used in CST. All the Memory Objects in a Container are of the same type, and
 * hold the same parameters. The only differences among them are that they were
 * generated by a different codelet, and they might have different evaluations.
 * An evaluation is an inner parameter from any Memory Object, which holds a
 * value (usually a real value between 0 and 1) that measures a relative
 * importance given by the codelet, and which is used by the Evaluation codelet
 * within the Container to decide which from all input Memory Objects will be
 * sent to the output.
 * 
 * @author A. L. O. Paraense
 * @see Memory
 * @see MemoryObject
 */
public class MemoryContainer implements Memory {

	private volatile ArrayList<Memory> memories;
        
        private Long id;

	/**
	 * Type of the memory container
	 */
	private String name;
        
        public enum Policy {MAX, MIN, RANDOM_FLAT, RANDOM_FLAT_STABLE, RANDOM_PROPORTIONAL, RANDOM_PROPORTIONAL_STABLE, ITERATE};
        
        /**
	 * Policy used for selecting a MemoryObject at the MemoryContainer
	 */
	private Policy policy;
        
        private volatile Memory last;
        private volatile int lasti=0;
        private transient Random rand = new Random();
        private transient int randchoice = -1;

	/**
	 * Creates a MemoryContainer.
	 */
	public MemoryContainer() {

		memories = new ArrayList<>();
                policy = Policy.MAX;

	}

	/**
	 * Creates a MemoryContainer.
	 * 
	 * @param type the type of the memories inside the container.
	 */
	public MemoryContainer(String type) {

		memories = new ArrayList<>();
                policy = Policy.MAX;
		this.name = type;
	}
        
        /**
	 * Gets the id of the Memory Container.
	 * 
	 * @return the id of the Memory Container.
	 */
        @Override
	public synchronized Long getId() {
		return this.id;
	}

	/**
	 * Sets the id of the Memory Container.
	 * 
	 * @param id
	 *            the id of the Memory Object to set.
	 */
        @Override
	public synchronized void setId(Long id) {
		this.id = id;
	}

	/**
	 * Sets the type of the memories inside the container.
	 * 
	 * @param name the type of the memories inside the container.
	 */
        @Deprecated
	public synchronized void setType(String name) {
		this.name = name;
	}
        
        /**
	 * Sets the name of the container.
	 * 
	 * @param name the type of the memories inside the container.
	 */
	public synchronized void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the info of the memory which has the greatest evaluation.
	 * 
	 * @return the info of the memory which has the greatest evaluation.
	 */
	private synchronized Object getIMax() {

		double maxEval = Double.NEGATIVE_INFINITY;
                Memory mlast = null;
                ArrayList<Memory> allmax = new ArrayList<>();
		for (Memory memory : memories) {
                        double memoryEval = memory.getEvaluation();
                        if (memoryEval > maxEval) {
                                maxEval = memoryEval;
                                mlast = memory;
                                allmax = new ArrayList<>();
                                allmax.add(memory);
			}
                        else if (memoryEval == maxEval) {
                            allmax.add(memory);
                        }
		}
                if (allmax.size() > 1) {
                    if (randchoice < 0) randchoice = rand.nextInt(allmax.size());
                    last = allmax.get(randchoice);
                    return(last.getI());
                }
                else {
                  if (mlast == null) return null;  
                  last = mlast;
		  return last.getI();
                }  
	}
        
        /**
	 * Gets the info of the memory which has the smallest evaluation.
	 * 
	 * @return the info of the memory which has the smallest evaluation.
	 */
	private synchronized Object getIMin() {

		double minEval = Double.MAX_VALUE;
                Memory mlast=null;
                ArrayList<Memory> allmin = new ArrayList<>();
		for (Memory memory : memories) {
			double memoryEval = memory.getEvaluation();
			if (memoryEval < minEval) {
				minEval = memoryEval;
                                mlast = memory;
                                allmin = new ArrayList<>();
                                allmin.add(memory);
			}
                        else if (memoryEval == minEval) {
                            allmin.add(memory);
                        }
		}
                if (allmin.size() > 1) {
                    if (randchoice < 0) randchoice = rand.nextInt(allmin.size());
                    last = allmin.get(randchoice);
                    return(last.getI());
                }
                else {
                  if (mlast == null) return null;    
                  last = mlast;
		  return last.getI();
                }
	}
        
        /**
	 * Gets the info of a random memory from within the MemoryContainer.
	 * 
	 * @return the info of a random memory within the MemoryContainer
	 */
	private synchronized Object getIRandomFlat() {

                int i = rand.nextInt(memories.size());
                last = memories.get(i);
                return(last.getI());
	}
        
        /**
	 * Gets the info of a random memory from within the MemoryContainer.
         * In this case, the choice will only change after a change in the container
	 * 
	 * @return the info of a random memory within the MemoryContainer (stable version)
	 */
	private synchronized Object getIRandomFlatStable() {

                if (randchoice < 0) randchoice = rand.nextInt(memories.size());
                last = memories.get(randchoice);
                return(last.getI());
	}
        
        /**
	 * Gets the info of a random memory from within the MemoryContainer using eval as a weight.
	 * 
	 * @return the info of a random memory within the MemoryContainer using eval as a weight
	 */
	private synchronized Object getIRandomProportional() {

                if (memories.size() == 0) return null;
                double indexfrom[] = new double[memories.size()];
                double indexto[] = new double[memories.size()];
                int i = 0;
                for (Memory memory : memories) {
                    if (i == 0) 
                       indexfrom[i] = 0;
                    else
                       indexfrom[i] = indexto[i-1];
                    double interval = memory.getEvaluation();
                    indexto[i] = indexfrom[i] + interval;
                    i++;
                }
                double llast = indexto[i-1];
                double wheel = rand.nextDouble();
                if (llast*wheel == 0) return(getIRandomFlat());
                for (int j=0;j<=memories.size();j++)
                    if (indexfrom[j] < wheel*llast && wheel*llast < indexto[j]) {
                        last = memories.get(j);
                        return(last.getI());
                    }
                last = memories.get(0);
                return(last.getI());
	}
        
        /**
	 * Gets the info of a random memory from within the MemoryContainer using eval as a weight.
	 * 
	 * @return the info of a random memory within the MemoryContainer using eval as a weight
	 */
	private synchronized Object getIRandomProportionalStable() {

                if (memories.size() == 0) return null;
                double indexfrom[] = new double[memories.size()];
                double indexto[] = new double[memories.size()];
                int i = 0;
                for (Memory memory : memories) {
                    if (i == 0) 
                       indexfrom[i] = 0;
                    else
                       indexfrom[i] = indexto[i-1];
                    double interval = memory.getEvaluation();
                    indexto[i] = indexfrom[i] + interval;
                    i++;
                }
                double llast = indexto[i-1];
                double wheel = rand.nextDouble();
                if (llast*wheel == 0) return(getIRandomFlatStable());
                for (int j=0;j<=memories.size();j++)
                    if (indexfrom[j] < wheel*llast && wheel*llast < indexto[j]) {
                        if (randchoice < 0) randchoice = j;
                        last = memories.get(randchoice);
                        return(last.getI());
                    }
                last = memories.get(0);
                return(last.getI());
	}
        
        /**
	 * Gets the info of a memory from within the MemoryContainer in an iterative way.
	 * 
	 * @return the info of a memory within the MemoryContainer in an iterative way
	 */
	private synchronized Object getIIterate() {
            if (memories.size() > 0 && lasti < memories.size()) {
                last = memories.get(lasti);
                lasti++;
                if (lasti > memories.size()-1) lasti = 0;
                return (last.getI());
            }
            Logger.getAnonymousLogger().log(Level.INFO,"The MemoryContainer {0} still does not have any internal Memories ...",getName()); 
            return null;
        }
        
        /**
	 * Gets the info of the memory according to the specified MemoryContainer policy
         * Available policies:
         *      Policy.MAX
         *      Policy.MIN
         *      Policy.RANDOM_FLAT
         *      Policy.RANDOM_FLAT_STABLE
         *      Policy.RANDOM_PROPORTIONAL
         *      Policy.RANDOM_PROPORTIONAL_STABLE
         *      Policy.ITERATE
	 * 
	 * @return the info of the memory according to the specified MemoryContainer policy
	 */
	@Override
	public synchronized Object getI() {
		switch(policy) {
                    case MAX: return getIMax();
                    case MIN: return getIMin();
                    case RANDOM_FLAT: return getIRandomFlat();
                    case RANDOM_FLAT_STABLE: return getIRandomFlatStable();
                    case RANDOM_PROPORTIONAL: return getIRandomProportional();
                    case RANDOM_PROPORTIONAL_STABLE: return getIRandomProportionalStable();
                    case ITERATE: return getIIterate();
                    default: return getIMax();
                }
	}
        
        /**
         * Gets the last info returned while a getI() was issued
         * @return the info object last returned by a getI()
         */
        public synchronized Object getLastI() {
            return last.getI();
        }
        
        /**
         * Gets the last Memory from inside the container, while a getI() was issued
         * @return the last Memory accessed by a getI()
         */
        public synchronized Memory getLast() {
            return last;
        }

	/**
	 * Gets the info of the memory which has the index passed.
	 * 
	 * @param index the index of the memory whose info is searched.
	 * @return the info of the memory which has the index passe or null is not
	 *         found.
	 */
	public synchronized Object getI(int index) {

		if (index >= 0 && index < memories.size()) {
			return (memories.get(index).getI());
		} else {
                    Logger.getAnonymousLogger().log(Level.INFO, "Index for the {0}.getI(index) method greater than the number of MemoryObjects within the MemoryContainer -> {1}", new Object[]{getName(), index});
	            return (null);
		}
	}

	/**
	 * Gets the info of the memory which has the name passed.
	 * 
	 * @param name the name of the memory whose info is searched.
	 * @return the info of the memory which has the name passed or null if it is not
	 *         found.
	 */
	public synchronized Object getI(String name) {
		for (Memory m : memories) {
			if (m.getName().equals(name))
				return (m.getI());
		}
                Logger.getAnonymousLogger().log(Level.INFO, "There is no Memory with the name {0} within the Container {1}", new Object[]{name, this.name});
		return (null);
	}

	/**
	 * Gets the info of the memory filtered by the predicate.
	 * 
	 * @param predicate the predicate to be used to filter the stream.
	 * @return the info of the memory or null if not found.
	 */
	public synchronized Object getI(Predicate<Memory> predicate) {

		Object object = null;

		if (memories != null && memories.size() > 0) {

			Optional<Memory> optional = memories.stream().filter(predicate).findFirst();

			if (optional.isPresent()) {// Check whether optional has element you
										// are looking for

				Memory memory = optional.get();// get it from optional
				object = memory.getI();
			}

		}

		return object;

	}

	/**
	 * Gets the info of the memory reduced by the binary operator passed.
	 * 
	 * @param accumulator the binary operator.
	 * @return the info of the memory or null if not found.
	 */
	public synchronized Object getI(BinaryOperator<Memory> accumulator) {

		Object object = null;

		if (memories != null && memories.size() > 0) {

			Optional<Memory> optional = memories.stream().reduce(accumulator);

			if (optional.isPresent()) {// Check whether optional has element you
										// are looking for

				Memory memory = optional.get();// get it from optional
				object = memory.getI();
			}

		}

		return object;

	}

	/**
	 * MemoryContainer inserts the info as a new MemoryObject in its Memory list.
	 */
	@Override
	public synchronized int setI(Object info) {
		return setI(info, -1.0d);
	}

	/**
	 * Creates a Memory Object with the info and the evaluation passed.
	 * 
	 * @param info       the info of the new Memory Object.
	 * @param evaluation the evaluation of the new Memory Object.
	 * @return the index of the new Memory Object.
	 */
	public synchronized int setI(Object info, Double evaluation) {

		randchoice = -1;
                MemoryObject mo = new MemoryObject();
		mo.setI(info);
		if (evaluation != -1.0)
			mo.setEvaluation(evaluation);
		mo.setName("");
                last = mo;
		memories.add(mo);
		return memories.indexOf(mo);

	}

	/**
	 * Sets the info of the Memory with the index passed.
	 * 
	 * @param info  the information to be set in the
	 * @param index the index of the memory inside the container.
	 */
	public synchronized void setI(Object info, int index) {
                randchoice = -1;
		if (memories != null && memories.size() > index) {
			Memory memory = memories.get(index);
			if (memory != null) {
                                last = memory;
				if (memory instanceof MemoryObject) {
					memory.setI(info);
				} else if (memory instanceof MemoryContainer) {
					((MemoryContainer) memory).setI(info, index);
				}
			}
		}
	}

	/**
	 * Sets the info and the evaluation of the memory with the index passed inside
	 * this container.
	 * 
	 * @param info       the information to be set in the.
	 * @param index      the index of the memory inside this container.
	 * @param evaluation the evaluation to be set.
	 */
	public synchronized void setI(Object info, Double evaluation, int index) {
            randchoice = -1;
		if (memories != null && memories.size() > index) {
			Memory memory = memories.get(index);
			if (memory != null) {
                                last = memory;
				if (memory instanceof MemoryObject) {
					memory.setI(info);
					memory.setEvaluation(evaluation);
				} else if (memory instanceof MemoryContainer) {
					((MemoryContainer) memory).setI(info, evaluation, index);
				}
			}
		}
	}

	/**
	 * Sets the info as the info and an evaluation passed to a Memory of the type
	 * passed.
	 * 
	 * @param info       the info.
	 * @param evaluation the evaluation to set.
	 * @param type       the type of the Memory
	 * @return the index of the memory
	 */
	public synchronized int setI(Object info, double evaluation, String type) {
                randchoice = -1;
		int index = -1;
		if (memories != null) {
			boolean set = false;
			for (int i = 0; i < memories.size(); i++) {
				Memory memory = memories.get(i);
				if (memory != null && memory instanceof MemoryObject) {
					MemoryObject memoryObject = (MemoryObject) memory;
					if (memoryObject.getName().equalsIgnoreCase(type)) {
						memory.setI(info);
						memory.setEvaluation(evaluation);
						index = i;
						set = true;
                                                last = memory;
						break;
					}
				}
			}
			if (!set) {
				MemoryObject mo = new MemoryObject();
				mo.setI(info);
				mo.setEvaluation(evaluation);
				mo.setName(type);
                                last = mo;
				memories.add(mo);
				index = memories.indexOf(mo);
			}
		}
		return index;
	}

	/**
	 * Gets the evaluation of the last memory accessed with getI
	 * 
	 * @return the evaluation of the last memory accessed with getI.
	 */
	@Override
	public synchronized Double getEvaluation() {
                if (last != null) {
		   return last.getEvaluation();
                }   
                else return null;
	}
        
        /**
	 * Gets the evaluation of a specific Memory at the MemoryContainer
	 * 
	 * @return the evaluation of a specific Memory at the MemoryContainer
	 */
	public synchronized Double getEvaluation(int index) {
                if (memories != null && memories.size() > index) {
			Memory memory = memories.get(index);
			if (memory != null) {
                            return(memory.getEvaluation());
                        }
                }
                return null;
        }        

	/**
	 * Gets the type of the memory which has the greatest evaluation.
	 * 
	 * @return the type of the memory which has the greatest evaluation.
	 */
	@Override
	public synchronized String getName() {
		return name;
	}

        /**
	 * Sets the evaluation of the last memory accessed with getI or setI
	 * 
	 * @param eval  the evaluation to set.
	 */
	@Override
	public synchronized void setEvaluation(Double eval) {
            randchoice = -1;
                if (last != null && last instanceof Memory) {
                    last.setEvaluation(eval);
                }
                else Logger.getAnonymousLogger().log(Level.INFO,"The MemoryContainer {0} still does not have any internal Memories ...",getName()); 
	}

	/**
	 * Sets the evaluation of the a specific memory from the MemoryContainer
	 * 
	 * @param eval  the evaluation to set.
	 * @param index the index of the memory inside this container.
	 */
	public synchronized void setEvaluation(Double eval, int index) {
            randchoice = -1;
		if (memories != null && memories.size() > index) {
			Memory memory = memories.get(index);
			if (memory != null) {
				if (memory instanceof MemoryObject) {
					memory.setEvaluation(eval);
				} else if (memory instanceof MemoryContainer) {
					((MemoryContainer) memory).setEvaluation(eval, index);
				}
			}
		}
	}

	/**
	 * Adds a memory to this container.
	 * 
	 * @param memory the memory to be added in this container
         * @return the index of the added memory
	 */
	public synchronized int add(Memory memory) {
            randchoice = -1;
		int index = -1;
		if (memory != null) {
			memories.add(memory);
			index = memories.indexOf(memory);
                        last = memory;
		}
		return index;
	}

//	/**
//	 * Sets the Java String info as the info and an evaluation passed to a
//	 * Memory of the type passed.
//	 * 
//	 * @param info
//	 *            Java String info.
//	 * @param evaluation
//	 *            the evaluation to set.
//	 * @param type
//	 *            the type of the Memory
//	 * @return the index of the memory
//	 */
//	public synchronized int setI(String info, double evaluation, String type) {
//		int index = -1;
//		if (memories != null) {
//			boolean set = false;
//			for (int i = 0; i < memories.size(); i++) {
//				Memory memory = memories.get(i);
//				if (memory != null && memory instanceof MemoryObject) {
//					MemoryObject memoryObject = (MemoryObject) memory;
//					if (memoryObject.getName().equalsIgnoreCase(type)) {
//						memory.setI(info);
//						memory.setEvaluation(evaluation);
//						index = i;
//						set = true;
//						break;
//					}
//				}
//			}
//			if (!set) {
//				MemoryObject mo = new MemoryObject();
//				mo.setI(info);
//				mo.setEvaluation(evaluation);
//				mo.setType(type);
//				memories.add(mo);
//				index = memories.indexOf(mo);
//			}
//		}
//		return index;
//	}

	/**
	 * Gets all the memories inside this container.
	 * 
	 * @return all the memories inside this container.
	 */
	public synchronized ArrayList<Memory> getAllMemories() {
		return memories;
	}
        
        /**
	 * Gets a specific memory from inside the container.
	 * 
         * @param i the specific memory to be returned
	 * @return the specified memory
	 */
	public synchronized Memory get(int i) {
            if (i >= 0 && i < memories.size()) {
		return memories.get(i);
            }    
            else return null;
	}

	/**
	 * Gets the internal memory which has the name passed.
	 * 
	 * @param name the name of the memory whose info is searched.
	 * @return the memory which has the name passed or null if it is not found.
	 */
	public synchronized Memory getInternalMemory(String name) {
		for (Memory m : memories) {
			if (m.getName().equals(name))
				return (m);
		}
		return (null);
	}

	/**
	 * Get the TimeStamp of the last returned Memory.
	 * 
	 * @return the timestamp in Long format
	 */
	public synchronized Long getTimestamp() {
            if (last != null) {
		return(last.getTimestamp());
            }    
            else {
                Logger.getAnonymousLogger().log(Level.INFO, "The MemoryContainer {0} still does not have any internal Memories ...",getName());
                return null;
            }
	}
        
        /**
	 * Get the TimeStamp of a specific index inside the MemoryContainer.
	 * 
	 * @return the timestamp in Long format
	 */
        
        public synchronized Long getTimestamp(int index) {
		if (memories != null && memories.size() > index) {
			Memory memory = memories.get(index);
			if (memory != null) {
				return(memory.getTimestamp());
			}
		}
                return(null);
	}

        /**
         * Adds a new MemoryObserver for the current Container
         * @param memoryObserver the observer to be added
         */
	@Override
	public synchronized void addMemoryObserver(MemoryObserver memoryObserver) {
		for (Memory memory : memories) {
			memory.addMemoryObserver(memoryObserver);
		}

	}
        
        /**
         * Removes a MemoryObserver from the current Container list
         * @param memoryObserver the observer to be removed
         */
        @Override
	public synchronized void removeMemoryObserver(MemoryObserver memoryObserver) {
		for (Memory memory : memories) {
			memory.removeMemoryObserver(memoryObserver);
		}

	}
        
        /**
         * Sets a new Policy for selecting an info with getI()
         * @param pol the new Policy to be used
         */
        public void setPolicy(Policy pol) {
            randchoice = -1;
            policy = pol;
        }
        
        /**
         * Returns the current Policy being used for selecting an info, when a getI() is called
         * @return the current Policy actually in use
         */
        public Policy getPolicy() {
            return policy;
        }

}
