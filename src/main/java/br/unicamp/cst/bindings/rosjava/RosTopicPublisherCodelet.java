/**
 * 
 */
package br.unicamp.cst.bindings.rosjava;

import java.net.URI;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

/**
 * @author andre
 *
 */
public abstract class RosTopicPublisherCodelet<T> extends Codelet implements NodeMain {
	
	protected String nodeName;
	
	protected String topic;
	
	protected String messageType;
	
	protected Memory motorMemory;
	
	protected T message;
	
	public RosTopicPublisherCodelet(String nodeName, String topic, String messageType, String host, URI masterURI) {

		super();
		this.nodeName = nodeName;
		this.topic = topic;
		this.messageType = messageType;
		setName(nodeName);
		
		NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
		NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(host,masterURI);
		nodeMainExecutor.execute(this, nodeConfiguration);
	}
	
	@Override
	public void accessMemoryObjects() {
		int index = 0;

		if(motorMemory == null)
			motorMemory = this.getInput(nodeName, index);
	}

	@Override
	public void calculateActivation() {
		try{
			setActivation(0.0d);
		} catch (CodeletActivationBoundsException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public void proc() {
		proc(message,motorMemory);
	}
	
	public abstract void proc(T message, Memory motorMemory);
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of(nodeName);
	}
	
	@Override
	public void onStart(ConnectedNode connectedNode) {
	    final Publisher<T> publisher = connectedNode.newPublisher(topic, messageType);
	    message = publisher.newMessage();

		    connectedNode.executeCancellableLoop(new CancellableLoop() {			
				@Override
				protected void loop() throws InterruptedException {
					publisher.publish(message);
				}
			});
		
	}

	@Override
	public void onShutdown(Node node) {
		// empty
	}

	@Override
	public void onShutdownComplete(Node node) {
		// empty
	}
	
	@Override
	public void onError(Node node, Throwable throwable) {
		// empty		
	}
}
