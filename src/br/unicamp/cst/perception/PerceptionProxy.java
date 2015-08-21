/*******************************************************************************
 * Copyright (c) 2012 K. Raizer, A. L. O. Paraense, R. R. Gudwin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     K. Raizer, A. L. O. Paraense, R. R. Gudwin - initial API and implementation
 ******************************************************************************/
package br.unicamp.cst.perception;

import java.util.ArrayList;
import java.util.List;

import br.unicamp.cst.core.entities.CodeRack;
import br.unicamp.cst.core.entities.Codelet;

/**
 * Perception proxy holds the codelets responsible for interpreting data coming from sensors 
 * 
 * @author klaus.raizer
 */

public class PerceptionProxy {
   
   private List<Codelet> listInterpreters;
   
   
   /**
    * Singleton instance
    */
   private static PerceptionProxy instance;
   
   public PerceptionProxy()
   {
	   listInterpreters = new ArrayList<Codelet>();     
   }
   
   /**
    * 
    * @return the singleton instance of BodyProxy
    */
   public synchronized static PerceptionProxy getInstance()
   {
      if(instance==null)
      {
         instance = new PerceptionProxy();         
      }

      return instance;
   }
   /**
    * Starts all interpreters in this proxy
    */
   public void start()
   {
      for(Codelet interpreter:listInterpreters)
      {
         interpreter.start();
      }
   }
   /**
    * Stops all interpreters in this proxy
    */
   public void stop()
   {
      for(Codelet interpreter:listInterpreters)
      {
    	  interpreter.stop();
      }
   }
   /**
    * Add an new interpreter to this proxy
    * @param interpreter to be added to this proxy
    */
   public void add(Codelet interpreter)
   {
      listInterpreters.add(interpreter);
   }
   
   /**
    * Removes the given codelet from this proxy and destroys it in code rack.
    * @param co
    */
   public void removeWithDelete(Codelet co){
	   this.listInterpreters.remove(co);
	   CodeRack.getInstance().destroyCodelet(co);
   }
   /**
    * Removes the given codelet from this proxy but keepts it in CodeRack.
    * @param co
    */
   public void remove(Codelet co){
	   this.listInterpreters.remove(co);
   }
   
}
