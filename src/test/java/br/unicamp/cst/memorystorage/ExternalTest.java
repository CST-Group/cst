package br.unicamp.cst.memorystorage;

import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;

/**
 * Code created to test the communication of CST-Java Memory Storage with other instances. 
 * At the moment, it must be executed manually.
 */
public class ExternalTest {

    private static final long SLEEP_TIME = (long) (0.75 * 1000);

    public static void main(String[] args){
        RedisClient redisClient = RedisClient.create("redis://localhost");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisAsyncCommands<String, String> commands = connection.async();
        try {
            commands.flushall().get();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        Mind mind = new Mind();
        MemoryObject memory1 = mind.createMemoryObject("Memory1", false);
        MemoryStorageCodelet ms;
        try{
            ms = new MemoryStorageCodelet(mind);
            ms.setTimeStep(100);
        } catch(Exception e)
        {
            e.printStackTrace();
            System.exit(1);
            return;
        }

        long lastTimestamp = memory1.getTimestamp();
        
        mind.insertCodelet(ms);
        mind.start();


        try{
            boolean valid = false;

            for(int i = 0; i<30; i++)
            {
                Thread.sleep(100);
                
                if (lastTimestamp != memory1.getTimestamp()) {
                    boolean info = (boolean) memory1.getI();
                    if(info)
                    {
                        valid = true;
                        break;
                    }
                }
            }

            if(!valid)
            {
                System.err.print("Could not communicate with the other CST node");
                System.exit(1);
            }
            
            memory1.setI("JAVA_INFO");

            Thread.sleep(SLEEP_TIME);
            
            String stringInfo = (String) memory1.getI();
            assert stringInfo.equals("OTHER_INFO");
            
            memory1.setI(1);
            Thread.sleep(SLEEP_TIME);

            Long longInfo = (Long) memory1.getI();
            assert longInfo == -1;

            memory1.setI(1.0);

            Thread.sleep(SLEEP_TIME);

            Double doubleInfo = (Double) memory1.getI();
            assert doubleInfo == 5.0;

            //while (true) {
            //    if (lastTimestamp != memory1.getTimestamp()) {
            //        System.out.println();
            //        System.out.println(memory1);
            //
            //        lastTimestamp = memory1.getTimestamp();
            //    }
            //}
        } catch(Exception e)
        {
            e.printStackTrace();
            System.exit(1);
            return;
        }
        

    }
    
}
