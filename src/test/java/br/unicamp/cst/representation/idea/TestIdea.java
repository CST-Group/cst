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
import br.unicamp.cst.support.TimeStamp;
import br.unicamp.cst.support.ToString;
import java.util.HashMap;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;


public class TestIdea {
    
    List<TestComplexMemoryObjectInfo> l;
    
    public Idea initialize() {
        Idea node = new Idea("Test","",0);
        node.add(new Idea("child1","",0)).add(new Idea("subchild1",3.14,1)).add(new Idea("subsubchild1","whatthe..."));
        double variable[] = new double[3];
        //node.add(new Idea("child2","I2",0)).add(new Idea("array",new double[]{3.4, 2.2, 1.23}));
        node.add(new Idea("child2","I2",0)).addObject(new double[]{3.4, 2.2, 1.23},"array");
        node.add(new Idea("child3",3.1416d,1));
        Idea child4 = new Idea("child4",null,2);
        node.add(child4);
        child4.add(node);
        System.out.println(node.toStringFull(true));
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
        assertEquals(n.get("child1.subchild1").getValue(),3.14);
        o = n.get("child1.subchild1.subsubchild1");
        print("child1.subchild1.subsubchild1",o);
        assertEquals(n.get("child1.subchild1.subsubchild1").getValue(),"whatthe...");
        o = n.get("child2");
        print("child2",o);
        assertEquals(n.get("child2").getValue(),"I2");
        o = n.get("child2.array");
        print("child2.array",o);
        assertEquals(n.get("child2.array.array[0]").getValue(),3.4);
        assertEquals(n.get("child2.array.array[1]").getValue(),2.2);
        assertEquals(n.get("child2.array.array[2]").getValue(),1.23);
        o = n.get("child3");
        print("child3",o);
        assertEquals(n.get("child3").getValue(),3.1416);
        o = n.get("child4");
        print("child4",o);
        assertEquals(n.get("child4.Test.child1.subchild1").getValue(),3.14);
    }
    
    private void initialize(TestComplexMemoryObjectInfo ttt) {
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
    }
    
