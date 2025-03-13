package br.unicamp.cst.memorystorage;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

/**
 * Synchonizes local memories with a Redis database. 
 * 
 * When using MemoryStorage, each local CST instance is called a node.
 * Memories with the same name in participating nodes are synchronized.
 * 
 * The collection of synchonized nodes is a mind.A single Redis instance can support multiple minds with unique names
 */
public class MemoryStorageCodelet extends Codelet {
    private static final String LOGICAL_TIME_FIELD = "logical_time";
    private static final String OWNER_FIELD = "owner";
    private static final String MEMORY_NAME_FIELD = "memory_name";

    private static final String MEMORY_PATH_TEMPLATE = "%s:memories:%s";

    private static final Logger LOGGER = Logger.getLogger(MemoryStorageCodelet.class.getName());

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
    private HashMap<String, LamportTime> memoryLogicalTime;
    private HashSet<String> waitingRetrieval;
    private HashMap<String, CompletableFuture<Boolean>> waitingRequestEvents;
    private ExecutorService retrieveExecutor;

    private LamportTime currentTime;
    private Gson gson;

    /**
     * MemoryStorageCodelet constructor.
     * 
     * @param mind agent mind, used to monitor memories.
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public MemoryStorageCodelet(Mind mind) throws ExecutionException, InterruptedException {
        this(mind, "node", "default_mind", 500.0e-3, RedisClient.create("redis://localhost"));
    }

    /**
     * MemoryStorageCodelet constructor.
     * 
     * @param mind agent mind, used to monitor memories.
     * @param redisClient redis client to connect.
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public MemoryStorageCodelet(Mind mind, RedisClient redisClient) throws ExecutionException, InterruptedException {
        this(mind, "node", "default_mind", 500.0e-3, redisClient);
    }

    /**
     * MemoryStorageCodelet constructor.
     * 
     * @param mind agent mind, used to monitor memories.
     * @param mindName name of the network mind.
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public MemoryStorageCodelet(Mind mind, String mindName) throws ExecutionException, InterruptedException {
        this(mind, "node", mindName, 500.0e-3, RedisClient.create("redis://localhost"));
    }

    /***
     * MemoryStorageCodelet constructor.
     * 
     * @param mind agent mind, used to monitor memories.
     * @param mindName name of the network mind.
     * @param redisClient redis client to connect.
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public MemoryStorageCodelet(Mind mind, String mindName, RedisClient redisClient)
            throws ExecutionException, InterruptedException {
        this(mind, "node", mindName, 500.0e-3, redisClient);
    }

    /**
     * MemoryStorageCodelet constructor.
     * 
     * @param mind agent mind, used to monitor memories.
     * @param nodeName name of the local node in the network.
     * @param mindName name of the network mind.
     * @param requestTimeout time before timeout when requesting a memory synchonization.
     * @param redisClient redis client to connect.
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public MemoryStorageCodelet(Mind mind, String nodeName, String mindName,
            double requestTimeout, RedisClient redisClient) throws ExecutionException, InterruptedException {
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

        this.redisClient = redisClient;
        connection = redisClient.connect();
        commands = connection.async();
        pubsubConnection = redisClient.connectPubSub();

        String baseName = nodeName;

        String mindNodesPath = String.format("%s:nodes", mindName);
        boolean isMemberResult = commands.sismember(mindNodesPath, nodeName).get();
        if (isMemberResult) {
            Long nodeNumber = commands.scard(mindNodesPath).get();
            nodeName = baseName + Long.toString(nodeNumber);

            isMemberResult = commands.sismember(mindNodesPath, nodeName).get();
            while (isMemberResult) {
                nodeNumber += 1;
                nodeName = baseName + Long.toString(nodeNumber);
                isMemberResult = commands.sismember(mindNodesPath, nodeName).get();
            }
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

    /**
     * Gets the name of the node.
     * 
     * @return node name.
     */
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public void accessMemoryObjects() { // NOSONAR
    }

