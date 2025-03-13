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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.cst.core.exceptions.CodeletThresholdBoundsException;
import br.unicamp.cst.core.exceptions.MemoryObjectNotFoundException;
import br.unicamp.cst.support.CodeletsProfiler;
import br.unicamp.cst.support.CodeletsProfiler.FileFormat;
import br.unicamp.cst.support.ExecutionTimeWriter;
import br.unicamp.cst.support.ProfileInfo;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The <b><i>Codelet</i></b> class, together with the <b><i>MemoryObject</i></b>
 * class and the <b><i>Mind</i></b> class is one of the most important classes
 * in the CST toolkit. According to the Baars-Franklin architecture,
 * consciousness is the emergence of a serial stream on top of a parallel set of
 * interacting devices. In the Baars-Franklin architectures, such devices are
 * called "codelets", which are small pieces of code specialized in performing
 * simple tasks. In a CST-built cognitive architecture, everything is either a
 * <b><i>Codelet</i></b> or a <b><i>MemoryObject</i></b>. Codelets are used to
 * implement every kind of processing in the architecture.
 * 
 * Codelets have two kinds of inputs: standard inputs and broadcast inputs.
 * Standard inputs are used to convey access to MemoryObjects. Broadcast inputs
 * come from consciousness, and can also be used. Nevertheless, Standard inputs
 * are usually fixed (but can be changed through learning mechanisms), and
 * Broadcast inputs change all the time, due to the consciousness mechanism.
 * Codelets also have outputs. Outputs are used for the Codelets to write or
 * generate new MemoryObjects. Codelets also have an Activation level, which can
 * be used in some situations.
 * 
 * @author A. L. O. Paraense
 * @author K. Raizer
 * @see Memory
 * @see Mind
 * @see Runnable
 */
public abstract class Codelet implements Runnable, MemoryObserver {
	/**
	 * Activation level of the Codelet. Ranges from 0.0 to 1.0d.
	 */
	protected volatile double activation = 0.0d;

	/**
	 * Threshold of the codelet, which is used to decide if it runs or not. If
	 * activation is equal or greater than activation, codelet runs
	 * proc().Ranges from 0.0 to 1.0d.
	 */
	protected volatile double threshold = 0.0d;
	/**
	 * Input memories, the ones that are read.
	 */
	protected volatile List<Memory> inputs = new ArrayList<Memory>();
	/**
	 * Output memories, the ones that are written.
	 */
	protected volatile List<Memory> outputs = new ArrayList<Memory>();
	/**
	 * Input memories, the ones that were broadcasted.
	 */
	protected volatile List<Memory> broadcast = new ArrayList<Memory>();

	/** defines if proc() should be automatically called in a loop */
	protected volatile boolean loop = true; //
	
	/** defines if codelet is a memory observer (runs when memory input changes) */
	protected volatile boolean isMemoryObserver = false; //
	
	/**
	 * If the proc() method is set to be called automatically in a loop, this
	 * variable stores the time step for such a loop. A timeStep of value 0
	 * means that the proc() method should be called continuously, without
	 * interval.
	 */
	protected long timeStep = 300l; //

	/**
	 * A codelet is a priori enabled to run its proc(). However, if it tries to
	 * read from a given output and fails, it becomes not able to do so.
	 */
	private volatile boolean enabled = true;

	/** Must be zero for this codelet to be enabled */
	private int enable_count = 0;

	/** Gives this codelet a name, mainly for debugging purposes */
	protected String name = Thread.currentThread().getName();

	/** The time for the last proc() execution for profiling purposes */
	volatile long laststarttime = 0l;

	/** This variable is a safe lock for multithread access */
	public volatile Lock lock = new ReentrantLock();

	/**
	 * This method is used in every Codelet to capture input, broadcast and
	 * output MemoryObjects which shall be used in the proc() method. This
	 * abstract method must be implemented by the user. Here, the user must get
	 * the inputs and outputs it needs to perform proc.
	 */
	public abstract void accessMemoryObjects();

	/**
	 * This abstract method must be implemented by the user. Here, the user must
	 * calculate the activation of the codelet before it does what it is
	 * supposed to do in proc();
	 */
	public abstract void calculateActivation();

	/**
	 * Main Codelet function, to be implemented in each subclass.
	 */
	public abstract void proc();

	private volatile Timer timer = new Timer();

	/**
	 * Option for profiling execution times
	 */
	private boolean isProfiling = false;

