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
	
	private List<ProfileInfo> profileInfo;
        
        
        public static String path = "";
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		if(codeletName!=null && profileInfo!=null && profileInfo.size()>0){								
			
			BufferedWriter writer = null;
	        try {
                    
                     File directory = new File(path);
                     if (! directory.exists()){
                            directory.mkdir();
                        // If you require it to make the entire directory path including parents,
                        // use directory.mkdirs(); here instead.
                    }
                    
	            File logFile = new File(path+codeletName+"_profile.csv");

	            // This will output the full path where the file will be written to...
	            //System.out.println("Creating log with profile at ... "+logFile.getCanonicalPath());
	            
	            writer = new BufferedWriter(new FileWriter(logFile, true));
	            
	            for(ProfileInfo profile : profileInfo){
					
	            	//writer.write(profile.executionTime+" "+profile.callingTime+" "+profile.lastCallingTime+" "+(profile.callingTime-profile.lastCallingTime)+"\n");
                        // We will be profiling just the proc() execution time and the codelet calling interval 
                        writer.write(TimeStamp.getStringTimeStamp(profile.callingTime, "dd/MM/yyyy HH:mm:ss.SSS")+" "+profile.executionTime+" "+(profile.callingTime-profile.lastCallingTime)+"\n");
					
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
	 * @param profileInfo the list of profile info collected 
	 */
	public void setProfileInfo(List<ProfileInfo> profileInfo) {
		this.profileInfo = profileInfo;
	}

}