    @Override
    public void calculateActivation() { // NOSONAR
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
            memories.put(memoryName, new WeakReference<>(memory));

            String memoryPath = String.format(MEMORY_PATH_TEMPLATE, mindName, memoryName);
            RedisFuture<Long> existFuture = commands.exists(memoryPath);
            try {
                if (existFuture.get() > 0) {
                    retrieveExecutor.execute(() -> retrieveMemory(memory));
                } else {
                    Map<String, String> impostor = new HashMap<>();
                    impostor.put("name", memoryName);
                    impostor.put("evaluation", "0.0");
                    impostor.put("I", "");
                    impostor.put("id", "0");
                    impostor.put(OWNER_FIELD, nodeName);
                    impostor.put(LOGICAL_TIME_FIELD, currentTime.toString());

                    commands.hset(memoryPath, impostor);
                    currentTime = currentTime.increment();
                }
            } catch (ExecutionException | InterruptedException e) { // NOSONAR
                LOGGER.log(Level.SEVERE, "Can't send memory to Redis server");
            }

            String subscribeUpdatePath = String.format("%s:memories:%s:update", mindName, memoryName);
            Consumer<String> handlerUpdate = message -> this.updateMemory(memoryName);
            subscribe(subscribeUpdatePath, handlerUpdate);

        }

