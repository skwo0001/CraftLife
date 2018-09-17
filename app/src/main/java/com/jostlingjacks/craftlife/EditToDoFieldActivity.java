package com.jostlingjacks.craftlife;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditToDoFieldActivity extends AppCompatActivity {

    Button saveToDoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_layout);
        saveToDoButton = (Button) findViewById(R.id.saveToDoBtn);
        saveToDoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = ((EditText) findViewById(R.id.addToDo)).getText().toString();
                if (messageText.equals("")){

                } else {
                    Intent intent = new Intent();
                    intent.putExtra(Tool.INTENT_TO_DO_MESSAGE_FIELD, messageText);
                    setResult(Tool.INTENT_RESULT_CODE, intent);
                    finish();
                }
            }
        });

    }
    
}
