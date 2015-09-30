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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.cst.core.exceptions.CodeletThresholdBoundsException;



/**
 * The <b><i>Codelet</i></b> class, together with the <b><i>MemoryObject</i></b> class and the 
 * <b><i>Mind</i></b> class is one of the most important classes in the CST toolkit.  
 * According to the Baars-Franklin architecture, consciousness is the emergence
 * of a serial stream on top of a parallel set of interacting devices. 
 * In the Baars-Franklin architectures, such devices are called "codelets",
 * which are small pieces of code specialized in performing simple tasks. In a CST-built
 * cognitive architecture, everything is either a <b><i>Codelet</i></b> or a <b><i>MemoryObject</i></b>. Codelets are used
 * to implement every kind of processing in the architecture. 
 * 
 * Codelets have two kinds of inputs: standard inputs and broadcast inputs. Standard inputs are
 * used to convey access to MemoryObjects. Broadcast inputs come from consciousness, and can
 * also be used. Nevertheless, Standard inputs are usually fixed (but can be changed through
 * learning mechanisms), and Broadcast inputs change all the time, due to the consciousness 
 * mechanism. Codelets also have outputs. Outputs are used for the Codelets to write or generate
 * new MemoryObjects. Codelets also have an Activation level, which can be used in some
 * situations. 
 * 
 * @see MemoryObject 
 * @see Mind
 * 
 * @author andre.paraense
 * @author klaus.raizer
 */
public abstract class Codelet implements Runnable 
{	
	/**
	 * Activation level of the Codelet. Ranges from 0.0 to 1.0d.
	 */
	private double activation=0.0d;

	/**
	 * Threshold of the codelet, which is used to decide if it runs or not. If activation is equal or
	 * greater than activation, codelet runs proc().Ranges from 0.0 to 1.0d.
	 */
	private double threshold=0.0d;
	/**
	 * Input memory objects, the ones that are read.
	 */
	private List<MemoryObject> inputs=new ArrayList<MemoryObject>();
	/**
	 * Output memory objetcs, the ones that are written.
	 */
	private List<MemoryObject> outputs=new ArrayList<MemoryObject>();
	/**
	 * Input memory objects, the ones that were broadcasted.
	 */
	private List<MemoryObject> broadcast=new ArrayList<MemoryObject>();
	
	/** defines if proc() should be automatically called in a loop */
	private boolean loop=true; //
	
	/** If the proc() method is set to be called automatically in a loop, this
         * variable stores the time step for such a loop. A timeStep of value 0 means
         * that the proc() method should be called continuously, without interval.
         */
	protected long timeStep=0; //
	
	/** A codelet is a priori enabled to run its proc(). However, if it tries to read from a given output and fails, it becomes not able to do so.*/
	private boolean enabled=true; 
	
	/** Must be zero for this codelet to be enabled*/
	private int enable_count=0;
	
	/** Gives this codelet a name, mainly for debugging purposes */
	private String name=Thread.currentThread().getName();
	
	/** This variable is a safe lock for multithread access */
	public Lock lock= new ReentrantLock();

	/** 
	 * This method is used in every Codelet to capture input, broadcast and output MemoryObjects
         * which shall be used in the proc() method. 
         * This abstract method must be implemented by the user. Here, the user must get the inputs and outputs it needs to perform proc.
	 */
	public abstract void accessMemoryObjects();

	/** 
	 * This abstract method must be implemented by the user. Here, the user must calculate the activation of the codelet before it does what it 
	 * is supposed to do in proc();
	 */
	public abstract void calculateActivation();

	/**
	 * Main Codelet function, to be implemented in each subclass.
	 */
	public abstract void proc();

