package br.unicamp.cst.representation.owrl;

import org.junit.Test;

import java.util.Arrays;

/**
 * Created by du on 30/05/17.
 */
public class AbstractObjectTest {

    public void setUp() {
        System.out.println("########## AFFORDANCE TESTS ##########");
    }

    @Test
    public void testDynamicAffordanceDetect() {
        //------------- Objeto 1 --------------------------

        AbstractObject robot = new AbstractObject("Robot");

        AbstractObject sensor = new AbstractObject("Sensor");
        Property position = new Property("position");
        position.addQualityDimension(new QualityDimension("x", 0.5));
        position.addQualityDimension(new QualityDimension("y", 0.6));
        sensor.addProperty(position);

        robot.addCompositePart(sensor);

        Property color = new Property("Color");
        color.addQualityDimension(new QualityDimension("R", 255.0));
        color.addQualityDimension(new QualityDimension("G", 0.0));
        color.addQualityDimension(new QualityDimension("B", 0.0));

        robot.addProperty(color);

        AbstractObject actuator = new AbstractObject("Actuator");
        actuator.addProperty(new Property("velocity", new QualityDimension("intensity", -0.12)));

        robot.addCompositePart(actuator);

        AbstractObject sonar = new AbstractObject("Sonar");
        sonar.addProperty(new Property("Distance", new QualityDimension("value", 215.0)));

        robot.addAggregatePart(sonar);

        AbstractObject gps = new AbstractObject("GPS");
        Property coordinates = new Property("Coordinates");
        coordinates.addQualityDimension(new QualityDimension("x", 12.0));
        coordinates.addQualityDimension(new QualityDimension("y", 76.0));
        coordinates.addQualityDimension(new QualityDimension("z", 50.0));
        gps.addProperty(coordinates);
        robot.addAggregatePart(gps);


        //------------- Objeto 2 --------------------------
        AbstractObject robot2 = new AbstractObject("Robot1");

        Property newcolor = new Property("Color");
        newcolor.addQualityDimension(new QualityDimension("R", 0.0));
        newcolor.addQualityDimension(new QualityDimension("G", 255.0));
        newcolor.addQualityDimension(new QualityDimension("B", 255.0));

        robot2.addProperty(newcolor);

        AbstractObject actuator1 = new AbstractObject("Actuator");
        actuator1.addProperty(new Property("velocity", new QualityDimension("intensity", -0.12)));

        robot2.addCompositePart(actuator1);

        AbstractObject temperatureSensor = new AbstractObject("Temperature Sensor");
        temperatureSensor.addProperty(new Property("temperature", new QualityDimension("value", 35.0)));

        robot2.addCompositePart(temperatureSensor);

        AbstractObject radio = new AbstractObject("Radio");
        radio.addProperty(new Property("frequency", new QualityDimension("value", 89.9)));
        robot2.addAggregatePart(radio);

        AbstractObject gps1 = new AbstractObject("GPS");
        Property coordinates1 = new Property("Coordinates");
        coordinates1.addQualityDimension(new QualityDimension("x", 12.0));
        coordinates1.addQualityDimension(new QualityDimension("y", 88.0));
        coordinates1.addQualityDimension(new QualityDimension("z", 50.0));
        gps1.addProperty(coordinates1);
        robot2.addAggregatePart(gps1);

        //------------- Objeto 3 --------------------------
        AbstractObject robot3 = new AbstractObject("Robot2");

        Property colorRobot3 = new Property("Color");
        colorRobot3.addQualityDimension(new QualityDimension("R", 254.0));
        colorRobot3.addQualityDimension(new QualityDimension("G", 200.0));
        colorRobot3.addQualityDimension(new QualityDimension("B", 255.0));
        robot3.addProperty(colorRobot3);

        AbstractObject actuator2 = new AbstractObject("Actuator");
        actuator2.addProperty(new Property("velocity", new QualityDimension("intensity", -0.22)));
        robot3.addCompositePart(actuator2);

        AbstractObject temperatureSensor1 = new AbstractObject("Temperature Sensor");
        temperatureSensor1.addProperty(new Property("temperature", new QualityDimension("value", 32.0)));
        robot3.addCompositePart(temperatureSensor1);

        AbstractObject radio1 = new AbstractObject("Radio");
        radio1.addProperty(new Property("frequency", new QualityDimension("value", 89.9)));
        robot3.addAggregatePart(radio1);

        AbstractObject gps2 = new AbstractObject("GPS");
        Property coordinates2 = new Property("Coordinates");
        coordinates2.addQualityDimension(new QualityDimension("x", 12.0));
        coordinates2.addQualityDimension(new QualityDimension("y", 80.0));
        coordinates2.addQualityDimension(new QualityDimension("z", 50.0));
        gps2.addProperty(coordinates2);
        robot3.addAggregatePart(gps2);

        robot.discoveryAffordance(robot3, Arrays.asList(robot2, robot3));

        System.out.println("Dynamic Affordance Created ----> OK");
    }

}
