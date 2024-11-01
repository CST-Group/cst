/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.unicamp.cst.representation.idea;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author rgudwin
 */
public class TestEntityCategory {
    
    private Idea createComplexIdea() {
        Idea a = new Idea("a");
        Idea b = new Idea("b");
        Idea c = new Idea("c");
        Idea d = new Idea("d");
        Idea e = new Idea("e");
        a.add(b);
        a.add(c);
        b.add(d);
        d.add(e);
        d.add(b);
        return(a);
    }
    
    @Test public void testEntityCategory() {
        Idea a = createComplexIdea();
        EntityCategory ec = new EntityCategory(a);
        Idea a1 = createComplexIdea();
        a1.get("b.d").add(new Idea("f"));
        System.out.println("a: "+a.getId()+"\n"+a.toStringFull());
        System.out.println("a1:"+a1.getId()+"\n"+a1.toStringFull());
        System.out.println("ma: "+ec.membership(a1));
        System.out.println("mab: "+ec.membership(a1.get("b")));
        assertEquals(ec.membership(a1),1.0);
        assertEquals(ec.membership(a1.get("b")),0.0);
        Idea ia = ec.getInstance();
        System.out.println("Getting instances");
        System.out.println("ia: "+ia.getId()+"\n"+ia.toStringFull());
        assertEquals(ec.membership(ia),1.0);
        ia = ec.getInstance();
        System.out.println("ia2: "+ia.getId()+"\n"+ia.toStringFull());
        assertEquals(ec.membership(ia),1.0);
    }
    
    @Test public void testNullEntityCategory() {
        Idea a = createComplexIdea();
        EntityCategory ec = new EntityCategory(null);
        Idea a1 = createComplexIdea();
        a1.get("b.d").add(new Idea("f"));
        System.out.println("a: "+a.getId()+"\n"+a.toStringFull());
        System.out.println("a1:"+a1.getId()+"\n"+a1.toStringFull());
        System.out.println("ma: "+ec.membership(a1));
        System.out.println("mab: "+ec.membership(a1.get("b")));
        assertEquals(ec.membership(a1),1.0);
        // This should be 1.0, because now the category is null
        assertEquals(ec.membership(a1.get("b")),1.0);
        Idea ia = ec.getInstance();
        System.out.println("Getting instances");
        System.out.println("ia: "+ia+"\n");
        assertEquals(ec.membership(ia),1.0);
        ia = ec.getInstance();
        System.out.println("ia2: "+ia+"\n");
        assertEquals(ec.membership(ia),1.0);
    }
    
    @Test public void testEntityCategoryIdea() {
        Idea a = createComplexIdea();
        EntityCategory ec = new EntityCategory(a);
        Idea eca = new Idea("ACategory",ec);
        Idea a1 = createComplexIdea();
        a1.get("b.d").add(new Idea("f"));
        System.out.println("This is the Idea used as a template for the category:\n"+a.toStringFull(true));
        System.out.println("This is the modified Idea to evaluate if it fits the category:\n"+a1.toStringFull(true));
        System.out.println("ma: "+ec.membership(a1));
        System.out.println("mab: "+ec.membership(a1.get("b")));
        Idea j = new Idea("j");
        j.add(a1.get("b"));
        a1.get("b").add(j);
        System.out.println("Testing the membership of the following Entity\n"+a1.toStringFull());
        System.out.println("ma_ext: "+ec.membership(a1));
        a1.get("b.d").setL(new ArrayList<Idea>());
        System.out.println("Testing the membership of the following Entity\n"+a1.toStringFull());
        System.out.println("ma_cut: "+ec.membership(a1));
        Idea ia = eca.getInstance();
        System.out.println("Lets create a Category instance and test:\n"+ia.toStringFull(true));
        Idea ia2 = eca.getInstance();
        System.out.println("Lets create a second Category instance and test:\n: "+ia2.toStringFull(true));
        assertEquals(ec.membership(ia),1.0);
        assertEquals(ec.membership(ia2),1.0);
    }
    
}
