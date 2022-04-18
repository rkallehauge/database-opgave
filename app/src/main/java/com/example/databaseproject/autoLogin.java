package com.example.databaseproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class autoLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_login);

    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("Testing for cancer");
    }

    @Override
    protected void onStart() {
        super.onStart();

        SessionHandler sh = new SessionHandler(this,"user");
        String user_id = sh.getString("user_id");

        if(user_id != null){
            System.out.println("user_id" + user_id);
            System.out.println("Session successfully gotten");
            Intent intent = new Intent(this, postCreation.class);
            startActivity(intent);

        } else{
            // No session found, send user to user creation
            System.out.println("Session NOT gotten");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

    }
}