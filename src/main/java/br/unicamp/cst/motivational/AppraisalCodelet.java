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
package br.unicamp.cst.motivational;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.cst.representation.wme.Idea;

import java.util.*;

public abstract class AppraisalCodelet extends Codelet {


    public static final String INPUT_IDEA_MEMORY = "INPUT_IDEA_MEMORY";
    public static final String OUTPUT_IDEA_MEMORY = "OUTPUT_IDEA_MEMORY";
    public static final String OUTPUT_APPRAISAL_MEMORY = "OUTPUT_APPRAISAL_MEMORY";


    private String id;
    private Idea inputAbstractObject;
    private Idea outputAbstractObject;
    private Appraisal appraisal;
    private Memory inputAbstractObjectMO;
    private Memory outputAbstractObjectMO;
    private Memory outputAppraisalMO;

    public AppraisalCodelet(String id) {
        setId(id);
        setName(id);
        setAppraisal(new Appraisal(id, "", 1d));
    }

    @Override
    public void accessMemoryObjects() {

        if (getInputIdeaMO() == null) {
            setInputIdeaMO(this.getInput(INPUT_IDEA_MEMORY, 0));
            setInputIdea((Idea) getInputIdeaMO().getI());
        }

        if (getOutputIdeaMO() == null) {
            setOutputIdeaMO(this.getOutput(OUTPUT_IDEA_MEMORY, 0));
            setOutputIdea(getInputIdea());
        }

        if (getOutputAppraisalMO() == null) {
            setOutputAppraisalMO(this.getOutput(OUTPUT_APPRAISAL_MEMORY, 0));
            getOutputAppraisalMO().setI(getAppraisal());
        }

    }

    @Override
    public synchronized void calculateActivation() {
        setInputIdea((Idea) getInputIdeaMO().getI());
        setAppraisal(appraisalGeneration(getInputIdea().clone()));
        getAppraisal().setName(getId());

        Optional<Idea> first = getInputIdea().getL().stream().filter(property -> property.getName().equals(getId())).findFirst();

        if (first.isPresent()) {

            Idea property = first.get();

            Idea evaluation = property.getL().stream().filter(quality -> quality.getName().equals("evaluation")).findFirst().get();
            evaluation.setValue(getAppraisal().getEvaluation());

            Idea currentState = property.getL().stream().filter(quality -> quality.getName().equals("currentStateEvaluation")).findFirst().get();
            currentState.setValue(getAppraisal().getCurrentStateEvaluation());

        } else {

            List<Idea> appraisalQ = new ArrayList<>();

            appraisalQ.add(new Idea("evaluation", getAppraisal().getEvaluation(),1));
            appraisalQ.add(new Idea("currentStateEvaluation", getAppraisal().getCurrentStateEvaluation(),1));

            Idea appraisalProperty = new Idea(getId(), appraisalQ);

            getInputIdea().add(appraisalProperty);
        }

        setOutputIdea(getInputIdea());

        try {
            setActivation(getAppraisal().getEvaluation());
        } catch (CodeletActivationBoundsException e) {
            e.printStackTrace();
        }
    }


    @Override
    public synchronized void proc(){

        getOutputAppraisalMO().setI(getAppraisal());
        getOutputIdeaMO().setI(getOutputIdea());

    }



    public abstract Appraisal appraisalGeneration(Idea inputIdea);

    public Idea getInputIdea() {
        return inputAbstractObject;
    }

    public void setInputIdea(Idea inputIdea) {
        this.inputAbstractObject = inputIdea;
    }

    public Appraisal getAppraisal() {
        return appraisal;
    }

    public void setAppraisal(Appraisal appraisal) {
        this.appraisal = appraisal;
    }

    public Memory getInputIdeaMO() {
        return inputAbstractObjectMO;
    }

    public void setInputIdeaMO(Memory inputIdeaMO) {
        this.inputAbstractObjectMO = inputIdeaMO;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Memory getOutputIdeaMO() {
        return outputAbstractObjectMO;
    }

    public void setOutputIdeaMO(Memory outputIdeaMO) {
        this.outputAbstractObjectMO = outputIdeaMO;
    }

    public Idea getOutputIdea() {
        return outputAbstractObject;
    }

    public void setOutputIdea(Idea outputIdea) {
        this.outputAbstractObject = outputIdea;
    }

    public Memory getOutputAppraisalMO() {
        return outputAppraisalMO;
    }

    public void setOutputAppraisalMO(Memory outputAppraisalMO) {
        this.outputAppraisalMO = outputAppraisalMO;
    }
}

