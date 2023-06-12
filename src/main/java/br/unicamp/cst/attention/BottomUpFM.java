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
 
package br.unicamp.cst.attention;

import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Codelet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import br.unicamp.cst.support.ToTxt;

/**
 * Codelet implementation of Bottom-Up Feature Maps generated by the Attentional 
 * System of Conscious Attention-Based Integrated Model (CONAIM). From a bottom-
 * up perspective, provide information that present saliences in the state to 
 * which attention should be oriented to and that, if attended, will enhance the
 * corresponding region in the attentional map for a certain time and inhibit
 * it in the sequence (inhibition of return).
 * 
 * The maps were computed using an average pool over the 
 * observation of each map at time t, and then the difference between each 
 * region mean and the image mean. It's computed the necessary size of the 
 * kernel and stride to reduce the feature map to a final size. 
 * 
 * @author L. M. Berto
 * @author L. L. Rossi (leolellisr)
 * 
 * @see Codelet
 * @see MemoryObject
 * @see FeatMapCodelet
 */
public class BottomUpFM extends FeatMapCodelet {
    private float saturation;                     //Max Value for VisionSensor
    private int original_resolution;                      //Resolution of VisionSensor
    private int time_graph;
    private int convolution_ratio;                    //Slices in each coordinate (x & y) 
    private int qdn, get_sens;
    private int qd;
    private boolean print_to_file = false, debug;
    private CopyOnWriteArrayList<Float> data_FM_t;
    private String file = "tests/bottomUpFM.txt";
   
    
    /**
     * init BottomUpFM class
    
     * @param get_sens
     *          input sensor index
     
     * @param featmapname
     *          output feature map name
     * @param timeWin
     *          analysed time window,   Buffer size
     * @param mapDim 
     *          output feature map dimension
     * @param saturation 
     *          top down feature map saturation
     * @param resolution 
     *          top down feature map input resolution
     * @param convolution_ratio 
     *          number of divisions that will be used in convolution to reduce the input resolution
     * @param qdn 
     *          quality dimension number: step to scan input sensors
     * @param qd 
     *          quality dimension to scan input sensors
     * @param print_to_file
     *          boolean that defines if should print to file
     * @param debug 
     *          boolean that indicates if log should be printed
     */
    public BottomUpFM(int get_sens, String featmapname,int timeWin, int mapDim, float saturation, 
            int resolution, int convolution_ratio, int qdn, int qd, boolean print_to_file,
            boolean debug) {
        super(featmapname,timeWin,mapDim);
        this.time_graph = 0;
        this.saturation = saturation; // 255
        this.original_resolution = resolution; // 256
        this.convolution_ratio = convolution_ratio; // 16
        this.qdn = qdn; // 3
        this.qd = qd; // 2
        this.print_to_file = print_to_file;
        this.get_sens = get_sens;
        this.debug = debug;
    }

    @Override
    /**
     * access MemoryObjects: inputs 
     * define output: feat_map_name
     * 
     */
    public void accessMemoryObjects() {
        featureMap = (MemoryObject) this.getOutput(feat_map_name);
        
    }
    
    @Override
    public void calculateActivation() {
         // Method calculateActivation isnt used here
    }
    
    /**
     * getFM. Separates the sensor values and calculates the Bottom Up feature map.
     * 
     * @param data_Array
     *        array with sensor values
     * @return bottom up feature map
     * */
    public CopyOnWriteArrayList<Float> getFM(CopyOnWriteArrayList<Float> data_Array){
        float sum = 0;
        for (float value : data_Array) sum += value;
        float mean_all = sum / data_Array.size();
        CopyOnWriteArrayList<Float> data_mean = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<Integer> limits = new CopyOnWriteArrayList<>();
        limits.add(0);
        limits.add(0);
        limits.add(0);
        limits.add(0);
        float new_res = (original_resolution/convolution_ratio)*(original_resolution/convolution_ratio);
        float new_res_1_2 = (original_resolution/convolution_ratio);
        for(int n = 0;n<convolution_ratio;n++){
            limits.set(0, (int) (n*new_res_1_2));
            limits.set(1,  (int) (new_res_1_2+n*new_res_1_2));
            for(int m = 0;m<convolution_ratio;m++){    
                limits.set(2, (int) (m*new_res_1_2));
                limits.set(3, (int) (new_res_1_2+m*new_res_1_2));
                float correct_mean = calculateCorrectMean(data_Array, mean_all, new_res, limits);
                data_mean.add(calculateDataMean(correct_mean));
            }
        }
        if(debug) Logger.getAnonymousLogger().log(Level.INFO, "data_mean: {0} size: {1}",  new Object[]{data_mean, data_mean.size()});
        return data_mean;
    }

