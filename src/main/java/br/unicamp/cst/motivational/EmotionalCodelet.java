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

    private String id;
    private Drive affectedDrive;

    private Map<Memory, Double> inputDrives;
    private Memory inputDrivesMO;
    private Memory inputAffectedDriveMO;
    private Memory outputAffectedDriveMO;
    private Memory inputMoodMO;

    public EmotionalCodelet(String id) throws CodeletActivationBoundsException {
        this.setId(id);
        this.setName(id);
        this.setActivation(0.0d);
        setAffectedDrive(new Drive(id));
    }

    @Override
    public void accessMemoryObjects() {

        if (getInputDrivesMO() == null) {
            setInputDrivesMO(this.getInput(INPUT_DRIVES_MEMORY, 0));
            setInputDrives((HashMap<Memory, Double>) getInputDrivesMO().getI());
        }

        if (getInputAffectedDriveMO() == null) {
            setInputAffectedDriveMO(getInput(INPUT_AFFECTED_DRIVE_MEMORY, 0));
            setAffectedDrive((Drive) ((Memory)getInputAffectedDriveMO().getI()).getI());
        }

        if (getInputMoodMO() == null) {
            setInputMoodMO(this.getInput(INPUT_MOOD_MEMORY, 0));
        }

        if (getOutputAffectedDriveMO() == null) {
            setOutputAffectedDriveMO(this.getOutput(OUTPUT_AFFECTED_DRIVE_MEMORY, 0));
        }

    }

    public abstract double calculateEmotionalDistortion(List<Drive> listOfDrives, Mood mood);

    @Override
    public synchronized void calculateActivation() {

        double activation = 0d;
        List<Drive> listOfDrives = new ArrayList<Drive>();

        for (Map.Entry<Memory, Double> drive : getInputDrives().entrySet()) {
            listOfDrives.add((Drive) ((Memory)drive.getKey()).getI());
        }

        Mood mood = (Mood) ((Memory)getInputMoodMO().getI()).getI();

        if(mood != null) {
            activation = this.calculateEmotionalDistortion(listOfDrives, mood);
        } else{
            activation = 0;
        }

        setAffectedDrive((Drive) ((Memory)getInputAffectedDriveMO().getI()).getI());
        if(getAffectedDrive() != null)
            getAffectedDrive().setEmotionalDistortion(activation);
    }

    @Override
    public void proc() {
        if(getAffectedDrive() != null) {
            getOutputAffectedDriveMO().setI(getAffectedDrive());
            getOutputAffectedDriveMO().setEvaluation(getAffectedDrive().getEmotionalDistortion());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {

        try {
            if (id.equals("")) {
                throw new Exception(CSTMessages.MSG_VAR_EMOTIONAL_NAME_NULL);
            }

            this.id = id;

        } catch (Exception me) {
            me.printStackTrace();
        }

    }

    private synchronized Map<Memory, Double> getInputDrives() {
        return inputDrives;
    }

    private synchronized void setInputDrives(Map<Memory, Double> inputDrives) {

        try {
            if (inputDrives == null) {
                throw new Exception(CSTMessages.MSG_VAR_EMOTIONAL_DRIVE_VOTES);
            }

            for (Map.Entry<Memory, Double> drive : inputDrives.entrySet()) {
                if (drive.getValue() > 1 || drive.getValue() < 0) {
                    throw new Exception("Drive:" + drive.getKey().getName() + " " + CSTMessages.MSG_VAR_RELEVANCE);
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
