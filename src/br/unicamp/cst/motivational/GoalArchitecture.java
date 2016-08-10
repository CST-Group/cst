/*******************************************************************************
 * Copyright (c) 2016  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * E. M. Fr�es, R. R. Gudwin - initial API and implementation
 ******************************************************************************/

package br.unicamp.cst.motivational;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The <b><i>GoalArchitecture</i></b> class, together with the <b><i>Goal</i></b> class and the 
 * <b><i>Drive</i></b> class are the most important classes in motivational system in the CST toolkit.  
 *
 * The Motivational System was inspired in papers and tutorials published by professor Ron Sun that describe functions and applicabilities of "Motivational Subsystem" 
 * in the Clarion Cognitive Architecture context, and along with Codelets and Subsumption concepts presents in the CST Toolkit.
 * According to Ron Sun et. al, "drives" and "goals" are implicit and explicit representations which is contained in a cognitive agent to meet your needs and your inner desires. 
 * Thus, the drives are implicit and primary representations to which are essential to define the explicit representations, which in this case are the goals. 
 * Once the goal established, the agent can performing differents kind of behaviors to achieve this goal [1][2].
 *
 * [1] - [Ron Sun, 2003] A Tutorial on Clarion 5.0 - http://www.sts.rpi.edu/~rsun/sun.tutorial.pdf
 * [2] - [Ron Sun, 2005] The Motivational and Metacognitive Control in CLARION - http://www.sts.rpi.edu/~rsun/folder-files/sun-wgbook2007.pdf
 *
 * @see Goal
 * @see Drive
 *
 * @author eduardofroes
 */

public class GoalArchitecture extends Codelet {

    public final static String DRIVES_MEMORY = "DRIVES_MEMORY";
    public final static String GOALS_MEMORY = "GOALS_MEMORY";

    private MemoryObject drivesMO;
    private MemoryObject goalsMO;

    private List<Drive> drives;
    private List<Goal> goals;
    private Goal lastGoal;
    private Goal currentGoal;
    private Thread monitoringUrgentGoal;
    private boolean shouldMonitoringUrgentGoal;

    public GoalArchitecture() {
        this.setShouldMonitoringUrgentGoal(true);
    }

    @Override
    public void accessMemoryObjects() {

        if (getDrivesMO() == null) {
            setDrivesMO(this.getInput(DRIVES_MEMORY, 0));
            this.setDrives(Collections.synchronizedList((List<Drive>) getDrivesMO().getI()));
        }

        if (getGoalsMO() == null) {
            setGoalsMO(this.getInput(GOALS_MEMORY, 0));
            this.setGoals(Collections.synchronizedList((List<Goal>) getGoalsMO().getI()));
        }
    }

