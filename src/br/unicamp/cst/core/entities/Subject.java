/**
 * 
 */
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
	public void registerCodelet(Codelet co, MemoryObjectType type, int io);
	
	/**
	 * Removes a codelet's io from the subject registered list
	 * @param co
	 * @param type
	 * @param io
	 */
	public void unregisterCodelet(Codelet co, MemoryObjectType type, int io);
	
	public void notifyCodelets();
}
