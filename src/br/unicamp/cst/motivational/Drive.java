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
    private double filter;
    private double activation;
    private double priority;
    private double level;
    private double urgentActivation;
    private double urgentActivationThreshold;
    private double lowerUrgentThreshold;

    public Drive(String name){
        setName(name);
    }

    public Drive(String name, double activation, double priority, double level, double urgentActivation, double urgentActivationThreshold, double lowerUrgentThreshold){
        setName(name);
        setActivation(activation);
        setPriority(priority);
        setLevel(level);
        setUrgentActivation(urgentActivation);
        setUrgentActivationThreshold(urgentActivationThreshold);
        setLowerUrgentThreshold(lowerUrgentThreshold);
        setFilter(1);
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

    public synchronized double getUrgentActivation() {
        return urgentActivation;
    }

    public synchronized void setUrgentActivation(double urgentActivation) {
        this.urgentActivation = urgentActivation;
    }

    public synchronized double getUrgentActivationThreshold() {
        return urgentActivationThreshold;
    }

    public synchronized void setUrgentActivationThreshold(double urgentActivationThreshold) {
        this.urgentActivationThreshold = urgentActivationThreshold;
    }

    public synchronized double getLowerUrgentThreshold() {
        return lowerUrgentThreshold;
    }

    public synchronized void setLowerUrgentThreshold(double lowerUrgentThreshold) {
        this.lowerUrgentThreshold = lowerUrgentThreshold;
    }

    public double getFilter() {
        return filter;
    }

    public void setFilter(double filter) {
        this.filter = filter;
    }
}
