package br.unicamp.cst.motivational;

import br.unicamp.cst.representation.owrl.Configuration;

/**
 * Created by du on 19/12/16.
 */
public class Goal {

    private String id;
    private Configuration goalConfiguration;

    public Goal(String id, Configuration goalConfiguration){
        this.setId(id);
        this.setGoalConfiguration(goalConfiguration);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Configuration getGoalConfiguration() {
        return goalConfiguration;
    }

    public void setGoalConfiguration(Configuration goalConfiguration) {
        this.goalConfiguration = goalConfiguration;
    }
}
