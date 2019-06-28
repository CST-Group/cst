/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.motivational;

import br.unicamp.cst.core.entities.Codelet;
import java.util.ArrayList;

import java.util.List;

/**
 * @author du
 */
public class MotivationalSubsystemModule {

    // Codelets in System 1;
    private List<MotivationalCodelet> motivationalCodelets;
    private List<EmotionalCodelet> emotionalCodelets;
    private List<MoodCodelet> moodCodelets;

    // Codelets in System 2;
    private List<AppraisalCodelet> appraisalCodelets;
    private List<GoalCodelet> goalCodelets;


    public MotivationalSubsystemModule() {
        setMotivationalCodelets(new ArrayList<>());
        setEmotionalCodelets(new ArrayList<>());
        setMoodCodelets(new ArrayList<>());
        setAppraisalCodelets(new ArrayList<>());
        setGoalCodelets(new ArrayList<>());
    }

    public MotivationalSubsystemModule(List<MotivationalCodelet> motivationalCodelets,
                                       List<EmotionalCodelet> emotionalCodelets,
                                       List<MoodCodelet> moodCodelets,
                                       List<AppraisalCodelet> appraisalCodelets,
                                       List<GoalCodelet> goalCodelets) {

        setMotivationalCodelets(motivationalCodelets);
        setEmotionalCodelets(emotionalCodelets);
        setMoodCodelets(moodCodelets);
        setAppraisalCodelets(appraisalCodelets);
        setGoalCodelets(goalCodelets);

    }

    public boolean verifyExistCodelets() {

        if (getAppraisalCodelets().size() > 0 || getEmotionalCodelets().size() > 0 || getMoodCodelets().size() > 0 || getGoalCodelets().size() > 0 || getMotivationalCodelets().size() > 0)
            return true;
        else
            return false;
    }

    /**
     * @return the motivationalCodelets
     */
    public List<MotivationalCodelet> getMotivationalCodelets() {
        return motivationalCodelets;
    }

    /**
     * @param motivationalCodelets the motivationalCodelets to set
     */
    public void setMotivationalCodelets(List<MotivationalCodelet> motivationalCodelets) {
        this.motivationalCodelets = motivationalCodelets;
    }

    /**
     * @return the emotionalCodelets
     */
    public List<EmotionalCodelet> getEmotionalCodelets() {
        return emotionalCodelets;
    }

    /**
     * @param emotionalCodelets the emotionalCodelets to set
     */
    public void setEmotionalCodelets(List<EmotionalCodelet> emotionalCodelets) {
        this.emotionalCodelets = emotionalCodelets;
    }

    /**
     * @return the moodCodelets
     */
    public List<MoodCodelet> getMoodCodelets() {
        return moodCodelets;
    }

    /**
     * @param moodCodelets the moodCodelets to set
     */
    public void setMoodCodelets(List<MoodCodelet> moodCodelets) {
        this.moodCodelets = moodCodelets;
    }

    /**
     * @return the appraisalCodelets
     */
    public List<AppraisalCodelet> getAppraisalCodelets() {
        return appraisalCodelets;
    }

    /**
     * @param appraisalCodelets the appraisalCodelets to set
     */
    public void setAppraisalCodelets(List<AppraisalCodelet> appraisalCodelets) {
        this.appraisalCodelets = appraisalCodelets;
    }

    /**
     * @return the goalCodelets
     */
    public List<GoalCodelet> getGoalCodelets() {
        return goalCodelets;
    }

    /**
     * @param goalCodelets the goalCodelets to set
     */
    public void setGoalCodelets(List<GoalCodelet> goalCodelets) {
        this.goalCodelets = goalCodelets;
    }


}
