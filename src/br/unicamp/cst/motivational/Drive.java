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

public class Drive {

    private String name;
    private double activation;
    private double priority;
    private double level;
    private double urgencyThreshold;
    private double emotionalDistortion = 0;

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

    public double getUrgencyThreshold() {
        return urgencyThreshold;
    }

    public void setUrgencyThreshold(double urgencyThreshold) {
        this.urgencyThreshold = urgencyThreshold;
    }

    public double getEmotionalDistortion() {
        return emotionalDistortion;
    }

    public void setEmotionalDistortion(double emotionalDistortion) {
        this.emotionalDistortion = emotionalDistortion;
    }
}