	/**
	 * When first activated, the thread containing this codelet runs the proc() method 
	 */
	public void run() 
	{ 

		do
		{
			try
			{
				this.accessMemoryObjects();//tries to connect to memory objects			

				if (enable_count==0)
				{
					this.calculateActivation();
					if(activation>=threshold)
						proc(); 				
				}else
				{					
					System.out.println("This codelet thread could not find a memory object it needs (Class):"+this.getClass().getCanonicalName());
				}
				enable_count=0;

				if(timeStep > 0)
				{
									
					long timeMarker = System.currentTimeMillis();

					while(System.currentTimeMillis() < timeMarker + timeStep){}
				}	
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		
		}while(this.shouldLoop());
	}

	public synchronized void start()
	{ 
		Thread t = new Thread(this);			
		t.start();		
	}

	/**
	 * Tells this codelet to stop looping (stops running)
	 */
	public synchronized void stop()
	{
		this.setLoop(false);
	}

	/**
	 * Safe access to other Codelets through reentrant locks
	 * 
	 * @param accesing
	 * @return
	 */
	public boolean impendingAccess(Codelet accesing)
	{
		Boolean myLock = false;
		Boolean yourLock = false;
		try
		{
			myLock = lock.tryLock();
			yourLock = accesing.lock.tryLock();
		} finally
		{
			if (!(myLock && yourLock))
			{
				if (myLock)
				{
					lock.unlock();
				}
				if (yourLock)
				{
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
	 * @return
	 */
	public boolean impendingAccessBuffer(MemoryBuffer accesing)
	{

		Boolean myLock = false;
		Boolean yourLock = false;
		try
		{
			myLock = lock.tryLock();
			yourLock = accesing.lock.tryLock();
		} finally
		{
			if (!(myLock && yourLock))
			{
				if (myLock)
				{
					lock.unlock();
				}
				if (yourLock)
				{
					accesing.lock.unlock();
				}
			}
		}
		return myLock && yourLock;
	}	

	/**
	 * @return the loop
	 */
	public boolean shouldLoop() 
	{
		return loop;
	}
	/**
	 * @param loop the loop to set
	 */
	public void setLoop(boolean loop) {
		this.loop = loop;
	}	

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	
	/**
	 * @return the loop
	 */
	public boolean isLoop() 
	{
		return loop;
	}

	/**
	 * @return the activation
	 */
	public synchronized double getActivation()
	{
		return activation;
	}

	/**
	 * @param activation the activation to set
	 * @throws CodeletActivationBoundsException 
	 */
	public synchronized void setActivation(double activation) throws CodeletActivationBoundsException
	{
		if(activation>1.0d)
		{
			this.activation = 1.0d;
			throw (new CodeletActivationBoundsException("Codelet activation set to value > 1.0"));
		}else if(activation<0.0d)
		{
			this.activation = 0.0d;
			throw (new CodeletActivationBoundsException("Codelet activation set to value < 0.0"));
		}else
		{
			this.activation = activation;
		}		
	}

	/**
	 * @return the inputs
	 */
	public synchronized List<MemoryObject> getInputs()
	{
		return inputs;
	}

	/**
	 * @return a string list with input info
	 */

//	public synchronized ArrayList<String> getInputsInfo()
//	{
//		ArrayList<String> inputsInfo=new ArrayList<String>();   
//		
//		if(inputs!=null)
//			for(MemoryObject input:inputs)
//			{
//				inputsInfo.add(input.getInfo());
//			}
//
//		return inputsInfo;
//	}

	/**
	 * @param inputs the inputs to set
	 */
	public synchronized void setInputs(List<MemoryObject> inputs)
	{
		this.inputs = inputs;
	}
	/**
	 * @param adds one input to set
	 */
	public synchronized void addInput(MemoryObject input)//TODO:  how should we deal with an attempt to add an existing MO?
	{
		this.inputs.add(input);
	}
	public synchronized void pushInput(MemoryObject input)//TODO:  how should we deal with an attempt to add an existing MO?
	{
		addInput(input);
	}
	/**
	 * @param adds a list of inputs
	 */
	public synchronized void pushInputs(List<MemoryObject> inputs)//TODO:  how should we deal with an attempt to add an existing MO?
	{
		this.inputs.addAll(inputs);
	}
	/**
	 * @param adds one output to set
	 */
	public synchronized void addOutput(MemoryObject output)
	{
		this.outputs.add(output);
	}
	public synchronized void pushOutput(MemoryObject output)
	{
		addOutput(output);
	}
	/**
	 * Removes a given memory object from output list
	 * @param output
	 */
	public synchronized void removesOutput(MemoryObject output){
		this.outputs.remove(output);
	}
	/**
	 * Removes a given memory object from input list
	 * @param output
	 */
	public synchronized void removesInput(MemoryObject input){
		this.inputs.remove(input);
	}

	public synchronized void removeFromOutput(List<MemoryObject> outputs)
	{
		this.outputs.removeAll(outputs);
	}
	public synchronized void removeFromInput(List<MemoryObject> inputs)
	{
		this.inputs.removeAll(inputs);
	}
	public synchronized void pushOutputs(List<MemoryObject> outputs)
	{
		this.outputs.addAll(outputs);
	}
	/**
	 * @return the outputs
	 */
	public synchronized List<MemoryObject> getOutputs()
	{
		return outputs;
	}

	/**
	 * @param type
	 * @return list of all memory objects in output of a given type
	 */
	private synchronized ArrayList<MemoryObject> getOutputsOfType(String type) 
	{
		ArrayList<MemoryObject> outputsOfType = new ArrayList<MemoryObject>();
		
		if(outputs!=null&&outputs.size()>0)
			for(MemoryObject mo:this.outputs)
			{
				if(mo.getName()!=null && mo.getName().equalsIgnoreCase(type))
				{
					outputsOfType.add(mo);
				}
			}
		return outputsOfType;
	}

	/**
	 * @param type
	 * @return list of memory objects in input of a given type
	 */
	public synchronized ArrayList<MemoryObject> getInputsOfType(String type) 
	{
		ArrayList<MemoryObject> inputsOfType = new ArrayList<MemoryObject>();
		
		if(inputs!=null&&inputs.size()>0)
			for(MemoryObject mo:this.inputs)
			{
				if(mo.getName()!=null && mo.getName().equalsIgnoreCase(type))
				{
					inputsOfType.add(mo);
				}
			}
		
		return inputsOfType;
	}


	/**
	 * @param outputs the outputs to set
	 */
	public synchronized void setOutputs(List<MemoryObject> outputs)
	{
		this.outputs = outputs;
	}

	/**
	 * @return the broadcast
	 */
	public synchronized List<MemoryObject> getBroadcast()
	{
		return broadcast;
	}


	/**
	 * @param broadcast the broadcast to set
	 */
	public synchronized void setBroadcast(List<MemoryObject> broadcast)
	{
		this.broadcast = broadcast;
	}

	public synchronized MemoryObject getBroadcast(String name) 
	{
		if(broadcast!=null&&broadcast.size()>0)
			for (MemoryObject mo : broadcast) 
			{
				if (mo.name!=null && mo.name.equalsIgnoreCase(name)) 
					return mo;
			}
		return null;
	}

	/**
	 * @param b one input to set
	 */
	 public synchronized void addBroadcast(MemoryObject b)//TODO:  how should we deal with an attempt to add an existing MO?
	 {
		 this.broadcast.add(b);
	 }

	 public synchronized void pushBroadcast(MemoryObject b)//TODO:  how should we deal with an attempt to add an existing MO?
	 {
		 addBroadcast(b);
	 }
	 /**
	  * 
	  * @return The name of the thread running this Codelet
	  */
	 public synchronized String getThreadName(){
		 return Thread.currentThread().getName();
	 }

	 /* (non-Javadoc)
	  * @see java.lang.Object#toString()
	  */
	 @Override
	 public synchronized String toString()
	 {
		 final int maxLen = 10;
		 return "Codelet [activation=" + activation + ", " + "name=" + name + ", " +(broadcast != null ? "broadcast=" + broadcast.subList(0, Math.min(broadcast.size(), maxLen)) + ", " : "") + (inputs != null ? "inputs=" + inputs.subList(0, Math.min(inputs.size(), maxLen)) + ", " : "") + (outputs != null ? "outputs=" + outputs.subList(0, Math.min(outputs.size(), maxLen)) : "") + "]";
	 }

	 /**
	  * This method returns an input memory object from its input list.
	  * If it couldn't find the given MO, it sets this codelet as not able to perform proc(), and keeps trying to find it.
	  * 
	  * @param type type of memory object it needs
	  * @param index position of memory object in the sublist
	  * @return memory object of type at position 
	  */
	 public synchronized MemoryObject getInput(String type, int index)
	 {
		 MemoryObject inputMO = null;
		 ArrayList<MemoryObject> listMO=new ArrayList<MemoryObject>();

		 if(inputs!=null&&inputs.size()>0)
			 for(MemoryObject mo:inputs)
			 {
				 if(mo.getName()!=null && mo.getName().equalsIgnoreCase(type))
				 {
					 listMO.add(mo);
				 }
			 }

		 if(listMO.size()>=index+1){
			 inputMO=listMO.get(index);
			 this.enabled=true;
		 }else{
			 this.enabled=false; //It must not run proc yet, for it still needs to find this mo it wants
			 enable_count++;
		 }

		 return inputMO;
	 }

	 public synchronized MemoryObject getInput(String name) 
	 {
		 if(inputs!=null&&inputs.size()>0)
			 for (MemoryObject mo : inputs) 
			 {
				 if (mo.name!=null && mo.name.equalsIgnoreCase(name)) 
					 return mo;
			 }
		 
		 return null;
	 }

	 /**
	  * This method returns an output memory object from its output list.
	  * If it couldn't find the given MO, it sets this codelet as not able to perform proc(), and keeps trying to find it.
	  * 
	  * @param type type of memory object it needs
	  * @param position position of memory object in the sublist
	  * @return memory object of type at position 
	  */
	 public synchronized MemoryObject getOutput(String type, int index)
	 {
		 MemoryObject outputMO = null;
		 ArrayList<MemoryObject> listMO=new ArrayList<MemoryObject>();

		 if(outputs!=null&&outputs.size()>0)
			 for(MemoryObject mo:outputs)
			 {
				 if(mo!=null && type!=null && mo.getName()!=null && mo.getName().equalsIgnoreCase(type))
				 {
					 listMO.add(mo);
				 }
			 }

		 if(listMO.size()>=index+1)
		 {
			 outputMO=listMO.get(index);
			 this.enabled=true;
		 }else
		 {
			 this.enabled=false; //It must not run proc yet, for it still needs to find this mo it wants
			 enable_count++;
		 }

		 return outputMO;
	 }

	 public synchronized MemoryObject getOutput(String name) 
	 {
		 if(outputs!=null&&outputs.size()>0)
			 for (MemoryObject mo : outputs) 
			 {
				 if (mo.name!=null && mo.name.equalsIgnoreCase(name)) 
					 return mo;
			 }
		 
		 return null;
	 }

	 /**
	  * 
	  * @param type
	  * @param index
	  * @return
	  */
	 public synchronized MemoryObject getBroadcast(String type, int index)
	 {
		 MemoryObject broadcastMO = null;
		 
		 ArrayList<MemoryObject> listMO=new ArrayList<MemoryObject>();

		 if(broadcast!=null&&broadcast.size()>0)
		 {
			 for(MemoryObject mo:broadcast)
			 {
				 if(mo.getName()!=null && mo.getName().equalsIgnoreCase(type))
				 {
					 listMO.add(mo);
				 }
			 }
		 }		

		 if(listMO.size()>=index+1)
		 {
			 broadcastMO=listMO.get(index);
		 }

		 return broadcastMO;
	 }

	 /**
	  * @return the threshold
	  */
	 public synchronized double getThreshold() 
	 {
		 return threshold;
	 }

	 /**
	  * 
	  * @param threshold
	  * @throws CodeletThresholdBoundsException
	  */
	 public synchronized void setThreshold(double threshold) throws CodeletThresholdBoundsException 
	 {		
		 if(threshold>1.0d)
		 {
			 this.threshold = 1.0d;
			 throw (new CodeletThresholdBoundsException("Codelet threshold set to value > 1.0"));
		 }else if(threshold<0.0d)
		 {
			 this.threshold = 0.0d;
			 throw (new CodeletThresholdBoundsException("Codelet threshold set to value < 0.0"));
		 }else
		 {
			 this.threshold = threshold;
		 }
	 }

	/**
	 * @return the timeStep
	 */
	public synchronized long getTimeStep() {
		return timeStep;
	}

	/**
	 * @param timeStep the timeStep to set
	 */
	public synchronized void setTimeStep(long timeStep) {
		this.timeStep = timeStep;
	}	
}
