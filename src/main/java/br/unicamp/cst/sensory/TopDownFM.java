/*
 * /*******************************************************************************
 *  * Copyright (c) 2012  DCA-FEEC-UNICAMP
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available under the terms of the GNU Lesser Public License v3
 *  * which accompanies this vision_redribution, and is available at
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
 *
 * @author L. M. Berto
 * @author L. L. Rossi (leolellisr)
 */
public class TopDownFM extends FeatMapCodelet {
    private List winnersList;
    private float mr = 255;                     //Max Value for VisionSensor
    private int max_time_graph=100;
    private int res = 256;                     //Resolution of VisionSensor
    private  int time_graph;
    private int slices = 16;                    //Slices in each coordinate (x & y) 
    private int stage;
    private String path = "results/txt_last_exp/vision_top_color_FM.txt";
    private ArrayList<Float> goal;  
    private int step_len;
    private boolean print_to_file = false;
    private boolean debug = true; 
    //private float max_value = 0;
    public TopDownFM(int nsensors, ArrayList<String> sens_names, String featmapname,int timeWin, int mapDim, ArrayList<Float> goal, float saturation, int max_time_graph, int resolution, int slices, int step, boolean debug) {
        super(nsensors, sens_names, featmapname,timeWin,mapDim);
        this.time_graph = 0;
        this.goal = goal;
        this.mr = saturation; // 255
        this.max_time_graph = max_time_graph; // 100
        this.res = resolution; // 256
        this.slices = slices; // 16
        this.step_len = step; // 3
        this.debug = debug;

    }

    public ArrayList<Float> getGoal(){
        return this.goal;
    }
    
    public void setGoal(ArrayList<Float> new_goal){
        this.goal = new_goal;
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
        
        MemoryObject data_bufferMO = (MemoryObject) sensor_buffers.get(0);        //Gets  Data from buffer 0
        
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

            if(data_buffer == null){
                return;
            }
            
            if(data_buffer.size() < 1){
                return;
            }

            dataMO = (MemoryObject)data_buffer.get(data_buffer.size()-1);

            List listData;

            listData = (List) dataMO.getI();
                       
            
            Float Fvalue_r, Fvalue_g, Fvalue_b;
            float MeanValue_r = 0, MeanValue_g = 0, MeanValue_b = 0;
            ArrayList<Float> vision_mean_color = new ArrayList<>();
            ArrayList<Float> visionData_Array_r = new ArrayList<>();
            ArrayList<Float> visionData_Array_g = new ArrayList<>();
            ArrayList<Float> visionData_Array_b = new ArrayList<>();
            for (int j = 0; j < res*res; j++) {
                visionData_Array_r.add(new Float(0));
                visionData_Array_g.add(new Float(0));
                visionData_Array_b.add(new Float(0));
            }
            
            int count_3 = 0;
            //System.out.println("Vision data r size:"+visionData.size());
            for (int j = 0; j+step_len < listData.size(); j+= step_len) {

                Fvalue_r = (Float) listData.get(j);               //Gets values 
                Fvalue_g = (Float) listData.get(j+1);
                Fvalue_b = (Float) listData.get(j+2);
                visionData_Array_r.set(count_3, Fvalue_r);        //red data
                visionData_Array_g.set(count_3, Fvalue_g);        //green data
                visionData_Array_b.set(count_3, Fvalue_b);        //blue data
                count_3 += 1;
            }
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
                            Fvalue_r = visionData_Array_r.get(y*res+x); 
                            Fvalue_g = visionData_Array_g.get(y*res+x);
                            Fvalue_b = visionData_Array_b.get(y*res+x);  
                            MeanValue_r += Fvalue_r;
                            MeanValue_g += Fvalue_g;
                            MeanValue_b += Fvalue_b;


                        }
                    }
                    float correct_mean_r = MeanValue_r/new_res;
                    float correct_mean_g = MeanValue_g/new_res;
                    float correct_mean_b = MeanValue_b/new_res;

                    if(Math.abs(correct_mean_r-goal.get(0))/mr<0.2 && Math.abs(correct_mean_g-goal.get(1))/mr<0.2 && Math.abs(correct_mean_b-goal.get(2))/mr<0.2) vision_mean_color.add(new Float(1));
                    else if(Math.abs(correct_mean_r-goal.get(0))/mr<0.4 && Math.abs(correct_mean_g-goal.get(1))/mr<0.4 && Math.abs(correct_mean_b-goal.get(2))/mr<0.4) vision_mean_color.add(new Float(0.75));
                    else if(Math.abs(correct_mean_r-goal.get(0))/mr<0.6 && Math.abs(correct_mean_g-goal.get(1))/mr<0.6 && Math.abs(correct_mean_b-goal.get(2))/mr<0.6) vision_mean_color.add(new Float(0.5));
                    else if(Math.abs(correct_mean_r-goal.get(0))/mr<0.8 && Math.abs(correct_mean_g-goal.get(1))/mr<0.8 && Math.abs(correct_mean_b-goal.get(2))/mr<0.8) vision_mean_color.add(new Float(0.25));
                    else vision_mean_color.add(new Float(0));     
                    if(debug) System.out.println("\n correct_mean_r: "+Math.abs(correct_mean_r-goal.get(0))/mr+" correct_mean_g: "+Math.abs(correct_mean_g-goal.get(1))+" correct_mean_b: "+Math.abs(correct_mean_b-goal.get(2)));
                    MeanValue_r = 0;
                    MeanValue_g = 0;
                    MeanValue_b = 0;
                }
            }


            for (int j = 0; j < vision_mean_color.size(); j++) {

                data_FM_t.set(j, vision_mean_color.get(j));
            }   
        
        if(print_to_file) printToFile(data_FM_t);
    }
    private void printToFile(ArrayList<Float> arr){

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");  
        LocalDateTime now = LocalDateTime.now(); 
        try(FileWriter fw = new FileWriter(path, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(dtf.format(now)+"_"+time_graph+" "+ arr);
            time_graph++;
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
    
