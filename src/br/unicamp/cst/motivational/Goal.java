/*******************************************************************************
 * Copyright (c) 2016  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     E. M. Fróes, R. R. Gudwin - initial API and implementation
 ******************************************************************************/


package br.unicamp.cst.motivational;

import br.unicamp.cst.behavior.subsumption.SubsumptionAction;
import br.unicamp.cst.behavior.subsumption.SubsumptionBehaviourLayer;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import java.util.Collections;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class Goal extends Codelet {

    private String name;
    private double interventionThreshold;
    private double belowInterventionThreshold;
    private int steps;
    private int minSteps;
    private int executedSteps;
    private SubsumptionBehaviourLayer subsumptionBehaviourLayer;
    private List<Drive> drivesVote;
    private boolean currentGoal = false;
    private boolean bLock = true;
    private boolean bPause = false;
    private boolean urgentIntervention = false;

    public Goal(String name, List<Drive> drivesVote, int steps, int minSteps, double interventionThreshold) {
        this.setName(name);
        this.setDrivesVote(Collections.synchronizedList(drivesVote));
        this.setSteps(steps);
        this.setExecutedSteps(0);
        this.setCurrentGoal(false);
        this.setUrgentIntervention(false);
        this.setbPause(false);
        this.setbLock(true);
        this.setInterventionThreshold(interventionThreshold);
        this.setBelowInterventionThreshold(interventionThreshold);
        this.setSubsumptionBehaviourLayer(new SubsumptionBehaviourLayer());
        this.setMinSteps(minSteps);

    }

    public Goal(String name, List<Drive> drivesVote, int steps, int minSteps, double interventionThreshold, double belowInterventionThreshold) {
        this.setName(name);
        this.setDrivesVote(Collections.synchronizedList(drivesVote));
        this.setSteps(steps);
        this.setExecutedSteps(0);
        this.setCurrentGoal(false);
        this.setUrgentIntervention(false);
        this.setbPause(false);
        this.setbLock(true);
        this.setInterventionThreshold(interventionThreshold);
        this.setBelowInterventionThreshold(belowInterventionThreshold);
        this.setSubsumptionBehaviourLayer(new SubsumptionBehaviourLayer());
        this.setMinSteps(minSteps);
    }
    
    public abstract double calculateVote(List<Drive> listOfDrivesVote);

    public abstract double calculateUrgentVote(List<Drive> listOHighPriorityDrive);

    public abstract void executeActions();
    
    
    public synchronized void processVote() {
        synchronized(this){
            
            double activation = this.calculateVote(this.getDrivesVote());
            activation = activation == Double.NaN? 0:activation;
            
            try {
                this.setActivation(activation);
            } catch (CodeletActivationBoundsException ex) {
                Logger.getLogger(Goal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public synchronized boolean isFinishedGoalActions() {
        return getSteps() == getExecutedSteps() ? true : false;
    }

    public void addSubsumptionAction(SubsumptionAction subsumptionAction, Codelet codelet) {
        getSubsumptionBehaviourLayer().attachActionToSensor(subsumptionAction, codelet);
        getSubsumptionBehaviourLayer().addAction(subsumptionAction);
    }

    public synchronized void startGoalActions() {
        setExecutedSteps(0);
        setbLock(false);
    }

    public synchronized void stopGoalActions() {
        setbLock(true);
        setUrgentIntervention(false);
        setbPause(false);
    }

    public synchronized void pauseGoalActions() {
        setbLock(true);
        setUrgentIntervention(false);
        setbPause(true);
    }
    
    public synchronized void resumeGoalActions(){
        setbLock(true);
        setUrgentIntervention(false);
        setbPause(false);
    }
    

    public synchronized void urgentIntervention() {

        synchronized (this) {
            if (getInterventionThreshold() != 0.0d) {
                List<Drive> listOHighPriorityDrive = getDrivesVote().stream().filter(d -> d.getPriority().equals(Priority.HIGH_PRIORITY)).collect(Collectors.toList());

                if (calculateUrgentVote(listOHighPriorityDrive) >= getInterventionThreshold()) {
                    setUrgentIntervention(true);
                } else {
                    setUrgentIntervention(false);
                }
            } else {
                setUrgentIntervention(false);
            }
        }
    }

    public synchronized void isFinishedUrgentIntervention() {
        synchronized (this) {
            if (getInterventionThreshold() != 0.0d) {
                List<Drive> listOHighPriorityDrive = getDrivesVote().stream().filter(d -> d.getPriority().equals(Priority.HIGH_PRIORITY)).collect(Collectors.toList());

                
                double urgentVote = calculateUrgentVote(listOHighPriorityDrive);
                
                if (urgentVote >= getBelowInterventionThreshold()
                        || getExecutedSteps() <= getMinSteps()) {
                    setUrgentIntervention(true);
                    try {
                        setActivation(urgentVote);
                    } catch (CodeletActivationBoundsException ex) {
                        Logger.getLogger(Goal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    setUrgentIntervention(false);
                }
            } else {
                setUrgentIntervention(false);
            }
        }
    }

    @Override
    public synchronized void calculateActivation() {
      
    }

    @Override
    public void proc() {

        while (isbLock());

        if (!isFinishedGoalActions()) {
            executeActions();
            setExecutedSteps(getExecutedSteps() + 1);

            if (isbPause()) {
                setbPause(false);
            }

        } else {
            stopGoalActions();
        }

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

    public synchronized int getSteps() {
        return steps;
    }

    public synchronized void setSteps(int steps) {

        try {
            if (steps <= 0) {
                throw new MotivationalException(MotivationalMessages.MSG_VAR_GOAL_STEP_VALUE);
            }

            this.steps = steps;
        } catch (MotivationalException me) {
            me.printStackTrace();
        }
    }

    public synchronized List<Drive> getDrivesVote() {
        return drivesVote;
    }

    public synchronized void setDrivesVote(List<Drive> drivesVote) {

        try {
            if (drivesVote == null) {
                throw new MotivationalException(MotivationalMessages.MSG_VAR_GOAL_DRIVE_VOTES);
            } else if (drivesVote.size() == 0) {
                throw new MotivationalException(MotivationalMessages.MSG_VAR_GOAL_DRIVE_VOTES);
            }

            this.drivesVote = drivesVote;
        } catch (MotivationalException me) {
            me.printStackTrace();
        }

    }

    public boolean isCurrentGoal() {
        return currentGoal;
    }

    public void setCurrentGoal(boolean currentGoal) {
        this.currentGoal = currentGoal;
    }

    public boolean isUrgentIntervention() {
        return urgentIntervention;
    }

    public void setUrgentIntervention(boolean urgentIntervention) {
        this.urgentIntervention = urgentIntervention;
    }

    public double getInterventionThreshold() {
        return interventionThreshold;
    }

    public void setInterventionThreshold(double interventionThreshold) {

        try {
            if (interventionThreshold < 0 && interventionThreshold > 1) {
                throw new MotivationalException(MotivationalMessages.MSG_VAR_GOAL_INTERVENTION_THRESHOLD);
            } else {
                this.interventionThreshold = interventionThreshold;
            }
        } catch (MotivationalException me) {
            me.printStackTrace();
        }
    }

    public synchronized int getExecutedSteps() {
        return executedSteps;
    }

    public synchronized void setExecutedSteps(int executedSteps) {
        this.executedSteps = executedSteps;
    }

    public SubsumptionBehaviourLayer getSubsumptionBehaviourLayer() {
        return subsumptionBehaviourLayer;
    }

    public void setSubsumptionBehaviourLayer(SubsumptionBehaviourLayer subsumptionBehaviourLayer) {
        this.subsumptionBehaviourLayer = subsumptionBehaviourLayer;
    }

    public synchronized boolean isbLock() {
        return bLock;
    }

    public synchronized void setbLock(boolean bLock) {
        this.bLock = bLock;
    }

    public double getBelowInterventionThreshold() {
        return belowInterventionThreshold;
    }

    public void setBelowInterventionThreshold(double belowInterventionThreshold) {
        this.belowInterventionThreshold = belowInterventionThreshold;
    }

    public int getMinSteps() {
        return minSteps;
    }

    public void setMinSteps(int minSteps) {
        this.minSteps = minSteps;
    }

    public boolean isbPause() {
        return bPause;
    }

    public void setbPause(boolean bPause) {
        this.bPause = bPause;
    }
}
