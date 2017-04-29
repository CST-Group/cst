/*******************************************************************************
 * Copyright (c) 2016  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * E. M. Froes, R. R. Gudwin - initial API and implementation
 ******************************************************************************/

package br.unicamp.cst.motivational;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.CSTMessages;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

import java.util.*;

public abstract class MotivationalCodelet extends Codelet {

    public static final String INPUT_DRIVES_MEMORY = "INPUT_DRIVES_MEMORY";
    public static final String INPUT_SENSORS_MEMORY = "INPUT_SENSORS_MEMORY";
    public static final String OUTPUT_DRIVE_MEMORY = "OUTPUT_DRIVE_MEMORY";

    private String id;
    private double priority;
    private double urgencyThreshold;
    private double level;

    private Map<Memory, Double> drivesRelevance;
    private List<Memory> sensoryVariables;

    private Drive outputDrive;
    private Memory inputDrivesMO;
    private Memory inputSensorsMO;
    private Memory outputDriveMO;

    public MotivationalCodelet(String id, double level, double priority, double urgencyThreshold) {
        setLevel(level);
        setId(id);
        setPriority(priority);
        setUrgencyThreshold(urgencyThreshold);
        setDrivesRelevance(new HashMap<Memory, Double>());
        setSensoryVariables(new ArrayList<Memory>());
        setOutputDrive(new Drive(getId(),
                0,
                getPriority(),
                getLevel(),
                getUrgencyThreshold(),
                0));

    }

    @Override
    public void accessMemoryObjects() {

        if (getInputDrivesMO() == null) {
            setInputDrivesMO(this.getInput(INPUT_DRIVES_MEMORY, 0));
            this.setDrivesRelevance((Map<Memory, Double>) getInputDrivesMO().getI());
        }

        if (getInputSensorsMO() == null) {
            setInputSensorsMO(this.getInput(INPUT_SENSORS_MEMORY, 0));
            this.setSensoryVariables(Collections.synchronizedList((List<Memory>) getInputSensorsMO().getI()));
        }

        if (getOutputDriveMO() == null) {
            setOutputDriveMO(this.getOutput(OUTPUT_DRIVE_MEMORY, 0));
        }

    }


    @Override
    public synchronized void proc() {
        synchronized (getOutputDriveMO()) {
            getOutputDrive().setActivation(getActivation());
            getOutputDriveMO().setEvaluation(getActivation());
            getOutputDriveMO().setI(getOutputDrive());
        }
    }


    public void addDrive(Memory drive, double relevance) {
        getDrivesRelevance().put(drive, relevance);
    }

    public void removeDrive(Memory drive) {
        getDrivesRelevance().remove(drive);
    }

    @Override
    public synchronized void calculateActivation() {
        synchronized (this) {
            try {
                if (getDrivesRelevance().size() > 0) {
                    List<Drive> listOfDrives = new ArrayList<Drive>();

                    for (Map.Entry<Memory, Double> driveMO : getDrivesRelevance().entrySet()) {

                        Drive drive = (Drive) driveMO.getKey();

                        Drive driveClone = new Drive(drive.getName(), drive.getActivation(), drive.getPriority(), drive.getLevel(),
                                drive.getUrgencyThreshold());

                        driveClone.setActivation(driveClone.getActivation() * driveMO.getValue());

                        listOfDrives.add(driveClone);
                    }

                    this.setActivation(verifingUrgencyThreshold(calculateSecundaryDriveActivation(getSensoryVariables(), listOfDrives), getOutputDrive()));

                } else {

                    if (getSensoryVariables().size() > 0) {
                        this.setActivation(verifingUrgencyThreshold(calculateSimpleActivation(getSensoryVariables()), getOutputDrive()));
                    } else {
                        this.setActivation(0);
                    }
                }
            } catch (CodeletActivationBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized double verifingUrgencyThreshold(double driveActivation, Drive drive) {

        double evaluation = 0;

        if (driveActivation > getUrgencyThreshold()) {
            evaluation = 0.5 + drive.getPriority();
        } else {
            evaluation = (driveActivation + drive.getEmotionalDistortion()) / 2;
        }

        return evaluation;
    }

    public abstract double calculateSimpleActivation(List<Memory> sensors);

    public abstract double calculateSecundaryDriveActivation(List<Memory> sensors, List<Drive> listOfDrives);

    public synchronized double getLevel() {
        return level;
    }

    public synchronized Map<Memory, Double> getDrivesRelevance() {
        return drivesRelevance;
    }

    public synchronized String getId() {
        return id;
    }

    public synchronized void setId(String id) {
        try {
            if (id == null) {
                throw new Exception(CSTMessages.MSG_VAR_NAME_NULL);
            }

            this.id = id;

        } catch (Exception me) {
            me.printStackTrace();
        }

    }

    public synchronized double getPriority() {
        return priority;
    }

    public synchronized void setPriority(double priority) {
        try {
            if (priority <= 0) {
                throw new Exception(CSTMessages.MSG_VAR_PRIORITY_NULL);
            }

            this.priority = priority;
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

    public Memory getInputSensorsMO() {
        return inputSensorsMO;
    }

    public void setInputSensorsMO(Memory inputSensorsMO) {
        this.inputSensorsMO = inputSensorsMO;
    }

    public Memory getOutputDriveMO() {
        return outputDriveMO;
    }

    public void setOutputDriveMO(Memory outputDriveMO) {
        this.outputDriveMO = outputDriveMO;
    }

    public List<Memory> getSensoryVariables() {
        return sensoryVariables;
    }

    public void setSensoryVariables(List<Memory> sensoryVariables) {
        this.sensoryVariables = sensoryVariables;
    }

    public void setLevel(double level) {
        try {
            if (level < 0 || level > 1) {
                throw new Exception(CSTMessages.MSG_VAR_URGENT_ACTIVATION_THRESHOLD_RANGE);
            } else {
                this.level = level;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setDrivesRelevance(Map<Memory, Double> drivesRelevance) {
        this.drivesRelevance = drivesRelevance;
    }

    private Drive getOutputDrive() {
        return outputDrive;
    }

    private void setOutputDrive(Drive outputDrive) {
        this.outputDrive = outputDrive;
    }

    public double getUrgencyThreshold() {
        return urgencyThreshold;
    }

    public void setUrgencyThreshold(double urgencyThreshold) {
        this.urgencyThreshold = urgencyThreshold;
    }
}
