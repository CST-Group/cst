package br.unicamp.cst.memorystorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class LogicalTimeTest {

    @Test
    public void fromStringNotImplementedTest()
    {
        assertThrows(IllegalStateException.class, () -> LogicalTime.fromString("null"));
    }

    @Test
    public void synchronizeNotImplementedTest()
    {
        LamportTime time = new LamportTime();

        assertThrows(IllegalStateException.class, () -> LogicalTime.synchronize(time, time));
    }
   
}
