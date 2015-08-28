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
