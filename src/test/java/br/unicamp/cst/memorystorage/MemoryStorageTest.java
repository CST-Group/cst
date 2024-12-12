package br.unicamp.cst.memorystorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.core.entities.RawMemory;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisConnectionException;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;

public class MemoryStorageTest {

    private static RedisClient client;
    private static StatefulRedisConnection<String, String> connection;
    private static RedisAsyncCommands<String, String> commands;

    private Mind mind;
    private Mind mind2;
    private Mind mind3;

    private List<Double> startTimes;
    private long sleepTime;

    @BeforeAll
    public static void initAll() throws Exception {
        client = RedisClient.create("redis://localhost");

        try {
            connection = client.connect();
        } catch (RedisConnectionException e) {
            assumeTrue(false);
        }

        commands = connection.async();
    }

    @BeforeEach
    public void init() throws Exception {
        commands.flushall().get();

        startTimes = new ArrayList<>();
        startTimes.add(0d);
        startTimes.add(1e3);

        sleepTime = (long) (0.75 * 1000);

        mind = new Mind();
        mind2 = new Mind();
        mind3 = new Mind();

        Field field = RawMemory.class.getDeclaredField("lastid");
        field.setAccessible(true);
        field.setLong(null, 0);
    }

    @AfterEach
    public void tearDown() throws Exception {
        mind.shutDown();
        mind2.shutDown();
        mind3.shutDown();

        commands.flushall().get();
    }

    @Test
    public void testMindName() throws Exception{
        MemoryStorageCodelet msCodelet = new MemoryStorageCodelet(mind, "Mind1");
        msCodelet.setTimeStep(50);
        mind.insertCodelet(msCodelet);

        RedisURI uri = RedisURI.Builder
                .redis("localhost", 6379)
                .build();
        RedisClient client = RedisClient.create(uri);

        MemoryStorageCodelet msCodelet2 = new MemoryStorageCodelet(mind, "Mind2", client);
        msCodelet2.setTimeStep(50);
        mind.insertCodelet(msCodelet2);

        mind.start();

        Set<String> members = commands.smembers("Mind1:nodes").get();
        assertEquals(1, members.size());
        assertTrue(members.contains("node"));

        Set<String> members2 = commands.smembers("Mind2:nodes").get();
        assertEquals(1, members2.size());
        assertTrue(members2.contains("node"));
    }

    @Test
    public void nodeEnterTest() throws Exception {
        RedisURI uri = RedisURI.Builder
                .redis("localhost", 6379)
                .build();
        RedisClient client = RedisClient.create(uri);
        
        MemoryStorageCodelet msCodelet = new MemoryStorageCodelet(mind);
        msCodelet.setTimeStep(50);
        mind.insertCodelet(msCodelet);
        mind.start();

        Thread.sleep(sleepTime);

        assertEquals("node", msCodelet.getNodeName());

        Set<String> members = commands.smembers("default_mind:nodes").get();
        assertEquals(1, members.size());
        assertTrue(members.contains("node"));

        MemoryStorageCodelet msCodelet2 = new MemoryStorageCodelet(mind2,"node2",   "default_mind", 500.0e-3, client );
        msCodelet.setTimeStep(50);
        mind2.insertCodelet(msCodelet2);
        mind2.start();

        Thread.sleep(sleepTime);

        assertEquals("node2", msCodelet2.getNodeName());

        members = commands.smembers("default_mind:nodes").get();
        assertEquals(2, members.size());
        assertTrue(members.contains("node"));
        assertTrue(members.contains("node2"));

        MemoryStorageCodelet msCodelet3 = new MemoryStorageCodelet(mind3);
        msCodelet3.setTimeStep(50);
        mind3.insertCodelet(msCodelet3);
        mind3.start();

        Thread.sleep(sleepTime);

        assertEquals("node3", msCodelet3.getNodeName());

        members = commands.smembers("default_mind:nodes").get();
        assertEquals(3, members.size());
        assertTrue(members.contains("node"));
        assertTrue(members.contains("node2"));
        assertTrue(members.contains("node3"));
    }

    @Test
    public void redisArgsTest() throws Exception {
        RedisURI uri = RedisURI.Builder
                .redis("localhost", 6379)
                .build();
        RedisClient client = RedisClient.create(uri);

        MemoryStorageCodelet msCodelet = new MemoryStorageCodelet(mind, client);
        mind.insertCodelet(msCodelet);
        mind.start();

        Thread.sleep(sleepTime);
        
        Set<String> members = commands.smembers("default_mind:nodes").get();
        assertEquals(1, members.size());
        assertTrue(members.contains("node"));
    }

