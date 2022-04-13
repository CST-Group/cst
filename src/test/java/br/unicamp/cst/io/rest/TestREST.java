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
package br.unicamp.cst.io.rest;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.support.CodeletsProfiler;
import br.unicamp.cst.support.InterfaceAdapter;
import br.unicamp.cst.support.TimeStamp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author rgudwin
 */
public class TestREST {
    
    Mind m;
    
    public TestREST() {
        m = prepareMind();
    }
    
    void updateMemoryObject(MemoryObject mo) {
        double value = (double) mo.getI();
        mo.setI(value+0.01);
    }
    
    void updateMemoryContainer(MemoryContainer mc) {
        for (Memory mem : mc.getAllMemories()) {
            if (mem.getClass().getCanonicalName().equalsIgnoreCase("br.unicamp.cst.core.entities.MemoryObject")) {
                updateMemoryObject((MemoryObject)mem);
                //System.out.println("Updating subnode");
            }    
        }
    }

    void updateMind() {
        //System.out.println("Updating Mind");
        for (Memory mem : m.getRawMemory().getAllMemoryObjects()) {
            if (mem.getClass().getCanonicalName().equalsIgnoreCase("br.unicamp.cst.core.entities.MemoryObject")) {
                updateMemoryObject((MemoryObject)mem);
            }
            if (mem.getClass().getCanonicalName().equalsIgnoreCase("br.unicamp.cst.core.entities.MemoryContainer")) {
                updateMemoryContainer((MemoryContainer)mem);
            }
        }
        
    }
    
    public void StartTimer() {
        Timer t = new Timer();
        TestREST.mainTimerTask tt = new TestREST.mainTimerTask(this);
        t.scheduleAtFixedRate(tt, 0, 100);
    }

    public void tick() {
        if (m != null) {
            updateMind();
        } else {
            System.out.println("Mind is null");
        }
        //System.out.println("update");
    }
    
    class mainTimerTask extends TimerTask {

        TestREST wov;
        boolean enabled = true;

        public mainTimerTask(TestREST wovi) {
            wov = wovi;
        }

        public void run() {
            if (enabled) {
                wov.tick();
            }
        }

        public void setEnabled(boolean value) {
            enabled = value;
        }
    }
    
    
    public Mind prepareMind() {
        Mind m = new Mind();
        m.createCodeletGroup("Sensory");
        m.createCodeletGroup("Perception");
        m.createCodeletGroup("Behavioral");
        m.createCodeletGroup("Motivational");
        m.createCodeletGroup("Motor");
        
        MemoryObject m1 = m.createMemoryObject("M1", 1.12);
        MemoryObject m2 = m.createMemoryObject("M2", 2.32);
        MemoryObject m3 = m.createMemoryObject("M3", 3.44);
        MemoryObject m4 = m.createMemoryObject("M4", 4.52);
        MemoryObject m5 = m.createMemoryObject("M5", 5.12);
        MemoryContainer m6 = m.createMemoryContainer("C1");
        MemoryContainer m7 = m.createMemoryContainer("C2");
        int mc1 = m7.setI(7.55, 0.23);
        int mc2 = m6.setI(6.33, 0.22);
        int mc3 = m6.setI(6.12, 0.13);
        int mc4 = m6.add(m7);
        //System.out.println("Memories: "+mc1+" "+mc2+" "+mc3+" "+mc4);
        
        Codelet c = new TestCodelet("Sensor1");
        c.addInput(m1);
        c.addInput(m2);
        c.addOutput(m3);
        c.addOutput(m4);
        c.addBroadcast(m5);
        //c.setCodeletProfiler("profile/", "c.json", "Mind 1", 10, null, CodeletsProfiler.FileFormat.JSON);
        m.insertCodelet(c,"Sensory");
        Codelet c2 = new TestCodelet("Motor1");
        c2.addInput(m4);
        c2.addInput(m5);
        c2.addOutput(m6);
        c2.addOutput(m3);
        c2.addBroadcast(m5);
        //c2.setCodeletProfiler("profile/", "c2.json", "Mind 1", 10, null, CodeletsProfiler.FileFormat.JSON);
        c.setProfiling(true);
        m.insertCodelet(c2,"Motor");
        
        Codelet mot1 = new TestCodelet("Curiosity");
        mot1.addInput(m7);
        mot1.addOutput(m5);
        m.insertCodelet(mot1,"Motivational");
        Codelet mot2 = new TestCodelet("Fear");
        mot2.addInput(m3);
        mot2.addOutput(m4);
        try {mot2.setActivation(1.0);} catch(Exception e){}
        m.insertCodelet(mot2,"Motivational");
        Codelet mot3 = new TestCodelet("Anger");
        mot3.addInput(m1);
        mot3.addOutput(m2);
        try {mot3.setActivation(0.5);} catch(Exception e){}
        m.insertCodelet(mot3,"Motivational");
        m.start();
        return(m);
    }
    
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://localhost:4011/";
    
    private String sendGET() throws IOException {
                String message = "";
		URL obj = new URL(GET_URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
                if (responseCode != 200)
                    System.out.println("GET Response Code :: " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
                        message = response.toString();
		} else {
			System.out.println("GET request not worked");
		}
          return(message);
	}
    
    @Test 
    public void testRest() throws IOException {
        TestREST tr = new TestREST();
        tr.StartTimer();
    	RESTServer rs = new RESTServer(tr.m,4011,true);
        String mes;
        for (int i=0;i<10;i++) {
            mes = sendGET();
            Gson gson = new GsonBuilder().registerTypeAdapter(Memory.class, new InterfaceAdapter<MemoryObject>())
                             .registerTypeAdapter(Memory.class, new InterfaceAdapter<MemoryContainer>())
                             .setPrettyPrinting().create();
            MindJson mj = gson.fromJson(mes,MindJson.class);
            assertEquals(mj.memories.size(),7);
            for(int j=0;j<5;j++) {
                MemoryJson mm = mj.memories.get(j);
                String sname = "M"+(j+1);
                assertEquals(mm.name,sname);
            }
            MemoryJson c = mj.memories.get(5);
            assertEquals(c.name,"C1");
            c = mj.memories.get(6);
            assertEquals(c.name,"C2");
            assertEquals(mj.codelets.size(),5);
            String time = TimeStamp.getStringTimeStamp(mj.memories.get(0).timestamp,"dd/MM/YYYY HH:mm:ss.SSS zzz");
            System.out.println("i: "+i+" time: "+time+" memories: "+mj.memories.size()+" codelets: "+mj.codelets.size());
        }
    }
    
}
