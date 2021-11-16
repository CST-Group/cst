package br.unicamp.cst.core.entities;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author wander
 *
 */
public class MemoryTest {

    Memory testMemory = new Memory() {
        Object I = null;
        Double evaluation = 0.0;
        String name = "";
        Long timestamp = 10L;

        @Override
        public Object getI() {
            return this.I;
        }

        @Override
        public int setI(Object info) {
            this.I = info;
            return 0;
        }

        @Override
        public Double getEvaluation() {
            return this.evaluation;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public void setType(String type) {
            this.name = type;
        }

        @Override
        public void setEvaluation(Double eval) {
            this.evaluation = eval;
        }

        @Override
        public Long getTimestamp() {
            return this.timestamp;
        }

		@Override
		public void addMemoryObserver(MemoryObserver memoryObserver) {
			// TODO Auto-generated method stub
			
		}
    };

    @Test
    public void getSetITest(){
        assertNull(testMemory.getI());

        Double testValue = 100.0;
        testMemory.setI(testValue);

        assertEquals(testValue, testMemory.getI());

        List<Memory> testList= Arrays.asList(new MemoryObject(), new MemoryObject());
        testMemory.setI(testList);

        assertEquals(testList, testMemory.getI());
    }

    @Test
    public void getSetTypeTest(){
        testMemory.setType("TYPE");

        assertEquals("TYPE", testMemory.getName());
    }

    @Test
    public void getSetEvalTest(){

        Double testValue = 100.0;
        testMemory.setEvaluation(testValue);

        assertEquals(testValue, testMemory.getEvaluation());
    }

    @Test
    public void getTimestampTest(){
        assertEquals(10L, (long)testMemory.getTimestamp());
    }

}
