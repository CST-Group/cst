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

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.CSTMessages;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class EmotionalCodelet extends Codelet {

    public final static String INPUT_DRIVES_MEMORY = "INPUT_DRIVES_MEMORY";
    public final static String OUTPUT_DRIVE_MEMORY = "OUTPUT_DRIVE_MEMORY";
    public final static String MOOD_MEMORY = "MOOD_MEMORY";

    private String name;
    private Mood mood;
    private Drive emotion;

    private Map<Drive, Double> inputDrives;
    private MemoryObject inputDrivesMO;
    private MemoryObject outputGoalMO;
    private MemoryObject moodMO;

    public EmotionalCodelet(String name) throws CodeletActivationBoundsException {
        this.setName(name);
        this.setActivation(0.0d);
        setEmotion(new Drive(name));
    }

    @Override
    public void accessMemoryObjects(){

        if(getInputDrivesMO() == null) {
            setInputDrivesMO((MemoryObject) this.getInput(INPUT_DRIVES_MEMORY, 0));
            this.setInputDrives((HashMap<Drive, Double>) getInputDrivesMO().getI());
        }

        if(getMoodMO() == null){
            setInputDrivesMO((MemoryObject) this.getInput(MOOD_MEMORY, 0));
            this.setMood((Mood) getInputDrivesMO().getI());
        }

        if(getOutputGoalMO() == null){
            setOutputGoalMO((MemoryObject) this.getOutput(OUTPUT_DRIVE_MEMORY, 0));
        }

    }

    public abstract double calculateMoodDistortion(List<Drive> listOfDrives, Mood mood);

    public abstract Drive generateEmotion(List<Drive> listOfDrives, Drive emotion);

    @Override
    public synchronized void calculateActivation() {

        synchronized(this){

            List<Drive> listOfDrives = new ArrayList<Drive>();

            for (Map.Entry<Drive, Double> drive : getInputDrives().entrySet()) {
                listOfDrives.add(drive.getKey());
            }

            double activation = this.calculateMoodDistortion(listOfDrives, getMood());

            try {
                this.setActivation(activation);
                getEmotion().setActivation(activation);
            } catch (CodeletActivationBoundsException ex) {
                Logger.getLogger(EmotionalCodelet.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    @Override
    public void proc() {

        List<Drive> listOfDrives = new ArrayList<Drive>();

        for (Map.Entry<Drive, Double> drive : getInputDrives().entrySet()) {
            listOfDrives.add(drive.getKey());
        }

        Drive emotion = generateEmotion(listOfDrives, getEmotion());
        getOutputGoalMO().setI(emotion);
        getOutputGoalMO().setEvaluation(getActivation());

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        try {
            if (name.equals("")) {
                throw new Exception(CSTMessages.MSG_VAR_GOAL_NAME_NULL);
            }

            this.name = name;

        } catch (Exception me) {
            me.printStackTrace();
        }

    }

    private synchronized Map<Drive, Double> getInputDrives() {
        return inputDrives;
    }

    private synchronized void setInputDrives(Map<Drive, Double> inputDrives) {

        try {
            if (inputDrives == null) {
                throw new Exception(CSTMessages.MSG_VAR_GOAL_DRIVE_VOTES);
            } else if (inputDrives.size() == 0) {
                throw new Exception(CSTMessages.MSG_VAR_GOAL_DRIVE_VOTES);
            }

            for (Map.Entry<Drive, Double> drive: inputDrives.entrySet()) {
                if(drive.getValue() > 1 || drive.getValue() < 0){
                    throw new Exception("Drive:"+drive.getKey().getName() +" "+ CSTMessages.MSG_VAR_RELEVANCE);
                }
            }

            this.inputDrives = inputDrives;

        } catch (Exception me) {
            me.printStackTrace();
        }

    }

    public MemoryObject getInputDrivesMO() {
        return inputDrivesMO;
    }

    public void setInputDrivesMO(MemoryObject inputDrivesMO) {
        this.inputDrivesMO = inputDrivesMO;
    }

    public MemoryObject getOutputGoalMO() {
        return outputGoalMO;
    }

    public void setOutputGoalMO(MemoryObject outputGoalMO) {
        this.outputGoalMO = outputGoalMO;
    }

    public MemoryObject getMoodMO() {
        return moodMO;
    }

    public void setMoodMO(MemoryObject moodMO) {
        this.moodMO = moodMO;
    }

    public Mood getMood() {
        return mood;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }

    public Drive getEmotion() {
        return emotion;
    }

    public void setEmotion(Drive emotion) {
        this.emotion = emotion;
    }
}
