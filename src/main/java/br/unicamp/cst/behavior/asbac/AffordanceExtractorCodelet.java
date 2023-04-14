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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import br.unicamp.cst.motivational.Drive;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rgpolizeli
 */
public class AffordanceExtractorCodelet extends Codelet {
    
    private MemoryObject workingMO;
    private MemoryObject extractedAffordancesMO; 
    private MemoryObject affordancesHierarchiesMO;
    private MemoryObject activatedAffordanceMO;
    private MemoryObject synchronizerMO;
    
    private Map<String, List<Percept>> workingMemory;
    private Map<Drive, List<AffordanceType>> drivesAffordancesMap;
    private List<ExtractedAffordance> extractedAffordances;
    private ExtractedAffordance activatedAffordance;
    
    private final Logger LOGGER = Logger.getLogger(AffordanceExtractorCodelet.class.getName());
    
    public AffordanceExtractorCodelet() {
    }
    
    public void extract(){
        
        for(Drive drive : this.drivesAffordancesMap.keySet()){
  
            for (AffordanceType consummatoryAffordance : this.drivesAffordancesMap.get(drive)) {
            
                Map< AffordanceType, Map<String, List<Percept>> > allRelevantConcepts = new HashMap<>();
                Map< AffordanceType, List<Map<String, Percept>> > allPermutations = new HashMap<>();
                
                List<AffordanceType> executableAffordances;

                if (this.isCurrentlyExecutable(consummatoryAffordance, this.workingMemory, allRelevantConcepts, allPermutations)) {
                    executableAffordances = new ArrayList<>();
                    executableAffordances.add(consummatoryAffordance);
                } else{
                    executableAffordances = this.searchExecutables(consummatoryAffordance, this.workingMemory,allRelevantConcepts,allPermutations);
                }
                
                createExtractedAffordances(consummatoryAffordance,drive,executableAffordances, allPermutations);
            }
            
        }
        
    }
    
    public void createExtractedAffordances(AffordanceType consummatoryAffordance, Drive drive, List<AffordanceType> executableAffordances, Map< AffordanceType, List<Map<String, Percept>> > allPermutations){
        
        for(AffordanceType executableAffordace : executableAffordances){
            
            List<Map<String, Percept>> relevantPerceptsPermutations = allPermutations.get(executableAffordace);
            
            for (Map<String, Percept> permutation : relevantPerceptsPermutations) {
               
                if (executableAffordace.isExecutable(permutation)) { 
                    ExtractedAffordance extAff;
                        
                    synchronized(this.extractedAffordancesMO){
                        this.extractedAffordances = (List<ExtractedAffordance>) this.extractedAffordancesMO.getI();
                        extAff = this.getExtractedAffordance(executableAffordace, permutation);
                        if (extAff == null) { //dont already extracted
                            extAff = new ExtractedAffordance(executableAffordace.getAffordanceName(), permutation);
                            extAff.addHierarchyNode(drive,executableAffordace);
                            this.extractedAffordances.add(extAff);
                        }                 
                        else{ //already extracted, only add new hierarchyNode
                            extAff.addHierarchyNode(drive,executableAffordace); 
                        }
                    }
                } 
            }   
        }
    }
    
    //////////////////////
    // AUXILIARY METHODS //
    //////////////////////
    
    private ExtractedAffordance getExtractedAffordance(AffordanceType aff, Map<String,Percept> permutation){
        ExtractedAffordance target = new ExtractedAffordance(aff.getAffordanceName(), permutation);
        int position = this.extractedAffordances.indexOf(target);
        if (position != -1) {
            return this.extractedAffordances.get(position);
        } else{
            return null;
        }
    }
    
