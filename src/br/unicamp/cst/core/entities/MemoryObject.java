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

import java.io.Serializable;


/** 
 * Unit of data in memory.
 * 
 * @author andre.paraense
 * @author klaus.raizer
 */
public class MemoryObject implements Memory,Serializable
{
   
   private static final long serialVersionUID = 1L;
   
   private Long idmemoryobject;
   
   /**
	 * Date when the data was "created" in milliseconds 
	 */
   private Long timestamp;
   
   /**
    * An evaluation of this memory object based on inner references
    */
   private volatile Double evaluation;
   
   /**
	 * Information contained in the memory object.
	 */
   private volatile Object I;

   /**
    * Type of the memory object
    */
   private String name;
   
   public MemoryObject()
   {
      evaluation = 0.0d;
   }
   
   /**
    * 
    * @return
    */
   public synchronized Long getIdmemoryobject()
   {
      return this.idmemoryobject;
   }
   
   /**
    * 
    * @param idmemoryobject
    */
   public synchronized void setIdmemoryobject(Long idmemoryobject)
   {
      this.idmemoryobject = idmemoryobject;
   }
   
   /**
    * 
    * @return
    */
   public synchronized Object getI()
   {
      return this.I;
   }
   
   /**
    *  Sets the info in memory object - Java Style. 
    * @param info
    */
   public synchronized int setI(Object info)
   {
      this.I = info;      
	  setTimestamp(System.currentTimeMillis());  
	  
	  return -1;
   }
   
   /**
    *  This method is deprecated after v0.1. For the time being, it has been kept only for backward compatibility. Use the {@link #setI(Object info) setI} method instead.
    * @param info
    */
   @Deprecated
   public synchronized void updateI(Object info)
   {
	   setI(info); 
   } 
   
   /**
    * 
    * @return
    */
   public synchronized Long getTimestamp()
   {
      return this.timestamp;
   }
   
   /**
    * 
    * @param timestamp
    */
   public synchronized void setTimestamp(Long timestamp)
   {
      this.timestamp = timestamp;
   }
   
   /**
    * @return the type
    */
   public synchronized String getName()
   {
      return name;
   }

   
   /**
    * @param name the type to set
    */
   public synchronized void setType(String name)
   {
      this.name = name;
   }

   
   /**
    * @return the evaluation
    */
   public synchronized Double getEvaluation()
   {
      return evaluation;
   }

   
   /**
    * @param evaluation the evaluation to set
    */
   public synchronized void setEvaluation(Double evaluation)
   {
      this.evaluation = evaluation;
   }

   @Override
   public String toString() {
	   return "MemoryObject [idmemoryobject=" + idmemoryobject + ", timestamp=" + timestamp + ", evaluation=" + evaluation
			   + ", I=" + I + ", name=" + name + "]";
   }

   @Override
   public int hashCode() {
	   final int prime = 31;
	   int result = 1;
	   result = prime * result + ((I == null) ? 0 : I.hashCode());
	   result = prime * result + ((evaluation == null) ? 0 : evaluation.hashCode());
	   result = prime * result + ((idmemoryobject == null) ? 0 : idmemoryobject.hashCode());
	   result = prime * result + ((name == null) ? 0 : name.hashCode());
	   result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
	   return result;
   }

   @Override
   public boolean equals(Object obj) {
	   if (this == obj)
		   return true;
	   if (obj == null)
		   return false;
	   if (getClass() != obj.getClass())
		   return false;
	   MemoryObject other = (MemoryObject) obj;
	   if (I == null) {
		   if (other.I != null)
			   return false;
	   } else if (!I.equals(other.I))
		   return false;
	   if (evaluation == null) {
		   if (other.evaluation != null)
			   return false;
	   } else if (!evaluation.equals(other.evaluation))
		   return false;
	   if (idmemoryobject == null) {
		   if (other.idmemoryobject != null)
			   return false;
	   } else if (!idmemoryobject.equals(other.idmemoryobject))
		   return false;
	   if (name == null) {
		   if (other.name != null)
			   return false;
	   } else if (!name.equals(other.name))
		   return false;
	   if (timestamp == null) {
		   if (other.timestamp != null)
			   return false;
	   } else if (!timestamp.equals(other.timestamp))
		   return false;
	   return true;
   }
}
