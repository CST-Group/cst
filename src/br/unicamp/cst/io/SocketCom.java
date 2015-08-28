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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This class serves as a socket communication link for applications that 
 * require network access to their world/creature
 * 
 * @author klaus.raizer
 *
 */
public class SocketCom {

	private PrintWriter out;
	private BufferedReader in;
	private Socket socket;

	public SocketCom(){

	}

	/**
	 * Connect a socket to the given host and port.
	 * @param host
	 * @param port
	 */
	public void connect(String host, int port){
		//Create socket connection
		try{
			socket = new Socket(host, port);
			out = new PrintWriter(socket.getOutputStream(), 
					true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (UnknownHostException e) {
			System.out.println("Unknown host: "+host);
			System.exit(1);
		} catch  (IOException e) {
			System.out.println("No I/O");
			System.exit(1);
		}
	}

	/**
	 * Sends a string message through socket connection.
	 * @param text
	 */
	public void sendMessage(String text){

		out.println(text);
	}

	
	/**
	 * Returns the last line in buffer.
	 * Beware that calling this method with nothing in the buffer will 
	 * make the object wait for a new line to come about, most likelly
	 *  locking the thread.
	 * @return
	 */
	public String receiveMessage(){
		String textReceived="";

		//Receive text from server

		try{
			while (!in.ready());
			do {
				String line = in.readLine();
				textReceived=line; //Gets the last line in buffer
			}while(in.ready());


		} catch (IOException e){
			System.out.println("Read failed");
			System.exit(1);
		}
		return textReceived;
	}

	public void close() {
		try {
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}  

}
