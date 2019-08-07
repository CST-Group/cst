/**
 * 
 */
package br.unicamp.cst.bindings.rosjava;

import java.net.URI;

import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

/**
 * 
 * @author andre
 *
 * @param <S> Service Message Request - Ex: AddTwoIntsRequest
 * @param <T> Service Message Response - Ex: AddTwonIntsResponse
 */
public abstract class RosServiceClientCodelet<S,T> extends Codelet implements NodeMain {
	
	protected String nodeName;
	
	protected String service;
	
	protected String messageServiceType;
	
	protected Memory motorMemory;
	
	protected S serviceMessageRequest;
	
	protected ServiceClient<S, T> serviceClient;
	
	public RosServiceClientCodelet(String nodeName, String service, String messageServiceType, String host, URI masterURI) {

		super();
		this.nodeName = nodeName;
		this.service = service;
		this.messageServiceType = messageServiceType;
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
		if(motorMemory != null && motorMemory.getI() != null) {
			if(serviceMessageRequest != null) {
				formatServiceRequest(motorMemory, serviceMessageRequest);
				callService();				
			}
		}
	}
	
	public abstract void formatServiceRequest(Memory motorMemory, S serviceMessageRequest);
	
	public void callService() {
		if(serviceClient != null && serviceMessageRequest != null) {
		    serviceClient.call(serviceMessageRequest, new ServiceResponseListener<T>() {
			      @Override
			      public void onSuccess(T response) {			    	  
			    	  if(response != null) {
			    		  processServiceResponse(response);
			    		  motorMemory.setI(null);
			    	  }						
			      }

			      @Override
			      public void onFailure(RemoteException e) {
			        throw new RosRuntimeException(e);
			      }
			    });		   
		}
	}
	
	public abstract void processServiceResponse(T serviceMessageResponse);
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of(nodeName);
	}

	@Override
	public void onStart(ConnectedNode connectedNode) {	    
	    try {
	      serviceClient = connectedNode.newServiceClient(service, messageServiceType);
	    } catch (ServiceNotFoundException e) {
	      throw new RosRuntimeException(e);
	    }
	    serviceMessageRequest = serviceClient.newMessage();
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
