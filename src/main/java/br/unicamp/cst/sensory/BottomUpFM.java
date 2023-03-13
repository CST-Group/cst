/*
 * /*******************************************************************************
 *  * Copyright (c) 2012  DCA-FEEC-UNICAMP
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available under the terms of the GNU Lesser Public License v3
 *  * which accompanies this vision_blueribution, and is available at
 *  * http://www.gnu.org/licenses/lgpl.html
 *  * 
 *  * Contributors:
 *  *     K. Raizer, A. L. O. Paraense, R. R. Gudwin - initial API and implementation
 *  ******************************************************************************/
 
package br.unicamp.cst.sensory;

import br.unicamp.cst.core.entities.MemoryObject;
//import codelets.motor.Lock;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
//import static java.lang.Math.abs;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author L. M. Berto
 * @author L. L. Rossi (leolellisr)
 */
public class BottomUpFM extends FeatMapCodelet {
    private float mr;                     //Max Value for VisionSensor
    private int max_time_graph;
    private int res;                      //Resolution of VisionSensor
    private int time_graph;
    private int slices;                    //Slices in each coordinate (x & y) 
    private int step_len;
    private int i_position;
    private boolean print_to_file = false;
    private boolean debug = true;
    //private float max_value = 0;
    public BottomUpFM(int nsensors, ArrayList<String> sens_names, String featmapname,int timeWin, int mapDim, float saturation, int max_time_graph, int resolution, int slices, int step, int i_position, boolean debug) {
        super(nsensors, sens_names, featmapname,timeWin,mapDim);
        this.time_graph = 0;
        this.mr = saturation; // 255
        this.max_time_graph = max_time_graph; // 100
        this.res = resolution; // 256
        this.slices = slices; // 16
        this.step_len = step; // 3
        this.i_position = i_position; // 2
        this.debug = debug;
    }

    @Override
    public void calculateActivation() {
        
    }

    @Override
    public void proc() {
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
        
        MemoryObject data_bufferMO = (MemoryObject) sensor_buffers.get(0);       //Gets Data Buffer in Pos 0
        
        List data_buffer;
        data_buffer = (List) data_bufferMO.getI();

        List data_FM = (List) featureMap.getI();        
        
        if(data_FM.size() == timeWindow){
            data_FM.remove(0);
        }
        
        data_FM.add(new ArrayList<>());
        
        int t = data_FM.size()-1;

        ArrayList<Float> data_FM_t = (ArrayList<Float>) data_FM.get(t);
        
        for (int j = 0; j < mapDimension; j++) {
            data_FM_t.add(new Float(0));
        }
        
        MemoryObject dataMO;
        if(debug&& data_buffer != null) System.out.println("data buffer:"+data_buffer+" size: "+data_buffer.size());
        if(data_buffer == null){
            return;
        }
        if(data_buffer.size() < 1){
            return;
        }

        dataMO = (MemoryObject)data_buffer.get(data_buffer.size()-1);

        List list_data;

        list_data = (List) dataMO.getI();
                
        Float Fvalue;
        float MeanValue = 0;
        ArrayList<Float> data_mean = new ArrayList<>();
        ArrayList<Float> data_Array = new ArrayList<>();
        
        for (int j = 0; j < res*res; j++) {
            data_Array.add(new Float(0));
        }
        
        
        int count = 0;
        for (int j = 0; j+step_len < list_data.size(); j+= step_len) {
           
            Fvalue = (Float) list_data.get(j+i_position);               //Gets JUST values for each i_position at each step_len

            data_Array.set(count, Fvalue);        // set data
            count += 1;
        }
        if(debug) System.out.println("data list:"+data_Array +" size: "+data_Array.size());
        
        // get mean all elements
        float sum = 0;
        for (float value : data_Array) {
            sum += value;
        }
    
        float mean_all = sum / data_Array.size();
        if(debug) System.out.println("mean all:"+mean_all);
        
        //Converts res*res image to res/slices*res/slices sensors
        float new_res = (res/slices)*(res/slices);
        float new_res_1_2 = (res/slices);
        
        for(int n = 0;n<slices;n++){
            int ni = (int) (n*new_res_1_2);
            int no = (int) (new_res_1_2+n*new_res_1_2);
            for(int m = 0;m<slices;m++){    
                int mi = (int) (m*new_res_1_2);
                int mo = (int) (new_res_1_2+m*new_res_1_2);
                for (int y = ni; y < no; y++) {
                    for (int x = mi; x < mo; x++) {
                        Fvalue = data_Array.get(y*res+x);                         
                        MeanValue += Fvalue;
                        
                    }
                }
                float correct_mean = MeanValue/new_res - mean_all;
                
                if(correct_mean/mr>1) data_mean.add(new Float(1));
                else if(correct_mean/mr<0.001) data_mean.add(new Float(0));
                else data_mean.add(correct_mean/mr);   
                
                MeanValue = 0;
                
            }
        }
        if(debug) System.out.println("data_mean:"+data_mean+" size: "+data_mean.size());
        
        for (int j = 0; j < data_mean.size(); j++) {
           
            data_FM_t.set(j, data_mean.get(j));
        }   
        if(debug) System.out.println("data_FM_t:"+data_FM_t+" size: "+data_FM_t.size());
        if(print_to_file) printToFile(data_FM_t);
    }
    private void printToFile(ArrayList<Float> arr){
        //if(time_graph%2 == 0 ){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");  
        LocalDateTime now = LocalDateTime.now(); 
        try(FileWriter fw = new FileWriter("results/txt_last_exp/vision_blue_FM.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(dtf.format(now)+"_"+"_"+time_graph+" "+ arr);
            time_graph++;
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
       // }else time_graph++; 
    }
}
    

