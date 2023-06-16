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
package br.unicamp.cst.core.entities;

import br.unicamp.cst.core.entities.MemoryContainer.Policy;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


/**
 * @author andre
 *
 */
public class MemoryContainerTest {

	
	@Test
	public void testMemoryContainerContent() {
		
		Memory memoryContainer = new MemoryContainer("TYPE");
		
		((MemoryContainer) memoryContainer).setI(71L, 0.1D, "TYPE");
		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE");
			
		assertEquals(75L, memoryContainer.getI());
	}
	
	@Test
	public void testMemoryContainerSize() {
		
		Memory memoryContainer = new MemoryContainer("TYPE");
		
		((MemoryContainer) memoryContainer).setI(71L, 0.1D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE3");
			
		assertEquals(2, ((MemoryContainer) memoryContainer).getAllMemories().size());
	}

	@Test
	public void setTypeTest(){
		MemoryContainer memoryContainer = new MemoryContainer();
		memoryContainer.setType("TYPE");
		assertEquals("TYPE", memoryContainer.getName());
                memoryContainer = new MemoryContainer();
		memoryContainer.setName("TYPE2");
		assertEquals("TYPE2", memoryContainer.getName());
                memoryContainer = new MemoryContainer("TYPE3");
		assertEquals("TYPE3", memoryContainer.getName());
	}
        
        @Test
	public void getTypeTest(){
		MemoryContainer memoryContainer = new MemoryContainer("TYPE-Container");
		memoryContainer.setI("value",1.0,"TYPE");
                assertEquals(memoryContainer.getI("TYPE"),"value");
                System.out.println("-- This test will raise a warning ...");
                assertNull(memoryContainer.getI("TYPE2"));
	}

	@Test
	public void getITest() {

		Memory memoryContainer = new MemoryContainer("TYPE");

		((MemoryContainer) memoryContainer).setI(71L, 0.1D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(70L, 0.3D, "TYPE3");

		assertEquals(70L, ((MemoryContainer) memoryContainer).getI());
		assertEquals(75L, ((MemoryContainer) memoryContainer).getI(0));
                System.out.println("-- This test will raise a warning ...");
		assertNull(((MemoryContainer) memoryContainer).getI(2)); // This test will raise a warning for index greater than the number of stored memories
		assertEquals(70L, ((MemoryContainer) memoryContainer).getI("TYPE3"));
	}

	@Test
	public void getIPredicateTest() {

		Memory memoryContainer = new MemoryContainer("TYPE");

		((MemoryContainer) memoryContainer).setI(71L, 0.1D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(70L, 0.3D, "TYPE3");
		((MemoryContainer) memoryContainer).setI(70L, 0.25D);

		Predicate<Memory> pred = new Predicate<Memory>() {
			@Override
			public boolean test(Memory memory) {
				return memory.getName().equals("TYPE2");
			}
		};

		assertEquals(75L, ((MemoryContainer) memoryContainer).getI(pred));
	}

	@Test
	public void getIAccumulatorTest() {

		Memory memoryContainer = new MemoryContainer("TYPE");

		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(70L, 0.3D, "TYPE3");
		((MemoryContainer) memoryContainer).setI(80L);

		BinaryOperator<Memory> binaryOperator = (mem1,mem2) -> mem1.getEvaluation() <= mem2.getEvaluation() ? mem1 : mem2;

		assertEquals(80L, ((MemoryContainer) memoryContainer).getI(binaryOperator));
	}

	@Test
	public void setISpecificTest() {

		Memory memoryContainer = new MemoryContainer("TYPE");

		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(70L, 0.3D, "TYPE3");
		((MemoryContainer) memoryContainer).setI(80L);

		((MemoryContainer) memoryContainer).setI(60L, 1);
		((MemoryContainer) memoryContainer).setI(90L, 0.5D, 2);

		assertEquals(60L, ((MemoryContainer) memoryContainer).getI(1));
		assertEquals(90L, ((MemoryContainer) memoryContainer).getI());
		assertEquals(0.5D, ((MemoryContainer) memoryContainer).getEvaluation(), 0);
	}

	@Test
	public void setEvaluationTest() {

		Memory memoryContainer = new MemoryContainer("TYPE");

		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(70L, 0.3D, "TYPE3");

		((MemoryContainer) memoryContainer).setI(90L, 0.5D, 2);


		assertEquals(70L, ((MemoryContainer) memoryContainer).getI());
		((MemoryContainer) memoryContainer).setEvaluation(0.5D, 0);
		assertEquals(75L, ((MemoryContainer) memoryContainer).getI());
	}

	@Test
	public void setEvaluationLastTest() {
		Memory memoryContainer = new MemoryContainer("TYPE");
                System.out.println("-- This test will raise a warning ...");
		memoryContainer.setEvaluation(2.0);
                assertEquals(memoryContainer.getEvaluation(),null);
                memoryContainer.setI("message");
                memoryContainer.setEvaluation(2.0);
                assertEquals(memoryContainer.getEvaluation(),2.0);
	}
        
        @Test
	public void getTimestampNotValidTest() {
		Memory memoryContainer = new MemoryContainer("TYPE");
                System.out.println("-- This test will raise a warning ...");
		Long ts = memoryContainer.getTimestamp();
                assertEquals(ts,null);
                memoryContainer.setI("message");
                ts = memoryContainer.getTimestamp();
                assertTrue(ts != null);
	}

	@Test
	public void addTest() {

		Memory memoryContainer = new MemoryContainer("TYPE");

		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(70L, 0.3D, "TYPE3");
		((MemoryContainer) memoryContainer).add(new MemoryObject());


		assertEquals(3, ((MemoryContainer) memoryContainer).getAllMemories().size());
	}

	@Test
	public void getInternalTest() {

		Memory memoryContainer = new MemoryContainer("TYPE");

		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(70L, 0.3D, "TYPE3");

		assertEquals(75L, ((MemoryContainer) memoryContainer).getInternalMemory("TYPE2").getI());
		assertNull(((MemoryContainer) memoryContainer).getInternalMemory("TYPE4"));
	}

	@Test
	public void getTimestampTest() {

		Memory memoryContainer = new MemoryContainer("TYPE");

		((MemoryContainer) memoryContainer).setI(75L, 0.2D, "TYPE2");
		((MemoryContainer) memoryContainer).setI(70L, 0.3D, "TYPE3");

		assertEquals(((MemoryContainer) memoryContainer).getInternalMemory("TYPE3").getTimestamp(),
				((MemoryContainer) memoryContainer).getTimestamp());
	}
        
        @Test
        public void testMaxPolicy() {
            MemoryContainer memoryContainer = new MemoryContainer("MAX");
            memoryContainer.setPolicy(Policy.MAX);
            int m1 = memoryContainer.setI(1, 0.2D);
            int m2 = memoryContainer.setI(2, 0.4D);
            int m3 = memoryContainer.setI(3, 0.8D);
            int i = (int) memoryContainer.getI();
            assertEquals(i,3);
            memoryContainer.setEvaluation(0.1);
            i = (int) memoryContainer.getI();
            assertEquals(i,2);
            memoryContainer.setEvaluation(0.1);
            i = (int) memoryContainer.getI();
            assertEquals(i,1);
            memoryContainer.setEvaluation(0.1D,m1);
            memoryContainer.setEvaluation(0.1D,m2);
            memoryContainer.setEvaluation(0.1D,m3);
            for (int j=0;j<20;j++) {
                int m = (int) memoryContainer.getI();
                boolean ver = (m == 1 || m == 2 || m == 3);
                assertEquals(ver,true);
                //System.out.println("max: "+m);
            }
            memoryContainer.setEvaluation(0.05D,m1);
            for (int j=0;j<20;j++) {
                int m = (int) memoryContainer.getI();
                boolean ver = (m == 2 || m == 3);
                assertEquals(ver,true);
                //System.out.println("max2: "+m);
            }
            
        }
        
        @Test
        public void testMaxUniquePolicy() {
            MemoryContainer memoryContainer = new MemoryContainer("MAX");
            memoryContainer.setPolicy(Policy.MAX);
            memoryContainer.setI(1);
            Integer i = (Integer) memoryContainer.getI();
            assertEquals(i,1);
        }
        
        @Test
        public void testMinPolicy() {
            MemoryContainer memoryContainer = new MemoryContainer("MIN");
            memoryContainer.setPolicy(Policy.MIN);
            int m1 = memoryContainer.setI(1, 0.2D);
            int m2 = memoryContainer.setI(2, 0.4D);
            int m3 = memoryContainer.setI(3, 0.8D);
            int i = (int) memoryContainer.getI();
            assertEquals(i,1);
            memoryContainer.setEvaluation(0.9);
            i = (int) memoryContainer.getI();
            assertEquals(i,2);
            memoryContainer.setEvaluation(0.9);
            i = (int) memoryContainer.getI();
            assertEquals(i,3);
            memoryContainer.setEvaluation(0.1D,m1);
            memoryContainer.setEvaluation(0.1D,m2);
            memoryContainer.setEvaluation(0.1D,m3);
            for (int j=0;j<20;j++) {
                int m = (int) memoryContainer.getI();
                boolean ver = (m == 1 || m == 2 || m == 3);
                assertEquals(ver,true);
                //System.out.println("min: "+m);
            }
            memoryContainer.setEvaluation(0.2D,m1);
            for (int j=0;j<20;j++) {
                int m = (int) memoryContainer.getI();
                boolean ver = (m == 2 || m == 3);
                assertEquals(ver,true);
                //System.out.println("min2: "+m);
            }
        }
        
         
        @Test
        public void testRandomProportionalPolicy() {
            MemoryContainer memoryContainer = new MemoryContainer("RANDOMPROPORTIONAL");
            memoryContainer.setPolicy(Policy.RANDOM_PROPORTIONAL);
            memoryContainer.setI(1, 0.2D); // 14%
            memoryContainer.setI(2, 0.4D); // 28%
            memoryContainer.setI(3, 0.8D); // 57%
            int count[] = new int[3];
            for (int i=0;i<1000;i++) {
                int j = (int) memoryContainer.getI();
                count[j-1]++;
            }
            //System.out.println("[0]: "+count[0]+" [1]: "+count[1]+" [2]: "+count[2]);
            assertEquals(count[0]<count[1],true);
            assertEquals(count[1]<count[2],true);
            memoryContainer.setEvaluation(0.8D,0);
            memoryContainer.setEvaluation(0.4D,1);
            memoryContainer.setEvaluation(0.2D,2);
            count = new int[3];
            for (int i=0;i<1000;i++) {
                int j = (int) memoryContainer.getI();
                count[j-1]++;
            }
            //System.out.println("[0]: "+count[0]+" [1]: "+count[1]+" [2]: "+count[2]);
            assertEquals(count[0]>count[1],true);
            assertEquals(count[1]>count[2],true);
            memoryContainer.setI(1,0.5,0);
            memoryContainer.setI(2,0.0,1);
            memoryContainer.setI(3,0.0,2);
            for (int i=0;i<5;i++) {
                int j = (int) memoryContainer.getI();
                assertEquals(j,1);
            }
            memoryContainer.setI(1,0.0,0);
            memoryContainer.setI(2,0.5,1);
            memoryContainer.setI(3,0.0,2);
            for (int i=0;i<5;i++) {
                int j = (int) memoryContainer.getI();
                assertEquals(j,2);
            }
            memoryContainer.setI(1,0.0,0);
            memoryContainer.setI(2,0.0,1);
            memoryContainer.setI(3,0.5,2);
            for (int i=0;i<5;i++) {
                int j = (int) memoryContainer.getI();
                assertEquals(j,3);
            }
            memoryContainer.setI(1,0.0,0);
            memoryContainer.setI(2,0.0,1);
            memoryContainer.setI(3,0.0,2);
            count = new int[3];
            for (int i=0;i<30;i++) {
                int j = (int) memoryContainer.getI();
                count[j-1]++;
            }
            //System.out.println("[0]: "+count[0]+" [1]: "+count[1]+" [2]: "+count[2]);
            assertEquals(count[0]>0,true);
            assertEquals(count[1]>0,true);
            assertEquals(count[2]>0,true);
        }
        
        @Test
        public void testRandomFlat() {
            MemoryContainer memoryContainer = new MemoryContainer("RANDOMFLAT");
            memoryContainer.setPolicy(Policy.RANDOM_FLAT);
            memoryContainer.setI(1, 0.2D); // 14%
            memoryContainer.setI(2, 0.4D); // 28%
            memoryContainer.setI(3, 0.8D); // 57%
            int count[] = new int[3];
            for (int i=0;i<1000;i++) {
                int j = (int) memoryContainer.getI();
                count[j-1]++;
            }
            assertEquals(count[0]>0,true);
            assertEquals(count[1]>0,true);
            assertEquals(count[2]>0,true);
        }
        
        @Test
        public void testIteratePolicy() {
            MemoryContainer memoryContainer = new MemoryContainer("ITERATE");
            memoryContainer.setPolicy(Policy.ITERATE);
            System.out.println("-- This test will raise a warning ...");
            Integer k = (Integer) memoryContainer.getI();
            assertNull(k);
            memoryContainer.setI(1); 
            memoryContainer.setI(2); 
            memoryContainer.setI(3); 
            for (int i=0;i<9;i++) {
                int j = (int) memoryContainer.getI();
                assertEquals(j,i%3+1);
            }
        } 
        
        @Test
        public void testGetEvaluation() {
            MemoryContainer memoryContainer = new MemoryContainer("TEST");
            assertEquals(memoryContainer.get(-1),null);
            assertEquals(memoryContainer.get(0),null);
            assertEquals(memoryContainer.get(10),null);
            assertEquals(memoryContainer.getName(),"TEST");
            memoryContainer.setName("TEST-NEW");
            assertEquals(memoryContainer.getName(),"TEST-NEW");
            memoryContainer.setType("TEST-NEW");
            assertEquals(memoryContainer.getName(),"TEST-NEW");
            // Testing the getEvaluation without any included MemoryObject
            assertEquals(memoryContainer.getEvaluation(),null);
            assertEquals(memoryContainer.getEvaluation(0),null);
            assertEquals(memoryContainer.getEvaluation(1),null);
            assertEquals(memoryContainer.getPolicy(),Policy.MAX);
            Double res = memoryContainer.getEvaluation();
            assertEquals(res,null);
            memoryContainer.setI(1);
            memoryContainer.setEvaluation(0.5);
            assertEquals(memoryContainer.getEvaluation(),0.5);
            assertEquals(memoryContainer.getEvaluation(0),0.5);
            memoryContainer.setPolicy(Policy.ITERATE);
            assertEquals(memoryContainer.getPolicy(),Policy.ITERATE);
            int i = (int) memoryContainer.getI();
            assertEquals(i,1);
            i = (int) memoryContainer.getLastI();
            assertEquals(i,1);
            MemoryObject mo = (MemoryObject) memoryContainer.getLast();
            i = (int) mo.getI();
            assertEquals(i,1);
            memoryContainer.setEvaluation(0.6,0);
            assertEquals(memoryContainer.getEvaluation(),0.6);
            assertEquals(memoryContainer.getEvaluation(0),0.6);
        }
        
        @Test
        public void testGetTimestamp() {
            MemoryContainer memoryContainer = new MemoryContainer("TEST");
            // Without any initialization, the timestamp must be null
            assertEquals(memoryContainer.getTimestamp(),null);
            System.out.println("This test will raise a warning...");
            assertEquals(memoryContainer.getTimestamp(0),null);
            System.out.println("This test will raise a warning...");
            assertEquals(memoryContainer.getTimestamp(1),null);
            // after we initialize the container, the timestamp must be something different from null
            memoryContainer.setI(1);
            assertEquals(memoryContainer.getTimestamp()!=null,true);
            assertEquals(memoryContainer.getTimestamp(0)!=null,true);
            // nevertheless, if we go further, it should remain null
            System.out.println("This test will raise a warning...");
            assertEquals(memoryContainer.getTimestamp(1),null);
            assertEquals(memoryContainer.get(0).getI(),memoryContainer.getI());
        }
        
        @Test
        public void testDoubleIndirection() {
            MemoryContainer mc1 = new MemoryContainer("TEST1");
            MemoryContainer mc2 = new MemoryContainer("TEST2");
            mc2.setI(0);
            mc1.add(mc2);
            assertEquals(mc1.getI(),0);
            mc1.setI(1,0.5,0);
            assertEquals(mc1.getI(),1);
            mc1.setEvaluation(0.6,0);
            assertEquals(mc1.getEvaluation(),0.6);
        }

}
