/*******************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     K. Raizer, A. L. O. Paraense, R. R. Gudwin - initial API and implementation
 ******************************************************************************/

package br.unicamp.cst.io;

import java.util.ArrayList;

import br.unicamp.cst.core.entities.Codelet;
/**
 * Body interface has two lists, a list of sensors and one of actuators. 
 * It is a holder for sensor and actuator codelets.
 * 
 * @author klaus
 * @author andre
 */
public class BodyProxy
{

	private ArrayList<Codelet> listSensors;

	private ArrayList<Codelet> listActuators;


	public BodyProxy()
	{
		listSensors = new ArrayList<Codelet>();
		listActuators = new ArrayList<Codelet>();           
	}

	public synchronized void startSenses()
	{
		for(Codelet sensor:listSensors)
		{
			sensor.start();
		}
	}


	public synchronized void stopSenses()
	{
		for(Codelet sensor:listSensors)
		{
			sensor.stop();
		}
	}

	public synchronized void startActuators()
	{
		for(Codelet actuator:listActuators)
		{
			actuator.start();
		}
	}


	public synchronized void stopActuators()
	{
		for(Codelet actuator:listActuators)
		{
			actuator.stop();
		}
	}


	public synchronized void addSensor(Codelet sensorAdded)
	{
		listSensors.add(sensorAdded);
	}

	public synchronized void addActuator(Codelet actuatorAdded)
	{
		listActuators.add(actuatorAdded);
	}

	public synchronized ArrayList<Codelet> getListSensors() {
		return listSensors;
	}

	public synchronized void setListSensors(ArrayList<Codelet> listSensors) {
		this.listSensors = listSensors;
	}

	public synchronized ArrayList<Codelet> getListActuators() {
		return listActuators;
	}

	public synchronized void setListActuators(ArrayList<Codelet> listActuators) {
		this.listActuators = listActuators;
	}
}
