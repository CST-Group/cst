/**
 * 
 */
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
