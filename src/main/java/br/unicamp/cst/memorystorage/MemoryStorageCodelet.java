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
import java.util.concurrent.ExecutorService;
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

public class MemoryStorageCodelet extends Codelet {
    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private RedisAsyncCommands<String, String> commands;
    private StatefulRedisPubSubConnection<String, String> pubsubConnection;
    HashMap<String, Consumer<String>> listeners;

    private Mind mind;
    private String nodeName;

    public String getNodeName() {
        return nodeName;
    }

    private String mindName;
    private double requestTimeout;

    private HashMap<String, WeakReference<Memory>> memories;
    private HashMap<String, Long> lastUpdate;
    private HashMap<String, LamportTime> memoryLogicalTime;
    private HashSet<String> waitingRetrieval;
    private HashMap<String, CompletableFuture<Boolean>> waitingRequestEvents;
    private ExecutorService retrieveExecutor;

    private LamportTime currentTime;
    private Gson gson;

    public MemoryStorageCodelet(Mind mind)
    {
        this(mind, "node", "default_mind", 500.0e-3);
    }

    public MemoryStorageCodelet(Mind mind, String nodeName, String mindName, double requestTimeout) {
        listeners = new HashMap<>();
        memories = new HashMap<>();
        lastUpdate = new HashMap<>();
        memoryLogicalTime = new HashMap<>();
        waitingRetrieval = new HashSet<>();
        waitingRequestEvents = new HashMap<>();
        retrieveExecutor = Executors.newFixedThreadPool(3);
        currentTime = new LamportTime();
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
            boolean isMemberResult = commands.sismember(mindNodesPath, nodeName).get();
            if(isMemberResult)
            {
                Long nodeNumber = commands.scard(mindNodesPath).get();
                nodeName = baseName + Long.toString(nodeNumber);

                isMemberResult = commands.sismember(mindNodesPath, nodeName).get();
                while (isMemberResult) {
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
            public void message(String receivedChannel, String message) {
                Consumer<String> listener = MemoryStorageCodelet.this.listeners.get(receivedChannel);

                if (listener != null) {
                    listener.accept(message);
                }
            }
        };

        pubsubConnection.addListener(listener);

        Consumer<String> handlerTransferMemory = this::handlerTransferMemory;
        subscribe(String.format("%s:nodes:%s:transfer_memory", mindName, nodeName), handlerTransferMemory);
        Consumer<String> handlerNotifyTransfer = this::handlerNotifyTransfer;
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
        HashMap<String, Memory> mindMemories = new HashMap<>();
        HashSet<String> mindMemoriesNames = new HashSet<>();
        for (Memory memory : mind.getRawMemory().getAllMemoryObjects()) {
            String memoryName = memory.getName();
            if (!memoryName.equals("")) {
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
                    Map<String, String> impostor = new HashMap<>();
                    impostor.put("name", memoryName);
                    impostor.put("evaluation", "0.0");
                    impostor.put("I", "");
                    impostor.put("id", "0");
                    impostor.put("owner", nodeName);
                    impostor.put("logical_time", currentTime.toString());

                    commands.hset(memoryPath, impostor);
                    currentTime = currentTime.increment();
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
                memoryLogicalTime.remove(memoryName);
                continue;
            }
            Memory memory = memories.get(memoryName).get();
            if (memory == null) {
                lastUpdate.remove(memoryName);
                memories.remove(memoryName);
                continue;
            }

            if (memory.getTimestamp() > lastUpdate.get(memoryName)) {
                memoryLogicalTime.put(memoryName, currentTime);
                updateMemory(memoryName);
            }
        }
    }

    private void updateMemory(String memoryName) {
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
            String messageTimeStr = commands.hget(memoryPath, "logical_time").get();
            LamportTime messageTime = LamportTime.fromString(messageTimeStr);

            LamportTime memoryTime = memoryLogicalTime.get(memoryName);


            if (memoryTime.lessThan(messageTime)) {
                retrieveExecutor.execute(() -> {
                    retrieveMemory(memory);
                });
            } else if (messageTime.lessThan(memoryTime)) {
                sendMemory(memory);
            }

            lastUpdate.put(memoryName, memory.getTimestamp());
        } catch (Exception e) {
            // TODO: handle exception
            System.err.println("Memory update error!");
            e.printStackTrace();
        }
    }

