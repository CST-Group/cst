package br.unicamp.cst.io.rest;

import br.unicamp.cst.core.entities.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;


public class TestHttpCodelet {
    Mind m1;
    Mind m2;

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
                String msg = this.sendGET("http://127.0.0.1:60000");
                System.out.println(msg);
                System.out.println("got from: " + "http://127.0.0.1:60000");
            }catch (Exception e){e.printStackTrace();}
        }
    };

    HttpCodelet restMotorTestCodelet = new HttpCodelet() {
        HashMap<String, Object> params = new HashMap<>();
        final Random r = new Random();
        final Double I = 2.0; //(double) (5 + r.nextInt(500));

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
            StringBuilder sbParams = new StringBuilder();



            Double eval = (double)(2 + r.nextInt(50));
            params.replace("I", I.toString());
            params.replace("evaluation", eval.toString());
            int i = 0;
            /*for (String key : params.keySet()) {
                try {
                    if (i != 0){
                        sbParams.append("&");
                    }
                    sbParams.append(key).append("=")
                            .append(URLEncoder.encode(params.get(key), "UTF-8"));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                i++;
            }*/
            try {
                //String paramsString = sbParams.toString();
                this.sendPOST("http://127.0.0.1:60000", params, null);
                System.out.println("send to: " + "http://127.0.0.1:60000");
            }catch (Exception e){e.printStackTrace();}
        }
    };

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
                    String msg = this.sendGET("http://127.0.0.1:60000");
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
                StringBuilder sbParams = new StringBuilder();



                Double eval = (double)(2 + r.nextInt(50));
                params.replace("I", I.toString());
                params.replace("evaluation", eval.toString());
                int i = 0;
                /*for (String key : params.keySet()) {
                    try {
                        if (i != 0){
                            sbParams.append("&");
                        }
                        sbParams.append(key).append("=")
                                .append(URLEncoder.encode(params.get(key), "UTF-8"));

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    i++;
                }*/
                try {
                    //String paramsString = sbParams.toString();
                    this.sendPOST("http://127.0.0.1:60000", params, null);
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

        RESTMemoryObject m1;
        if (baseIP.equals("localhost")){
            m1 = m.createRESTMemoryObject("M1", portIn);
        }
        else{
            m1 = m.createRESTMemoryObject("M1", baseIP, portIn);
        }
        m1.setI(1);


        MemoryObject m2 = m.createMemoryObject("M2", 2.0);
        MemoryObject m3 = m.createMemoryObject("M3", null);
        MemoryObject m4 = m.createMemoryObject("M4", null);

        RESTMemoryObject m5;
        if (baseIP.equals("localhost")){
            m5 = m.createRESTMemoryObject("M5", portOut);
        }
        else{
            m5 = m.createRESTMemoryObject("M5", baseIP, portOut);
        }
        m5.setI(toGetI);


        MemoryContainer m6 = m.createMemoryContainer("C1");
        MemoryContainer m7 = m.createMemoryContainer("C2");

        m7.setI(7.55, 0.23);
        m6.setI(6.33, 0.22);
        m6.setI(6.12, 0.13);
        m6.add(m7);

        //dummy
        RESTMemoryObject m8 = new RESTMemoryObject(portOut);
        m8.setId(1l);
        m8.setName("M8");
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
    public void testError() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("test", 2);
        try{Thread.sleep(2000);
        }catch (Exception e){e.printStackTrace();}

        Exception exception1 = assertThrows(ConnectException.class, () -> {
            restMotorTestCodelet.sendPOST("http://127.0.0.1:6000", params, null);
        });

        Exception exception2 = assertThrows(ConnectException.class, () -> {
            restMotorTestCodelet.sendGET("http://127.0.0.1:6000");
        });

    }

    @Test
    public void sendPOSTReturnsResponseWhenSuccessful() throws IOException {
        //HttpCodelet httpCodelet = new HttpCodelet();
        HashMap<String, Object> params = new HashMap<>();
        params.put("I", "value");
        params.put("Evaluation", 1.0);

        Mind mind = new Mind();
        Memory mo = mind.createRESTMemoryObject("M1","127.0.0.1", 60000);
        mind.getRawMemory().addMemory(mo);
        mind.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String response = restMotorTestCodelet.sendPOST("http://localhost:60000", params, "application/json");

        mind.shutDown();

        assertEquals("I: value, Evaluation: 1.0", response);
    }

    @Test
    public void sendPOSTThrowsExceptionWhenUnsuccessful() {
        //HttpCodelet httpCodelet = new HttpCodelet();
        HashMap<String, Object> params = new HashMap<>();
        params.put("test", "value");

        assertThrows(IOException.class, () -> restMotorTestCodelet.sendPOST("http://invalidurl", params, "application/json"));
    }

    /*@Test
    public void sendGETReturnsResponseWhenSuccessful() throws IOException {
        //HttpCodelet httpCodelet = new HttpCodelet();

        String response = restMotorTestCodelet.sendGET("http://localhost:8080");

        assertEquals("Expected Response", response);
    }

    @Test
    public void sendGETThrowsExceptionWhenUnsuccessful() {
        //HttpCodelet httpCodelet = new HttpCodelet();

        assertThrows(IOException.class, () -> restMotorTestCodelet.sendGET("http://invalidurl"));
    }*/


}
