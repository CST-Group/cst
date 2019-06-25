/*******************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     K. Raizer, A. L. O. Paraense, R. R. Gudwin - initial API and implementation
 ******************************************************************************/

package br.unicamp.cst.io;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServer { 
	private int port;
	private int bufferSize;
	private DatagramSocket serverSocket;
	public UDPServer(int port, int bufferSize){ 
		this.bufferSize=bufferSize;
		this.port=port; 
		try {
			this.serverSocket = new DatagramSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 




	} 
	/**
	 * Receives information from server as a String
	 * @return
	 */
	public synchronized String receive(){
//		System.out.println("inside receive");
		byte[] receiveData = new byte[bufferSize];  //TODO Must be dynamic

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); 
		try {
			serverSocket.receive(receivePacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		String sentence = new String(receivePacket.getData()); 
//		System.out.println("exited received");
		return sentence;
	}


	/**
	 * Receives information from server as an array of bytes
	 * @return
	 */
	public synchronized byte[] receiveByteArray(){
		byte[] receiveData = new byte[bufferSize];  //TODO Must be dynamic

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); 
		try {
			serverSocket.receive(receivePacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		//		String sentence = new String(receivePacket.getData()); 
		byte[] sentence = receivePacket.getData();
		return sentence;
	}
}  

