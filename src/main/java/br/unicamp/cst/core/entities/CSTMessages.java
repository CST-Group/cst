/*******************************************************************************
 * Copyright (c) 2016  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     E. M. Froes, R. R. Gudwin - initial API and implementation
 ******************************************************************************/

package br.unicamp.cst.core.entities;

/**
 * This class represents CST's message inside the emotional system, consisting
 * of drive messages, goal messages and affordance messages.
 * 
 * @author E. M. Froes
 *
 */
public class CSTMessages {

	// DRIVE MESSAGES.
	public static final String MSG_VAR_NAME_NULL = "Drive name is null.";
	public static final String MSG_VAR_RELEVANCE = "Drive relevance must be less equal than 1 and greater equal than 0.";
	public static final String MSG_VAR_PRIORITY_NULL = "Drive priority is null.";
	public static final String MSG_VAR_URGENT_ACTIVATION_RANGE = "Drive urgent activation must be less equal than 1 and greater equal than 0.";
	public static final String MSG_VAR_URGENT_ACTIVATION_THRESHOLD_RANGE = "Drive urgent activation threshold must be less equal than 1 and greater equal than 0.";
	public static final String MSG_VAR_LOWER_URGENT_ACTIVATION_THRESHOLD_RANGE = "Drive lower urgent activation threshold must be less equal than 1 and greater equal than 0.";
	public static final String MSG_VAR_HIGH_PRIORITY = "Drive high priority must be less equal than 1 and greater equal than 0.";

	// GOAL MESSAGES.
	public static final String MSG_VAR_EMOTIONAL_NAME_NULL = "EmotionalCodelet name is null";
	public static final String MSG_VAR_EMOTIONAL_DRIVE_VOTES = "EmotionalCodelet drive votes is null or empty.";
	public static final String MSG_VAR_EMOTIONAL_INTERVENTION_THRESHOLD = "Intervention threshold must be less equal than 1 and greater equal than 0.";

	// AFFORDANCE MESSAGES.
	public static final String MSG_VAR_DETECTOR_OBJECT = "Detector object is null.";
	public static final String MSG_VAR_APPLY_OBJECT = "Apply object is null.";

}
