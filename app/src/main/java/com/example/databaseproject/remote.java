package com.example.databaseproject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

public class remote {

    private static final String REMOTE_AUTH_KEY = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYXBwMjAyMiJ9.iEPYaqBPWoAxc7iyi507U3sexbkLHRKABQgYNDG4Awk";
    private static final String REMOTE_URL = "http://caracal.imada.sdu.dk/app2022/";


    public static JSONArray getEverythingFromRemote(String database) {
        try {
            URL requestURL = new URL(REMOTE_URL + database);
            Scanner scanner = new Scanner(requestURL.openStream());
            String response = scanner.useDelimiter("\\Z").next();
            JSONArray json = new JSONArray(response);
            scanner.close();
            return json;
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray selectRemote(String database, JSONObject criteria) {
        try {
            String urlCriteria = "?";
            Iterator<String> it = criteria.keys();
            while(it.hasNext()){
                String key = it.next();
                urlCriteria = urlCriteria + key + "=eq." +criteria.get(key) + "&";
            }
            URL url = new URL(REMOTE_URL + encodeValue(database) + encodeValue(urlCriteria));

            Scanner scanner = new Scanner(url.openStream());
            String response = scanner.useDelimiter("\\Z").next();
            JSONArray json = new JSONArray(response);
            scanner.close();
            return  json;
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public static void insertRemote(String database, JSONObject payload) {
        try {
            URL url = new URL(REMOTE_URL + database);

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

            URL url = new URL(REMOTE_URL + encodeValue(database) + encodeValue(urlCriteria));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Authorization", REMOTE_AUTH_KEY);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    //TODO: may be removed if not used
    private void updateRemote(String database, JSONObject criteria, JSONObject updates) {
        JSONArray entries = selectRemote(database,criteria);
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
