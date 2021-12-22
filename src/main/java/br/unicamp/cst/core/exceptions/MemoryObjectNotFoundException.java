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
	 * @param message the message
	 */
	public MemoryObjectNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause the cause of the exception
	 */
	public MemoryObjectNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message the message
	 * @param cause the cause of the exception
	 */
	public MemoryObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message the message
	 * @param cause the cause of the exception
	 * @param enableSuppression if enable is to be supressed
	 * @param writableStackTrace if there should be a writable StackTrace
	 */
	public MemoryObjectNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
