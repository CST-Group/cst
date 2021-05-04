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

import br.unicamp.cst.util.TestComplexMemoryObjectInfo;
import java.util.Date;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.junit.Test;

public class TestWMNode {
    
    public Idea initialize() {
        Idea node = new Idea("Test","",0);
        node.add(new Idea("child1","",0)).add(new Idea("subchild1",3.14,1)).add(new Idea("subsubchild1","whatthe..."));
        double variable[] = new double[3];
        node.add(new Idea("child2","I2",0)).add(new Idea("array",new double[]{3.4, 2.2, 1.23}));
        node.add(new Idea("child3",3.1416d,1));
        node.add(new Idea("child4",null,2));
        System.out.println(node.toStringFull());
        return(node);
    }
    
    void print(String s,List<Object> o) {
        for (Object oo : o)
            if (oo != null)
                System.out.println("get(\""+s+"\") : "+oo.toString()+" -> "+oo.getClass().getSimpleName());
            else System.out.println("get(\""+s+"\") : "+"null");
    }
    
    @Test 
    public void testGet() {
        Idea n = initialize();
        List<Object> o = n.get("child1");
        print("child1",o);
        o = n.get("child1",true);
        print("child1",o);
        o = n.get("child1.subchild1");
        print("child1.subchild1",o);
        o = n.get("child1.subchild1",true);
        print("child1.subchild1",o);
        o = n.get("child1.subchild1.subsubchild1");
        print("child1.subchild1.subsubchild1",o);
        o = n.get("child1.subchild1.subsubchild1",true);
        print("child1.subchild1.subsubchild1",o);
        o = n.get("child2");
        print("child2",o);
        o = n.get("child2",true);
        print("child2",o);
        o = n.get("child2.array");
        print("child2.array",o);
        o = n.get("child2.array",true);
        print("child2.array",o);
        o = n.get("child3");
        print("child3",o);
        o = n.get("child3",true);
        print("child3",o);
        o = n.get("child4");
        print("child4",o);     
        o = n.get("child4",true);
        print("child4",o);     
    }
    
    @Test 
    public void testWMNode() {
        Idea ln = new Idea("a");
        Idea ln2 = new Idea("b");
        Idea ln3 = new Idea("c");
        ln.add(ln2);
        ln2.add(ln3);
        Idea v1 = new Idea("v","3");
        ln.add(v1);
        ln2.add(v1);
        ln3.add(v1);
        System.out.println(ln.toStringFull());
        Date d = new Date();
        Idea complexnode = new Idea("teste");
        complexnode.addObject(d,"date");
        DefaultMutableTreeNode dt = new DefaultMutableTreeNode(d);
        complexnode.addObject(dt, "defaultMutableTreeNode");
        complexnode.addObject(complexnode,"recursion");
        IdeaPanel wmp = new IdeaPanel(new Idea("Root","[S1]",0),true);
        complexnode.addObject(wmp, "wmpanel");
        TestComplexMemoryObjectInfo ttt = new TestComplexMemoryObjectInfo();
        complexnode.addObject(ttt,"complex");
        System.out.println("Finished creation of objects");
        System.out.println(complexnode.toStringFull());
        
        
    }
    
}