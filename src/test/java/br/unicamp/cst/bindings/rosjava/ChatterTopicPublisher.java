/**
 * 
 */
package br.unicamp.cst.bindings.rosjava;

import java.net.URI;

import br.unicamp.cst.core.entities.Memory;

/**
 * @author andre
 *
 */
public class ChatterTopicPublisher extends RosTopicPublisherCodelet<std_msgs.String> {

	public ChatterTopicPublisher(java.lang.String host, URI masterURI) {
		super("ChatterTopicPublisher", "chatter", std_msgs.String._TYPE, host, masterURI);
	}

	@Override
	public void fillMessageToBePublished(Memory motorMemory, std_msgs.String message) {
		
		if(motorMemory == null) {
			return;
		}
		
		String messageData = (String) motorMemory.getI();
		
		if(messageData == null) {
			return;
		}
		
		if(message == null) {
			return;
		}
		
		message.setData(messageData);	
	}
}
