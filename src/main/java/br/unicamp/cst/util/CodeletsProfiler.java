package br.unicamp.cst.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.atomic.AtomicBoolean;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.Mind;

public class CodeletsProfiler implements Runnable {

	private Mind m;

	private String filePath;

	private String fileName;
	
	private double batchSize;
	
	private Thread worker;
    private AtomicBoolean running = new AtomicBoolean(false);
    private int interval;


	public CodeletsProfiler(Mind m, String filePath, String fileName, double batchSizeInMB) {
		super();
		this.m = m;
		this.filePath = filePath;
		this.fileName = fileName;
		this.batchSize = batchSizeInMB * 1024D;

	}

	private void createFile() {
		
		if (m != null && filePath != null && fileName != null) {
			BufferedWriter writer = null;
			StringBuffer text = null;
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

	            text = new StringBuffer();
	            System.out.println(">>>>>>>>>>>>> BEFORE text.capacity() " + text.capacity() );
	            System.out.println("------ this.batchSize " + this.batchSize);
	            for (Codelet c : m.getCodeRack().getAllCodelets()) {
	            	
	            	text.append("------------------"+c.getName()+"----------------------");
	            	text.append(System.getProperty("line.separator"));
	            	
	            	text.append("Time: " + System.currentTimeMillis());
	            	text.append(System.getProperty("line.separator"));
	            	
	            	text.append("    * Name: " + c.getName());
	            	text.append(System.getProperty("line.separator"));
	            	
	            	text.append("    * Thread Name: " + c.getThreadName());
	            	text.append(System.getProperty("line.separator"));
	            	
	            	text.append("    * activation: " + c.getActivation());
	            	text.append(System.getProperty("line.separator"));
	            	
	            	text.append("    * threshold: " + c.getThreshold());
	            	text.append(System.getProperty("line.separator"));
	            	
	            	text.append("    * loop: " + c.isLoop());
	            	text.append(System.getProperty("line.separator"));
	            	
	            	text.append("    * timeStep: " + c.getTimeStep());
	            	text.append(System.getProperty("line.separator"));
	            	
	            	text.append("    * isProfiling(): " + c.isProfiling());
	            	text.append(System.getProperty("line.separator"));
	            	text.append(System.getProperty("line.separator"));
	            	
	            	text.append("+++++++++Input Memory++++++++++++++");
	            	text.append(System.getProperty("line.separator"));
	            	
	            	for (Memory mInput : c.getInputs()) {
	            		text.append("      - Input Memory Name: " + mInput.getName()); 
	            		text.append(System.getProperty("line.separator"));
	            		
	            		text.append("      - Input Memory Evaluation: " + mInput.getEvaluation()); 
	            		text.append(System.getProperty("line.separator"));
	            		
	            		text.append("      - Input Memory Info: " + mInput.getI());
	            		text.append(System.getProperty("line.separator"));
	            		text.append(System.getProperty("line.separator"));
	            	}
	            	
	            	text.append("+++++++++++++++++++++++");
	            	text.append(System.getProperty("line.separator"));
	            	text.append(System.getProperty("line.separator"));
	            	
	            	text.append("*********Output Memory***********");
	            	text.append(System.getProperty("line.separator"));
	            	
	            	for (Memory mOutput : c.getOutputs()) {
	            		text.append("      - Output Memory Name: " + mOutput.getName());
	            		text.append(System.getProperty("line.separator"));
	            		
	            		text.append("      - Output Memory Evaluation: " + mOutput.getEvaluation()); 
	            		text.append(System.getProperty("line.separator"));
	            		
	            		text.append("      - Output Memory Info: " + mOutput.getI());
	            		text.append(System.getProperty("line.separator"));
	            		text.append(System.getProperty("line.separator"));
	            	}
	            	text.append("*********************");
	            	text.append(System.getProperty("line.separator"));
	            	text.append(System.getProperty("line.separator"));
	            	
	            	text.append("#########Broadcast Memory##########");
	            	text.append(System.getProperty("line.separator"));

	            	for (Memory mBroadcast : c.getBroadcast()) {
	            		text.append("      - Broadcast Memory Name: " + mBroadcast.getName());
	            		text.append(System.getProperty("line.separator"));
	            		
	            		text.append("      - Broadcast Memory Evaluation: " + mBroadcast.getEvaluation());
	            		text.append(System.getProperty("line.separator"));
	            		
	            		text.append("      - Broadcast Memory Info: " + mBroadcast.getI());
	            		text.append(System.getProperty("line.separator"));
	            		text.append(System.getProperty("line.separator"));
	            	}
	            	text.append("#######################");
	            	text.append(System.getProperty("line.separator"));
	            	text.append(System.getProperty("line.separator"));
	            	
	            	text.append("-------------------------------------------------------------");
	            	text.append(System.getProperty("line.separator"));
	            	text.append(System.getProperty("line.separator"));
	            	text.append(System.getProperty("line.separator"));
	            	
	            	//If the created text is bigger then the defined batchSize, it writes it in the file and clears StringBuffer
	            	if (text.toString().getBytes("UTF-16").length >= this.batchSize) {
	            		System.out.println("WRITING IN FILEEEEEEEEE");
	            		String textString = text.toString();
	      	            System.out.println(">>>>>>>>>>>>> text.capacity() " + text.capacity() );
	      	            System.out.println(">>>>>>>>>>>>> text.length() " + text.length() );
	      	            System.out.println(">>>>>>>>>>>>>> text size " + textString.getBytes("UTF-16").length);
	      	            System.out.println(">>>>>>>>>>>>>> text size " + textString.length());
	      	            writer.write(textString);
	      	            text = new StringBuffer();
	            	}
	            
	            }
	              
	        } catch (Exception e) {
	            
	            System.out.println("EXCEPTION 1");
	            System.out.println("e " + e);
	            e.printStackTrace();
	            
	        } finally {
	            try {
	                // Close the writer regardless of what happens...
	                writer.close();
	            } catch (Exception e) {
	            	System.out.println("EXCEPTION 2");
	 	            System.out.println("e " + e);
	 	            e.printStackTrace();
	            }
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
            	this.createFile();
            } catch (Exception e){
                Thread.currentThread().interrupt();
                System.out.println(
                  "Thread was interrupted, Failed to complete operation");
            }
            // do something
            System.out.println(" 1");
        }
        System.out.println(" 2");
	
	}

}
