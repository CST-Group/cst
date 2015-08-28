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

package br.unicamp.cst.core.entities;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 
 * The Raw Memory contains all memory objects in the system.
 * 
 * @author andre.paraense
 * @author klaus.raizer
 *
 */
public class RawMemory
{
   /**
    * List of all memory objects in the system
    */
   private List<MemoryObject> allMemoryObjects;
   
   /**
    * Default constructor
    */
   public RawMemory()
   {
      allMemoryObjects =  Collections.synchronizedList(new ArrayList<MemoryObject>());
   }

   /**
    * @return the allMemoryObjects
    */
   public synchronized List<MemoryObject> getAllMemoryObjects()
   {
	   synchronized(allMemoryObjects)
	   {
		   return allMemoryObjects; 
	   }   
   }
/**
 * Returns a list of all memory objects in raw memory of a given type
 * @param type of memory object
 * @return list of MOs of a given type
 */
   public synchronized List<MemoryObject> getAllOfType(int type)
   {
	   List<MemoryObject> listOfType=new ArrayList<MemoryObject>();
	   
	   synchronized(allMemoryObjects)
	   {
		   for(MemoryObject mo:this.allMemoryObjects)
		   {
			   if(mo.getType().equals(type))
			   {
				   listOfType.add(mo);
			   }
		   } 
	   }
	   	   
	return listOfType;
	   
   }
   
   
   /**
    * @param allMemoryObjects the allMemoryObjects to set
    */
   public synchronized void setAllMemoryObjects(List<MemoryObject> allMemoryObjects)
   {
	   synchronized(this.allMemoryObjects)
	   {
		   this.allMemoryObjects = allMemoryObjects;
	   }     
   }

   /**
    * Print Raw Memory contents
    */
   public synchronized void printContent()
   {
	   synchronized(allMemoryObjects)
	   {
		   for(MemoryObject mo : allMemoryObjects)
		      {
		         System.out.println(mo.toString());
		      }  
	   }    
   }

   /**
    * Adds a new MemoryObject to the Raw Memory
    * 
    * @param mo memory object to be added
    */
   public synchronized void addMemoryObject(MemoryObject mo)
   {
	   synchronized(allMemoryObjects)
	   {
		      allMemoryObjects.add(mo);
	   }     
   }

   /**
    * Creates a new MemoryObject and adds it to the Raw Memory, using provided info and type
    * 
    * @param type memory object type
    * @param info memory object info
    * @return mo created MemoryObject
    */
   public synchronized MemoryObject createMemoryObject(MemoryObjectType type, String info)
   {
	   // memory object to be added to rawmemory
       MemoryObject mo = new MemoryObject();
       Date date = new Date();         
       mo.setInfo(info);
       mo.setType(type);
       mo.setTimestamp(new Timestamp(date.getTime()));
       mo.setEvaluation(0.5d);
       mo.name = "";

       // adding the new object to raw memory
       this.addMemoryObject(mo);
       return mo;
      
   }
   /**
    * Creates a new MemoryObject (Java style) and adds it to the Raw Memory, using provided info and type
    * 
    * @param type memory object type
    * @param info memory object info
    * @return mo created MemoryObject
    */
   public synchronized MemoryObject createMemoryObject(String name, Class<Object> type, Object info)
   {
	   // memory object to be added to rawmemory
       MemoryObject mo = new MemoryObject();
       Date date = new Date();         
       mo.setI(info);
       mo.setT(type);
       mo.setInfo("");
       mo.setTimestamp(new Timestamp(date.getTime()));
       mo.setEvaluation(0.5d);
       mo.name = name;

       // adding the new object to raw memory
       this.addMemoryObject(mo);
       return mo;
   }
   
   public synchronized MemoryObject createMemoryObject(String name) {
       return createMemoryObject(name, Object.class, null);
   }
   
   
   /**
    * Destroys a given memory object from raw memory
    * @param mo
    */
   public synchronized void destroyMemoryObject(MemoryObject mo)
   {
	   synchronized(allMemoryObjects)
	   {
		   allMemoryObjects.remove(mo);
	   }  
   }
   
   /**
    * 
    * @return size of Raw Memory
    */
   public synchronized int size()
   { 
	   synchronized(allMemoryObjects)
	   {
		   return allMemoryObjects.size();
	   }     
   }
   

   /**
    * Removes all memory objects from RawMemory. Sets all memory objects to null;
    */
   public void shutDown() 
   {
	   synchronized(allMemoryObjects)
	   {
		   for(MemoryObject mo: this.getAllMemoryObjects())
		   {
			   mo=null;
		   }
		   this.allMemoryObjects.clear();
	   }	
   }  
}
