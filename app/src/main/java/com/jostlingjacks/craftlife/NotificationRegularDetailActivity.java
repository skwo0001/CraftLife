package com.jostlingjacks.craftlife;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class NotificationRegularDetailActivity extends AppCompatActivity {
    private Button button;
    private TextView titleTextView,  descTextView;
    private ImageView actionImage, timeImg, addressImg, urlImg;
    private LinearLayout resultLL,questionTV, pullLL;
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
        questionTV = (LinearLayout) findViewById(R.id.question);
        actionImage = (ImageView) findViewById(R.id.imageAction);
        resultLL = (LinearLayout) findViewById(R.id.resultLL);
        pullLL = (LinearLayout) findViewById(R.id.pull);
        addressImg = (ImageView) findViewById(R.id.addressImg);
        timeImg = (ImageView) findViewById(R.id.timeImg);
        urlImg = (ImageView) findViewById(R.id.urlImg);

        questionTV.setVisibility(View.GONE);
        pullLL.setVisibility(View.GONE);
        resultLL.setVisibility(View.GONE);
        addressImg.setVisibility(View.GONE);
        timeImg.setVisibility(View.GONE);
        urlImg.setVisibility(View.GONE);


        if (title.toLowerCase().contains("water"))
        {
            actionImage.setVisibility(View.VISIBLE);
            actionImage.setImageResource(R.drawable.drinkwater);
        } else if (title.toLowerCase().contains("walk")){
            actionImage.setVisibility(View.VISIBLE);
            actionImage.setImageResource(R.drawable.walking);
        } else if (title.toLowerCase().contains("stand up")){
            actionImage.setVisibility(View.VISIBLE);
            actionImage.setImageResource(R.drawable.coach);
        }else if (title.toLowerCase().contains("meditation")){
            actionImage.setVisibility(View.VISIBLE);
            actionImage.setImageResource(R.drawable.meditation);
        } else if (title.toLowerCase().contains("eye")){
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
