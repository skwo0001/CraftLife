package com.jostlingjacks.craftlife;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import static android.content.Context.MODE_PRIVATE;

public class NotificationReceiver extends BroadcastReceiver {

    private NotificationManager notificationManager;

    DataBaseHelper db;

    String toDoText;
    String fileName;
    ArrayList<String> toDoListArrayList;

    String notificationTitle;
    String email;
    String notificationDescription;
    String notificationArtAddress;

    @Override
    public void onReceive(Context context, Intent intent) {
        db = new DataBaseHelper(context);
        String addToToDoListMessage = intent.getStringExtra("addToToDoList");
        String yesResponseActionClicked = intent.getStringExtra("yesAction");
        String noResponseActionClicked = intent.getStringExtra("noAction");
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // if addToToDoListMessage is not null, then it is from the notification message...
        if (addToToDoListMessage != null) {
            // initialise the list
            toDoListArrayList = new ArrayList<>();
            addToToDoList(addToToDoListMessage, context);
            // when added to to-do list, show a message saying, yes added.
            Toast toast = Toast.makeText(context, "CraftLife: We just added this place to your Daily Planner", Toast.LENGTH_LONG);
            toast.show();

        } else if (yesResponseActionClicked != null){
            String response = yesResponseActionClicked; // "1"

            notificationTitle = intent.getStringExtra("title");
            notificationDescription = intent.getStringExtra("description");
            notificationArtAddress = intent.getStringExtra("address");
            email = intent.getStringExtra("email");
            if (notificationArtAddress != null) {
                String suggestionId = getNotificationId(email,notificationTitle,notificationArtAddress);
                db.updateOption(suggestionId, yesResponseActionClicked);
                    Toast toast = Toast.makeText(context, "CraftLife: We'll give you more related suggestions.", Toast.LENGTH_LONG);
                    toast.show();
            }

        } else if (noResponseActionClicked != null){

            String response = noResponseActionClicked; // "0"

            notificationTitle = intent.getStringExtra("title");
            notificationDescription = intent.getStringExtra("description");
            notificationArtAddress = intent.getStringExtra("address");
            email = intent.getStringExtra("email");
            if (notificationArtAddress != null) {
                String suggestionId = getNotificationId(email,notificationTitle,notificationArtAddress);
                db.updateOption(suggestionId, yesResponseActionClicked);
                Toast toast = Toast.makeText(context, "CraftLife: Thank you, We'll show less, and make suggestions more relevant.", Toast.LENGTH_LONG);
                toast.show();
            }
        }
//
//        // cancel this notification activity...
//        notificationManager.cancel(2);
//        notificationManager.cancel(3);

    }

    private void addToToDoList(String message, Context context){
        SharedPreferences logInPreferences = context.getSharedPreferences("CURRENT_USER_INFO", MODE_PRIVATE);
        fileName = logInPreferences.getString("CURRENT_USER_EMAIL", "") + "ToDo.txt";
        // load previous data into the arrayList.
        loadToDoListFromFile(context);
        writeNewItemToArrayList(toDoListArrayList, message);
        writeToFile(context, toDoListArrayList);
    }

    public void loadToDoListFromFile(Context context){
        try {
            Scanner sc = new Scanner(context.openFileInput(fileName));
            while (sc.hasNextLine()){
                String data = sc.nextLine();
                toDoListArrayList.add(data);
            }
            sc.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public void writeNewItemToArrayList(ArrayList<String> arrayList, String message){
        arrayList.add(message);
    }

    public void writeToFile(Context context, ArrayList<String> arrayList){
        try {
            PrintWriter printWriter = new PrintWriter(context.openFileOutput(fileName, MODE_PRIVATE));
            for (String data: arrayList){
                printWriter.println(data);
            }
            printWriter.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public String getNotificationId(String email, String title, String address){
        Cursor cursor = db.getSuggestionID(email,title,address);
        String s = "";
        if (cursor.moveToLast()){
            //return id
            s = cursor.getString(0);
        }
        return  s;
    }
}
