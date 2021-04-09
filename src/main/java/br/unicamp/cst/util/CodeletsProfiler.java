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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;

public class CodeletsProfiler {
	
    private final static String csvColumns = "MindIdentifier;Time;codeletName;ThreadName;Activation;Threshold;isLoop;TimeStep;isProfiling;"+
    										";MemoryName;Evaluation;Info;Timestamp"+System.getProperty("line.separator");
    
    private final static String openJSONList = "["+System.getProperty("line.separator");
    private final static String closeJSONList = "]"+System.getProperty("line.separator");
    
    private final static String csvSeparator = ";";
    private final static String comma = ",";
    
    private final static String lineSeparator = System.getProperty("line.separator");
    
    private String filePath;
    private String fileName;
    private String codeletIdentifier;
    private double batchSize;
    private String mindIdentifier;
    private Long  intervalTimeMillis;
    private Integer queueSize;
    private long lastTimeMillis;
    private static ConcurrentLinkedQueue<QueuePair> queue;  
    //private Gson gson = new Gson();
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private FileFormat fileFormat;
    public enum FileFormat {CSV, JSON};
    
    private class QueuePair {
    	private String codeletIdentifier;
    	private String text;
    	
		public QueuePair(String codeletIdentifier, String text) {
			super();
			this.codeletIdentifier = codeletIdentifier;
			this.text = text;
		}
		
		public String getCodeletIdentifier() {
			return codeletIdentifier;
		}

		public String getText() {
			return text;
		}
	
    }


    public CodeletsProfiler(String codeletIdentifier, String filePath, String fileName, String mindIdentifier, Integer queueSize, FileFormat fileFormat) {
		super();
		this.filePath = filePath;
		this.fileName = fileName;
		this.queueSize = queueSize;
		this.mindIdentifier = mindIdentifier;
		this.fileFormat = fileFormat;
		this.codeletIdentifier = codeletIdentifier;
		this.initializeQueue();
	}
	
    public CodeletsProfiler(String codeletIdentifier, String filePath, String fileName, String mindIdentifier, Long intervalTimeMillis, FileFormat fileFormat) {
		super();
		this.filePath = filePath;
		this.fileName = fileName;
		this.mindIdentifier = mindIdentifier;
		this.intervalTimeMillis = intervalTimeMillis;
		this.lastTimeMillis = System.currentTimeMillis();
		this.fileFormat = fileFormat;
		this.codeletIdentifier = codeletIdentifier;
		this.initializeQueue();
	}
	
    public CodeletsProfiler(String codeletIdentifier, String filePath, String fileName, String mindIdentifier,Integer queueSize, Long intervalTimeMillis, FileFormat fileFormat) {
		super();
		this.filePath = filePath;
		this.fileName = fileName;
		this.mindIdentifier = mindIdentifier;
		this.intervalTimeMillis = intervalTimeMillis;
		this.lastTimeMillis = System.currentTimeMillis();
		this.queueSize = queueSize;
		this.fileFormat = fileFormat;
		this.codeletIdentifier = codeletIdentifier;
		this.initializeQueue();

	}
    
    private void initializeQueue() {
    	 if (queue == null) {
			queue = new ConcurrentLinkedQueue<QueuePair>(); 
		 }
    	 switch(fileFormat) {
	        case CSV:
	          queue.add(new QueuePair(this.codeletIdentifier, csvColumns));
	          break;
	        case JSON:
	          queue.add(new QueuePair(this.codeletIdentifier, openJSONList));
	          break;
	        default:
	          queue.add(new QueuePair(this.codeletIdentifier, openJSONList));
		 }
    }
	
	private void createFile() {

		if (filePath != null && fileName != null) {
			BufferedWriter writer = null;
	        try {
                    
                     File directory = new File(filePath);
                     if (!directory.exists()){
                            directory.mkdir();
                        // If you require it to make the entire directory path including parents,
                        // use directory.mkdirs(); here instead.
                    }
                    
	            File profilerFile = new File(filePath+fileName);

	            // This will output the full path where the file will be written to...
	            //System.out.println("Creating log with profile at ... "+logFile.getCanonicalPath());
	            
	            writer = new BufferedWriter(new FileWriter(profilerFile, true));
	            
	            for (QueuePair line : queue) {
	            	if (line.getCodeletIdentifier().equals(this.codeletIdentifier)) {
	        			writer.write(line.getText());
	        			queue.remove(line);
	            	}
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
        
        public class CodeletTrack {
            String mindId;
            String time;
            String codeletName;
            String threadName;
            double activation;
            double threshold;
            boolean isLoop;
            String timeStep;
            boolean isProfiling;
            String separator;
            List<Memory> mInputs;
            List<Memory> mOutputs;
            List<Memory> mBroadcasts;
            
            public CodeletTrack(Codelet c) {
                mindId = mindIdentifier;
                time = TimeStamp.getStringTimeStamp(System.currentTimeMillis(),"dd/MM/yyyy HH:mm:ss.SSS");
                codeletName = c.getName();
                threadName = c.getThreadName();
                activation = c.getActivation();
                threshold = c.getThreshold();
                isLoop = c.isLoop();
                timeStep = TimeStamp.getStringTimeStamp(c.getTimeStep(),"dd/MM/yyyy HH:mm:ss.SSS");
                isProfiling = c.isProfiling();
                separator = System.getProperty("line.separator");
                mInputs = c.getInputs();
                mOutputs = c.getOutputs();
                mBroadcasts = c.getBroadcast();
            }
        }
        
    private void addJsonText(Codelet c) {
    	String textBlock = gson.toJson(new CodeletTrack(c));
        queue.add(new QueuePair(this.codeletIdentifier, lineSeparator + textBlock  + comma));
    }
    
    private void addCSVText(Codelet c) {
    	String textBlock = mindIdentifier +csvSeparator+System.currentTimeMillis()+csvSeparator+c.getName()+csvSeparator+c.getThreadName()+csvSeparator+c.getActivation()+";"+
            			c.getThreshold()+csvSeparator+c.isLoop()+csvSeparator+c.getTimeStep()+csvSeparator+c.isProfiling()+csvSeparator+csvSeparator+csvSeparator+csvSeparator+lineSeparator;
    	queue.add(new QueuePair(this.codeletIdentifier, textBlock));
    }
    
    private void addTextToQueue(Codelet c) {
    	 switch(fileFormat) {
	        case CSV:
	          addCSVText(c);
	          break;
	        case JSON:
	          addJsonText(c);
	          break;
	        default:
	          addJsonText(c);
     }
    }
    
    private void finalizeJSONFile() {
  		BufferedWriter writer = null;
		try {
			 File profilerFile = new File(filePath+fileName);

	            // This will output the full path where the file will be written to...
	            //System.out.println("Creating log with profile at ... "+logFile.getCanonicalPath());
	            
			writer = new BufferedWriter(new FileWriter(profilerFile, true));
	        writer.write(closeJSONList);
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
	
	private void fillQueue(Codelet c) { 
		
	        this.addTextToQueue(c);

        	//If the one of the the variables is met, will write in file
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

    public void profile(Codelet c) {
    	this.fillQueue(c);
    }
    
    public void finishProfile(Codelet c) {
    	this.addTextToQueue(c);
    	this.createFile();
    	if (fileFormat == FileFormat.JSON) {
    		this.finalizeJSONFile();
    	}
    }

}
