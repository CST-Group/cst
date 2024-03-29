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
import br.unicamp.cst.core.entities.Codelet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import br.unicamp.cst.support.ToTxt;


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
    private float saturation = 255;                     //Max Value for VisionSensor
    private int old_resolution = 256;                     //Resolution of VisionSensor
    private  int time_graph=0;
    private int convolution_ratio = 16;                    //Slices in each coordinate (x & y) 
    private String file="tests/topDownFM.txt";
    private CopyOnWriteArrayList<Float> goal;  
    private int qdn, get_sens;
    private boolean print_to_file = false;
    private CopyOnWriteArrayList<CopyOnWriteArrayList<Float>> arrayData = new CopyOnWriteArrayList<>();
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
     * @param convolution_ratio 
     *          number of divisions that will be used in convolution to reduce the input resolution
     * @param qdn 
     *          quality dimension number: step to scan input sensors
     * @param print_to_file
     *          boolean that defines if should print to file
     */
    public TopDownFM(int get_sens, String featmapname,int timeWin, int mapDim, CopyOnWriteArrayList<Float> goal, 
            float saturation, int resolution, int convolution_ratio, int qdn, boolean print_to_file) {
        super(featmapname,timeWin,mapDim);
        this.time_graph = 0;
        this.goal = goal;
        this.saturation = saturation; // 255
        this.old_resolution = resolution; // 256
        this.convolution_ratio = convolution_ratio; // 16
        this.qdn = qdn; // 3
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
        CopyOnWriteArrayList<Float> conv_map = new CopyOnWriteArrayList<>();
        float new_res_1_2 = old_resolution/convolution_ratio;
        CopyOnWriteArrayList<Integer> limits = new CopyOnWriteArrayList<>();
        for (int i=0; i<4;i++) limits.add(0);
        for(int n = 0;n<convolution_ratio;n++){
            limits.set(0, (int) (n*new_res_1_2));
            limits.set(1, (int) (new_res_1_2+n*new_res_1_2));
            for(int m = 0;m<convolution_ratio;m++){    
                limits.set(2, (int) (m*new_res_1_2));
                limits.set(3, (int) (new_res_1_2+m*new_res_1_2));
                CopyOnWriteArrayList<Float> meanValues = getPixelValues(limits);
                float vision_color_value = getValue(meanValues);
                conv_map.add(vision_color_value);
            }
        }
        return conv_map;
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
    private CopyOnWriteArrayList<Float> getPixelValues(CopyOnWriteArrayList<Integer> limits) {
        CopyOnWriteArrayList<Float> MeanValues = new CopyOnWriteArrayList<>();
        for(int i=0; i<qdn; i++){
            MeanValues.add((float) 0.0);
        }
        for (int y = limits.get(0); y < limits.get(1); y++) {
            for (int x = limits.get(2); x < limits.get(3); x++) {
                for(int i=0; i<qdn; i++){
                    CopyOnWriteArrayList<Float> values = (CopyOnWriteArrayList<Float>) arrayData.get(i);
                    MeanValues.set(i, MeanValues.get(i)+values.get(y*old_resolution+x));
                }
            }
        }
        float new_res = (old_resolution/convolution_ratio)*(old_resolution/convolution_ratio);
        for(int i=0; i<qdn; i++){
            MeanValues.set(i, MeanValues.get(i)/new_res);
        }
        return MeanValues;
    }

    /**
     * compare. It compares the differences between the values obtained for the 
     * defined regions and a pre-established value.  
     * 
     * @param diff
     *        values to compare
     * @param comp
     *        pre-established value to compare
     * @return boolean that classifies the comparison of values as true or false
     */
    private boolean compare(CopyOnWriteArrayList<Float> diff, float comp){
        boolean result = true;
        for(int i=0; i<qdn; i++){
            result = result && diff.get(i)<comp;
        }
        return result;
    }
    
    /**
     * getValue. Returns the value to be adopted for the region considering the 
     * difference between the average pool obtained and the goal value.
     * @param correct_mean
     *        mean values of each channel
     * @return value
     *        final value of the region
     **/
    private float getValue(CopyOnWriteArrayList<Float> correct_mean) {
        float value;
        CopyOnWriteArrayList<Float> diff = new CopyOnWriteArrayList<>();
        for(int i=0; i<qdn; i++){
            diff.add(Math.abs(correct_mean.get(i)-goal.get(i))/saturation);
        }
        if(compare(diff,      (float) 0.2)) value = (float)1;
        else if(compare(diff, (float) 0.4)) value = (float)0.75;
        else if(compare(diff, (float) 0.6)) value = (float) 0.5;
        else if(compare(diff, (float) 0.8)) value = (float) 0.25;
        else value =(float)0;     
        return value;
    }
    
    /**
     * inicializeMeanValues. Initializes the arrays used to calculate the
     * average pool with zeros
     **/
    private void inicializeMeanValues(){
        for(int i=0; i<qdn; i++){
            CopyOnWriteArrayList<Float> sensor_data = new CopyOnWriteArrayList<>();
            for (int j = 0; j < old_resolution*old_resolution; j++) {
                sensor_data.add((float)0);
            }
            arrayData.add(sensor_data);
        }
    }
    
    /**
     * separateValues. separates the sensor values between the channels used.
     * @param listData
     *        array with sensor values
     */
    private void separateValues(List<Float> listData){
        int count_3 = 0;
        for (int j = 0; j+qdn < listData.size(); j+= qdn) {
            for(int i=0; i<qdn; i++){
                CopyOnWriteArrayList<Float> values = (CopyOnWriteArrayList<Float>) arrayData.get(i);
                
                values.set(count_3, listData.get(j+i));        
                
                arrayData.set(i, values);
                 
            }
            count_3 += 1;
        }
    }
    
    
    /**
     * getMeanValues. Set the feature map values
     * @param conv_map 
     *        array with average pool values
     */
    private void getMeanValues(CopyOnWriteArrayList<Float> conv_map){
        for (int j = 0; j < conv_map.size(); j++) { 
            data_FM_t.set(j, conv_map.get(j));
        }
    }
    
    /**
     * printFileIfAllowed. Function to print calculated map
     **/
    private void printFileIfAllowed(){
        if(print_to_file){
            ToTxt.printToFile(data_FM_t, file, false, time_graph);
            time_graph++;
        }
    }
    
    /**
     * proc: codelet logical process. Collects sensor values, initializes and 
     * calculates top down feature map
    */
    @Override
    public void proc() {
        try { Thread.sleep(300); } catch (Exception e) { Thread.currentThread().interrupt(); }        
        MemoryObject data_bufferMO = (MemoryObject) inputs.get(get_sens);        //Gets  Data from buffer get_sens
        List<Float> data_buffer = (List<Float>) data_bufferMO.getI();
        List<CopyOnWriteArrayList<Float>> data_FM = (List<CopyOnWriteArrayList<Float>>) featureMap.getI();        
        if(data_FM.size() == timeWindow) data_FM.remove(0);
        data_FM.add(new CopyOnWriteArrayList<>());
        data_FM_t = (CopyOnWriteArrayList<Float>) data_FM.get(data_FM.size()-1);
        for (int j = 0; j < mapDimension; j++) data_FM_t.add((float)0);
        if(data_buffer == null) return;
        if(data_buffer.size() < 1) return;
        inicializeMeanValues();
        separateValues(data_buffer);
        CopyOnWriteArrayList<Float> conv_map = getFM();
        getMeanValues(conv_map);
        featureMap.setI(data_FM_t);
        printFileIfAllowed();
        steps++;
    }
    

}
    

