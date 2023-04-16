/***********************************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * K. Raizer, A. L. O. Paraense, E. M. Froes, R. R. Gudwin - initial API and implementation
 ***********************************************************************************************/
package br.unicamp.cst.representation.idea;

import java.util.List;

/**
 * This interface is used to represent a Habit, i.e., an executable action, that typically is used 
 * to create or modify an idea
 * @author rgudwin
 */
public interface Habit {
    /**
     * This method executes the habit
     * @param idea an optional parameter representing an idea that might be used by the habit to generate a new idea
     * @return the generated or modified idea
     */
    public List<Idea> op(Idea idea);
}
