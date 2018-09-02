package com.jostlingjacks.craftlife;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class NotificationDetailActivity extends AppCompatActivity {

    private Button button;
    private TextView titleTextView,  descTextView,  addressTextView,  timeTextView, addTV, timeTV;
    private ImageView actionImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        Intent intent = getIntent();
        Bundle details = intent.getExtras();

        String type = details.getString("type");
        String title = details.getString("title");
        String description  = details.getString("description");
        String address = details.getString("address");
        String lat = details.getString("lat");
        String lon = details.getString("lon");
        String time = details.getString("time");


        titleTextView = (TextView) findViewById(R.id.noti_title);
        descTextView = (TextView) findViewById(R.id.noti_desc);
        addressTextView = (TextView) findViewById(R.id.noti_add);
        timeTextView = (TextView) findViewById(R.id.noti_time);
        addTV = (TextView)findViewById(R.id.address);
        timeTV = (TextView) findViewById(R.id.time);
        actionImage = (ImageView) findViewById(R.id.imageAction);
        //mMapView = (MapView)findViewById(R.id.mapView);


        if (address != null || time != null)
        {
            addTV.setVisibility(View.VISIBLE);
            timeTextView.setVisibility(View.INVISIBLE);
        }

        if (title.toLowerCase().contains("water"))
        {
            actionImage.setVisibility(View.VISIBLE);
            actionImage.setImageResource(R.drawable.drinkwater);
        } else if (title.toLowerCase().contains("walk")){
            actionImage.setVisibility(View.VISIBLE);
            actionImage.setImageResource(R.drawable.walking);
        } else if (title.toLowerCase().contains("concert")){
            actionImage.setVisibility(View.VISIBLE);
            actionImage.setImageResource(R.drawable.stage);
        }else if (title.toLowerCase().contains("art")){
            actionImage.setVisibility(View.VISIBLE);
            actionImage.setImageResource(R.drawable.art);
        } else if (title.toLowerCase().contains("stand")){
            actionImage.setVisibility(View.VISIBLE);
            actionImage.setImageResource(R.drawable.coach);
        }else if (title.toLowerCase().contains("meditation")){
            actionImage.setVisibility(View.VISIBLE);
            actionImage.setImageResource(R.drawable.meditation);
        } else if (title.toLowerCase().contains("window")){
            actionImage.setVisibility(View.VISIBLE);
            actionImage.setImageResource(R.drawable.curtain);
        }

        titleTextView.setText(title);
        descTextView.setText(description);
        addressTextView.setText(address);



        //timeTextView.setText(time);


        button = (Button) findViewById(R.id.getbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    public void goBack() {
        Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
        finish();
        //moveTaskToBack(true);
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
            String shareBody = "Hey! There is a " + getDescription() + " in " + getAddress() + ". Do you want to come with me?";
            myIntent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
            myIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
            startActivity(Intent.createChooser(myIntent, "Share using"));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getName(){
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



}
