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
import java.sql.Timestamp;
import java.util.Date;


/**
 * 
 * Unit of data in memory.
 * 
 * @author andre.paraense
 * @author klaus.raizer
 */
public class MemoryObject implements Serializable
{
   
   private static final long serialVersionUID = 1L;
   
   private Long idmemoryobject;
   
   /**
	 * Information contained in the memory object.
	 */
   private volatile String info;
   
   /**
	 * Date when the data was "created".
	 */
   private Timestamp timestamp;
   
   /**
	 * Type of the information in the memory object.
	 */
   private MemoryObjectType type;
   
   /**
    * An evaluation of this memory object based on inner references
    */
   private volatile Double evaluation;
   
   private Object I;
   //private Class<Object> T;
   public String name;
   
   public MemoryObject()
   {
      
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
   public synchronized String getInfo()
   {
      return this.info;
   }
   
   /**
    *  Sets the info in memory object. 
    * @param info
    */
   public synchronized void setInfo(String info)
   {
      this.info = info;
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
   public synchronized void setI(Object info)
   {
      this.I = info;
   }
   
//   /**
//    * 
//    * @return
//    */
//   public synchronized Class<Object> getT()
//   {
//      return this.T;
//   }
   
//   /**
//    *  Sets the type of memory object - Java style. 
//    * @param nclass
//    */
//   public synchronized void setT(Class<Object> nclass)
//   {
//      this.T = nclass;
//   }
   
   /**
    *  Updates the info in memory object.  And in the process, updates the time stamp.
    * @param info
    */
   public synchronized void updateInfo(String info)
   {
	   setInfo(info);
	   Date date = new Date(); 
	   setTimestamp(new Timestamp(date.getTime()));    
   } 
   
   /**
    * 
    * @return
    */
   public synchronized Timestamp getTimestamp()
   {
      return this.timestamp;
   }
   
   /**
    * 
    * @param timestamp
    */
   public synchronized void setTimestamp(Timestamp timestamp)
   {
      this.timestamp = timestamp;
   }
   
   /**
    * @return the type
    */
   public synchronized MemoryObjectType getType()
   {
      return type;
   }

   
   /**
    * @param type the type to set
    */
   public synchronized void setType(MemoryObjectType type)
   {
      this.type = type;
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

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "MemoryObject [" + (idmemoryobject != null ? "idmemoryobject=" + idmemoryobject + ", " : "") + (info != null ? "info=" + info + ", " : "") + (timestamp != null ? "timestamp=" + timestamp + ", " : "") + (type != null ? "type=" + type + ", " : "") + (evaluation != null ? "evaluation=" + evaluation : "") + "]";
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
	   final int prime = 31;
	   int result = 1;
	   result = prime * result
	   + ((evaluation == null) ? 0 : evaluation.hashCode());
	   result = prime * result
	   + ((idmemoryobject == null) ? 0 : idmemoryobject.hashCode());
	   result = prime * result + ((info == null) ? 0 : info.hashCode());
	   result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
	   result = prime * result + ((type == null) ? 0 : type.hashCode());
	   return result;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
	   if (this == obj)
		   return true;
	   if (obj == null)
		   return false;
	   if (getClass() != obj.getClass())
		   return false;
	   MemoryObject other = (MemoryObject) obj;
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
	   if (info == null) {
		   if (other.info != null)
			   return false;
	   } else if (!info.equals(other.info))
		   return false;
	   if (timestamp == null) {
		   if (other.timestamp != null)
			   return false;
	   } else if (!timestamp.equals(other.timestamp))
		   return false;
	   if (type == null) {
		   if (other.type != null)
			   return false;
	   } else if (!type.equals(other.type))
		   return false;
	   return true;
   }


}
