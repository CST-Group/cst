package br.unicamp.cst.motivational;

/**
 * Created by Du on 03/11/16.
 */
public class Drive {

    private String name;
    private double activation;
    private double priority;
    private double level;
    private double urgentActivation;
    private double urgentActivationThreshold;
    private double lowerUrgentThreshold;

    public Drive(String name){
        setName(name);
    }

    public Drive(String name, double activation, double priority, double level, double urgentActivation, double urgentActivationThreshold, double lowerUrgentThreshold){
        setName(name);
        setActivation(activation);
        setPriority(priority);
        setLevel(level);
        setUrgentActivation(urgentActivation);
        setUrgentActivationThreshold(urgentActivationThreshold);
        setLowerUrgentThreshold(lowerUrgentThreshold);
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized double getActivation() {
        return activation;
    }

    public synchronized void setActivation(double activation) {
        this.activation = activation;
    }

    public synchronized double getPriority() {
        return priority;
    }

    public synchronized void setPriority(double priority) {
        this.priority = priority;
    }

    public synchronized double getLevel() {
        return level;
    }

    public synchronized void setLevel(double level) {
        this.level = level;
    }

    public synchronized double getUrgentActivation() {
        return urgentActivation;
    }

    public synchronized void setUrgentActivation(double urgentActivation) {
        this.urgentActivation = urgentActivation;
    }

    public synchronized double getUrgentActivationThreshold() {
        return urgentActivationThreshold;
    }

    public synchronized void setUrgentActivationThreshold(double urgentActivationThreshold) {
        this.urgentActivationThreshold = urgentActivationThreshold;
    }

    public synchronized double getLowerUrgentThreshold() {
        return lowerUrgentThreshold;
    }

    public synchronized void setLowerUrgentThreshold(double lowerUrgentThreshold) {
        this.lowerUrgentThreshold = lowerUrgentThreshold;
    }
}
