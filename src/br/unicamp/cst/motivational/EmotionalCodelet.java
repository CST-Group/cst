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
import br.unicamp.cst.core.entities.Memory;
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
    public final static String INPUT_AFFECTED_DRIVE_MEMORY = "INPUT_AFFECTED_DRIVE_MEMORY";
    public final static String INPUT_MOOD_MEMORY = "INPUT_MOOD_MEMORY";
    public final static String OUTPUT_AFFECTED_DRIVE_MEMORY = "OUTPUT_AFFECTED_DRIVE_MEMORY";


    private String name;
    private Mood mood;
    private Drive affectedDrive;

    private Map<Drive, Double> inputDrives;
    private Memory inputDrivesMO;
    private Memory inputAffectedDriveMO;
    private Memory outputAffectedDriveMO;
    private Memory inputMoodMO;

    public EmotionalCodelet(String name) throws CodeletActivationBoundsException {
        this.setName(name);
        this.setActivation(0.0d);
        setAffectedDrive(new Drive(name));
    }

    @Override
    public void accessMemoryObjects(){

        if(getInputDrivesMO() == null) {
            setInputDrivesMO(this.getInput(INPUT_DRIVES_MEMORY, 0));
            this.setInputDrives((HashMap<Drive, Double>) getInputDrivesMO().getI());
        }

        if(getInputAffectedDriveMO() == null){
            setInputAffectedDriveMO(getInput(INPUT_AFFECTED_DRIVE_MEMORY, 0));
        }


        if(getInputMoodMO() == null){
            setInputMoodMO(this.getInput(INPUT_MOOD_MEMORY, 0));
            this.setMood((Mood) getInputDrivesMO().getI());
        }

        if(getOutputAffectedDriveMO() == null){
            setOutputAffectedDriveMO(this.getOutput(OUTPUT_AFFECTED_DRIVE_MEMORY, 0));
        }

    }

    public abstract double calculateEmotionalDistortion(List<Drive> listOfDrives, Mood mood);


    @Override
    public synchronized void calculateActivation() {

        synchronized(this){

            List<Drive> listOfDrives = new ArrayList<Drive>();

            for (Map.Entry<Drive, Double> drive : getInputDrives().entrySet()) {
                listOfDrives.add(drive.getKey());
            }

            double activation = this.calculateEmotionalDistortion(listOfDrives, getMood());

            try {
                this.setActivation(activation);
                getAffectedDrive().setActivation(activation);
            } catch (CodeletActivationBoundsException ex) {
                Logger.getLogger(EmotionalCodelet.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    @Override
    public void proc() {

        Drive emotion = getAffectedDrive();

        getOutputAffectedDriveMO().setI(emotion);
        getOutputAffectedDriveMO().setEvaluation(getActivation());

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        try {
            if (name.equals("")) {
                throw new Exception(CSTMessages.MSG_VAR_EMOTIONAL_NAME_NULL);
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
                throw new Exception(CSTMessages.MSG_VAR_EMOTIONAL_DRIVE_VOTES);
            } else if (inputDrives.size() == 0) {
                throw new Exception(CSTMessages.MSG_VAR_EMOTIONAL_DRIVE_VOTES);
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

    public Memory getInputDrivesMO() {
        return inputDrivesMO;
    }

    public void setInputDrivesMO(Memory inputDrivesMO) {
        this.inputDrivesMO = inputDrivesMO;
    }

    public Memory getOutputAffectedDriveMO() {
        return outputAffectedDriveMO;
    }

    public void setOutputAffectedDriveMO(Memory outputAffectedDriveMO) {
        this.outputAffectedDriveMO = outputAffectedDriveMO;
    }

    public Memory getInputMoodMO() {
        return inputMoodMO;
    }

    public void setInputMoodMO(Memory inputMoodMO) {
        this.inputMoodMO = inputMoodMO;
    }

    public Mood getMood() {
        return mood;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }

    public Drive getAffectedDrive() {
        return affectedDrive;
    }

    public void setAffectedDrive(Drive affectedDrive) {
        this.affectedDrive = affectedDrive;
    }

    public Memory getInputAffectedDriveMO() {
        return inputAffectedDriveMO;
    }

    public void setInputAffectedDriveMO(Memory inputAffectedDriveMO) {
        this.inputAffectedDriveMO = inputAffectedDriveMO;
    }
}
