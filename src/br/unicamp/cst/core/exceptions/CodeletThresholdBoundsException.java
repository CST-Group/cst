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
 * @author klaus
 *
 */
public class CodeletThresholdBoundsException extends Exception 
{
	private static final long serialVersionUID = 1L;

	public CodeletThresholdBoundsException(String message) 
	{
		super(message);
	}
	public CodeletThresholdBoundsException(String message, Throwable cause) 
	{
		super(message, cause);
	}

}
