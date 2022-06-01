package com.example.databaseproject;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.Scanner;

public class remote {

    private static final String REMOTE_AUTH_KEY = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYXBwMjAyMiJ9.iEPYaqBPWoAxc7iyi507U3sexbkLHRKABQgYNDG4Awk";
    private static final String REMOTE_URL = "http://caracal.imada.sdu.dk/app2022/";

    /**
     * Get every entry from the given remote table
     * @param table The table which everything is selected
     * @return A JSON array of elements
     */
    public static JSONArray getEverythingFromRemote(String table) {
        try {
            URL requestURL = new URL(REMOTE_URL + table);
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

    /**
     * Creates a select on the remote table, with the given criterias given in the JSON object
     * @param table The table of which the select is made
     * @param criteria The criteria which the select constraints
     * @return A JSON array which contains returned rows
     */
    private static JSONArray selectRemote(String table, JSONObject criteria) {
        try {
            URL url = criteriaURL(criteria,table);

            Scanner scanner = new Scanner(url.openStream());
            String response = scanner.useDelimiter("\\Z").next();
            JSONArray json = new JSONArray(response);
            scanner.close();
            return json;
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    /**
     * Inserts the given payload into the wanted table in remote
     * @param table The table fo which the payload is inserted
     * @param payload The item which is inserted into the table in a JSONObject
     */
    public static void insertRemote(String table, JSONObject payload) {
        try {
            URL url = new URL(REMOTE_URL + table);

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
            Log.d("Insert remote", connection.getResponseMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes every row in the remote table which meets the criteria
     * @param table The table of the remote database which the remove should be done
     * @param criteria The criteria which the rows which are removed should meet
     */
    public static void removeRemote(String table, JSONObject criteria) {
        try {
            URL url = criteriaURL(criteria,table);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Authorization", REMOTE_AUTH_KEY);
            connection.connect();
            Log.d("Remove remote",connection.getResponseMessage());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a URL to the remote table with the criteria
     * @param criteria The criteria which the select uses
     * @param table The table the select is made upon
     * @return An URL object with the url of the select
     */
    private static URL criteriaURL(JSONObject criteria, String table) {
        try {
            String urlCriteria = "?";
            Iterator<String> it = criteria.keys();
            while (it.hasNext()) {
                String key = it.next();
                urlCriteria = urlCriteria + key + "=eq." + criteria.get(key) + "&";
            }
            return new URL(REMOTE_URL + table + urlCriteria);
        } catch (JSONException | MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates the remote table by first selecting with criteria, removing with criteria
     * and updating the selected to then insert
     * @param table The table of the remote database
     * @param criteria The criteria which the update
     * @param updates The updated value on the rows
     */
    public static void updateRemote(String table, JSONObject criteria, JSONObject updates) {
        JSONArray entries = selectRemote(table, criteria);
        removeRemote(table, criteria);
        for (int i = 0; i < entries.length(); i++) {
            try {
                JSONObject entry = entries.getJSONObject(i);
                Iterator<String> it = updates.keys();
                while (it.hasNext()) {
                    String key = it.next();
                    entry.put(key, updates.get(key));
                }
                insertRemote(table, entry);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Uploads the selected image to the remote server
     * @param bytes Byte array representing the image
     * @return the given filename in remote
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String uploadImage(byte[] bytes) {
        try {
            URL url = new URL("https://kklokker.com/databaseApp/upload.php");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            os.write(bytes,0,bytes.length);
            os.close();
            Log.d("Post creation", "Image is being uploaded");

            //Searched for the image name in response
            String output = readFully(connection.getInputStream());
            connection.connect();
            connection.disconnect();
            String imageName = output.substring(output.indexOf("images/"),output.indexOf(".jpg")+4);
            Log.d("Post creation", "Image is uploaded with the following name " + imageName);
            return imageName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Reads the whole response to the upload
     * @param inputStream The input stream which response
     * @return String of whole response message
     * @throws IOException
     */
    private static String readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1)
            baos.write(buffer, 0, length);
        return baos.toString();
    }
}
