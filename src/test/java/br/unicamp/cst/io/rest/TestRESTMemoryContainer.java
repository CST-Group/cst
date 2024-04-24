package br.unicamp.cst.io.rest;

import br.unicamp.cst.core.entities.*;
import java.io.BufferedReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Random;
import java.net.InetAddress;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestRESTMemoryContainer {
    Mind m1;
    Mind m2;

    public Mind prepareMind(int portOut, int portIn, int partnerPortOut, int partnerPortIn, double outI, double toGetI, String baseIP) {
        String baseURL = "http://" + baseIP + ":";

        String partnerURLOut = baseURL + partnerPortOut+"/";
        String partnerURLIn = baseURL + partnerPortIn+"/";

        HttpCodelet restSensoryTestCodelet = new HttpCodelet() {
            //Memory
            @Override
            public void accessMemoryObjects() {

            }

            @Override
            public void calculateActivation() {

            }

            @Override
            public void proc() {
                try {
                    String msg = this.sendGET(partnerURLOut);
                    System.out.println(msg);
                    System.out.println("got from: " + partnerURLOut);
                }catch (Exception e){e.printStackTrace();}
            }
        };

        HttpCodelet restMotorTestCodelet = new HttpCodelet() {
            HashMap<String, Object> params = new HashMap<>();
            final Random r = new Random();
            final Double I = outI; //(double) (5 + r.nextInt(500));

            @Override
            public void accessMemoryObjects() {
                params.put("I", "2.0");
                params.put("evaluation", "3.0");
            }

            @Override
            public void calculateActivation() {

            }

            @Override
            public void proc() {

                Double eval = (double)(2 + r.nextInt(50));
                params.replace("I", I.toString());
                params.replace("evaluation", eval.toString());

                //String paramsString = prepareParams(params);
                try {
                    this.sendPOST(partnerURLIn, params, null);
                    System.out.println("send to: " + partnerURLIn);
                }catch (Exception e){e.printStackTrace();}
            }
        };

        Codelet interCodelet = new Codelet() {
            Memory in;
            Memory out;
            @Override
            public void accessMemoryObjects() {
                if(in == null){
                    this.in = this.getInput("M3");
                }
                if(out == null){
                    this.out = this.getOutput("M4");
                }
            }

            @Override
            public void calculateActivation() {}

            @Override
            public void proc() {
                Object testNum = in.getI();
                if (testNum != null){
                    out.setI((double)testNum);
                }
            }
        };


        Mind m = new Mind();
        //RESTMemory m1 = m.createRESTMemoryObject("M1", baseIP, portIn);
        //m1.setI(1);

        RESTMemoryContainer m1;
        if (baseIP.equals("localhost")){
            m1 = m.createRESTMemoryContainer("M1", portIn);
        }
        else{
            m1 = m.createRESTMemoryContainer("M1", baseIP, portIn);
        }
        m1.setI(1);


        MemoryObject m2 = m.createMemoryObject("M2", 2.0);
        MemoryObject m3 = m.createMemoryObject("M3", null);
        MemoryObject m4 = m.createMemoryObject("M4", null);

        RESTMemoryContainer m5;
        if (baseIP.equals("localhost")){
            m5 = m.createRESTMemoryContainer("M5", portOut);
        }
        else{
            m5 = m.createRESTMemoryContainer("M5", baseIP, portOut);
        }
        m5.setI(toGetI);


        MemoryContainer m6 = m.createMemoryContainer("C1");
        MemoryContainer m7 = m.createMemoryContainer("C2");

        m7.setI(7.55, 0.23);
        m6.setI(6.33, 0.22);
        m6.setI(6.12, 0.13);
        m6.add(m7);

        //dummy
        String hostname = "";
        try {hostname = InetAddress.getLocalHost().getHostName();} catch (Exception e){}
        RESTMemoryContainer m8 = new RESTMemoryContainer(hostname,portOut+1);
        //m8.setIdmemoryobject(2l);
        m8.setName("M8");
        m8.setI("2");
        m.getRawMemory().addMemory(m8);

        //REST Sensory that will use GET on another agent motor memory
        restSensoryTestCodelet.addInput(m1);
        restSensoryTestCodelet.addOutput(m2);
        restSensoryTestCodelet.setProfiling(true);
        m.insertCodelet(restSensoryTestCodelet);

        // simple "pass" Codelet
        interCodelet.addInput(m3);
        interCodelet.addOutput(m4);
        m.insertCodelet(interCodelet);

        // simple motor that writes on a memory that will listen to GET methods
        Codelet c2 = new TestCodelet("Motor1");
        c2.addInput(m4);
        c2.addOutput(m5);
        m.insertCodelet(c2);

        // REST Motor Codelet that will write on another agent Sensory Memory
        restMotorTestCodelet.addInput(m4);
        //sends to partner url in
        m.insertCodelet(restMotorTestCodelet);


        return(m);
    }


    @Test
    public void testRestHostname() throws IOException {
        //String baseIP = "192.xxx.xxx.x";
        //String baseIP = "172.xx.x.x";
        String baseIP = "127.0.0.1";
        //String baseIP = "localhost";
        try {baseIP = InetAddress.getLocalHost().getHostAddress();} catch (Exception e){}

        Random r = new Random();
        // Finding a random port higher than 5000
        int portIn1 = 5000 + r.nextInt(50000);
        int portIn2 = 5000 + r.nextInt(50000);
        int portOut1 = 5000 + r.nextInt(50000);
        int portOut2 = 5000 + r.nextInt(50000);

        TestRESTMemoryContainer tr = new TestRESTMemoryContainer();
        double outI1 = (5 + r.nextInt(500));
        double toGetI1 = (5 + r.nextInt(500));
        double outI2 = (5 + r.nextInt(500));
        double toGetI2 =(5 + r.nextInt(500));

        tr.m1 = prepareMind(portOut1, portIn1, portOut2, portIn2, outI1, toGetI1, baseIP);
        tr.m2 = prepareMind(portOut2, portIn2, portOut1, portIn1, outI2, toGetI2, baseIP);

        tr.m1.start();
        tr.m2.start();

        try{Thread.sleep(2000);
        }catch (Exception e){e.printStackTrace();}

        assertEquals(tr.m1.getRawMemory().getAllOfType("M5").get(0).getI(), toGetI1);
        assertEquals(Double.parseDouble((String) tr.m2.getRawMemory().getAllOfType("M1").get(0).getI()), outI1, 0.0);

        assertEquals(tr.m2.getRawMemory().getAllOfType("M5").get(0).getI(), toGetI2);
        assertEquals(Double.parseDouble((String) tr.m1.getRawMemory().getAllOfType("M1").get(0).getI()), outI2, 0.0);
    }

    @Test
    public void testRestLocalhost() throws IOException {
        String baseIP = "localhost";

        Random r = new Random();
        // Finding a random port higher than 5000
        int portIn1 = 5000 + r.nextInt(50000);
        int portIn2 = 5000 + r.nextInt(50000);
        int portOut1 = 5000 + r.nextInt(50000);
        int portOut2 = 5000 + r.nextInt(50000);

        TestRESTMemoryContainer tr = new TestRESTMemoryContainer();
        double outI1 = (5 + r.nextInt(500));
        double toGetI1 = (5 + r.nextInt(500));
        double outI2 = (5 + r.nextInt(500));
        double toGetI2 =(5 + r.nextInt(500));

        tr.m1 = prepareMind(portOut1, portIn1, portOut2, portIn2, outI1, toGetI1, "localhost");
        tr.m2 = prepareMind(portOut2, portIn2, portOut1, portIn1, outI2, toGetI2, "localhost");

        tr.m1.start();
        tr.m2.start();

        MemoryObject m = new MemoryObject();
        m.setName("testName");
        MemoryContainer memoryContainer = new MemoryContainer("anon");
        memoryContainer.add(m);
        MemoryContainerJson memoryContainerJson = new MemoryContainerJson(memoryContainer, "group");
        
        try{Thread.sleep(2000);
        }catch (Exception e){e.printStackTrace();}

        assertEquals(tr.m1.getRawMemory().getAllOfType("M5").get(0).getI(), toGetI1);
        assertEquals(Double.parseDouble((String) tr.m2.getRawMemory().getAllOfType("M1").get(0).getI()), outI1, 0.0);

        assertEquals(tr.m2.getRawMemory().getAllOfType("M5").get(0).getI(), toGetI2);
        assertEquals(Double.parseDouble((String) tr.m1.getRawMemory().getAllOfType("M1").get(0).getI()), outI2, 0.0);

        RESTMemoryContainer m8 = (RESTMemoryContainer) tr.m2.getRawMemory().getAllOfType("M8").get(0);
        //assertEquals(m8.getIdmemoryobject(), 2l);

        assertEquals(memoryContainerJson.memories.get(0).name, m.getName());

    }
    
    private String sendGET(String GET_URL) {
                String message = "";
                int responseCode=0;
                HttpURLConnection con=null;
                try {
                    URL obj = new URL(GET_URL);
                    con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    //con.setRequestProperty("User-Agent", USER_AGENT);
                    responseCode = con.getResponseCode();
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
			//System.out.println(con.getResponseMessage());
                        return(con.getResponseMessage());
                    }
                } catch (java.net.ConnectException e) {
                    return("Connection refused");
                } catch (Exception e) {
                    e.printStackTrace();
                }
          return(message);
	}
    
    @Test
    public void testConstructors() {
        String ret;
        RESTMemoryContainer mc1 = new RESTMemoryContainer(10000);
        mc1.setI(252);
        RESTMemoryContainer mc2 = new RESTMemoryContainer(10001, true);
        mc2.setI(253);
        RESTMemoryContainer mc3 = new RESTMemoryContainer(10002, true, "localhost"); 
        mc3.setI(254);
        do {
          ret = sendGET("http://localhost:10000");
        } while (ret.equalsIgnoreCase("Connection refused"));   
        System.out.println("mc1: "+ret);
        do {
          ret = sendGET("http://localhost:10001");
        } while (ret.equalsIgnoreCase("Connection refused"));   
        System.out.println("mc2: "+ret);
        do {
          ret = sendGET("http://localhost:10002");
        } while (ret.equalsIgnoreCase("Connection refused"));     
        System.out.println("mc3: "+ret);
        RESTMemoryObject mo1 = new RESTMemoryObject(10003);
        mo1.setI(252);
        RESTMemoryObject mo2 = new RESTMemoryObject(10004, true);
        mo2.setI(253);
        RESTMemoryObject mo3 = new RESTMemoryObject(10005, true, "localhost"); 
        mo3.setI(254);
        do {
            ret = sendGET("http://localhost:10003");
        } while (ret.equalsIgnoreCase("Connection refused"));       
        System.out.println("mo1: "+ret);
        do {
            ret = sendGET("http://localhost:10004");
        } while (ret.equalsIgnoreCase("Connection refused"));   
        System.out.println("mo2: "+ret);
        do {
           ret = sendGET("http://localhost:10005");
        } while (ret.equalsIgnoreCase("Connection refused"));      
        System.out.println("mo3: "+ret);
        
    }
    
    @Test
    public void testRawMemorycreateRESTMemories() {
        String ret;
        RawMemory rm = new RawMemory();
        RESTMemoryObject rmo = rm.createRESTMemoryObject("testmo",10006);
        rmo.setI(255);
        do {
           ret = sendGET("http://localhost:10006");
        } while (ret.equalsIgnoreCase("Connection refused"));   
        System.out.println("mo: "+ret);
        RESTMemoryContainer rmc = rm.createRESTMemoryContainer("testmc",10007);
        rmc.setI(256);
        do {
            ret = sendGET("http://localhost:10007");
        } while (ret.equalsIgnoreCase("Connection refused"));
        System.out.println("mc: "+ret);
    }


}
