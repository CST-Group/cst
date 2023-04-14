/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.unicamp.cst.representation.idea;

import java.util.List;
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
    
    
    public TestCategory() {
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
        public Idea instantiation(List<Idea> constraints) {
             // Create an instance of the category based on constraints
             int number = new Random().nextInt();             
             Idea evenNumber = new Idea("even_number",number*2);
             return(evenNumber);
        }
        };
        // Creating a concept for even numbers
        evenIdea = new Idea("evenIdea",even,"Property",2);
        // Creating a category for odd numbers
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
        public Idea instantiation(List<Idea> constraints) {
             // Create an instance of the category based on constraints
             int number = new Random().nextInt();             
             Idea oddNumber = new Idea("odd_number",number*2+1);
             return(oddNumber);
        }
        };
        // Creating a concept for odd numbers
        oddIdea = new Idea("oddIdea",odd,"Property",2);
    }
    
    @Test 
    public void testCategoryIdeas() {
        // Creating a category for even numbers
        TestCategory tc = new TestCategory();
        Category even = (Category) tc.evenIdea.getValue();
        Category odd = (Category) tc.oddIdea.getValue();
        System.out.println("Creating and testing even numbers ...");
        for (int i=0;i<100;i++) {
            Idea newevennumber = even.instantiation(null);
            System.out.print(" "+newevennumber.getValue());
            assertEquals(even.membership(newevennumber),1.0);
            assertEquals(odd.membership(newevennumber),0.0);
        }
        System.out.println("\nCreating and testing odd numbers ...");
        for (int i=0;i<100;i++) {
            //System.out.println("what ?");
            Idea newoddnumber = odd.instantiation(null);
            System.out.print(" "+newoddnumber.getValue());
            assertEquals(even.membership(newoddnumber),0.0);
            assertEquals(odd.membership(newoddnumber),1.0);
        }
        System.out.println("\nfinished !");
    }
    
        
    
}
