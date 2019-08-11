/**
 * 
 */
package br.unicamp.cst.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.unicamp.cst.representation.owrl.AbstractObject;
import br.unicamp.cst.representation.owrl.Affordance;
import br.unicamp.cst.representation.owrl.Property;
import br.unicamp.cst.representation.owrl.QualityDimension;

/**
 * @author rgudwin
 *
 */
public class AbstractObjectEditorTest {
	
	@Test
	public void testAbstractObjectEditor() {
		
		AbstractObject robot = new AbstractObject("Robot");

        AbstractObject sensor = new AbstractObject("Sensor");

        Property position = new Property("position");

        position.addQualityDimension(new QualityDimension("x",0.5));
        position.addQualityDimension(new QualityDimension("y",0.6));
        sensor.addProperty(position);

        Property speed = new Property("speed");

        speed.addQualityDimension(new QualityDimension("x",0.0));
        speed.addQualityDimension(new QualityDimension("y",0.0));
        sensor.addProperty(speed);

        Property color = new Property("color");

        color.addQualityDimension(new QualityDimension("R",0.0));
        color.addQualityDimension(new QualityDimension("G",0.0));
        color.addQualityDimension(new QualityDimension("B",255.0));
        sensor.addProperty(color);

        Property temperature = new Property("temperature");

        temperature.addQualityDimension(new QualityDimension("value",0.0));
        sensor.addProperty(temperature);
        
        robot.addCompositePart(sensor);
        AbstractObject actuator = new AbstractObject("Actuator");
        actuator.addProperty(new Property("velocity",new QualityDimension("intensity",-0.12)));
        robot.addCompositePart(actuator);
        robot.addAggregatePart(actuator.clone());
        robot.addProperty(new Property("Model",new QualityDimension("Serial#","1234XDr56")));
        
        List<Affordance> affordances = new ArrayList<>();
        /*affordances.add(new Affordance("heat up", "", new Comparator<AbstractObject>() {
            @Override
            public int compare(AbstractObject o1, AbstractObject o2) {
                QualityDimension temperature_value1 = getTemperatureValue(o1);
                if (temperature_value1 != null) {
                    QualityDimension temperature_value2 = getTemperatureValue(o2);
                    if (temperature_value2 != null) {
                        if (((Double) temperature_value2.getValue()) > (Double) temperature_value1.getValue()) {
                            return 1;
                        }
                    }
                }
                return 0;
            }
        }) {
            @Override
            public void apply(AbstractObject object, Object[] factors) {
                if (factors.length == 1 && factors[0] instanceof Double) {
                    QualityDimension temperature_value = getTemperatureValue(object);
                    if (temperature_value != null) {
                        temperature_value.setValue((Double) temperature_value.getValue() + (Double) factors[0]);
                    }
                }
            }
        });
        affordances.add(new Affordance("paint", "", new Comparator<AbstractObject>() {
            @Override
            public int compare(AbstractObject o1, AbstractObject o2) {
                List<QualityDimension> color1 = getColorDimensions(o1);
                if (color1 != null) {
                    List<QualityDimension> color2 = getColorDimensions(o2);
                    if (color2 != null) {
                        if (Math.abs((Double) color1.get(0).getValue() - (Double) color2.get(0).getValue()) > 0
                            || Math.abs((Double) color1.get(1).getValue() - (Double) color2.get(1).getValue()) > 0
                            || Math.abs((Double) color1.get(2).getValue() - (Double) color2.get(2).getValue()) > 0) {
                            return 1;
                        }
                    }
                }
                return 0;
            }
        }) {
            @Override
            public void apply(AbstractObject object, Object[] factors) {
                if (factors.length == 3
                        && factors[0] instanceof Double
                        && factors[1] instanceof Double
                        && factors[2] instanceof Double) {
                    List<QualityDimension> color = getColorDimensions(object);
                    if (color != null) {
                        color.get(0).setValue(factors[0]);
                        color.get(1).setValue(factors[1]);
                        color.get(2).setValue(factors[2]);
                    }
                }
            }
        });*/
        /*affordances.add(new Affordance("push", "", new Comparator<AbstractObject>() {
            @Override
            public int compare(AbstractObject o1, AbstractObject o2) {
                List<QualityDimension> speed1 = getSpeedDimensions(o1);
                if (speed1 != null) {
                    List<QualityDimension> speed2 = getSpeedDimensions(o2);
                    if (speed2 != null && speed1.size() == speed2.size()) {
                        List<QualityDimension> position1 = getPositionDimensions(o1);
                        if (position1 != null && position1.size() == speed1.size()) {
                            List<QualityDimension> position2 = getPositionDimensions(o2);
                            if (position2 != null && position2.size() == speed2.size()) {
                                boolean speedChange = false;
                                for (int dimension = 0; dimension < speed1.size(); dimension++) {
                                    if (Math.abs((Double) speed1.get(dimension).getValue() - (Double) speed2.get(dimension).getValue()) > 0) {
                                        speedChange = true;
                                        break;
                                    }
                                }
                                if (speedChange) {
                                    for (int dimension = 0; dimension < position1.size(); dimension++) {
                                        if (Math.abs((Double) position1.get(dimension).getValue() - (Double) position2.get(dimension).getValue()) > 0) {
                                            return 0;
                                        }
                                    }
                                    return 1;
                                }
                            }
                        }
                    }
                }
                return 0;
            }
        }) {
            @Override
            public void apply(AbstractObject object, Object[] factors) {
                List<QualityDimension> speed = getSpeedDimensions(object);
                if (speed != null && factors.length == speed.size() + 1) {
                    boolean doubles = true;
                    for (Object factor : factors) {
                        if (!(factor instanceof Double)) {
                            doubles = false;
                            break;
                        }
                    }
                    if (doubles) {
                        double time = (Double) factors[factors.length - 1];
                        for (int dimension = 0; dimension < speed.size(); dimension++) {
                            speed.get(dimension).setValue((Double) speed.get(dimension).getValue() + (Double) factors[dimension] * time);
                        }
                    }
                }
            }
        });*/
        /*affordances.add(new Affordance("move", "", new Comparator<AbstractObject>() {
            @Override
            public int compare(AbstractObject o1, AbstractObject o2) {
                List<QualityDimension> position1 = getPositionDimensions(o1);
                if (position1 != null) {
                    List<QualityDimension> position2 = getPositionDimensions(o2);
                    if (position2 != null && position2.size() == position1.size()) {
                        for (int dimension = 0; dimension < position1.size(); dimension++) {
                            if (Math.abs((Double) position1.get(dimension).getValue() - (Double) position2.get(dimension).getValue()) > 0) {
                                return 1;
                            }
                        }
                    }
                }
                return 0;
            }
        }) {
            @Override
            public void apply(AbstractObject object, Object[] factors) {
                if (factors.length == 2
                        && factors[0] instanceof Double
                        && factors[1] instanceof Double) {
                    List<QualityDimension> speed = getSpeedDimensions(object);
                    if (speed != null) {
                        List<QualityDimension> position = getPositionDimensions(object);
                        if (position != null && position.size() == speed.size()) {
                            double time = (Double) factors[0];
                            double friction = (Double) factors[1];
                            for (int dimension = 0; dimension < speed.size(); dimension++) {
                                position.get(dimension).setValue((Double) position.get(dimension).getValue() + (Double) speed.get(dimension).getValue() * time);
                                speed.get(dimension).setValue(Math.signum((Double) speed.get(dimension).getValue()) * Math.max(0, Math.abs((Double) speed.get(dimension).getValue()) - time * friction));
                            }
                        }
                    }
                }
            }
        });*/
        robot.setAffordances(affordances);
        AbstractObjectEditor ov = new AbstractObjectEditor(robot);
        //System.out.println(robot);
       
        ov.setVisible(true);
	}

}
