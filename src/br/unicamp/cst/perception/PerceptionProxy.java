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

package br.unicamp.cst.perception;

import br.unicamp.cst.core.entities.CodeRack;
import br.unicamp.cst.core.entities.Codelet;

import java.util.ArrayList;
import java.util.List;

/**
 * Perception proxy holds the codelets responsible for interpreting data coming from sensors 
 * 
 * @author klaus.raizer
 */

public class PerceptionProxy 
{  
   private List<Codelet> listInterpreters;
   
   private CodeRack codeRack;
   
   /**
    * Default Constructor.
    * @param codeRack
    */
   public PerceptionProxy(CodeRack codeRack)
   {
	   this.codeRack = codeRack;
	   
	   listInterpreters = new ArrayList<Codelet>();     
   }
   
   /**
    * Starts all interpreters in this proxy.
    */
   public void start()
   {
      for(Codelet interpreter:listInterpreters)
      {
         interpreter.start();
      }
   }
   
   /**
    * Stops all interpreters in this proxy.
    */
   public void stop()
   {
      for(Codelet interpreter:listInterpreters)
      {
    	  interpreter.stop();
      }
   }
   
   /**
    * Add an new interpreter to this proxy.
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
   public void removeWithDelete(Codelet co)
   {
	   this.listInterpreters.remove(co);
	   if(codeRack!=null)
		   codeRack.destroyCodelet(co);
   }
   
   /**
    * Removes the given codelet from this proxy but keepts it in CodeRack.
    * @param co
    */
   public void remove(Codelet co){
	   this.listInterpreters.remove(co);
   }
   
}
