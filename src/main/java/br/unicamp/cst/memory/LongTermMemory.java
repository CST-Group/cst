/*******************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     K. Raizer, A. L. O. Paraense, R. R. Gudwin - initial API and implementation
 *     E. M. Froes - documentation
 ******************************************************************************/

package br.unicamp.cst.memory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.RawMemory;



/**
 * This long term memory is responsible for providing access to stored knowledge
 * @author klaus
 *
 */
public class LongTermMemory
{
	private ArrayList<MemoryObject> ltmMOs = new ArrayList<MemoryObject>();

	private static String path = "LongTermMemory/";
	
	private RawMemory rawMemory;

	/**
	 * Default constructor.
         * @param rawMemory
	 */
	public LongTermMemory(RawMemory rawMemory)
	{

		this.rawMemory = rawMemory;
		//learnedPropositions = new ConcurrentHashMap<String,MemoryObject>();
		boolean success = (new File("LongTermMemory")).mkdirs();


	}

        /** 
         * This method receive a object "File" as parameter and return the contents of the file in a byte array.
         * @param file
         * @return byte array.
         * @throws IOException 
         */
	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int)length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "+file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

	/**
	 * 
	 * Avoids cloning.
	 */
	public Object clone() throws CloneNotSupportedException{
		throw new CloneNotSupportedException();
	}


	/**
	 *  Stores Information of a given type in long term memory.
	 *  /TODO At this moment, long term memory is persisted in a json file in hard disk. This should be changed to use a DB such as postgre or mysql. 
	 *  
	 * @param mo
	 */
	public void learn(MemoryObject mo) {
		MemoryObject hasMemory=this.checksIfMemoryExists(mo.getName(),mo.getI());

		if(hasMemory==null){ //doesn't exist yet
			int endIndex = ((String)mo.getI()).length();
			if(endIndex>8){endIndex=8;}
			String filename=mo.getName()+"_"+((String)mo.getI()).substring(0, endIndex)+"_"+mo.getTimestamp().toString().replace(":", "-");
			String extension=".mo";
			//SERIALIZE
	
			try {
				// Serialize to a file
				ObjectOutput out = new ObjectOutputStream(new FileOutputStream(path+filename+extension));
				out.writeObject(mo);
				out.close();
	
				// Serialize to a byte array
				ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
				out = new ObjectOutputStream(bos) ;
				out.writeObject(mo);
				out.close();
	
				// Get the bytes of the serialized object
				byte[] buf = bos.toByteArray();
			} catch (IOException e) {
				System.out.println("Couldn't create file with serialized memory object.");
			}
		}
	}

	
	
	/**
	 * Searches in persisted long term memory if this memory object already exists.
	 * The criteria are its type and info.
	 * @param type
         * @param info
	 * @return the existing memory object, or null if there aren't any.
	 */
	private MemoryObject checksIfMemoryExists(String type, Object info) {
		MemoryObject ltmMO=null;
		File pathName = new File(path); // gets the element at the index of the List 
		String[] fileNames = pathName.list();  // lists all files in the directory
		if(fileNames!=null){
		for(int i = 0; i < fileNames.length; i++) { 
			File f = new File(pathName.getPath(), fileNames[i]); // getPath converts abstract path to path in String, 
			if (!f.isDirectory()) { 
				//             System.out.println(f.getCanonicalPath()); 
				MemoryObject recoveredMO=this.deserializeMO(f);
				if(type.equalsIgnoreCase(recoveredMO.getName()) && info.equals(recoveredMO.getI())){
					ltmMO=recoveredMO;
					break;
				}
			}
		} 
		if(ltmMO!=null)
		{
			if(rawMemory!=null)
				rawMemory.addMemoryObject(ltmMO);
		}
		
		}
		return ltmMO;
	}
        
        
	/**
	 *  Reads a memory object from disk.
	 * @param file
	 * @return memory object.
	 */
	private MemoryObject deserializeMO(File file){
		//DESERIALIZE
		MemoryObject mo2=null;
		try {
			// Deserialize from a file
			//File file = new File(path+filename+extension);
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			// Deserialize the object
			mo2 = (MemoryObject) in.readObject();
			in.close();

			// Get some byte array data
			byte[] bytes = getBytesFromFile(file);
			// see Reading a File into a Byte Array for the implementation of this method

			// Deserialize from a byte array
			in = new ObjectInputStream(new ByteArrayInputStream(bytes));
			mo2 = (MemoryObject) in.readObject();
			in.close();

		} catch (ClassNotFoundException e) {
			System.out.println("Could not deserialize memory object from long term memory.");
		} catch (IOException e) {
			System.out.println("Could not deserialize memory object from long term memory.");
		}
		return mo2;

	}

	/**
	 *  Retrieves from long term memory a given type and info of memory object.
	 *  It first looks for it in the memory objects already loaded in ram LTM. 
	 *  If it fails to find it, it then looks for it on disk.
	 * @param type
	 * @param info
	 * @return memory object.
	 */
	public MemoryObject retrieve(String type, String info) {
		MemoryObject retrievedMO=null;
		boolean isInRAM=false;
		//Check if has already been loaded
		for(MemoryObject ramMO:this.ltmMOs){
			if(ramMO.getName().equalsIgnoreCase(type) && ramMO.getI().equals(info)){
				retrievedMO=ramMO;
				isInRAM=true;
				break;
			}
		}
		
		if(!isInRAM){//Couldn't find in ram, look for it in disk
				retrievedMO=this.checksIfMemoryExists(type,info);			
				if(retrievedMO!=null){
					ltmMOs.add(retrievedMO);
				}
		}
		return retrievedMO;
	}
        
	/**
	 * WARNING: 
	 * Clears everything from LTM, both from ram and disk.
	 * Use with extreme care!
	 */
	public void totalClearLTM() {
		File f = new File(path); // gets the element at the index of the List 
		File[] lista = f.listFiles();
		//System.out.println(lista);
		if(lista!=null){
		 for (File c : f.listFiles()) //Cleans up directory for other tests
		      c.delete();
		}
	}
        
	/**
	 * Prints all active content in ltm.
	 */
	public void printLTM(){
		System.out.println("LTM Active Content: "+ltmMOs);
	}
        
	/** 
	 * Clears LTM from memory.
	 */
	public void shutDown() {
		this.ltmMOs.clear();
		
	}
	
}
