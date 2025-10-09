/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.unicamp.cst.representation.idea;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Random;

import org.junit.jupiter.api.Test;

import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;

/**
 *
 * @author rgudwin
 */
public class HabitExecutionerCodeletTest {
    double act;
    long timeStep;
    boolean publishSubscribe;
    int exec_counter;
    int proc_counter;

    Habit summer;
    Habit decrementer;
    Habit actSetter;
    Habit timeStepSetter;
    Habit publishSubscribeSetter;
    Habit outputSetter;
    Habit simple;
    Habit notExecuted;

    Mind m;
    MemoryContainer mc;
    MemoryObject moi;
    MemoryObject moo;

    Idea global_idea;
    
    private void setUp() {
        MockHabits mh = new MockHabits();
        summer = mh.summer;
        decrementer = mh.decrementer;

        m = new Mind();

        mc = m.createMemoryContainer("testHabits");
        Idea sh = new Idea("Summer");
        sh.setValue(summer);
        sh.setScope(2);
        Idea dh = new Idea("Decrementer");
        dh.setValue(decrementer);
        dh.setScope(2);
        mc.setI(sh);
        mc.setI(dh);
        moi = m.createMemoryObject("InputIdeasMemory");
        moo = m.createMemoryObject("OutputIdeasMemory");
        HabitExecutionerCodelet hec = new HabitExecutionerCodelet("test");
        hec.addInput(mc);
        hec.addInput(moi);
        hec.addOutput(moo);
        hec.setPublishSubscribe(true);
        m.insertCodelet(hec);
        m.start();
    }

    @Test
    public void testName() {
        System.out.println("\nTesting Name Setting and Default Name");
        HabitExecutionerCodelet hec = new HabitExecutionerCodelet();
        assertEquals("Default", hec.getName());
        hec = new HabitExecutionerCodelet("MyName");
        assertEquals("MyName", hec.getName(), "Name should be MyName");
    }
    
    @Test 
    public void testHabitExecutionerCodeletMAX() {
        setUp();
        mc.setPolicy(MemoryContainer.Policy.MAX);
        System.out.println("\nTesting the MAX Policy - Sums for < 50 Decs for > 50");
        doTest();
    }

    @Test 
    public void testHabitExecutionerCodeletMIN() {
        setUp();
        mc.setPolicy(MemoryContainer.Policy.MIN);
        System.out.println("\nTesting the MIN Policy - Sums for > 50 Decs for < 50");
        doTest();
    }
    
    @Test 
    public void testHabitExecutionerCodeletIterate() {
        setUp();
        mc.setPolicy(MemoryContainer.Policy.ITERATE);
        System.out.println("\nTesting the ITERATE Policy - Iterate Sums and Decs");
        doTest(); 
    }
    
    @Test 
    public void testHabitExecutionerCodeletRandom() {
        setUp();
        mc.setPolicy(MemoryContainer.Policy.RANDOM_FLAT);
        System.out.println("\nTesting the RANDOM_FLAT Policy - Sums and Decs at Random");
        doTest(); 
    }

    @Test
    public void testActivation() {
        System.out.println("\nTesting Activation Setting through Habits");

        Random r = new Random();
        for (int k=0;k<100;k++) {
            act = r.nextDouble();
            MockHabits mh = new MockHabits();
            actSetter = mh.actSetter;
            HabitExecutionerCodelet hec = new HabitExecutionerCodelet("Name");
            hec.h = actSetter;
            hec.proc();

            assertEquals(act, hec.getActivation(), "Activation should be " + act);
        }
    }

    @Test
    public void testTimeStep() {
        System.out.println("\nTesting TimeStep Setting through Habits");

        Random r = new Random();
        for (int k=0;k<100;k++) {
            timeStep = r.nextInt(1000);
            MockHabits mh = new MockHabits();
            timeStepSetter = mh.timeStepSetter;
            HabitExecutionerCodelet hec = new HabitExecutionerCodelet("Name");
            hec.h = timeStepSetter;
            hec.proc();

            assertEquals(timeStep, hec.getTimeStep(), "TimeStep should be " + timeStep);
        }

    }

