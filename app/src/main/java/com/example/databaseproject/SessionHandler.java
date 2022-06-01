package com.example.databaseproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import androidx.appcompat.app.AppCompatActivity;


public class SessionHandler extends AppCompatActivity {

    private SharedPreferences sp;
    SharedPreferences.Editor editor;

    public SessionHandler(Context context, String pref_table){
        this.sp = context.getSharedPreferences(pref_table, Context.MODE_PRIVATE);
        this.editor = sp.edit();
    }

    /**
     * Updates key with value in session
     * @param key key which value is updated
     * @param value the new value for the key
     */
    public void putString(String key, String value){
        editor.putString(key,value);
        editor.commit();
    }

    /**
     * Getter for a key value
     * @param key the key of which the value is returned
     * @return the value of the key
     */
    public String getString(String key){
        return sp.getString(key, null);
    }

}
