package com.jostlingjacks.craftlife;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class HTTPDataHandler {
    private static final String BASE_URI = "https://monash-ie-dev.herokuapp.com/api/v1/";
    private static final String ITERATION_BASE_URI = "https://letian-bucket-test.herokuapp.com/";


    public static String loginUser (UserInfo userInfo) {
        String responseString = "";
        URL url = null;
        HttpURLConnection conn = null;
        final String methodPath = "auth/login" ;
        try{
            Gson gson = new Gson();
            String stringUserInfo = gson.toJson(userInfo);
            url = new URL(ITERATION_BASE_URI + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            conn.setRequestProperty("Content-Type", "application/json");
            //stringUserInfo = "{ \"email\": \"email_address@gmail.com\", \"password\": \"password\"}";
            conn.setFixedLengthStreamingMode(stringUserInfo.getBytes().length);

            conn.getOutputStream().write(stringUserInfo.getBytes("UTF8"));

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_ACCEPTED || responseCode == HttpURLConnection.HTTP_OK) {
                //get the token back
                Scanner inStream = new Scanner(conn.getInputStream()); //read the input stream and store it as string
                while (inStream.hasNextLine())
                {
                    responseString += inStream.nextLine();
                }
            }

            Log.i("errorrrrrrrr",new Integer(conn.getResponseCode()).toString());

        }catch (Exception e){
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return responseString;
    }

    public static String signUpUser (UserInfo userInfo){
        //initialise
        String responseString = "";
        URL url = null;
        HttpURLConnection conn = null;
        final String methodPath="auth/register";
        try {
            Gson gson =new Gson();
            String stringUserInfo=gson.toJson(userInfo);
            url = new URL(ITERATION_BASE_URI + methodPath);
            //open the connection
            conn = (HttpURLConnection) url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to POST
            conn.setRequestMethod("POST"); //set the output to true
            conn.setDoOutput(true);
            //set length of the data you want to send


           // conn.setFixedLengthStreamingMode(stringUserInfo.getBytes().length + 2); //add HTTP headers
            conn.setFixedLengthStreamingMode(61);


            conn.setRequestProperty("Content-Type", "application/json");
            //Send the POST out

            stringUserInfo = "{ \"email\": \"email_address@gmail.com\", \"password\": \"password\"}";
//            PrintWriter out= new PrintWriter(conn.getOutputStream());
//            out.print(stringUserInfo);
//            out.close();
            conn.getOutputStream().write(stringUserInfo.getBytes("UTF8"));

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_ACCEPTED) {
                //get the token back
                Scanner inStream = new Scanner(conn.getInputStream()); //read the input stream and store it as string
                while (inStream.hasNextLine())
                {
                    responseString += inStream.nextLine();
                }
            }

            Log.i("error",new Integer(conn.getResponseCode()).toString());

        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();

        } finally {
            conn.disconnect();
        }

        return responseString;
    }

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
            Scanner inStream = new Scanner(conn.getInputStream()); //read the input stream and store it as string
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

