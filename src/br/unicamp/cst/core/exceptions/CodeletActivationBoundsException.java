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
 * @author andre.paraense
 * @author klaus.raizer
 *
 */
public class CodeletActivationBoundsException extends Exception
{
   private static final long serialVersionUID = 6550752642966697942L;
   
   public CodeletActivationBoundsException(String message) 
   {
      super(message);
   }
   public CodeletActivationBoundsException(String message, Throwable cause) 
   {
      super(message, cause);
   }
   
}
