package com.jostlingjacks.craftlife;

import android.content.SharedPreferences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

/*
This class is to store all the extra method which is used more than one class
 */

public class Tool {

    public final static int INTENT_REQUEST_CODE = 1;
    public final static String INTENT_TO_DO_MESSAGE_FIELD = "message_field";
    public final static int INTENT_RESULT_CODE = 1;
    public final static int INTENT_REQUEST_CODE_TWO = 2;
    public final static int INTENT_RESULT_CODE_TWO = 2;
    public final static String INTENT_TO_DO_MESSAGE_DATA = "message_data";
    public final static String INTENT_TO_DO_ITEM_POSITION = "item_position";
    public final static String INTENT_TO_DO_CHANGED_MESSAGE = "changed_message";


    public static int randomNumberGenerator(int upperBound) {
        Random rand = new Random();
        return rand.nextInt(upperBound) + 1;
    }

    public static String hashPw(String password){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(password.getBytes());

        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        final String pw = sb.toString();

        return pw;
    }


}
