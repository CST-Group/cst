/**
 * ****************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * S. M. de Paula and R. R. Gudwin
 * ****************************************************************************
 */
package br.unicamp.cst.representation.owrl;

import br.unicamp.cst.util.AbstractObjectEditor;
import br.unicamp.cst.util.CodeBuilder;
import br.unicamp.cst.util.NameGenerator;
import br.unicamp.cst.util.Pair;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Suelen Mapa and Eduardo Froes.
 */
public class AbstractObject implements Cloneable, Entity {

    private List<Property> properties;//1-n;
    private List<AbstractObject> compositeList;//1-n;
    private List<AbstractObject> aggregateList;//1-n;
    private List<Affordance> affordances;
    private String name;


    public AbstractObject(String name) {
        setName(name);
        setProperties(new ArrayList<Property>());
        setCompositeList(new ArrayList<AbstractObject>());
        setAggregateList(new ArrayList<AbstractObject>());
        setAffordances(new ArrayList<>());
    }


    public AbstractObject(String name, List<Property> props) {
        setName(name);
        setProperties(props);
        setCompositeList(new ArrayList<AbstractObject>());
        setAggregateList(new ArrayList<AbstractObject>());
        setAffordances(new ArrayList<>());
    }


    public AbstractObject(String name, List<Property> props, List<AbstractObject> composite, List<AbstractObject> aggregate) {
        setName(name);
        setCompositeParts(composite);
        setAggregatePart(aggregate);
        setProperties(props);
        setAffordances(new ArrayList<>());
    }

    public AbstractObject(String name, List<Property> props, List<AbstractObject> composite) {
        setName(name);
        setCompositeParts(composite);
        setProperties(props);
        setAffordances(new ArrayList<>());
    }


    public AbstractObject(List<AbstractObject> aggregate, List<Property> props, String name) {
        setName(name);
        setAggregatePart(aggregate);
        setProperties(props);
        setAffordances(new ArrayList<>());
    }
    
