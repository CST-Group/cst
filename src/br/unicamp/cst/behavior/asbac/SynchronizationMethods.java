/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.behavior.asbac;

import br.unicamp.cst.core.entities.MemoryObject;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rgpolizeli
 */
public class SynchronizationMethods {

    private static final Lock SYNCHRONIZER_LOCK = new ReentrantLock();
    private static final Logger LOGGER = Logger.getLogger(SynchronizationMethods.class.getName());
    
    public SynchronizationMethods() {
    }
    
    public static Lock getSynchronizerLock(){
        return SYNCHRONIZER_LOCK;
    }
    
    public static void createLock(String lockName, MemoryObject synchronizerMO){
        getSynchronizerLock().lock();
        Map<String,MyLock> myLocks = (Map<String,MyLock>) synchronizerMO.getI();
        try{
            MyLock ml = new MyLock();
            myLocks.put(lockName, ml);
            LOGGER.log(Level.INFO, "Created lock: {0}", lockName);
        } finally {
            getSynchronizerLock().unlock();
        }
    }
    
    private static void destroyLock(String lockName, Map<String, MyLock> myLocks){
        
        MyLock myLock = myLocks.get(lockName);

        if (myLock != null) {

            synchronized(myLock.lock){
                myLocks.remove(lockName);
                LOGGER.log(Level.INFO, "Deleted lock: {0}", lockName);
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
        getSynchronizerLock().lock();
        
        Map<String, MyLock> myLocks = (Map<String, MyLock>) synchronizerMO.getI();
        MyLock myLock = myLocks.get(codeletName);
        myLock.lock();
            
        if (isUnlockTime(myLocks)) {
            unLockAll(myLocks);
            getSynchronizerLock().unlock();
        } else{ //wait to lock

            synchronized(myLock.lock){
                try {
                    getSynchronizerLock().unlock();
                    while(myLock.isLocked){
                        myLock.lock.wait();
                    }
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        }   
    }
}
