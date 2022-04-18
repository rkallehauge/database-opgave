package com.example.databaseproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import androidx.appcompat.app.AppCompatActivity;


public class SessionHandler extends AppCompatActivity {

    private SharedPreferences sp;

    public SessionHandler(Context context, String pref_table){
        this.sp = context.getSharedPreferences(pref_table, Context.MODE_PRIVATE);
    }

    public void putData(String key, String value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public String getString(String key){
        return sp.getString(key, null);
    }

}
