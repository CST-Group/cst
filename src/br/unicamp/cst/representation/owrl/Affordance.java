/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.representation.owrl;

import java.util.Comparator;

/**
 *
 * @author suelen
 */
public abstract class Affordance {
    
    private String name;
    private String descriptor; //???? Qual a utilidade do descritor????
    private Comparator detector;
    
    
    
    public Affordance(String name, String descriptor, Comparator detector) {
        this.name = name;
        this.descriptor = descriptor;
        this.detector = detector;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public Comparator getDetector() {
        return detector;
    }

    public void setDetector(Comparator detector) {
        this.detector = detector;
    }
    
    public abstract void apply(AbstractObject object, double[] factor);
    
}
