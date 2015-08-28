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


/**
 * @author andre
 * 
 * * Generic Core Types of memory objects for every application
 *
 */
public enum MemoryObjectTypesCore implements MemoryObjectType
{
   ACTION_PROPOSITION,
   BEHAVIOR_PROPOSITION,
   WORLD_STATE,
   NOT_WORLD_STATE,
   SENSORY_INFO,
   ONCE_ONLY_GOAL,
   PROTECTED_GOAL,
   PERMANENT_GOAL,
   BEHAVIOR_STATE,
   TEMP_THETA,
   PROPOSITION,
   WORLD_STATE_LIST,
   SELF_STATE,
   INNER_SENSE,
   RESOURCE;
}
