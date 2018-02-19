/**
 * 
 */
package br.unicamp.cst.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * @author andre
 *
 */
public class ExecutionTimeWriter implements Runnable {

	private String codeletName;
	
	private List<Long> executionTimes;
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		if(codeletName!=null && executionTimes!=null && executionTimes.size()>0){								
			
			BufferedWriter writer = null;
	        try {	            
	            File logFile = new File(codeletName+"_profile.txt");

	            // This will output the full path where the file will be written to...
	            System.out.println(logFile.getCanonicalPath());
	            
	            writer = new BufferedWriter(new FileWriter(logFile, true));
	            
	            for(Long duration: executionTimes){
					
	            	writer.write(codeletName+" cycle took "+duration+" ms.\n");
					
				}	            
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                // Close the writer regardless of what happens...
	                writer.close();
	            } catch (Exception e) {
	            }
	        }
		}
		
	}

	/**
	 * @param codeletName the codeletName to set
	 */
	public void setCodeletName(String codeletName) {
		this.codeletName = codeletName;
	}

	/**
	 * @param executionTimes the executionTimes to set
	 */
	public void setExecutionTimes(List<Long> executionTimes) {
		this.executionTimes = executionTimes;
	}
	
	

}