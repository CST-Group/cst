/**
 * 
 */
package br.unicamp.cst.core.exceptions;

/**
 * This class represents a Java exception to be thrown when the access memory object
 * method is not capable of finding the corresponding memory object
 * 
 * @author andre
 *
 */
public class MemoryObjectNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6845401281653737754L;

	/**
	 * 
	 */
	public MemoryObjectNotFoundException() {
	}

	/**
	 * @param message
	 */
	public MemoryObjectNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MemoryObjectNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MemoryObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public MemoryObjectNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