	/**
	 * Information for profiling
	 */
	private List<ProfileInfo> profileInfo = new ArrayList<>();

        /**
	 * Codelet profiler
	 */
	private CodeletsProfiler codeletProfiler;

	/**
	 * When first activated, the thread containing this codelet runs the proc()
	 * method
	 */
	public synchronized void run() {
		try {
			this.timer.cancel(); // this will cancel the current task. if there
									// is no active task, nothing happens
			this.timer = new Timer();

			timer.schedule(new CodeletTimerTask(), 0l); // first execution
														// should be immediate,
														// hence the 0l in delay
														// for scheduling

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts this codelet execution.
	 */
	public synchronized void start() {
            if (isMemoryObserver == false) {
		Thread t = new Thread(this);
		t.start();
            }    
	}

	/**
	 * Tells this codelet to stop looping (stops running)
	 */
	public synchronized void stop() {
		this.setLoop(false);
		if (Codelet.this.codeletProfiler != null) {
			Codelet.this.codeletProfiler.finishProfile(Codelet.this);
		}
	}

	/**
	 * Safe access to other Codelets through reentrant locks.
	 * 
	 * @param accesing
	 *            the Codelet accessing.
	 * @return true if is impeding access.
	 */
	public synchronized boolean impendingAccess(Codelet accesing) {
		Boolean myLock = false;
		Boolean yourLock = false;
		try {
			myLock = lock.tryLock();
			yourLock = accesing.lock.tryLock();
		} finally {
			if (!(myLock && yourLock)) {
				if (myLock) {
					lock.unlock();
				}
				if (yourLock) {
					accesing.lock.unlock();
				}
			}
		}
		return myLock && yourLock;
	}

	/**
	 * Safe access to MemoryBuffers through reentrant locks
	 * 
	 * @param accesing
	 *            the Memory Buffer accessing.
	 * @return true if is impending Access.
	 */
	public synchronized boolean impendingAccessBuffer(MemoryBuffer accesing) {

		Boolean myLock = false;
		Boolean yourLock = false;
		try {
			myLock = lock.tryLock();
			yourLock = accesing.lock.tryLock();
		} finally {
			if (!(myLock && yourLock)) {
				if (myLock) {
					lock.unlock();
				}
				if (yourLock) {
					accesing.lock.unlock();
				}
			}
		}
		return myLock && yourLock;
	}

	/**
	 * Gets if this Codelet is looping.
	 * 
	 * @return the loop
	 */
	public synchronized boolean shouldLoop() {
		return loop;
	}

	/**
	 * Sets this Codelet to loop.
	 * 
	 * @param loop
	 *            the loop to set
	 */
	public synchronized void setLoop(boolean loop) {
		this.loop = loop;
	}

        /**
	 * Gets the enable status.
	 * 
	 * @return the enable state.
	 */
	public synchronized boolean getEnabled() {
		return enabled;
	}
        
        /**
	 * Set the enable status.
	 * 
	 * @param status the new enable status
	 */
	public synchronized void setEnabled(boolean status) {
		enabled = status;
                if (status == true) enable_count = 0;
	}
        
        
	/**
	 * Gets this Codelet name.
	 * 
	 * @return the name.
	 */
	public synchronized String getName() {
		return name;
	}

	/**
	 * Sets this Codelet name.
	 * 
	 * @param name
	 *            the name to set
	 */
	public synchronized void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets if this Codelet is looping.
	 * 
	 * @return the loop
	 */
	public synchronized boolean isLoop() {
		return loop;
	}

	/**
	 * Gets this Codelet activation.
	 * 
	 * @return the activation
	 */
	public synchronized double getActivation() {
		return activation;
	}

	/**
	 * Sets this codelet's activation.
	 * 
	 * @param activation
	 *            the activation to set
	 * @throws CodeletActivationBoundsException
	 *             exception triggered if an activation lower than 0 or bigger
	 *             than 1 is set
	 */
	public synchronized void setActivation(double activation) throws CodeletActivationBoundsException {
		if (activation > 1.0d) {
			this.activation = 1.0d;
			throw (new CodeletActivationBoundsException("Codelet activation set to value > 1.0"));
		} else if (activation < 0.0d) {
			this.activation = 0.0d;
			throw (new CodeletActivationBoundsException("Codelet activation set to value < 0.0"));
		} else {
			this.activation = activation;
		}
	}

	/**
	 * Gets the input memories list.
	 * 
	 * @return the inputs.
	 */
	public synchronized List<Memory> getInputs() {
		return inputs;
	}

	/**
	 * Sets the input memories list.
	 * 
	 * @param inputs
	 *            the inputs to set.
	 */
	public synchronized void setInputs(List<Memory> inputs) {
		this.inputs = inputs;
	}

	/**
	 * Add one memory to the input list.
	 * 
	 * @param input
	 *            one input to set.
	 */
	public synchronized void addInput(Memory input) {
		if (isMemoryObserver) {
			input.addMemoryObserver(this);
		}
		this.inputs.add(input);
	}

	/**
	 * Add a list of memories to the input list.
	 * 
	 * @param inputs
	 *            a list of inputs.
	 */
	public synchronized void addInputs(List<Memory> inputs) {
		if (isMemoryObserver) {
		    for (Memory memory : inputs) {
		    	memory.addMemoryObserver(this);
			}
		}
		this.inputs.addAll(inputs);
	}

	/**
	 * Add a memory to the output list.
	 * 
	 * @param output
	 *            one output to set.
	 */
	public synchronized void addOutput(Memory output) {
		this.outputs.add(output);
	}

	/**
	 * Removes a given memory from the output list.
	 * 
	 * @param output
	 *            the memory to be removed from output.
	 */
	public synchronized void removesOutput(Memory output) {
		this.outputs.remove(output);
	}

	/**
	 * Removes a given memory from the input list.
	 * 
	 * @param input
	 *            the memory to be removed from input.
	 */
	public synchronized void removesInput(Memory input) {
		this.inputs.remove(input);
	}

	/**
	 * Removes a given memory list from the output list.
	 * 
	 * @param outputs
	 *            the list of memories to be removed from output.
	 */
	public synchronized void removeFromOutput(List<Memory> outputs) {
		this.outputs.removeAll(outputs);
	}

	/**
	 * Removes a given list of memories from the input list.
	 * 
	 * @param inputs
	 *            the list of memories to be removed from input.
	 */
	public synchronized void removeFromInput(List<Memory> inputs) {
		this.inputs.removeAll(inputs);
	}

	/**
	 * Adds a list of memories to the output list.
	 * 
	 * @param outputs
	 *            the list of memories to be added to the output.
	 */
	public synchronized void addOutputs(List<Memory> outputs) {
		this.outputs.addAll(outputs);
	}

	/**
	 * Gets the list of output memories.
	 * 
	 * @return the outputs.
	 */
	public synchronized List<Memory> getOutputs() {
		return outputs;
	}

	/**
	 * Gets a list of output memories of a certain type.
	 * 
	 * @param type
	 *            the type of memories to be fetched from the output.
	 * @return the list of all memory objects in output of a given type.
	 */
	public synchronized ArrayList<Memory> getOutputsOfType(String type) {
		ArrayList<Memory> outputsOfType = new ArrayList<Memory>();

		if (outputs != null && outputs.size() > 0)
			for (Memory mo : this.outputs) {
				if (mo.getName() != null && mo.getName().equalsIgnoreCase(type)) {
					outputsOfType.add(mo);
				}
			}
		return outputsOfType;
	}

	/**
	 * Gets a list of input memories of a certain type.
	 * 
	 * @param type
	 *            the type of memories to be retrieved.
	 * @return the list of memory objects in input of a given type.
	 */
	public synchronized ArrayList<Memory> getInputsOfType(String type) {
		ArrayList<Memory> inputsOfType = new ArrayList<Memory>();

		if (inputs != null && inputs.size() > 0)
			for (Memory mo : this.inputs) {
				if (mo.getName() != null && mo.getName().equalsIgnoreCase(type)) {
					inputsOfType.add(mo);
				}
			}

		return inputsOfType;
	}

	/**
	 * Sets the list of output memories.
	 * 
	 * @param outputs
	 *            the outputs to set.
	 */
	public synchronized void setOutputs(List<Memory> outputs) {
		this.outputs = outputs;
	}

	/**
	 * Gets the list of broadcast memories.
	 * 
	 * @return the broadcast.
	 */
	public synchronized List<Memory> getBroadcast() {
		return broadcast;
	}

	/**
	 * Sets the list of broadcast memories.
	 * 
	 * @param broadcast
	 *            the broadcast to set.
	 */
	public synchronized void setBroadcast(List<Memory> broadcast) {
		this.broadcast = broadcast;
	}

	/**
	 * Returns a specific memory (with the given name) from the broadcast list
	 * of the Codelet.
	 * 
	 * @param name
	 *            the name of a memory to be retrieved at the broadcast list.
	 * @return the memory in the broadcast list.
	 */
	public synchronized Memory getBroadcast(String name) {
		if (broadcast != null && broadcast.size() > 0)
			for (Memory mo : broadcast) {
				if (mo.getName() != null && mo.getName().equalsIgnoreCase(name))
					return mo;
			}
		return null;
	}

	/**
	 * Adds a memory to the broadcast list.
	 * 
	 * @param b
	 *            one broadcast input to set.
	 */
	public synchronized void addBroadcast(Memory b) {
		if (isMemoryObserver) {
			b.addMemoryObserver(this);
		}
		this.broadcast.add(b);
	}

	/**
	 * Adds a list of memories to the broadcast input list.
	 * 
	 * @param broadcast
	 *            one input to set.
	 */
	public synchronized void addBroadcasts(List<Memory> broadcast) {
		if (isMemoryObserver) {
			for (Memory memory : broadcast) {
				memory.addMemoryObserver(this);
			}
		}
		this.broadcast.addAll(broadcast);
	}

	/**
	 * Gets the Codelet's Java Thread name, for debugging purposes.
	 * 
	 * @return The name of the thread running this Codelet.
	 */
	public synchronized String getThreadName() {
		return Thread.currentThread().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public synchronized String toString() {
		final int maxLen = 10;
		return "Codelet [activation=" + activation + ", " + "name=" + name + ", "
				+ (broadcast != null ? "broadcast=" + broadcast.subList(0, Math.min(broadcast.size(), maxLen)) + ", "
						: "")
				+ (inputs != null ? "inputs=" + inputs.subList(0, Math.min(inputs.size(), maxLen)) + ", " : "")
				+ (outputs != null ? "outputs=" + outputs.subList(0, Math.min(outputs.size(), maxLen)) : "") + "]";
	}

	/**
	 * This method returns an input memory from its input list. If it couldn't
	 * find the given M, it sets this codelet as not able to perform proc(), and
	 * keeps trying to find it.
	 * 
	 * @param type
	 *            type of memory it needs.
	 * @param index
	 *            position of memory in the sublist.
	 * @return memory of type at position.
	 */
	public synchronized Memory getInput(String type, int index) {
		Memory inputMO = null;
		ArrayList<Memory> listMO = new ArrayList<Memory>();

		if (inputs != null && inputs.size() > 0)
			for (Memory mo : inputs) {
				if (mo.getName() != null && mo.getName().equalsIgnoreCase(type)) {
					listMO.add(mo);
				}
			}

		if (listMO.size() >= index + 1) {
			inputMO = listMO.get(index);
			this.enabled = true;
		} else {
			this.enabled = false; // It must not run proc yet, for it still
									// needs to find this mo it wants
			enable_count++;
		}

		return inputMO;
	}

	/**
	 * Gets a certain memory from the input list by its name.
	 * 
	 * @param name
	 *            the name of the memory being searched.
	 * @return the memory searched by name or null if not found.
	 */
	public synchronized Memory getInput(String name) {
		if (inputs != null && inputs.size() > 0)
			for (Memory mo : inputs) {
				if (mo.getName() != null && mo.getName().equalsIgnoreCase(name))
					return mo;
			}

		return null;
	}

	/**
	 * This method returns an output memory from its output list. If it couldn't
	 * find the given M, it sets this codelet as not able to perform proc(), and
	 * keeps trying to find it.
	 * 
	 * @param type
	 *            type of memory it needs.
	 * @param index
	 *            position of memory in the sublist.
	 * @return memory of type at position.
	 */
	public synchronized Memory getOutput(String type, int index) {
		Memory outputMO = null;
		ArrayList<Memory> listMO = new ArrayList<Memory>();

		if (outputs != null && outputs.size() > 0)
			for (Memory mo : outputs) {
				if (mo != null && type != null && mo.getName() != null && mo.getName().equalsIgnoreCase(type)) {
					listMO.add(mo);
				}
			}

		if (listMO.size() >= index + 1) {
			outputMO = listMO.get(index);
			this.enabled = true;
		} else {
			this.enabled = false; // It must not run proc yet, for it still
									// needs to find this mo it wants
			enable_count++;
		}

		return outputMO;
	}

	/**
	 * Gets a Memory from the output list by its name.
	 * 
	 * @param name
	 *            the name of the memory.
	 * @return the memory with the name searched or null if not found.
	 */
	public synchronized Memory getOutput(String name) {
		if (outputs != null && outputs.size() > 0)
			for (Memory mo : outputs) {
				if (mo.getName() != null && mo.getName().equalsIgnoreCase(name))
					return mo;
			}

		return null;
	}

	/**
	 * Return a memory if its name is found at the broadcast list ... if more
	 * than one memory with the same name, it return the one at the index
	 * position.
	 * 
	 * @param type
	 *            the name of the memory to be retrieved in the broadcast list.
	 * @param index
	 *            the index to be considered while multiple equal names are
	 *            found within the broadcast list.
	 * @return the memory.
	 */
	public synchronized Memory getBroadcast(String type, int index) {
		Memory broadcastMO = null;

		ArrayList<Memory> listMO = new ArrayList<Memory>();

		if (broadcast != null && broadcast.size() > 0) {
			for (Memory mo : broadcast) {
				if (mo.getName() != null && mo.getName().equalsIgnoreCase(type)) {
					listMO.add(mo);
				}
			}
		}

		if (listMO.size() >= index + 1) {
			broadcastMO = listMO.get(index);
		}

		return broadcastMO;
	}

	/**
	 * Gets the Codelet's threshold.
	 * 
	 * @return the threshold.
	 */
	public synchronized double getThreshold() {
		return threshold;
	}

	/**
	 * Sets the Codelet's threshold.
	 * 
	 * @param threshold
	 *            the threshold to be set.
	 * @throws CodeletThresholdBoundsException
	 *             the exception thrown if the threshold value is less than zero
	 *             or greater than 1.
	 */
	public synchronized void setThreshold(double threshold) throws CodeletThresholdBoundsException {
		if (threshold > 1.0d) {
			this.threshold = 1.0d;
			throw (new CodeletThresholdBoundsException("Codelet threshold set to value > 1.0"));
		} else if (threshold < 0.0d) {
			this.threshold = 0.0d;
			throw (new CodeletThresholdBoundsException("Codelet threshold set to value < 0.0"));
		} else {
			this.threshold = threshold;
		}
	}

	/**
	 * Gets the Codelet' s timestep.
	 * 
	 * @return the timeStep.
	 */
	public synchronized long getTimeStep() {
		return timeStep;
	}

	/**
	 * Sets the Codelet's timestep.
	 * 
	 * @param timeStep
	 *            the timeStep to set.
	 */
	public synchronized void setTimeStep(long timeStep) {
		this.timeStep = timeStep;
	}

	/**
	 * Gets if this Codelet is profiling.
	 * 
	 * @return the isProfiling.
	 */
	public synchronized boolean isProfiling() {
		return isProfiling;
	}

	/**
	 * Sets if this Codelet is profiling.
	 * 
	 * @param isProfiling
	 *            the isProfiling to set
	 */
	public synchronized void setProfiling(boolean isProfiling) {
		this.isProfiling = isProfiling;
	}
	
	/**
	 * Sets this Codelet to be a memory observer.
	 * 
	 * @param isMemoryObserver
	 *            the isMemoryObserver to set
	 */
	public synchronized void setIsMemoryObserver(boolean isMemoryObserver) {
		this.isMemoryObserver = isMemoryObserver;
	}
        
        @SuppressWarnings("empty-statement")
        public synchronized void setPublishSubscribe(boolean enable) {
            if (enable) {
                setIsMemoryObserver(true);
                for (Memory m : inputs) {
                    m.addMemoryObserver(this);
                }
            } else {
                for (Memory m : inputs) {
                    m.removeMemoryObserver(this);
                }
                setIsMemoryObserver(false);
                try { this.wait(300L); } 
                catch(InterruptedException e) {
                    // just ignore exception
                };
                run();
            }
        }    
	
	/**
	 * Sets Codelet Profiler
	 * 
	 * @param filePath 
	 * 			path to create file
	 * @param fileName
	 * 			name file
	 * @param mindIdentifier
	 * 			mind identifier in file
	 * @param queueSize
	 * 			max queue size which a write in file must be done
	 * @param intervalTimeMillis
	 * 			max interval in millis which a write in file must be done
	 * @param fileFormat
	 * 			desired file format CSV or JSON
	 * 		  
	 */
	public void setCodeletProfiler(String filePath, String fileName, String mindIdentifier,Integer queueSize, Long intervalTimeMillis, FileFormat fileFormat) {
		if (intervalTimeMillis == null) {
		  this.codeletProfiler = new CodeletsProfiler(filePath, fileName, mindIdentifier, queueSize, fileFormat);
		} else if (queueSize == null) {
			this.codeletProfiler = new CodeletsProfiler(filePath, fileName, mindIdentifier, intervalTimeMillis, fileFormat);
		} else {
			this.codeletProfiler = new CodeletsProfiler(filePath, fileName, mindIdentifier, queueSize, intervalTimeMillis, fileFormat);
		}		
	}
        
        private void raiseException() throws MemoryObjectNotFoundException {
            throw new MemoryObjectNotFoundException("This Codelet could not find a memory object it needs: "
							+ Codelet.this.name);
        }
        
        /**
         *  runs when codelet is a memory observer and memory input changes
         */
        @Override
    	public void notifyCodelet() {
    		long startTime = 0l;
    		long endTime = 0l;
    		long duration = 0l;

    		try {
    			if (isProfiling)
    				startTime = System.currentTimeMillis();
    			accessMemoryObjects();// tries to connect to memory objects
    			if (enable_count == 0) {
    				calculateActivation();
    				if (activation >= threshold)
    					proc();
    			} else {
                                    raiseException();
    			}
    			enable_count = 0;
                } catch (MemoryObjectNotFoundException ex) {
                        Logger.getLogger(Codelet.class.getName()).log(Level.SEVERE, ex.getMessage());
    		} finally {

    			if (Codelet.this.codeletProfiler != null) {
    				Codelet.this.codeletProfiler.profile(Codelet.this);
    			}
    			if (isProfiling) {
                    endTime = System.currentTimeMillis();
    				duration = (endTime - startTime);
    				ProfileInfo pi = new ProfileInfo(duration, startTime, laststarttime);
    				profileInfo.add(pi);
    				laststarttime = startTime;

    				if (profileInfo.size() >= 50) {

    					ExecutionTimeWriter executionTimeWriter = new ExecutionTimeWriter();
    					executionTimeWriter.setCodeletName(name);
                                        executionTimeWriter.setPath("profile/");
    					executionTimeWriter.setProfileInfo(profileInfo);

    					Thread thread = new Thread(executionTimeWriter);
    					thread.start();

    					profileInfo = new ArrayList<>();
    				}
    			}
    		}
    	}

	private class CodeletTimerTask extends TimerTask {

		@Override
		public synchronized void run() {
			
			long startTime = 0l;
			long endTime = 0l;
			long duration = 0l;

			try {

				if (isProfiling)
					startTime = System.currentTimeMillis();

				if (!isMemoryObserver)
                                    accessMemoryObjects();// tries to connect to memory objects

				if (enable_count == 0) {
                                    if (isMemoryObserver == false) {
					calculateActivation();
					if (activation >= threshold)
						proc();
                                    }    
				} else {
                                    raiseException();
				}

				enable_count = 0;

                    } catch (MemoryObjectNotFoundException ex) {
                        Logger.getLogger(Codelet.class.getName()).log(Level.SEVERE, ex.getMessage());
                    } finally {

				if (!isMemoryObserver && shouldLoop()) 
					timer.schedule(new CodeletTimerTask(), timeStep);
				if (Codelet.this.codeletProfiler != null) {
					Codelet.this.codeletProfiler.profile(Codelet.this);
				}
				if (isProfiling) {
                                        endTime = System.currentTimeMillis();
					duration = (endTime - startTime);
					ProfileInfo pi = new ProfileInfo(duration, startTime, laststarttime);
					profileInfo.add(pi);
					laststarttime = startTime;

					if (profileInfo.size() >= 50) {

						ExecutionTimeWriter executionTimeWriter = new ExecutionTimeWriter();
						executionTimeWriter.setCodeletName(name);
                                                executionTimeWriter.setPath("profile/");
						executionTimeWriter.setProfileInfo(profileInfo);

						Thread thread = new Thread(executionTimeWriter);
						thread.start();

						profileInfo = new ArrayList<>();
					}
				}
			}
		}
	}
}