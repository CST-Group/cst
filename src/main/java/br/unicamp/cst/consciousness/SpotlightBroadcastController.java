/*******************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     K. Raizer, A. L. O. Paraense, R. R. Gudwin - initial API and implementation
 ******************************************************************************/

package br.unicamp.cst.consciousness;

import java.util.ArrayList;
import java.util.List;

import br.unicamp.cst.core.entities.CodeRack;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import java.util.Comparator;
import java.util.Random;

/**
 * A codelet-based implementation of the Global Workspace Theory, originally
 * formulated in [1988 Baars] Bernard J. Baars. A Cognitive Theory of
 * Consciousness. Cambridge University Press, 1988.
 * 
 * @author A. L. O. Paraense
 * @see Codelet
 *
 */
public class SpotlightBroadcastController extends Codelet {
	
    private List<Codelet> consciousCodelets;

    /** access to all codelets, so the broadcast can be made */
    private CodeRack codeRack;

    private double thresholdActivation = 0.9d;
    
    // Utilities for selection policies
    private Random rand;
    private int iterateIndex = 0;

    /** The default policy is MAX
     * MAX: Broadcast codelet with max activation
     * MIN: Broadcast codelet with min activation
     * RANDOM_FLAT: Broadcast a random codelet 
     * RANDOM_PROPORTIONAL: Broadcast a random codelet using activation as a weight.
     * ITERATE: Broadcat a codelet in an iterative way.
     * ALL_MAX: Broadcast all codelets above thresholdActivation
     * ALL_MIN: Broadcast all codelets below thresholdActivation
     */
    public enum Policy {
        MAX, 
        MIN,
        RANDOM_FLAT, 
        RANDOM_PROPORTIONAL,
        ITERATE,
        ALL_MAX,
        ALL_MIN
    };

    /**
     * Policy used for selecting a MemoryObject at the MemoryContainer
     */
    private SpotlightBroadcastController.Policy policy;

    /**
     * Creates a SpotlightBroadcastController.
     * 
     * @param codeRack the codeRack containing all Codelets.
     */
    public SpotlightBroadcastController(CodeRack codeRack) {
        initSpotlightBroadcastController(codeRack);
        this.policy = Policy.MAX;
    }

    /**
     * Creates a SpotlightBroadcastController.
     * 
     * @param codeRack the codeRack containing all Codelets.
     * @param policy define th policy to be set
     */
    public SpotlightBroadcastController(CodeRack codeRack, Policy policy) {
        initSpotlightBroadcastController(codeRack);
        this.policy = policy;
    }

