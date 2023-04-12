/*
 * /*******************************************************************************
 *  * Copyright (c) 2012  DCA-FEEC-UNICAMP
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available under the terms of the GNU Lesser Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/lgpl.html
 *  * 
 *  * Contributors:
 *  *     K. Raizer, A. L. O. Paraense, R. R. Gudwin - initial API and implementation
 *  ******************************************************************************/
 
package br.unicamp.cst.support;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;




public class ToTxt {

/**
* printToFile. Function to print on txt.
* @param object map to print
* @param filename name to write txt file
* @param debug boolean that indicates if log should be printed
* @param time_graph timestep
* 
* @author leolellisr
**/
    
    public static void printToFile(Object object,String filename, boolean debug, int time_graph){
        String user_dir = System.getProperty("user.dir");
        File dir = new File(user_dir);
        if (!dir.exists()) {
            dir.mkdir();
            if(debug) Logger.getAnonymousLogger().log(Level.INFO, "dir created: {0}",  new Object[]{dir});
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");  
        LocalDateTime now = LocalDateTime.now();
            try(FileWriter fw = new FileWriter(user_dir+ File.separator+filename,true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                out.println(dtf.format(now)+"_"+"_"+time_graph+" "+ object);
                out.close();
            } catch (IOException e) { e.printStackTrace(); }
    }
    
    public static void printToFile(Object object,String filename, int time_graph){
        String user_dir = System.getProperty("user.dir");
        File dir = new File(user_dir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");  
        LocalDateTime now = LocalDateTime.now();
            try(FileWriter fw = new FileWriter(user_dir+ File.separator+filename,true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                out.println(dtf.format(now)+"_"+"_"+time_graph+" "+ object);
                out.close();
            } catch (IOException e) { e.printStackTrace(); }
    }
    
}