    private void sendMemory(Memory memory) {
        String memoryName = memory.getName();

        Map<String, String> memoryDict = MemoryEncoder.toDict(memory);
        memoryDict.put("owner", "");
        memoryDict.put("logical_time", memoryLogicalTime.get(memoryName).toString());

        String memoryPath = String.format("%s:memories:%s", mindName, memoryName);
        commands.hset(memoryPath, memoryDict);

        String memoryUpdatePath = memoryPath + ":update";
        commands.publish(memoryUpdatePath, "");

        currentTime = currentTime.increment();
    }

    private void retrieveMemory(Memory memory) {
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
                }

                memoryDict = commands.hgetall(memoryPath).get();
            }

            MemoryEncoder.loadMemory(memory, memoryDict);

            LamportTime messageTime = LamportTime.fromString(memoryDict.get("logical_time"));
            currentTime = LamportTime.synchronize(messageTime, currentTime);
            
            Long timestamp = memory.getTimestamp();
            lastUpdate.put(memoryName, timestamp);
            memoryLogicalTime.put(memoryName, messageTime);

            waitingRetrieval.remove(memoryName);
        } catch (Exception e) {
            // TODO handle exception
            e.printStackTrace();
        }
    }

    private void requestMemory(String memoryName, String ownerName) {
        String requestAddr = String.format("%s:nodes:%s:transfer_memory", mindName, ownerName);

        HashMap<String, String> requestDict = new HashMap<String, String>();
        requestDict.put("memory_name", memoryName);
        requestDict.put("node", nodeName);

        Map<String, Object> fullRequestDict = new HashMap<>();
        fullRequestDict.put("request", requestDict);
        fullRequestDict.put("logical_time", currentTime.toString());

        String request = gson.toJson(fullRequestDict);

        commands.publish(requestAddr, request);
    }

    private void handlerNotifyTransfer(String message) {
        Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        Map<String, String> data = gson.fromJson(message, type);

        if(data.containsKey("logical_time"))
        {
            LamportTime messageTime = LamportTime.fromString(data.get("logical_time"));
            currentTime = LamportTime.synchronize(messageTime, currentTime);
        }

        String memoryName = data.get("memory_name");

        CompletableFuture<Boolean> event = waitingRequestEvents.get(memoryName);
        if (event != null) {
            event.complete(true);

            waitingRequestEvents.remove(memoryName);
        }
    }

    private void handlerTransferMemory(String message) {
        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        Map<String, Object> data = gson.fromJson(message, type);

        if(data.containsKey("logical_time"))
        {
            LamportTime messageTime = LamportTime.fromString((String) data.get("logical_time"));
            currentTime = LamportTime.synchronize(messageTime, currentTime);
        }

        Map<String, String> request = (Map<String, String>) data.get("request");

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

        memoryLogicalTime.put(memoryName, currentTime);

        sendMemory(memory);

        Map<String, String> response = new HashMap<>(); 
        response.put("memory_name", memoryName);
        response.put("logical_time", currentTime.toString());
        String responseString = gson.toJson(response);

        String responseAddr = String.format("%s:nodes:%s:transfer_done", mindName, requestingNode);
        commands.publish(responseAddr, responseString);
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
    public void stop()
    {
        pubsubConnection.close();
        retrieveExecutor.shutdownNow();
        redisClient.shutdown();

        super.stop();
    }

    @Override
    protected void finalize() {
        pubsubConnection.close();
        retrieveExecutor.shutdownNow();
        redisClient.shutdown();
    }

}
