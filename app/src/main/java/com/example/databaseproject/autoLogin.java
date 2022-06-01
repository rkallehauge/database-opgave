package com.example.databaseproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class autoLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_login);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

        SessionHandler sh = new SessionHandler(this,"user");
        String user_id = sh.getString("user_id");

        if(user_id != null){
            Log.d("Session","Logged in as: " + user_id);
            Intent intent = new Intent(this, postFeed.class);
            startActivity(intent);
        } else{
            // No session found, send user to user creation
            Intent intent = new Intent(this, createUser.class);
            startActivity(intent);
        }

    }
}