    @Override
    public void calculateActivation() {
        try {
            this.setActivation(1d);

        } catch (CodeletActivationBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void proc() {

        synchronized (this) {

            if (getMonitoringUrgentGoal() == null)
                initMonitoringUrgentGoal();

            getGoals().stream().forEach(goal -> {
                synchronized (goal) {
                    goal.processVote();
                }
            });

            Goal mostVotedGoal = getMostVotedGoal(this.getGoals());

            setCurrentGoal(mostVotedGoal);

            mostVotedGoal.startGoalActions();

            while (!mostVotedGoal.isbLock()) ;

            mostVotedGoal.stopGoalActions();

            setLastGoal(getCurrentGoal());

        }

    }

    public synchronized Goal getMostVotedGoal(List<Goal> goals) {

        Goal likelyGoal = null;

        for (int i = 0; i < goals.size(); i++) {

            if (i == 0) {
                likelyGoal = goals.get(i);
            } else {
                if (goals.get(i).getActivation() > likelyGoal.getActivation()) {
                    likelyGoal = goals.get(i);
                }

            }

            goals.get(i).setCurrentGoal(false);

        }

        likelyGoal.setCurrentGoal(true);

        return likelyGoal;
    }

    public synchronized Goal findWinnerUrgentGoal(List<Goal> lstOfGoals) {

        synchronized (this) {
            Goal urgentGoal = null;

            lstOfGoals.forEach(goal -> {
                synchronized (goal) {
                    goal.urgentIntervention();
                }
            });

            List<Goal> lstOfIntervationGoals = lstOfGoals.stream().filter(goal -> goal.isUrgentIntervention() == true).collect(Collectors.toList());

            if (lstOfIntervationGoals.size() > 0) {

                if (lstOfIntervationGoals.size() > 1) {
                    urgentGoal = getMostVotedGoal(lstOfIntervationGoals);

                } else {
                    urgentGoal = lstOfIntervationGoals.get(0);
                }
            }

            return urgentGoal;
        }

    }

    public void initMonitoringUrgentGoal() {

        setMonitoringUrgentGoal(new Thread() {
            @Override
            public void run() {
                do {

                    synchronized (this) {

                        Goal urgentGoal = findWinnerUrgentGoal(getGoals());

                        if (urgentGoal != null) {

                            getCurrentGoal().pauseGoalActions();

                            urgentGoal.startGoalActions();

                            while (!urgentGoal.isFinishedGoalActions() && urgentGoal.isUrgentIntervention()) {
                                urgentGoal.isFinishedUrgentIntervention();
                            }

                            urgentGoal.stopGoalActions();

                            getCurrentGoal().resumeGoalActions();
                        }
                    }

                } while (isShouldMonitoringUrgentGoal());
            }

        });

        getMonitoringUrgentGoal().start();
    }

    public synchronized List<Drive> getDrives() {
        return drives;
    }

    public synchronized void setDrives(List<Drive> drives) {

        try {
            if (drives == null) {
                throw new MotivationalException(MotivationalMessages.MSG_VAR_GOAL_ARC_DRIVES_NULL);
            } else if (drives.size() == 0) {
                throw new MotivationalException(MotivationalMessages.MSG_VAR_GOAL_ARC_DRIVES_NULL);
            }

            this.drives = drives;
        } catch (MotivationalException me) {
            me.printStackTrace();
        }
    }

    public synchronized List<Goal> getGoals() {
        return goals;
    }

    public synchronized void setGoals(List<Goal> goals) {

        try {
            if (goals == null) {
                throw new MotivationalException(MotivationalMessages.MSG_VAR_GOAL_ARC_GOALS_NULL);
            } else if (goals.size() == 0) {
                throw new MotivationalException(MotivationalMessages.MSG_VAR_GOAL_ARC_GOALS_NULL);
            }

            this.goals = goals;
        } catch (MotivationalException me) {
            me.printStackTrace();
        }

    }

    public synchronized Goal getCurrentGoal() {
        return currentGoal;
    }

    public synchronized void setCurrentGoal(Goal currentGoal) {
        this.currentGoal = currentGoal;
    }

    public synchronized Goal getLastGoal() {
        return lastGoal;
    }

    public synchronized void setLastGoal(Goal lastGoal) {
        this.lastGoal = lastGoal;
    }

    public boolean isShouldMonitoringUrgentGoal() {
        return shouldMonitoringUrgentGoal;
    }

    public void setShouldMonitoringUrgentGoal(boolean shouldMonitoringUrgentGoal) {
        this.shouldMonitoringUrgentGoal = shouldMonitoringUrgentGoal;
    }

    /**
     * @return the monitoringUrgentGoal
     */
    public Thread getMonitoringUrgentGoal() {
        return monitoringUrgentGoal;
    }

    /**
     * @param monitoringUrgentGoal the monitoringUrgentGoal to set
     */
    public void setMonitoringUrgentGoal(Thread monitoringUrgentGoal) {
        this.monitoringUrgentGoal = monitoringUrgentGoal;
    }

    public MemoryObject getDrivesMO() {
        return drivesMO;
    }

    public void setDrivesMO(MemoryObject drivesMO) {
        this.drivesMO = drivesMO;
    }

    public MemoryObject getGoalsMO() {
        return goalsMO;
    }

    public void setGoalsMO(MemoryObject goalsMO) {
        this.goalsMO = goalsMO;
    }

}
