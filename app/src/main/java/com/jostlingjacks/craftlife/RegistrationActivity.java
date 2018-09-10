package com.jostlingjacks.craftlife;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    EditText emailText, passwordText,reEnterPasswordText;
    Button signupButton;
    TextView loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);
        emailText = (EditText) findViewById(R.id.register_email);
        passwordText = (EditText) findViewById(R.id.register_password);
        reEnterPasswordText = (EditText) findViewById(R.id.register_reEnterPassword);
        signupButton = (Button) findViewById(R.id.btn_register);
        loginLink = (TextView) findViewById(R.id.link_login);



        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onRegisterFailed();
            return;
        }

        signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegistrationActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        //password = Tool.hashPw(password);


        //Send the User Object to the API
        final String finalPassword = password;
        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... params) {
                UserInfo userinfo = new UserInfo(email, finalPassword);
                JSONObject jsonReply = null;
                try {
                String jsonString = HTTPDataHandler.signUpUser(userinfo);

                if (jsonString != "") {
                        jsonReply = new JSONObject(jsonString.toString());
                    }} catch (JSONException e) {
                        e.printStackTrace();
                    }

                return jsonReply ;
            }
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                if (jsonObject != null)
                {
                    try {
                        String message = jsonObject.getString("message");
                        String status = jsonObject.getString("status");
                        if (status.toLowerCase().contains("failed")){
                            if (message.toLowerCase().contains("please sign in"))
                            {
                                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                                signupButton.setEnabled(true);
                                progressDialog.dismiss();
                                Intent login = new Intent(RegistrationActivity.this, LoginActivity.class);
                                startActivity(login);

                            }
                            else {
                                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                                signupButton.setEnabled(true);
                                progressDialog.dismiss();
                            }
                        } else {
                            String token = jsonObject.getString("auth_token");
                            Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                            onRegisterSuccess();
                            progressDialog.dismiss();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        e.printStackTrace();
                    }


                } else {
                    onRegisterFailed();
                    progressDialog.dismiss();
                }

            }
        }.execute();

    }


    public void onRegisterSuccess() {
        signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent login = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(login);
    }

    public void onRegisterFailed() {
        Toast.makeText(getBaseContext(), "Sign Up failed", Toast.LENGTH_LONG).show();

        signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String reEnterPassword = reEnterPasswordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 8 || password.length() > 16 || !password.matches("[a-zA-Z0-9]*")) {
            passwordText.setError("between 8 and 16 alphanumeric and numeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 8 || reEnterPassword.length() > 16 || !(reEnterPassword.equals(password))) {
            reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            reEnterPasswordText.setError(null);
        }

        return valid;
    }

}