        Set<String> toUpdate = lastUpdate.keySet();
        for (String memoryName : toUpdate) {
            if (!memories.containsKey(memoryName) || memories.get(memoryName).get() == null) {
                lastUpdate.remove(memoryName);
                memoryLogicalTime.remove(memoryName);
                continue;
            }
            Memory memory = memories.get(memoryName).get();

            if (memory.getTimestamp() > lastUpdate.get(memoryName)) {
                memoryLogicalTime.put(memoryName, currentTime);
                updateMemory(memoryName);
            }
        }
    }

    /**
     * Updates a memory, sending or retrieving the memory data
     * to/from the database.
     * 
     * Performs a time comparison with the local data and storage
     * data to decide whether to send or retrieve the data.
     * 
     * @param memoryName name of the memory to synchonize.
     */
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
            String memoryPath = String.format(MEMORY_PATH_TEMPLATE, mindName, memoryName);
            String messageTimeStr = commands.hget(memoryPath, LOGICAL_TIME_FIELD).get();
            LamportTime messageTime = LamportTime.fromString(messageTimeStr);

            LamportTime memoryTime = memoryLogicalTime.get(memoryName);

            if (memoryTime.lessThan(messageTime)) {
                retrieveExecutor.execute(() -> retrieveMemory(memory));
            } else if (messageTime.lessThan(memoryTime)) {
                sendMemory(memory);
            }

            lastUpdate.put(memoryName, memory.getTimestamp());
        } catch (ExecutionException | InterruptedException e) { // NOSONAR
            LOGGER.log(Level.SEVERE, "Can't retrieve information from Redis server");
        }
    }

    /**
     * Sends a memory data to the storage.
     * 
     * @param memory memory to send.
     */
    private void sendMemory(Memory memory) {
        String memoryName = memory.getName();

        Map<String, String> memoryDict = MemoryEncoder.toDict(memory);
        memoryDict.put(OWNER_FIELD, "");
        memoryDict.put(LOGICAL_TIME_FIELD, memoryLogicalTime.get(memoryName).toString());

        String memoryPath = String.format(MEMORY_PATH_TEMPLATE, mindName, memoryName);
        commands.hset(memoryPath, memoryDict);

        String memoryUpdatePath = memoryPath + ":update";
        commands.publish(memoryUpdatePath, "");

        currentTime = currentTime.increment();
    }

    /**
     * Retrieves a memory data from the storage.
     * 
     * Blocks the application, it is advisable to use a separate thread to call the method.
     * 
     * @param memory memory to retrieve data.
     */
    private void retrieveMemory(Memory memory) {
        String memoryName = memory.getName();

        if (waitingRetrieval.contains(memoryName)) {
            return;
        }
        waitingRetrieval.add(memoryName);

        String memoryPath = String.format(MEMORY_PATH_TEMPLATE, mindName, memoryName);
        Map<String, String> memoryDict;
        try {
            memoryDict = commands.hgetall(memoryPath).get();
            String owner = memoryDict.get(OWNER_FIELD);

            if (!owner.equals("")) {
                CompletableFuture<Boolean> event = new CompletableFuture<>();
                waitingRequestEvents.put(memoryName, event);
                requestMemory(memoryName, owner);

                try { // NOSONAR
                    boolean eventResult = event.get((long) (requestTimeout * 1000), TimeUnit.MILLISECONDS);
                    if (!eventResult) {
                        sendMemory(memory);
                    }
                } catch (TimeoutException e) {
                    sendMemory(memory);
                }

                memoryDict = commands.hgetall(memoryPath).get();
            }

            MemoryEncoder.loadMemory(memory, memoryDict);

            LamportTime messageTime = LamportTime.fromString(memoryDict.get(LOGICAL_TIME_FIELD));
            currentTime = LamportTime.synchronize(messageTime, currentTime);

            Long timestamp = memory.getTimestamp();
            lastUpdate.put(memoryName, timestamp);
            memoryLogicalTime.put(memoryName, messageTime);

            waitingRetrieval.remove(memoryName);
        } catch (ExecutionException | InterruptedException e) { // NOSONAR
            LOGGER.log(Level.SEVERE, "Can't send memory to Redis server");
        }
    }

    /**
     * Requests another node to send its local memory to storage.
     * 
     * @param memoryName name of the memory to request.
     * @param ownerName node owning the memory.
     */
    private void requestMemory(String memoryName, String ownerName) {
        String requestAddr = String.format("%s:nodes:%s:transfer_memory", mindName, ownerName);

        HashMap<String, String> requestDict = new HashMap<>();
        requestDict.put(MEMORY_NAME_FIELD, memoryName);
        requestDict.put("node", nodeName);

        Map<String, Object> fullRequestDict = new HashMap<>();
        fullRequestDict.put("request", requestDict);
        fullRequestDict.put(LOGICAL_TIME_FIELD, currentTime.toString());

        String request = gson.toJson(fullRequestDict);

        commands.publish(requestAddr, request);
    }

    /**
     * Handles a message in the notify transfer channel.
     * 
     * @param message message received in the channel.
     */
    private void handlerNotifyTransfer(String message) {
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        Map<String, String> data = gson.fromJson(message, type);

        if (data.containsKey(LOGICAL_TIME_FIELD)) {
            LamportTime messageTime = LamportTime.fromString(data.get(LOGICAL_TIME_FIELD));
            currentTime = LamportTime.synchronize(messageTime, currentTime);
        }

        String memoryName = data.get(MEMORY_NAME_FIELD);

        CompletableFuture<Boolean> event = waitingRequestEvents.get(memoryName);
        if (event != null) {
            event.complete(true);

            waitingRequestEvents.remove(memoryName);
        }
    }

    /**
     * Handles a message in the transfer memory channel.
     * 
     * @param message message received in the channel.
     */
    @SuppressWarnings("unchecked")
    private void handlerTransferMemory(String message) {
        Type type = new TypeToken<HashMap<String, Object>>() {
        }.getType();
        Map<String, Object> data = gson.fromJson(message, type);

        if (data.containsKey(LOGICAL_TIME_FIELD)) {
            LamportTime messageTime = LamportTime.fromString((String) data.get(LOGICAL_TIME_FIELD));
            currentTime = LamportTime.synchronize(messageTime, currentTime);
        }

        Map<String, String> request;
        try {
            request = (Map<String, String>) data.get("request");
        } catch (ClassCastException e) {
            LOGGER.warning("Transfer memory request is not valid");
            return;
        }

        String memoryName = request.get(MEMORY_NAME_FIELD);
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
        response.put(MEMORY_NAME_FIELD, memoryName);
        response.put(LOGICAL_TIME_FIELD, currentTime.toString());
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
    public synchronized void stop() {
        pubsubConnection.close();
        retrieveExecutor.shutdownNow();
        redisClient.shutdown();

        super.stop();
    }

}
