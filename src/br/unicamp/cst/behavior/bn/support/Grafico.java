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
package br.unicamp.cst.behavior.bn.support;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import java.awt.*;

public class Grafico {

	private JFreeChart chart;
	private XYPlot xyplot;

	public Grafico(String frametitle, String charttitle, String xlabel, String ylabel, XYSeriesCollection dataset){
		JFreeChart chart = ChartFactory.createXYLineChart(charttitle, xlabel, ylabel, dataset, PlotOrientation.VERTICAL, true, true, false);

		XYPlot plot = (XYPlot) chart.getPlot();

		plot.setBackgroundPaint(Color.lightGray);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		renderer.setShapesVisible(true);
		renderer.setShapesFilled(true);

		setXyplot(plot);
		setChart(chart);

		ChartFrame frame= new ChartFrame(frametitle,chart);
	
		frame.pack();
		frame.setVisible(true);
	}


	public JFreeChart getChart() {
		return chart;
	}

	public void setChart(JFreeChart chart) {
		this.chart = chart;
	}

	public XYPlot getXyplot() {
		return xyplot;
	}

	public void setXyplot(XYPlot xyplot) {
		this.xyplot = xyplot;
	}
}
