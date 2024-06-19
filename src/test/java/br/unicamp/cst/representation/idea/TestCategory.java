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

import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author rgudwin
 */
public class TestCategory {
    
    Idea evenIdea;
    Idea oddIdea;
    
    private Category createEvenNumber() {
        Category even = new Category() { 
        @Override 
        public double membership(Idea idea) { 
             //Check if belongs to category return membershipDegree;
             if (idea.getValue() instanceof Integer) {
                  int number = (int) idea.getValue();
                  if (number % 2 == 0) return(1.0);
             }    
             return(0.0);
        }
        @Override
        public Idea getInstance(Idea constraints) {
             // Create an instance of the category based on constraints
             int number = new Random().nextInt();             
             Idea evenNumber = new Idea("even_number",number*2);
             return(evenNumber);
        }
        };
        
        return(even);
    }
    
    private Category createOddNumber() {
        Category odd = new Category() {
        @Override 
        public double membership(Idea idea) { 
             //Check if belongs to category return membershipDegree;
             if (idea.getValue() instanceof Integer) {
                  int number = (int) idea.getValue();
                  if (number % 2 != 0) return(1.0);
             }    
             return(0.0);
        }
        @Override
        public Idea getInstance(Idea constraints) {
             // Create an instance of the category based on constraints
             int number = new Random().nextInt();             
             Idea oddNumber = new Idea("odd_number",number*2+1);
             return(oddNumber);
        }
        };
        return(odd);
    }
    
    public TestCategory() {
        // Creating a concept for even numbers
        evenIdea = new Idea("evenIdea",createEvenNumber(),"Property",2);
        // Creating a concept for odd numbers
        oddIdea = new Idea("oddIdea",createOddNumber(),"Property",2);
    }
    
    @Test 
    public void testRawCategoryIdeas() {
        TestCategory tc = new TestCategory();
        // Getting the category for even numbers
        Category even = (Category) tc.evenIdea.getValue();
        // Getting the category for odd numbers
        Category odd = (Category) tc.oddIdea.getValue();
        System.out.println("Testing the instantiation of 'even numbers' from raw Category ...");
        for (int i=0;i<100;i++) {
            Idea newevennumber = even.getInstance(null);
            System.out.print(" "+newevennumber.getValue());
            assertEquals(even.membership(newevennumber),1.0);
            assertEquals(odd.membership(newevennumber),0.0);
        }
        System.out.println("\nTesting the instantiation of 'odd numbers' from raw Category ...");
        for (int i=0;i<100;i++) {
            Idea newoddnumber = odd.getInstance(null);
            System.out.print(" "+newoddnumber.getValue());
            assertEquals(even.membership(newoddnumber),0.0);
            assertEquals(odd.membership(newoddnumber),1.0);
        }
        System.out.println("\nfinished !");
    }
    
    @Test 
    public void testCategoryIdeasDirect() {
        // Creating a category for even numbers
        TestCategory tc = new TestCategory();
        System.out.println("Testing if indeed even and odd are categories");
        assertEquals(tc.evenIdea.isCategory(),true);
        assertEquals(tc.oddIdea.isCategory(),true);
        Idea id = new Idea();
        System.out.println("Testing if a complete new Idea is not a category");
        assertEquals(id.isCategory(),false);
        System.out.println("Testing the instantiation of 'even numbers' from a category idea ...");
        for (int i=0;i<100;i++) {
            Idea newevennumber = tc.evenIdea.getInstance(null);
            System.out.print(" "+newevennumber.getValue());
            assertEquals(tc.evenIdea.membership(newevennumber),1.0);
            assertEquals(tc.oddIdea.membership(newevennumber),0.0);
        }
        System.out.println("\nTesting the instantiation of 'odd numbers' from a category idea ...");
        for (int i=0;i<100;i++) {
            Idea newoddnumber = tc.oddIdea.getInstance(null);
            System.out.print(" "+newoddnumber.getValue());
            assertEquals(tc.evenIdea.membership(newoddnumber),0.0);
            assertEquals(tc.oddIdea.membership(newoddnumber),1.0);
        }
        System.out.println("\nfinished !");
    }

    private Category createInterval(int mi,int ma) {
        Category interval = new Category() { 
            public int min=mi;
            public int max=ma;
            @Override 
            public double membership(Idea idea) { 
                 //Check if belongs to category return membershipDegree;
                if (idea.getValue() instanceof Integer) {
                  int number = (int) idea.getValue();
                  if (number >= min && number <= max) return(1.0);
                }    
                return(0.0);
            }
            @Override
            public Idea getInstance(Idea constraints) {
                int minimum = min;
                int maximum = max;
                if (constraints != null) {
                    for (Idea i : constraints.getL()) {
                        if (i.getName().equals("min") && i.getValue() instanceof Integer && (int)i.getValue() > min) {
                            minimum = (int) i.getValue();
                        }
                        if (i.getName().equals("max") && i.getValue() instanceof Integer && (int)i.getValue() < max) {
                            maximum = (int) i.getValue();
                        }
                    }
                }
                // Create an instance of the category based on constraints
                Idea i;
                do {
                    int number = new Random().nextInt(maximum-minimum+1)+minimum;             
                    i = new Idea("number",number);
                } while(membership(i) == 0);  
                return(i);
            }
        };
        return(interval);
    }    
    @Test 
    public void testParameterizedIdeas() {
        System.out.println("Testing the creation of interval (0,100) without constraints");
        Category interval = createInterval(0,100);
        for (int i=0;i<100;i++) {
           Idea i1 = interval.getInstance(null);
           assertEquals(interval.membership(i1),1.0);
           System.out.print(" "+i1.getValue());
        }
        Idea c1 = new Idea("min",10);
        Idea c2 = new Idea("max",20);
        Idea[] co = {c1, c2};
        Idea  constraint = new Idea("constraint");
        constraint.add(c1);
        constraint.add(c2);
        System.out.println("\nTesting the creation of interval (0,100) with constraints (10,20)");
        for (int i=0;i<100;i++) {
           Idea i1 = interval.getInstance(constraint);
           assertEquals(interval.membership(i1),1.0);
           System.out.print(" "+i1.getValue());
        }
        System.out.println("\nfinished !");
        System.out.println("Testing the creation of interval (0,100) with constraints (30,50)");
        Idea inter = new Idea("interval",interval);
        inter.add(c1);
        inter.add(c2);
        c1.setValue(30);
        c2.setValue(50);
        for (int i=0;i<100;i++) {
           Idea i1 = inter.getInstance();
           assertEquals(inter.membership(i1),1.0);
           System.out.print(" "+i1.getValue());
        }
        System.out.println("\nfinished !");
    }
        
    
}
