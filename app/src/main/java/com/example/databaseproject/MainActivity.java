package com.example.databaseproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

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

    public void isValidId(View view) {
        new Thread (() -> {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "User").build();
        String id = ((EditText) findViewById(R.id.userIdInput)).getText().toString();
        UserDao userDao = db.userDao();
        String takenUser = userDao.findUserById(id);
        if(takenUser == null) {
            String name = ((EditText) findViewById(R.id.userNameInput)).getText().toString();
            db.userDao().insert(id,name, System.currentTimeMillis() / 1000L);
        }
        else
            ((TextView) findViewById(R.id.userIdInvalid)).setTextColor(Color.rgb(255,0,0));

        }).start();
    }
}