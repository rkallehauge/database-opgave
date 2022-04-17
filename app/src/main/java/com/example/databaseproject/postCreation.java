package com.example.databaseproject;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link postCreation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class postCreation extends AppCompatActivity {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creation);
    }

    public void createPost(){
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        // TODO : fix
        textInput t = new textInput();

        transaction.add(R.id.postContent, t, "textInput");
    }

}