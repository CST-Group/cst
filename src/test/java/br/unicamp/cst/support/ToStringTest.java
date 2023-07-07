/***********************************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * K. Raizer, A. L. O. Paraense, E. M. Froes, R. R. Gudwin - initial API and implementation
 * **********************************************************************************************/
package br.unicamp.cst.support;
import java.util.Date;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author rgudwin
 */
public class ToStringTest {
    
    @Test
    public void testFrom() {
        Locale.setDefault(Locale.US);
        Object o = null;
        String s = ToString.from(o);
        assertEquals(s,"<NULL>");
        long l = 0l;
        s = ToString.from(l);
        assertEquals(s,"0");
        int i = 0;
        s = ToString.from(i);
        assertEquals(s,"0");
        float f = 0f;
        s = ToString.from(f);
        assertEquals(s,"0.00");
        double d = 0d;
        s = ToString.from(d);
        assertEquals(s,"0.00");
        byte by = 0;
        s = ToString.from(by);
        assertEquals(s,"0");
        boolean boo = true;
        s = ToString.from(boo);
        assertEquals(s,"true");
        boo = false;
        s = ToString.from(boo);
        assertEquals(s,"false");
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        s = ToString.from(date);
        assertEquals(s,TimeStamp.getStringTimeStamp(now,"dd/MM/yyyy HH:mm:ss.SSS"));
        o = new Object();
        s = ToString.from(o);
        assertEquals(s,null);
    }
    
    @Test
    public void testEl() {
        String s = ToString.el("Test",15); 
        assertEquals(s,"Test[15]");
    }
    
}
