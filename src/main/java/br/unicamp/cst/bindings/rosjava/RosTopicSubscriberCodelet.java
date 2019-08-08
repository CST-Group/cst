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
	
	public RosTopicSubscriberCodelet(String nodeName, String topic,String messageType, String host, URI masterURI) {

		super();
		this.nodeName = nodeName;
		this.topic = topic;
		this.messageType = messageType;
		setName(nodeName);
		
		nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
		nodeConfiguration = NodeConfiguration.newPublic(host,masterURI);
		
		startRosNode();
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
		proc(message,sensoryMemory);

	}
	
	public abstract void proc(T message, Memory sensoryMemory);
	
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
