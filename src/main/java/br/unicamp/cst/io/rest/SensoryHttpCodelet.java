package br.unicamp.cst.io.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SensoryHttpCodelet extends HttpCodelet {
    String URL;

    public SensoryHttpCodelet(String URL){
        super();
        this.URL = URL;
    }
    @Override
    public void accessMemoryObjects() {

    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {

    }

    public String sendGET(String GET_URL) throws IOException {
        String message = "";

        URL obj = new URL(GET_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        //con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode=0;
        try {
            responseCode = con.getResponseCode();
        } catch (java.net.ConnectException e) {
            e.printStackTrace();
        }
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
}
