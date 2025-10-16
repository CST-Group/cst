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
    Habit notExecuted;
    Habit nullIdeaSetter;
    Habit emptyIdeaSetter;

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
    public void testName() {
        System.out.println("\nTesting Name Setting and Default Name");
        HabitExecutionerCodelet hec = new HabitExecutionerCodelet();
        assertEquals("Default", hec.getName());
        hec = new HabitExecutionerCodelet("MyName");
        assertEquals("MyName", hec.getName(), "Name should be MyName");
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
        System.out.println("\nTesting the case where there is no habit memory container");

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
            outputSetter = mh.outputSetter;
            m = new Mind();
            mc = m.createMemoryContainer(name);
            Idea osh = new Idea("OutputSetter");
            osh.setValue(outputSetter);
            osh.setScope(2);
            mc.setI(osh);
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
            assertEquals(outputSetter, hec.h, "Habit should be the same as the one set in the container with name: " + name);
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
            outputSetter = mh.outputSetter;
            m = new Mind();
            mc = m.createMemoryContainer(name);
            Idea osh = new Idea("OutputSetter");
            osh.setValue(outputSetter);
            osh.setScope(2);
            mc.setI(osh);
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
        outputSetter = mh.outputSetter;
        mc = m.createMemoryContainer("testHabits");
        Idea osh = new Idea("OutputSetter");
        osh.setValue(outputSetter);
        osh.setScope(2);
        mc.setI(osh);

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
        assertEquals(outputSetter, hec.h, "Habit should not be null for container with correct name");

        Idea habit_input = global_idea.get("NotExecuted");
        // The other habit should be added to the root idea, but not executed
        assertEquals(habit_input, neh, "The habit in the container with incorrect name should be added to the root idea");
    }

    @Test
    public void testNullIdea() {
        System.out.println("\nTesting the case where the habit returns a null idea");

        exec_counter = 0;

        MockHabits mh = new MockHabits();
        nullIdeaSetter = mh.nullIdeaSetter;
        m = new Mind();
        mc = m.createMemoryContainer("testHabits");
        Idea nish = new Idea("NullIdeaSetter");
        nish.setValue(nullIdeaSetter);
        nish.setScope(2);
        mc.setI(nish);
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
        assertEquals(5, exec_counter, "The habit should have been executed 5 times without errors");
    }

    @Test
    public void testEmptyIdea() {
        System.out.println("\nTesting the case where the habit returns an empty idea");

        exec_counter = 0;

        MockHabits mh = new MockHabits();
        emptyIdeaSetter = mh.emptyIdeaSetter;
        m = new Mind();
        mc = m.createMemoryContainer("testHabits");
        Idea eish = new Idea("EmptyIdeaSetter");
        eish.setValue(emptyIdeaSetter);
        eish.setScope(2);
        mc.setI(eish);
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
        assertEquals(5, exec_counter, "The habit should have been executed 5 times without errors");
    }

    @Test
    public void testSettingIdeaMemoryObject() {
        System.out.println("\nTesting the case where the habit sets an idea to an output memory object");

        exec_counter = 0;

        MockHabits mh = new MockHabits();
        outputSetter = mh.outputSetter;
        m = new Mind();
        mc = m.createMemoryContainer("testHabits");
        Idea osh = new Idea("OutputSetter");
        osh.setValue(outputSetter);
        osh.setScope(2);
        mc.setI(osh);
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
        assertEquals(5, exec_counter, "The habit should have been executed 5 times without errors");

        Object oo = moo.getI();
        if (oo != null) {
            Idea ooi = (Idea) oo;
            int val = (int) ooi.getValue();
            assertEquals(13, val, "The value set in the output memory object should be 13");
        }
        else fail("The output memory object is null");
    }

    @Test
    public void testSettingIdeaMemoryContainer() {
        System.out.println("\nTesting the case where the habit sets an idea to an output memory container");

        exec_counter = 0;

        MockHabits mh = new MockHabits();
        outputSetter = mh.outputSetter;
        m = new Mind();
        mc = m.createMemoryContainer("testHabits");
        Idea osh = new Idea("OutputSetter");
        osh.setValue(outputSetter);
        osh.setScope(2);
        mc.setI(osh);
        moi = m.createMemoryObject("InputIdeasMemory");
        MemoryContainer moc = m.createMemoryContainer("OutputIdeasMemory");
        HabitExecutionerCodelet hec = new HabitExecutionerCodelet("test");
        hec.addInput(mc);
        hec.addInput(moi);
        hec.addOutput(moc);

        m.insertCodelet(hec);
        m.start();

        try {
            while(exec_counter < 5) { System.out.print("."); Thread.sleep(1); }
        } catch (Exception e) {
            fail("An error occurred: " + e.getMessage());
        }
        assertEquals(5, exec_counter, "The habit should have been executed 5 times without errors");

        Object oo = moc.getLastI();
        if (oo != null) {
            Idea ooi = (Idea) oo;
            int val = (int) ooi.getValue();
            assertEquals(13, val, "The value set in the output memory container should be 13");
        }
        else fail("The output memory container is empty");
    }

    @Test
    public void testSettingTwoIdeasTwoMemories() {
        System.out.println("\nTesting the case where the habit sets two ideas to two output memories");

        exec_counter = 0;

        MockHabits mh = new MockHabits();
        outputSetter = mh.outputSetter;
        m = new Mind();
        mc = m.createMemoryContainer("testHabits");
        Idea osh = new Idea("OutputSetter");
        osh.setValue(outputSetter);
        osh.setScope(2);
        mc.setI(osh);
        moi = m.createMemoryObject("InputIdeasMemory");
        MemoryContainer moc = m.createMemoryContainer("OutputIdeasMemory");
        moo = m.createMemoryObject("anotherIdea");
        HabitExecutionerCodelet hec = new HabitExecutionerCodelet("test");
        hec.addInput(mc);
        hec.addInput(moi);
        hec.addOutput(moc);
        hec.addOutput(moo);

        m.insertCodelet(hec);
        m.start();

        try {
            while(exec_counter < 5) { System.out.print("."); Thread.sleep(1); }
        } catch (Exception e) {
            fail("An error occurred: " + e.getMessage());
        }
        assertEquals(5, exec_counter, "The habit should have been executed 5 times without errors");

        Object oo = moc.getLastI();
        if (oo != null) {
            Idea ooi = (Idea) oo;
            int val = (int) ooi.getValue();
            assertEquals(13, val, "The value set in the first output memory container should be 13");
        }
        else fail("The first output memory container is empty");

        oo = moo.getI();
        if (oo != null) {
            Idea ooi = (Idea) oo;
            String val = (String) ooi.getValue();
            assertEquals("abc", val, "The value set in the second output memory object should be 'abc'");
        }
        else fail("The second output memory object is null");
    }

    class MockHabits {
        public MockHabits() {
        }

        Habit summer = new Habit() { 
            @Override 
            public Idea exec(Idea idea) {
                exec_counter++;
                
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
                exec_counter++;

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
                exec_counter++;

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
                exec_counter++;

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
                exec_counter++;

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
                exec_counter++;
                global_idea = idea;

                Idea root = new Idea("root", "");
                root.add(new Idea("someIdea", 123));
                root.add(new Idea("anotherIdea", "abc"));
                root.add(new Idea("OutputIdeasMemory", 13));
                return root;
            }
        };

        Habit notExecuted = new Habit() {
            @Override 
            public Idea exec(Idea idea) {
                exec_counter++;

                Idea root = new Idea("root", "");
                root.add(new Idea("OutputIdeasMemory", 9999));
                return root;
            }
        };

        Habit nullIdeaSetter = new Habit() {
            @Override 
            public Idea exec(Idea idea) {
                exec_counter++;

                return null;
            }
        };

        Habit emptyIdeaSetter = new Habit() {
            @Override 
            public Idea exec(Idea idea) {
                exec_counter++;

                return new Idea("root", "");
            }
        };
    }
}