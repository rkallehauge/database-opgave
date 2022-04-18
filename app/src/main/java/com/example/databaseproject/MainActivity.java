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

public class MainActivity extends AppCompatActivity {

    private static final String REMOTE_AUTH_KEY = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYXBwMjAyMiJ9.iEPYaqBPWoAxc7iyi507U3sexbkLHRKABQgYNDG4Awk";

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


                db.UserDao().insert(id, name, OffsetDateTime.now().toString());
                db.close();
                finish();
            }
        }).start();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void tryInsert(View view) {

        new Thread (() -> {
            try {
                removeRemote("users",new JSONObject().put("id",""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            updateFromRemote();
            String id = ((EditText) findViewById(R.id.userIdInput)).getText().toString();
        if(!isUsedId(id)) {
            String name = ((EditText) findViewById(R.id.userNameInput)).getText().toString();
            insertUser(id,name, OffsetDateTime.now().toString());

        }
        else
            ((TextView) findViewById(R.id.userIdInvalid)).setTextColor(Color.rgb(255,0,0));

        }).start();
    }

    private void insertUser(String id, String name, String time) {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "User").fallbackToDestructiveMigration().build();
        db.UserDao().insert(id,name, time);
        JSONObject userEntry = new JSONObject();
        try {
            userEntry.put("id",id);
            userEntry.put("name",name);
            userEntry.put("stamp",time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        insertRemote("users",userEntry);
    }

    private boolean isUsedId(String userId) {
        if(userId == null || userId == "") return true;
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "User").fallbackToDestructiveMigration().build();
        UserDao userDao = db.UserDao();
        return (userDao.findUserById(userId) != null);
    }


    private void updateFromRemote() {
        try {
            URL requestURL = new URL("http://caracal.imada.sdu.dk/app2022/users");
            Scanner scanner = new Scanner(requestURL.openStream());
            String response = scanner.useDelimiter("\\Z").next();
            JSONArray json = new JSONArray(response);
            scanner.close();
            for(int i = 0; i < json.length(); i++) {
                JSONObject entry = json.getJSONObject(i);
                if(!isUsedId(entry.getString("id")))
                    insertUser(entry.getString("id"),entry.getString("name"),entry.getString("stamp"));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private JSONArray getRemote(String database, JSONObject criteria) {
        try {
            String urlCriteria = "?";
            Iterator<String> it = criteria.keys();
            while(it.hasNext()){
                String key = it.next();
                urlCriteria = urlCriteria + key + "=eq." +criteria.get(key) + "&";
            }
            URL url = new URL("http://caracal.imada.sdu.dk/app2022/" + encodeValue(database) + encodeValue(urlCriteria));

            Scanner scanner = new Scanner(url.openStream());
            String response = scanner.useDelimiter("\\Z").next();
            JSONArray json = new JSONArray(response);
            scanner.close();
            return  json;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    private void insertRemote(String database, JSONObject payload) {
        try {
            URL url = new URL("http://caracal.imada.sdu.dk/app2022/" + database);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", REMOTE_AUTH_KEY);
            connection.setDoOutput(true);

            String payloadString = payload.toString();
            OutputStream os = connection.getOutputStream();
            os.write(payloadString.getBytes(StandardCharsets.UTF_8));
            os.close();
            connection.connect();

            Log.d("REMOTE", "Insert "+payloadString + " with response: " + connection.getResponseMessage());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeRemote(String database, JSONObject criteria) {
        try {
            String urlCriteria = "?";
            Iterator<String> it = criteria.keys();
            while(it.hasNext()){
                String key = it.next();
                urlCriteria = urlCriteria + key + "=eq." +criteria.get(key) + "&";
            }

            URL url = new URL("http://caracal.imada.sdu.dk/app2022/" + encodeValue(database) + encodeValue(urlCriteria));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Authorization", REMOTE_AUTH_KEY);

            Log.d("REMOTE", "Remoeved with criteria " + encodeValue(urlCriteria) + " in " + encodeValue(database) + " with response " + connection.getResponseMessage());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateRemote(String database, JSONObject criteria, JSONObject updates) {
        JSONArray entries = getRemote(database,criteria);
        removeRemote(database,criteria);
        for(int i = 0; i < entries.length(); i++) {
            try {
                JSONObject entry = entries.getJSONObject(i);
                Iterator<String> it = updates.keys();
                while(it.hasNext()) {
                    String key = it.next();
                    entry.put(key,updates.get(key));
                }
                insertRemote(database,entry);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }
}