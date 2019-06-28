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


public class Mood {

    private String name;
    private String emotionalState;
    private double value;

    public Mood(String name, double value){
        setName(name);
        setValue(value);
        setEmotionalState("");
    }

    public Mood(String name, String emotionalState, double value){
        setName(name);
        setValue(value);
        setEmotionalState(emotionalState);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getEmotionalState() {
        return emotionalState;
    }

    public void setEmotionalState(String emotionalState) {
        this.emotionalState = emotionalState;
    }

    @Override
    public String toString(){
        return "Mood [name:"+getName()+", value:"+getValue()+", emotionalState:"+getEmotionalState()+"]";
    }

}
