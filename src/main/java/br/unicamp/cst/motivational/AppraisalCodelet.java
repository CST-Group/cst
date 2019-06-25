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
import br.unicamp.cst.representation.owrl.AbstractObject;
import br.unicamp.cst.representation.owrl.Property;
import br.unicamp.cst.representation.owrl.QualityDimension;

import java.util.*;

public abstract class AppraisalCodelet extends Codelet {


    public static final String INPUT_ABSTRACT_OBJECT_MEMORY = "INPUT_ABSTRACT_OBJECT_MEMORY";
    public static final String OUTPUT_ABSTRACT_OBJECT_MEMORY = "OUTPUT_ABSTRACT_OBJECT_MEMORY";
    public static final String OUTPUT_APPRAISAL_MEMORY = "OUTPUT_APPRAISAL_MEMORY";


    private String id;
    private AbstractObject inputAbstractObject;
    private AbstractObject outputAbstractObject;
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

        if (getInputAbstractObjectMO() == null) {
            setInputAbstractObjectMO(this.getInput(INPUT_ABSTRACT_OBJECT_MEMORY, 0));
            setInputAbstractObject((AbstractObject) getInputAbstractObjectMO().getI());
        }

        if (getOutputAbstractObjectMO() == null) {
            setOutputAbstractObjectMO(this.getOutput(OUTPUT_ABSTRACT_OBJECT_MEMORY, 0));
            setOutputAbstractObject(getInputAbstractObject());
        }

        if (getOutputAppraisalMO() == null) {
            setOutputAppraisalMO(this.getOutput(OUTPUT_APPRAISAL_MEMORY, 0));
            getOutputAppraisalMO().setI(getAppraisal());
        }

    }

    @Override
    public synchronized void calculateActivation() {
        setInputAbstractObject((AbstractObject) getInputAbstractObjectMO().getI());
        setAppraisal(appraisalGeneration(getInputAbstractObject().clone()));
        getAppraisal().setName(getId());

        Optional<Property> first = getInputAbstractObject().getProperties().stream().filter(property -> property.getName().equals(getId())).findFirst();

        if (first.isPresent()) {

            Property property = first.get();

            QualityDimension evaluation = property.getQualityDimensions().stream().filter(quality -> quality.getName().equals("evaluation")).findFirst().get();
            evaluation.setValue(getAppraisal().getEvaluation());

            QualityDimension currentState = property.getQualityDimensions().stream().filter(quality -> quality.getName().equals("currentStateEvaluation")).findFirst().get();
            currentState.setValue(getAppraisal().getCurrentStateEvaluation());

        } else {

            List<QualityDimension> appraisalQ = new ArrayList<>();

            appraisalQ.add(new QualityDimension("evaluation", getAppraisal().getEvaluation()));
            appraisalQ.add(new QualityDimension("currentStateEvaluation", getAppraisal().getCurrentStateEvaluation()));

            Property appraisalProperty = new Property(getId(), appraisalQ);

            getInputAbstractObject().addProperty(appraisalProperty);
        }

        setOutputAbstractObject(getInputAbstractObject());

        try {
            setActivation(getAppraisal().getEvaluation());
        } catch (CodeletActivationBoundsException e) {
            e.printStackTrace();
        }
    }


    @Override
    public synchronized void proc(){

        getOutputAppraisalMO().setI(getAppraisal());
        getOutputAbstractObjectMO().setI(getOutputAbstractObject());

    }



    public abstract Appraisal appraisalGeneration(AbstractObject inputAbstractObject);

    public AbstractObject getInputAbstractObject() {
        return inputAbstractObject;
    }

    public void setInputAbstractObject(AbstractObject inputAbstractObject) {
        this.inputAbstractObject = inputAbstractObject;
    }

    public Appraisal getAppraisal() {
        return appraisal;
    }

    public void setAppraisal(Appraisal appraisal) {
        this.appraisal = appraisal;
    }

    public Memory getInputAbstractObjectMO() {
        return inputAbstractObjectMO;
    }

    public void setInputAbstractObjectMO(Memory inputAbstractObjectMO) {
        this.inputAbstractObjectMO = inputAbstractObjectMO;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Memory getOutputAbstractObjectMO() {
        return outputAbstractObjectMO;
    }

    public void setOutputAbstractObjectMO(Memory outputAbstractObjectMO) {
        this.outputAbstractObjectMO = outputAbstractObjectMO;
    }

    public AbstractObject getOutputAbstractObject() {
        return outputAbstractObject;
    }

    public void setOutputAbstractObject(AbstractObject outputAbstractObject) {
        this.outputAbstractObject = outputAbstractObject;
    }

    public Memory getOutputAppraisalMO() {
        return outputAppraisalMO;
    }

    public void setOutputAppraisalMO(Memory outputAppraisalMO) {
        this.outputAppraisalMO = outputAppraisalMO;
    }
}

