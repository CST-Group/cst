/**
 * ****************************************************************************
 * Copyright (c) 2018  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * R. G. Polizeli and R. R. Gudwin
 * ****************************************************************************
 */
package br.unicamp.cst.behavior.asbac;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rgpolizeli
 */
public class ActionSelectionMechanism {
    
    private final Mind m;
    
    private MemoryObject workingMO;
    private MemoryObject affordancesHierarchiesMO;
    private MemoryObject extractedAffordanceMO;
    private MemoryObject activatedAffordanceMO;
    private MemoryObject synchronizerMO;
    
    private double maxAffordanceActivation = -1.0;
    private double minAffordanceActivation = -1.0;
    private double activationThreshold = -1.0;
    private double decrementPerCount = -1.0;
    private List<ConsummatoryAffordanceType> consummatoryAffordances;
    
    public ActionSelectionMechanism(Mind m) {
        this.m = m;
        this.createAsbacMOs();
    }
    
    public void setConsummatoryAffordances(List<ConsummatoryAffordanceType> consummatoryAffordances){
        this.consummatoryAffordances = consummatoryAffordances;
    }
    
    public void setCountParameters(double maxAffordanceActivation, double minAffordanceActivation, double activationThreshold, double decrementPerCount){
        this.maxAffordanceActivation = maxAffordanceActivation;
        this.minAffordanceActivation = minAffordanceActivation;
        this.activationThreshold = activationThreshold;
        this.decrementPerCount = decrementPerCount;
    }
    
    public void createAsbacMOs(){
        
        Map<String,Map<String,List<Percept>>> workingMemory = new HashMap<>();
        this.workingMO = m.createMemoryObject(MemoryObjectsNames.WORKING_MO, workingMemory);
        
        this.consummatoryAffordances = new ArrayList<>();
        this.affordancesHierarchiesMO = m.createMemoryObject(MemoryObjectsNames.AFFORDANCES_HIERARCHIES_MO, consummatoryAffordances);
        
        List<ExtractedAffordance> extractedAffordances = new ArrayList<>();
        this.extractedAffordanceMO = m.createMemoryObject(MemoryObjectsNames.EXTRACTED_AFFORDANCES_MO, extractedAffordances);
        
        ExtractedAffordance activatedAffordance = null;
        this.activatedAffordanceMO = m.createMemoryObject(MemoryObjectsNames.ACTIVATED_AFFORDANCE_MO, activatedAffordance);
        
        Map<String, MyLock> synchronizers = new HashMap<>();
        this.synchronizerMO = m.createMemoryObject(MemoryObjectsNames.SYNCHRONIZER_MO, synchronizers);
    }
    
    private void validParameters(){
        
        if (this.maxAffordanceActivation == -1.0) {
            throw new IllegalArgumentException();
        }
        
        if (this.minAffordanceActivation == -1.0) {
            throw new IllegalArgumentException();
        }
        
        if (this.activationThreshold == -1.0) {
            throw new IllegalArgumentException();
        }
        
        if (this.decrementPerCount == -1.0) {
            throw new IllegalArgumentException();
        }
        
        if (this.consummatoryAffordances == null) {
            throw new IllegalArgumentException();
        }
        
    }
    
    
    public void createCodelets(){
        
        validParameters();
        
        AffordanceExtractorCodelet affordanceExtractorCodelet = new AffordanceExtractorCodelet();
        affordanceExtractorCodelet.addInput(this.workingMO);
        affordanceExtractorCodelet.addInput(this.affordancesHierarchiesMO);
        affordanceExtractorCodelet.addInput(this.synchronizerMO);
        affordanceExtractorCodelet.addInput(this.activatedAffordanceMO);
        affordanceExtractorCodelet.addInput(this.extractedAffordanceMO);
        affordanceExtractorCodelet.setName("AffordanceExtractorCodelet");
        affordanceExtractorCodelet.setTimeStep(0);
        this.m.insertCodelet(affordanceExtractorCodelet);
        Logger.getLogger(AffordanceExtractorCodelet.class.getName()).setLevel(Level.SEVERE);
        
        CountCodelet countCodelet = new CountCodelet(this.maxAffordanceActivation, this.minAffordanceActivation, this.activationThreshold, this.decrementPerCount);
        countCodelet.addInput(this.extractedAffordanceMO);
        countCodelet.addInput(this.activatedAffordanceMO);
        countCodelet.addInput(this.workingMO);
        countCodelet.addInput(this.synchronizerMO);
        countCodelet.setName("CountCodelet");
        countCodelet.setTimeStep(0);
        this.m.insertCodelet(countCodelet);
        
        this.affordancesHierarchiesMO.setI(this.consummatoryAffordances);
        
        Logger.getLogger(SynchronizationMethods.class.getName()).setLevel(Level.SEVERE);
        
    }
    
    public void createSynchronizationMechanism(){
        for (Codelet codelet : this.m.getCodeRack().getAllCodelets()) {
            String codeletName = codelet.getName();
            SynchronizationMethods.createLock(codeletName,this.synchronizerMO);
        }
    }
}
