package br.unicamp.cst.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.Mind;

public class CodeletsProfiler implements Runnable {
	
	private static Collection<String> csvColumns = Arrays.asList("MindIdentifier;Time;codeletName;ThreadName;Activation;Threshold;isLoop;TimeStep;isProfiling;"+
										";MemoryName;Evaluation;Info;Timestamp"+System.getProperty("line.separator"));

	private Mind m;

	private String filePath;

	private String fileName;
	
	private double batchSize;
	
	private String mindIdentifier;
	
    private Long  intervalTimeMillis;
    
	private Integer queueSize;
	
	private long lastTimeMillis;
	
	private static ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>(csvColumns);  
	
	private Thread worker;
    private AtomicBoolean running = new AtomicBoolean(false);



	public CodeletsProfiler(Mind m, String filePath, String fileName, String mindIdentifier, Integer queueSize) {
		super();
		this.m = m;
		this.filePath = filePath;
		this.fileName = fileName;
		this.queueSize = queueSize;
		this.mindIdentifier = mindIdentifier;

	}
	
	public CodeletsProfiler(Mind m, String filePath, String fileName, String mindIdentifier, Long intervalTimeMillis) {
		super();
		this.m = m;
		this.filePath = filePath;
		this.fileName = fileName;
		this.mindIdentifier = mindIdentifier;
		this.intervalTimeMillis = intervalTimeMillis;
		this.lastTimeMillis = System.currentTimeMillis();

	}
	
	public CodeletsProfiler(Mind m, String filePath, String fileName, String mindIdentifier,Integer queueSize, Long intervalTimeMillis) {
		super();
		this.m = m;
		this.filePath = filePath;
		this.fileName = fileName;
		this.mindIdentifier = mindIdentifier;
		this.intervalTimeMillis = intervalTimeMillis;
		this.lastTimeMillis = System.currentTimeMillis();
		this.queueSize = queueSize;

	}
	
	private void createFile() {
		if (m != null && filePath != null && fileName != null) {
			BufferedWriter writer = null;
	        try {
                    
                     File directory = new File(filePath);
                     if (! directory.exists()){
                            directory.mkdir();
                        // If you require it to make the entire directory path including parents,
                        // use directory.mkdirs(); here instead.
                    }
                    
	            File profilerFile = new File(filePath+fileName);

	            // This will output the full path where the file will be written to...
	            //System.out.println("Creating log with profile at ... "+logFile.getCanonicalPath());
	            
	            writer = new BufferedWriter(new FileWriter(profilerFile, true));
	            
	            for (String line : queue) {
        			writer.write(line);
        			queue.remove(line);
        		}
	            
	        } catch (Exception e) {
	            
	            e.printStackTrace();
	            
	        } finally {
	            try {
	                // Close the writer regardless of what happens...
	                writer.close();
	            } catch (Exception e) {
	 	            e.printStackTrace();
	            }
	        }
		}
		
	}
	
	private void fillQueue() {
        String textBlock = new String(); 
        for (Codelet c : m.getCodeRack().getAllCodelets()) {
        	
        	textBlock = mindIdentifier +";"+System.currentTimeMillis()+";"+c.getName()+";"+c.getThreadName()+";"+c.getActivation()+";"+
        				c.getThreshold()+";"+c.isLoop()+";"+c.getTimeStep()+";"+c.isProfiling()+";;;;"+System.getProperty("line.separator");
        	
        	queue.add(textBlock);
        	
        	for (Memory mInput : c.getInputs()) {
        		textBlock = mindIdentifier +";;;;;;;;;"+mInput.getName()+";"+mInput.getEvaluation()+";"+mInput.getI()+";"+mInput.getTimestamp()+System.getProperty("line.separator");
        		queue.add(textBlock);
        	}
        	
        	for (Memory mOutput : c.getOutputs()) {
        		textBlock = mindIdentifier +";;;;;;;;;"+mOutput.getName()+";"+mOutput.getEvaluation()+";"+mOutput.getI()+";"+mOutput.getTimestamp()+System.getProperty("line.separator");
        		queue.add(textBlock);
        	}
        	
        	for (Memory mBroadcast : c.getBroadcast()) {
        		textBlock = mindIdentifier +";;;;;;;;;"+mBroadcast.getName()+";"+mBroadcast.getEvaluation()+";"+mBroadcast.getI()+";"+mBroadcast.getTimestamp()+System.getProperty("line.separator");
        		queue.add(textBlock);
        	}
        	
        	
        	//If the one of the 
        	long currentTime = System.currentTimeMillis();
        	if (queueSize != null && intervalTimeMillis != null) {
        		if (queue.size() > queueSize.intValue() || (currentTime - lastTimeMillis) > intervalTimeMillis.longValue()) {
            		this.createFile();
            		lastTimeMillis = System.currentTimeMillis();
        		}
        	} else if (queueSize != null && queue.size() > queueSize.intValue()) {
        		this.createFile();
        	} else if (intervalTimeMillis != null && (currentTime - lastTimeMillis) > intervalTimeMillis.longValue()) {
        		this.createFile();
        		lastTimeMillis = System.currentTimeMillis();
        	}
        
        }
		
	}
	
	public void start() {
        worker = new Thread(this);
        worker.start();
    }
	
	public void interrupt() {
        running.set(false);
        worker.interrupt();
    }

    boolean isRunning() {
        return running.get();
    }

    boolean isStopped() {
        return !running.get();
    }


	@Override
	public void run() {
		running.set(true);
        while (running.get()) {
            try {
            	this.fillQueue();
            } catch (Exception e){
                Thread.currentThread().interrupt();
                System.out.println(
                  "Thread was interrupted, Failed to complete operation");
            }
        }
	
	}

}
