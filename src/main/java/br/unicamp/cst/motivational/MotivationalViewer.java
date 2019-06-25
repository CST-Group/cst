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

import br.unicamp.cst.core.entities.Codelet;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MotivationalViewer implements Runnable  {
    private List<Codelet> listOfMotivationalEntities = new ArrayList<Codelet>();
    private long refreshPeriod;
    private String title;
    private String entity;

    public MotivationalViewer(List<Codelet> listOfMotivationalEntities, long refreshPeriod, String title, String entity) {
        this.setListOfMotivationalEntities(listOfMotivationalEntities);
        this.setRefreshPeriod(refreshPeriod);
        this.setTitle(title);
        this.setEntity(entity);
    }

    @Override
    public synchronized void run() {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        final JFreeChart chart = ChartFactory.createBarChart(
                getTitle(),
                getEntity(),
                "Value",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        chart.setBackgroundPaint(Color.lightGray);

        ChartFrame frame= new ChartFrame(getTitle(), chart);
        frame.pack();
        frame.setVisible(true);

        while (true) {
            ArrayList<Codelet> tempCodeletsList = new ArrayList<Codelet>();
            tempCodeletsList.addAll(this.getListOfMotivationalEntities());

            synchronized (tempCodeletsList) {

                for (Codelet co : tempCodeletsList) {
                    dataset.addValue(co.getActivation(), co.getName(), "activation");
                }
                try {
                    Thread.currentThread().sleep(getRefreshPeriod());
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

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public List<Codelet> getListOfMotivationalEntities() {
        return listOfMotivationalEntities;
    }

    public void setListOfMotivationalEntities(List<Codelet> listOfMotivationalEntities) {
        this.listOfMotivationalEntities = listOfMotivationalEntities;
    }

    public long getRefreshPeriod() {
        return refreshPeriod;
    }

    public void setRefreshPeriod(long refreshPeriod) {
        this.refreshPeriod = refreshPeriod;
    }
}


