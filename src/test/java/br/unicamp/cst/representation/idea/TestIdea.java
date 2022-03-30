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
package br.unicamp.cst.representation.idea;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;


import br.unicamp.cst.core.profiler.TestComplexMemoryObjectInfo;
import static org.junit.Assert.assertEquals;
import org.junit.Test;


public class TestIdea {
    
    List<TestComplexMemoryObjectInfo> l;
    
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
    
    void print(String s,Idea o) {
        if (o != null) {
            if (o.getValue() != null) System.out.println("get(\""+s+"\") : "+o.getName()+" -> "+o.getValue().toString());
            else System.out.println("get(\""+s+"\") : "+o.getName()+" -> null");
        }
        else System.out.println("get(\""+s+"\") : "+"null");
    }
    
    @Test 
    public void testGet() {
        System.out.println("\n Starting the testGet ...");
        Idea n = initialize();
        Idea o = n.get("child1");
        System.out.println();
        print("child1",o);
        o = n.get("child1.subchild1");
        print("child1.subchild1",o);
        o = n.get("child1.subchild1.subsubchild1");
        print("child1.subchild1.subsubchild1",o);
        o = n.get("child2");
        print("child2",o);
        o = n.get("child2.array");
        print("child2.array",o);
        o = n.get("child3");
        print("child3",o);
        o = n.get("child4");
        print("child4",o);     
    }
    
    @Test 
    public void testIdea() {
        System.out.println("\n Starting the testIdea ...");
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
        Idea complexnode = Idea.createIdea("complexnode","",0);
        System.out.println("Adding the object date within complexnode");
        complexnode.addObject(d,"complexnode.date");
        DefaultMutableTreeNode dt = new DefaultMutableTreeNode(d);
        System.out.println("Adding the object defaultMutableTreeNode within complexnode");
        complexnode.addObject(dt, "teste.defaultMutableTreeNode");
        System.out.println("Adding the object complexnode within itself with the name recursion");
        complexnode.addObject(complexnode,"complexnode.recursion");
        //IdeaPanel wmp = new IdeaPanel(Idea.createIdea("Root","[S1]",0),true);
        System.out.println("Adding the object wmpanel within complexnode");
        //complexnode.addObject(wmp, "complexnode.wmpanel");
        TestComplexMemoryObjectInfo ttt = new TestComplexMemoryObjectInfo();
        ttt.testbyte = 10;
        ttt.testshort = 0xa;
        ttt.testlong = 23;
        ttt.testint = 3;
        ttt.testfloat = 3.12f;
        ttt.testdouble = 3.21;
        ttt.testboolean = true;
        for (int i=0;i<ttt.testdoublearray.length;i++)
            ttt.testdoublearray[i] = i*0.1;
        for (int i=0;i<ttt.testfloatarray.length;i++)
            ttt.testfloatarray[i] = i*0.1f;
        for (int i=0;i<ttt.testlongarray.length;i++)
            ttt.testlongarray[i] = i*2;
        for (int i=0;i<ttt.testintarray.length;i++)
            ttt.testintarray[i] = i*2;
        for (short i=0;i<ttt.testshortarray.length;i++)
            ttt.testshortarray[i] = i;
        for (byte i=0;i<ttt.testbytearray.length;i++)
            ttt.testbytearray[i] = i;
        System.out.println("Adding the object complex within complexnode");
        complexnode.addObject(ttt,"complexnode.complex");
        System.out.println("Finished creation of objects");
        System.out.println(complexnode.toStringFull());
        TestComplexMemoryObjectInfo returned = (TestComplexMemoryObjectInfo) complexnode.getObject("complex", "br.unicamp.cst.core.profiler.TestComplexMemoryObjectInfo");
        System.out.println("Recovered object: "+returned.toString());
        System.out.println("Returned: "+returned.testdate);
        System.out.println("ttt: "+ttt.testdate);
        assertEquals(returned.equals(ttt),0);
        double[] nt = new double[3];
        nt[0] = 1.2;
        nt[1] = 2.3;
        nt[2] = 3.1;
        Idea i_nt = Idea.createIdea("novo","", 0);
        i_nt.addObject(nt,"d");
        System.out.println(i_nt.toStringFull());
        double[] ntr = (double[]) i_nt.getObject("d","double[]");
        System.out.println("nt: "+nt[0]+" "+nt[1]+" "+nt[2]);
        l = new ArrayList<TestComplexMemoryObjectInfo>();
        l.add(new TestComplexMemoryObjectInfo());
        l.add(new TestComplexMemoryObjectInfo());
        l.add(new TestComplexMemoryObjectInfo());
        l.get(1).complextestlist2.set(0, 7.88);
        l.get(1).complextestlist2.set(1, 8.88);
        l.get(2).complextestlist2.set(0, 5.44);
        l.get(2).complextestlist2.set(1, 6.44);
        System.out.println("Testing if complextestlist2 is there ..."+l.get(1).complextestlist2.get(0)+" "+l.get(2).complextestlist2.get(1));
        
        
        Idea node = Idea.createIdea("root","", 0);
        Field stringListField=null;
        try {stringListField = TestIdea.class.getDeclaredField("l");} catch(Exception e) { e.printStackTrace();}
        ParameterizedType stringListType = (ParameterizedType) stringListField.getGenericType();
        Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
        
        System.out.println("Classe: "+l.getClass().getTypeParameters()+" "+stringListClass.getCanonicalName());
        node.reset();
        node.addObject(l,"lista");
        System.out.println(node.toStringFull(true));
        TestComplexMemoryObjectInfo[] l2 = (TestComplexMemoryObjectInfo[]) node.getObject("lista", "br.unicamp.cst.core.profiler.TestComplexMemoryObjectInfo[]");
        if (l2.length == 3) System.out.println("Yes ! I got 3 objects !!!");
        List<TestComplexMemoryObjectInfo> l3;
        if (l2 != null) l3 = Arrays.asList(l2);
        else System.out.println("Unfortunately I was not able to recover the list");
        List<String> ls = new ArrayList<>();
        for (String s : Idea.repo.keySet()) {
            ls.add(s);
        }
        Collections.sort(ls);
        for (String s : ls) {
            System.out.println(s);
        }
    }
    
}