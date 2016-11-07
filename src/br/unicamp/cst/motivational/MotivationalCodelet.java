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
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.CSTMessages;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

import java.util.*;

public abstract class MotivationalCodelet extends Codelet {

    public static final String INPUT_DRIVES_MEMORY = "INPUT_DRIVES_MEMORY";
    public static final String SENSORS_MEMORY = "SENSORS_MEMORY";
    public static final String OUTPUT_DRIVE_MEMORY = "OUTPUT_DRIVES_MEMORY";

    private Map<Drive, Double> inputDrives;
    private List<MemoryObject> sensors;
    private String name;
    private double priority;
    private double urgentActivation;
    private double urgentActivationThreshold;
    private double lowerUrgentThreshold;
    private double level;
    private double highPriorityThreshold;

    private MemoryObject inputDrivesMO;
    private MemoryObject sensorsMO;
    private MemoryObject outputDriveMO;

    public MotivationalCodelet(String name, double level, double priority, double highPriorityThreshold, double urgentActivationThreshold, double lowerUrgentThreshold) throws CodeletActivationBoundsException {
        setLevel(level);
        setName(name);
        setPriority(priority);
        setActivation(0.0d);
        setUrgentActivation(0.0d);
        setUrgentActivationThreshold(urgentActivationThreshold);
        setLowerUrgentThreshold(lowerUrgentThreshold);
        setInputDrives(new HashMap<Drive, Double>());
        setHighPriorityThreshold(highPriorityThreshold);

    }

    @Override
    public void accessMemoryObjects() {

        if (getLevel() != 0) {
            setInputDrivesMO((MemoryObject) this.getInput(INPUT_DRIVES_MEMORY, 0));
            this.setInputDrives((Map<Drive, Double>) getInputDrivesMO().getI());
        }

        if (getSensorsMO() == null) {
            setSensorsMO((MemoryObject) this.getInput(SENSORS_MEMORY, 0));
            this.setSensors(Collections.synchronizedList((List<MemoryObject>) getSensorsMO().getI()));
        }

        if (getOutputDriveMO() == null) {
            setOutputDriveMO((MemoryObject) this.getOutput(OUTPUT_DRIVE_MEMORY, 0));
        }

    }


    @Override
    public void proc() {
        Drive drive = new Drive(getName(), getActivation(), getPriority(), getLevel(), getUrgentActivation(), getUrgentActivationThreshold(), getLowerUrgentThreshold());
        getOutputDriveMO().setI(drive);
        getOutputDriveMO().setEvaluation(getActivation());
    }


    public void addDrive(Drive drive, double relevance) {
        getInputDrives().put(drive, relevance);
    }

    public void removeDrive(Drive drive){
        getInputDrives().remove(drive);
    }

    @Override
    public synchronized void calculateActivation() {

        synchronized (this) {
            try {
                if (getInputDrives() != null && getLevel() != 0) {
                    if (getInputDrives().size() > 0) {
                        List<Drive> listOfDrives = new ArrayList<Drive>();
                        List<Drive> listOfHighPriorityDrives = new ArrayList<Drive>();

                        for (Map.Entry<Drive, Double> drive : getInputDrives().entrySet()) {

                            Drive driveClone = new Drive(getName(), getActivation(), getPriority(), getLevel(),
                                                         getUrgentActivation(), getUrgentActivationThreshold(), getLowerUrgentThreshold());

                            driveClone.setActivation(driveClone.getActivation() * drive.getValue());

                            if (driveClone.getPriority() >= getHighPriorityThreshold())
                                listOfHighPriorityDrives.add(drive.getKey());

                            listOfDrives.add(driveClone);
                        }

                        this.setActivation(calculateSecundaryDriveActivation(getSensors(), listOfDrives));
                        this.calculateUrgentActivation(getSensors(), listOfHighPriorityDrives);

                    } else {
                        this.setActivation(calculateSimpleActivation(getSensors()));
                        this.calculateUrgentActivation(getSensors(), null);
                    }
                } else {
                    this.setActivation(calculateSimpleActivation(getSensors()));
                    this.calculateUrgentActivation(getSensors(), null);
                }

            } catch (CodeletActivationBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract double calculateUrgentActivation(List<MemoryObject> sensors, List<Drive> listOfHighPriorityDrives);

    public abstract double calculateSimpleActivation(List<MemoryObject> sensors);

    public abstract double calculateSecundaryDriveActivation(List<MemoryObject> sensors, List<Drive> listOfDrives);

    public synchronized double getLevel() {
        return level;
    }

    public synchronized Map<Drive, Double> getInputDrives() {
        return inputDrives;
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

    public MemoryObject getInputDrivesMO() {
        return inputDrivesMO;
    }

    public void setInputDrivesMO(MemoryObject inputDrivesMO) {
        this.inputDrivesMO = inputDrivesMO;
    }

    public MemoryObject getSensorsMO() {
        return sensorsMO;
    }

    public void setSensorsMO(MemoryObject sensorsMO) {
        this.sensorsMO = sensorsMO;
    }

    public MemoryObject getOutputDriveMO() {
        return outputDriveMO;
    }

    public void setOutputDriveMO(MemoryObject outputDriveMO) {
        this.outputDriveMO = outputDriveMO;
    }

    public List<MemoryObject> getSensors() {
        return sensors;
    }

    public void setSensors(List<MemoryObject> sensors) {
        this.sensors = sensors;
    }

    public double getUrgentActivation() {
        return urgentActivation;
    }

    public void setUrgentActivation(double urgentActivation) {

        try {
            if(urgentActivation < 0 || urgentActivation > 1)
            {
                throw new Exception(CSTMessages.MSG_VAR_URGENT_ACTIVATION_RANGE);
            }
            else
            {
                this.urgentActivation = urgentActivation;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public double getUrgentActivationThreshold() {
        return urgentActivationThreshold;
    }

    public void setUrgentActivationThreshold(double urgentActivationThreshold) {

        try {
            if(urgentActivationThreshold < 0 || urgentActivationThreshold > 1)
            {
                throw new Exception(CSTMessages.MSG_VAR_URGENT_ACTIVATION_THRESHOLD_RANGE);
            }
            else
            {
                this.urgentActivationThreshold = urgentActivationThreshold;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }


    }

    public double getLowerUrgentThreshold() {
        return lowerUrgentThreshold;
    }

    public void setLowerUrgentThreshold(double lowerUrgentThreshold) {

        try {
            if(lowerUrgentThreshold < 0 || lowerUrgentThreshold > 1)
            {
                throw new Exception(CSTMessages.MSG_VAR_LOWER_URGENT_ACTIVATION_THRESHOLD_RANGE);
            }
            else
            {
                this.lowerUrgentThreshold = lowerUrgentThreshold;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
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

    public double getHighPriorityThreshold() {
        return highPriorityThreshold;
    }

    public void setHighPriorityThreshold(double highPriorityThreshold) {


        try {
            if(highPriorityThreshold < 0 || highPriorityThreshold > 1)
            {
                throw new Exception(CSTMessages.MSG_VAR_HIGH_PRIORITY);
            }
            else
            {
                this.highPriorityThreshold = highPriorityThreshold;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }


    }

    private void setInputDrives(Map<Drive, Double> inputDrives) {
        this.inputDrives = inputDrives;
    }
}
