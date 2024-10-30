/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.unicamp.cst.representation.idea;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
        Idea ia = ec.getInstance();
        System.out.println("ia: "+ia.getId()+"\n"+ia.toStringFull());
        ia = ec.getInstance();
        System.out.println("ia2: "+ia.getId()+"\n"+ia.toStringFull());
        
        // The next test is meant to create a loop and check if the procedure can avoid it
        //d.add(b);
        //d1.add(b1);
        //assertTrue(a1.equivalent(a));
        //assertFalse(a.equivalent(a1));
    }
    
    @Test public void testEntityCategoryIdea() {
        Idea a = createComplexIdea();
        EntityCategory ec = new EntityCategory(a);
        Idea eca = new Idea("ACategory",ec);
        Idea a1 = createComplexIdea();
        a1.get("b.d").add(new Idea("f"));
        System.out.println("a: "+a.getId()+"\n"+a.toStringFull());
        System.out.println("a1:"+a1.getId()+"\n"+a1.toStringFull());
        System.out.println("ma: "+ec.membership(a1));
        System.out.println("mab: "+ec.membership(a1.get("b")));
        Idea ia = eca.getInstance();
        System.out.println("ia: "+ia.getId()+"\n"+ia.toStringFull());
        Idea ia2 = eca.getInstance();
        System.out.println("ia2: "+ia2.getId()+"\n"+ia2.toStringFull());
        
        // The next test is meant to create a loop and check if the procedure can avoid it
        //d.add(b);
        //d1.add(b1);
        //assertTrue(a1.equivalent(a));
        //assertFalse(a.equivalent(a1));
    }
    
}