    @Test
    public void testPublishSubscribe() {
        System.out.println("\nTesting PublishSubscribe Setting through Habits");

        MockHabits mh = new MockHabits();
        publishSubscribeSetter = mh.publishSubscribeSetter;
        HabitExecutionerCodelet hec = new HabitExecutionerCodelet("Name");
        hec.h = publishSubscribeSetter;
        publishSubscribe = true;
        hec.proc();
        assertEquals(true, hec.isPublishSubscribe(), "PublishSubscribe should be true");
        publishSubscribe = false;
        hec.proc();

        assertEquals(false, hec.isPublishSubscribe(), "PublishSubscribe should be false");
    }

    @Test
    public void testNoOutputMemory() {
        System.out.println("\nTesting the case where there is no output memory");

        exec_counter = 0;
        MockHabits mh = new MockHabits();
        outputSetter = mh.outputSetter;
        m = new Mind();
        mc = m.createMemoryContainer("testHabits");
        Idea osh = new Idea("OutputSetter");
        osh.setValue(outputSetter);
        osh.setScope(2);
        mc.setI(osh);
        HabitExecutionerCodelet hec = new HabitExecutionerCodelet("test");
        hec.addInput(mc);
        m.insertCodelet(hec);
        m.start();
        try {
            while(exec_counter < 5) { System.out.print("."); Thread.sleep(1); }
        } catch (Exception e) {
            fail("An error occurred: " + e.getMessage());
        }
        assertEquals(5, exec_counter, "The habit should have been executed 5 times without errors");
    }

    @Test
    public void testNoHabitMemoryContainer() {
        System.out.println("\nTesting the case where there is no habit to execute");

        proc_counter = 0;
        m = new Mind();
        moi = m.createMemoryObject("InputIdeasMemory");
        moo = m.createMemoryObject("OutputIdeasMemory");
        HabitExecutionerCodelet hec = new HabitExecutionerCodelet("test") {
            @Override
            public void proc() {
                proc_counter++;
                super.proc();
            }
        };
        hec.addInput(moi);
        hec.addOutput(moo);

        m.insertCodelet(hec);
        m.start();

        try {
            while(proc_counter < 5) { System.out.print("."); Thread.sleep(1); }
        } catch (Exception e) {
            fail("An error occurred: " + e.getMessage());
        }
        assertNull(hec.h, "The habit should be null");
    }

    @Test
    public void testNoHabit() {
        System.out.println("\nTesting the case where there is a habit memory container but no habit inside it");

        proc_counter = 0;
        m = new Mind();
        mc = m.createMemoryContainer("testHabits");
        moi = m.createMemoryObject("InputIdeasMemory");
        moo = m.createMemoryObject("OutputIdeasMemory");
        HabitExecutionerCodelet hec = new HabitExecutionerCodelet("test") {
            @Override
            public void proc() {
                proc_counter++;
                super.proc();
            }
        };
        hec.addInput(mc);
        hec.addInput(moi);
        hec.addOutput(moo);

        m.insertCodelet(hec);
        m.start();

        try {
            while(proc_counter < 5) { System.out.print("."); Thread.sleep(1); }
        } catch (Exception e) {
            fail("An error occurred: " + e.getMessage());
        }
        assertNull(hec.h, "The habit should be null");
    }

    @Test
    public void testHabitsContainerNameMatching() {
        System.out.println("\nTesting the case where there is one Habit Memory Container with correct name");

        String[] matchingNames = {"testHabits", "TESTHABITS", "TestHabits", "tEsThAbItS"};

        for (String name : matchingNames) {
            exec_counter = 0;
            MockHabits mh = new MockHabits();
            simple = mh.simple;
            m = new Mind();
            mc = m.createMemoryContainer(name);
            Idea sh = new Idea("Simple");
            sh.setValue(simple);
            sh.setScope(2);
            mc.setI(sh);
            moi = m.createMemoryObject("InputIdeasMemory");
            moo = m.createMemoryObject("OutputIdeasMemory");
            HabitExecutionerCodelet hec = new HabitExecutionerCodelet("test");
            hec.addInput(mc);
            hec.addInput(moi);
            hec.addOutput(moo);

            m.insertCodelet(hec);
            m.start();

            try {
                while(exec_counter < 5) { System.out.print("."); Thread.sleep(1); }
            } catch (Exception e) {
                fail("An error occurred: " + e.getMessage());
            }
            assertEquals(simple, hec.h, "Habit should be the same as the one set in the container with name: " + name);
        }
    }

