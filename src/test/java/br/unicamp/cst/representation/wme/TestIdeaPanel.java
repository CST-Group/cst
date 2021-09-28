/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.representation.wme;

import javax.swing.JFrame;
import org.junit.Test;

public class TestIdeaPanel {
    
    IdeaPanel wmp;
    
    public Idea initialize() {
        Idea node = new Idea("Test","",0);
        node.add(new Idea("child1","",0));
        node.add(new Idea("child2","I2",0));
        node.add(new Idea("child3",3.1416d,1));
        node.add(new Idea("child3","I3",2));
        return(node);
    }
    
    @Test 
    public void testWMNode() {
        
        JFrame frame = new JFrame();
        frame.setSize(300,200);
        Idea node = initialize();
        System.out.println(node.toStringFull());
        wmp = new IdeaPanel(node,true);
        wmp.setOpaque(true); //content panes must be opaque
        frame.setContentPane(wmp);
        wmp.expandAllNodes();
        wmp.updateTree();
        frame.setVisible(true);
        
        try {
           Thread.sleep(2000);
        } catch (Exception e) {} 
        frame.setVisible(false);
    }
    
}    
