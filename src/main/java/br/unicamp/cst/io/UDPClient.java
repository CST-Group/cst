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
import java.net.InetAddress;
import java.net.SocketException;

/**
 * UDP communication via socket. Client side.
 * 
 * 
 * @author Klaus
 *
 */

public class UDPClient { 

	private String serverHostname;
	private int port;
	private DatagramSocket clientSocket;
	private InetAddress IPAddress;
	private int bufferSize;

	public UDPClient(String serverHostname, int port,int bufferSize){ 
		this.serverHostname = serverHostname;
		this.bufferSize=bufferSize;
		this.port=port;
		try {
			clientSocket = new DatagramSocket();

			try {
				IPAddress = InetAddress.getByName(serverHostname);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 

	public synchronized void  send(String sentence){
		byte[] sendData = new byte[bufferSize]; 

		sendData = sentence.getBytes();  
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port); 

		try {
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	/**
	 * Receives information from server as an array of bytes
	 * @return
	 */
	public synchronized byte[] receiveByteArray(){

		byte[] receiveData = new byte[bufferSize];  //TODO Must be dynamic

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); 
		try {
			clientSocket.receive(receivePacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

//		String sentence = new String(receivePacket.getData()); 
		byte[] sentence = receivePacket.getData();
		return sentence;
	}
	/**
	 * Receives information from client as a String
	 * @return
	 */
	public synchronized String receive(){

		byte[] receiveData = new byte[bufferSize]; 
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); 
		String modifiedSentence="";

		try {
			clientSocket.receive(receivePacket);
			modifiedSentence = new String(receivePacket.getData()); 


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return modifiedSentence; 
	}
	public synchronized void close(){
		clientSocket.close(); 
	}
} 

