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

package br.unicamp.cst.behavior.glas;

import java.sql.Timestamp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.RawMemory;
import br.unicamp.cst.memory.WorkingStorage;

/**
 * This class records the sequence of events seen by the agent,
 * @author klaus
 *
 */
public class SequenceBuilderCodelet extends Codelet
{
	private  boolean enabled = true;
	private Memory STIMULUS_MO;
	private Memory ACTION_MO;
	private Memory PREVIOUS_REWARD_MO; //Reward for i=1 a,s pair
	private boolean first_run=true;
	private Timestamp previous_stimulus_time_stamp;
	private JSONArray sequence = new JSONArray();
	private Memory EVENTS_SEQUENCE_MO;
	private int sensed_stimulus;
	private int expected_action;
	private double reward_received;
	private boolean printEvents=false;
	private Memory NEW_EVENT_DETECTED_MO;

	private Memory NEW_STIM_MO;
	private Memory NEW_ACTION_MO;
	private Memory NEW_REWARD_MO;
	
	private RawMemory rawMemory;

	public SequenceBuilderCodelet(RawMemory rawMemory,WorkingStorage ws)
	{
		this.rawMemory = rawMemory;
		
		if(ws!=null)
		{
			ws.registerCodelet(this,"SOLUTION_TREE", 0);
			ws.registerCodelet(this,"STIMULUS", 0);
			ws.registerCodelet(this,"ACTION", 0);
			ws.registerCodelet(this,"REWARD", 0);
		}

		if(rawMemory!=null)
			EVENTS_SEQUENCE_MO=rawMemory.createMemoryObject("EVENTS_SEQUENCE", "");
		this.addOutput(EVENTS_SEQUENCE_MO);
		if(ws!=null)
			ws.putMemoryObject(EVENTS_SEQUENCE_MO);

		if(rawMemory!=null)
			NEW_EVENT_DETECTED_MO=rawMemory.createMemoryObject("NEW_EVENT_DETECTED", "FALSE");
		this.addOutput(NEW_EVENT_DETECTED_MO);
		if(ws!=null)
			ws.putMemoryObject(NEW_EVENT_DETECTED_MO);


		if(rawMemory!=null)
		{
			NEW_STIM_MO = rawMemory.createMemoryObject("NEW_STIM", String.valueOf(false));
			NEW_ACTION_MO = rawMemory.createMemoryObject("NEW_ACTION", String.valueOf(false));
			NEW_REWARD_MO = rawMemory.createMemoryObject("NEW_REWARD", String.valueOf(false));

		}
		
		this.addOutput(NEW_STIM_MO);
		this.addOutput(NEW_ACTION_MO);
		this.addOutput(NEW_REWARD_MO);

		if(ws!=null)
		{
			ws.putMemoryObject(NEW_STIM_MO);
			ws.putMemoryObject(NEW_ACTION_MO);
			ws.putMemoryObject(NEW_REWARD_MO);
		}	

		if(rawMemory!=null)
		{
			STIMULUS_MO = rawMemory.createMemoryObject("STIMULUS", "");
			PREVIOUS_REWARD_MO = rawMemory.createMemoryObject("REWARD", ""); //reward for i-1 s,a pair
		}
		
		this.addInput(STIMULUS_MO);
		this.addInput(PREVIOUS_REWARD_MO);
		if(ws!=null)
		{
			ws.putMemoryObject(STIMULUS_MO);
			ws.putMemoryObject(PREVIOUS_REWARD_MO);
		}
		

	}

	@Override
	public void accessMemoryObjects() {
		//		STIMULUS_MO=this.getInput(MemoryObjectTypesGlas.STIMULUS, 0);
		ACTION_MO=this.getInput("ACTION", 0);
		//		PREVIOUS_REWARD_MO=this.getInput(MemoryObjectTypesGlas.REWARD, 0);		

		//		int index=0;
		//		NEW_STIM_MO = this.getOutput(MemoryObjectTypesGlas.NEW_STIM, index);
		//		NEW_ACTION_MO = this.getOutput(MemoryObjectTypesGlas.NEW_ACTION, index);
		//		NEW_REWARD_MO = this.getOutput(MemoryObjectTypesGlas.NEW_REWARD, index);


	}

