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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author L. M. Berto
 * @author L. L. Rossi (leolellisr)
 */
public class BottomUpFM extends FeatMapCodelet {
    private float mr;                     //Max Value for VisionSensor
    private int res;                      //Resolution of VisionSensor
    private int time_graph;
    private int slices;                    //Slices in each coordinate (x & y) 
    private int step_len, get_sens;
    private int i_position;
    private boolean print_to_file = false;
    private CopyOnWriteArrayList<Float> data_FM_t;
    public BottomUpFM(int nsensors, int get_sens, CopyOnWriteArrayList<String> sens_names, String featmapname,int timeWin, int mapDim, float saturation, int resolution, int slices, int step, int i_position, boolean print_to_file) {
        super(nsensors, sens_names, featmapname,timeWin,mapDim);
        this.time_graph = 0;
        this.mr = saturation; // 255
        this.res = resolution; // 256
        this.slices = slices; // 16
        this.step_len = step; // 3
        this.i_position = i_position; // 2
        this.print_to_file = print_to_file;
        this.get_sens = get_sens;
    }

    @Override
    public void calculateActivation() {
         // Method calculateActivation isnt used here
    }
    
    public CopyOnWriteArrayList<Float> getFM(CopyOnWriteArrayList<Float> data_Array){
        float sum = 0;
        for (float value : data_Array) sum += value;
        float mean_all = sum / data_Array.size();
        CopyOnWriteArrayList<Float> data_mean = new CopyOnWriteArrayList<>();
        float new_res = (res/slices)*(res/slices);
        float new_res_1_2 = (res/slices);
        for(int n = 0;n<slices;n++){
            int ni = (int) (n*new_res_1_2);
            int no = (int) (new_res_1_2+n*new_res_1_2);
            for(int m = 0;m<slices;m++){    
                int mi = (int) (m*new_res_1_2);
                int mo = (int) (new_res_1_2+m*new_res_1_2);
                float correct_mean = calculateCorrectMean(data_Array, mean_all, new_res, ni, no, mi, mo);
                data_mean.add(calculateDataMean(correct_mean));
            }
        }
        return data_mean;
    }

    private float calculateCorrectMean(CopyOnWriteArrayList<Float> data_Array, float mean_all, float new_res, int ni, int no, int mi, int mo) {
        float meanValue = 0;
        for (int y = ni; y < no; y++) {
            for (int x = mi; x < mo; x++) {
                meanValue += data_Array.get(y*res+x);
            }
        }
        return meanValue/new_res - mean_all;
    }

    private float calculateDataMean(float correct_mean) {
        if(correct_mean/mr > 1) {
            return (float) 1;
        } else if(correct_mean/mr < 0.001) {
            return (float) 0;
        } else {
            return correct_mean/mr;
        }
    }

    private void updateFeatureMap(List data_FM) {
        if(data_FM.size() == timeWindow) {
            data_FM.remove(0);
        }
        data_FM.add(new CopyOnWriteArrayList<>());
        data_FM_t = (CopyOnWriteArrayList<Float>) data_FM.get(data_FM.size()-1);
        for (int j = 0; j < mapDimension; j++) {
            data_FM_t.add((float)0);
        }
    }

    private CopyOnWriteArrayList<Float> getDataArray(List list_data) {
        CopyOnWriteArrayList<Float> data_Array = new CopyOnWriteArrayList<>();
        for (int j = 0; j < res*res; j++) {
            data_Array.add((float)0);
        }
        int count = 0;
        for (int j = 0; j+step_len < list_data.size(); j+= step_len) {
            data_Array.set(count, (Float) list_data.get(j+i_position));
            count += 1;
        }
        return data_Array;
    }

    private void updateDataFM(List data_FM, CopyOnWriteArrayList<Float> data_Array) {
        CopyOnWriteArrayList<Float> data_mean = getFM(data_Array);
        data_FM_t = (CopyOnWriteArrayList<Float>) data_FM.get(data_FM.size()-1);
        for (int j = 0; j < data_mean.size(); j++) {
            data_FM_t.set(j, data_mean.get(j));
        }
    }

    @Override
    public void proc() {
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
        MemoryObject data_bufferMO = (MemoryObject) sensor_buffers.get(get_sens);
        List data_buffer = (List) data_bufferMO.getI(), data_FM = (List) featureMap.getI();
        updateFeatureMap(data_FM);
        if(data_buffer == null || data_buffer.isEmpty()) {
            return;
        }
        MemoryObject dataMO = (MemoryObject)data_buffer.get(data_buffer.size()-1);
        List list_data = (List) dataMO.getI();
        CopyOnWriteArrayList<Float> data_Array = getDataArray(list_data);
        updateDataFM(data_FM, data_Array);
        featureMap.setI(data_FM_t);
        if(print_to_file) {
            printToFile((CopyOnWriteArrayList<Float>) data_FM.get(data_FM.size()-1));
        }
        steps++;
    }



    
    private void printToFile(CopyOnWriteArrayList<Float> arr){
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
        
    }
}
    

