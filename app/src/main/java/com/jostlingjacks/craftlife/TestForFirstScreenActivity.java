package com.jostlingjacks.craftlife;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class TestForFirstScreenActivity extends AppCompatActivity{

    private Button startMainActivityButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_for_first_screen);

        startMainActivityButton = findViewById(R.id.btn_go_to_main_activity);

        //When user press skip, start Main Activity
        startMainActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TestForFirstScreenActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
