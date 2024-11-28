package br.unicamp.cst.memorystorage;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ch.qos.logback.classic.LoggerContext;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

public class MemoryStorage extends Codelet {
    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private RedisAsyncCommands<String, String> commands;
    private StatefulRedisPubSubConnection<String, String> pubsubConnection;
    HashMap<String, Consumer<String>> listeners;

    private Mind mind;
    private String nodeName;
    private String mindName;
    private double requestTimeout;

    private HashMap<String, WeakReference<Memory>> memories;
    private HashMap<String, Long> lastUpdate;
    private HashSet<String> waitingRetrieval;
    private HashMap<String, CompletableFuture<Boolean>> waitingRequestEvents;
    private Executor retrieveExecutor;

    private Gson gson;

    public static void main(String args[]) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        for (ch.qos.logback.classic.Logger logger : loggerContext.getLoggerList()) {
            logger.setLevel(ch.qos.logback.classic.Level.ERROR);
        }

        RedisClient redisClient = RedisClient.create("redis://localhost");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisAsyncCommands<String, String> commands = connection.async();
        try {
            commands.flushall().get();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        String mindName = "default_mind";

        try {
            Mind mind = new Mind();
            MemoryObject memory1 = mind.createMemoryObject("Memory1", "");
            String nodeName = "node0";

            MemoryStorage ms = new MemoryStorage(mind, nodeName, mindName, 500.0e-3);
            ms.timeStep = 100;

            mind.insertCodelet(ms);
            mind.start();

            Thread.sleep(1000);

            Mind mind2 = new Mind();
            MemoryObject mind2_memory1 = mind2.createMemoryObject("Memory1", "");
            MemoryStorage mind2_ms = new MemoryStorage(mind2, nodeName, mindName, 500.0e-3);
            mind2_ms.timeStep = 100;

            mind2.insertCodelet(mind2_ms);
            mind2.start();

            System.out.println(memory1);
            System.out.println(mind2_memory1);

            Thread.sleep(1000);

            memory1.setI("INFO");

            System.out.println();
            System.out.println(memory1);
            System.out.println(mind2_memory1);

            Thread.sleep(1000);

            System.out.println();
            System.out.println(memory1);
            System.out.println(mind2_memory1);

            long lastTimestamp = memory1.getTimestamp();

            while (true) {
                if (lastTimestamp != memory1.getTimestamp()) {
                    System.out.println();
                    System.out.println(memory1);

                    lastTimestamp = memory1.getTimestamp();
                }
            }

            // commands.flushall().get();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public MemoryStorage(Mind mind, String nodeName, String mindName, double requestTimeout) {
        listeners = new HashMap<String, Consumer<String>>();
        memories = new HashMap<String, WeakReference<Memory>>();
        lastUpdate = new HashMap<String, Long>();
        waitingRetrieval = new HashSet<String>();
        waitingRequestEvents = new HashMap<String, CompletableFuture<Boolean>>();
        retrieveExecutor = Executors.newFixedThreadPool(3);
        gson = new Gson();

        this.mind = mind;
        this.mindName = mindName;
        this.requestTimeout = requestTimeout;

        redisClient = RedisClient.create("redis://localhost");
        connection = redisClient.connect();
        commands = connection.async();
        pubsubConnection = redisClient.connectPubSub();

        String baseName = nodeName;

        String mindNodesPath = String.format("%s:nodes", mindName);
        try {
            if(commands.sismember(mindNodesPath, nodeName).get())
            {
                Long nodeNumber = commands.scard(mindNodesPath).get();
                nodeName = baseName + Long.toString(nodeNumber);
                while (commands.sismember(mindNodesPath, nodeName).get()) {
                    nodeNumber += 1;
                    nodeName = baseName+Long.toString(nodeNumber);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        this.nodeName = nodeName;

        commands.sadd(mindNodesPath, nodeName);

        RedisPubSubListener<String, String> listener = new RedisPubSubAdapter<String, String>() {
            @Override
            public void message(String received_channel, String message) {
                Consumer<String> listener = MemoryStorage.this.listeners.get(received_channel);

                if (listener != null) {
                    listener.accept(message);
                }
            }
        };

        pubsubConnection.addListener(listener);

        Consumer<String> handlerTransferMemory = message -> this.handlerTransferMemory(message);
        subscribe(String.format("%s:nodes:%s:transfer_memory", mindName, nodeName), handlerTransferMemory);
        Consumer<String> handlerNotifyTransfer = message -> this.handlerNotifyTransfer(message);
        subscribe(String.format("%s:nodes:%s:transfer_done", mindName, nodeName), handlerNotifyTransfer);
    }

    @Override
    public void accessMemoryObjects() {
    }

    @Override
    public void calculateActivation() {
    }

    @Override
    public void proc() {
        HashMap<String, Memory> mindMemories = new HashMap<String, Memory>();
        HashSet<String> mindMemoriesNames = new HashSet<String>();
        for (Memory memory : mind.getRawMemory().getAllMemoryObjects()) {
            String memoryName = memory.getName();
            if (memoryName != "") {
                mindMemories.put(memoryName, memory);
                mindMemoriesNames.add(memoryName);
            }
        }

        Set<String> memoriesNames = this.memories.keySet();

        mindMemoriesNames.removeAll(memoriesNames);
        Set<String> difference = mindMemoriesNames;
        for (String memoryName : difference) {
            Memory memory = mindMemories.get(memoryName);
            memories.put(memoryName, new WeakReference<Memory>(memory));

            String memoryPath = String.format("%s:memories:%s", mindName, memoryName);
            RedisFuture<Long> existFuture = commands.exists(memoryPath);
            try {
                if (existFuture.get() > 0) {
                    retrieveExecutor.execute(() -> {
                        retrieveMemory(memory);
                    });
                } else {
                    Map<String, String> impostor = new HashMap<String, String>();
                    impostor.put("name", memoryName);
                    impostor.put("evaluation", "0.0");
                    impostor.put("I", "");
                    impostor.put("id", "0");
                    impostor.put("owner", nodeName);

                    commands.hset(memoryPath, impostor);
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

            String subscribeUpdatePath = String.format("%s:memories:%s:update", mindName, memoryName);
            Consumer<String> handlerUpdate = message -> this.updateMemory(memoryName);
            subscribe(subscribeUpdatePath, handlerUpdate);

        }

        Set<String> toUpdate = lastUpdate.keySet();
        for (String memoryName : toUpdate) {
            if (!memories.containsKey(memoryName)) {
                lastUpdate.remove(memoryName);
                continue;
            }
            Memory memory = memories.get(memoryName).get();
            if (memory == null) {
                lastUpdate.remove(memoryName);
                memories.remove(memoryName);
                continue;
            }

            if (memory.getTimestamp() > lastUpdate.get(memoryName)) {
                updateMemory(memoryName);
            }
        }
    }

    private void updateMemory(String memoryName) {
        System.out.println(mindName + ":" + nodeName + " Update memory: " + memoryName);

        String memoryUpdatePath = String.format("%s:memories:%s:update", mindName, memoryName);

        if (!memories.containsKey(memoryName)) {
            unsubscribe(memoryUpdatePath);
            return;
        }
        Memory memory = memories.get(memoryName).get();
        if (memory == null) {
            unsubscribe(memoryUpdatePath);
            memories.remove(memoryName);
            return;
        }

        try {
            String memoryPath = String.format("%s:memories:%s", mindName, memoryName);
            Long timestamp = Long.parseLong(commands.hget(memoryPath, "timestamp").get());

            Long memoryTimestamp = memory.getTimestamp();

            if (memoryTimestamp < timestamp) {
                retrieveExecutor.execute(() -> {
                    retrieveMemory(memory);
                });
            } else if (memoryTimestamp > timestamp) {
                sendMemory(memory);
            }

            lastUpdate.put(memoryName, memoryTimestamp);
        } catch (Exception e) {
            // TODO: handle exception
            System.err.println("Memory update error!");
            e.printStackTrace();
        }
    }

    private void sendMemory(Memory memory) {
        System.out.println(mindName + ":" + nodeName + " Send memory: " + memory.getName());

        String memoryName = memory.getName();
        Long memoryTimestamp = memory.getTimestamp();

        HashMap<String, String> memoryDict = new HashMap<String, String>();

        memoryDict.put("timestamp", memoryTimestamp.toString());
        memoryDict.put("evaluation", memory.getEvaluation().toString());
        memoryDict.put("name", memoryName);
        memoryDict.put("id", memory.getId().toString());
        memoryDict.put("owner", "");

        Object info = memory.getI();
        memoryDict.put("I", gson.toJson(info));

        String memoryPath = String.format("%s:memories:%s", mindName, memoryName);
        commands.hset(memoryPath, memoryDict);

        String memoryUpdatePath = memoryPath + ":update";
        commands.publish(memoryUpdatePath, "");

        lastUpdate.put(memoryName, memoryTimestamp);
    }

    private void retrieveMemory(Memory memory) {
        System.out.println(mindName + ":" + nodeName + " Retrieve memory: " + memory.getName());

        String memoryName = memory.getName();

        if (waitingRetrieval.contains(memoryName)) {
            return;
        }
        waitingRetrieval.add(memoryName);

        String memoryPath = String.format("%s:memories:%s", mindName, memoryName);
        Map<String, String> memoryDict;
        try {
            memoryDict = commands.hgetall(memoryPath).get();
            String owner = memoryDict.get("owner");

            if (owner != "") {
                CompletableFuture<Boolean> event = new CompletableFuture<Boolean>();
                waitingRequestEvents.put(memoryName, event);
                requestMemory(memoryName, owner);

                try {
                    if (!event.get((long) (requestTimeout * 1000), TimeUnit.MILLISECONDS)) {
                        sendMemory(memory);
                    }
                } catch (Exception e) {
                    // sendMemory(memory);
                    System.out.println(mindName + ":" + nodeName + " Memory request error");
                }

                memoryDict = commands.hgetall(memoryPath).get();
            }

            memory.setEvaluation(Double.parseDouble(memoryDict.get("evaluation")));
            memory.setId(Long.parseLong(memoryDict.get("id")));

            String infoJSON = memoryDict.get("I");
            Object info = gson.fromJson(infoJSON, Object.class);
            memory.setI(info);

            Long timestamp = memory.getTimestamp();
            lastUpdate.put(memoryName, timestamp);

            waitingRetrieval.remove(memoryName);
        } catch (Exception e) {
            // TODO handle exception
            e.printStackTrace();
        }
    }

    private void requestMemory(String memoryName, String ownerName) {
        System.out.println(mindName + ":" + nodeName + " Request memory: " + memoryName);

        String requestAddr = String.format("%s:nodes:%s:transfer_memory", mindName, ownerName);

        HashMap<String, String> requestDict = new HashMap<String, String>();
        requestDict.put("memory_name", memoryName);
        requestDict.put("node", nodeName);
        String request = gson.toJson(requestDict);

        commands.publish(requestAddr, request);
    }

    private void handlerNotifyTransfer(String message) {
        System.out.println(mindName + ":" + nodeName + " Transfer done: " + message);

        String memoryName = message;

        CompletableFuture<Boolean> event = waitingRequestEvents.get(memoryName);
        if (event != null) {
            event.complete(true);

            waitingRequestEvents.remove(memoryName);
        }
    }

    private void handlerTransferMemory(String message) {
        System.out.println(mindName + ":" + nodeName + " Transfer memory: " + message);

        Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        Map<String, String> request = gson.fromJson(message, type);

        String memoryName = request.get("memory_name");
        String requestingNode = request.get("node");

        Memory memory;
        WeakReference<Memory> memoryReference = memories.get(memoryName);
        if (memoryReference == null || memoryReference.get() == null) {
            memory = new MemoryObject();
            memory.setName(memoryName);
        } else {
            memory = memoryReference.get();
        }

        sendMemory(memory);

        String responseAddr = String.format("%s:nodes:%s:transfer_done", mindName, requestingNode);
        commands.publish(responseAddr, memoryName);
    }

    private void subscribe(String channel, Consumer<String> handler) {
        listeners.put(channel, handler);
        pubsubConnection.async().subscribe(channel);
    }

    private void unsubscribe(String channel) {
        pubsubConnection.async().unsubscribe(channel);
        listeners.remove(channel);
    }

    @Override
    protected void finalize() {
        redisClient.shutdown();
    }

}
