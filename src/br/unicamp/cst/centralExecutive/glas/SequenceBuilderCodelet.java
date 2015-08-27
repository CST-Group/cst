/**
 * 
 */
package br.unicamp.cst.centralExecutive.glas;

import java.sql.Timestamp;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
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
	private MemoryObject STIMULUS_MO;
	private MemoryObject ACTION_MO;
	private MemoryObject PREVIOUS_REWARD_MO; //Reward for i=1 a,s pair
	private boolean first_run=true;
	private Timestamp previous_stimulus_time_stamp;
	private JSONArray sequence = new JSONArray();
	private MemoryObject EVENTS_SEQUENCE_MO;
	private int sensed_stimulus;
	private int expected_action;
	private double reward_received;
	private boolean printEvents=false;
	private MemoryObject NEW_EVENT_DETECTED_MO;

	private MemoryObject NEW_STIM_MO;
	private MemoryObject NEW_ACTION_MO;
	private MemoryObject NEW_REWARD_MO;
	
	private RawMemory rawMemory;

	public SequenceBuilderCodelet(RawMemory rawMemory,WorkingStorage ws)
	{
		this.rawMemory = rawMemory;
		
		if(ws!=null)
		{
			ws.registerCodelet(this,MemoryObjectTypesGlas.SOLUTION_TREE, 0);
			ws.registerCodelet(this,MemoryObjectTypesGlas.STIMULUS, 0);
			ws.registerCodelet(this,MemoryObjectTypesGlas.ACTION, 0);
			ws.registerCodelet(this,MemoryObjectTypesGlas.REWARD, 0);
		}

		if(rawMemory!=null)
			EVENTS_SEQUENCE_MO=rawMemory.createMemoryObject(MemoryObjectTypesGlas.EVENTS_SEQUENCE, "");
		this.pushOutput(EVENTS_SEQUENCE_MO);
		if(ws!=null)
			ws.putMemoryObject(EVENTS_SEQUENCE_MO);

		if(rawMemory!=null)
			NEW_EVENT_DETECTED_MO=rawMemory.createMemoryObject(MemoryObjectTypesGlas.NEW_EVENT_DETECTED, "FALSE");
		this.pushOutput(NEW_EVENT_DETECTED_MO);
		if(ws!=null)
			ws.putMemoryObject(NEW_EVENT_DETECTED_MO);


		if(rawMemory!=null)
		{
			NEW_STIM_MO = rawMemory.createMemoryObject(MemoryObjectTypesGlas.NEW_STIM, String.valueOf(false));
			NEW_ACTION_MO = rawMemory.createMemoryObject(MemoryObjectTypesGlas.NEW_ACTION, String.valueOf(false));
			NEW_REWARD_MO = rawMemory.createMemoryObject(MemoryObjectTypesGlas.NEW_REWARD, String.valueOf(false));

		}
		
		this.pushOutput(NEW_STIM_MO);
		this.pushOutput(NEW_ACTION_MO);
		this.pushOutput(NEW_REWARD_MO);

		if(ws!=null)
		{
			ws.putMemoryObject(NEW_STIM_MO);
			ws.putMemoryObject(NEW_ACTION_MO);
			ws.putMemoryObject(NEW_REWARD_MO);
		}	

		if(rawMemory!=null)
		{
			STIMULUS_MO = rawMemory.createMemoryObject(MemoryObjectTypesGlas.STIMULUS, "");
			PREVIOUS_REWARD_MO = rawMemory.createMemoryObject(MemoryObjectTypesGlas.REWARD, ""); //reward for i-1 s,a pair
		}
		
		this.pushInput(STIMULUS_MO);
		this.pushInput(PREVIOUS_REWARD_MO);
		if(ws!=null)
		{
			ws.putMemoryObject(STIMULUS_MO);
			ws.putMemoryObject(PREVIOUS_REWARD_MO);
		}
		

	}

	@Override
	public void accessMemoryObjects() {
		//		STIMULUS_MO=this.getInput(MemoryObjectTypesGlas.STIMULUS, 0);
		ACTION_MO=this.getInput(MemoryObjectTypesGlas.ACTION, 0);
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


			boolean new_stim=(NEW_STIM_MO.getInfo().equals(String.valueOf(true)));
			boolean new_action=(NEW_ACTION_MO.getInfo().equals(String.valueOf(true)));
			boolean new_reward=(NEW_REWARD_MO.getInfo().equals(String.valueOf(true)));

			if(new_stim && new_action && new_reward){ //OK to snapshot an event!


				try{
					sensed_stimulus=Integer.valueOf(STIMULUS_MO.getInfo());
				}catch(NumberFormatException e){
					sensed_stimulus=0;
				}

				try{
					expected_action=Integer.valueOf(ACTION_MO.getInfo());
				}catch(NumberFormatException e){
					expected_action=0;
				}

				try{
					reward_received=Double.valueOf(PREVIOUS_REWARD_MO.getInfo());
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

				EVENTS_SEQUENCE_MO.updateInfo(sequence.toString());

				//----------

				NEW_STIM_MO.updateInfo(String.valueOf(false));
				NEW_ACTION_MO.updateInfo(String.valueOf(false));
				NEW_REWARD_MO.updateInfo(String.valueOf(false));

			}//if enable
		}// proc()
	
	}

	/**
	 * Prints the whole sequence stored in memory
	 */
	public void printSequence() {
		System.out.println("------------------------------------");
		try {
			JSONArray es = new JSONArray(EVENTS_SEQUENCE_MO.getInfo());

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
	public MemoryObject getEVENTS_SEQUENCE_MO() {
		return EVENTS_SEQUENCE_MO;
	}

	/**
	 * @param eVENTS_SEQUENCE_MO the eVENTS_SEQUENCE_MO to set
	 */
	public void setEVENTS_SEQUENCE_MO(MemoryObject eVENTS_SEQUENCE_MO) {
		EVENTS_SEQUENCE_MO = eVENTS_SEQUENCE_MO;
	}


}
