package com.example.databaseproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;


import android.app.AlertDialog;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
    public void tryInsertUser(View view) {
        SessionHandler sh = new SessionHandler(this,"user");
        Log.d("User creation", "Trying to create user");
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("User already exists")
                .setMessage("Users with this ID already exists, please choose another username");
        String id = ((EditText) findViewById(R.id.userIdInput)).getText().toString();
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "User").fallbackToDestructiveMigration().build();

        //Get a boolean if user exist
        CompletableFuture<Boolean> isUsed = new CompletableFuture<>().supplyAsync(() -> {
            //Find users from remote and insert
            JSONArray remoteUsers = remote.getEverythingFromRemote("users");
            List<String> ids = new ArrayList<>();
            for(int i = 0; i < remoteUsers.length(); i++) {
                try {
                    JSONObject entry = remoteUsers.getJSONObject(i);
                    db.userDao().insert(new User(entry.getString("id"), entry.getString("name"),entry.getString("stamp")));
                    ids.add(entry.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //Delete removed users such a removed user can be created again
            db.userDao().removeAllNotInRemote(ids);
            return isUsedId(id,db);
        });

        //Try to insert if user not exist or show error, have to be in main thread due to alert can not be shown from thread
        try {
            if(!isUsed.get()) {
                String name = ((EditText) findViewById(R.id.userNameInput)).getText().toString();
                Log.d("User creation", "A user was created with id: " + id + " and name: " + name);
                new Thread(() -> insertUser(new User(id,name, OffsetDateTime.now().toString()),db)).start();
                //Insert userId to sessionHandler
                sh.putString("user_id", id);
                finish();
            }
            //Show error message if already used
            else
                builder.show();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts the user into the local database and the remote database
     * @param user The user which is inserted
     * @param db Reference to the local database
     */
    public void insertUser(User user, AppDatabase db) {
        db.userDao().insert(user);
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
        if(userId == null || userId == ""){
            return true;
        }
        String id = db.userDao().findUserById(userId);
        System.out.println("found: " + id);
        return (id != null);
    }
}