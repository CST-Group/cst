package br.unicamp.cst.io.rest;

import br.unicamp.cst.core.entities.Codelet;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public abstract class HttpCodelet extends Codelet {
    
    private static final String APPLICATION_JSON = "application/json";

    public String sendPOST(String POST_URL, String POST_PARAMS, String method) throws IOException {
        URL obj = new URL(POST_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");

        //"application/json"
        if(method != null){con.setRequestProperty("Content-Type", method);}

        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(POST_PARAMS.getBytes());
        os.flush();
        os.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }

        return null;
    }

    public String sendGET(String GET_URL) throws IOException {
        String message = "";

        URL obj = new URL(GET_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode=0;

        responseCode = con.getResponseCode();

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
        }
        return(message);
    }

    public String prepareParams(HashMap<String, Object> params){
        StringBuilder sbParams = new StringBuilder();

        int i = 0;
        for (String key : params.keySet()) {
            try {
                if (i != 0){
                    sbParams.append("&");
                }
                sbParams.append(key).append("=")
                        .append(URLEncoder.encode(params.get(key).toString(), "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i++;
        }
        return sbParams.toString();
    }

    public String sendPOST(String POST_URL, HashMap<String, Object> params, String method) throws IOException {
        if(method == null){
            String paramsString = prepareParams(params);
            return sendPOSTForm(POST_URL,  paramsString);
        }
        else if (method.equals(APPLICATION_JSON)){
            String payload = IdeaToJSON(params);
            return sendPOSTJSON(POST_URL,  payload);
        }
        else {
            String paramsString = prepareParams(params);
            return sendPOSTForm(POST_URL,  paramsString);
        }
    }


    public String sendPOSTForm(String POST_URL, String POST_PARAMS) throws IOException {
        URL obj = new URL(POST_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        // For POST only - START
        con.setDoOutput(true);

        {OutputStream os = con.getOutputStream();
            byte[] input = POST_PARAMS.getBytes(StandardCharsets.UTF_8);
            os.write(input);
            os.flush();
            os.close();
        }
        // For POST only - END

        int responseCode = con.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }

        return "POST Response Code :: " + responseCode;
    }

    public String sendPOSTJSON(String POST_URL, String payload) throws IOException {
        String method = APPLICATION_JSON;
        URL obj = new URL(POST_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        // For POST only - START
        con.setDoOutput(true);

        con.setRequestProperty("Content-Type", method);
        con.setRequestProperty("mimetype", method);

        con.setRequestProperty("Accept", APPLICATION_JSON);
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = payload.getBytes(StandardCharsets.UTF_8);
            os.write(input);
            os.flush();
        }
        // For POST only - END

        int responseCode = con.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }
        return "POST Response Code :: " + responseCode;
    }

    public String IdeaToJSON(HashMap<String, Object> idea){
        JSONObject json = new JSONObject(idea);
        return json.toString();
    }

}