    /**
     * Verify whether the cartesian product should be build to an affordance type or not.
     * @param relevantPercepts
     * @return 
     */
    private boolean isCombinationTime(Map<String, List<Percept>> relevantPercepts){
        for (List<Percept> percepts : relevantPercepts.values()) {
            if (percepts.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * Verify whether a consummatory AffordanceType is executable or not in the current situation.
     * @param aff
     * @param perceptsSource
     * @param allRelevantPercepts
     * @param allPermutations
     * @return 
     */
    private boolean isCurrentlyExecutable(AffordanceType aff, Map<String,List<Percept>> perceptsSource, Map<AffordanceType, Map<String, List<Percept>>> allRelevantPercepts, Map< AffordanceType, List<Map<String, Percept>> > allPermutations){
        boolean affTypeIsExecutable = false;
        Map<String, List<Percept>> relevantPercepts = new HashMap<>();
        Map<String,List<String>> relevantPerceptsCategoriesMap = aff.getRelevantPerceptsCategories();
        
        for (Map.Entry<String,List<String>> entry : relevantPerceptsCategoriesMap.entrySet()){
            String relevantPerceptCategory = entry.getKey();
            
            List<Percept> perceptsOfAllCategories= new ArrayList<>();
            for (String perceptCategory : entry.getValue()) {
                List<Percept> perceptsOfCategory = perceptsSource.get(perceptCategory);
                if (perceptsOfCategory!=null) {
                    perceptsOfAllCategories.addAll(perceptsOfCategory);
                }
            }
            relevantPercepts.put(relevantPerceptCategory,this.searchRelevantPercepts(aff, perceptsOfAllCategories));  
        }
        
        if (!relevantPercepts.isEmpty()) {
            allRelevantPercepts.put(aff, relevantPercepts);
        }
        
        if (!isCombinationTime(relevantPercepts)) {
            return false;
        }
        
        List<Map<String, Percept>> relevantPerceptsPermutations = new ArrayList<>();
        relevantPerceptsPermutations.addAll(this.getPerceptsCombinations(aff, relevantPercepts));
        if (allPermutations.containsKey(aff)) {
            this.putPermutationsInMap(allPermutations.get(aff), relevantPerceptsPermutations);
        } else{
            if (!relevantPerceptsPermutations.isEmpty()) {
                allPermutations.put(aff, relevantPerceptsPermutations);
            }
        }
        
        for (int i=0; i< relevantPerceptsPermutations.size() && !affTypeIsExecutable; i++) {
            Map<String, Percept> permutation = relevantPerceptsPermutations.get(i);
            if (aff.isExecutable(permutation)) {
                return true;
            }
        }
        
        return affTypeIsExecutable;
    }
    
    /**
     * Verify whether an appetitive AffordanceType is executable or not in the current situation.
     * @param interAff
     * @param perceptsSource
     * @param allRelevantPercepts
     * @param allPermutations
     * @return 
     */
    private boolean isCurrentlyExecutable(AffordanceType aff, AffordanceType parentAff, Map<String,List<Percept>> perceptsSource, Map<AffordanceType, Map<String, List<Percept>>> allRelevantPercepts, Map< AffordanceType, List<Map<String, Percept>> > allPermutations){
        
        boolean affTypeIsExecutable = false;
        Map<String, List<Percept>> relevantPercepts = new HashMap<>(); 
        
        Map<String,List<String>> affRelevantPerceptCategoriesMap = aff.getRelevantPerceptsCategories();
        Map<String,List<String>> parentAffRelevantPerceptCategoriesMap = parentAff.getRelevantPerceptsCategories();

        for (Map.Entry<String,List<String>> entry : affRelevantPerceptCategoriesMap.entrySet()) {
            String relevantPerceptCategory = entry.getKey();
            if (parentAffRelevantPerceptCategoriesMap.get(relevantPerceptCategory)!=null) {
                List<Percept> relevantPercetsToParentAffPerceptCategory = allRelevantPercepts.get(parentAff).get(relevantPerceptCategory);
                relevantPercepts.put(relevantPerceptCategory,this.searchRelevantPercepts(aff, relevantPercetsToParentAffPerceptCategory));
            } else{
                List<Percept> perceptsOfAllCategories= new ArrayList<>();
                for (String perceptCategory : entry.getValue()) {
                    List<Percept> perceptsOfCategory = perceptsSource.get(perceptCategory);
                    if (perceptsOfCategory!=null) {
                        perceptsOfAllCategories.addAll(perceptsOfCategory);
                    }
                }
                relevantPercepts.put(relevantPerceptCategory,this.searchRelevantPercepts(aff, perceptsOfAllCategories)); 
            }
        }
        
        allRelevantPercepts.put(aff, relevantPercepts);
        
        if (!isCombinationTime(relevantPercepts)) {
            return false;
        }
        
        List<Map<String, Percept>> relevantPerceptsPermutations = new ArrayList<>();
        relevantPerceptsPermutations.addAll(this.getPerceptsCombinations(aff, relevantPercepts));
        if (allPermutations.containsKey(aff)) {
            this.putPermutationsInMap(allPermutations.get(aff), relevantPerceptsPermutations);
        } else{
            if (!relevantPerceptsPermutations.isEmpty()) {
                allPermutations.put(aff, relevantPerceptsPermutations);
            }
        }
        
        for (int i=0; i< relevantPerceptsPermutations.size() && !affTypeIsExecutable; i++) {
            Map<String, Percept> permutation = relevantPerceptsPermutations.get(i);
            if (aff.isExecutable(permutation)) {
                return true;
            }
        }
        
        return affTypeIsExecutable;
    }
    
    private void putPermutationsInMap(List<Map<String, Percept>> currentRelevantPerceptsPermutations, List<Map<String, Percept>> newRelevantPerceptsPermutations){
        for (Map<String, Percept> permutation : newRelevantPerceptsPermutations) {
            if (!currentRelevantPerceptsPermutations.contains(permutation)) {
                currentRelevantPerceptsPermutations.add(permutation);
            }
        }
    }
    
    /**
     * for percept's category of affordance, get all percepts in perceptsSource with this category that is relevant to aff.
     * @param aff
     * @param perceptsOfCategory
     * @return a list of Percepts
     */
    public List<Percept> searchRelevantPercepts(AffordanceType aff, List<Percept> perceptsOfCategory){ //find relevant concepts with properties to affordance. 
        
        List<Percept> relevantPerceptsOfCategory = new ArrayList<>();
        
        if (perceptsOfCategory!=null) {
            for (Percept perceptInSource : perceptsOfCategory) {
                if (aff.isRelevantPercept(perceptInSource)) { //if percept is relevant to aff. type
                    relevantPerceptsOfCategory.add(perceptInSource);
                }
            }
        }
            
        return relevantPerceptsOfCategory;
    }
    
    /**
     * Preprocess and init the computation of the cartesian product.
     * @param aff
     * @param relevantPercepts
     * @return a list of map<String,Percept>
     */
    public List<Map<String, Percept>> getPerceptsCombinations(AffordanceType aff, Map<String, List<Percept>> relevantPercepts){
        
        Map<Integer, Percept[]> sets = new HashMap<>();
        int qtt = 0;
        
        for (Map.Entry<String, List<Percept>> entry : relevantPercepts.entrySet()) {
            List<Percept> relevantPerceptsOfCategory = entry.getValue();
            Percept[] relevantPerceptsOfCategoryArray = new Percept[relevantPerceptsOfCategory.size()];
            relevantPerceptsOfCategory.toArray(relevantPerceptsOfCategoryArray);
            sets.put(qtt, relevantPerceptsOfCategoryArray);
            
            qtt++;
        }
        
        Percept[] combination = new Percept[sets.size()];
        List<Percept[]> combinations = new ArrayList<>();
        
        this.makeCombinations(sets, 0, combination, combinations);
        
        List<Map<String,Percept>> convertedCombinations = new ArrayList<>();
        for (Percept[] comb : combinations) {
            Map<String,Percept> convertedCombination = new HashMap<>();
            for (int i = 0; i < comb.length; i++) {
                convertedCombination.put(comb[i].getCategory(), comb[i]);
            }
            convertedCombinations.add(convertedCombination);
        }
        
        return convertedCombinations;
    }
    
    /**
     * Compute the cartesian product.
     * @param sets
     * @param n
     * @param combination
     * @param combinations 
     */
    private void makeCombinations(Map<Integer, Percept[]> sets, int n, Percept[] combination, List<Percept[]> combinations){
        
        if (n == (sets.size()-1)) {
            for (int i = 0; i < sets.get(n).length; i++) {
                combination[n] = sets.get(n)[i];
                Percept[] combinationCpy = new Percept[combination.length];
                for (int k = 0; k < combination.length; k++) {
                    combinationCpy[k] = combination[k];
                }
                combinations.add(combinationCpy);
            }
        } else{
            for (int i = 0; i < sets.get(n).length; i++) {
                combination[n] = sets.get(n)[i];
                makeCombinations(sets, n+1, combination, combinations);
            }
        }    
    }
    
    public List<AffordanceType> searchExecutables(AffordanceType parentAff, Map<String,List<Percept>> perceptsSource, Map<AffordanceType, Map<String, List<Percept>>> allRelevantPercepts, Map< AffordanceType, List<Map<String, Percept>> > allPermutations){
        
        List<AffordanceType> executableAffordances = new ArrayList<>();
        Queue<AffordanceType> openAffordances; // to explore affordances
        
        for (AffordanceType currentAffordance : parentAff.getChildren()) {
            
            if ( !(this.isCurrentlyExecutable(currentAffordance, parentAff, perceptsSource, allRelevantPercepts, allPermutations)) ){ //if current affordance type is NOT executable
                
                openAffordances = new LinkedList<>();
                openAffordances.addAll(currentAffordance.getChildren());
                
                boolean endSearch = false;
                
                while(!endSearch){ //Breadth-first search
                    
                    List<AffordanceType> openListClone = new LinkedList<>(openAffordances);
                    
                    for (AffordanceType aff : openListClone) {
                        
                        if (this.isCurrentlyExecutable(aff, aff.getParent(), perceptsSource, allRelevantPercepts, allPermutations)) { //if executable, add it in map and NOT add your children in open list
                            executableAffordances.add(aff);
                        } else{
                            openAffordances.addAll(aff.getChildren());
                        }
                        openAffordances.remove(aff);
                    }
                    
                    if (openAffordances.isEmpty()) {
                        endSearch = true;
                    }
                }
            } else{
                executableAffordances.add(currentAffordance);
            }
        }
        
        return executableAffordances;
    }
    
    private void mountPerceptsSources(){
        
        this.workingMemory = new HashMap<>();
        
        synchronized(this.workingMO){
            Map<String, Map<String,List<Percept>>> workingMemoryContent = (Map<String, Map<String,List<Percept>>>) this.workingMO.getI();
            if(workingMemoryContent != null && !workingMemoryContent.isEmpty()){
                for(Map<String,List<Percept>> workingMemoryItem : workingMemoryContent.values()){
                    
                    for(Map.Entry<String,List<Percept>> e : workingMemoryItem.entrySet()){
                        List<Percept> perceptsOfCategoryInWMO = this.workingMemory.get(e.getKey());
                        if(perceptsOfCategoryInWMO != null){
                            for(Percept p : e.getValue()){
                                if(!perceptsOfCategoryInWMO.contains(p)){
                                    perceptsOfCategoryInWMO.add(p);
                                }
                            }
                        } else{
                            perceptsOfCategoryInWMO = new ArrayList<>(e.getValue());
                            this.workingMemory.put(e.getKey(), perceptsOfCategoryInWMO);
                        }
                    }
                } 
            }
        }
    }
    
    /**
     * Check whether or not the percept is in working memory.
     * @param p is the percept to check.
     * @return 
     */
    private boolean workingMemoryContains(Percept p){
        boolean contains = false;
        
        if(!this.workingMemory.isEmpty()){
            List<Percept> perceptsOfCategory = this.workingMemory.get(p.getCategory());
            if(perceptsOfCategory!= null && perceptsOfCategory.contains(p)){
                return true;
            }
        }
        
        return contains;
    }
    
    private void removeExtractedAffordancesDueToDeletedPercepts(){
    
        List<ExtractedAffordance> deletedExtractedAffordances = new ArrayList<>();

        synchronized(this.extractedAffordancesMO){
            this.extractedAffordances = (List<ExtractedAffordance>) this.extractedAffordancesMO.getI();
            List<ExtractedAffordance> extractedAffordancesBkp = new ArrayList<>(this.extractedAffordances);

            for (ExtractedAffordance aff : extractedAffordancesBkp) {
                Map<String, Percept> permutation = aff.getPerceptsPermutation();
                Iterator<Percept> permutationPerceptsIterator = permutation.values().iterator();
                while (this.extractedAffordances.contains(aff) && permutationPerceptsIterator.hasNext()) {
                    Percept p = permutationPerceptsIterator.next();
                    if (!workingMemoryContains(p)) {
                        this.extractedAffordances.remove(aff);
                        deletedExtractedAffordances.add(aff);
                    }
                }
            }
        }

        synchronized(this.activatedAffordanceMO){
            this.activatedAffordance = (ExtractedAffordance) this.activatedAffordanceMO.getI();
            if (deletedExtractedAffordances.contains(this.activatedAffordance)) {
                this.activatedAffordanceMO.setI(null);
                LOGGER.log(Level.INFO, "Removed activated affordance: {0}", activatedAffordance.getAffordanceName());
            }
        }   
    }
    
    public <K,V> Map<K,V> deepCopyMap(Map<K,V> m){
        synchronized(m){
            Map<K, V> copy = new HashMap<>();
            for(Map.Entry<K,V> entry : m.entrySet()){
                copy.put( entry.getKey(),entry.getValue() );
            }
            return copy;
        }
    }
    
    private void getDriveAffordancesMap(){
        synchronized(this.affordancesHierarchiesMO){
            this.drivesAffordancesMap = (Map<Drive,List<AffordanceType>>) this.affordancesHierarchiesMO.getI();
            this.drivesAffordancesMap = this.deepCopyMap(this.drivesAffordancesMap);
        }
    }
    
    private void removeNotExecutableExtractedAffordances(){
        synchronized(this.extractedAffordancesMO){
            this.extractedAffordances = (List<ExtractedAffordance>) this.extractedAffordancesMO.getI();
            for (ExtractedAffordance extAff : new ArrayList<>(this.extractedAffordances)) {
                for(Map.Entry<Drive,List<AffordanceType>> entry : this.deepCopyMap(extAff.getHierachiesNodes()).entrySet()){
                    for( AffordanceType aff : new ArrayList<>(entry.getValue()) ){
                        if(!aff.isExecutable(extAff.getPerceptsPermutation())){
                            extAff.removeHierarchyNode(entry.getKey(), aff);
                        }
                    }
                }
                if(extAff.getHierachiesNodes().isEmpty()){
                    this.extractedAffordances.remove(extAff);
                }
            }
        }
    }
    
    //////////////////////
    // OVERRIDE METHODS //
    //////////////////////
    
    @Override
    public void accessMemoryObjects() {
        this.workingMO = (MemoryObject) this.getInput(MemoryObjectsNames.WORKING_MO);
        this.affordancesHierarchiesMO = (MemoryObject) this.getInput(MemoryObjectsNames.AFFORDANCES_HIERARCHIES_MO);
        this.synchronizerMO = (MemoryObject) this.getInput(MemoryObjectsNames.SYNCHRONIZER_MO);
        this.extractedAffordancesMO = (MemoryObject) this.getInput(MemoryObjectsNames.EXTRACTED_AFFORDANCES_MO);
        this.activatedAffordanceMO = (MemoryObject) this.getInput(MemoryObjectsNames.ACTIVATED_AFFORDANCE_MO);
    }

    @Override
    public void calculateActivation() {
    }
    
    @Override
    public void proc(){
        
        this.mountPerceptsSources();
        
        if (this.workingMemory.size() > 0) {
            this.getDriveAffordancesMap();
            if(!this.drivesAffordancesMap.isEmpty()){
                this.extract();
                this.removeNotExecutableExtractedAffordances();
                this.extractedAffordancesMO.setI(this.extractedAffordances);
            }
        } 
        
        removeExtractedAffordancesDueToDeletedPercepts();
        
        SynchronizationMethods.synchronize(super.getName(), this.synchronizerMO);
    }
}
