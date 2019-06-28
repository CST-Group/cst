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

import br.unicamp.cst.core.entities.Memory;

import java.io.Serializable;

public class Drive implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private double activation;
    private double priority;
    private double level;
    private double urgencyThreshold;
    private double emotionalDistortion = 0;
    private boolean urgencyState = false;

    public Drive(String name){
        setName(name);
    }

    public Drive(String name, double activation, double priority, double level, double urgentThreshold){
        setName(name);
        setActivation(activation);
        setPriority(priority);
        setLevel(level);
        setUrgencyThreshold(urgentThreshold);
    }

    public Drive(String name, double activation, double priority, double level, double urgentThreshold, double emotionalDistortion){
        setName(name);
        setActivation(activation);
        setPriority(priority);
        setLevel(level);
        setUrgencyThreshold(urgentThreshold);
        setEmotionalDistortion(emotionalDistortion);
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized double getActivation() {
        return activation;
    }

    public synchronized void setActivation(double activation) {
        this.activation = activation;
    }

    public synchronized double getPriority() {
        return priority;
    }

    public synchronized void setPriority(double priority) {
        this.priority = priority;
    }

    public synchronized double getLevel() {
        return level;
    }

    public synchronized void setLevel(double level) {
        this.level = level;
    }

    public synchronized double getUrgencyThreshold() {
        return urgencyThreshold;
    }

    public synchronized void setUrgencyThreshold(double urgencyThreshold) {
        this.urgencyThreshold = urgencyThreshold;
    }

    public synchronized double getEmotionalDistortion() {
        return emotionalDistortion;
    }

    public synchronized void setEmotionalDistortion(double emotionalDistortion) {
        this.emotionalDistortion = emotionalDistortion;
    }

    @Override
    public String toString(){

        return"Drive [name="+getName()+", activation="+getActivation()+", urgency state="+isUrgencyState()+", priority="+getPriority()+", level="+getLevel()
                +", urgency threshold="+getUrgencyThreshold()+", emotional distortion="+getEmotionalDistortion()+"]";
    }

    public boolean isUrgencyState() {
        return urgencyState;
    }

    public void setUrgencyState(boolean urgencyState) {
        this.urgencyState = urgencyState;
    }
}
