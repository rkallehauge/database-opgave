package com.example.databaseproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class autoLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_login);
        SharedPreferences pref_userid = getSharedPreferences("user", Context.MODE_PRIVATE);
        String user_id = pref_userid.getString("user_id", null);
        if(user_id != null){
            /*
        new Thread(){
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "User").build();
        UserDao u = db.UserDao();




        }.start();
         */
            System.out.println("Session successfully gotten");
            Intent intent = new Intent(this, postCreation.class);
            startActivity(intent);
        } else{
            // No session found, send user to user creation
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}