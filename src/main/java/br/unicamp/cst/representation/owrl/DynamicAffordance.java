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
package br.unicamp.cst.representation.owrl;

import br.unicamp.cst.core.entities.CSTMessages;
import br.unicamp.cst.util.CodeBuilder;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Suelen Mapa and Eduardo Froes.
 */
public class DynamicAffordance extends Affordance {

    private String applyCode;
    private String applyClassName;
    private String detectorCode;
    private String detectorClassName;

    private Object applyObject;
    private Object detectorObject;


    public DynamicAffordance(String name) {
        super(name, new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    public DynamicAffordance(String name, HashMap<String, AbstractObject> aggregateObjects,  HashMap<String, AbstractObject> compositeObjects, HashMap<String, Property> modifiedProperties) {
        super(name, aggregateObjects, compositeObjects, modifiedProperties);
    }

    public DynamicAffordance(String name, String applyClassName, String applyCode, String detectorClassName, String detectorCode, HashMap<String, AbstractObject> aggregateObjects,  HashMap<String, AbstractObject> compositeObjects, HashMap<String, Property> modifiedProperties) {

        super(name, aggregateObjects, compositeObjects, modifiedProperties);

        setApplyCode(applyCode);
        setApplyClassName(applyClassName);
        setDetectorCode(detectorCode);
        setDetectorClassName(detectorClassName);

        setDetectorObject(CodeBuilder.generateNewInstance(CodeBuilder.compile(getDetectorClassName(), getDetectorCode())));
        setApplyObject(CodeBuilder.generateNewInstance(CodeBuilder.compile(getApplyClassName(), getApplyCode())));
    }

    public Object detector(String methodName, Object... args) {
        try {
            if (getDetectorObject() == null) {
                throw new Exception(CSTMessages.MSG_VAR_DETECTOR_OBJECT);
            } else {
                return CodeBuilder.executeMethod(getDetectorObject(), methodName, args);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Object apply(String methodName, Object... args) {
        try {
            if (getDetectorObject() == null) {
                throw new Exception(CSTMessages.MSG_VAR_APPLY_OBJECT);
            } else {
                return CodeBuilder.executeMethod(getApplyObject(), methodName, args);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    protected String getApplyCode() {
        return applyCode;
    }

    protected void setApplyCode(String applyCode) {
        this.applyCode = applyCode;
    }

    public String getApplyClassName() {
        return applyClassName;
    }

    public void setApplyClassName(String applyClassName) {
        this.applyClassName = applyClassName;
    }

    protected String getDetectorCode() {
        return detectorCode;
    }

    protected void setDetectorCode(String detectorCode) {
        this.detectorCode = detectorCode;
    }

    public String getDetectorClassName() {
        return detectorClassName;
    }

    public void setDetectorClassName(String detectorClassName) {
        this.detectorClassName = detectorClassName;
    }

    protected Object getApplyObject() {
        return applyObject;
    }

    protected void setApplyObject(Object applyObject) {
        this.applyObject = applyObject;
    }

    protected Object getDetectorObject() {
        return detectorObject;
    }

    protected void setDetectorObject(Object detectorObject) {
        this.detectorObject = detectorObject;
    }

    public static void main(String[] args) {

        String detectorClassName = "br.com.reflection.test.Detector";
        String detectorCode = "package br.com.reflection.test;\n" +
                "public class Detector {" + "\n" +
                "    public boolean compare(Integer i0, Integer i1) {" + "\n" +
                "        if(i0 == i1)" + "\n" +
                "           return true;"+ "\n" +
                "        else"+ "\n" +
                "           return false;"+ "\n" +
                "    }" + "\n" +
                "}" + "\n";


        String applyClassName = "br.com.reflection.test.Apply";
        String applyCode = "package br.com.reflection.test;\n" +
                "import java.util.List;" + "\n" +
                "public class Apply {" + "\n" +
                "    public void execute(List<Integer> list) {" + "\n" +
                "        list.stream().forEach( i -> System.out.println(i));" + "\n" +
                "    }" + "\n" +
                "}" + "\n";

        Date dateTimeInit = new Date();
        DynamicAffordance affordance = new DynamicAffordance("Test", applyClassName, applyCode, detectorClassName, detectorCode, new HashMap<>(), new HashMap<>(), new HashMap<>());

        Date dateTimeFinish = new Date();

        System.out.println("Compile Time:" + (dateTimeFinish.getTime() - dateTimeInit.getTime()));

        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9,10);

        affordance.apply("execute", list);
        System.out.println(affordance.detector("compare", 1,1));

    }
}
