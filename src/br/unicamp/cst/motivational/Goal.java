package br.unicamp.cst.motivational;

import br.unicamp.cst.representation.owrl.AbstractObject;

/**
 * Created by du on 19/12/16.
 */
public class Goal {

    private String id;
    private AbstractObject goalAbstractObjects;

    public Goal(String id, AbstractObject goalAbstractObjects){
        this.setId(id);
        this.setGoalAbstractObjects(goalAbstractObjects);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AbstractObject getGoalAbstractObjects() {
        return goalAbstractObjects;
    }

    public void setGoalAbstractObjects(AbstractObject goalAbstractObjects) {
        this.goalAbstractObjects = goalAbstractObjects;
    }
}
