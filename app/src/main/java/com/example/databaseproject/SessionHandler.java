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

    public void putString(String key, String value){
        editor.putString(key,value);
        editor.commit();
    }

    public String getString(String key){
        return sp.getString(key, null);
    }

}
