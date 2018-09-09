package com.jostlingjacks.craftlife;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;


public class HTTPDataHandler {
    private static final String BASE_URI = "https://monash-ie-dev.herokuapp.com/api/v1/";
    private static final String BASE_URI_ITERATION1 = "https://monash-ie-dev.herokuapp.com";

    public static String getEventNotification (JSONObject jsonObject) {
        final String methodPath = "Event/";
        //initialise

        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";

        //Making HTTP request
        try {
            url = new URL(BASE_URI + methodPath);

            //open the connection
            conn = (HttpURLConnection) url.openConnection();

            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json"); //Read the response
            Scanner inStream = new Scanner(conn.getInputStream()); //read the input steream and store it as string
            while (inStream.hasNextLine())
            {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            conn.disconnect();
        }
        return textResult;
    }

    public static String getRegularNotification (JSONObject jsonObject) {
        final String methodPath = "Regular/";
        //initialise

        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";

        //Making HTTP request
        try {
            url = new URL(BASE_URI_ITERATION1 + methodPath);

            //open the connection
            conn = (HttpURLConnection) url.openConnection();

            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            //set the connection method to GET
            conn.setRequestMethod("POST");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json"); //Read the response
            Scanner inStream = new Scanner(conn.getInputStream()); //read the input steream and store it as string
            while (inStream.hasNextLine())
            {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            conn.disconnect();
        }
        return textResult;
    }

    public static void createUser(){
        String query = "{ \"email\": \"email_address@gmail.com\", \"password\": \"password\"}";
        URL url = null;
        HttpURLConnection connection = null;
        final String methodPath = "/auth/login/";

        try {
            Gson gson = new Gson();
            //String stringUserInfo = gson.toJson(userInfo);
            url = new URL(BASE_URI_ITERATION1 + methodPath);

            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            //connection.setFixedLengthStreamingMode(stringUserInfo.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json");

            connection.getOutputStream().write(query.getBytes("UTF8"));


        }catch (Exception e){
            Log.d("Exception Started:", "Exception Started");
            e.printStackTrace();
        }


        }




    // HTTP POST request
    private String sendPost() throws Exception {

        String url = "https://monash-ie-dev.herokuapp.com/";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "auth/login/";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        return response.toString();


    }
    }

