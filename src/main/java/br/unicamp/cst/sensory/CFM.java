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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 *
 * @author leolellisr
 */
public class CFM extends CombFeatMapCodelet {
    private static final int BOTTOM_UP = 0;
    private static final int TOP_DOWN = 1;
    private  int time_graph;
    public int steps;

    private boolean print_to_file = false;
    private String path = "results/txt_last_exp/";
    private List CFMrow, winners_row;
    public CFM(int numfeatmaps, CopyOnWriteArrayList<String> featmapsnames, int timeWin, int CFMdim, boolean print_to_file) {
        super(numfeatmaps, featmapsnames,timeWin,CFMdim);
        this.time_graph = 0;
        this.print_to_file = print_to_file;
        this.steps = 0;
    }

     
    private void initializeCFMrowAndWinnersRow() {
    for (int j = 0; j < CFMdimension; j++) {
        this.CFMrow.add((float) 0);
        this.winners_row.add(0);
        }
    }
    
    private void calculateCFMandWinners() {
        List weight_values = (List) weights.getI();
        for (int j = 0; j < CFMrow.size(); j++) { 
                float ctj= 0, sum_top=0, sum_bottom=0;
                for (int k = 0; k < num_feat_maps; k++) { 
                    MemoryObject FMkMO = (MemoryObject) feature_maps.get(k);
                    List FMk = (List) FMkMO.getI();
                    if(FMk == null || weight_values == null) return;
                    if(FMk.size() < 1) return;
                    List FMk_t = (List) FMk.get(FMk.size()-1);
                    Float fmkt_val = (Float) FMk_t.get(j), weight_val = (Float) weight_values.get(k);
                    ctj += weight_val*fmkt_val;
                    if(k>=4) sum_top += weight_val*fmkt_val;
                    else sum_bottom += weight_val*fmkt_val; 
                }   
                CFMrow.set(j, ctj);
                if(sum_top > sum_bottom) winners_row.set(j, TOP_DOWN);
                else winners_row.set(j, BOTTOM_UP); 
            }
    }
    
    @Override
    public void calculateCombFeatMap() {
        try {
            Thread.sleep(300);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
        List combinedFM = (List) comb_feature_mapMO.getI(), winnersTypeList = (List) winnersType.getI();
        if(combinedFM.size() == timeWindow) combinedFM.remove(0);
        if(winnersTypeList.size() == timeWindow) winnersTypeList.remove(0);
        combinedFM.add(new CopyOnWriteArrayList<>());
        winnersTypeList.add(new CopyOnWriteArrayList<>());
        CFMrow = (List) combinedFM.get(combinedFM.size()-1);
        winners_row = (List) winnersTypeList.get(winnersTypeList.size()-1);
        
        initializeCFMrowAndWinnersRow();
        
        calculateCFMandWinners();
        comb_feature_mapMO.setI(CFMrow);
        if(print_to_file){
            printToFile((CopyOnWriteArrayList<Float>) CFMrow, "CFM.txt");
            printToFile((CopyOnWriteArrayList<Integer>) winners_row, "winnerType.txt"); 
        }
        steps++;
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