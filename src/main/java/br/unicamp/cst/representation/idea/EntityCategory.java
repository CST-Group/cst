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
 *
 * @author rgudwin
 */
public class EntityCategory implements Category {
    private Idea template;
    
    public EntityCategory() {
        this(null);
    }
    
    public EntityCategory(Idea template) {
        this.template = template;
    }
    
    @Override
    public double membership(Idea idea) {
        if (template != null) {
             if (idea.equivalent(template)) return(1.0);
             else return(0.0);
        }
        return (1.0);
    }
    
    public Idea getInstance() {
        return(this.getInstance(null));
    }

    @Override
    public Idea getInstance(Idea constraints) {
        if (template != null)
           return (template.clone());
        else return(null);
    }
}
