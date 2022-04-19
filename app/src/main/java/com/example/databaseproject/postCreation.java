package com.example.databaseproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import java.time.OffsetDateTime;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import android.os.Build;
import android.view.View;

import android.widget.EditText;

import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class postCreation extends AppCompatActivity {

    FragmentManager manager;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creation);
        List<Post> l  = getPosts();
        for(Post post:l){
            makePost(post);
        }
    }

    public void createPost(View view){

        // Hide button and feed for now
        findViewById(R.id.postButton).setVisibility(View.GONE);
        findViewById(R.id.postFeed).setVisibility(View.GONE);


        manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = new textInput();

        transaction.add(R.id.postCreation, fragment, "textInput");
        transaction.addToBackStack("idkbro");
        transaction.commit();

    }
    // TODO : Find less scuffed way to handle text, possibly (hopefully ) through textInput

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void post(View view){


        // Show button again
        findViewById(R.id.postButton).setVisibility(View.VISIBLE);
        findViewById(R.id.postFeed).setVisibility(View.VISIBLE);

        SessionHandler sh = new SessionHandler(this,"user");
        String user_id = sh.getString("user_id");
        String content = ((EditText) findViewById(R.id.userTextInput)).getText().toString();
        String epoch = OffsetDateTime.now().toString();

        Post post = new Post(user_id,content,epoch);
        makePost(post);

        new Thread(()->{

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "User").fallbackToDestructiveMigration().build();
        PostDao postdao = db.PostDao();

        List<Long> result = postdao.insertAll(post);
        System.out.println("post_id = " + result.get(0));

        // Remove fragment again
        manager.popBackStack();

        }).start();
    }

    public void close(View view){

        // Show button again
        findViewById(R.id.postButton).setVisibility(View.VISIBLE);
        findViewById(R.id.postFeed).setVisibility(View.VISIBLE);

        manager.popBackStack();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Post> getPosts(){
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "User").fallbackToDestructiveMigration().build();
        PostDao postdao = db.PostDao();
        CompletableFuture<List<Post>> posts = CompletableFuture.supplyAsync(postdao::getAll);
        try {
            return posts.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void makePost(Post post){
        // TODO : unfuck pls
        feed_post fragment = feed_post.newInstance(post);

        androidx.fragment.app.FragmentManager m = getSupportFragmentManager();

        // TODO : sometimes this crashes the app, unsure as to why atm
        m.beginTransaction().add(R.id.postFeed, fragment).commit();

    }

}