package br.unicamp.cst.core.entities;

import static org.junit.Assert.*;

import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author wander
 *
 */
public class CodeRackTest {

    Codelet testCodelet = new Codelet() {

        @Override
        public void accessMemoryObjects() {}
        @Override
        public void proc() {
            System.out.println("proc method ran correctly!");
        }
        @Override
        public void calculateActivation() {}
    };

    Codelet otherCodelet = new Codelet() {

        @Override
        public void accessMemoryObjects() {}
        @Override
        public void proc() {

            System.out.println("proc method ran correctly!");
        }
        @Override
        public void calculateActivation() {}
    };

    @Test
    public void setAllCodeletTest(){
        CodeRack codeRack = new CodeRack();
        List<Codelet> testList = Arrays.asList(testCodelet, otherCodelet);

        codeRack.setAllCodelets(testList);
        assertEquals(testList, codeRack.getAllCodelets());
    }

    @Test
    public void insertCodeletTest(){
        CodeRack codeRack = new CodeRack();
        List<Codelet> testList = Arrays.asList(testCodelet);

        codeRack.insertCodelet(testCodelet);

        assertEquals(testList, codeRack.getAllCodelets());
    }

    @Test
    public void createCodeletTest(){
        CodeRack codeRack = new CodeRack();
        List<Memory> memInputTest = Arrays.asList(new MemoryObject(), new MemoryObject());
        List<Memory> memOutputTest = Arrays.asList(new MemoryObject());

        codeRack.createCodelet(0.5, null, memInputTest, memOutputTest, testCodelet);

        assertEquals(testCodelet, codeRack.getAllCodelets().get(0));
    }


    @Test
    public void destroyCodeletTest(){
        CodeRack codeRack = new CodeRack();
        List<Memory> memInputTest = Arrays.asList(new MemoryObject(), new MemoryObject());
        List<Memory> memOutputTest = Arrays.asList(new MemoryObject());

        codeRack.createCodelet(0.5, null, memInputTest, memOutputTest, testCodelet);

        codeRack.destroyCodelet(testCodelet);

        assertEquals(0, codeRack.getAllCodelets().size());
    }
    @Test
    public void startStopTest(){
        CodeRack codeRack = new CodeRack();
        List<Codelet> testList = Arrays.asList(testCodelet, otherCodelet);

        codeRack.setAllCodelets(testList);
        codeRack.start();
        assertTrue(codeRack.getAllCodelets().get(0).isLoop());

        codeRack.stop();
        assertFalse(codeRack.getAllCodelets().get(0).isLoop());
    }


}
