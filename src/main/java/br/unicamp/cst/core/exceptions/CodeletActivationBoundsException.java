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
 * activation is set to more than 1.0 or less than 0.0.
 * 
 * @author A. L. O. Paraense
 * @author K. Raizer
 * @see Exception
 *
 */
public class CodeletActivationBoundsException extends Exception {
	private static final long serialVersionUID = 6550752642966697942L;

	/**
	 * Creates a CodeletActivationBoundsException.
	 * 
	 * @param message
	 *            the exception message.
	 */
	public CodeletActivationBoundsException(String message) {
		super(message);
	}

	/**
	 * Creates a CodeletActivationBoundsException.
	 * 
	 * @param message
	 *            the exception message.
	 * @param cause
	 *            the exception cause.
	 */
	public CodeletActivationBoundsException(String message, Throwable cause) {
		super(message, cause);
	}

}
