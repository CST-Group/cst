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

import br.unicamp.cst.behavior.bn.Behavior;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFrame;

import br.unicamp.cst.core.entities.Memory;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

public class BNplot {
	Graph<String, String> g;


	/**
	 * 
	 * @param behaviorList the Behavior list
	 */
	public BNplot(ArrayList<Behavior> behaviorList) {

		// Graph<V, E> where V is the type of the vertices and E is the type of the edges
		g = new SparseMultigraph<String, String>();
		// Add some vertices.


		for(Behavior be:behaviorList)
		{
			g.addVertex(be.getName());
		}


		for(Behavior be:behaviorList)
		{

			HashMap<Behavior,ArrayList<Memory>> succPred=new HashMap<Behavior, ArrayList<Memory>>();
			succPred.putAll(be.getSuccessors());
			String sourceBeName=be.getName();
			Iterator it = succPred.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry)it.next();
				Behavior targetBe=(Behavior) pairs.getKey();
				String targetBeName=targetBe.getName();
				//		        if(g.findEdge(key, jsonLinks.getString(index))==null){
				g.addEdge(sourceBeName+"_"+targetBeName, sourceBeName,targetBeName, EdgeType.UNDIRECTED);
				//		        }
			}


			HashMap<Behavior,ArrayList<Memory>> conflict=new HashMap<Behavior, ArrayList<Memory>>();
			conflict.putAll(be.getConflicters());

			it = conflict.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry)it.next();
				Behavior targetBe=(Behavior) pairs.getKey();
				String targetBeName=targetBe.getName();
				//		        if(g.findEdge(key, jsonLinks.getString(index))==null){
				g.addEdge(sourceBeName+"_"+targetBeName, sourceBeName,targetBeName, EdgeType.DIRECTED);
				
				//		        }
			}
			




		}

	}
	BasicVisualizationServer<String,String> vv_global;

	public void plot() {
		BNplot sgv = this; //We create our graph in here
		// The Layout<V, E> is parameterized by the vertex and edge types
		//		Layout<Integer, String> layout = new CircleLayout(sgv.g);
		//		Layout<String, String> layout = new ISOMLayout<String, String>(sgv.g);

		Layout<String, String> layout = new KKLayout<String, String>(sgv.g);

		layout.setSize(new Dimension(600,600)); // sets the initial size of the layout space
		// The BasicVisualizationServer<V,E> is parameterized by the vertex and edge types
		BasicVisualizationServer<String,String> vv = new BasicVisualizationServer<String,String>(layout);
		vv_global=vv;
		vv.setPreferredSize(new Dimension(650,650)); //Sets the viewing area size
//		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.setBackground(Color.white);

		//		vv.getRenderContext().setVertexShapeTransformer(vertexShape);

		JFrame frame = new JFrame("Behavior Network Graph View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv); 
		frame.pack();
		frame.setVisible(true);       

//		vv.getRenderContext().setVertexFillPaintTransformer(vertexColor);

	}
}
