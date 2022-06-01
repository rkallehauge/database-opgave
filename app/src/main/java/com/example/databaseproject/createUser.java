package com.example.databaseproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;


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

import java.time.OffsetDateTime;

public class createUser extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Method for trying to create user from login, is called by create button
     * @param view The current view with the input fields
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void tryInsert(View view) {
        SessionHandler sh = new SessionHandler(this,"user");

        new Thread (() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "User").fallbackToDestructiveMigration().build();

            //Find users from remote and insert
            JSONArray remoteUsers = remote.getEverythingFromRemote("users");
            for(int i = 0; i < remoteUsers.length(); i++) {
                try {
                    JSONObject entry = remoteUsers.getJSONObject(i);
                    insertUser(new User(entry.getString("id"), entry.getString("name"),entry.getString("stamp")),db);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            String id = ((EditText) findViewById(R.id.userIdInput)).getText().toString();
            //Insert user if not used
            if(!isUsedId(id,db)) {
                String name = ((EditText) findViewById(R.id.userNameInput)).getText().toString();
                Log.d("User creation", "A user was created with id: " + id + " and name: " + name);
                insertUser(new User(id,name, OffsetDateTime.now().toString()),db);
            }
            //Show error message if already used
            else{
                ((TextView) findViewById(R.id.userIdInvalid)).setTextColor(Color.rgb(255,0,0));
            }

            //Insert userId to sessionHandler
            sh.putString("user_id", id);
            finish();

        }).start();
    }

    /**
     * Inserts the user into the local database and the remote database
     * @param user The user which is inserted
     * @param db Reference to the local database
     */
    public void insertUser(User user, AppDatabase db) {
        db.UserDao().insert(user);
        JSONObject userEntry = new JSONObject();
        try {
            userEntry.put("id",user.id);
            userEntry.put("name",user.name);
            userEntry.put("stamp",user.timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        remote.insertRemote("users",userEntry);
    }

    /**
     * Checks if a user with the id exists in local database
     * @param userId The user id which is checked
     * @param db The database which is checked
     * @return true if a user with the id exists
     */
    private boolean isUsedId(String userId,AppDatabase db) {
        if(userId == null || userId == "") return true;
        UserDao userDao = db.UserDao();
        return (userDao.findUserById(userId) != null);
    }
}