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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

public abstract class EmotionalCodelet extends Codelet {

    public final static String DRIVES_VOTE_MEMORY = "DRIVES_VOTE_MEMORY";
    public final static String OUTPUT_GOAL_MEMORY = "OUTPUT_GOAL_MEMORY";

    private String name;
    private double urgentActivationThreshold;
    private double lowerUrgentThreshold;
    private double priorityHighLevel = 0d;
    private double urgentActivation = 0d;

    private Map<MemoryObject, Double> drivesVote;
    private MemoryObject drivesVoteMO;
    private MemoryObject outputGoalMO;

    public EmotionalCodelet(String name, double urgentInterventionThreshold, double priorityHighLevel) {
        this.setName(name);
        this.setUrgentActivationThreshold(urgentInterventionThreshold);
        this.setLowerUrgentThreshold(urgentInterventionThreshold);
        this.setPriorityHighLevel(priorityHighLevel);

    }

    public EmotionalCodelet(String name, double urgentInterventionThreshold, double belowInterventionThreshold, double priorityHighLevel) {
        this.setName(name);
        this.setUrgentActivationThreshold(urgentInterventionThreshold);
        this.setLowerUrgentThreshold(belowInterventionThreshold);
        this.setPriorityHighLevel(priorityHighLevel);
    }

    @Override
    public void accessMemoryObjects(){

        if(getDrivesVoteMO() == null) {
            setDrivesVoteMO((MemoryObject) this.getInput(DRIVES_VOTE_MEMORY, 0));
            this.setDrivesVote((HashMap<MemoryObject, Double>)getDrivesVoteMO().getI());
        }

        if(getOutputGoalMO() == null){
            setOutputGoalMO((MemoryObject) this.getInput(OUTPUT_GOAL_MEMORY, 0));
        }

    }

    public abstract double calculateActivation(List<MemoryObject> listOfDrivesVote);

    public abstract double calculateUrgentActivation(List<MemoryObject> listOfHighPriorityDrive);

    @Override
    public synchronized void calculateActivation() {

        synchronized(this){

            double urgent = this.calculateUrgentActivation(getHighPriorityDrives());
            double activation = this.calculateActivation(getDrivesWithRelevance());

            activation = activation == Double.NaN? 0:activation;
            urgent = urgent == Double.NaN? 0:urgent;

            try {
                this.setActivation(activation);
                this.setUrgentActivation(urgent);
            } catch (CodeletActivationBoundsException ex) {
                Logger.getLogger(EmotionalCodelet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private List<MemoryObject> getDrivesWithRelevance(){

        List<MemoryObject> drives = new ArrayList<>();

        for (Map.Entry<MemoryObject, Double> drive : getDrivesVote().entrySet()) {

            Map<String, Object> driveParameters = (Map<String, Object>) drive.getKey().getI();

            drive.getKey().setEvaluation(drive.getKey().getEvaluation() * (Double)driveParameters.get("relevance"));
            drives.add(drive.getKey());

        }

        return drives;
    }

    private List<MemoryObject> getHighPriorityDrives(){

        List<MemoryObject> drives = new ArrayList<>();

        for (Map.Entry<MemoryObject, Double> drive : getDrivesVote().entrySet()) {

            Map<String, Object> driveParameters = (Map<String, Object>) drive.getKey().getI();

            if((Double)driveParameters.get("priorityValue") >= getPriorityHighLevel())
            {
                drive.getKey().setEvaluation(drive.getKey().getEvaluation() * (Double)driveParameters.get("relevance"));
                drives.add(drive.getKey());
            }
        }

        return drives;
    }

    @Override
    public void proc() {

        Map<String, Object> goal = new HashMap<>();

        goal.put("name", getName());
        goal.put("activation", getActivation());
        goal.put("urgentActivation", getUrgentActivation());
        goal.put("urgentActivationThreshold", getUrgentActivationThreshold());
        goal.put("lowerUrgentThreshold", getLowerUrgentThreshold());

        getOutputGoalMO().setI(goal);
        getOutputGoalMO().setEvaluation(getActivation());

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        try {
            if (name.equals("")) {
                throw new MotivationalException(MotivationalMessages.MSG_VAR_GOAL_NAME_NULL);
            }

            this.name = name;

        } catch (MotivationalException me) {
            me.printStackTrace();
        }

    }

    private synchronized Map<MemoryObject, Double> getDrivesVote() {
        return drivesVote;
    }

    private synchronized void setDrivesVote(Map<MemoryObject, Double> drivesVote) {

        try {
            if (drivesVote == null) {
                throw new MotivationalException(MotivationalMessages.MSG_VAR_GOAL_DRIVE_VOTES);
            } else if (drivesVote.size() == 0) {
                throw new MotivationalException(MotivationalMessages.MSG_VAR_GOAL_DRIVE_VOTES);
            }

            for (Map.Entry<MemoryObject, Double> drive: drivesVote.entrySet()) {
                if(drive.getValue() > 1 || drive.getValue() < 0){
                    throw new MotivationalException("Drive:"+drive.getKey().getName() +" "+ MotivationalMessages.MSG_VAR_RELEVANCE);
                }
            }

            this.drivesVote = drivesVote;
        } catch (MotivationalException me) {
            me.printStackTrace();
        }

    }

    public double getUrgentActivationThreshold() {
        return urgentActivationThreshold;
    }

    private void setUrgentActivationThreshold(double urgentActivationThreshold) {

        try {
            if (urgentActivationThreshold < 0 && urgentActivationThreshold > 1) {
                throw new MotivationalException(MotivationalMessages.MSG_VAR_GOAL_INTERVENTION_THRESHOLD);
            } else {
                this.urgentActivationThreshold = urgentActivationThreshold;
            }
        } catch (MotivationalException me) {
            me.printStackTrace();
        }
    }

    public double getLowerUrgentThreshold() {
        return lowerUrgentThreshold;
    }

    public void setLowerUrgentThreshold(double lowerUrgentThreshold) {
        this.lowerUrgentThreshold = lowerUrgentThreshold;
    }

    public double getPriorityHighLevel() {
        return priorityHighLevel;
    }

    public void setPriorityHighLevel(double priorityHighLevel) {
        this.priorityHighLevel = priorityHighLevel;
    }

    public MemoryObject getDrivesVoteMO() {
        return drivesVoteMO;
    }

    public void setDrivesVoteMO(MemoryObject drivesVoteMO) {
        this.drivesVoteMO = drivesVoteMO;
    }

    public double getUrgentActivation() {
        return urgentActivation;
    }

    public void setUrgentActivation(double urgentActivation) {
        this.urgentActivation = urgentActivation;
    }

    public MemoryObject getOutputGoalMO() {
        return outputGoalMO;
    }

    public void setOutputGoalMO(MemoryObject outputGoalMO) {
        this.outputGoalMO = outputGoalMO;
    }
}
