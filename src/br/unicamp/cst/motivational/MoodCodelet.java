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
import br.unicamp.cst.memory.EpisodicMemory;

import java.util.ArrayList;
import java.util.List;

public abstract class MoodCodelet extends Codelet {

    public static final String INPUT_DRIVES_MEMORY = "INPUT_DRIVES_MEMORY";
    public static final String INPUT_APPRAISAL_MEMORY = "INPUT_APPRAISAL_MEMORY";
    public static final String INPUT_SENSORY_MEMORY = "INPUT_SENSORY_MEMORY";

    public static final String OUTPUT_MOOD_MEMORY = "OUTPUT_MOOD_MEMORY";

    private String id;
    private List<Drive> listOfDrives;
    private Appraisal appraisal;
    private EpisodicMemory episodicRecallMemory;

    private Memory inputDrivesMemoryMO;
    private Memory inputAppraisalMO;
    private Memory inputSensoryMO;
    private Memory outputMoodMO;

    public MoodCodelet(String id){
        this.setId(id);
        this.setListOfDrives(new ArrayList<Drive>());
    }

    @Override
    public void accessMemoryObjects() {

        if(getInputDrivesMemoryMO() != null){
            setInputDrivesMemoryMO(getInput(INPUT_DRIVES_MEMORY, 0));
            setListOfDrives((List<Drive>) getInputDrivesMemoryMO().getI());

        }

        if(getInputAppraisalMO() != null){
            setInputAppraisalMO(getInput(INPUT_APPRAISAL_MEMORY, 0));
            setAppraisal((Appraisal) getInputAppraisalMO().getI());
        }

        if(getInputSensoryMO() != null){
            setInputSensoryMO(getInput(INPUT_SENSORY_MEMORY, 0));
            setEpisodicRecallMemory((EpisodicMemory) getInputSensoryMO().getI());
        }

        if(getOutputMoodMO() != null){
            setOutputMoodMO(getOutput(OUTPUT_MOOD_MEMORY, 0));
        }
    }

    @Override
    public void calculateActivation() {
        try {
            setActivation(0);
        } catch (CodeletActivationBoundsException e) {
            e.printStackTrace();
        }
    }


    public abstract Mood moodGeneration(List<Drive> listOfDrives, Appraisal appraisal, EpisodicMemory episodicMemory);

    @Override
    public void proc() {
        Mood mood = moodGeneration(getListOfDrives(), getAppraisal(), getEpisodicRecallMemory());
        mood.setName(getId());
        getOutputMoodMO().setI(mood);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Drive> getListOfDrives() {
        return listOfDrives;
    }

    public void setListOfDrives(List<Drive> listOfDrives) {
        this.listOfDrives = listOfDrives;
    }

    public Appraisal getAppraisal() {
        return appraisal;
    }

    public void setAppraisal(Appraisal appraisal) {
        this.appraisal = appraisal;
    }

    public EpisodicMemory getEpisodicRecallMemory() {
        return episodicRecallMemory;
    }

    public void setEpisodicRecallMemory(EpisodicMemory episodicRecallMemory) {
        this.episodicRecallMemory = episodicRecallMemory;
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






}
