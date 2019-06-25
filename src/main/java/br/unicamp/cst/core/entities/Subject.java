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

/**
 * @author Klaus Raizer
 *
 */
public interface Subject {
	/**
	 *  Registers a codelet's input or output list to receive notifications from working storage.
	 * @param co Codelet
	 * @param type Type of memory objects being observed
	 * @param io Which list to register: 0 - input and 1 - output
	 */
	public void registerCodelet(Codelet co, String type, int io);
	
	/**
	 * Removes a codelet's io from the subject registered list
	 * @param co
	 * @param type
	 * @param io
	 */
	public void unregisterCodelet(Codelet co, String type, int io);
	
	public void notifyCodelets();
}
