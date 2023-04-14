/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.unicamp.cst.sensory;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.support.TimeStamp;
import java.util.concurrent.CopyOnWriteArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author rgudwin
 */
public class ATest {
    
    public MemoryObject source;
    public MemoryObject destination;
    public Codelet cod;
    public int steps=0;
    
    
    public ATest() {
        cod = new Codelet() {
            
				
			@Override
			public void accessMemoryObjects() {
			}
			
			@Override
			public void proc() {
                            System.out.println("steps: "+steps+" "+TimeStamp.getTimeSinceStart("HH:mm:ss.SSS"));
                            steps++;
			}
			
			@Override
			public void calculateActivation() {
								
			}
		};
        Mind testMind = new Mind();
        source = testMind.createMemoryObject("SOURCE");
        destination = testMind.createMemoryObject("COMB_FM");
        destination.setI(new CopyOnWriteArrayList<Float>());
        CopyOnWriteArrayList<String> FMnames = new CopyOnWriteArrayList<>();
        testMind.insertCodelet(cod);
        cod.addInput(source);
        cod.addOutput(destination);
        cod.setIsMemoryObserver(true);
	source.addMemoryObserver(cod);
        testMind.start();
    }
    
    @Test
    public void testATest() {
        TimeStamp.setStartTime();
        ATest test = new ATest();
        System.out.println("waiting 2s before calling setI");
        try { Thread.sleep(2000);} catch (Exception e) {}
        System.out.println("calling setI");
        test.source.setI((1));
        while(TimeStamp.getTimeSinceStart() < 2500) {
            System.out.println("Testing ... "+test.steps+" "+TimeStamp.getTimeSinceStart("HH:mm:ss.SSS")+" "+TimeStamp.getTimeSinceStart());
        }
    }
    
}
