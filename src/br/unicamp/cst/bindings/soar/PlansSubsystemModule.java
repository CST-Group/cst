/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.bindings.soar;

/**
 *
 * @author du
 */
public class PlansSubsystemModule {

    // Codelets in System 2;
    private JSoarCodelet jSoarCodelet;
    private PlanSelectionCodelet planSelectionCodelet;

    public PlansSubsystemModule(){

    }

    public boolean verifyExistCodelets() {
        if (jSoarCodelet != null)
            return true;
        else
            return false;
    }

    public PlansSubsystemModule(JSoarCodelet jSoarCodelet){
        this.setjSoarCodelet(jSoarCodelet);
    }

    public JSoarCodelet getjSoarCodelet() {
        return jSoarCodelet;
    }

    public void setjSoarCodelet(JSoarCodelet jSoarCodelet) {
        this.jSoarCodelet = jSoarCodelet;
    }

    public PlanSelectionCodelet getPlanSelectionCodelet() {
        return planSelectionCodelet;
    }

    public void setPlanSelectionCodelet(PlanSelectionCodelet planSelectionCodelet) {
        this.planSelectionCodelet = planSelectionCodelet;
    }
}
