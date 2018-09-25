package com.jostlingjacks.craftlife;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import static android.content.Context.MODE_PRIVATE;

public class NotificationReceiver extends BroadcastReceiver {

    private NotificationManager notificationManager;

    String toDoText;
    String fileName;
    ArrayList<String> toDoListArrayList;

    String notificationTitle;
    String notificationDescription;
    String notificationArtAddress;

    @Override
    public void onReceive(Context context, Intent intent) {
        String addToToDoListMessage = intent.getStringExtra("addToToDoList");
        String yesResponseActionClicked = intent.getStringExtra("yesAction");
        String noResponseActionClicked = intent.getStringExtra("noAction");
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // if addToToDoListMessage is not null, then it is from the notification message...
        if (addToToDoListMessage != null) {
            // initialise the list
            toDoListArrayList = new ArrayList<>();
            addToToDoList(addToToDoListMessage, context);
        } else if (yesResponseActionClicked != null){
            /**
             * TODO: Joanna please update here to receive the response. when the user clicked the yes option.
             * TODO: Help me to get Frank! He's mine!
             * yesResponseActionClicked variable here would be "1"
             */
            String response = yesResponseActionClicked; // "1"
            /**
             * TODo: also get the data you want from below:
             */
            notificationTitle = intent.getStringExtra("title");
            notificationDescription = intent.getStringExtra("description");
            notificationArtAddress = intent.getStringExtra("address");
        } else if (noResponseActionClicked != null){
            /**
             * TODO: Joanna please update here to receive the response, when the user clicked the no option.
             * noResponseActionClicked variable here would be "0"
             */
            String response = noResponseActionClicked; // "0"
            /**
             * TODo: also get the data you want from below:
             */
            notificationTitle = intent.getStringExtra("title");
            notificationDescription = intent.getStringExtra("description");
            notificationArtAddress = intent.getStringExtra("address");
        }

        // cancel this notification activity...
        notificationManager.cancel(1);
        notificationManager.cancel(2);

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
}
