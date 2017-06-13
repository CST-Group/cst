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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AppraisalCodelet extends Codelet {

    private String id;
    private AbstractObject inputAbstractObject;
    private AbstractObject outputAbstractObject;
    private Appraisal appraisal;
    private Memory inputAbstractObjectMO;
    private Memory outputAbstractObjectMO;

    public AppraisalCodelet(String id) {
        setId(id);
        setAppraisal(new Appraisal(id, "", 1d));
    }

    @Override
    public void calculateActivation() {
        try {
            setActivation(0);
        } catch (CodeletActivationBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void proc() {

        setInputAbstractObject((AbstractObject) getInputAbstractObjectMO().getI());
        setAppraisal(appraisalGeneration(getInputAbstractObject().clone()));
        getAppraisal().setId(getId());

        Optional<Property> first = getInputAbstractObject().getProperties().stream().filter(property -> property.getName().equals(getId())).findFirst();

        if (first.isPresent()) {

            Property property = first.get();

            QualityDimension evaluation = property.getQualityDimensions().stream().filter(quality -> quality.getName().equals("evaluation")).findFirst().get();
            evaluation.setValue(getAppraisal().getEvaluation());

            QualityDimension currentState = property.getQualityDimensions().stream().filter(quality -> quality.getName().equals("currentState")).findFirst().get();
            currentState.setValue(getAppraisal().getCurrentState());

        } else {

            List<QualityDimension> appraisalQ = new ArrayList<>();

            appraisalQ.add(new QualityDimension("evaluation", getAppraisal().getEvaluation()));
            appraisalQ.add(new QualityDimension("currentState", getAppraisal().getCurrentState()));

            Property appraisalProperty = new Property(getId(), appraisalQ);

            getInputAbstractObject().addProperty(appraisalProperty);
        }

        getOutputAbstractObjectMO().setI(getAppraisal());
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
}

