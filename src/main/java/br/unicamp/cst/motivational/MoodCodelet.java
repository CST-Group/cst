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
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

import java.util.ArrayList;
import java.util.List;

public abstract class MoodCodelet extends Codelet {

    public static final String INPUT_DRIVES_MEMORY = "INPUT_DRIVES_MEMORY";
    public static final String INPUT_APPRAISAL_MEMORY = "INPUT_APPRAISAL_MEMORY";
    public static final String INPUT_SENSORY_MEMORY = "INPUT_SENSORY_MEMORY";

    public static final String OUTPUT_MOOD_MEMORY = "OUTPUT_MOOD_MEMORY";

    private String id;
    private List<Memory> listOfDrivesMO;
    private Appraisal appraisal;
    private List<Object> sensors;
    private Mood outputMood;

    private Memory inputDrivesMemoryMO;
    private Memory inputAppraisalMO;
    private Memory inputSensoryMO;
    private Memory outputMoodMO;

    public MoodCodelet(String id){
        this.setId(id);
        this.setName(id);
        this.setListOfDrivesMO(new ArrayList<Memory>());
        this.setSensors(new ArrayList<Object>());
        this.setOutputMood(new Mood(getId(), "", 0d));
    }

    @Override
    public void accessMemoryObjects() {

        if(getInputDrivesMemoryMO() == null){
            setInputDrivesMemoryMO(getInput(INPUT_DRIVES_MEMORY, 0));
            setListOfDrivesMO((List<Memory>) getInputDrivesMemoryMO().getI());

        }

        if(getInputAppraisalMO() == null){
            setInputAppraisalMO(getInput(INPUT_APPRAISAL_MEMORY, 0));
            setAppraisal((Appraisal) ((Memory) getInputAppraisalMO().getI()).getI());
        }

        if(getInputSensoryMO() == null){
            setInputSensoryMO(getInput(INPUT_SENSORY_MEMORY, 0));
            setSensors((List<Object>) getInputSensoryMO().getI());
        }

        if(getOutputMoodMO() == null){
            setOutputMoodMO(getOutput(OUTPUT_MOOD_MEMORY, 0));
        }
    }

    @Override
    public synchronized void calculateActivation() {
        List<Drive> listOfDrive  = new ArrayList<>();

        getListOfDrivesMO().stream().forEach(memory -> {
            listOfDrive.add((Drive) memory.getI());
        });

        Mood mood = moodGeneration(listOfDrive, (Appraisal) ((Memory) getInputAppraisalMO().getI()).getI(), getSensors());
        mood.setName(getId());
        setOutputMood(mood);
    }

    @Override
    public synchronized void proc(){
        getOutputMoodMO().setI(getOutputMood());
    }


    public abstract Mood moodGeneration(List<Drive> listOfDrives, Appraisal appraisal, List<Object> sensors);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Memory> getListOfDrivesMO() {
        return listOfDrivesMO;
    }

    public void setListOfDrivesMO(List<Memory> listOfDrivesMO) {
        this.listOfDrivesMO = listOfDrivesMO;
    }

    public Appraisal getAppraisal() {
        return appraisal;
    }

    public void setAppraisal(Appraisal appraisal) {
        this.appraisal = appraisal;
    }

    public Memory getInputDrivesMemoryMO() {
        return inputDrivesMemoryMO;
    }

    public void setInputDrivesMemoryMO(Memory inputDrivesMemoryMO) {
        this.inputDrivesMemoryMO = inputDrivesMemoryMO;
    }

    public Memory getInputAppraisalMO() {
        return inputAppraisalMO;
    }

    public void setInputAppraisalMO(Memory inputAppraisalMO) {
        this.inputAppraisalMO = inputAppraisalMO;
    }

    public Memory getInputSensoryMO() {
        return inputSensoryMO;
    }

    public void setInputSensoryMO(Memory inputSensoryMO) {
        this.inputSensoryMO = inputSensoryMO;
    }

    public Memory getOutputMoodMO() {
        return outputMoodMO;
    }

    public void setOutputMoodMO(Memory outputMoodMO) {
        this.outputMoodMO = outputMoodMO;
    }


    public List<Object> getSensors() {
        return sensors;
    }

    public void setSensors(List<Object> sensors) {
        this.sensors = sensors;
    }

    public Mood getOutputMood() {
        return outputMood;
    }

    public void setOutputMood(Mood outputMood) {
        this.outputMood = outputMood;
    }
}
