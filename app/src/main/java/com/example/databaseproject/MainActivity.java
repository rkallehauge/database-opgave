package com.example.databaseproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


import android.graphics.Color;
import android.os.Bundle;
import android.view.View;


import android.widget.EditText;
import android.widget.TextView;

import java.time.Instant;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    // TODO : More accurate method name
    public void isValidId(View view) {
        new Thread (() -> {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "User").build();
        String id = ((EditText) findViewById(R.id.userIdInput)).getText().toString();
        UserDao userDao = db.UserDao();
        String takenUser = userDao.findUserById(id);
        if(takenUser == null) {
            // get session
            SharedPreferences pref_userid = getSharedPreferences("user", Context.MODE_PRIVATE);
            // create session editor
            Editor editor = pref_userid.edit();
            // put id into session
            editor.putString("user_id", id);
            // git commit xd
            editor.commit();


            String name = ((EditText) findViewById(R.id.userNameInput)).getText().toString();
            db.UserDao().insert(id,name, System.currentTimeMillis() / 1000L);
            db.close();
            finish();
        }
        else
            ((TextView) findViewById(R.id.userIdInvalid)).setTextColor(Color.rgb(255,0,0));

        }).start();
    }
}