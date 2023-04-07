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
 
package br.unicamp.cst.attention;


import br.unicamp.cst.core.entities.MemoryObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Codelet implementation of Top-Down Feature Maps generated by the Attentional 
 * System of Conscious Attention-Based Integrated Model (CONAIM). From a 
 * top-down perspective, depending on the system goal and on the attentional 
 * dynamic current state (orienting, selecting or sustaining), voluntary 
 * attention can be directed to a region of space or object in two ways: by 
 * deliberative enhancing a region in the attentional map or by adjusting the 
 * weights that define the contribution of each feature dimension.
 * 
 * Top-down Feature Maps allow the agent to target its attention to desired 
 * elements deliberately. The maps were computed using an average pool over the 
 * observation of each map at time t, and then the difference between each 
 * region mean and the image mean. It's computed the necessary size of the 
 * kernel and stride to reduce the feature map to a final size. 
 * Each value is compared to a particular goal to build these maps. The closer 
 * these elements are to the target values according to predefined percentage 
 * ranges, the higher the map activation in that region. 
 * 
 * @author L. M. Berto
 * @author L. L. Rossi (leolellisr)
 * 
 * @see Codelet
 * @see MemoryObject
 * @see FeatMapCodelet
 */
public class TopDownFM extends FeatMapCodelet {
    private float mr = 255;                     //Max Value for VisionSensor
    private int res = 256;                     //Resolution of VisionSensor
    private  int time_graph;
    private int slices = 16;                    //Slices in each coordinate (x & y) 
    private String path = "results/txt_last_exp/", file="topDownFM.txt";
    private CopyOnWriteArrayList<Float> goal;  
    private int step_len, get_sens;
    private boolean print_to_file = false;
    private CopyOnWriteArrayList<Float> visionData_Array_r = new CopyOnWriteArrayList<>(), visionData_Array_g = new CopyOnWriteArrayList<>(), visionData_Array_b = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Float> data_FM_t;    
    
    
    /**
     * init TopDownFM class

     * @param get_sens
     *          input sensor index

     * @param featmapname
     *          output feature map name
     * @param timeWin
     *          analysed time window,   Buffer size
     * @param mapDim 
     *          output feature map dimension
     * @param goal 
     *          top down feature map goal
     * @param saturation 
     *          top down feature map saturation
     * @param resolution 
     *          top down feature map input resolution
     * @param slices 
     *          slices number that will reduce the input resolution
     * @param step 
     *          step to scan input sensors
     * @param print_to_file
     *          boolean that defines if should print to file
     */
    public TopDownFM(int get_sens, String featmapname,int timeWin, int mapDim, CopyOnWriteArrayList<Float> goal, 
            float saturation, int resolution, int slices, int step, boolean print_to_file) {
        super(featmapname,timeWin,mapDim);
        this.time_graph = 0;
        this.goal = goal;
        this.mr = saturation; // 255
        this.res = resolution; // 256
        this.slices = slices; // 16
        this.step_len = step; // 3
        this.print_to_file = print_to_file;
        this.get_sens = get_sens;

    }
    
    @Override
    /**
     * access MemoryObjects: SensorBuffers (inputs) and Winners (for top-down perspective) 
     * define output: feat_map_name
     * 
     */
    public void accessMemoryObjects() {
        featureMap = (MemoryObject) this.getOutput(feat_map_name);
        winners = (MemoryObject) this.getInput("WINNERS");
        
    }

    /**
     * getGoal. Get the Top Down feature map goal.
     * @return goal
     * */
    public CopyOnWriteArrayList<Float> getGoal(){
        return this.goal;
    }
    
    /**
     * setGoal. set the Top Down feature map goal.
     * @param new_goal
     *        new goal to set
     * */
    public void setGoal(CopyOnWriteArrayList<Float> new_goal){
        this.goal = new_goal;
    }
    
    @Override
    public void calculateActivation() {
        // Method calculateActivation isnt used here
    }
   
    /**
     * getFM. Calculates the Top Down feature map.
     * @return top down feature map
     * */
    public CopyOnWriteArrayList<Float> getFM(){
        CopyOnWriteArrayList<Float> vision_mean_color = new CopyOnWriteArrayList<>();
        float new_res_1_2 = res/slices;
        CopyOnWriteArrayList<Integer> limits = new CopyOnWriteArrayList<>();
        for (int i=0; i<4;i++) limits.add(0);
        for(int n = 0;n<slices;n++){
            limits.set(0, (int) (n*new_res_1_2));
            limits.set(1, (int) (new_res_1_2+n*new_res_1_2));
            for(int m = 0;m<slices;m++){    
                limits.set(2, (int) (m*new_res_1_2));
                limits.set(3, (int) (new_res_1_2+m*new_res_1_2));
                float[] meanValues = getPixelValues(limits);
                float vision_color_value = getValue(meanValues[0], meanValues[1], meanValues[2]);
                vision_mean_color.add(vision_color_value);
            }
        }
        return vision_mean_color;
    }

    /**
     * getPixelValues. Calculates the average pool values with the pixel values 
     * for the Top Down feature map.
     * @param ni
     *        Start value for y-axis scan of predefined region
     * @param no
     *        Final value for y-axis scan of predefined region
     * @param mi
     *        Start value for x-axis scan of predefined region
     * @param mo
     *        Final value for x-axis scan of predefined region
     * @return average pool values for the pixel values
     * */
    private float[] getPixelValues(CopyOnWriteArrayList<Integer> limits) {
        float MeanValue_r = 0, MeanValue_g = 0, MeanValue_b = 0;
        for (int y = limits.get(0); y < limits.get(1); y++) {
            for (int x = limits.get(2); x < limits.get(3); x++) {
                MeanValue_r += visionData_Array_r.get(y*res+x);
                MeanValue_g += visionData_Array_g.get(y*res+x);
                MeanValue_b += visionData_Array_b.get(y*res+x);
            }
        }
        float new_res = (res/slices)*(res/slices);
        
        return new float[] {MeanValue_r/new_res, MeanValue_g/new_res, MeanValue_b/new_res};
    }

