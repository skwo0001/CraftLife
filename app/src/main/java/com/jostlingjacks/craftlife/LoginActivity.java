package com.jostlingjacks.craftlife;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    boolean doubleBackToExitPressedOnce = false;

    EditText emailInput,passwordInput;
    Button loginButton;

    TextView signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        emailInput = (EditText) findViewById(R.id.emailET);
        passwordInput = (EditText) findViewById(R.id.passwordET);
        loginButton = (Button) findViewById(R.id.btn_login);
        signupLink = (TextView) findViewById(R.id.link_signup);

        // login button click listener...
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });


        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    // login method...
    @SuppressLint("StaticFieldLeak")
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();

        //Send the JSON Object to the API
        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... params) {
                UserInfo userInfo = new UserInfo(email, password);
                JSONObject jsonReply = null;
                //Get the inout stream from http
                String jsonString = HTTPDataHandler.loginUser(userInfo);

                if (jsonString != ""){
                    try {
                        // when the string is not null, convert to JSON Object
                        jsonReply = new JSONObject(jsonString.toString());
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                //the josn Object
                return jsonReply;

            }
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                if (jsonObject != null){
                    try {
                        String message = jsonObject.getString("message");
                        String status = jsonObject.getString("status");
                        if (status.toLowerCase().contains("failed")){
                            Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                        }
                        onLoginSuccess();
                        progressDialog.dismiss();
                        finish();
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                } else {
                    onLoginFailed();
                    progressDialog.dismiss();
                }

            }
        }.execute();

    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        Intent main = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(main);
        //finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email");
            valid = false;
        } else {
            emailInput.setError(null);
        }

        //set the requirement of the password with the range between 8 to 16, that contains both numeric and alphabetic characters
        if (password.isEmpty() || password.length() < 8 || password.length() > 16 || !password.matches("[a-zA-Z0-9]*")) {
            passwordInput.setError("between 8 and 16 alphanumeric and numeric characters");
            valid = false;
        } else {
            passwordInput.setError(null);
        }

        return valid;
    }
}

// { "email": "email_address@outlook.com", "password": "password"}
// {
//    "auth_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1MzY1NjYwMTEsImlhdCI6MTUzNjQ3OTU5MSwic3ViIjo1fQ.kskG-eD0hSnzAyuoblUgKAvz0CQuX1sh-38t5DYeOHk",
//    "message": "Successfully registered",
//    "status": "success"
//}