/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.util;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import java.util.List;

/**
 *
 * @author rgudwin
 */
public class CodeletTrackInfo {
    //String mindId;
    String time;
    String codeletName;
    String threadName;
    double activation;
    double threshold;
    boolean isLoop;
    String codeletTimeStep;
    boolean isProfiling;
    String separator;
    List<Memory> mInputs;
    List<Memory> mOutputs;
    List<Memory> mBroadcasts;
            
    public CodeletTrackInfo(Codelet c) {
        //mindId = mindIdentifier;
        time = TimeStamp.getStringTimeStamp(System.currentTimeMillis(),"dd/MM/yyyy HH:mm:ss.SSS");
        codeletName = c.getName();
        threadName = c.getThreadName();
        activation = c.getActivation();
        threshold = c.getThreshold();
        isLoop = c.isLoop();
        codeletTimeStep = TimeStamp.getStringTimeStamp(c.getTimeStep(),"HH:mm:ss.SSS");
        isProfiling = c.isProfiling();
        separator = System.getProperty("line.separator");
        mInputs = c.getInputs();
        mOutputs = c.getOutputs();
        mBroadcasts = c.getBroadcast();
    }    
}
