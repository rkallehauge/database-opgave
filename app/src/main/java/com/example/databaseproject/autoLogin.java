package com.example.databaseproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();

        //Check if session has a user
        SessionHandler sh = new SessionHandler(this,"user");
        String user_id = sh.getString("user_id");
        CompletableFuture<Integer> futureFound = CompletableFuture.supplyAsync(() -> {
            try {
                return remote.selectRemote("users",new JSONObject().put("id",user_id)).length();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return 0;
        });
        int found = 0;
        try {
            found = futureFound.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if(user_id != null && found == 1){
            Log.d("Auto-login", "Session gotten successfully");
            Intent intent = new Intent(this, feed.class);
            startActivity(intent);
        } else{
            Log.d("Auto-login", "Session not found");
            Intent intent = new Intent(this, createUser.class);
            startActivity(intent);
        }

    }
}