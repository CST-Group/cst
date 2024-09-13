package br.unicamp.cst.memorystorage;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.Mind;

import java.util.HashMap;
import java.util.function.Consumer;

import io.lettuce.core.RedisClient;
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

    private Mind mind;
    private String nodeName;
    private String mindName;
    private float requestTimeout;

    HashMap<String, Consumer<String>> listeners;

    public static void main(String args[]) {
        MemoryStorage ms = new MemoryStorage();

        while (true) {

        }
    }

    public MemoryStorage() {
        listeners = new HashMap<String, Consumer<String>>();

        redisClient = RedisClient.create("redis://localhost");
        connection = redisClient.connect();
        commands = connection.async();
        pubsubConnection = redisClient.connectPubSub();

        mindName = "default_mind";
        nodeName = "node";

        
        
        RedisPubSubListener<String, String> listener = new RedisPubSubAdapter<String, String>() {
            @Override
            public void message(String received_channel, String message) {
                Consumer<String> listener = MemoryStorage.this.listeners.get(received_channel);

                System.out.println(received_channel);
                System.out.println(listener);

                if(listener != null)
                {
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

    public MemoryStorage(Mind mind, String nodeName, String mindName, float requestTimeout) {
        this.mind = mind;
        this.nodeName = nodeName;
        this.mindName = mindName;
        this.requestTimeout = requestTimeout;

        redisClient = RedisClient.create();
        connection = redisClient.connect();
        commands = connection.async();

        commands.sadd(String.format("%s:nodes", mindName), nodeName);

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

    }

    
    private void sendMemory(Memory memory) {
        String memoryName = memory.getName();

    }

    private void handlerTransferMemory(String message) {

    }

    private void handlerNotifyTransfer(String message) {

    }


    private void subscribe(String channel, Consumer<String> handler) 
    {
        listeners.put(channel, handler);
        pubsubConnection.async().subscribe(channel);
    }

    private void unsubscribe(String channel)
    {
        pubsubConnection.async().unsubscribe(channel);
        listeners.remove(channel);
    }

    @Override
    protected void finalize() {
        redisClient.shutdown();
    }

}
