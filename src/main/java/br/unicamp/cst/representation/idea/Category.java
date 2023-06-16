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

/**
 * This interface is used to represent a Category, i.e. a set that is subject to a membership law
 * indicated by the membership method. The instantiation method provides a random instance for
 * the category, subject to a set of constraints, when available.
 * @author rgudwin
 */
public interface Category {
    /**
     * The method getInstance provides a random instance of this category, subject to
 a list of constraints, when available
     * @param constraints a list of constraints for the new instance, in the form of Ideas
     * @return the new Idea that was generated
     */
    public Idea getInstance(Idea constraints );
    /**
     * This method tests if a provided Idea is a member of this category. 
     * @param idea the idea under consideration for membership
     * @return the membership value, a value among 0 and 1. 
     */
    public double membership(Idea idea);
}
