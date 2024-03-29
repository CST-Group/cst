/***********************************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * K. Raizer, A. L. O. Paraense, E. M. Froes, R. R. Gudwin - initial API and implementation
 ***********************************************************************************************/
package br.unicamp.cst.core.entities;

import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


/**
 * @author wander
 *
 */
public class CoalitionTest {
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
    public void calculateActivationTest(){
        Coalition coalition = new Coalition(Arrays.asList(testCodelet, otherCodelet));
        try {
            coalition.getCodeletsList().get(0).setActivation(1.0);
        } catch (CodeletActivationBoundsException e) {
            e.printStackTrace();
        }

        assertEquals(0.5, coalition.calculateActivation(), 0);
    }

    @Test
    public void setCodeletListTest(){
        Coalition coalition = new Coalition(Arrays.asList(testCodelet));

        List<Codelet> listTest = Arrays.asList(testCodelet, otherCodelet);
        coalition.setCodeletsList(listTest);

        assertEquals(listTest, coalition.getCodeletsList());
    }

    @Test
    public void activationTest(){
        Coalition coalition = new Coalition(Arrays.asList(testCodelet));

        Double activationTest = 0.8;
        coalition.setActivation(activationTest);

        assertEquals(0.8, coalition.getActivation(), 0);
    }

    @Test
    public void toStringTest(){
        List<Codelet> listTest = Arrays.asList(testCodelet, otherCodelet);
        Coalition coalition = new Coalition(Arrays.asList(testCodelet, otherCodelet));
        coalition.setActivation(0.8);
        String expectMessage = "Coalition [activation=" + 0.8 + ", " + ("codeletsList=" + listTest) + "]";

        assertTrue(expectMessage.contains(coalition.toString()));
    }

}
