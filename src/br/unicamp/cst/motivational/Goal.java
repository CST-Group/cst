package br.unicamp.cst.motivational;

import br.unicamp.cst.representation.owrl.Configuration;

/**
 * Created by du on 19/12/16.
 */
public class Goal {

    private String name;
    private Configuration goalConfiguration;

    public Goal(String name, Configuration goalConfiguration){
        this.setName(name);
        this.setGoalConfiguration(goalConfiguration);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Configuration getGoalConfiguration() {
        return goalConfiguration;
    }

    public void setGoalConfiguration(Configuration goalConfiguration) {
        this.goalConfiguration = goalConfiguration;
    }
}
