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
package br.unicamp.cst.behavior.bn.support;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

public class Grafico {

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

		
		ChartFrame frame= new ChartFrame(frametitle,chart);
	
		frame.pack();
		frame.setVisible(true);
	}
	
	
}
