/**
 * *****************************************************************************
 * Copyright (c) 2012 DCA-FEEC-UNICAMP All rights reserved. This program and the
 * accompanying materials are made available under the terms of the GNU Lesser
 * Public License v3 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * Contributors: K. Raizer, A. L. O. Paraense, E. M. Fr�es, R. R. Gudwin - initial API and
 * implementation
 * ****************************************************************************
 */
package br.unicamp.cst.core.entities;

import br.unicamp.cst.behavior.bn.support.Grafico;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This class is a monitor to watch the Codelets activation during execution. It
 * is used for debugging and analysis purposes.
 * 
 * @author K. Raizer
 * @see Runnable
 */
public class CodeletsMonitor implements Runnable {

	private double initialTime = Calendar.getInstance().getTimeInMillis();
	private List<Codelet> listOfCodelets = new ArrayList<Codelet>();
	private long refreshPeriod;
	private String title;
	private boolean isAutoFixedRange = false;
	private long autoRangeValue = 1000;

	/**
	 * Creates a CodeletsMonitor.
	 * 
	 * @param listOfCodelets
	 *            the list of Codelets monitored.
	 * @param refreshPeriod
	 *            the refresh period of monitoring.
	 * @param title
	 *            the chart's title.
	 */
	public CodeletsMonitor(List<Codelet> listOfCodelets, long refreshPeriod, String title) {
		this.listOfCodelets = listOfCodelets;
		this.refreshPeriod = refreshPeriod;
		this.setTitle(title);
	}

	/**
	 * Creates a CodeletsMonitor.
	 * 
	 * @param listOfCodelets
	 *            the list of Codelets monitored.
	 * @param refreshPeriod
	 *            the refresh period of monitoring.
	 * @param title
	 *            the chart's title.
	 * @param isAutoFixedRange
	 *            if the range of the chart should auto-fix.
	 * @param autoRangeValue
	 *            the auto range value.
	 */
	public CodeletsMonitor(List<Codelet> listOfCodelets, long refreshPeriod, String title, boolean isAutoFixedRange,
			long autoRangeValue) {
		this.listOfCodelets = listOfCodelets;
		this.refreshPeriod = refreshPeriod;
		this.setTitle(title);
		this.setAutoFixedRange(isAutoFixedRange);
		this.setAutoRangeValue(autoRangeValue);
	}

	@Override
	public synchronized void run() {

		XYSeriesCollection dataset = new XYSeriesCollection();

		for (Codelet co : listOfCodelets) {
			dataset.addSeries(new XYSeries(co.getName()));
		}

		Grafico activationLevel = new Grafico("Activation Levels", getTitle(), "time", "activation", dataset);

		while (true) {
			ArrayList<Codelet> tempCodeletsList = new ArrayList<Codelet>();
			tempCodeletsList.addAll(this.listOfCodelets);

			synchronized (tempCodeletsList) {

				double instant = Calendar.getInstance().getTimeInMillis() - initialTime;
				for (Codelet co : tempCodeletsList) {
					dataset.getSeries(co.getName()).add(instant, co.getActivation());
				}
				try {
					Thread.currentThread().sleep(refreshPeriod);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (isAutoFixedRange())
					activationLevel.getXyplot().getDomainAxis().setFixedAutoRange(getAutoRangeValue());
			}
		}
	}

	/**
	 * Starts the monitoring.
	 */
	public void start() {
		new Thread(this).start();
	}

	/**
	 * Stops the monitoring.
	 */
	public void stop() {
		new Thread(this).interrupt();
	}

	/**
	 * Gets the chart's title.
	 * 
	 * @return the title.
	 */
	public String getTitle() {
		return title.trim().equals("") ? "Codelet" : title;
	}

	/**
	 * Sets the chart's title.
	 * 
	 * @param title
	 *            the title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets if this monitor is auto fixing range.
	 * 
	 * @return true if this monitor is auto fixing range.
	 */
	public boolean isAutoFixedRange() {
		return isAutoFixedRange;
	}

	/**
	 * Sets if this monitor is auto fixing range.
	 * 
	 * @param autoFixedRange
	 *            the autoFixedRange to set.
	 */
	public void setAutoFixedRange(boolean autoFixedRange) {
		isAutoFixedRange = autoFixedRange;
	}

	/**
	 * Gets the auto range value.
	 * 
	 * @return the auto range value.
	 */
	public long getAutoRangeValue() {
		return autoRangeValue;
	}

	/**
	 * Sets the auto range value.
	 * 
	 * @param autoRangeValue
	 *            the autoRangeValue to set.
	 */
	public void setAutoRangeValue(long autoRangeValue) {
		this.autoRangeValue = autoRangeValue;
	}
}
