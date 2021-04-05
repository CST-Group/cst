/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 *
 * @author rgudwin
 */
public class CodeletTrackWriter implements Runnable {

	private String codeletName;
	private List<CodeletTrackInfo> trackInfo;
        public static String path = "tests/";
        private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		if(codeletName!=null && trackInfo!=null && trackInfo.size()>0){								
			
			BufferedWriter writer = null;
	        try {
                    
                     File directory = new File(path);
                     if (! directory.exists()){
                            directory.mkdir();
                        // If you require it to make the entire directory path including parents,
                        // use directory.mkdirs(); here instead.
                    }
                    
	            File logFile = new File(path+codeletName+"_track.json");

	            // This will output the full path where the file will be written to...
	            //System.out.println("Creating log with profile at ... "+logFile.getCanonicalPath());
	            
	            writer = new BufferedWriter(new FileWriter(logFile, true));
	            
	            for(CodeletTrackInfo profile : trackInfo){
					
	            	//writer.write(profile.executionTime+" "+profile.callingTime+" "+profile.lastCallingTime+" "+(profile.callingTime-profile.lastCallingTime)+"\n");
                        // We will be profiling just the proc() execution time and the codelet calling interval 
                        String textBlock = gson.toJson(profile);
                        writer.write(textBlock+"\n");
					
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
	 * @param trackInfo the list of profile info collected 
	 */
	public void setTrackInfo(List<CodeletTrackInfo> trackInfo) {
		this.trackInfo = trackInfo;
	}

}