    private void initSpotlightBroadcastController(CodeRack codeRack){
            this.setName("SpotlightBroadcastController");
            this.codeRack = codeRack;
            consciousCodelets = null;
            this.timeStep = 300l;
            this.rand = new Random();
            this.consciousCodelets = new ArrayList<Codelet>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see br.unicamp.cogsys.core.entities.Codelet#accessMemoryObjects()
     */
    @Override
    public void accessMemoryObjects() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see br.unicamp.cogsys.core.entities.Codelet#calculateActivation()
     */
    @Override
    public void calculateActivation() {
        try {
            setActivation(0.0d);
        } catch (CodeletActivationBoundsException e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see br.unicamp.cogsys.core.entities.Codelet#proc()
     */
    @Override
    public void proc() {
        // Reset state
        consciousCodelets.clear();

        if (codeRack != null) {
            List<Codelet> allCodeletsList = codeRack.getAllCodelets();
            allCodeletsList.remove(this);
            if (allCodeletsList != null && !allCodeletsList.isEmpty()) {
                // Choose which codelets become conscious based on policy
                selectCodelets(allCodeletsList);
                
                // Send information from conscious codelets to everyone else
                broadcastMemories(allCodeletsList);
                
            }
        }
    }
    
    /**
     * Broadcasts outputs from conscious codelets to all other codelets.
     * @param allCodelets List of all codelets in the rack
     */
    private void broadcastMemories(List<Codelet> allCodelets) {
        for (Codelet target : allCodelets) {
            List<Memory> memoriesToBeBroadcasted = new ArrayList<>();
            for (Codelet sender : consciousCodelets) {
                // Don't broadcast to self (optional check, depends on architecture, kept from original)
                if (!target.getName().equalsIgnoreCase(sender.getName())) {
                    if (sender.getOutputs() != null) {
                        memoriesToBeBroadcasted.addAll(sender.getOutputs());
                    }
                }
            }
            target.setBroadcast(memoriesToBeBroadcasted);
        }
    }

    /**
    * Logic to select codelets based on the active Policy.
    * Populates the consciousCodelets list.
    * @param consciousCodeletCandidates List of available codelets
    */
    private void selectCodelets(List<Codelet> consciousCodeletCandidates) {
        switch (this.policy) {
            case MAX:
                // Find single codelet with highest activation
                consciousCodeletCandidates.stream()
                    .max(Comparator.comparingDouble(Codelet::getActivation))
                    .ifPresent(consciousCodelets::add);
                
                break;

            case MIN:
                // Find single codelet with lowest activation
                consciousCodeletCandidates.stream()
                    .min(Comparator.comparingDouble(Codelet::getActivation))
                    .ifPresent(consciousCodelets::add);
                break;

            case ALL_MAX:
                // All codelets above threshold
                consciousCodeletCandidates.stream()
                    .filter(c -> c.getActivation() > thresholdActivation)
                    .forEach(consciousCodelets::add);
                break;

            case ALL_MIN:
                // All codelets below threshold
                consciousCodeletCandidates.stream()
                    .filter(c -> c.getActivation() < thresholdActivation)
                    .forEach(consciousCodelets::add);
                break;

            case RANDOM_FLAT:
                int i = rand.nextInt(consciousCodeletCandidates.size());
                consciousCodelets.add(consciousCodeletCandidates.get(i));
                break;

            case RANDOM_PROPORTIONAL:
                Codelet winner = getProportionalCodelet(consciousCodeletCandidates);
                if (winner != null) {
                    consciousCodelets.add(winner);
                }
                break;

            case ITERATE:
                Codelet next = getIterateCodelet(consciousCodeletCandidates);
                if (next != null) {
                    consciousCodelets.add(next);
                }
                break;
        }
    }
    
    /**
     * Implements Roulette Wheel Selection based on activation.
     * @param List of all codelets
     * @return The random codelet with weight proportional to activation
     */
    private Codelet getProportionalCodelet(List<Codelet> candidates) {
        double[] indexFrom = new double[candidates.size()];
        double[] indexTo = new double[candidates.size()];
        
        // Build the probability intervals
        for (int i = 0; i < candidates.size(); i++) {
            if (i == 0) {
                indexFrom[i] = 0;
            } else {
                indexFrom[i] = indexTo[i - 1];
            }
            
            double interval = candidates.get(i).getActivation();
            // Ensure non-negative activation for probability
            if (interval < 0) interval = 0; 
            
            indexTo[i] = indexFrom[i] + interval;
        }

        double totalActivation = indexTo[candidates.size() - 1];
        double wheel = rand.nextDouble();
        double target = wheel * totalActivation;

        if (target == 0) {
            // If total activation is 0, fallback to random flat
            int randomIdx = rand.nextInt(candidates.size());
            return candidates.get(randomIdx);
        }

        for (int j = 0; j < candidates.size(); j++) {
            if (indexFrom[j] <= target && target < indexTo[j]) {
                return candidates.get(j);
            }
        }
        // Fallback (should theoretically not be reached if math holds)
        return candidates.get(0);
    }
    
    /**
     * Implements Round-Robin selection.
     * @param List of all codelets
     * @return The codelet selected in iterative way
     */
    private Codelet getIterateCodelet(List<Codelet> candidates) {
        if (!candidates.isEmpty()) {
            // Ensure index is within bounds (handle list size changes)
            if (iterateIndex >= candidates.size()) {
                iterateIndex = 0;
            }
            
            Codelet selected = candidates.get(iterateIndex);
            
            // Advance index for next call
            iterateIndex++;
            if (iterateIndex >= candidates.size()) {
                iterateIndex = 0;
            }
            
            return selected;
        } 
        return null;
    }

    /**
    * Gets the current activation threshold used for ALL_MAX and ALL_MIN policies.
    * @return the threshold value
    */
    public double getThresholdActivation() {
        return thresholdActivation;
    }

   /**
    * Sets the activation threshold.
    * @param thresholdActivation the new threshold value
    */
    public void setThresholdActivation(double thresholdActivation) {
        this.thresholdActivation = thresholdActivation;
    }

    /**
    * Gets the current policy.
    * @return the policy enum
    */
    public Policy getPolicy() {
        return policy;
    }

    /**
    * Sets the current policy.
    * @param policy the new policy to be set
    */
    public void setPolicy(Policy policy) {
        this.policy = policy;
    }
}
