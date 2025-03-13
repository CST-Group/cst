package br.unicamp.cst.memorystorage;

/**
 * A logical time for distributed communication.
 */
public interface LogicalTime {
    
    /**
     * Returns a time with the self time incremented by one. 
     * 
     * @return incremented time.
     */
    public abstract LogicalTime increment();

    /**
     * Creates a instance from a string.
     * 
     * @param string String to create time, generated with LogicalTime.toString().
     * @return Created time.
     */
    public static LogicalTime fromString(String string)
    {
        throw new IllegalStateException("fromString not implemented in the subclass");
    }

    @Override
    public abstract String toString();

    /**
     * Compares two times, and return the current time.
     * 
     * @param time0 first time to compare.
     * @param time1 second time to compare. 
     * @return current time.
     */
    public static LogicalTime synchronize(LogicalTime time0, LogicalTime time1)
    {
        throw new IllegalStateException("synchronize not implemented in the subclass");
    }

    @Override
    public abstract boolean equals(Object o);
    public abstract boolean lessThan(Object o);
}
