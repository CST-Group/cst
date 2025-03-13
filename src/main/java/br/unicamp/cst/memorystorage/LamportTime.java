package br.unicamp.cst.memorystorage;

/**
 * Logical time implementation using Lamport times.
 */
public class LamportTime implements LogicalTime {

    private int time;

    /**
     * LamportTime constructor.
     * 
     * @param initialTime time to start the clock.
     */
    public LamportTime(int initialTime)
    {
        this.time = initialTime;
    }

    /**
     * LamportTime constructor.
     */
    public LamportTime()
    {
        this(0);
    }

    /**
     * Get the clock internal time.
     * @return clock internal time.
     */
    public int getTime()
    {
        return this.time;
    }

    @Override
    public LamportTime increment() {
        return new LamportTime(this.time+1);
    }

    public static LamportTime fromString(String string)
    {
        return new LamportTime(Integer.parseInt(string));
    }

    public String toString()
    {
        return Integer.toString(time);
    }

    
    public static LamportTime synchronize(LogicalTime time0, LogicalTime time1) {
        if(!(LamportTime.class.isInstance(time0) && LamportTime.class.isInstance(time1))){
            throw new IllegalArgumentException("LamportTime can only synchonize LamportTime instances");
        }
        
        LamportTime lamportTime0 = (LamportTime) time0;  
        LamportTime lamportTime1 = (LamportTime) time1;

        int newTime = 0;
        if(time0.lessThan(time1)){
            newTime = lamportTime1.getTime();
        }
        else{
            newTime = lamportTime0.getTime();
        }

        newTime += 1;

        return new LamportTime(newTime);

    }

    @Override
    public boolean lessThan(Object o) {
        LamportTime otherTime = (LamportTime) o;

        return this.time < otherTime.getTime();
    }

    @Override
    public boolean equals(Object o)
    {
        LamportTime otherTime = (LamportTime) o;
        return this.time == otherTime.getTime();
    }
    
}
