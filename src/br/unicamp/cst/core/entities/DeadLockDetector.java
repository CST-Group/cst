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
package br.unicamp.cst.core.entities;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import br.unicamp.cst.core.entities.Codelet;


/**
 *  This codelet detects deadlocks in the system and warns the user about it.
 *  
 *  
 * @author klauslocal
 *
 */

public class DeadLockDetector implements Runnable{

	private int ddRefreshPeriod=1000;
	private boolean shouldLoop=true;

	public DeadLockDetector(int ddRefreshPeriod){
		this.ddRefreshPeriod=ddRefreshPeriod;
	}
	public DeadLockDetector(){
	}

	@Override
	public void run() {
		do{
			ThreadMXBean tmx = ManagementFactory.getThreadMXBean();
			long[] ids = tmx.findDeadlockedThreads();
			if (ids != null) {
				ThreadInfo[] infos = tmx.getThreadInfo(ids, true, true);
				System.out.println("The following threads are deadlocked: ");
				for (ThreadInfo ti : infos) {
					System.out.println(ti);
				}
			}
			try {Thread.currentThread().sleep(this.ddRefreshPeriod);} catch (InterruptedException e) {e.printStackTrace();}
		}while(shouldLoop);
	}

	public void start()
	{ 		this.shouldLoop=true;
			new Thread(this).start();
	}
	public synchronized void stop(){
		this.shouldLoop=false;
	}
	
	/**
	 * @return the ddRefreshPeriod
	 */
	public int getDdRefreshPeriod() {
		return ddRefreshPeriod;
	}

	/**
	 * @param ddRefreshPeriod the ddRefreshPeriod to set
	 */
	public void setDdRefreshPeriod(int ddRefreshPeriod) {
		this.ddRefreshPeriod = ddRefreshPeriod;
	}

}
