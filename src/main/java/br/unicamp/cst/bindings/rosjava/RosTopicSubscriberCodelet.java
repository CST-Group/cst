/**
 * 
 */
package br.unicamp.cst.bindings.rosjava;

import java.net.URI;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Subscriber;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

/**
 *  Wrapper binding a RosJava Topic Subscriber and a Codelet.
 * The object of this class is a hybrid of Codelet and ROS Topic Subscriber,
 * not only operating in both lifetime cycles of each one but also having these
 * lifetime cycle coupled and integrated.
 * 
 * @author andre
 *
 * @param <T> The ROS Message Type - Ex: std_msgs.String from ROS standard messages
 */
public abstract class RosTopicSubscriberCodelet<T> extends Codelet implements NodeMain {
	
	protected String nodeName;
	
	protected String topic;
	
	protected String messageType;
	
	protected T message;
	
	protected Memory sensoryMemory;
	
	protected NodeMainExecutor nodeMainExecutor;
	
	protected NodeConfiguration nodeConfiguration;
	
	/**
	 * Constructor for the RosTopicSubscriberCodelet.
	 * 
	 * @param nodeName the name of this ROS node.
	 * @param topic the name of the ROS topic this node will be subscribing to.
	 * @param messageType the ROS message type. Ex: "std_msgs.String"
	 * @param host the host IP where to run. Ex: "127.0.0.1" 
	 * @param masterURI the URI of the master ROS node. Ex: new URI("http://127.0.0.1:11311")
	 */
	public RosTopicSubscriberCodelet(String nodeName, String topic,String messageType, String host, URI masterURI) {

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

		if(sensoryMemory == null)
			sensoryMemory = this.getOutput(nodeName, index);	
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
		fillMemoryWithReceivedMessage(message,sensoryMemory);

	}
	
	/**
	 * Fills the sensory memory with the received message through the subscription to the ROS topic.
	 * @param message the message received through the subscription to the ROS topic.
	 * @param sensoryMemory the memory to store the message received.
	 */
	public abstract void fillMemoryWithReceivedMessage(T message, Memory sensoryMemory);
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of(nodeName);
	}
	
	@Override
	public void onStart(ConnectedNode connectedNode) {
	    Subscriber<T> subscriber = connectedNode.newSubscriber(topic, messageType);
	    subscriber.addMessageListener(new MessageListener<T>() {
	      @Override
	      public void onNewMessage(T newMessage) {
	        message = newMessage;
	      }
	    });
	}

	@Override
	public void onShutdown(Node node) {
		//empty
	}

	@Override
	public void onShutdownComplete(Node node) {
		//empty
	}
	
	@Override
	public void onError(Node node, Throwable throwable) {
		//empty
	}
}
