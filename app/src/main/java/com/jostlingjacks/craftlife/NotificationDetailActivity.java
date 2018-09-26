package com.jostlingjacks.craftlife;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class NotificationDetailActivity extends AppCompatActivity {

    private Button button;
    private TextView titleTextView,  descTextView,  addressTextView,  timeTextView;
    private ImageView addressImg, timeImg;
    private Button artEventOnMapButton;
    private ImageView actionImage, resultImg;
    private DataBaseHelper db;
    private LinearLayout resultLL, yesLL, noLL,questionTV,pullLL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);
        db = new DataBaseHelper(this);

        Intent intent = getIntent();
        Bundle details = intent.getExtras();

        String type = details.getString("type");
        final String title = details.getString("title");
        String description  = details.getString("description");
        String address = details.getString("address");
        String lat = details.getString("lat");
        String lon = details.getString("lon");
        String time = details.getString("time");

        artEventOnMapButton = (Button) findViewById(R.id.art_event_on_map_button);
        button = (Button) findViewById(R.id.getbutton);
        titleTextView = (TextView) findViewById(R.id.noti_title);
        descTextView = (TextView) findViewById(R.id.noti_desc);
        addressTextView = (TextView) findViewById(R.id.noti_add);
        timeTextView = (TextView) findViewById(R.id.noti_time);
        addressImg = (ImageView) findViewById(R.id.addressImg);
        timeImg = (ImageView) findViewById(R.id.timeImg);
        actionImage = (ImageView) findViewById(R.id.imageAction);
        actionImage.setImageResource(R.drawable.event);
        questionTV = (LinearLayout) findViewById(R.id.question);
        yesLL = (LinearLayout) findViewById(R.id.yesImg);
        noLL = (LinearLayout) findViewById(R.id.noImg);
        resultImg = (ImageView) findViewById(R.id.userrespond);
        resultLL = (LinearLayout) findViewById(R.id.resultLL);
        pullLL = (LinearLayout) findViewById(R.id.pull);

        //get the user email
        SharedPreferences userInfoSharedPreferences = this.getSharedPreferences("REGISTER_PREFERENCES", MODE_PRIVATE);
        final String emailAddress = userInfoSharedPreferences.getString("UserEmailAddress", "");

        //Check the respond of the user to this suggestion
        String optionId = readRespond(emailAddress,title);

        String[] result = optionId.split("#");
        final String id = result[0];
        String option = result[1];

        if (option.contains("1") || option.contains("0")){
            questionTV.setVisibility(View.GONE);
            pullLL.setVisibility(View.GONE);
            if (option.contains("1")){
                resultImg.setImageResource(R.drawable.good);
            }
        } else {
            resultLL.setVisibility(View.GONE);
            yesLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.updateOption(id,"1");
                    questionTV.setVisibility(View.GONE);
                    pullLL.setVisibility(View.GONE);
                    resultLL.setVisibility(View.VISIBLE);
                    resultImg.setImageResource(R.drawable.good);
                }

            });

            noLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.updateOption(id,"0");
                    questionTV.setVisibility(View.GONE);
                    pullLL.setVisibility(View.GONE);
                    resultLL.setVisibility(View.VISIBLE);
                    resultImg.setImageResource(R.drawable.dislike);
                }

            });

        }

        //Format the time
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
        TemporalAccessor accessor = dateTimeFormatter.parse(time);
        Date date = Date.from(Instant.from(accessor));
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm");
        time = sdf.format(date);

        timeTextView.setText(time);

        addressImg.setVisibility(View.VISIBLE);
        artEventOnMapButton.setVisibility(View.VISIBLE);
        timeTextView.setVisibility(View.VISIBLE);
        timeImg.setVisibility(View.VISIBLE);


        // set the image to different type of suggestion
        if (title.toLowerCase().contains("gallery"))
        {
            actionImage.setImageResource(R.drawable.gallery);
        } else if (title.toLowerCase().contains("concert")){
            actionImage.setImageResource(R.drawable.stage);
        }else if (title.toLowerCase().contains("art")){
            int i = Tool.randomNumberGenerator(100);
            if (i % 2 == 0){
                actionImage.setImageResource(R.drawable.art);
            } else {
                actionImage.setImageResource(R.drawable.painter);
            }
            actionImage.setImageResource(R.drawable.art);
        } else if (title.toLowerCase().contains("fountain")){
            actionImage.setImageResource(R.drawable.fountains_2);
        } else if (title.toLowerCase().contains("monument")){
            actionImage.setImageResource(R.drawable.history);
        }else if (title.toLowerCase().contains("theatre")){
            actionImage.setImageResource(R.drawable.theatre);
        }else if (title.toLowerCase().contains("garden")){
            actionImage.setImageResource(R.drawable.park);
        }else if (title.toLowerCase().contains("facility")){
            actionImage.setImageResource(R.drawable.exercise);
        }

        //set the text of the title, description and the address
        titleTextView.setText(title);
        descTextView.setText(description);
        addressTextView.setText(address);
        //timeTextView.setText(time);

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
            String shareBody = "Hey! " + getDescription() + " in " + getAddress() + ". Would you like to come with me?";

            myIntent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
            myIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
            startActivity(Intent.createChooser(myIntent, "Share using"));

            return true;
        }

        if (id == R.id.action_addtodo) {
            /**
             * TODO: Oliver please update here to add the things to to-do list
             * you can just call the getNotiTitle() and getAddress() to have the value of title and address
             */
        }

        return super.onOptionsItemSelected(item);
    }

    public String getName(){
        Intent intent = getIntent();
        Bundle details = intent.getExtras();

        return details.getString("title");
    }

    public String getNotiTitle(){
        Intent intent = getIntent();
        Bundle details = intent.getExtras();

        return details.getString("title");
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

    public String readRespond(String email, String title){
        Cursor c = db.getOptions(email,title);
        String s = "";
        if (c.moveToLast()){
            //return title,details,address and time
            s = c.getString(0)+ "#" + c.getString(1);
        }
        return  s;
    }

}
