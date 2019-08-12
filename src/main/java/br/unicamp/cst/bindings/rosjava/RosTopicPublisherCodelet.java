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
 * Wrapper binding a RosJava Topic Publisher and a Codelet.
 * The object of this class is a hybrid of Codelet and ROS Topic Publisher,
 * not only operating in both lifetime cycles of each one but also having these
 * lifetime cycle coupled and integrated.
 * 
 * @author andre
 * 
 * @param <T> The ROS Message Type - Ex: std_msgs.String from ROS standard messages
 */
public abstract class RosTopicPublisherCodelet<T> extends Codelet implements NodeMain {
	
	protected String nodeName;
	
	protected String topic;
	
	protected String messageType;
	
	protected Memory motorMemory;
	
	protected T message;
	
	protected NodeMainExecutor nodeMainExecutor;
	
	protected NodeConfiguration nodeConfiguration;
	
	/**
	 * Constructor for the RosTopicPublisherCodelet.
	 * 
	 * @param nodeName the name of this ROS node.
	 * @param topic the name of the ROS topic this node will be publishing to.
	 * @param messageType the ROS message type. Ex: "std_msgs.String".
	 * @param host the host IP where to run. Ex: "127.0.0.1".
	 * @param masterURI the URI of the master ROS node. Ex: new URI("http://127.0.0.1:11311").
	 */
	public RosTopicPublisherCodelet(String nodeName, String topic, String messageType, String host, URI masterURI) {

		super();
		this.nodeName = nodeName;
		this.topic = topic;
		this.messageType = messageType;
		setName(nodeName);
		
		nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
		nodeConfiguration = NodeConfiguration.newPublic(host,masterURI);
	}
	
	@Override
	public synchronized void start() {
		startRosNode();
		super.start();
	}
	
	@Override
	public synchronized void stop() {
		
		stopRosNode();
		super.stop();
	}
	
	private void startRosNode() {
		nodeMainExecutor.execute(this, nodeConfiguration);
	}
	
	private void stopRosNode() {
		nodeMainExecutor.shutdownNodeMain(this);
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
		fillMessageToBePublished(motorMemory,message);
	}
	
	/**
	 * Fill the message to be published in the ROS topic with the contents of the motor memory.
	 * @param motorMemory has the content to fill the message to be published with.
	 * @param message the message to be published, which should be filled with the contents of the motor memory.
	 */
	public abstract void fillMessageToBePublished(Memory motorMemory, T message);
	
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
