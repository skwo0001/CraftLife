package com.jostlingjacks.craftlife;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationRegularDetailActivity extends AppCompatActivity {
    private Button button;
    private TextView titleTextView,  descTextView;
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



        titleTextView = (TextView) findViewById(R.id.noti_title);
        descTextView = (TextView) findViewById(R.id.noti_desc);
        actionImage = (ImageView) findViewById(R.id.imageAction);


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
        } else if (title.toLowerCase().contains("stand up")){
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
}