	@Override
	public void calculateActivation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void proc() {
		if(enabled){


			boolean new_stim=(NEW_STIM_MO.getI().equals(String.valueOf(true)));
			boolean new_action=(NEW_ACTION_MO.getI().equals(String.valueOf(true)));
			boolean new_reward=(NEW_REWARD_MO.getI().equals(String.valueOf(true)));

			if(new_stim && new_action && new_reward){ //OK to snapshot an event!


				try{
					sensed_stimulus=Integer.valueOf((String) STIMULUS_MO.getI());
				}catch(NumberFormatException e){
					sensed_stimulus=0;
				}

				try{
					expected_action=Integer.valueOf((String) ACTION_MO.getI());
				}catch(NumberFormatException e){
					expected_action=0;
				}

				try{
					reward_received=Double.valueOf((String) PREVIOUS_REWARD_MO.getI());
				}catch(NumberFormatException e){
					reward_received=0.0;
				}
				if(this.printEvents){
					System.out.println("Event:"+sensed_stimulus+", "+expected_action+", "+reward_received);
				}

				JSONObject event;
				try {
					event = new JSONObject();//A new event each time
					event.put(GlasSequenceElements.SENSED_STIMULUS.toString(), sensed_stimulus);
					event.put(GlasSequenceElements.EXPECTED_ACTION.toString(), expected_action);
					event.put(GlasSequenceElements.REWARD_RECEIVED.toString(), reward_received);

					//--------------------
//					int s = event.getInt(GlasSequenceElements.SENSED_STIMULUS.toString());
//					int a = event.getInt(GlasSequenceElements.EXPECTED_ACTION.toString());
//					double r = event.getInt(GlasSequenceElements.REWARD_RECEIVED.toString());
					//----------------
					sequence.put(event);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				EVENTS_SEQUENCE_MO.setI(sequence.toString());

				//----------

				NEW_STIM_MO.setI(String.valueOf(false));
				NEW_ACTION_MO.setI(String.valueOf(false));
				NEW_REWARD_MO.setI(String.valueOf(false));

			}//if enable
		}// proc()
	
	}

	/**
	 * Prints the whole sequence stored in memory
	 */
	public void printSequence() {
		System.out.println("------------------------------------");
		try {
			JSONArray es = new JSONArray(EVENTS_SEQUENCE_MO.getI());

			for(int i=0; i<es.length();i++){
				JSONObject ev = es.getJSONObject(i);
				System.out.println(i+"(s,a,r)=("+ev.getInt(GlasSequenceElements.SENSED_STIMULUS.toString())+","+ev.getInt(GlasSequenceElements.EXPECTED_ACTION.toString())+","+ev.getInt(GlasSequenceElements.REWARD_RECEIVED.toString())+")");
			}

		} catch (JSONException e) {
		}
		System.out.println("------------------------------------");

	}

	/**
	 * @return the printEvents
	 */
	public boolean isPrintEvents() {
		return printEvents;
	}

	/**
	 * @param printEvents the printEvents to set
	 */
	public void setPrintEvents(boolean printEvents) {
		this.printEvents = printEvents;
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
	 * @return the EVENTS_SEQUENCE_MO
	 */
	public Memory getEVENTS_SEQUENCE_MO() {
		return EVENTS_SEQUENCE_MO;
	}

	/**
	 * @param eVENTS_SEQUENCE_MO the eVENTS_SEQUENCE_MO to set
	 */
	public void setEVENTS_SEQUENCE_MO(Memory eVENTS_SEQUENCE_MO) {
		EVENTS_SEQUENCE_MO = eVENTS_SEQUENCE_MO;
	}


}
