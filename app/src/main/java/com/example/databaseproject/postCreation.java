package com.example.databaseproject;

import android.app.Fragment;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import java.time.OffsetDateTime;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.ActivityNotFoundException;

import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;
import android.view.ViewParent;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import org.w3c.dom.Text;

public class postCreation extends AppCompatActivity {

    FragmentManager manager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creation);
        List<Post> l  = getPosts();
        LinearLayout layout = findViewById(R.id.postCreation);
        for(Post post:l){
            TextView t = new TextView(this);
            t.setText(post.content);
            layout.addView(t);
        }
    }

    public void createPost(View view){

        manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = new textInput();
        transaction.add(R.id.postCreation, fragment, "textInput");
        transaction.commit();

    }
    // TODO : Find less scuffed way to handle text, possibly (hopefully ) through textInput
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void post(View view){
        new Thread(()->{

        String content = ((EditText) findViewById(R.id.userTextInput)).getText().toString();

        SharedPreferences pref_userid = getSharedPreferences("user", Context.MODE_PRIVATE);
        String user_id = pref_userid.getString("user_id", null);
        String epoch = OffsetDateTime.now().toString();


        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "User").fallbackToDestructiveMigration().build();
        PostDao postdao = db.PostDao();
        Post post = new Post(user_id,content,epoch);

        List<Long> result = postdao.insertAll(post);

        System.out.println("post_id = " + result.get(0));

        }).start();
    }

    public void close(View view){
        // TODO : When we try to do this, hell breaks loose, figure out why, and fix it
        //manager.beginTransaction().remove(this).commit();
    }

    public List<Post> getPosts(){
        // TODO : better way of creating new elements than ad-hoc solution
        // TODO : better way of handling db interaction than doing it on main thread, do it asynchronously
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "User").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        PostDao postdao = db.PostDao();
        List<Post> posts = postdao.getAll();
        return posts;
    }

}