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
 
package br.unicamp.cst.sensory;

import br.unicamp.cst.core.entities.MemoryObject;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author leolellisr
 */
public class CFM extends CombFeatMapCodelet {
    private static final int BOTTOM_UP = 0;
    private static final int TOP_DOWN = 1;
    private  int time_graph;

    private boolean print_to_file = false;
    private boolean debug = false;
    private String path = "results/txt_last_exp/";
    
    public CFM(int numfeatmaps, ArrayList<String> featmapsnames, int timeWin, int CFMdim) {
        super(numfeatmaps, featmapsnames,timeWin,CFMdim);
        this.time_graph = 0;
        
        
    }

     
    @Override
    public void calculateCombFeatMap() {
       
        for (int i = 0; i < num_feat_maps; i++) {
            MemoryObject mo = (MemoryObject)feature_maps.get(i);
            List fm = (List) mo.getI();
        }
        
       
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
        
        List weight_values = (List) weights.getI();
        
        List combinedFM = (List) comb_feature_mapMO.getI();
        List winnersTypeList = (List) winnersType.getI();
        
        if(combinedFM.size() == timeWindow){
            combinedFM.remove(0);
        }
        if(winnersTypeList.size() == timeWindow){
            winnersTypeList.remove(0);
        }
        
        combinedFM.add(new ArrayList<>());
        winnersTypeList.add(new ArrayList<>());
        
        int t = combinedFM.size()-1;

        List CFMrow, winners_row;
        CFMrow = (List) combinedFM.get(t);
        winners_row = (List) winnersTypeList.get(t);
        
        for(int j = 0; j < CFMdimension; j++){
            CFMrow.add((float)0);
            winners_row.add(0);
        }
        
        
        for (int j = 0; j < CFMrow.size(); j++) {
            float ctj;
            float sum_top=0, sum_bottom=0;
            ctj = 0;
            
            for (int k = 0; k < num_feat_maps; k++) {
                MemoryObject FMkMO;
                FMkMO = (MemoryObject) feature_maps.get(k);

                List FMk;
                FMk = (List) FMkMO.getI();
                
                
                if(FMk == null){
                    return;
                }
                
                if(FMk.size() < 1){
                    return;
                }
                
                List FMk_t;
                FMk_t = (List) FMk.get(FMk.size()-1);
                
                Float weight_val, fmkt_val;
                
                fmkt_val = (Float) FMk_t.get(j); 
                
                if(weight_values == null){
                    return;
                }
                
                if(debug) System.out.print("  weight_values: "+ weight_values + " k:"+k);                
                weight_val = (Float) weight_values.get(k);
                ctj += weight_val*fmkt_val;
                
                
                if(k>=4) sum_top += weight_val*fmkt_val;
                else sum_bottom += weight_val*fmkt_val;
                   
                
            }   
            
            CFMrow.set(j, ctj);
            
            if(sum_top > sum_bottom) winners_row.set(j, TOP_DOWN);
            else winners_row.set(j, BOTTOM_UP);
            
        }
        
        if(print_to_file) {
            printToFile((ArrayList<Float>) CFMrow, "CFM.txt");
            printToFile((ArrayList<Integer>) winners_row, "winnerType.txt");
        }
}
    
      
    private void printToFile(Object object,String filename){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");  
        LocalDateTime now = LocalDateTime.now();
            try(FileWriter fw = new FileWriter(path+filename,true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                out.println(dtf.format(now)+"_"+"_"+time_graph+" "+ object);
                time_graph++;
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    
    }
}