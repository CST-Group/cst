package br.unicamp.cst.memorystorage;
public interface LogicalTime {
    public abstract LogicalTime increment();

    public static LogicalTime fromString(String string)
    {
        throw new IllegalStateException("fromString not implemented in the subclass");
    }

    @Override
    public abstract String toString();

    public static LogicalTime synchronize(LogicalTime time0, LogicalTime time1)
    {
        throw new IllegalStateException("synchronize not implemented in the subclass");
    }

    @Override
    public abstract boolean equals(Object o);
    public abstract boolean lessThan(Object o);
}
