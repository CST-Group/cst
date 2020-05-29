/**
 * 
 */
package br.unicamp.cst.bindings.rosjava;

import java.net.URI;

import rosjava_test_msgs.AddTwoIntsRequest;
import rosjava_test_msgs.AddTwoIntsResponse;

/**
 * @author andre
 *
 */
public class AddTwoIntServiceClientSync extends RosServiceClientSync<AddTwoIntsRequest, AddTwoIntsResponse> {

	public AddTwoIntServiceClientSync(String host,URI masterURI) {		
		super("AddTwoIntServiceClientSync", "add_two_ints", rosjava_test_msgs.AddTwoInts._TYPE, host, masterURI);
	}

	@Override
	public void formatServiceRequest(Object[] args, AddTwoIntsRequest serviceMessageRequest) {
		try {				
			serviceMessageRequest.setA((int) (args[0]));
			serviceMessageRequest.setB((int) (args[1]));			
		}catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
