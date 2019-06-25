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
import br.unicamp.cst.behavior.bn.BehaviorNetwork;
import br.unicamp.cst.behavior.bn.GlobalVariables;
import java.util.ArrayList;
import java.util.Calendar;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import br.unicamp.cst.core.entities.Codelet;


/**
 * @author Klaus
 *
 */
public class BHMonitor extends Codelet 
{
	private XYSeriesCollection dataset;
	private BehaviorNetwork behaviorNetwork;
	private double initialTime=Calendar.getInstance().getTimeInMillis();
	private ArrayList<String> behaviorsIWantShownInGraphics=null;
	
	private GlobalVariables globalVariables;

	public BHMonitor(BehaviorNetwork behaviorNetwork,ArrayList<String> behaviorsIWantShownInGraphics,GlobalVariables globalVariables) 
	{
		this.globalVariables = globalVariables;
		this.behaviorsIWantShownInGraphics=behaviorsIWantShownInGraphics;
		this.dataset = new XYSeriesCollection();
		this.behaviorNetwork=behaviorNetwork;

		ArrayList<Behavior> competences=new ArrayList<Behavior>();
		competences.addAll(behaviorNetwork.getBehaviors());
		if(!competences.isEmpty()){

			for(Behavior module:competences){
				synchronized(module){
					if(behaviorsIWantShownInGraphics!=null && !behaviorsIWantShownInGraphics.isEmpty()){
						if(behaviorsIWantShownInGraphics.contains(module.getName())){
							dataset.addSeries(new XYSeries(module.getName()));
						}
					}else{
						dataset.addSeries(new XYSeries(module.getName()));
					}
				}
			}
			dataset.addSeries(new XYSeries("Temp Theta"));
		}	


		//-- Chart with the level of activation --------			
		Grafico activationLevel=new Grafico("Activation plot","Behaviors","time","activation",dataset);
		//--------------------------------------------

	}

	public BHMonitor(BehaviorNetwork behaviorNetwork) {
		this.dataset = new XYSeriesCollection();
		this.behaviorNetwork=behaviorNetwork;

		ArrayList<Behavior> competences=new ArrayList<Behavior>();
		competences.addAll(behaviorNetwork.getBehaviors());
		if(!competences.isEmpty()){

			for(Behavior module:competences){
				synchronized(module){
					dataset.addSeries(new XYSeries(module.getName()));
				}
			}
			dataset.addSeries(new XYSeries("Temp Theta"));
		}	


		//-- Chart with the level of activation --------			
		Grafico activationLevel=new Grafico("Activation plot","Behaviors","time [ms]","activation [AU]",dataset);
		//--------------------------------------------

	}

	/* (non-Javadoc)
	 * @see br.unicamp.cogsys.core.entities.Codelet#proc()
	 */
	@Override
	public void proc() {

		ArrayList<Behavior> tempBehaviors=new ArrayList<Behavior>();
		tempBehaviors.addAll(behaviorNetwork.getBehaviors());
		if(!tempBehaviors.isEmpty()){

			for(Behavior module:tempBehaviors){
				synchronized(module){
					//System.out.println(module.getId()+": "+module.getValue());
					double instant=Calendar.getInstance().getTimeInMillis()-initialTime;

					

					
					if(behaviorsIWantShownInGraphics!=null && !behaviorsIWantShownInGraphics.isEmpty()){
						if(behaviorsIWantShownInGraphics.contains(module.getName())){
							dataset.getSeries(module.getName()).add(instant, module.getActivation());
						}
					}else{
						dataset.getSeries(module.getName()).add(instant, module.getActivation());
					}
					
				}
			}
			double instant=Calendar.getInstance().getTimeInMillis()-initialTime;
			if(globalVariables!=null)
				dataset.getSeries("Temp Theta").add(instant, globalVariables.getThetaTemp());

		}



	}

	@Override
	public void accessMemoryObjects() {
		// TODO Auto-generated method stub

	}

	@Override
	public void calculateActivation() {
		// TODO Auto-generated method stub
		
	}

}
