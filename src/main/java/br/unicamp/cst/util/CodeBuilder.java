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
package br.unicamp.cst.util;

import net.openhft.compiler.CompilerUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by du on 11/05/17.
 */
public class CodeBuilder {

    private final String ENTER = "\n";
    private final String SPACE = " ";

    private String fullClassName;
    private String className;
    private String packagePath = "package br.unicamp.cst.util";
    private String code = getPackagePath() + ";" + ENTER +
            "@IMPORTS" + ENTER +
            "public class @CLASS @INHERITANCE @INHERITANCE_ENTITY {" + ENTER +
            "@ATTRIBUTES" + ENTER +
            "@METHODS" + ENTER +
            "}";

    private List<String> imports;
    private List<String> methods;
    private List<String> attributes;



    public CodeBuilder(String className) {
        this.setClassName(className);
        this.setFullClassName("br.unicamp.cst.util." + className);
        this.setCode(this.getCode().replace("@CLASS", className));
        this.setCode(this.getCode().replace("@INHERITANCE_ENTITY", ""));
        this.setCode(this.getCode().replace("@INHERITANCE", ""));

        this.setMethods(new ArrayList<String>());
        this.setAttributes(new ArrayList<String>());
        this.setImports(new ArrayList<String>());
    }

    public CodeBuilder(String className, String inheritance, String inheriranceEntity) {
        this.setClassName(className);
        this.setFullClassName(getPackagePath() + "." + className);
        this.setCode(this.getCode().replace("@CLASS", className));

        if(inheritance != null)
            this.setCode(this.getCode().replace("@INHERITANCE", inheritance));
        else
            this.setCode(this.getCode().replace("@INHERITANCE", ""));

        if(inheriranceEntity != null)
            this.setCode(this.getCode().replace("@INHERITANCE_ENTITY", inheriranceEntity));
        else
            this.setCode(this.getCode().replace("@INHERITANCE_ENTITY", ""));

        this.setMethods(new ArrayList<String>());
        this.setAttributes(new ArrayList<String>());
        this.setImports(new ArrayList<String>());

    }

    public void addImports(String importStatement){
        getImports().add("import" + SPACE + importStatement + ";");
    }

    public void addAttributes(String acessibility, String attributeType, String attributeName){
        String attribute = acessibility + SPACE + attributeType + SPACE + attributeName + ";";
        getAttributes().add(attribute);
    }

    public void addAttributes(String acessibility, String attributeType, String attributeName, String value){
        String attribute = acessibility + SPACE + attributeType + SPACE + attributeName + SPACE + "=" + SPACE + value +  ";";
        getAttributes().add(attribute);
    }

    public void addMethod(String acessibility, String returnType, String methodName, List<String> parameterList, String methodCode, boolean isOverride) {

        String method = acessibility + SPACE + returnType + SPACE + methodName + "(@PARAMETERS){" + ENTER + "@CODE" + ENTER + "}";

        method = isOverride ? "@Override" + ENTER + method : method;

        final String parameters = String.join(", ", parameterList);

        String finalMethodCode = "";

        finalMethodCode = method.replace("@PARAMETERS", parameters);
        finalMethodCode = finalMethodCode.replace("@CODE", methodCode);

        this.getMethods().add(finalMethodCode);
    }

    public String buildClassCode() {

        String imports = String.join(ENTER, this.getImports());

        String methods = String.join(ENTER, this.getMethods());

        String attributes = String.join(ENTER, this.getAttributes());

        String finalCode = this.getCode();

        if(!getImports().isEmpty())
            finalCode = finalCode.replace("@IMPORTS", imports);
        else
            finalCode = finalCode.replace("@IMPORTS", "");

        finalCode = finalCode.replace("@METHODS", methods);

        finalCode = finalCode.replace("@ATTRIBUTES", attributes);

        return finalCode;
    }

    public static Object executeMethod(Object object, String methodName, Object... args) {
        Method method = Arrays.stream(object.getClass().getMethods()).filter(m -> m.getName().contains(methodName)).findFirst().get();
        Object returnObject = null;

        try {
            returnObject = method.invoke(object, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return returnObject;
    }

    public static Class compile(String className, String code) {
        Class aClass = null;

        try {
            aClass = CompilerUtils.CACHED_COMPILER.loadFromJava(className, code);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return aClass;
    }

    public static Object generateNewInstance(Class aClass){
        Object object = null;

        try {
            object = aClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return object;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    public void setFullClassName(String fullClassName) {
        this.fullClassName = fullClassName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }
}

