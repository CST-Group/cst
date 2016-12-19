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

import java.util.List;

public abstract class MoodCodelet extends Codelet {

    public static String INPUT_DRIVES_MEMORY = "INPUT_DRIVES_MEMORY";
    public static String INPUT_APPRAISAL_MEMORY = "INPUT_APPRAISAL_MEMORY";
    public static String INPUT_EPISODIC_RECALL_MEMORY = "INPUT_EPISODIC_RECALL_MEMORY";
    public static String OUTPUT_MOOD_MEMORY = "OUTPUT_MOOD_MEMORY";


    private List<Drive> listOfDrives;
    private Appraisal appraisal;
    private EpisodicMemory episodicMemory;

    private Memory inputDrivesMemoryMO;
    private Memory appraisalMO;
    private Memory episodicRecallMO;
    private Memory outputMoodMO;

    @Override
    public void accessMemoryObjects() {

        if(getInputDrivesMemoryMO() != null){
            setInputDrivesMemoryMO(getInput(INPUT_DRIVES_MEMORY, 0));
            setListOfDrives((List<Drive>) getInputDrivesMemoryMO().getI());

        }

        if(getAppraisalMO() != null){
            setAppraisalMO(getInput(INPUT_APPRAISAL_MEMORY, 0));
            setAppraisal((Appraisal) getAppraisalMO().getI());
        }

        if(getEpisodicRecallMO() != null){
            setEpisodicRecallMO(getInput(INPUT_EPISODIC_RECALL_MEMORY, 0));
            setEpisodicMemory((EpisodicMemory) getEpisodicRecallMO().getI());
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


    public abstract Mood moodGenerate(List<Drive> listOfDrives, Appraisal appraisal, EpisodicMemory episodicMemory);

    @Override
    public void proc() {
        Mood mood = moodGenerate(getListOfDrives(), getAppraisal(), getEpisodicMemory());
        getOutputMoodMO().setI(mood);
    }

    public Memory getInputDrivesMemoryMO() {
        return inputDrivesMemoryMO;
    }

    public void setInputDrivesMemoryMO(Memory inputDrivesMemoryMO) {
        this.inputDrivesMemoryMO = inputDrivesMemoryMO;
    }

    public Memory getAppraisalMO() {
        return appraisalMO;
    }

    public void setAppraisalMO(Memory appraisalMO) {
        this.appraisalMO = appraisalMO;
    }

    public Memory getEpisodicRecallMO() {
        return episodicRecallMO;
    }

    public void setEpisodicRecallMO(Memory episodicRecallMO) {
        this.episodicRecallMO = episodicRecallMO;
    }

    public Memory getOutputMoodMO() {
        return outputMoodMO;
    }

    public void setOutputMoodMO(Memory outputMoodMO) {
        this.outputMoodMO = outputMoodMO;
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

    public EpisodicMemory getEpisodicMemory() {
        return episodicMemory;
    }

    public void setEpisodicMemory(EpisodicMemory episodicMemory) {
        this.episodicMemory = episodicMemory;
    }
}
