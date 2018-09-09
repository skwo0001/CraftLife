package com.jostlingjacks.craftlife;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class HTTPDataHandler {
    private static final String BASE_URI = "https://monash-ie-dev.herokuapp.com/api/v1/";


    //public static JSONObject registration
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



}

