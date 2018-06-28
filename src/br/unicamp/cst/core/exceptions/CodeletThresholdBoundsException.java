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

package br.unicamp.cst.core.exceptions;

/**
 * This class represents a Java exception to be thrown when the codelet's
 * threshold is set to more than 1.0 or less than 0.0.
 * 
 * @author A. L. O. Paraense
 * @author K. Raizer
 * @see Exception
 */
public class CodeletThresholdBoundsException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a CodeletThresholdBoundsException.
	 * 
	 * @param message
	 *            the exception message.
	 */
	public CodeletThresholdBoundsException(String message) {
		super(message);
	}

	/**
	 * Creates a CodeletThresholdBoundsException.
	 * 
	 * @param message
	 *            the exception message.
	 * @param cause
	 *            the exception cause.
	 */
	public CodeletThresholdBoundsException(String message, Throwable cause) {
		super(message, cause);
	}

}
