package com.jostlingjacks.craftlife;

import android.content.SharedPreferences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class Tool {

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
