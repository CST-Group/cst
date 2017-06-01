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

public abstract class AppraisalCodelet extends Codelet {

    private String id;
    private AbstractObject inputAbstractObject;
    private Appraisal appraisal;
    private Memory inputAbstractObjectMO;
    private Memory outputAppraisalMO;

    public AppraisalCodelet(String id) {
        setId(id);
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
        setAppraisal(appraisalGeneration(getInputAbstractObject()));
        getAppraisal().setId(getId());
        getOutputAppraisalMO().setI(getAppraisal());
    }


    public abstract Appraisal appraisalGeneration(AbstractObject inputAbstractObject);


    public Memory getOutputAppraisalMO() {
        return outputAppraisalMO;
    }

    public void setOutputAppraisalMO(Memory outputAppraisalMO) {
        this.outputAppraisalMO = outputAppraisalMO;
    }

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
}

