package br.unicamp.cst.memorystorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class LamportTimeTest {
    
    @Test
    public void initialTimeTest()
    {
        LamportTime time0 = new LamportTime(123);

        assertEquals(123, time0.getTime());
    }

    @Test
    public void stringTest()
    {
        LamportTime time0 = new LamportTime(456);

        assertEquals("456", time0.toString());
    }

    @Test
    public void fromStringTest()
    {
        LamportTime time0 = new LamportTime(987);

        assertTrue(time0.equals(LamportTime.fromString(time0.toString())));
    }

    @Test
    public void incrementTest()
    {
        LamportTime time0 = new LamportTime();
        int time0Time = time0.getTime();

        LamportTime time1 = time0.increment();

        assertEquals(time0Time, time0.getTime());
        assertEquals(time0Time+1, time1.getTime());
    }

    @Test
    public void synchronizeTest()
    {
        LamportTime time0 = new LamportTime(-10);
        LamportTime time1 = new LamportTime(55);

        LamportTime timeS = LamportTime.synchronize(time0, time1);

        assertTrue(time0.lessThan(timeS));
        assertTrue(time1.lessThan(timeS));
        assertEquals(56, timeS.getTime());

        LamportTime timeS2 = LamportTime.synchronize(time1, time0);

        assertTrue(time0.lessThan(timeS2));
        assertTrue(time1.lessThan(timeS2));
        assertEquals(56, timeS2.getTime());
    }

    @Test
    public void equalsTest()
    {
        LamportTime time0 = new LamportTime(0);
        LamportTime time1 = new LamportTime(1);
        LamportTime time2 = new LamportTime(1);

        assertNotEquals(time0, time1);
        assertEquals(time1, time2);
    }

    @Test
    public void lessThanTest()
    {
        LamportTime time0 = new LamportTime(0);
        LamportTime time1 = new LamportTime(1);
        LamportTime time2 = new LamportTime(2);

        assertTrue(time0.lessThan(time1));
        assertFalse(time2.lessThan(time1));
    }

    @Test
    public void synchonizeNonLamportTest()
    {
        LogicalTime logicalTime = new LogicalTime() {

            @Override
            public LogicalTime increment() {
                throw new UnsupportedOperationException("Unimplemented method 'increment'");
            }

            @Override
            public boolean lessThan(Object o) {
                throw new UnsupportedOperationException("Unimplemented method 'lessThan'");
            }
            
        };

        LamportTime lamportTime = new LamportTime();

        assertThrows(IllegalArgumentException.class, () -> LamportTime.synchronize(lamportTime, logicalTime));
        assertThrows(IllegalArgumentException.class, () -> LamportTime.synchronize(logicalTime, lamportTime));

    }
}
