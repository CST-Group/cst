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

import java.util.Calendar;
import java.util.List;


public abstract class TimedGoal extends Goal {

    private Calendar startDate;
    private Calendar expiryDate;
    private double diffenceOfDates = 0d;


    public TimedGoal(String name, int steps, int minSteps, double interventionThreshold, double priorityHighLevel, Calendar expiryDate) {
        super(name, steps, minSteps, interventionThreshold, priorityHighLevel);
        this.setExpiryDate(expiryDate);
    }

    public TimedGoal(String name, int steps, int minSteps, double interventionThreshold, double belowInterventionThreshold, double priorityHighLevel, Calendar expiryDate) {
        super(name, steps, minSteps, interventionThreshold, belowInterventionThreshold, priorityHighLevel);
        this.setExpiryDate(expiryDate);
    }

    public synchronized Calendar getExpiryDate() {
        return expiryDate;
    }

    public synchronized void setExpiryDate(Calendar expiryDate) {

        try {
            if (expiryDate == null) {
                throw new MotivationalException(MotivationalMessages.MSG_VAR_EXPIRY_TIME_NULL);
            } else
                this.expiryDate = expiryDate;
        } catch (MotivationalException me) {
            me.printStackTrace();
        }

    }

    public double calculateCompletedTime(){

        if(getStartDate() == null) {
            setStartDate(Calendar.getInstance());
            setDiffenceOfDates(getExpiryDate().getTimeInMillis() - getStartDate().getTimeInMillis());
        }

        return (getExpiryDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis())/ getDiffenceOfDates();

    }

    @Override
    public synchronized double calculateVote(List<Drive> listOfDrivesVote) {
        return calculateVoteByTime(listOfDrivesVote, this.calculateCompletedTime());
    }

    @Override
    public synchronized double calculateUrgentVote(List<Drive> listOfDrivesVote) {
        return calculateUrgentVoteByTime(listOfDrivesVote, this.calculateCompletedTime());
    }

    public abstract double calculateVoteByTime(List<Drive> listOfDriveVote, double timeActivation);

    public abstract double calculateUrgentVoteByTime(List<Drive> listOHighPriorityDrive, double timeActivation);

    @Override
    public synchronized void setDrivesVote(List<Drive> drivesVote) {

        this.drivesVote = drivesVote;

    }

    public synchronized Calendar getStartDate() {
        return startDate;
    }

    public synchronized void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public double getDiffenceOfDates() {
        return diffenceOfDates;
    }

    public void setDiffenceOfDates(double diffenceOfDates) {

        try {
            if(diffenceOfDates <= 0){
                throw new MotivationalException(MotivationalMessages.MSG_VAR_DIFFERENCE_BETWEEN_DATES_ZERO);
            }
            this.diffenceOfDates = diffenceOfDates;

        }catch (MotivationalException me){
            me.printStackTrace();
        }
    }
}

