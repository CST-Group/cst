/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.behavior.asbac;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rgpolizeli
 */
public class SynchronizationMethods {

    private static Lock synchronizerLock = new ReentrantLock();
    private static Lock destroyerLock = new ReentrantLock();
    
    public SynchronizationMethods() {
    }
    
    public static void createLock(String lockName, MemoryObject synchronizerMO){
        synchronizerLock.lock();
        Map<String,MyLock> myLocks = (Map<String,MyLock>) synchronizerMO.getI();
        try{
            MyLock ml = new MyLock();
            myLocks.put(lockName, ml);         
        } finally {
            synchronizerLock.unlock();
        }
    }
    
    private static void destroyLock(String lockName, Map<String, MyLock> myLocks){
        
        MyLock myLock = myLocks.get(lockName);

        if (myLock != null) {

            synchronized(myLock.lock){
                myLocks.remove(lockName);
            }
        }   
            
        if (isUnlockTime(myLocks)) {
            unLockAll(myLocks);
        }

    }
    
    private static boolean isUnlockTime(Map<String, MyLock> myLocks){
        for (Map.Entry<String, MyLock> entry : myLocks.entrySet()) {
            if (!entry.getValue().isLocked) { //if any lock is free
                return false;
            }
        }
        return true; //if all lock is locked
    }
    
    private static void unLockAll(Map<String, MyLock> myLocks){
        
        for (Map.Entry<String, MyLock> entry : myLocks.entrySet()) {
            synchronized(entry.getValue().lock){
                entry.getValue().unlock();
                entry.getValue().lock.notify();
            }
        }
    }
    
    
    public static void synchronize(String codeletName, MemoryObject synchronizerMO){
        synchronizerLock.lock();
        
        Map<String, MyLock> myLocks = (Map<String, MyLock>) synchronizerMO.getI();
        MyLock myLock = myLocks.get(codeletName);
        myLock.lock();
            
        if (isUnlockTime(myLocks)) {
            unLockAll(myLocks);
            synchronizerLock.unlock();
        } else{ //wait to lock

            synchronized(myLock.lock){
                try {
                    synchronizerLock.unlock();
                    while(myLock.isLocked){
                        myLock.lock.wait();
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(SynchronizationMethods.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
            
        
    }
    
    public static void synchronizeOperation(String operationName, MemoryObject synchronizerMO){
        synchronizerLock.lock();
        Map<String, MyLock> myLocks = (Map<String, MyLock>) synchronizerMO.getI();
        try{
            destroyLock(operationName,myLocks);
        } finally{
            synchronizerLock.unlock();
        }
    }
    
    public static void synchronizeExecutor(String executorName, Codelet codelet, MemoryObject synchronizerMO){
        synchronizerLock.lock();
        Map<String, MyLock> myLocks = (Map<String, MyLock>) synchronizerMO.getI();
        try{
            codelet.setLoop(Boolean.FALSE);
            destroyLock(executorName, myLocks);
        } finally{
            synchronizerLock.unlock();
        }
    }
    
    
    
    public static void synchronizeCodelet(String codeletName, MemoryObject synchronizerMO){
        ConcurrentHashMap<String, MyLock> synchronizers = (ConcurrentHashMap) synchronizerMO.getI();
        MyLock myLock = synchronizers.get(codeletName);
        
        synchronized(myLock.lock){
       
            myLock.lock();

            while(myLock.isLocked){
                try {
                    myLock.lock.wait(); //The thread releases ownership of this monitor
                } catch (Exception ex) {
                    Logger.getLogger(SynchronizationMethods.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        
        }
        
    }
}
