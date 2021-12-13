/***********************************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * K. Raizer, A. L. O. Paraense, E. M. Froes, R. R. Gudwin - initial API and implementation
 ***********************************************************************************************/
package br.unicamp.cst.representation.wme;

import java.util.Comparator;

/**
 *
 * @author rgudwin
 */
public class IdeaComparator implements Comparator {
    
        private final boolean order;

        public IdeaComparator()
        {
            this(true);
        }

        public IdeaComparator(boolean order)
        {
            this.order = order;
        }
        
        int getInt(String s) {
            int n;
            try { 
               n = Integer.parseInt(s);
            } catch(Exception e) {
               return(Integer.MIN_VALUE);     
            }
            return(n); 
        }
        
        int getNumber(String name) {
            String[] splitter = name.split("\\[");
            if (splitter.length > 1) {
                return(getInt(splitter[1].split("\\]")[0].trim()));
            }
            else return Integer.MIN_VALUE;
        }

        @Override
        public int compare(Object o1, Object o2)
        {
            Idea i1 = (Idea) o1;
            Idea i2 = (Idea) o2;
            int try1 = getNumber(i1.getName());
            int try2 = getNumber(i2.getName());
            if (try1 != Integer.MIN_VALUE && try2 != Integer.MIN_VALUE) {
                if (try1 > try2) return(1);
                else return(-1);
            }
            if (i1.getName().startsWith("operator") && !i2.getName().startsWith("operator")) return(1);
            else if(!i1.getName().startsWith("operator") && i2.getName().startsWith("operator")) return(-1); 
            else if (i1.getType() == i2.getType() || i1.getType() == 0 && i2.getType() == 2 ||
                     i1.getType() == 2 && i2.getType() == 0) {
                return i1.getName().compareTo(i2.getName());
            }    
            else if (i1.getType() == 1 && i2.getType() == 0 || i1.getType() == 1 && i2.getType() == 2 || i1.getType() == 2 && i2.getType() == 0 ) {
                return(1);
            }
            else return(-1);
        }    
}
