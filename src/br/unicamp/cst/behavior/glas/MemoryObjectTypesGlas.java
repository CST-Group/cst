/**
 * 
 */
package br.unicamp.cst.behavior.glas;

import br.unicamp.cst.core.entities.MemoryObjectType;

/**
 * @author klaus
 *
 */
public enum MemoryObjectTypesGlas implements MemoryObjectType{
	EVENTS_SEQUENCE,
	STIMULUS, 
	REWARD, 
	ACTION,
	SOLUTION_TREE, 
	NEW_EVENT_DETECTED, NEW_STIM_REWARD_PRESENTED,
	NEW_STIM,
	NEW_ACTION,
	NEW_REWARD, ACTION_SENT

}
