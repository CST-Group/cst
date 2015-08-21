/*******************************************************************************
 * Copyright (c) 2012 K. Raizer, A. L. O. Paraense, R. R. Gudwin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     K. Raizer, A. L. O. Paraense, R. R. Gudwin - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package br.unicamp.cst.core.entities;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
/**
 * @author klaus
 *
 */

public class CodeletsMonitor implements Runnable{
	private double initialTime=Calendar.getInstance().getTimeInMillis();
	private List<Codelet> listOfCodelets=new ArrayList<Codelet>();
	private long refreshPeriod;

	public CodeletsMonitor(List<Codelet> listOfCodelets,long refreshPeriod){
		this.listOfCodelets=listOfCodelets;
		this.refreshPeriod=refreshPeriod;
	}



	@Override
	public void run() {
		
		XYSeriesCollection dataset = new XYSeriesCollection();

		for(Codelet co:listOfCodelets){
			dataset.addSeries(new XYSeries(co.getName()));
		}

//		Grafico activationLevel=new Grafico("Activation Levels","Codelets","time","activation",dataset);

		while(true){
			ArrayList<Codelet> tempCodeletsList=new ArrayList<Codelet>();
			tempCodeletsList.addAll(this.listOfCodelets);


			for(Codelet co:tempCodeletsList){

				double instant=Calendar.getInstance().getTimeInMillis()-initialTime;

				dataset.getSeries(co.getName()).add(instant, co.getActivation());
			}
			try {Thread.currentThread().sleep(refreshPeriod);} catch (InterruptedException e) {e.printStackTrace();}
			
		}
	}

	public void start()
	{ 
		new Thread(this).start();
	}
}
