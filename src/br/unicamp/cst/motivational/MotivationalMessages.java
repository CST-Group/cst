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

package br.unicamp.cst.motivational;

public class MotivationalMessages {

    //DRIVE MESSAGES.
    public static final String MSG_VAR_LEVEL_NULL = "Drive level is null.";
    public static final String MSG_VAR_NAME_NULL = "Drive name is null.";
    public static final String MSG_VAR_RELEVANCE_NULL = "Drive relevance must be less equal than 1 and greater equal than 0.";
    public static final String MSG_VAR_PRIORITY_NULL = "Drive priority is null.";


    //GOAL MESSAGES.
    public static final String MSG_VAR_GOAL_NAME_NULL = "Goal name is null";
    public static final String MSG_VAR_GOAL_DRIVE_VOTES = "Goal drive votes is null or empty.";
    public static final String MSG_VAR_GOAL_STEP_VALUE = "Goal step must be greater than 0.";
    public static final String MSG_VAR_GOAL_INTERVENTION_THRESHOLD = "Intervention threshold must be less equal than 1 and greater equal than 0.";


    //GOAL ARCHITECTURE MESSAGES.
    public static final String MSG_VAR_GOAL_ARC_GOALS_NULL = "List of goals are null or empty.";
    public static final String MSG_VAR_GOAL_ARC_DRIVES_NULL = "List of drives are null or empty.";

    //TIMED GOAL.
    public static final String MSG_VAR_EXPIRY_TIME_NULL = "Expiry time is null or zero.";
    public static final String MSG_VAR_DIFFERENCE_BETWEEN_DATES_ZERO = "Differece between dates is zero or less than zero.";

}