    /**
     * calculateCorrectMean. Calculates the average pool values with the pixel values 
     * for the Bottom up feature map.
     * 
     * @param data_Array
     *        array with sensor values
     * @param mean_all
     *        mean of array with sensor values
     * @param new_res
     *        new resolution for the feature map
     * @param ni
     *        Start value for y-axis scan of predefined region
     * @param no
     *        Final value for y-axis scan of predefined region
     * @param mi
     *        Start value for x-axis scan of predefined region
     * @param mo
     *        Final value for x-axis scan of predefined region
     * @return average pool values for the pixel values
     **/
    private float calculateCorrectMean(CopyOnWriteArrayList<Float> data_Array, float mean_all, float new_res, CopyOnWriteArrayList<Integer> limits) {
        float meanValue = 0;
        for (int y = limits.get(0); y < limits.get(1); y++) {
            for (int x = limits.get(2); x < limits.get(3); x++) {
                meanValue += data_Array.get(y*original_resolution+x);
            }
        }
        return meanValue/new_res - mean_all;
    }

    /**
     * calculateDataMean. Calculates the correct average pool values with the 
     * pixel values for the Bottom up feature map, removing the removing the 
     * extreme values.
     **/
    private float calculateDataMean(float correct_mean) {
        if(correct_mean/saturation > 1) {
            return (float) 1;
        } else if(correct_mean/saturation < 0.001) {
            return (float) 0;
        } else {
            return correct_mean/saturation;
        }
    }

    /**
     * inicializeFeatureMap. Initializes the array used to calculate the
     * average pool with zeros
     * @param data_FM
     *        actual feature map
     **/
    private void inicializeFeatureMap(List<CopyOnWriteArrayList<Float>> data_FM) {
        if(data_FM.size() == timeWindow) {
            data_FM.remove(0);
        }
        data_FM.add(new CopyOnWriteArrayList<>());
        data_FM_t = (CopyOnWriteArrayList<Float>) data_FM.get(data_FM.size()-1);
        for (int j = 0; j < mapDimension; j++) {
            data_FM_t.add((float)0);
        }
    }

    /**
     * getDataArray. get sensor values used.
     * @param listData
     *        array with sensor values
     */
    private CopyOnWriteArrayList<Float> getDataArray(List<Float> list_data) {
        CopyOnWriteArrayList<Float> data_Array = new CopyOnWriteArrayList<>();
        if(debug) Logger.getAnonymousLogger().log(Level.INFO, "list_data: {0} size: {1}",  new Object[]{list_data, list_data.size()});
        for (int j = 0; j < original_resolution*original_resolution; j++) {
            data_Array.add((float)0);
        }
        int count = 0;
        for (int j = 0; j+qdn < list_data.size()+1; j+= qdn) {
            data_Array.set(count, list_data.get(j+qd));
            count += 1;
        }
        return data_Array;
    }

    /**
     * updateDataFM. Updates the bottom up feature map
     * @param data_FM
     *        actual feature map
     * @param data_Array
     *        array with sensor values
     **/
    private void updateDataFM(List<CopyOnWriteArrayList<Float>> data_FM, CopyOnWriteArrayList<Float> data_Array) {
        CopyOnWriteArrayList<Float> data_mean = getFM(data_Array);
        data_FM_t = (CopyOnWriteArrayList<Float>) data_FM.get(data_FM.size()-1);
        for (int j = 0; j < data_mean.size(); j++) {
            data_FM_t.set(j, data_mean.get(j));
        }
    }

    /**
     * proc: codelet logical process. Collects sensor values, initializes and 
     * calculates bottom up feature map
    */
    @Override
    public void proc() {
        try { Thread.sleep(50); } catch (Exception e) { Thread.currentThread().interrupt(); }
        MemoryObject data_bufferMO = (MemoryObject) inputs.get(get_sens);
        List<Float> data_buffer = (List<Float>) data_bufferMO.getI();
        List<CopyOnWriteArrayList<Float>> data_FM = (List<CopyOnWriteArrayList<Float>>) featureMap.getI();
        inicializeFeatureMap(data_FM);
        if(data_buffer == null || data_buffer.isEmpty()) {
            return;
        }
        CopyOnWriteArrayList<Float> data_Array = getDataArray(data_buffer);        
        updateDataFM(data_FM, data_Array);
        if(debug){
            Logger.getAnonymousLogger().log(Level.INFO, "data_buffer: {0} size: {1}",  new Object[]{data_buffer, data_buffer.size()});
            Logger.getAnonymousLogger().log(Level.INFO, "data_Array: {0} size: {1}",  new Object[]{data_Array, data_Array.size()});
            Logger.getAnonymousLogger().log(Level.INFO, "data_FM: {0} size: {1}",  new Object[]{data_FM, data_FM.size()});
        }
        featureMap.setI(data_FM_t);
        if(print_to_file) {
            ToTxt.printToFile((CopyOnWriteArrayList<Float>) data_FM.get(data_FM.size()-1), file, debug, time_graph);
            time_graph++;
        }
        steps++;
    }



    
}
    

