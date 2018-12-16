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
import java.util.concurrent.CopyOnWriteArrayList;
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
    private Map<Drive, List<ConsummatoryAffordanceType>> drivesAffordancesMap;
    private List<ExtractedAffordance> extractedAffordances;
    private ExtractedAffordance activatedAffordance;
    
    private final Logger LOGGER = Logger.getLogger(AffordanceExtractorCodelet.class.getName());
    
    public AffordanceExtractorCodelet() {
    }
    
    public void extract(){
        
        for(Drive drive : this.drivesAffordancesMap.keySet()){
  
            for (ConsummatoryAffordanceType consummatoryAffordance : this.drivesAffordancesMap.get(drive)) {
            
                Map< AffordanceType, Map<String, List<Percept>> > allRelevantConcepts = new HashMap<>();
                Map< AffordanceType, List<Map<String, Percept>> > allPermutations = new HashMap<>();
                
                List<ExecutableAffordance> executableAffordances;

                if (this.isCurrentlyExecutable(consummatoryAffordance, this.workingMemory, allRelevantConcepts, allPermutations)) {
                    executableAffordances = new ArrayList<>();
                    executableAffordances.add( new ExecutableAffordance(consummatoryAffordance, 1, null) );
                } else{
                    executableAffordances = this.searchExecutables(consummatoryAffordance, this.workingMemory,allRelevantConcepts,allPermutations);
                }
                
                createExtractedAffordances(consummatoryAffordance,drive,executableAffordances, allPermutations);
            }
            
        }
        
    }
    
    public void createExtractedAffordances(ConsummatoryAffordanceType consummatoryAffordance, Drive factor, List<ExecutableAffordance> executableAffordances, Map< AffordanceType, List<Map<String, Percept>> > allPermutations){
        
        for(ExecutableAffordance executableAffordace : executableAffordances){
           
            AffordanceType aff = executableAffordace.getAffordance();
            Integer hierarchyContribution = executableAffordace.getHierarchyContribution();

            List<Map<String, Percept>> relevantPerceptsPermutations = allPermutations.get(aff);
                
            for (Map<String, Percept> permutation : relevantPerceptsPermutations) {
                ExtractedAffordance extAff;

                if (aff.isExecutable(permutation)) { 
                    ComposeAffordanceType compAff = aff.getComposeAffordance();
                    if (compAff == null) {
                        synchronized(this.extractedAffordancesMO){

                            extAff = this.getExtractedAffordance(aff, permutation);

                            if (extAff == null) { //dont already extracted
                                extAff = new ExtractedAffordance(aff, permutation, hierarchyContribution);
                                extAff.addConsummatoryPath(consummatoryAffordance, factor, executableAffordace.getIntermediateAffordance(), hierarchyContribution);
                                this.extractedAffordances.add(extAff);
                            }                 
                            else{ //already extracted
                                extAff.addConsummatoryPath(consummatoryAffordance, factor, executableAffordace.getIntermediateAffordance(), hierarchyContribution); //add new consummatoryPath if dont exist and/or add the decision factor in existe or new consummatoryPath
                            }
                        }
                    } else{
   
                        synchronized(this.extractedAffordancesMO){
                            compAff = compAff.getClone();
                            Map<String, Percept> composePermutation = compAff.mountComposePermutation(permutation);
                            compAff.setComposePermutation(composePermutation);
                            extAff = this.getExtractedAffordance(compAff.getAffordance(), composePermutation);
                            
                            if (extAff == null) { //dont already extracted
                                extAff = new ExtractedAffordance(compAff.getAffordance(), composePermutation, hierarchyContribution+1);
                                extAff.addConsummatoryPath(consummatoryAffordance, factor, executableAffordace.getIntermediateAffordance(), hierarchyContribution, permutation, compAff, composePermutation);
                                this.extractedAffordances.add(extAff);
                            }                 
                            else{ //already extracted
                                extAff.addConsummatoryPath(consummatoryAffordance, factor, executableAffordace.getIntermediateAffordance(), hierarchyContribution, permutation, compAff, composePermutation); //add new consummatoryPath if dont exist and/or add the decision factor in existe or new consummatoryPath
                            }
                        }
                    }
                            
                } 
                
                else{ //don't executable
                    ComposeAffordanceType compAff = aff.getComposeAffordance();
                    if (compAff != null) {
                        synchronized(this.extractedAffordancesMO){
                            Map<String, Percept> composePermutation = compAff.mountComposePermutation(permutation);
                            extAff = this.getExtractedAffordance(compAff.getAffordance(), composePermutation);
                            if (extAff != null){ 
                                extAff.refreshIntermediateAffordanceInComposeAffordance(consummatoryAffordance, factor, executableAffordace.getIntermediateAffordance(), permutation, compAff.getAffordance(), composePermutation);
                                if (!extAff.hasIntermediateInComposeAffordance()) {
                                    this.extractedAffordances.remove(extAff);
                                }
                            } 
                        }
                    } else{
                        synchronized(this.extractedAffordancesMO){
                            extAff = this.getExtractedAffordance(aff, permutation);
                            if (extAff != null) { //remove this extractedAffordance because it was inexecutable.
                                this.extractedAffordances.remove(extAff);
                            }
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
        ExtractedAffordance target = new ExtractedAffordance(aff, permutation, 0); //hierarchyContribution isn't relevant.
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
     * Verify whether a consummatory affordance type is executable or not in the current situation.
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
     * Verify whether an affordance type (appetitive) is executable or not in the current situation.
     * @param interAff
     * @param perceptsSource
     * @param allRelevantPercepts
     * @param allPermutations
     * @return 
     */
    private boolean isCurrentlyExecutable(IntermediateAffordanceType interAff, Map<String,List<Percept>> perceptsSource, Map<AffordanceType, Map<String, List<Percept>>> allRelevantPercepts, Map< AffordanceType, List<Map<String, Percept>> > allPermutations){
        AffordanceType parentAff = interAff.getParentAffordance();
        AffordanceType aff = interAff.getAffordance();
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
     * @return 
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
     * @return 
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
    
    
    public List<ExecutableAffordance> searchExecutables(AffordanceType parentAff, Map<String,List<Percept>> perceptsSource, Map<AffordanceType, Map<String, List<Percept>>> allRelevantPercepts, Map< AffordanceType, List<Map<String, Percept>> > allPermutations){
        
        List<ExecutableAffordance> executableAffordances = new ArrayList<>();
        
        int level; //level in tree;
        
        Queue<IntermediateAffordanceType> openAffordances; // explored affordances
        
        for (IntermediateAffordanceType intermediateCurrentAffordance : parentAff.getIntermediateAffordances()) {
            
            AffordanceType currentAffordance = intermediateCurrentAffordance.getAffordance();
            
            if ( !(this.isCurrentlyExecutable(intermediateCurrentAffordance, perceptsSource, allRelevantPercepts, allPermutations)) ){ //if current affordance type is NOT executable
                
                openAffordances = new LinkedList<>();
                openAffordances.addAll(currentAffordance.getIntermediateAffordances());
                
                level = 3;
                
                boolean endSearch = false;
                
                while(!endSearch){ //Breadth-first search = busca em largura
                    
                    List<IntermediateAffordanceType> openListClone = new LinkedList<>(openAffordances);
                    
                    for (IntermediateAffordanceType intermediateAff : openListClone) {
                        
                        AffordanceType aff = intermediateAff.getAffordance();
                        if (this.isCurrentlyExecutable(intermediateAff, perceptsSource, allRelevantPercepts, allPermutations)) { //if executable, add it in map and NOT add your children in open list
                            ExecutableAffordance execAff = new ExecutableAffordance(aff, level, intermediateAff);
                            executableAffordances.add(execAff);
                        } else{
                            openAffordances.addAll(aff.getIntermediateAffordances());
                        }
                        openAffordances.remove(intermediateAff);
                        
                    }
                    
                    level+=1;
                    
                    if (openAffordances.isEmpty()) {
                        endSearch = true;
                    }
                    
                }
                
            } else{
                ExecutableAffordance execAff = new ExecutableAffordance(currentAffordance, 2, intermediateCurrentAffordance);
                executableAffordances.add(execAff);
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
                LOGGER.log(Level.INFO, "Removed activated affordance: {0}", activatedAffordance.getAffordanceType().getAffordanceName());
            }
        }   
    }
    
    private void mountDriveAffordancesMap(){
        this.drivesAffordancesMap = new HashMap<>();
        List<ConsummatoryAffordanceType> consummatoryAffordances = new CopyOnWriteArrayList( (List<ConsummatoryAffordanceType>) this.affordancesHierarchiesMO.getI());
        for(ConsummatoryAffordanceType consummatoryAffordance : consummatoryAffordances){
            for(Drive drive : consummatoryAffordance.getDrives()){
                
                List<ConsummatoryAffordanceType> consummatoryAffordancesForDrive = drivesAffordancesMap.get(drive);
                
                if(consummatoryAffordancesForDrive==null){
                    consummatoryAffordancesForDrive = new ArrayList<>();
                    consummatoryAffordancesForDrive.add(consummatoryAffordance);
                    this.drivesAffordancesMap.put(drive, consummatoryAffordancesForDrive);
                } else{
                    if(!consummatoryAffordancesForDrive.contains(consummatoryAffordance)){
                        consummatoryAffordancesForDrive.add(consummatoryAffordance);
                    }
                }
              
            }
        }
    }
    
    //////////////////////
    // OVERRIDE METHODS //
    //////////////////////
    
    @Override
    public void proc(){
        
        this.mountPerceptsSources();
        
        if (this.workingMemory.size() > 0) {
            this.extractedAffordances = (List<ExtractedAffordance>) this.extractedAffordancesMO.getI();
            
            this.mountDriveAffordancesMap();
            
            this.extract();
                
            for (ExtractedAffordance extAff : new ArrayList<>(this.extractedAffordances)) {
                if (!extAff.getAffordanceType().isExecutable(extAff.getPerceptsPermutation())) {
                    this.extractedAffordances.remove(extAff);
                }
            }
            
            this.extractedAffordancesMO.setI(this.extractedAffordances);
            
        }
        
        removeExtractedAffordancesDueToDeletedPercepts();
        
        SynchronizationMethods.synchronize(super.getName(), this.synchronizerMO);
    }

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
    
}