    @Test 
    public void testIdea() {
        Locale.setDefault(Locale.US);
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
        assertEquals(ln.get("b.c.v").getValue(),3);
        assertEquals(ln.get("b.v").getValue(),3);
        assertEquals(ln.get("v").getValue(),3);
        Date d = new Date();
        Idea complexnode = Idea.createIdea("complexnode","",0);
        System.out.println("Adding the object date within complexnode");
        complexnode.addObject(d,"date");
        assertEquals(complexnode.get("date").getValue(),ToString.from(d));
        DefaultMutableTreeNode dt = new DefaultMutableTreeNode(d);
        System.out.println("Adding the object defaultMutableTreeNode within complexnode");
        complexnode.addObject(dt, "defaultMutableTreeNode");
        assertEquals(complexnode.get("defaultMutableTreeNode.userObject").getValue(),ToString.from(d));
        System.out.println("Adding the object complexnode within itself with the name recursion");
        complexnode.addObject(complexnode,"recursion");
        System.out.println(complexnode.toStringFull(true));
        assertEquals(complexnode.get("recursion").getId(),complexnode.get("recursion.complexnode.recursion").getId());
        assertEquals(complexnode.get("recursion").getId(),complexnode.get("recursion.complexnode.recursion.complexnode.recursion").getId());
        TestComplexMemoryObjectInfo ttt = new TestComplexMemoryObjectInfo();
        initialize(ttt);
        TestComplexMemoryObjectInfo ttt2 = new TestComplexMemoryObjectInfo();
        initialize(ttt2);
        ttt2.complextestlist2.add(3.12);
        ttt.complextest = ttt2;
        System.out.println("Adding the object complex within complexnode");
        complexnode.addObject(ttt,"complex");
        System.out.println(complexnode.toStringFull());
        TestComplexMemoryObjectInfo returned = (TestComplexMemoryObjectInfo) complexnode.getObject("complex", "br.unicamp.cst.core.profiler.TestComplexMemoryObjectInfo");
        System.out.println("Recovered object: "+returned.toString());
        System.out.println("Returned: "+returned.testdate);
        System.out.println("ttt: "+ttt.testdate);
        System.out.println("ttt: "+ttt);
        System.out.println("returned: "+returned);
        System.out.println("returned.equals(ttt): "+returned.equals(ttt));
        assertEquals(returned.equals(ttt),0);
        System.out.println("Now testing if addObject can detect recursion ...");
        ttt.complextest = ttt;
        complexnode = new Idea("complexnode","",0);
        complexnode.addObject(ttt, "complex");
        Idea recursion = complexnode.get("complex.complextest");
        assertEquals(recursion.getName(),"complextest");
        assertEquals(recursion.getType(),2);
        System.out.println("recursion: "+recursion.toStringFull());
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
    
    /* 0 - AbstractObject
     * 1 - Property
     * 2 - Link or Reference to another Idea
     * 3 - QualityDimension
     * 4 - Episode
     * 5 - Composite
     * 6 - Aggregate
     * 7 - Configuration
     * 8 - TimeStep
     * 9 - PropertyCategory
     * 10 - ObjectCategory
     * 11 - EpisodeCategory
     */
    @Test 
    public void testIdea2() {
        Idea idea = new Idea("idea","idea","AbstractObject",1);
        assertEquals(idea.getType(),0);
        idea = new Idea("idea","idea","Property",1);
        assertEquals(idea.getType(),1);
        idea = new Idea("idea","idea","Link",1);
        assertEquals(idea.getType(),2);
        idea = new Idea("idea","idea","QualityDimension",1);
        assertEquals(idea.getType(),3);
        idea = new Idea("idea","idea","Episode",1);
        assertEquals(idea.getType(),4);
        idea = new Idea("idea","idea","Composite",1);
        assertEquals(idea.getType(),5);
        idea = new Idea("idea","idea","Aggregate",1);
        assertEquals(idea.getType(),6);
        idea = new Idea("idea","idea","Configuration",1);
        assertEquals(idea.getType(),7);
        idea = new Idea("idea","idea","TimeStep",1);
        assertEquals(idea.getType(),8);
        idea = new Idea("idea","idea","Property",2);
        assertEquals(idea.getType(),9);
        idea = new Idea("idea","idea","AbstractObject",2);
        assertEquals(idea.getType(),10);
        idea = new Idea("idea","idea","Episode",2);
        assertEquals(idea.getType(),11);
        idea = new Idea("idea","idea","Property",0);
        assertEquals(idea.getType(),12);
        idea = new Idea("idea","idea","AbstractObject",0);
        assertEquals(idea.getType(),13);
        idea = new Idea("idea","idea","Episode",0);
        assertEquals(idea.getType(),14);
        idea = new Idea("idea","idea","Action",0);
        assertEquals(idea.getType(),15);
        idea = new Idea("idea","idea","Action",1);
        assertEquals(idea.getType(),16);
        idea = new Idea("idea","idea","Action",2);
        assertEquals(idea.getType(),17);
        idea = new Idea("idea","idea","Goal",0);
        assertEquals(idea.getType(),18);
        idea = new Idea("idea","idea",0,"Episode",0);
        idea.setCategory("Property");
        idea.setScope(1);
        assertEquals(idea.getCategory().equalsIgnoreCase("Property"),true);
        assertEquals(idea.getScope(),1);
        assertEquals(Idea.guessType(null, 0),0);
        assertEquals(Idea.guessType("Property",3),0);
        assertEquals(Idea.guessType("AbstractObject",3),0);
        assertEquals(Idea.guessType("Episode",3),0);
        assertEquals(Idea.guessType("Action",3),0);
        assertEquals(Idea.guessType("",3),0);
        // Testing scope limits
        idea = new Idea("idea","idea",0,"Episode",-1);
        assertEquals(idea.getScope(),1);
        assertEquals(idea.getType(),0);
        idea = new Idea("idea","idea",0,"Episode",3);
        assertEquals(idea.getScope(),1);
        assertEquals(idea.getType(),0);
    }
    
    @Test 
    public void testIdea3() {
        DefaultMutableTreeNode tn = new DefaultMutableTreeNode();
        tn.setUserObject("tn");
        DefaultMutableTreeNode tn21 = new DefaultMutableTreeNode();
        tn21.setUserObject("tn21");
        DefaultMutableTreeNode tn22 = new DefaultMutableTreeNode();
        tn22.setUserObject("tn22");
        DefaultMutableTreeNode tn3 = new DefaultMutableTreeNode();
        tn3.setUserObject("tn3");
        tn.add(tn21);
        tn22.add(tn3);
        tn.add(tn22);
        
        Idea i = new Idea("dm");
        i.addObject(tn, "tn");
        System.out.println(i.toStringFull());
    }
    
    @Test 
    public void testIsMethods() {
        Locale.setDefault(Locale.US);
        Idea i = new Idea();
        i.setName("test");
        assertEquals(i.getName(),"test");
        i.setValue(null);
        assertEquals(i.getValue(),null);
        assertEquals(i.isLong(),false);
        assertEquals(i.isBoolean(),false);
        assertEquals(i.isInteger(),false);
        assertEquals(i.isString(),false);
        assertEquals(i.isHashMap(),false);
        assertEquals(i.isNumber(),false);
        assertEquals(i.getInstance(),null);
        assertEquals(i.membership(null),0.0);
        assertEquals(i.exec(null),null);
        i.setValue(1D);
        assertEquals(i.isDouble(),true);
        assertEquals(i.isNumber(),true);
        assertEquals(i.isInteger(),false);
        assertEquals(i.isBoolean(),false);
        assertEquals(i.isString(),false);
        assertEquals(i.isHashMap(),false);
        assertEquals(i.getResumedValue()," 1.0");
        i.setValue(1F);
        assertEquals(i.isFloat(),true);
        assertEquals(i.isNumber(),true);
        assertEquals(i.getResumedValue()," 1.0");
        i.setValue(1L);
        assertEquals(i.isLong(),true);
        assertEquals(i.isNumber(),true);
        assertEquals(i.getResumedValue(),"1");
        i.setValue(true);
        assertEquals(i.isBoolean(),true);
        i.setValue(1);
        assertEquals(i.isInteger(),true);
        assertEquals(i.isNumber(),true);
        assertEquals(i.getResumedValue(),"1");
        i.setValue("");
        assertEquals(i.isString(),true);
        i.setValue(new HashMap());
        assertEquals(i.isHashMap(),true);
        // The default type should be 1
        assertEquals(i.isType(1),true);
        i.setType(0);
        assertEquals(i.isType(1),false);
        assertEquals(i.isType(0),true);
        i.setValue("3.1");
        assertEquals(i.getResumedValue()," 3.1");
        i.setValue("nothing");
        assertEquals(i.getResumedValue(),"nothing");
    }
    
    @Test 
    public void cloneTest() {
        Idea i = new Idea("test","value",12,"category",2);
        long n1,n2,n3,n4,n5,n6;
        n1 = i.getId();
        Idea sub1 = new Idea("sub","value1",13,"category1",1);
        n2 = sub1.getId();
        Idea sub2 = new Idea("sub2","value2",14,"category2",0);
        n3 = sub2.getId();
        assertNotEquals(n1,n2);
        assertNotEquals(n1,n3);
        assertNotEquals(n2,n3);
        i.add(sub1);
        sub1.add(sub2);
        Idea i2 = i.clone();
        n4 = i2.getId();
        assertEquals(i2.getName(),"test");
        assertEquals(i2.getValue(),"value");
        assertEquals(i2.getType(),12);
        assertEquals(i2.getCategory(),"category");
        assertEquals(i2.getScope(),2);
        assertEquals(i2.toString(),"test");
        Idea i3 = i2.get("sub");
        n5 = i3.getId();
        assertEquals(i3.getName(),"sub");
        assertEquals(i3.getValue(),"value1");
        assertEquals(i3.getType(),13);
        assertEquals(i3.getCategory(),"category1");
        assertEquals(i3.getScope(),1);
        i3 = i2.get("sub.sub2");
        n6 = i3.getId();
        assertEquals(i3.getName(),"sub2");
        assertEquals(i3.getValue(),"value2");
        assertEquals(i3.getType(),14);
        assertEquals(i3.getCategory(),"category2");
        assertEquals(i3.getScope(),0);
        assertNotEquals(n1,n4);
        assertNotEquals(n2,n5);
        assertNotEquals(n3,n6);
        i3 = i2.get("what?");
        assertEquals(i3,null);
    }
    
    @Test public void testSetL() {
        Idea i = new Idea();
        Idea i2 = new Idea("sub1");
        Idea i3 = new Idea("sub2");
        List<Idea> l = new ArrayList<>();
        l.add(i2);
        l.add(i3);
        i.setL(l);
        assertEquals(i.get("sub1"),i2);
        assertEquals(i.get("sub2"),i3);
    }
    
    @Test public void testConvertStringValue() {
        Idea i = new Idea("test","2");
        assertEquals(i.getValue(),2);
        i = new Idea("test","3.0");
        assertEquals(i.getValue(),3.0);
    }
    
    @Test public void testCreateIdea() {
        Idea i = Idea.createIdea("test", l, 0);
        Idea i2 = Idea.createIdea("test", l, 1);
        Idea i3 = Idea.createIdea("test", l, 1);
        Idea i4 = Idea.createIdea("test", l, 1);
        assertNotEquals(i,i2);
        assertEquals(i2,i3);
        assertEquals(i2,i4);
        System.out.println("REPO: ");
        Idea.repo.forEach((key, value) -> {
            System.out.println("Key=" + key + ", Value=" + value.getName()+","+value.getType());
        });
    }
    
    @Test public void testCreateJavaObject() {
        double d = (double) Idea.createJavaObject("java.lang.Double");
        assertEquals(d,0);
        float f = (float) Idea.createJavaObject("java.lang.Float");
        assertEquals(f,0);
        int i = (int) Idea.createJavaObject("java.lang.Integer");
        assertEquals(i,0);
        long l = (long) Idea.createJavaObject("java.lang.Long");
        assertEquals(l,0);
        short s = (short) Idea.createJavaObject("java.lang.Short");
        assertEquals(s,0);
        boolean b = (boolean) Idea.createJavaObject("java.lang.Boolean");
        assertEquals(b,false);
        byte by = (byte) Idea.createJavaObject("java.lang.Byte");
        assertEquals(by,0);
        System.out.println("This test is designed to fail !!!");
        Object o = Idea.createJavaObject("whatever");
        assertEquals(o,null);
    }
    
    @Test public void testTryThings() {
        Idea root = new Idea("root","3.1");
        assertEquals(root.getValue(),3.1);
        root.setValue("3");
        int ii = (int) root.getObject(null, "java.lang.Integer");
        assertEquals(ii,3);
        long il = (long) root.getObject(null,"java.lang.Long");
        assertEquals(il,3);
        short is = (short) root.getObject(null,"java.lang.Short");
        assertEquals(is,3);
        byte ibb = (byte) root.getObject(null,"java.lang.Byte");
        assertEquals(ibb,3);
        root.setValue("4.1");
        double id = (double) root.getObject(null, "java.lang.Double");
        assertEquals(id,4.1);
        float iff = (float) root.getObject(null,"java.lang.Float");
        assertEquals(iff,4.1F);
        root.setValue("23/06/2023 10:23:34.789");
        Date date = (Date) root.getObject(null, "java.util.Date");
        assertEquals(date.getTime(),TimeStamp.getLongTimeStamp("23/06/2023 10:23:34.789","dd/MM/yyyy HH:mm:ss.SSS"));
        System.out.println(date);
    }
    
    static int maxid = 0;  // To be used in TestAutoReference
    
    private class TestAutoReference {
        int id;
        public TestAutoReference parent;
        public ArrayList<TestAutoReference> children = new ArrayList<>();
        public TestAutoReference() {
            id = maxid;
            maxid++;
        }
        public void add(TestAutoReference child) {
            children.add(child);
            parent = this;
        }
        @Override
        public String toString() {
            String s = "ar"+id;
            return s;
        }
        
    }
    
    @Test public void testAutoReferenceIdea() {
        TestAutoReference ar = new TestAutoReference();
        TestAutoReference ar2 = new TestAutoReference();
        TestAutoReference ar3 = new TestAutoReference();
        TestAutoReference ar4 = new TestAutoReference();
        ar.add(ar2);
        ar2.add(ar4);
        ar.add(ar3);
        Idea i1 = Idea.createIdea("autoref",ar,1);
        i1.addObject(ar, "autoref");
        i1.addObject(ar, "autoref",false);
        i1.addObject(ar, "autoref2",false);
        Idea i2 = i1.get("autoref");
        i2.addObject(ar, "autoref",false);
        i1.addObject(i2, "autoref",false);
        System.out.println(i1.toStringFull());
        Idea i3 = i1.get("autoref.autoref");
        System.out.println(i3.toStringFull());
    }
    
}