    @Test
    public void transferMemoryTest() throws Exception {
        Memory memory1 = mind.createMemoryObject("Memory1", "INFO");

        MemoryStorageCodelet msCodelet = new MemoryStorageCodelet(mind);
        msCodelet.setTimeStep(50);
        mind.insertCodelet(msCodelet);
        mind.start();

        Thread.sleep(sleepTime);

        assertTrue(commands.exists("default_mind:memories:Memory1").get() >= 1);
        Map<String, String> result = commands.hgetall("default_mind:memories:Memory1").get();

        Map<String, String> expectedResult = new HashMap<>();
        expectedResult.put("name", "Memory1");
        expectedResult.put("evaluation", "0.0");
        expectedResult.put("I", "");
        expectedResult.put("id", "0");
        expectedResult.put("owner", "node");
        expectedResult.put("logical_time", "0");

        assertEquals(expectedResult, result);

        String request = "{'request':{'memory_name':'Memory1', 'node':'node1'}, 'logical_time':'0'}";
        commands.publish("default_mind:nodes:node:transfer_memory", request);

        Thread.sleep(sleepTime);

        result = commands.hgetall("default_mind:memories:Memory1").get();

        expectedResult = new HashMap<>();
        expectedResult.put("name", "Memory1");
        expectedResult.put("evaluation", "0.0");
        expectedResult.put("I", "\"INFO\"");
        expectedResult.put("id", "0");
        expectedResult.put("owner", "");

        result.remove("logical_time");
        result.remove("timestamp");

        assertEquals(expectedResult, result);
    }

    @Test
    public void msTest() throws Exception
    {
        Memory memory1 = mind.createMemoryObject("Memory1", "");

        MemoryStorageCodelet msCodelet = new MemoryStorageCodelet(mind);
        msCodelet.setTimeStep(50);
        mind.insertCodelet(msCodelet);
        mind.start();

        assertEquals("", memory1.getI());
        
        int[] info = {1, 1, 1};
        memory1.setI(info);

        Thread.sleep(sleepTime);

        Memory mind2Memory1 = mind2.createMemoryObject("Memory1", "");

        MemoryStorageCodelet msCodelet2 = new MemoryStorageCodelet(mind2);
        msCodelet2.setTimeStep(50);
        mind2.insertCodelet(msCodelet2);
        mind2.start();

        assertEquals("", mind2Memory1.getI());

        Thread.sleep(sleepTime);

        for(int i = 0; i<3; i++)
        {
            assertEquals(info[i], ((int[]) memory1.getI())[i]);
            assertEquals(info[i], Math.toIntExact((long)((ArrayList) mind2Memory1.getI()).get(i)));
        }
        

        Map<String, String> result = commands.hgetall("default_mind:memories:Memory1").get();

        Map<String, String> expectedResult = new HashMap<>();
        expectedResult.put("name", "Memory1");
        expectedResult.put("evaluation", "0.0");
        expectedResult.put("I", "[1,1,1]");
        expectedResult.put("id", "0");
        expectedResult.put("owner", "");
        
        assertTrue(result.containsKey("logical_time"));
        assertTrue(result.containsKey("timestamp"));
        result.remove("logical_time");
        result.remove("timestamp");
        assertEquals(result, expectedResult);

        memory1.setI("INFO");
        Thread.sleep(sleepTime);

        assertEquals("INFO", memory1.getI());
        assertEquals("INFO", mind2Memory1.getI());

        mind2Memory1.setI("INFO2");
        Thread.sleep(sleepTime);

        assertEquals("INFO2", memory1.getI());
        assertEquals("INFO2", mind2Memory1.getI());

        memory1.setI(1);
        Thread.sleep(sleepTime);

        assertEquals(1, memory1.getI());
        assertEquals(1, Math.toIntExact((long) mind2Memory1.getI()));

        memory1.setI(true);
        Thread.sleep(sleepTime);

        assertEquals(true, memory1.getI());
        assertEquals(true, mind2Memory1.getI());
    }

    @Test
    public void unsubscribeTest() throws Exception
    {
        MemoryStorageCodelet msCodelet = new MemoryStorageCodelet(mind);
        msCodelet.setTimeStep(50);
        
        mind.createMemoryObject("Memory", "NODE1_INFO");
        
        mind.insertCodelet(msCodelet);
        mind.start();

        Thread.sleep(sleepTime);

        mind.getRawMemory().shutDown();
        System.gc();

        Thread.sleep(sleepTime);

        commands.publish("default_mind:memories:Memory:update", "");

        Thread.sleep(sleepTime);

        Field memoriesField = msCodelet.getClass().getDeclaredField("memories");
        memoriesField.setAccessible(true);
        HashMap<String, WeakReference<Memory>> memories = (HashMap<String, WeakReference<Memory>>) memoriesField.get(msCodelet);
        
        Field listenersField = msCodelet.getClass().getDeclaredField("listeners");
        listenersField.setAccessible(true);
        HashMap<String, Consumer<String>> listeners = (HashMap<String, Consumer<String>>) listenersField.get(msCodelet);

        assertEquals(0, memories.size());
        assertEquals(2, listeners.size());

        Memory memory2 = mind2.createMemoryObject("Memory", "NODE2_INFO");
        MemoryStorageCodelet msCodelet2 = new MemoryStorageCodelet(mind2);
        msCodelet2.setTimeStep(50);
        mind2.insertCodelet(msCodelet2);
        mind2.start();

        Thread.sleep(sleepTime);

        assertEquals("NODE2_INFO", memory2.getI());
    }

}
