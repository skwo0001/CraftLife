package com.jostlingjacks.craftlife;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.GregorianCalendar;


public class NotificationDetailActivity extends AppCompatActivity {

    private Button button;
    private TextView titleTextView,  descTextView,  addressTextView,  timeTextView, urlTextView;
    private ImageView addressImg, timeImg, descImg, urlImg;
    private Button artEventOnMapButton;
    private ImageView actionImage, resultImg;
    private DataBaseHelper db;
    private LinearLayout resultLL, yesLL, noLL,questionTV,pullLL, descLL, addressLL, timeLL, urlLL ;

    // variables that can do add-to-do list related things...
    private String fileName;
    private ArrayList<String> toDoListArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);
        db = new DataBaseHelper(this);

        Intent intent = getIntent();
        Bundle details = intent.getExtras();

        //get the values from bundle
        final String title = details.getString("title");
        String description  = details.getString("description");
        String address = details.getString("address");
        String time = details.getString("time");
        String noti_id = details.getString("id");
        String url = details.getString("url");


        artEventOnMapButton = (Button) findViewById(R.id.art_event_on_map_button);
        button = (Button) findViewById(R.id.getbutton);
        titleTextView = (TextView) findViewById(R.id.noti_title);
        descImg = (ImageView) findViewById(R.id.noti_desc_img);
        descTextView = (TextView) findViewById(R.id.noti_desc);
        addressTextView = (TextView) findViewById(R.id.noti_add);
        timeTextView = (TextView) findViewById(R.id.noti_time);
        addressImg = (ImageView) findViewById(R.id.addressImg);
        timeImg = (ImageView) findViewById(R.id.timeImg);
        actionImage = (ImageView) findViewById(R.id.imageAction);
        actionImage.setImageResource(R.drawable.app_logo);
        questionTV = (LinearLayout) findViewById(R.id.question);
        yesLL = (LinearLayout) findViewById(R.id.yesImg);
        noLL = (LinearLayout) findViewById(R.id.noImg);
        resultImg = (ImageView) findViewById(R.id.userrespond);
        resultLL = (LinearLayout) findViewById(R.id.resultLL);
        pullLL = (LinearLayout) findViewById(R.id.pull);
        descLL = (LinearLayout) findViewById(R.id.descLL);
        addressLL = (LinearLayout) findViewById(R.id.addressLL);
        timeLL = (LinearLayout) findViewById(R.id.timeLL);
        urlLL = (LinearLayout) findViewById(R.id.urlLL);
        urlTextView = (TextView) findViewById(R.id.urlTV);
        urlImg = (ImageView) findViewById(R.id.urlImg);

        //get the user email
        SharedPreferences userInfoSharedPreferences = getSharedPreferences("CURRENT_USER_INFO", MODE_PRIVATE);
        final String emailAddress = userInfoSharedPreferences.getString("CURRENT_USER_EMAIL", "");

        final String id;
        String option;
        if (noti_id == null){
            //Check the respond of the user to this suggestion
            String optionId = readRespond(emailAddress, title);
            String[] result = optionId.split("#");
            id = result[0];
            option = result[1];
        } else {
            id = noti_id;
            option = readOptionById(noti_id);
            if (option == null) {
                option = "null";
            }
        }

        // set the image of the response received from user
        if (option.contains("1")) {
            resultImg.setImageResource(R.drawable.good);
        }
        if (option.contains("0")) {
            resultImg.setImageResource(R.drawable.dislike);
        }
        yesLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.updateOption(id, "1");
                resultLL.setVisibility(View.VISIBLE);
                resultImg.setImageResource(R.drawable.good);
            }
        });
        noLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.updateOption(id, "0");
                resultLL.setVisibility(View.VISIBLE);
                resultImg.setImageResource(R.drawable.dislike);
            }
        });

        //set the text of the title, description and the address
        titleTextView.setText(title);
        descTextView.setText(description);
        addressTextView.setText(address);
        timeTextView.setText(time);
        urlTextView.setText(url);

        if (time == null || time.contains("null")){
            timeLL.setVisibility(View.GONE);
            urlLL.setVisibility(View.GONE);
            artEventOnMapButton.setVisibility(View.VISIBLE);
            // set the image to different type of suggestion
            if (description.toLowerCase().contains("gallery"))
            {
                actionImage.setImageResource(R.drawable.gallery);
            } else if (description.toLowerCase().contains("concert")){
                actionImage.setImageResource(R.drawable.stage);
            }else if (description.toLowerCase().contains("art")){
                int i = Tool.randomNumberGenerator(100);
                if (i % 2 == 0){
                    actionImage.setImageResource(R.drawable.art);
                } else {
                    actionImage.setImageResource(R.drawable.painter);
                }
                actionImage.setImageResource(R.drawable.art);
            } else if (description.toLowerCase().contains("fountain")){
                actionImage.setImageResource(R.drawable.fountains_2);
            } else if (description.toLowerCase().contains("monument")){
                actionImage.setImageResource(R.drawable.history);
            }else if (description.toLowerCase().contains("theatre")){
                actionImage.setImageResource(R.drawable.theatre);
            }else if (description.toLowerCase().contains("garden")){
                actionImage.setImageResource(R.drawable.park);
            }else if (description.toLowerCase().contains("facility") || title.toLowerCase().contains("health")){
                actionImage.setImageResource(R.drawable.exercise);
            }
        }

        if ( address == null || address.contains("null")){
            descLL.setVisibility(View.GONE);
            addressLL.setVisibility(View.GONE);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        artEventOnMapButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                goToMapActivity();
            }
        });

    }

    public void goBack() {
        Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
        finish();
        //moveTaskToBack(true);
    }

    public void goToMapActivity(){
        Intent intent = new Intent(this, MapActivity.class);

        // have to make sure that don't give the intent an null value to prevent the app crashing...
        if (getAddress() != null) {
            intent.putExtra("ArtAddress", getAddress());
            intent.putExtra("ArtName", getName());
            intent.putExtra("ArtDescription", getDescription());
        } else {
            intent.putExtra("ArtAddress", "U1 52 Panorama St Clayton VIC");  // the address is for testing...
        }

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            String shareSub = "Activity suggested by CraftLife";
            String shareBody = "";
            String type = getNotiType();

            if (getNotiType().contains("event")){
                shareBody = "Hey! There is an event called " + getName() + " on " + getDate() + ". Would you like to come with me?";

            }else {
            shareBody = "Hey! " + getDescription() + " in " + getAddress() + ". Would you like to come with me?";}

            myIntent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
            myIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
            startActivity(Intent.createChooser(myIntent, "Share using"));

            return true;
        }

        if (id == R.id.action_addtodo) {
            /**
             * TODO: Oliver please update here to add the things to to-do list
             * you can just call the getNotiTitle() and getMapEntryDataFromDatabase() to have the value of title and address
             * Done...
             */
            toDoListArrayList = new ArrayList<>();

            if (getNotiType().contains("event")){
                addToToDoList("Go " + getName().toLowerCase()
                        + " on " + getDate() + ".", this);

            }else {
                addToToDoList("Go " + getName().toLowerCase()
                        + " at " + getAddress() + ".", this);}
        }

        if (id == R.id.action_addtocalendar){
            Intent calIntent = new Intent(Intent.ACTION_INSERT);
            calIntent.setType("vnd.android.cursor.item/event");
            calIntent.putExtra(CalendarContract.Events.TITLE,getName());
            if (getType().contains("location")){
                calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION,getAddress());
                calIntent.putExtra(CalendarContract.Events.DESCRIPTION,getDescription());
            }
            if(getType().contains("event")){
                calIntent.putExtra(CalendarContract.Events.DESCRIPTION,getUrl());
                String time = getDate();
                String[] details = time.split("-");
                int year = Integer.parseInt(details[0]);
                int month = Integer.parseInt(details[1]);
                String daytime = details[2];
                String[] daytimelist = daytime.split(" ");
                int day = Integer.parseInt(daytimelist[0]);
                String hourNmin = daytimelist[1];
                String[] min = hourNmin.split(":");
                int hour = Integer.parseInt(min[0]);
                int minutes = Integer.parseInt(min[1]);

                GregorianCalendar calDate = new GregorianCalendar(year,month-1,day,hour,minutes);
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY,false);
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,calDate.getTimeInMillis());
                GregorianCalendar calDate2 = new GregorianCalendar(year,month-1,day,hour+2,minutes);
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calDate2.getTimeInMillis());
            }



            startActivity(calIntent);

        }

        return super.onOptionsItemSelected(item);
    }

    private void addToToDoList(String message, Context context){
        SharedPreferences logInPreferences = context.getSharedPreferences("CURRENT_USER_INFO", MODE_PRIVATE);
        fileName = logInPreferences.getString("CURRENT_USER_EMAIL", "") + "ToDo.txt";
        // load previous data into the arrayList.
        loadToDoListFromFile(context);

        // check existence of existing item..
        if (checkSameItemInTheToDoList(toDoListArrayList, message) ){
            // has same item
            Toast toast = Toast.makeText(this, "You have a same item in the Daily Planner haven't finished yet.", Toast.LENGTH_LONG);
            toast.show();
        } else {
            // does not have same item...
            writeNewItemToArrayList(toDoListArrayList, message);
            writeToFile(context, toDoListArrayList);
            Toast toast = Toast.makeText(this, "CraftLife: We just added this place to your Daily Planner", Toast.LENGTH_LONG);
            toast.show();
        }
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

    public boolean checkSameItemInTheToDoList(ArrayList<String> arrayListToBeChecked, String wordsToBeChecked){
        for (String toDoListItem: arrayListToBeChecked) {
            if (toDoListItem.equals(wordsToBeChecked)){
                return true;
            }
        }
        return false;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getName(){
        Intent intent = getIntent();
        Bundle details = intent.getExtras();

        return details.getString("title");
    }

    public String getNotiType(){
        Intent intent = getIntent();
        Bundle details = intent.getExtras();

        return details.getString("type");
    }

    public String getAddress(){
        Intent intent = getIntent();
        Bundle details = intent.getExtras();

        return details.getString("address");
    }

    public String getDescription(){
        Intent intent = getIntent();
        Bundle details = intent.getExtras();

        return details.getString("description");
    }

    public String getDate(){
        Intent intent = getIntent();
        Bundle details = intent.getExtras();

        String time = details.getString("time");

        return time;
    }

    public String getType(){
        Intent intent = getIntent();
        Bundle details = intent.getExtras();

        return details.getString("type");
    }

    public String getUrl(){
        Intent intent = getIntent();
        Bundle details = intent.getExtras();

        return details.getString("url");
    }

    public String readRespond(String email, String title){
        Cursor c = db.getOptions(email,title);
        String s = "";
        if (c.moveToLast()){
            //return title,details,address and time
            s = c.getString(0)+ "#" + c.getString(1);
        }
        return  s;
    }

    public String readOptionById(String id){
        Cursor c = db.getOptionsById(id);
        String s = "";
        if (c.moveToLast()){
            //return title,details,address and time
            s = c.getString(0);
        }
        return s;
    }

}
