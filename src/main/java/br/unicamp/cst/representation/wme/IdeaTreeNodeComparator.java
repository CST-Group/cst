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

import br.unicamp.cst.util.TreeElement;
import java.util.Comparator;

/**
 *
 * @author rgudwin
 */
public class IdeaTreeNodeComparator implements Comparator {
    
        private final boolean order;

        public IdeaTreeNodeComparator()
        {
            this(true);
        }

        public IdeaTreeNodeComparator(boolean order)
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
            IdeaTreeNode wo1 = (IdeaTreeNode) o1;
            IdeaTreeNode wo2 = (IdeaTreeNode) o2;
            TreeElement te1 = wo1.getTreeElement();
            TreeElement te2 = wo2.getTreeElement();
            int try1 = getNumber(te1.getName());
            int try2 = getNumber(te2.getName());
            System.out.println(te1.getName()+": "+try1+" "+te2.getName()+": "+try2);
            if (try1 != Integer.MIN_VALUE && try2 != Integer.MIN_VALUE) {
                if (try1 > try2) return(1);
                else return(-1);
            }
            if (te1.getName().startsWith("operator") && !te2.getName().startsWith("operator")) return(1);
            else if(!te1.getName().startsWith("operator") && te2.getName().startsWith("operator")) return(-1); 
            else if (te1.getIcon() == te2.getIcon() || te1.getIcon() == 2 && te2.getIcon() == 18 ||
                     te1.getIcon() == 18 && te2.getIcon() == 2) {
                return te1.getNamePlusValuePlusId().compareTo(te2.getNamePlusValuePlusId());
            }    
            else if (te1.getIcon() == 2 && te2.getIcon() == 5 || te1.getIcon() == 2 && te2.getIcon() == 18 || te1.getIcon() == 18 && te2.getIcon() == 5 ) {
                return(-1);
            }
            else return(1);
        }    
}
