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
package br.unicamp.cst.motivational;

import br.unicamp.cst.representation.wme.Idea;

/**
 * Created by du on 19/12/16.
 */
public class Goal {

    private String id;
    private Idea goalAbstractObjects;

    public Goal(String id, Idea goalIdeas){
        this.setId(id);
        this.setGoalAbstractObjects(goalIdeas);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Idea getGoalAbstractObjects() {
        return goalAbstractObjects;
    }

    public void setGoalAbstractObjects(Idea goalAbstractObjects) {
        this.goalAbstractObjects = goalAbstractObjects;
    }
}
