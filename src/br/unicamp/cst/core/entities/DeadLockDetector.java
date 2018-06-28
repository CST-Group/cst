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

package br.unicamp.cst.core.entities;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * This codelet detects deadlocks in the system and warns the user about it.
 * 
 * @author K. Raizer
 *
 */
public class DeadLockDetector implements Runnable {

	private int ddRefreshPeriod = 1000;
	private boolean shouldLoop = true;

	/**
	 * Creates a DeadLockDetector.
	 * 
	 * @param ddRefreshPeriod
	 *            the refresh period of detection.
	 */
	public DeadLockDetector(int ddRefreshPeriod) {
		this.ddRefreshPeriod = ddRefreshPeriod;
	}

	/**
	 * Creates a DeadLockDetector.
	 */
	public DeadLockDetector() {
	}

	@Override
	public void run() {
		do {
			ThreadMXBean tmx = ManagementFactory.getThreadMXBean();
			long[] ids = tmx.findDeadlockedThreads();
			if (ids != null) {
				ThreadInfo[] infos = tmx.getThreadInfo(ids, true, true);
				System.out.println("The following threads are deadlocked: ");
				for (ThreadInfo ti : infos) {
					System.out.println(ti);
				}
			}
			try {
				Thread.currentThread().sleep(this.ddRefreshPeriod);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (shouldLoop);
	}

	/**
	 * Starts the DeadLockDetector.
	 */
	public void start() {
		this.shouldLoop = true;
		new Thread(this).start();
	}

	/**
	 * Stops the DeadLockDetector.
	 */
	public synchronized void stop() {
		this.shouldLoop = false;
	}

	/**
	 * Gets the detector refresh period.
	 * 
	 * @return the ddRefreshPeriod.
	 */
	public int getDdRefreshPeriod() {
		return ddRefreshPeriod;
	}

	/**
	 * Sets the detector refresh period.
	 * 
	 * @param ddRefreshPeriod
	 *            the ddRefreshPeriod to set.
	 */
	public void setDdRefreshPeriod(int ddRefreshPeriod) {
		this.ddRefreshPeriod = ddRefreshPeriod;
	}

}
