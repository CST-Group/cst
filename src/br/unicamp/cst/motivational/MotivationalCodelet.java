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
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.CSTMessages;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

import java.util.*;

public abstract class MotivationalCodelet extends Codelet {

    public static final String INPUT_DRIVES_MEMORY = "INPUT_DRIVES_MEMORY";
    public static final String INPUT_SENSORS_MEMORY = "INPUT_SENSORS_MEMORY";
    public static final String OUTPUT_DRIVE_MEMORY = "OUTPUT_DRIVES_MEMORY";

    private String name;
    private double priority;
    private double urgencyThreshold;
    private double level;

    private Map<Drive, Double> drivesRelevance;
    private List<Memory> sensoryVariables;

    private Drive outputDrive;

    private Memory inputDrivesMO;
    private Memory inputSensorsMO;
    private Memory outputDriveMO;

    public MotivationalCodelet(String name, double level, double priority, double urgencyThreshold)
            throws CodeletActivationBoundsException {
        setLevel(level);
        setName(name);
        setPriority(priority);
        setActivation(0.0d);
        setUrgencyThreshold(urgencyThreshold);
        setDrivesRelevance(new HashMap<Drive, Double>());

        setOutputDrive(new Drive(getName(),
                                getActivation(),
                                getPriority(),
                                getLevel(),
                                getUrgencyThreshold(),
                                0));

    }

    @Override
    public void accessMemoryObjects() {

        if (getLevel() != 0) {
            setInputDrivesMO((MemoryObject) this.getInput(INPUT_DRIVES_MEMORY, 0));
            this.setDrivesRelevance((Map<Drive, Double>) getInputDrivesMO().getI());
        }

        if (getInputSensorsMO() == null) {
            setInputSensorsMO((MemoryObject) this.getInput(INPUT_SENSORS_MEMORY, 0));
            this.setSensoryVariables(Collections.synchronizedList((List<Memory>) getInputSensorsMO().getI()));
        }

        if (getOutputDriveMO() == null) {
            setOutputDriveMO((MemoryObject) this.getOutput(OUTPUT_DRIVE_MEMORY, 0));
        }

    }


    @Override
    public void proc() {

        getOutputDrive().setActivation(getActivation());
        getOutputDrive().setPriority(getPriority());
        getOutputDrive().setLevel(getLevel());

        getOutputDriveMO().setI(getOutputDrive());
        getOutputDriveMO().setEvaluation(getActivation());
    }


    public void addDrive(Drive drive, double relevance) {
        getDrivesRelevance().put(drive, relevance);
    }

    public void removeDrive(Drive drive){
        getDrivesRelevance().remove(drive);
    }

    @Override
    public synchronized void calculateActivation() {

        synchronized (this) {
            try {
                if (getDrivesRelevance() != null) {
                    if (getDrivesRelevance().size() > 0) {
                        List<Drive> listOfDrives = new ArrayList<Drive>();

                        for (Map.Entry<Drive, Double> drive : getDrivesRelevance().entrySet()) {

                            Drive driveClone = new Drive(drive.getKey().getName(), drive.getKey().getActivation(), drive.getKey().getPriority(), drive.getKey().getLevel(),
                                    drive.getKey().getUrgencyThreshold());

                            driveClone.setActivation(driveClone.getActivation() * drive.getValue());

                            listOfDrives.add(driveClone);
                        }

                        this.setActivation(calculateSecundaryDriveActivation(getSensoryVariables(), listOfDrives));

                    } else {
                        this.setActivation(calculateSimpleActivation(getSensoryVariables()));
                    }
                } else {
                    this.setActivation(calculateSimpleActivation(getSensoryVariables()));
                }

            } catch (CodeletActivationBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract double calculateSimpleActivation(List<Memory> sensors);

    public abstract double calculateSecundaryDriveActivation(List<Memory> sensors, List<Drive> listOfDrives);

    public synchronized double getLevel() {
        return level;
    }

    public synchronized Map<Drive, Double> getDrivesRelevance() {
        return drivesRelevance;
    }

    @Override
    public synchronized String getName() {
        return name;
    }

    @Override
    public synchronized void setName(String name) {
        try {
            if (name == null) {
                throw new Exception(CSTMessages.MSG_VAR_NAME_NULL);
            }

            this.name = name;

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

    public void setInputDrivesMO(MemoryObject inputDrivesMO) {
        this.inputDrivesMO = inputDrivesMO;
    }

    public Memory getInputSensorsMO() {
        return inputSensorsMO;
    }

    public void setInputSensorsMO(MemoryObject inputSensorsMO) {
        this.inputSensorsMO = inputSensorsMO;
    }

    public Memory getOutputDriveMO() {
        return outputDriveMO;
    }

    public void setOutputDriveMO(MemoryObject outputDriveMO) {
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
            if(level < 0 || level > 1)
            {
                throw new Exception(CSTMessages.MSG_VAR_URGENT_ACTIVATION_THRESHOLD_RANGE);
            }
            else
            {
                this.level = level;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void setDrivesRelevance(Map<Drive, Double> drivesRelevance) {
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