    /**
     * compare. It compares the differences between the values obtained for the 
     * defined regions and a pre-established value.  
     * 
     * @param red_diff
     *        first value to compare
     * @param green_diff
     *        second value to compare
     * @param blue_diff
     *        third value to compare
     * @param comp
     *        pre-established value to compare
     * @return boolean that classifies the comparison of values as true or false
     */
    private boolean compare(float red_diff, float green_diff, float blue_diff, float comp){
        return red_diff<comp && green_diff<comp && blue_diff < comp;
    }
    
    /**
     * getValue. Returns the value to be adopted for the region considering the 
     * difference between the average pool obtained and the goal value.
     * @param correct_mean_r
     *        mean values of first channel
     * @param correct_mean_g
     *        mean values of second channel
     * @param correct_mean_b
     *        mean values of third channel
     * @return value
     *        final value of the region
     **/
    private float getValue(float correct_mean_r, float correct_mean_g, float correct_mean_b) {
        float value;
        float red_diff = Math.abs(correct_mean_r-goal.get(0))/mr;
        float green_diff = Math.abs(correct_mean_g-goal.get(1))/mr;
        float blue_diff = Math.abs(correct_mean_b-goal.get(2))/mr;
        if(compare(red_diff, green_diff, blue_diff,      (float) 0.2)) value = (float)1;
        else if(compare(red_diff, green_diff, blue_diff, (float) 0.4)) value = (float)0.75;
        else if(compare(red_diff, green_diff, blue_diff, (float) 0.6)) value = (float) 0.5;
        else if(compare(red_diff, green_diff, blue_diff, (float) 0.8)) value = (float) 0.25;
        else value =(float)0;     
        return value;
    }
    
    /**
     * inicializeMeanValues. Initializes the arrays used to calculate the
     * average pool with zeros
     **/
    private void inicializeMeanValues(){
        for (int j = 0; j < res*res; j++) {
            visionData_Array_r.add((float)0);
            visionData_Array_g.add((float)0);
            visionData_Array_b.add((float)0);
        }
    }
    
    /**
     * separateValues. separates the sensor values between the channels used.
     * @param listData
     *        array with sensor values
     */
    private void separateValues(List listData){
        int count_3 = 0;
        for (int j = 0; j+step_len < listData.size(); j+= step_len) {
                visionData_Array_r.set(count_3, (Float) listData.get(j));        //red data
                visionData_Array_g.set(count_3, (Float) listData.get(j+1));        //green data
                visionData_Array_b.set(count_3, (Float) listData.get(j+2));        //blue data
                count_3 += 1; }
    }
    
    
    /**
     * getMeanValues. Set the feature map values
     * @param vision_mean_color 
     *        array with average pool values
     */
    private void getMeanValues(CopyOnWriteArrayList<Float> vision_mean_color){
        for (int j = 0; j < vision_mean_color.size(); j++) { 
            data_FM_t.set(j, vision_mean_color.get(j));
        }
    }
    
    /**
     * printFileIfAllowed. Function to print calculated map
     **/
    private void printFileIfAllowed(){
        if(print_to_file) printToFile(data_FM_t);
    }
    
    /**
     * proc: codelet logical process. Collects sensor values, initializes and 
     * calculates top down feature map
    */
    @Override
    public void proc() {
        try { Thread.sleep(300); } catch (Exception e) { Thread.currentThread().interrupt(); }        
        MemoryObject data_bufferMO = (MemoryObject) inputs.get(get_sens);        //Gets  Data from buffer get_sens
        List data_buffer = (List) data_bufferMO.getI();
        List data_FM = (List) featureMap.getI();        
        if(data_FM.size() == timeWindow) data_FM.remove(0);
        data_FM.add(new CopyOnWriteArrayList<>());
        data_FM_t = (CopyOnWriteArrayList<Float>) data_FM.get(data_FM.size()-1);
        for (int j = 0; j < mapDimension; j++) data_FM_t.add((float)0);
        if(data_buffer == null) return;
        if(data_buffer.size() < 1) return;
        inicializeMeanValues();
        separateValues(data_buffer);
        CopyOnWriteArrayList<Float> vision_mean_color = getFM();
        getMeanValues(vision_mean_color);
        featureMap.setI(data_FM_t);
        printFileIfAllowed();
        steps++;
    }
    
    /**
     * printToFile. Function to print calculated map.
     * @param arr
     *        map to print
     **/
    private void printToFile(CopyOnWriteArrayList<Float> arr){
        String user_dir = System.getProperty("user.dir");
        File dir = new File(user_dir);
        if (!dir.exists()) {
            dir.mkdir();
            Logger.getAnonymousLogger().log(Level.INFO, "dir created: {0}",  new Object[]{dir});
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");  
        LocalDateTime now = LocalDateTime.now(); 
        try(FileWriter fw = new FileWriter(user_dir + File.separator +file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(dtf.format(now)+"_"+time_graph+" "+ arr);
            time_graph++;
            out.close();
        } catch (IOException e) { e.printStackTrace(); }        
    }
}
    

