/**
 * *****************************************************************************
 * Copyright (c) 2012 DCA-FEEC-UNICAMP All rights reserved. This program and the
 * accompanying materials are made available under the terms of the GNU Lesser
 * Public License v3 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * Contributors: K. Raizer, A. L. O. Paraense, R. R. Gudwin - initial API and
 * implementation
 * ****************************************************************************
 */
package br.unicamp.cst.core.entities;

import br.unicamp.cst.behavior.bn.support.Grafico;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @author klaus
 *
 */
public class CodeletsMonitor implements Runnable {

    private double initialTime = Calendar.getInstance().getTimeInMillis();
    private List<Codelet> listOfCodelets = new ArrayList<Codelet>();
    private long refreshPeriod;
    private String title;

    public CodeletsMonitor(List<Codelet> listOfCodelets, long refreshPeriod, String title) {
        this.listOfCodelets = listOfCodelets;
        this.refreshPeriod = refreshPeriod;
        this.setTitle(title);
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

                for (Codelet co : tempCodeletsList) {

                    double instant = Calendar.getInstance().getTimeInMillis() - initialTime;

                    dataset.getSeries(co.getName()).add(instant, co.getActivation());
                }
                try {
                    Thread.currentThread().sleep(refreshPeriod);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void start() {
        new Thread(this).start();
    }

    public void stop() {
        new Thread(this).interrupt();
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title.trim().equals("") ? "Codelet" : title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
}
