package com.example.databaseproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URLEncoder;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import android.widget.EditText;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Iterator;
import java.util.Scanner;

public class createUser extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    // TODO : More accurate method name
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void isValidId(View view) {
        new Thread (() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "User").build();
            String id = ((EditText) findViewById(R.id.userIdInput)).getText().toString();
            UserDao userDao = db.UserDao();
            String takenUser = userDao.findUserById(id);
            if (takenUser == null) {

                // get session
                SharedPreferences pref_userid = getSharedPreferences("user", Context.MODE_PRIVATE);
                // create session editor
                Editor editor = pref_userid.edit();
                // put id into session
                editor.putString("user_id", id);
                // git commit xd
                editor.commit();


                String name = ((EditText) findViewById(R.id.userNameInput)).getText().toString();


                db.UserDao().insert(new User(id, name, OffsetDateTime.now().toString()));
                db.close();
                finish();
            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void tryInsert(View view) {

        SessionHandler sh = new SessionHandler(this,"user");

        new Thread (() -> {
            JSONArray remoteUsers = remote.updateFromRemote("users");
            for(int i = 0; i < remoteUsers.length(); i++) {
                try {
                    JSONObject entry = remoteUsers.getJSONObject(i);
                    insertUser(entry.getString("id"), entry.getString("name"),entry.getString("stamp"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            String id = ((EditText) findViewById(R.id.userIdInput)).getText().toString();
        if(!isUsedId(id)) {
            String name = ((EditText) findViewById(R.id.userNameInput)).getText().toString();
            insertUser(id,name, OffsetDateTime.now().toString());
        }
        else{
            ((TextView) findViewById(R.id.userIdInvalid)).setTextColor(Color.rgb(255,0,0));
        }

        sh.putString("user_id", id);
        finish();


        }).start();
    }

    public void insertUser(String id, String name, String time) {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "User").fallbackToDestructiveMigration().build();
        db.UserDao().insert(new User(id,name, time));
        JSONObject userEntry = new JSONObject();
        try {
            userEntry.put("id",id);
            userEntry.put("name",name);
            userEntry.put("stamp",time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        remote.insertRemote("users",userEntry);
    }

    private boolean isUsedId(String userId) {
        if(userId == null || userId == "") return true;
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "User").fallbackToDestructiveMigration().build();
        UserDao userDao = db.UserDao();
        return (userDao.findUserById(userId) != null);
    }



}