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
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MotivationalCodelet extends Codelet {


    public static final String INPUT_DRIVES_MEMORY = "INPUT_DRIVES_MEMORY";
    public static final String SENSORS_MEMORY = "SENSORS_MEMORY";
    public static final String OUTPUT_DRIVE_MEMORY = "OUTPUT_DRIVES_MEMORY";

    private DriveLevel level;
    private List<MemoryObject> inputDrives;
    private List<MemoryObject> sensors;
    private String name;
    private double priorityValue;

    private MemoryObject inputDrivesMO;
    private MemoryObject sensorsMO;
    private MemoryObject outputDriveMO;

    public MotivationalCodelet(String name, DriveLevel level, double priority) throws CodeletActivationBoundsException {
        this.setLevel(level);
        this.setName(name);
        this.setPriority(priority);
        this.setActivation(0.0d);
    }

    @Override
    public void accessMemoryObjects(){

        if(level.equals(DriveLevel.SECUNDARY)){
            setInputDrivesMO((MemoryObject) this.getInput(INPUT_DRIVES_MEMORY, 0));
            this.setInputDrives(Collections.synchronizedList((List<MemoryObject>) getInputDrivesMO().getI()));
        }

        if (getSensorsMO() == null) {
            setSensorsMO((MemoryObject) this.getInput(SENSORS_MEMORY, 0));
            this.setSensors(Collections.synchronizedList((List<MemoryObject>) getSensorsMO().getI()));
        }

        if(getOutputDriveMO() == null){
            setOutputDriveMO((MemoryObject) this.getOutput(OUTPUT_DRIVE_MEMORY, 0));
        }

    }


    @Override
    public void proc(){
        Map<String, Object> drive = new HashMap<>();

        drive.put("name", getName());
        drive.put("priorityValue", getPriority());
        drive.put("level", getLevel());
        drive.put("activation", getActivation());

        getOutputDriveMO().setI(drive);
        getOutputDriveMO().setEvaluation(getActivation());
    }

    @Override
    public synchronized void calculateActivation() {

        synchronized (this) {
            try {
                if (getInputDrives() != null && getLevel().equals(DriveLevel.SECUNDARY)) {
                    if (getInputDrives().size() > 0) {
                        this.setActivation(calculateSecundaryDriveActivation(getInputDrives(), getSensors()));

                    } else {
                        this.setActivation(calculateSimpleActivation(getSensors()));
                    }
                } else {
                    this.setActivation(calculateSimpleActivation(getSensors()));
                }

            } catch (CodeletActivationBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract double calculateSimpleActivation(List<MemoryObject> sensors);

    public abstract double calculateSecundaryDriveActivation(List<MemoryObject> inputDrives, List<MemoryObject> sensors);

    public synchronized DriveLevel getLevel() {
        return level;
    }

    public synchronized void setLevel(DriveLevel level) {
        try {
            if (level == null) {
                throw new MotivationalException(MotivationalMessages.MSG_VAR_LEVEL_NULL);
            }

            this.level = level;

        } catch (MotivationalException me) {
            me.printStackTrace();
        }

    }

    public synchronized List<MemoryObject> getInputDrives() {
        return inputDrives;
    }

    public synchronized void setInputDrives(List<MemoryObject> inputDrives) {
        this.inputDrives = inputDrives;
    }

    @Override
    public synchronized String getName() {
        return name;
    }

    @Override
    public synchronized void setName(String name) {
        try {
            if (name == null) {
                throw new MotivationalException(MotivationalMessages.MSG_VAR_NAME_NULL);
            }

            this.name = name;

        } catch (MotivationalException me) {
            me.printStackTrace();
        }

    }

    public synchronized double getPriority() {
        return priorityValue;
    }

    public synchronized void setPriority(double priority) {
        try {
            if (priority <= 0) {
                throw new MotivationalException(MotivationalMessages.MSG_VAR_PRIORITY_NULL);
            }

            this.priorityValue = priority;
        } catch (MotivationalException me) {
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
}