    @Test
    public void testHabitsContainerNameNotMatching() {
        System.out.println("\nTesting the case where there is one Habit Memory Container with incorrect name");

        String[] nonMatchingNames = {"someOtherName", "habitsTest", "test_habits", "testhabit"};

        // Test non-matching names (should not find habit)
        for (String name : nonMatchingNames) {
            System.out.println("\nTesting with container name: " + name);

            proc_counter = 0;
            MockHabits mh = new MockHabits();
            simple = mh.simple;
            m = new Mind();
            mc = m.createMemoryContainer(name);
            Idea sh = new Idea("Simple");
            sh.setValue(simple);
            sh.setScope(2);
            mc.setI(sh);
            moi = m.createMemoryObject("InputIdeasMemory");
            moo = m.createMemoryObject("OutputIdeasMemory");
            HabitExecutionerCodelet hec = new HabitExecutionerCodelet("test") {
                @Override
                public void proc() {
                    proc_counter++;
                    super.proc();
                }
            };
            hec.addInput(mc);
            hec.addInput(moi);
            hec.addOutput(moo);

            m.insertCodelet(hec);
            m.start();

            try {
                while(proc_counter < 5) { System.out.print("."); Thread.sleep(1); }
            } catch (Exception e) {
                fail("An error occurred: " + e.getMessage());
            }
                assertNull(hec.h, "Habit should be null for container name: " + name);
            }
    }

    // Test case for when there is one Habit Memory Container with correct name and one without
    // In this case, the codelet should find the habit in the container with the correct name
    // and add the other habit to the root idea, but not execute it
    @Test
    public void testDoubleHabitContainers() {
        System.out.println("\nTesting the case where there are two habit memory containers, one with the correct name and one without");

        proc_counter = 0;
        m = new Mind();

        MockHabits mh = new MockHabits();
        simple = mh.simple;
        mc = m.createMemoryContainer("testHabits");
        Idea sh = new Idea("Simple");
        sh.setValue(simple);
        sh.setScope(2);
        mc.setI(sh);

        notExecuted = mh.notExecuted;
        MemoryContainer mc2 = m.createMemoryContainer("someOtherName");
        Idea neh = new Idea("NotExecuted");
        neh.setValue(notExecuted);
        neh.setScope(2);
        mc2.setI(neh);

        moi = m.createMemoryObject("InputIdeasMemory");
        moo = m.createMemoryObject("OutputIdeasMemory");
        HabitExecutionerCodelet hec = new HabitExecutionerCodelet("test") {
            @Override
            public void proc() {
                proc_counter++;
                super.proc();
            }
        };
        hec.addInput(mc);
        hec.addInput(mc2);
        hec.addInput(moi);
        hec.addOutput(moo);

        m.insertCodelet(hec);
        m.start();

        try {
            while(proc_counter < 5) { System.out.print("."); Thread.sleep(1); }
        } catch (Exception e) {
            fail("An error occurred: " + e.getMessage());
        }

        // Should find the habit in the container with the correct name
        assertEquals(simple, hec.h, "Habit should not be null for container with correct name");

        Idea habit_input = global_idea.get("NotExecuted");
        // The other habit should be added to the root idea, but not executed
        assertEquals(habit_input, neh, "The habit in the container with incorrect name should be added to the root idea");
    }