    public AbstractObject(File file) {
        String line;
        //AbstractObject newAO = null;
        List<Entity> parseAOs = new ArrayList<Entity>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                    String linesplitted[] = line.split(" ");
                    int level = getLevel(linesplitted);
                    mode m = getMode(linesplitted);
                    String name = getName(linesplitted);
                    String value = getValue(linesplitted);
                    AbstractObject ao;
                    AbstractObject father;
                    Property pfather;
                    if (level == 0) {
                        setName(name);
                        setProperties(new ArrayList<Property>());
                        setCompositeList(new ArrayList<AbstractObject>());
                        setAggregateList(new ArrayList<AbstractObject>());
                        setAffordances(new ArrayList<>());
                        parseAOs.add(this);
                    }
                    else {
                          switch(m) {
                                case COMPOSITE:ao = new AbstractObject(name);
                                               parseAOs.add(ao);
                                               father = (AbstractObject) parseAOs.get(level-1);
                                               father.addCompositePart(ao);
                                               if (level >= parseAOs.size()) parseAOs.add(ao);
                                               else parseAOs.set(level, ao);
                                               break;
                                case AGGREGATE:ao = new AbstractObject(name);
                                               parseAOs.add(ao);
                                               father = (AbstractObject) parseAOs.get(level-1);
                                               father.addAggregatePart(ao);
                                               if (level >= parseAOs.size()) parseAOs.add(ao);
                                               else parseAOs.set(level, ao);
                                               break;
                                case PROPERTY:Property p = new Property(name);
                                              father = (AbstractObject) parseAOs.get(level-1);
                                              father.addProperty(p);
                                              if (level >= parseAOs.size()) parseAOs.add(p);
                                              else parseAOs.set(level, p);
                                              break;
                                case QUALITY_DIMENSION:QualityDimension qd = new QualityDimension(name,value);
                                                       pfather = (Property) parseAOs.get(level-1);
                                                       pfather.addQualityDimension(qd);
                                                       break;
                          }                            
                    }
                    //System.out.println(line+" -> level: "+level+" mode: "+m+" name: "+name+" value: "+value);
                }
            reader.close();
        } catch (Exception e) {e.printStackTrace();}
    }
    
    public enum mode {NULL, COMPOSITE, AGGREGATE, PROPERTY, QUALITY_DIMENSION}
    
    private int getLevel(String splitted[]) {
        int level = 0;
        for (int i=0;i<splitted.length;i++) {
            if (splitted[i].equals("")) level++;
        }
        return(level/3);
    }
    
    private mode getMode(String splitted[]) {
        mode m = mode.NULL;
        for (int i=0;i<splitted.length;i++) {
            if (splitted[i].equals("*")) {
                m = mode.COMPOSITE;
                break;
            }
            if (splitted[i].equals("+")) {
                m = mode.AGGREGATE;
                break;
            }
            if (splitted[i].equals(">")) {
                m = mode.PROPERTY;
                break;
            }
            if (splitted[i].equals("-")) {
                m = mode.QUALITY_DIMENSION;
                break;
            }
        }
        return(m);
    }
    
    private String getName(String splitted[]) {
        int level = getLevel(splitted);
        if (level == 0) return(splitted[0]);
        else return(splitted[3*level+1]);
    }
    
    private String getValue(String splitted[]) {
        String value = "";
        for (int i=0;i<splitted.length;i++) {
            if (splitted[i].equals(":")) {
                value = splitted[i+1];
                break;
            }
        }    
        return(value);    
    }

    public List<Object> search(String path) {
        path = path.trim();
        int dot = path.indexOf(".");
        String name = path;
        String subPath = null;
        if (dot > -1) {
            name = path.substring(0, dot);
            subPath = path.substring(dot + 1);
        }
        List<Object> results = new ArrayList<>();
        if (getName().equals(name)) {
            if (subPath != null) {
                results.addAll(search(subPath));
            } else {
                results.add(this);
            }
        }
        for (AbstractObject composite : getCompositeList()) {
            if (composite.getName().equals(name)) {
                if (subPath != null) {
                    results.addAll(composite.search(subPath));
                } else {
                    results.add(composite);
                }
            }
            results.addAll(composite.search(path));
        }
        for (AbstractObject aggregate : getAggregateList()) {
            if (aggregate.getName().equals(name)) {
                if (subPath != null) {
                    results.addAll(aggregate.search(subPath));
                } else {
                    results.add(aggregate);
                }
            }
            results.addAll(aggregate.search(path));
        }
        for (Property property : getProperties()) {
            if (property.getName().equals(name)) {
                if (subPath != null) {
                    results.addAll(property.search(subPath));
                } else {
                    results.add(property);
                }
            }
            results.addAll(property.search(path));
        }
        for (Affordance affordance : affordances) {
            if (affordance.getName().equals(name)) {
                results.add(affordance);
            }
        }
        return results;
    }


    public void delete(String path) {
        String parentPath = null;
        String name = path;
        int dot = path.lastIndexOf(".");
        if (dot > -1) {
            parentPath = path.substring(0, dot);
            name = path.substring(dot + 1);
        }
        Object parent = this;
        List<Object> children = new ArrayList<>();
        if (parentPath != null) {
            List<Object> parents = search(parentPath);
            int p = parents.size();
            do {
                p--;
                parent = parents.get(p);
                if (parent instanceof AbstractObject) {
                    children = ((AbstractObject) parent).search(name);
                } else {
                    children = ((Property) parent).search(name);
                }
            } while (p > 0 && children.isEmpty());
        } else {
            children = search(name);
        }
        if (!children.isEmpty()) {
            Object child = children.get(children.size() - 1);
            if (parent instanceof AbstractObject) {
                AbstractObject ao = (AbstractObject) parent;
                if (child instanceof AbstractObject) {
                    ao.removeAggregatePart((AbstractObject) child);
                    ao.removeCompositePart((AbstractObject) child);
                } else {
                    ao.removeProperty((Property) child);
                }
            } else {
                ((Property) parent).deleteChild(child);
            }
        }
    }

    public boolean detectAffordance(HashMap<String, AbstractObject> aggregateObjects, HashMap<String, AbstractObject> compositeObjects, HashMap<String, Property> modifiedProperties) {

        final boolean[] bVerify = {true};

        if (getAffordances().size() != 0) {
            getAffordances().stream().forEach(affordance -> {
                aggregateObjects.entrySet().stream().forEach(aggrDetect -> {
                    Optional<Map.Entry<String, AbstractObject>> first = affordance.getAggregateDetectorObjects().entrySet().stream().filter(aggr -> aggr.getKey().equals(aggrDetect.getKey())
                            && aggr.getValue().getName().equals(aggrDetect.getValue().getName())).findFirst();
                    if (!first.isPresent()) {
                        bVerify[0] = false;
                        return;
                    }
                });

                compositeObjects.entrySet().stream().forEach(compDetect -> {
                    Optional<Map.Entry<String, AbstractObject>> first = affordance.getCompositeDetectorObjects().entrySet().stream().filter(comp -> comp.getKey().equals(compDetect.getKey())
                            && comp.getValue().getName().equals(compDetect.getValue().getName())).findFirst();
                    if (!first.isPresent()) {
                        bVerify[0] = false;
                        return;
                    }
                });

                modifiedProperties.entrySet().stream().forEach(propDetect -> {
                    Optional<Map.Entry<String, Property>> first = affordance.getPropertyDetectorObjects().entrySet().stream().filter(prop -> prop.getKey().equals(propDetect.getKey())
                            && prop.getValue().getName().equals(propDetect.getValue().getName())).findFirst();
                    if (!first.isPresent()) {
                        bVerify[0] = false;
                        return;
                    }
                });
            });
        }

        if (bVerify[0]) {
            return false;
        } else {
            return true;
        }
    }


    public void discoveryAffordance(AbstractObject after, List<AbstractObject> path) {

        HashMap<String, AbstractObject> aggregateObjects = verifyAbstractObjectsStatus(after.getAggregateParts(), getAggregateParts());
        HashMap<String, AbstractObject> compositeObjects = verifyAbstractObjectsStatus(after.getCompositeParts(), getCompositeParts());
        HashMap<String, Property> modifiedProperties = verifyPropertiesStatus(path);

        if (!detectAffordance(aggregateObjects, compositeObjects, modifiedProperties)) {
            Pair<String, String> applyCode = createApplyCode(aggregateObjects, compositeObjects, modifiedProperties);
            Pair<String, String> detectorCode = createDetectorCode(aggregateObjects, compositeObjects, modifiedProperties);

            if (applyCode != null && detectorCode != null) {
                DynamicAffordance newAffordance = new DynamicAffordance((new NameGenerator()).generateWord(),
                        applyCode.getFirst(),
                        applyCode.getSecond(),
                        detectorCode.getFirst(),
                        detectorCode.getSecond(),
                        aggregateObjects,
                        compositeObjects,
                        modifiedProperties);
                this.addAffordance(newAffordance);

                getAggregateParts().forEach( aggr -> {

                    Optional<AbstractObject> gotAggr = after.getAggregateParts().stream().filter(aAggr -> aAggr.getName().equals(aggr.getName())).findFirst();

                    if(gotAggr.isPresent()){
                        List<AbstractObject> pathAggr = new ArrayList<AbstractObject>();
                        path.stream().forEach(p -> pathAggr.addAll(p.getAggregateParts().stream().filter(pAggr -> pAggr.getName().equals(aggr.getName())).collect(Collectors.toList())));

                        aggr.discoveryAffordance(gotAggr.get(), pathAggr);
                    }
                });

                getCompositeParts().forEach( comp -> {

                    Optional<AbstractObject> gotComp = after.getCompositeParts().stream().filter(aAggr -> aAggr.getName().equals(comp.getName())).findFirst();

                    if(gotComp.isPresent()){
                        List<AbstractObject> pathComp = new ArrayList<AbstractObject>();
                        path.stream().forEach(p -> pathComp.addAll(p.getCompositeParts().stream().filter(pAggr -> pAggr.getName().equals(comp.getName())).collect(Collectors.toList())));

                        comp.discoveryAffordance(gotComp.get(), pathComp);
                    }
                });

            }
        }
    }


    private Pair<String, String> createDetectorCode(HashMap<String, AbstractObject> aggregateObjects, HashMap<String, AbstractObject> compositeObjects, HashMap<String, Property> properties) {

        List<String> methodCodeIfStatement = new ArrayList<>();
        List<String> methodParameterDetector = new ArrayList<>();

        String className = "Detector" + this.getName().replace(" ", "");
        String abstractObjectParameter = "AbstractObject " + this.getName().replace(" ", "").toLowerCase();

        methodParameterDetector.add(abstractObjectParameter);

        CodeBuilder codeBuilderDetector = new CodeBuilder(className);

        codeBuilderDetector.addImports("java.util.List");
        codeBuilderDetector.addImports("br.unicamp.cst.representation.owrl.AbstractObject");
        codeBuilderDetector.addImports("br.unicamp.cst.representation.owrl.Property");

        String methodCode = "if(@CONDITION){" +
                "return true;" +
                "}" +
                "else{" +
                "return false;" +
                "}";


        if (aggregateObjects != null) {
            aggregateObjects.entrySet().stream().forEach(aggregateAO -> {
                String aggregateParameter = "";

                if (aggregateAO.getKey() == "added") {
                    aggregateParameter = "AbstractObject added" + aggregateAO.getValue().getName().replace(" ", "") + "AO";
                    methodCodeIfStatement.add(this.getName().replace(" ", "").toLowerCase() + ".getAggregateParts().stream().filter(oa -> oa.getName().equals(added"
                            + aggregateAO.getValue().getName().replace(" ", "") + "AO.getName())).findFirst().isPresent()");
                } else {
                    aggregateParameter = "AbstractObject removed" + aggregateAO.getValue().getName().replace(" ", "") + "AO";
                    methodCodeIfStatement.add("!" + this.getName().replace(" ", "").toLowerCase() + ".getAggregateParts().stream().filter(oa -> oa.getName().equals(removed"
                            + aggregateAO.getValue().getName().replace(" ", "") + "AO.getName())).findFirst().isPresent()");
                }

                methodParameterDetector.add(aggregateParameter);

            });
        }

        if (compositeObjects != null) {
            compositeObjects.entrySet().stream().forEach(compositeAO -> {
                String compositeParameter = "";

                if (compositeAO.getKey() == "added") {
                    compositeParameter = "AbstractObject added" + compositeAO.getValue().getName().replace(" ", "") + "CO";
                    methodCodeIfStatement.add(this.getName().replace(" ", "").toLowerCase() + ".getCompositeParts().stream().filter(oa -> oa.getName().equals(added"
                            + compositeAO.getValue().getName().replace(" ", "") + "CO.getName())).findFirst().isPresent()");
                } else {
                    compositeParameter = "AbstractObject removed" + compositeAO.getValue().getName().replace(" ", "") + "CO";
                    methodCodeIfStatement.add("!" + this.getName().replace(" ", "").toLowerCase() + ".getCompositeParts().stream().filter(oa -> oa.getName().equals(removed"
                            + compositeAO.getValue().getName().replace(" ", "") + "CO.getName())).findFirst().isPresent()");
                }

                methodParameterDetector.add(compositeParameter);
            });

        }

        if (properties != null) {
            properties.entrySet().stream().forEach(property -> {
                String propertyParameter = "";

                if (property.getKey() == "added") {
                    propertyParameter = "Property added" + property.getValue().getName().replace(" ", "");
                    methodCodeIfStatement.add(this.getName().replace(" ", "").toLowerCase() + ".getProperties().stream().filter(oa -> oa.getName().equals(added"
                            + property.getValue().getName().replace(" ", "") + ".getName())).findFirst().isPresent()");
                } else {
                    if (property.getKey() == "removed") {
                        propertyParameter = "Property removed" + property.getValue().getName().replace(" ", "");
                        methodCodeIfStatement.add("!" + this.getName().replace(" ", "").toLowerCase() + ".getProperties().stream().filter(oa -> oa.getName().equals(removed"
                                + property.getValue().getName().replace(" ", "") + ".getName())).findFirst().isPresent()");
                    } else {
                        propertyParameter = "Property mod" + property.getValue().getName().replace(" ", "");
                        methodCodeIfStatement.add(this.getName().replace(" ", "").toLowerCase() + ".getProperties().stream().filter(oa -> oa.getName().equals(mod"
                                + property.getValue().getName().replace(" ", "") + ".getName())).findFirst().get().equals(mod" + property.getValue().getName().replace(" ", "") + ")");
                    }
                }

                methodParameterDetector.add(propertyParameter);
            });
        }

        if (methodParameterDetector.size() != 0 && methodCodeIfStatement.size() != 0) {
            codeBuilderDetector.addMethod("public",
                    "boolean",
                    "detect",
                    methodParameterDetector,
                    methodCode.replace("@CONDITION", String.join(" &&\n", methodCodeIfStatement)),
                    false);

            return new Pair<String, String>(codeBuilderDetector.getFullClassName(), codeBuilderDetector.buildClassCode());
        } else {
            return null;
        }
    }

    private Pair<String, String> createApplyCode(HashMap<String, AbstractObject> aggregateObjects, HashMap<String, AbstractObject> compositeObjects, HashMap<String, Property> properties) {

        List<String> methodCodeApply = new ArrayList<>();
        List<String> methodParameterApply = new ArrayList<>();

        String className = "Apply" + this.getName().replace(" ", "");
        String abstractObjectParameter = "AbstractObject " + this.getName().replace(" ", "").toLowerCase();

        methodParameterApply.add(abstractObjectParameter);

        CodeBuilder codeBuilderApply = new CodeBuilder(className);

        codeBuilderApply.addImports("java.util.List");
        codeBuilderApply.addImports("br.unicamp.cst.representation.owrl.AbstractObject");
        codeBuilderApply.addImports("br.unicamp.cst.representation.owrl.Property");

        if (aggregateObjects != null) {
            aggregateObjects.entrySet().stream().forEach(aggregateAO -> {
                String aggregateParameter = "";

                if (aggregateAO.getKey() == "added") {
                    aggregateParameter = "AbstractObject add" + aggregateAO.getValue().getName().replace(" ", "") + "AO";
                    methodCodeApply.add(this.getName().replace(" ", "").toLowerCase() + ".getAggregateParts().add(add"
                            + aggregateAO.getValue().getName().replace(" ", "") + "AO);");
                } else {
                    aggregateParameter = "AbstractObject remove" + aggregateAO.getValue().getName().replace(" ", "") + "AO";
                    methodCodeApply.add(this.getName().replace(" ", "").toLowerCase() + ".getAggregateParts().removeIf(agg -> agg.getName().equals(remove"
                            + aggregateAO.getValue().getName().replace(" ", "") + "AO.getName()));");
                }

                methodParameterApply.add(aggregateParameter);

            });
        }

        if (compositeObjects != null) {
            compositeObjects.entrySet().stream().forEach(compositeAO -> {
                String compositeParameter = "";

                if (compositeAO.getKey() == "added") {
                    compositeParameter = "AbstractObject add" + compositeAO.getValue().getName().replace(" ", "") + "CO";
                    methodCodeApply.add(this.getName().replace(" ", "").toLowerCase() + ".getCompositeParts().add(add"
                            + compositeAO.getValue().getName().replace(" ", "") + "CO);");
                } else {
                    compositeParameter = "AbstractObject remove" + compositeAO.getValue().getName().replace(" ", "") + "CO";
                    methodCodeApply.add(this.getName().toLowerCase() + ".getCompositeParts().removeIf(comp -> comp.getName().equals(remove"
                            + compositeAO.getValue().getName().replace(" ", "") + "CO.getName()));");
                }

                methodParameterApply.add(compositeParameter);
            });

        }


        if (properties != null) {
            properties.entrySet().stream().forEach(property -> {
                String propertyParameter = "";

                if (property.getKey() == "added") {
                    propertyParameter = "Property add" + property.getValue().getName().replace(" ", "");
                    methodCodeApply.add(this.getName().replace(" ", "").toLowerCase() + ".getProperties().add(add"
                            + property.getValue().getName().replace(" ", "") + ");");
                } else {
                    if (property.getKey() == "removed") {
                        propertyParameter = "Property remove" + property.getValue().getName().replace(" ", "");
                        methodCodeApply.add(this.getName().toLowerCase() + ".getProperties().removeIf(prop -> prop.getName().equals(remove"
                                + property.getValue().getName().replace(" ", "") + ".getName()));");
                    } else {
                        propertyParameter = "Property mod" + property.getValue().getName().replace(" ", "");
                        methodCodeApply.add(this.getName().toLowerCase() + ".getProperties().removeIf(prop -> prop.getName().equals(mod"
                                + property.getValue().getName().replace(" ", "") + ".getName()));");
                        methodCodeApply.add(this.getName().replace(" ", "").toLowerCase() + ".getProperties().add(mod"
                                + property.getValue().getName().replace(" ", "") + ");");
                    }
                }

                methodParameterApply.add(propertyParameter);
            });


        }

        if (methodParameterApply.size() != 0 && methodCodeApply.size() != 0) {
            codeBuilderApply.addMethod("public",
                    "void",
                    "apply",
                    methodParameterApply,
                    String.join("\n", methodCodeApply),
                    false);
            return new Pair<String, String>(codeBuilderApply.getFullClassName(), codeBuilderApply.buildClassCode());
        } else {
            return null;
        }
    }

    private HashMap<String, Property> verifyPropertiesStatus(List<AbstractObject> path) {

        List<AbstractObject> pathWithRoot = new ArrayList<>();
        pathWithRoot.add(this);
        pathWithRoot.addAll(path);

        HashMap<String, Property> properties = new HashMap<>();


        for (int i = 0; i < path.size(); i++) {

            HashMap<String, Property> checkProperties = new HashMap<>();

            if (i + 1 > path.size())
                break;

            int finalI = i;

            pathWithRoot.get(i).getProperties().forEach(propertyFirst -> {

                Optional<Property> propertyOptional = pathWithRoot.get(finalI + 1).getProperties().stream().filter(propertySecond -> propertySecond.getName().equals(propertyFirst.getName())).findFirst();

                if (propertyOptional.isPresent()) {

                    if (!propertyFirst.equals(propertyOptional.get())) {
                        if (!checkProperties.containsValue(propertyOptional.get())) {
                            checkProperties.put("modified", propertyOptional.get().clone());
                        }
                    }
                } else {
                    checkProperties.put("removed", propertyFirst.clone());
                }
            });

            pathWithRoot.get(i + 1).getProperties().forEach(propertySecond -> {

                Optional<Property> propertyOptional = pathWithRoot.get(finalI).getProperties().stream().filter(propertyFirst -> propertySecond.getName().equals(propertySecond.getName())).findFirst();

                if (!propertyOptional.isPresent()) {
                    checkProperties.put("added", propertySecond.clone());
                }
            });

            if (checkProperties.size() == 0) {
                break;
            } else {
                properties.putAll(checkProperties);
            }
        }

        return properties;

    }

    private HashMap<String, AbstractObject> verifyAbstractObjectsStatus(List<AbstractObject> abstractObjectsAfter, List<AbstractObject> abstractObjectsBefore) {

        HashMap<String, AbstractObject> abstractObjects = new HashMap<>();

        abstractObjectsAfter.stream().forEach(abstractObjectAfter -> {
            Optional<AbstractObject> first = abstractObjectsBefore.stream().filter(abstractObjectBefore ->
                    abstractObjectBefore.getName().equals(abstractObjectAfter.getName())).findFirst();
            if (!first.isPresent()) {
                abstractObjects.put("added", abstractObjectAfter.clone());
            }
        });

        abstractObjectsBefore.stream().forEach(abstractObjectBefore -> {
            Optional<AbstractObject> first = abstractObjectsAfter.stream().filter(abstractObjectAfter ->
                    abstractObjectAfter.getName().equals(abstractObjectBefore.getName())).findFirst();
            if (!first.isPresent()) {
                abstractObjects.put("removed", abstractObjectBefore.clone());
            }
        });

        return abstractObjects;

    }

    public List<AbstractObject> getCompositeParts() {
        return getCompositeList();
    }

    public void setCompositeParts(List<AbstractObject> parts) {
        this.setCompositeList(parts);
    }

    public void addCompositePart(AbstractObject part) {
        getCompositeList().add(part);
    }

    public void removeCompositePart(AbstractObject part) {
        getCompositeList().remove(part);
    }

    public List<AbstractObject> getAggregateParts() {
        return getAggregateList();
    }

    public void setAggregatePart(List<AbstractObject> aggregatedList) {
        this.setAggregateList(aggregatedList);
    }

    public void addAggregatePart(AbstractObject part) {
        getAggregateList().add(part);
    }

    public void removeAggregatePart(AbstractObject part) {
        getAggregateList().remove(part);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> props) {
        this.properties = props;
    }

    public void addProperty(Property prop) {
        getProperties().add(prop);
    }

    public void removeProperty(Property prop) {
        getProperties().remove(prop);
    }

    public List<Affordance> getAffordances() {
        return affordances;
    }

    public void setAffordances(List<Affordance> affordances) {
        this.affordances = affordances;
    }

    @Override
    public AbstractObject clone() {
        List<Property> newProperties = new ArrayList<>();
        for (Property p : getProperties()) {
            newProperties.add(p.clone());
        }
        List<AbstractObject> newComposite = new ArrayList<>();
        for (AbstractObject wo : getCompositeParts()) {
            newComposite.add(wo.clone());
        }
        List<AbstractObject> newAggregate = new ArrayList<>();
        for (AbstractObject wo : getAggregateParts()) {
            newAggregate.add(wo.clone());
        }
        AbstractObject result = new AbstractObject(getName(), newProperties, newComposite, newAggregate);
        result.setAffordances(getAffordances());
        return result;
    }

    public void deleteChild(Object child) {
        String childclass = child.getClass().getCanonicalName();
        //System.out.println("Childclass: "+childclass);
        if (childclass.equals("br.unicamp.cst.representation.owrl.AbstractObject")) {
            boolean cres = getCompositeList().remove(child);
            boolean ares = getAggregateList().remove(child);
            //System.out.println("cres: "+cres+" ares: "+ares);
        } else if (childclass.equals("br.unicamp.cst.representation.owrl.Property")) {
            getProperties().remove(child);
        }

    }


    public void addAffordance(Affordance affordance) {
        this.getAffordances().add(affordance);
    }

    public List<AbstractObject> getCompositeList() {
        return compositeList;
    }

    public void setCompositeList(List<AbstractObject> compositeList) {
        this.compositeList = compositeList;
    }

    public List<AbstractObject> getAggregateList() {
        return aggregateList;
    }

    public void setAggregateList(List<AbstractObject> aggregateList) {
        this.aggregateList = aggregateList;
    }
    
    public String toString() {
        String out = this.toString(1);
        return(out);
    }
    
    public String toString(int level) {
        String out="";
        out += name+"\n";
        for (AbstractObject ao : compositeList) {
            for (int i=0;i<level;i++) out += "   ";
            out += "* "+ ao.toString(level+1);
        }
        for (AbstractObject ao : aggregateList) {
            for (int i=0;i<level;i++) out += "   ";
            out += "+ "+ ao.toString(level+1);
        }
        for (Property p : properties) {
            for (int i=0;i<level;i++) out += "   ";
            out += "> "+ p.toString(level+1);
        }
       return(out); 
    }

    public boolean isEmpty() {
        if (properties.isEmpty() && compositeList.isEmpty() && aggregateList.isEmpty() && affordances.isEmpty()) return true;
        else return false;
    }
}
