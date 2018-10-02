package com.jostlingjacks.craftlife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditToDoItemActivity extends AppCompatActivity {

    String messageText;
    int position;

    Button saveToDoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_layout);

        saveToDoButton = (Button) findViewById(R.id.saveToDoBtn);

        Intent intent = getIntent();
        messageText = intent.getStringExtra(Tool.INTENT_TO_DO_MESSAGE_DATA);
        position = intent.getIntExtra(Tool.INTENT_TO_DO_ITEM_POSITION, -1);

        EditText messageData = (EditText) findViewById(R.id.addToDo);
        messageData.setText(messageText);


        saveToDoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String changedMessageText = ((EditText) findViewById(R.id.addToDo)).getText().toString();

                if (!changedMessageText.trim().equals("")) {
                    //String changedMessageText = ((EditText) findViewById(R.id.addToDo)).getText().toString();

                    Intent intent = new Intent();
                    intent.putExtra(Tool.INTENT_TO_DO_CHANGED_MESSAGE, changedMessageText);

                    intent.putExtra(Tool.INTENT_TO_DO_ITEM_POSITION, position);
                    setResult(Tool.INTENT_RESULT_CODE_TWO, intent);
                    finish();
                }
            }
        });
    }


}