    private void doTest() {
        Object oo;
        Random r = new Random();
        for (int k=0;k<100;k++) {
            int major = r.nextInt(100);
            if (major < 50) {
                mc.setEvaluation(0.7,0);
                mc.setEvaluation(0.3,1);
            }
            else {
                mc.setEvaluation(0.3,0);
                mc.setEvaluation(0.7,1);
            }
            int minor = r.nextInt(10);
            Idea i = new Idea("value",major);
            i.add(new Idea("add",minor));
            moi.setI(i);
            long ti = moi.getTimestamp();
            while(moo.getTimestamp() < ti) System.out.print(".");
            oo = moo.getI();
            if (oo != null) {
                Idea ooi = (Idea) oo;
                int sum = (int) ooi.getValue();
                Idea ih = (Idea) mc.getLastI();
                int op;
                String ops;
                if (ih.getName().equals("Summer")) {
                    op = 0;
                    ops = "+";
                }
                else {
                    op = 1;
                    ops = "-";
                }
                System.out.println(ih+" "+major+ops+minor+"="+sum);
                if (op == 0)
                   assertEquals(sum,major+minor);
                else 
                   assertEquals(sum,major-minor); 
            }
            else fail("The output memory object is null");
        } 
    }

    class MockHabits {
        public MockHabits() {
        }

        Habit summer = new Habit() { 
            @Override 
            public Idea exec(Idea idea) {
                Idea root = new Idea("root", "");
                Idea adder = idea.get("value.add");
                int valuetoadd=0;
                if (adder != null && adder.getValue() instanceof Integer) {
                    valuetoadd = (int) adder.getValue();
                }
                if (idea.get("value").getValue() instanceof Integer) {
                    int number = (int) idea.get("value").getValue();
                    Idea modifiedIdea = new Idea("OutputIdeasMemory",number+valuetoadd);
                    root.add(modifiedIdea);
                    return root;
                }
                System.out.println("Something wrong happened");
                return(null);
            }
        };

        Habit decrementer = new Habit() { 
            @Override 
            public Idea exec(Idea idea) {
                Idea root = new Idea("root", "");
                Idea adder = idea.get("value.add");
                int valuetodec=0;
                if (adder != null && adder.getValue() instanceof Integer) {
                    valuetodec = (int) adder.getValue();
                }
                if (idea.get("value").getValue() instanceof Integer) {
                    int number = (int) idea.get("value").getValue();
                    Idea modifiedIdea = new Idea("OutputIdeasMemory",number-valuetodec);
                    root.add(modifiedIdea);
                    return root;
                }
                System.out.println("Something wrong happened");
                return(null);
            }
        };

        Habit actSetter = new Habit() { 
            @Override 
            public Idea exec(Idea idea) {
                Idea root = new Idea("root", "");
                root.add(new Idea("activation", act));
                root.add(new Idea("someIdea", 123));
                root.add(new Idea("anotherIdea", "abc"));
                return root;
            }
        };

        Habit timeStepSetter = new Habit() { 
            @Override 
            public Idea exec(Idea idea) {
                Idea root = new Idea("root", "");
                root.add(new Idea("timeStep", timeStep));
                root.add(new Idea("someIdea", 123));
                root.add(new Idea("anotherIdea", "abc"));
                return root;
            }
        };

        Habit publishSubscribeSetter = new Habit() { 
            @Override 
            public Idea exec(Idea idea) {
                Idea root = new Idea("root", "");
                root.add(new Idea("publishSubscribe", publishSubscribe));
                root.add(new Idea("someIdea", 123));
                root.add(new Idea("anotherIdea", "abc"));
                return root;
            }
        };

        Habit outputSetter = new Habit() {
            @Override 
            public Idea exec(Idea idea) {
                Idea root = new Idea("root", "");
                root.add(new Idea("someIdea", 123));
                root.add(new Idea("anotherIdea", "abc"));
                exec_counter++;
                return root;
            }
        };

        Habit simple = new Habit() {
            @Override 
            public Idea exec(Idea idea) {
                global_idea = idea;
                Idea root = new Idea("root", "");
                root.add(new Idea("OutputIdeasMemory", 13));
                exec_counter++;
                return root;
            }
        };

        Habit notExecuted = new Habit() {
            @Override 
            public Idea exec(Idea idea) {
                Idea root = new Idea("root", "");
                root.add(new Idea("OutputIdeasMemory", 9999));
                exec_counter++;
                return root;
            }
        };
    }
}